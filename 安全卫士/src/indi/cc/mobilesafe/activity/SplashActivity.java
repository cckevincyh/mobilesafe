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
//程序开启前的过渡界面


public class SplashActivity extends Activity {
	 protected static final String tag = "SplashActivity";
	 /**
		 * 更新新版本的状态码
		 */
		protected static final int UPDATE_VERSION = 100;
		/**
		 * 进入应用程序主界面状态码
		 */
		protected static final int ENTER_HOME = 101;
		
		/**
		 * url地址出错状态码
		 */
		protected static final int URL_ERROR = 102;
		protected static final int IO_ERROR = 103;
		protected static final int JSON_ERROR = 104;
	    
		private TextView tv_version_name;//版本名称TextView
		private int mLocalVersionCode;//版本号
		private String mVersionDes;//请求到的版本描述
		private String mDownloadUrl;//请求到的版本URL地址

		private RelativeLayout rl_root;
		
		private Handler mHandler = new Handler(){
			@Override
			//alt+ctrl+向下箭头,向下拷贝相同代码
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case UPDATE_VERSION:
					//弹出对话框,提示用户更新
					showUpdateDialog();
					break;
				case ENTER_HOME:
					//进入应用程序主界面,activity跳转过程
					enterHome();
					break;
				case URL_ERROR:
					ToastUtil.show(getApplicationContext(), "url异常");
					enterHome();
					break;
				case IO_ERROR:
					ToastUtil.show(getApplicationContext(), "读取异常");
					enterHome();
					break;
				case JSON_ERROR:
					ToastUtil.show(getApplicationContext(), "json解析异常");
					enterHome();
					break;
				}
				
			}
			
		};
		
		
		@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_splash);
	        //去除掉当前activity头title
