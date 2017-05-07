package indi.cc.mobilesafe.activity;

import indi.cc.mobilesafe.R;
import indi.cc.mobilesafe.engine.VirusDao;
import indi.cc.mobilesafe.utils.Md5Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AnitVirusActivity extends Activity {
	protected static final int SCANING = 100;

	protected static final int SCAN_FINISH = 101;
	
	private ImageView iv_scanning;
	private TextView tv_name;
	private ProgressBar pb_bar;
	private LinearLayout ll_add_text;
	private int index = 0;
	private List<ScanInfo> mVirusScanInfoList;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANING:
				//1,��ʾ����ɨ��Ӧ�õ�����
				ScanInfo info = (ScanInfo)msg.obj;
				tv_name.setText(info.name);
				//2,�����Բ��������һ������ɨ��Ӧ�õ�TextView
				TextView textView = new TextView(getApplicationContext());
				if(info.isVirus){
					//�ǲ���
					textView.setTextColor(Color.RED);
					textView.setText("���ֲ���:"+info.name);
				}else{
					//���ǲ���
					textView.setTextColor(Color.BLACK);
					textView.setText("ɨ�谲ȫ:"+info.name);
				}
				ll_add_text.addView(textView, 0);
				break;
			case SCAN_FINISH:
				tv_name.setText("ɨ�����");
				//ֹͣ����ִ�е���ת����
				iv_scanning.clearAnimation();
				//��֪�û�ж�ذ����˲�����Ӧ��
				unInstallVirus();
				break;
			}
		};
	};

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anit_virus);
		//��ʼ��UI
		initUI();
		//��ʼ������
		initAnimation();
		//ɱ��
		checkVirus();
	}

	/**
	 * ж���ж���Ӧ��
	 */
	protected void unInstallVirus() {
		for(ScanInfo scanInfo:mVirusScanInfoList){
			String packageName = scanInfo.packageName;
			//Դ��
			Intent intent = new Intent("android.intent.action.DELETE");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setData(Uri.parse("package:"+packageName));
			startActivity(intent);
		}
	}

	/**
	 * ɨ��ɱ��
	 */
	private void checkVirus() {
		new Thread(){
			public void run() {
				//��ȡ���ݿ������еĲ�����md5��
				List<String> virusList = VirusDao.getVirusList();
				//��ȡ�ֻ����������Ӧ�ó���ǩ���ļ���md5��
				//1.��ȡ�������߶���
				PackageManager pm = getPackageManager();
				//2.��ȡ����Ӧ�ó���ǩ���ļ�(PackageManager.GET_SIGNATURES �Ѱ�װӦ�õ�ǩ���ļ�+)
				//PackageManager.GET_UNINSTALLED_PACKAGES	ж�����˵�Ӧ��,������ļ�
				List<PackageInfo> packageInfoList = pm.getInstalledPackages(
						PackageManager.GET_SIGNATURES + PackageManager.GET_UNINSTALLED_PACKAGES);
				//������¼�����ļ���
				
				mVirusScanInfoList = new ArrayList<ScanInfo>();
				
				//��¼����Ӧ�õļ���
				List<ScanInfo> scanInfoList = new ArrayList<ScanInfo>();
				
				//���ý����������ֵ
				pb_bar.setMax(packageInfoList.size());
				
				//3.����Ӧ�ü���
				for (PackageInfo packageInfo : packageInfoList) {
					ScanInfo scanInfo = new ScanInfo();
					//��ȡǩ���ļ�������
					Signature[] signatures = packageInfo.signatures;
					//��ȡǩ���ļ�����ĵ�һλ,Ȼ�����md5,����md5�����ݿ��е�md5�ȶ�
					Signature signature = signatures[0];
					String string = signature.toCharsString();
					//32λ�ַ���,16�����ַ�(0-f)
					String encoder = Md5Util.encoder(string);
					//4,�ȶ�Ӧ���Ƿ�Ϊ����
					if(virusList.contains(encoder)){
						//5.��¼����
						scanInfo.isVirus = true;
						mVirusScanInfoList.add(scanInfo);
					}else{
						scanInfo.isVirus = false;
					}
					//6,ά������İ���,�Լ�Ӧ������
					scanInfo.packageName = packageInfo.packageName;
					scanInfo.name = packageInfo.applicationInfo.loadLabel(pm).toString();
					scanInfoList.add(scanInfo);
					//7.��ɨ��Ĺ�����,��Ҫ���½�����
					index++;
					pb_bar.setProgress(index);

					try {
						Thread.sleep(50+new Random().nextInt(100));	//�ӳ���ʾ���û��������ȽϺ�
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					//8.�����߳��з�����Ϣ,��֪���̸߳���UI(1:����ɨ��Ӧ�õ�����2:ɨ������������Բ��������view)
					Message msg = Message.obtain();
					msg.what = SCANING;
					msg.obj = scanInfo;
					mHandler.sendMessage(msg);
				}
				Message msg = Message.obtain();
				msg.what = SCAN_FINISH;
				mHandler.sendMessage(msg);
			};
		}.start();
	}
	/**
	 * ɨ����Ϣ
	 * @author c
	 *
	 */
	class ScanInfo{
		public boolean isVirus;
		public String packageName;
		public String name;
	}

	/**
	 * ��ʼ������
	 */
	private void initAnimation() {
		RotateAnimation rotateAnimation = new RotateAnimation(
				0, 360, 
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(1000);
		//ָ������һֱ��ת
//		rotateAnimation.setRepeatMode(RotateAnimation.INFINITE);
		rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
		//���ֶ���ִ�н������״̬
		rotateAnimation.setFillAfter(true);
		//һֱִ�ж���
		iv_scanning.startAnimation(rotateAnimation);
	}

	/**
	 * ��ʼ��UI
	 */
	private void initUI() {
		iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
		tv_name = (TextView) findViewById(R.id.tv_name);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);
	}
}
