package indi.cc.mobilesafe.service;

import indi.cc.mobilesafe.activity.EnterPsdActivity;
import indi.cc.mobilesafe.db.dao.AppLockDao;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

public class WatchDogService extends Service {

	private boolean isWatch;
	private AppLockDao mDao;
	private List<String> mPacknameList;
	private InnerReceiver mInnerReceiver;
	private String mSkipPackagename;
	private MyContentObserver mContentObserver;
	@Override
	public void onCreate() {
		//ά��һ�����Ź�����ѭ��,����ʱ�̼�����ڿ�����Ӧ��,�Ƿ�Ϊ��������Ҫȥ���ص�Ӧ��
		mDao = AppLockDao.getInstance(this);
		isWatch = true;
		watch();
		
		IntentFilter intentFilter = new IntentFilter();	
		intentFilter.addAction("android.intent.action.SKIP");
		
		mInnerReceiver = new InnerReceiver();
		registerReceiver(mInnerReceiver, intentFilter);
		
		
		//ע��һ�����ݹ۲���,�۲����ݿ�ı仯,һ��������ɾ���������,����Ҫ��mPacknameList���»�ȡһ������
		mContentObserver = new MyContentObserver(new Handler());
		getContentResolver().registerContentObserver(
				Uri.parse("content://applock/change"), true, mContentObserver);
		super.onCreate();
	}
	
	class MyContentObserver extends ContentObserver{

		public MyContentObserver(Handler handler) {
			super(handler);
		}
		
		//һ�����ݿⷢ���ı�ʱ����÷���,���»�ȡ�������ڼ��ϵ�����
		@Override
		public void onChange(boolean selfChange) {
			new Thread(){
				public void run() {
					mPacknameList = mDao.findAll();
				};
			}.start();
			super.onChange(selfChange);
		}
	}
	
	class InnerReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			//��ȡ���͹㲥�����д��ݹ����İ���,�����ΰ���������
			mSkipPackagename = intent.getStringExtra("packagename");
		}
	}
	
	/**
	 * ���Ź����񣬼������ջ�д򿪵�Ӧ�ã���ȡ����ջ��ҪȨ��
	 */
	private void watch() {
		//1,���߳���,����һ���ɿ���ѭ��
		new Thread(){
			public void run() {
				mPacknameList = mDao.findAll();
				while(isWatch){
					//2.����������ڿ�����Ӧ��,����ջ
					//3.��ȡactivity�����߶���
					ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
					//4.��ȡ���ڿ���Ӧ�õ�����ջ
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
					RunningTaskInfo runningTaskInfo = runningTasks.get(0);
					//5.��ȡջ����activity,Ȼ���ڻ�ȡ��activity����Ӧ�õİ���
					String packagename = runningTaskInfo.topActivity.getPackageName();
					
					//�������ջָ��Ӧ�����л�,��mSkipPackagename���ַ���
					
					//6.�ô˰������Ѽ����İ���������ȥ���ȶ�,��������ΰ���,����Ҫ�������ؽ���
					if(mPacknameList.contains(packagename)){
						//������ڼ��ĳ���,�Լ�������,����Ҫȥ�������ؽ���
						if(!packagename.equals(mSkipPackagename)){
							//7,�������ؽ���
							Intent intent = new Intent(getApplicationContext(),EnterPsdActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("packagename", packagename);
							startActivity(intent);
						}
					}
					//˯��һ��,ʱ��Ƭ��ת
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	
	}
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	@Override
	public void onDestroy() {
		//ֹͣ���Ź�ѭ��
		isWatch = false;
		//ע���㲥������
		if(mInnerReceiver!=null){
			unregisterReceiver(mInnerReceiver);
		}
		//ע�����ݹ۲���
		if(mContentObserver!=null){
			getContentResolver().unregisterContentObserver(mContentObserver);
		}
		super.onDestroy();
	}

}
