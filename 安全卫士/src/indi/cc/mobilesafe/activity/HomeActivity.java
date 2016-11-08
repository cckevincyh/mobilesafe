package indi.cc.mobilesafe.activity;

import indi.cc.mobilesafe.R;
import indi.cc.mobilesafe.utils.ConstantValue;
import indi.cc.mobilesafe.utils.Md5Util;
import indi.cc.mobilesafe.utils.SpUtil;
import indi.cc.mobilesafe.utils.ToastUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

//主程序界面Activity
public class HomeActivity extends Activity {
	private GridView gv_home; //网格
	private String[] mTitleStrs;	//标题
	private int[] mDrawableIds;	//图片资源id
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //初始化UI
        initUI();
		//初始化数据的方法
		initData();
    }


	/**
	 * 初始化数据
	 */
	private void initData() {
		//准备数据(文字(9组),图片(9张))
		mTitleStrs = new String[]{
				"手机防盗","通信卫士","软件管理","进程管理","流量统计","手机杀毒","缓存清理","高级工具","设置中心"
		};
		//图片
		mDrawableIds = new int[]{
				R.drawable.home_safe,R.drawable.home_callmsgsafe,
				R.drawable.home_apps,R.drawable.home_taskmanager,
				R.drawable.home_netmanager,R.drawable.home_trojan,
				R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_settings
		};
		//九宫格控件设置数据适配器(等同ListView数据适配器)
		gv_home.setAdapter(new MyAdapter());
		//注册九宫格单个条目点击事件
		gv_home.setOnItemClickListener(new OnItemClickListener() {
			//点中列表条目索引position
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch(position){
					case 0:
						//密码防盗
						//开启对话框
						showDialog();
						break;
					case 1:
						//跳转到通信卫士模块
						startActivity(new Intent(getApplicationContext(), BlackNumberActivity.class));
						break;
					case 2:
						//跳转到软件管理模块
						startActivity(new Intent(getApplicationContext(), AppManagerActivity.class));
						break;
					case 3:
						//跳转到进程管理模块
						startActivity(new Intent(getApplicationContext(), ProcessManagerActivity.class));
						break;
					case 4:
						//跳转到流量管理模块
						startActivity(new Intent(getApplicationContext(), TrafficActivity.class));
						break;
					case 5:
						//跳转到手机杀毒模块
						startActivity(new Intent(getApplicationContext(), AnitVirusActivity.class));
						break;
					case 6:
						//跳转到清理缓存模块
//						startActivity(new Intent(getApplicationContext(), CacheClearActivity.class));
						startActivity(new Intent(getApplicationContext(), BaseCacheClearActivity.class));
						
						break;
					case 7:
						//跳转到高级工具功能列表界面
						startActivity(new Intent(getApplicationContext(), AToolActivity.class));
						break;
					case 8:
						//设置中心
						Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
						startActivity(intent);
						break;
				}
			}
		
		});
	}

	/**
	 * 显示对话框
	 */
	protected void showDialog() {
		//判断本地是否有存储密码(sp	字符串)
		String psd = SpUtil.getString(this,ConstantValue.MOBILE_SAFE_PSD,"");
		if(TextUtils.isEmpty(psd)){
			//1.初始设置密码对话框
			showSetPsdDialog();
		}else{
			//2,确认密码对话框
			showConfirmPsdDialog();
		}
		
	}


	/**
	 * 确认密码对话框
	 */
	private void showConfirmPsdDialog() {
		//因为需要去自己定义对话框的展示样式,所以需要调用dialog.setView(view);
		//view是由自己编写的xml转换成的view对象xml----->view
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		final View view = View.inflate(this, R.layout.dialog_confirm_psd, null);
		//让对话框显示一个自己定义的对话框界面效果
		dialog.setView(view);
		dialog.show();
		
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		//确认按钮事件监听
		bt_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText et_confirm_psd = (EditText)view.findViewById(R.id.et_confirm_psd);
				
				String confirmPsd = et_confirm_psd.getText().toString();
				
				if(!TextUtils.isEmpty(confirmPsd)){
					//取出之前设置的密码作比对
					String psd = SpUtil.getString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, "");
					if(psd.equals(Md5Util.encoder(confirmPsd))){
						//进入应用手机防盗模块,开启一个新的activity
						//Intent intent = new Intent(getApplicationContext(), TestActivity.class);
						Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
						startActivity(intent);
						//跳转到新的界面以后需要去隐藏对话框
						dialog.dismiss();
					}else{
						ToastUtil.show(getApplicationContext(),"确认密码错误");
					}
				}else{
					//提示用户密码输入有为空的情况
					ToastUtil.show(getApplicationContext(), "请输入密码");
				}
			}
		});
		//取消按钮事件监听
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * 设置密码对话框
	 */
	private void showSetPsdDialog() {
		//因为需要去自己定义对话框的展示样式,所以需要调用dialog.setView(view);
		//view是由自己编写的xml转换成的view对象xml----->view
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		
		final View view = View.inflate(this,  R.layout.dialog_set_psd, null);
		//让对话框显示一个自己定义的对话框界面效果
		dialog.setView(view);
		dialog.show();
	
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		
		//确定按钮事件监听
		bt_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText et_set_psd = (EditText) view.findViewById(R.id.et_set_psd);
				EditText et_confirm_psd = (EditText)view.findViewById(R.id.et_confirm_psd);
				
				String psd = et_set_psd.getText().toString();
				String confirmPsd = et_confirm_psd.getText().toString();
				
				
				if(!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(confirmPsd)){
					if(psd.equals(confirmPsd)){
						//进入应用手机防盗模块,开启一个新的activity
						//Intent intent = new Intent(getApplicationContext(), TestActivity.class);
						Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
						startActivity(intent);
						//跳转到新的界面以后需要去隐藏对话框
						dialog.dismiss();
						
						SpUtil.putString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, Md5Util.encoder(confirmPsd));
					}else{
						ToastUtil.show(getApplicationContext(),"确认密码错误");
					}
				}else{
					//提示用户密码输入有为空的情况
					ToastUtil.show(getApplicationContext(), "请输入密码");
				}
			}
		});
		//取消按钮事件监听
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}


	/**
	 * 初始化UI
	 */
	private void initUI() {
		//获取网格控件
		gv_home = (GridView) findViewById(R.id.gv_home);
	}

	
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			//条目的总数	文字组数 == 图片张数
			return mTitleStrs.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mTitleStrs[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
			TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
			ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
			tv_title.setText(mTitleStrs[position]);
			iv_icon.setBackgroundResource(mDrawableIds[position]);
			return view;
		}

		
		
	}
 
    
}
