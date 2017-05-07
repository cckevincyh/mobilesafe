package indi.cc.mobilesafe.activity;

import indi.cc.mobilesafe.R;
import indi.cc.mobilesafe.db.domain.AppInfo;
import indi.cc.mobilesafe.engine.AppInfoProvider;
import indi.cc.mobilesafe.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class AppManagerActivity extends Activity implements OnClickListener{
	
	private List<AppInfo> mAppInfoList;
	
	private ListView lv_app_list;
	private MyAdapter mAdapter;
	private AppInfo mAppInfo;
	private List<AppInfo> mSystemList;
	private List<AppInfo> mCustomerList;
	private PopupWindow mPopupWindow;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			mAdapter = new MyAdapter();
			lv_app_list.setAdapter(mAdapter);
			
			if(tv_des!=null && mCustomerList!=null){
				tv_des.setText("�û�Ӧ��("+mCustomerList.size()+")");
			}
		};
	};

	private TextView tv_des;
	
	class MyAdapter extends BaseAdapter{
		
		//��ȡ��������������Ŀ���͵�����,�޸ĳ�����(���ı�,ͼƬ+����)
		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount()+1;
		}
		
		//ָ������ָ�����Ŀ����,��Ŀ����״̬��ָ��(0(����ϵͳ),1)
		@Override
		public int getItemViewType(int position) {
			if(position == 0 || position == mCustomerList.size()+1){
				//����0,�����ı���Ŀ��״̬��
				return 0;
			}else{
				//����1,����ͼƬ+�ı���Ŀ״̬��
				return 1;
			}
		}
		
		//listView���������������Ŀ
		@Override
		public int getCount() {
			return mCustomerList.size()+mSystemList.size()+2;
		}

		@Override
		public AppInfo getItem(int position) {
			if(position == 0 || position == mCustomerList.size()+1){
				return null;
			}else{
				if(position<mCustomerList.size()+1){
					return mCustomerList.get(position-1);
				}else{
					//����ϵͳӦ�ö�Ӧ��Ŀ�Ķ���
					return mSystemList.get(position - mCustomerList.size()-2);
				}
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			
			if(type == 0){
				//չʾ��ɫ���ı���Ŀ
				ViewTitleHolder holder = null;
				if(convertView == null){
					convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item_title, null);
					holder = new ViewTitleHolder();
					holder.tv_title = (TextView)convertView.findViewById(R.id.tv_title);
					convertView.setTag(holder);
				}else{
					holder = (ViewTitleHolder) convertView.getTag();
				}
				if(position == 0){
					holder.tv_title.setText("�û�Ӧ��("+mCustomerList.size()+")");
				}else{
					holder.tv_title.setText("ϵͳӦ��("+mSystemList.size()+")");
				}
				return convertView;
			}else{
				//չʾͼƬ+������Ŀ
				ViewHolder holder = null;
				if(convertView == null){
					convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item, null);
					holder = new ViewHolder();
					holder.iv_icon = (ImageView)convertView.findViewById(R.id.iv_icon);
					holder.tv_name = (TextView)convertView.findViewById(R.id.tv_name);
					holder.tv_path = (TextView) convertView.findViewById(R.id.tv_path);
					convertView.setTag(holder);
				}else{
					holder = (ViewHolder) convertView.getTag();
				}
				holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
				holder.tv_name.setText(getItem(position).name);
				if(getItem(position).isSdCard){
					holder.tv_path.setText("sd��Ӧ��");
				}else{
					holder.tv_path.setText("�ֻ�Ӧ��");
				}
				return convertView;
			}
		}
	}
	
	static class ViewHolder{
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_path;
	}
	
	static class ViewTitleHolder{
		TextView tv_title;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		//��ȡ��ͷ�ֻ��ڴ���Ϣ
		initTitle();
		//��ʼ���б�
		initList();
	}

	/**
	 * ��ʼ���б�
	 */
	private void initList() {
		lv_app_list = (ListView) findViewById(R.id.lv_app_list);
		tv_des = (TextView) findViewById(R.id.tv_des);
		
		//ListView���û�������
		lv_app_list.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				//���������е��÷���
				//AbsListView��view����listView����
				//firstVisibleItem��һ���ɼ���Ŀ����ֵ
				//visibleItemCount��ǰһ����Ļ�Ŀɼ���Ŀ��
				//�ܹ���Ŀ����
				if(mCustomerList!=null && mSystemList!=null){
					if(firstVisibleItem>=mCustomerList.size()+1){
						//��������ϵͳ��Ŀ
						tv_des.setText("ϵͳӦ��("+mSystemList.size()+")");
					}else{
						//���������û�Ӧ����Ŀ
						tv_des.setText("�û�Ӧ��("+mCustomerList.size()+")");
					}
				}
				
			}
		});
		
		lv_app_list.setOnItemClickListener(new OnItemClickListener() {
			//view������Ŀָ���view����
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				if(position == 0 || position == mCustomerList.size()+1){
					return;
				}else{
					if(position<mCustomerList.size()+1){
						mAppInfo = mCustomerList.get(position-1);
					}else{
						//����ϵͳӦ�ö�Ӧ��Ŀ�Ķ���
						mAppInfo = mSystemList.get(position - mCustomerList.size()-2);
					}
					showPopupWindow(view);
				}
			}
		});
		
	}
	
	
	@Override
	protected void onResume() {
		//���»�ȡ����
		getData();
		super.onResume();
	}

	private void getData() {
		new Thread(){
			public void run() {
				mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
				mSystemList = new ArrayList<AppInfo>();
				mCustomerList = new ArrayList<AppInfo>();
				for (AppInfo appInfo : mAppInfoList) {
					if(appInfo.isSystem){
						//ϵͳӦ��
						mSystemList.add(appInfo);
					}else{
						//�û�Ӧ��
						mCustomerList.add(appInfo);
					}
				}
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}
	

	protected void showPopupWindow(View view) {
		View popupView = View.inflate(this, R.layout.popupwindow_layout, null);
		
		TextView tv_uninstall = (TextView) popupView.findViewById(R.id.tv_uninstall);
		TextView tv_start = (TextView) popupView.findViewById(R.id.tv_start);
		TextView tv_share = (TextView) popupView.findViewById(R.id.tv_share);
		
		tv_uninstall.setOnClickListener(this);
		tv_start.setOnClickListener(this);
		tv_share.setOnClickListener(this);
		
		//͸������(͸��--->��͸��)
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(1000);
		alphaAnimation.setFillAfter(true);
		
		//���Ŷ���
		ScaleAnimation scaleAnimation = new ScaleAnimation(
				0, 1, 
				0, 1, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(1000);
		alphaAnimation.setFillAfter(true);
		//��������Set
		AnimationSet animationSet = new AnimationSet(true);
		//�����������
		animationSet.addAnimation(alphaAnimation);
		animationSet.addAnimation(scaleAnimation);
		
		//1,�����������,ָ�����
		
		mPopupWindow = new PopupWindow(popupView, 
				LinearLayout.LayoutParams.WRAP_CONTENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT, true);
		//2,����һ��͸������(new ColorDrawable())
		mPopupWindow.setBackgroundDrawable(new ColorDrawable());
		//3,ָ������λ��
		mPopupWindow.showAsDropDown(view, 50, -view.getHeight());
		//4,popupViewִ�ж���
		popupView.startAnimation(animationSet);
		
	}

	private void initTitle() {
		//1,��ȡ����(�ڴ�,�������ֻ������ڴ�)���ô�С,����·��
		String path = Environment.getDataDirectory().getAbsolutePath();
		//2,��ȡsd�����ô�С,sd��·��
		String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		//3,��ȡ��������·�����ļ��еĿ��ô�С
		String memoryAvailSpace = Formatter.formatFileSize(this, getAvailSpace(path));
		String sdMemoryAvailSpace = Formatter.formatFileSize(this,getAvailSpace(sdPath));
		
		TextView tv_memory = (TextView) findViewById(R.id.tv_memory);
		TextView tv_sd_memory = (TextView) findViewById(R.id.tv_sd_memory);
		
		tv_memory.setText("���̿���:"+memoryAvailSpace);
		tv_sd_memory.setText("sd������:"+sdMemoryAvailSpace);
	}

	//int������ٸ�G	
	/**
	 * ����ֵ�����λΪbyte = 8bit,�����Ϊ2147483647 bytes
	 * @param path
	 * @return	����ָ��·�����������byte����ֵ
	 */
	private long getAvailSpace(String path) {
		//��ȡ���ô��̴�С��
		StatFs statFs = new StatFs(path);
		//��ȡ��������ĸ���
		long count = statFs.getAvailableBlocks();
		//��ȡ����Ĵ�С
		long size = statFs.getBlockSize();
		//�����С*����������� == ���ÿռ��С
		return count*size;
//		Integer.MAX_VALUE	����int�������ݵ�����С
//		0x7FFFFFFF
//		
//		2147483647bytes/1024 =  2096128 KB
//		2096128KB/1024 = 2047	MB
//		2047MB = 2G
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_uninstall:
			if(mAppInfo.isSystem){
				ToastUtil.show(getApplicationContext(), "��Ӧ�ò���ж��");
			}else{
				Intent intent = new Intent("android.intent.action.DELETE");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:"+mAppInfo.getPackageName()));
				startActivity(intent);
			}
			break;
		case R.id.tv_start:
			//ͨ������ȥ����ָ������Ӧ��
			PackageManager pm = getPackageManager();
			//ͨ��Launch�����ƶ���������ͼ,ȥ����Ӧ��
			Intent launchIntentForPackage = pm.getLaunchIntentForPackage(mAppInfo.getPackageName());
			if(launchIntentForPackage!=null){
				startActivity(launchIntentForPackage);
			}else{
				ToastUtil.show(getApplicationContext(), "��Ӧ�ò��ܱ�����");
			}
			break;
			//����(������(΢��,����,��Ѷ)ƽ̨),�ǻ۱���
			//����-->����:��ͼƬ�ϴ���΢�ŷ�����,΢���ṩ�ӿ�api,�ƹ�
			//�鿴����Ȧ��ʱ��:�ӷ������ϻ�ȡ����(���ϴ���ͼƬ)
		case R.id.tv_share:
			//ͨ������Ӧ��,���ⷢ�Ͷ���
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_TEXT,"����һ��Ӧ��,Ӧ������Ϊ"+mAppInfo.getName());
			intent.setType("text/plain");
			startActivity(intent);
			break;
		}
		
		//����˴������ʧ����
		if(mPopupWindow!=null){
			mPopupWindow.dismiss();
		}
		
	}

}
