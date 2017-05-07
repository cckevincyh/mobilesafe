package indi.cc.mobilesafe.service;

import indi.cc.mobilesafe.R;
import indi.cc.mobilesafe.engine.ProcessInfoProvider;
import indi.cc.mobilesafe.receiver.MyAppWidgetProvider;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * 更新小部件的服务
 * @author c
 *
 */
public class UpdateWidgetService extends Service {
	protected static final String tag = "UpdateWidgetService";
	private Timer mTimer;
	private InnerReceiver mInnerReceiver;
	@Override
	public void onCreate() {
		//管理进程总数和可用内存数更新(定时器)
		startTimer();
		
		//注册开锁,解锁广播接受者
		IntentFilter intentFilter = new IntentFilter();
		//开锁action
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		//解锁action
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		
		mInnerReceiver = new InnerReceiver();
		registerReceiver(mInnerReceiver, intentFilter);
		
		super.onCreate();
	}
	
	class InnerReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			//锁屏的时候不需要开启任务
			if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
				//开启定时更新任务
				startTimer();
			}else{
				//关闭定时更新任务
				cancelTimerTask();
			}
		}
	}
	
	private void startTimer() {
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				//ui定时刷新
				updateAppWidget();
				Log.i(tag, "5秒一次的定时任务现在正在运行..........");
			}
		}, 0, 5000);
	}
	public void cancelTimerTask() {
		//mTimer中cancel方法取消定时任务方法
		if(mTimer!=null){
			mTimer.cancel();
			mTimer = null;
		}
	}
	/**
	 * 更新小部件
	 */
	protected void updateAppWidget() {
		//1.获取AppWidget对象
		AppWidgetManager aWM = AppWidgetManager.getInstance(this);
		//2.获取窗体小部件布局转换成的view对象(定位应用的包名,当前应用中的那块布局文件)
		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
		//3.给窗体小部件布view对象,内部控件赋值
		remoteViews.setTextViewText(R.id.tv_process_count, "进程总数:"+ProcessInfoProvider.getProcessCount(this));
		//4.显示可用内存大小
		String strAvailSpace = Formatter.formatFileSize(this, ProcessInfoProvider.getAvailSpace(this));
		remoteViews.setTextViewText(R.id.tv_process_memory, "可用内存:"+strAvailSpace);

		
		//点击窗体小部件,进入应用
		//1:在那个控件上响应点击事件2:延期的意图
		Intent intent = new Intent("android.intent.action.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.ll_root, pendingIntent);
		
		//通过延期意图发送广播,在广播接受者中杀死进程,匹配规则看action
		Intent broadCastintent = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
		PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, broadCastintent, PendingIntent.FLAG_CANCEL_CURRENT);
		//设置按钮事件
		remoteViews.setOnClickPendingIntent(R.id.btn_clear,broadcast);
		
		//上下文环境,窗体小部件对应广播接受者的字节码文件
		ComponentName componentName = new ComponentName(this,MyAppWidgetProvider.class);
		//更新窗体小部件
		aWM.updateAppWidget(componentName, remoteViews);
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onDestroy() {
		if(mInnerReceiver!=null){
			unregisterReceiver(mInnerReceiver);
		}
		//调用onDestroy即关闭服务,关闭服务的方法在移除最后一个窗体小部件的时调用,定时任务也没必要维护
		cancelTimerTask();
		super.onDestroy();
	}

}
