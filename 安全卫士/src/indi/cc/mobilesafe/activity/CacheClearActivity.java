package indi.cc.mobilesafe.activity;

import indi.cc.mobilesafe.R;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CacheClearActivity extends Activity {
	protected static final int UPDATE_CACHE_APP = 100;
	protected static final int CHECK_CACHE_APP = 101;
	protected static final int CHECK_FINISH = 102;
	protected static final int CLEAR_CACHE = 103;
	protected static final String tag = "CacheClearActivity";
	
	private Button bt_clear;
	private ProgressBar pb_bar;
	private TextView tv_name;
	private LinearLayout ll_add_text;
	private PackageManager mPm;
	private int mIndex = 0;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_CACHE_APP:
				//8.�����Բ���������л���Ӧ����Ŀ
				View view = View.inflate(getApplicationContext(), R.layout.linearlayout_cache_item, null);
				
				ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				TextView tv_item_name = (TextView) view.findViewById(R.id.tv_name);
				TextView tv_memory_info = (TextView)view.findViewById(R.id.tv_memory_info);
				ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
				
				final CacheInfo cacheInfo = (CacheInfo) msg.obj;
				iv_icon.setBackgroundDrawable(cacheInfo.icon);
				tv_item_name.setText(cacheInfo.name);
				tv_memory_info.setText(Formatter.formatFileSize(getApplicationContext(), cacheInfo.cacheSize));
				
				ll_add_text.addView(view, 0);
				
				iv_delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//�������ѡ��Ӧ�õĻ�������(PackageMananger)
						
						/* ���´������Ҫִ�гɹ�����ҪϵͳӦ�òſ���ȥʹ�õ�Ȩ��
						 * android.permission.DELETE_CACHE_FILES
						 * try {
							Class<?> clazz = Class.forName("android.content.pm.PackageManager");
							//2.��ȡ���÷�������
							Method method = clazz.getMethod("deleteApplicationCacheFiles", String.class,IPackageDataObserver.class);
							//3.��ȡ������÷���
							method.invoke(mPm, cacheInfo.packagename,new IPackageDataObserver.Stub() {
								@Override
								public void onRemoveCompleted(String packageName, boolean succeeded)
										throws RemoteException {
									//ɾ����Ӧ�û����,���õķ���,���߳���
									Log.i(tag, "onRemoveCompleted.....");
								}
							});
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
						//Դ�뿪���γ�(Դ��(handler����,AsyncTask(�첽����,�ֻ���������)Դ��))
						//ͨ���鿴ϵͳ��־,��ȡ����������activity��action��data
						Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
						intent.setData(Uri.parse("package:"+cacheInfo.packagename));
						startActivity(intent);
					}
				});
				break;
			case CHECK_CACHE_APP:
				tv_name.setText((String)msg.obj);
				break;
			case CHECK_FINISH:
				tv_name.setText("ɨ�����");
				break;
			case CLEAR_CACHE:
				//�����Բ������Ƴ����е���Ŀ
				ll_add_text.removeAllViews();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_clear);
		initUI();
		initData();
	}

	/**
	 * �����ֻ����е�Ӧ��,��ȡ�л����Ӧ��,������ʾ
	 */
	private void initData() {
		new Thread(){
			public void run() {
				//1.��ȡ�������߶���
				
				mPm = getPackageManager();
				
				//2.��ȡ��װ���ֻ��ϵ����е�Ӧ��
				List<PackageInfo> installedPackages = mPm.getInstalledPackages(0);
				//3.���������������ֵ(�ֻ�������Ӧ�õ�����)
				pb_bar.setMax(installedPackages.size());
				//4.����ÿһ��Ӧ��,��ȡ�л����Ӧ����Ϣ(Ӧ������,ͼ��,�����С,����)
				for (PackageInfo packageInfo : installedPackages) {
					//������Ϊ��ȡ������Ϣ������
					String packageName = packageInfo.packageName;
					getPackageCache(packageName);
					
					try {
						Thread.sleep(100+new Random().nextInt(50));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mIndex++;
					pb_bar.setProgress(mIndex);
					
					//ÿѭ��һ�ξͽ����Ӧ�õ����Ʒ��͸����߳���ʾ
					Message msg = Message.obtain();
					msg.what = CHECK_CACHE_APP;
					String name = null;
					try {
						name = mPm.getApplicationInfo(packageName, 0).loadLabel(mPm).toString();
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					msg.obj = name;
					mHandler.sendMessage(msg);
				}
				Message msg = Message.obtain();
				msg.what = CHECK_FINISH;
				mHandler.sendMessage(msg);
			};
		}.start();
	}
	
	class CacheInfo{
		public String name;
		public Drawable icon;
		public String packagename;
		public long cacheSize;
	}

	/**ͨ��������ȡ�˰���ָ��Ӧ�õĻ�����Ϣ
	 * @param packageName	Ӧ�ð���
	 */
	protected void getPackageCache(String packageName) {
		IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {

			public void onGetStatsCompleted(PackageStats stats,
					boolean succeeded) {
				//���߳��з���,�õ���Ϣ����
				
				//4.��ȡָ�������Ļ����С
				long cacheSize = stats.cacheSize;
				//5.�жϻ����С�Ƿ����0
				if(cacheSize>0){
					//6.��֪���̸߳���UI
					Message msg = Message.obtain();
					msg.what = UPDATE_CACHE_APP;
					CacheInfo cacheInfo = null;
					try {
						//7.ά���л���Ӧ�õ�javabean
						cacheInfo = new CacheInfo();
						cacheInfo.cacheSize = cacheSize;
						cacheInfo.packagename = stats.packageName;
						cacheInfo.name = mPm.getApplicationInfo(stats.packageName, 0).loadLabel(mPm).toString();
						cacheInfo.icon = mPm.getApplicationInfo(stats.packageName, 0).loadIcon(mPm);
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					msg.obj = cacheInfo;
					mHandler.sendMessage(msg);
				}
			}
		};
		//1.��ȡָ������ֽ����ļ�
		try {
			Class<?> clazz = Class.forName("android.content.pm.PackageManager");
			//2.��ȡ���÷�������
			Method method = clazz.getMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
			//3.��ȡ������÷���
			method.invoke(mPm, packageName,mStatsObserver);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initUI() {
		bt_clear = (Button) findViewById(R.id.bt_clear);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		tv_name = (TextView) findViewById(R.id.tv_name);
		ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);
		
		bt_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//1.��ȡָ������ֽ����ļ�
				try {
					Class<?> clazz = Class.forName("android.content.pm.PackageManager");
					//2.��ȡ���÷�������
					Method method = clazz.getMethod("freeStorageAndNotify", long.class,IPackageDataObserver.class);
					//3.��ȡ������÷���
					method.invoke(mPm, Long.MAX_VALUE,new IPackageDataObserver.Stub() {
						@Override
						public void onRemoveCompleted(String packageName, boolean succeeded)
								throws RemoteException {
							//���������ɺ���õķ���(����Ȩ��)
							Message msg = Message.obtain();
							msg.what = CLEAR_CACHE;
							mHandler.sendMessage(msg);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
