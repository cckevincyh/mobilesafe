package indi.cc.mobilesafe.activity;

import indi.cc.mobilesafe.R;
import indi.cc.mobilesafe.utils.ConstantValue;
import indi.cc.mobilesafe.utils.SpUtil;
import indi.cc.mobilesafe.utils.StreamUtil;
import indi.cc.mobilesafe.utils.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
//������ǰ�Ĺ��ɽ���


public class SplashActivity extends Activity {
	 protected static final String tag = "SplashActivity";
	 /**
		 * �����°汾��״̬��
		 */
		protected static final int UPDATE_VERSION = 100;
		/**
		 * ����Ӧ�ó���������״̬��
		 */
		protected static final int ENTER_HOME = 101;
		
		/**
		 * url��ַ����״̬��
		 */
		protected static final int URL_ERROR = 102;
		protected static final int IO_ERROR = 103;
		protected static final int JSON_ERROR = 104;
	    
		private TextView tv_version_name;//�汾����TextView
		private int mLocalVersionCode;//�汾��
		private String mVersionDes;//���󵽵İ汾����
		private String mDownloadUrl;//���󵽵İ汾URL��ַ

		private RelativeLayout rl_root;
		
		private Handler mHandler = new Handler(){
			@Override
			//alt+ctrl+���¼�ͷ,���¿�����ͬ����
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case UPDATE_VERSION:
					//�����Ի���,��ʾ�û�����
					showUpdateDialog();
					break;
				case ENTER_HOME:
					//����Ӧ�ó���������,activity��ת����
					enterHome();
					break;
				case URL_ERROR:
					ToastUtil.show(getApplicationContext(), "url�쳣");
					enterHome();
					break;
				case IO_ERROR:
					ToastUtil.show(getApplicationContext(), "��ȡ�쳣");
					enterHome();
					break;
				case JSON_ERROR:
					ToastUtil.show(getApplicationContext(), "json�����쳣");
					enterHome();
					break;
				}
				
			}
			
		};
		
		
		@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_splash);
	        //ȥ������ǰactivityͷtitle
