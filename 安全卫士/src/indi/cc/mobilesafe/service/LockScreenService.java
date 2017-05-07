package indi.cc.mobilesafe.service;

import indi.cc.mobilesafe.engine.ProcessInfoProvider;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class LockScreenService extends Service {
	
	private IntentFilter intentFilter;
	private InnerReceiver innerReceiver;
	@Override
	public void onCreate() {
		
		//锁屏action
		intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		innerReceiver = new InnerReceiver();
		//注册锁屏广播接受者
		registerReceiver(innerReceiver, intentFilter);
		
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		//注销广播接受者
		if(innerReceiver!=null){
			unregisterReceiver(innerReceiver);
		}
		super.onDestroy();
	}
	
	//接收到广播后进行处理
	class InnerReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			//清理手机正在运行的进程
			ProcessInfoProvider.killAll(context);
		}
	}
}