//	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        //初始化UI
	        initUI();
	        //初始化数据
	        initData();
	        //初始化动画
	        initAnimation();
	        //初始化数据库
	        initDB();
	     
	        //根据sp中判断是否生成过快捷方式
	        if(!SpUtil.getBoolean(this, ConstantValue.HAS_SHORTCUT, false)){
	        	//生成快捷方式
	            initShortCut();
	      
	        }
	    }


		/**
		 * 生成快捷方式(需要权限)
		 */
		private void initShortCut() {
			//1,给intent维护图标,名称
			Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
			//维护图标
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, 
					BitmapFactory.decodeResource(getResources(), R.drawable.mobilesafe));
			//名称
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "安全卫士");
			//2,点击快捷方式后跳转到的activity
			//2.1维护开启的意图对象
			Intent shortCutIntent = new Intent("android.intent.action.HOME");
			shortCutIntent.addCategory("android.intent.category.DEFAULT");
			
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
			//3,发送广播
			sendBroadcast(intent);
			//4,告知sp已经生成快捷方式
			SpUtil.putBoolean(this, ConstantValue.HAS_SHORTCUT, true);
		
		}


		private void initDB() {
			//1,归属地数据拷贝过程
			initAddressDB("address.db");
			//2,常用号码数据库拷贝过程
			initAddressDB("commonnum.db");
		}


		/**
		 * 拷贝数据库值files文件夹下
		 * @param dbName	数据库名称
		 */
		private void initAddressDB(String dbName) {
			//1,在files文件夹下创建同名dbName数据库文件过程
			File files = getFilesDir();
			File file = new File(files, dbName);
			if(file.exists()){
				return;
			}
			InputStream stream = null;
			FileOutputStream fos = null;
			//2,输入流读取第三方资产目录下的文件
			try {
				stream = getAssets().open(dbName);
				//3,将读取的内容写入到指定文件夹的文件中去
				fos = new FileOutputStream(file);
				//4,每次的读取内容大小
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
	 * 添加淡入动画效果
	 */
	private void initAnimation() {
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(3000);
		rl_root.startAnimation(alphaAnimation);
	}
	
	
	/**
	 * 弹出对话框,提示用户更新
	 */
	protected void showUpdateDialog() {
		// 对话框，是依赖于activity存在的
		Builder builder = new AlertDialog.Builder(this);
		//设置左上角图标
		builder.setIcon(R.drawable.mobilesafe);
		builder.setTitle("版本更新");
		//设置描述内容
		builder.setMessage(mVersionDes);
		
		//积极按钮，立即更新
		builder.setPositiveButton("立即更新", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				//下载apk,apk链接地址,downloadUrl
				downloadApk();
			}
		});
		builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//取消对话框,进入主界面
				enterHome();
			}
		});
		
		//点击取消事件监听
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				//即使用户点击取消,也需要让其进入应用程序主界面
				enterHome();
				dialog.dismiss();
			}
		});
		builder.show();
	}

	protected void downloadApk() {
		//apk下载链接地址,放置apk的所在路径
		
		//1.判断sd卡是否可用,是否挂在上
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			//2.获取sd路径
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mobilesafe.apk";
			//3.发送请求，获取apk，并且放置到指定路径
			HttpUtils httpUtils = new HttpUtils();
			//4.发送请求，传递参数(下载地址，下载应用放置位置)
			httpUtils.download(mDownloadUrl,path, new RequestCallBack<File>() {
				
				@Override
				public void onSuccess(ResponseInfo<File> responseInfo) {
					//下载成功(下载过后的放置在sd卡中apk)
					File file = responseInfo.result;
					//提示用户安装
					installApk(file);
				}
				
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					Log.i(tag, "下载失败");
					//下载失败
				}
				//刚刚开始下载方法
				@Override
				public void onStart() {
					Log.i(tag, "刚刚开始下载");
					super.onStart();
				}
				//下载过程中的方法(下载apk总大小,当前的下载位置,是否正在下载)
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					Log.i(tag, "下载中........");
					Log.i(tag, "total = "+total);
					Log.i(tag, "current = "+current);
					super.onLoading(total, current, isUploading);
				}
			});
		}
		
	}

	/**
	 * 安装对应apk
	 * @param file	安装文件
	 */
	protected void installApk(File file) {
		// TODO Auto-generated method stub
		//系统应用界面,源码,安装apk入口
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		/*//文件作为数据源
		intent.setData(Uri.fromFile(file));
		//设置安装的类型
		intent.setType("application/vnd.android.package-archive");*/
		intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
//				startActivity(intent);
		startActivityForResult(intent, 0);	
		//在安装中点击取消也要做处理，所以使用startActivityForResult方法开启安装应用做处理
	}
	
	//开启一个activity后,返回结果调用的方法
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		enterHome();
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 进入应用程序主界面
	 */
	protected void enterHome() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this,HomeActivity.class);
		startActivity(intent);
		//在开启一个新的界面后,将导航界面关闭(导航界面只可见一次)
		finish();
	}


	/**
	 * 获取数据方法
	 */
	private void initData() {
		//1.应用版本名称
		tv_version_name.setText("版本名称:"+getVersionName());
		//检测(本地版本号和服务器版本号比对)是否有更新,如果有更新,提示用户下载(member)
		//2.获取本地版本号
		mLocalVersionCode = getVersionCode();
		//3.获取服务器版本号(客户端发请求,服务端给响应,(json,xml))
		//http://www.oxxx.com/update74.json?key=value  返回200 请求成功,流的方式将数据读取下来
		//json中内容包含:
		/* 更新版本的版本名称
		 * 新版本的描述信息
		 * 服务器版本号
		 * 新版本apk下载地址*/
		if(SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false)){
			checkVersion();
		}else{
			//直接进入应用程序主界面
//			enterHome();
			//消息机制
//			mHandler.sendMessageDelayed(msg, 4000);
			//在发送消息4秒后去处理,ENTER_HOME状态码指向的消息
			mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
		}
	}
	
	
	
	/**
	 * 检测版本号
	 */	
	private void checkVersion() {
		new Thread(){
			public void run() {
				//发送请求获取数据，参数则为请求json的链接地址
				//发送请求获取数据,参数则为请求json的链接地址
				//http://192.168.13.99:8080/update.json	测试阶段不是最优
				//仅限于模拟器访问电脑tomcat
				Message msg = Message.obtain();
				long startTime = System.currentTimeMillis();
				
				try {
					//1.封装url地址
					URL url = new URL("http://10.0.2.2:8080/update.json");
					//2.开启一个链接
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					//3.设置常见请求参数(请求头)
					//请求超时
					connection.setConnectTimeout(2000);
					//读取超时
					connection.setReadTimeout(2000);
					
					//默认就是get请求方式,
//					connection.setRequestMethod("POST");
					
					//4.获取请求成功响应码
					if(connection.getResponseCode() == 200){
						//5.以流的形式，将数据获取下来
						InputStream is = connection.getInputStream();
						//6.将流转换成字符串(工具类封装)
						String json = StreamUtil.streamToString(is);
						Log.i(tag, json);
						//7.json解析
						JSONObject jsonObject = new JSONObject(json);
						//debug调试,解决问题
						String versionName = jsonObject.getString("versionName");
						mVersionDes = jsonObject.getString("versionDes");
						String versionCode = jsonObject.getString("versionCode");
						mDownloadUrl = jsonObject.getString("downloadUrl");
						//日志打印	
						Log.i(tag, versionName);
						Log.i(tag, mVersionDes);
						Log.i(tag, versionCode);
						Log.i(tag, mDownloadUrl);
						//8.比对版本号(服务器版本号>本地版本号,提示用户更新)
						if(mLocalVersionCode<Integer.parseInt(versionCode)){
							//提示用户更新,弹出对话框(UI),消息机制
							msg.what = UPDATE_VERSION;
						}else{
							//进入应用程序主界面
							msg.what = ENTER_HOME;
						}
					}
				} catch (MalformedURLException e) {
				//URL错误
					e.printStackTrace();
					msg.what = URL_ERROR;
				} catch (IOException e) {
					//IO错误
					e.printStackTrace();
					msg.what = IO_ERROR;
				} catch (JSONException e) {
					//JSON解析错误
					e.printStackTrace();
					msg.what = JSON_ERROR;
				}finally{
					//指定睡眠时间,请求网络的时长超过4秒则不做处理
					//请求网络的时长小于4秒,强制让其睡眠满4秒钟
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
	 * 获取版本名称:清单文件中
	 * @return	应用版本名称	返回null代表异常
	 */
	private String getVersionName() {
		//1.包管理者对象packageManager
		PackageManager pm = getPackageManager();
		//2.从包的管理者对象中，获取指定包名的基本信息(版本号，版本名称),传0代表获取基本信息
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			//3.获取版本名称
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}


	/**
	 * 返回版本号
	 * @return	
	 * 			非0 则代表获取成功
	 */
	private int getVersionCode() {
		//1.包管理者对象packageManager
				PackageManager pm = getPackageManager();
				//2.从包的管理者对象中，获取指定包名的基本信息(版本号，版本名称),传0代表获取基本信息
				try {
					PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
					//3.获取版本号
					return packageInfo.versionCode;
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return 0;
	}


	/**
	 * 初始化UI方法	alt+shift+j
	 */
	private void initUI() {
		tv_version_name = (TextView) findViewById(R.id.tv_version_name);
		rl_root = (RelativeLayout) findViewById(R.id.rl_root);
	}
}