//	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        //��ʼ��UI
	        initUI();
	        //��ʼ������
	        initData();
	        //��ʼ������
	        initAnimation();
	        //��ʼ�����ݿ�
	        initDB();
	     
	        //����sp���ж��Ƿ����ɹ���ݷ�ʽ
	        if(!SpUtil.getBoolean(this, ConstantValue.HAS_SHORTCUT, false)){
	        	//���ɿ�ݷ�ʽ
	            initShortCut();
	      
	        }
	    }


		/**
		 * ���ɿ�ݷ�ʽ(��ҪȨ��)
		 */
		private void initShortCut() {
			//1,��intentά��ͼ��,����
			Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
			//ά��ͼ��
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, 
					BitmapFactory.decodeResource(getResources(), R.drawable.mobilesafe));
			//����
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "��ȫ��ʿ");
			//2,�����ݷ�ʽ����ת����activity
			//2.1ά����������ͼ����
			Intent shortCutIntent = new Intent("android.intent.action.HOME");
			shortCutIntent.addCategory("android.intent.category.DEFAULT");
			
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
			//3,���͹㲥
			sendBroadcast(intent);
			//4,��֪sp�Ѿ����ɿ�ݷ�ʽ
			SpUtil.putBoolean(this, ConstantValue.HAS_SHORTCUT, true);
		
		}


		private void initDB() {
			//1,���������ݿ�������
			initAddressDB("address.db");
			//2,���ú������ݿ⿽������
			initAddressDB("commonnum.db");
		}


		/**
		 * �������ݿ�ֵfiles�ļ�����
		 * @param dbName	���ݿ�����
		 */
		private void initAddressDB(String dbName) {
			//1,��files�ļ����´���ͬ��dbName���ݿ��ļ�����
			File files = getFilesDir();
			File file = new File(files, dbName);
			if(file.exists()){
				return;
			}
			InputStream stream = null;
			FileOutputStream fos = null;
			//2,��������ȡ�������ʲ�Ŀ¼�µ��ļ�
			try {
				stream = getAssets().open(dbName);
				//3,����ȡ������д�뵽ָ���ļ��е��ļ���ȥ
				fos = new FileOutputStream(file);
				//4,ÿ�εĶ�ȡ���ݴ�С
				byte[] bs = new byte[1024];
				int temp = -1;
				while( (temp = stream.read(bs))!=-1){
					fos.write(bs, 0, temp);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(stream!=null && fos!=null){
					try {
						stream.close();
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		}

	/**
	 * ��ӵ��붯��Ч��
	 */
	private void initAnimation() {
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(3000);
		rl_root.startAnimation(alphaAnimation);
	}
	
	
	/**
	 * �����Ի���,��ʾ�û�����
	 */
	protected void showUpdateDialog() {
		// �Ի�����������activity���ڵ�
		Builder builder = new AlertDialog.Builder(this);
		//�������Ͻ�ͼ��
		builder.setIcon(R.drawable.mobilesafe);
		builder.setTitle("�汾����");
		//������������
		builder.setMessage(mVersionDes);
		
		//������ť����������
		builder.setPositiveButton("��������", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				//����apk,apk���ӵ�ַ,downloadUrl
				downloadApk();
			}
		});
		builder.setNegativeButton("�Ժ���˵", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//ȡ���Ի���,����������
				enterHome();
			}
		});
		
		//���ȡ���¼�����
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				//��ʹ�û����ȡ��,Ҳ��Ҫ�������Ӧ�ó���������
				enterHome();
				dialog.dismiss();
			}
		});
		builder.show();
	}

	protected void downloadApk() {
		//apk�������ӵ�ַ,����apk������·��
		
		//1.�ж�sd���Ƿ����,�Ƿ������
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			//2.��ȡsd·��
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mobilesafe.apk";
			//3.�������󣬻�ȡapk�����ҷ��õ�ָ��·��
			HttpUtils httpUtils = new HttpUtils();
			//4.�������󣬴��ݲ���(���ص�ַ������Ӧ�÷���λ��)
			httpUtils.download(mDownloadUrl,path, new RequestCallBack<File>() {
				
				@Override
				public void onSuccess(ResponseInfo<File> responseInfo) {
					//���سɹ�(���ع���ķ�����sd����apk)
					File file = responseInfo.result;
					//��ʾ�û���װ
					installApk(file);
				}
				
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					Log.i(tag, "����ʧ��");
					//����ʧ��
				}
				//�ոտ�ʼ���ط���
				@Override
				public void onStart() {
					Log.i(tag, "�ոտ�ʼ����");
					super.onStart();
				}
				//���ع����еķ���(����apk�ܴ�С,��ǰ������λ��,�Ƿ���������)
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					Log.i(tag, "������........");
					Log.i(tag, "total = "+total);
					Log.i(tag, "current = "+current);
					super.onLoading(total, current, isUploading);
				}
			});
		}
		
	}

	/**
	 * ��װ��Ӧapk
	 * @param file	��װ�ļ�
	 */
	protected void installApk(File file) {
		// TODO Auto-generated method stub
		//ϵͳӦ�ý���,Դ��,��װapk���
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		/*//�ļ���Ϊ����Դ
		intent.setData(Uri.fromFile(file));
		//���ð�װ������
		intent.setType("application/vnd.android.package-archive");*/
		intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
//				startActivity(intent);
		startActivityForResult(intent, 0);	
		//�ڰ�װ�е��ȡ��ҲҪ����������ʹ��startActivityForResult����������װӦ��������
	}
	
	//����һ��activity��,���ؽ�����õķ���
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		enterHome();
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * ����Ӧ�ó���������
	 */
	protected void enterHome() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this,HomeActivity.class);
		startActivity(intent);
		//�ڿ���һ���µĽ����,����������ر�(��������ֻ�ɼ�һ��)
		finish();
	}


	/**
	 * ��ȡ���ݷ���
	 */
	private void initData() {
		//1.Ӧ�ð汾����
		tv_version_name.setText("�汾����:"+getVersionName());
		//���(���ذ汾�źͷ������汾�űȶ�)�Ƿ��и���,����и���,��ʾ�û�����(member)
		//2.��ȡ���ذ汾��
		mLocalVersionCode = getVersionCode();
		//3.��ȡ�������汾��(�ͻ��˷�����,����˸���Ӧ,(json,xml))
		//http://www.oxxx.com/update74.json?key=value  ����200 ����ɹ�,���ķ�ʽ�����ݶ�ȡ����
		//json�����ݰ���:
		/* ���°汾�İ汾����
		 * �°汾��������Ϣ
		 * �������汾��
		 * �°汾apk���ص�ַ*/
		if(SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false)){
			checkVersion();
		}else{
			//ֱ�ӽ���Ӧ�ó���������
//			enterHome();
			//��Ϣ����
//			mHandler.sendMessageDelayed(msg, 4000);
			//�ڷ�����Ϣ4���ȥ����,ENTER_HOME״̬��ָ�����Ϣ
			mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
		}
	}
	
	
	
	/**
	 * ���汾��
	 */	
	private void checkVersion() {
		new Thread(){
			public void run() {
				//���������ȡ���ݣ�������Ϊ����json�����ӵ�ַ
				//���������ȡ����,������Ϊ����json�����ӵ�ַ
				//http://192.168.13.99:8080/update.json	���Խ׶β�������
				//������ģ�������ʵ���tomcat
				Message msg = Message.obtain();
				long startTime = System.currentTimeMillis();
				
				try {
					//1.��װurl��ַ
					URL url = new URL("http://10.0.2.2:8080/update.json");
					//2.����һ������
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					//3.���ó����������(����ͷ)
					//����ʱ
					connection.setConnectTimeout(2000);
					//��ȡ��ʱ
					connection.setReadTimeout(2000);
					
					//Ĭ�Ͼ���get����ʽ,
//					connection.setRequestMethod("POST");
					
					//4.��ȡ����ɹ���Ӧ��
					if(connection.getResponseCode() == 200){
						//5.��������ʽ�������ݻ�ȡ����
						InputStream is = connection.getInputStream();
						//6.����ת�����ַ���(�������װ)
						String json = StreamUtil.streamToString(is);
						Log.i(tag, json);
						//7.json����
						JSONObject jsonObject = new JSONObject(json);
						//debug����,�������
						String versionName = jsonObject.getString("versionName");
						mVersionDes = jsonObject.getString("versionDes");
						String versionCode = jsonObject.getString("versionCode");
						mDownloadUrl = jsonObject.getString("downloadUrl");
						//��־��ӡ	
						Log.i(tag, versionName);
						Log.i(tag, mVersionDes);
						Log.i(tag, versionCode);
						Log.i(tag, mDownloadUrl);
						//8.�ȶ԰汾��(�������汾��>���ذ汾��,��ʾ�û�����)
						if(mLocalVersionCode<Integer.parseInt(versionCode)){
							//��ʾ�û�����,�����Ի���(UI),��Ϣ����
							msg.what = UPDATE_VERSION;
						}else{
							//����Ӧ�ó���������
							msg.what = ENTER_HOME;
						}
					}
				} catch (MalformedURLException e) {
				//URL����
					e.printStackTrace();
					msg.what = URL_ERROR;
				} catch (IOException e) {
					//IO����
					e.printStackTrace();
					msg.what = IO_ERROR;
				} catch (JSONException e) {
					//JSON��������
					e.printStackTrace();
					msg.what = JSON_ERROR;
				}finally{
					//ָ��˯��ʱ��,���������ʱ������4����������
					//���������ʱ��С��4��,ǿ������˯����4����
					long endTime = System.currentTimeMillis();
					if(endTime-startTime<4000){
						try {
							Thread.sleep(4000-(endTime-startTime));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					mHandler.sendMessage(msg);
				}
			}
		}.start();
		
		/*new Thread(new Runnable() {
		@Override
		public void run() {
			
		}
	});*/
	}


	/**
	 * ��ȡ�汾����:�嵥�ļ���
	 * @return	Ӧ�ð汾����	����null�����쳣
	 */
	private String getVersionName() {
		//1.�������߶���packageManager
		PackageManager pm = getPackageManager();
		//2.�Ӱ��Ĺ����߶����У���ȡָ�������Ļ�����Ϣ(�汾�ţ��汾����),��0�����ȡ������Ϣ
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			//3.��ȡ�汾����
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}


	/**
	 * ���ذ汾��
	 * @return	
	 * 			��0 ������ȡ�ɹ�
	 */
	private int getVersionCode() {
		//1.�������߶���packageManager
				PackageManager pm = getPackageManager();
				//2.�Ӱ��Ĺ����߶����У���ȡָ�������Ļ�����Ϣ(�汾�ţ��汾����),��0�����ȡ������Ϣ
				try {
					PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
					//3.��ȡ�汾��
					return packageInfo.versionCode;
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return 0;
	}


	/**
	 * ��ʼ��UI����	alt+shift+j
	 */
	private void initUI() {
		tv_version_name = (TextView) findViewById(R.id.tv_version_name);
		rl_root = (RelativeLayout) findViewById(R.id.rl_root);
	}
}
