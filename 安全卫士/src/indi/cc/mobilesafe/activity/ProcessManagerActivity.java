package indi.cc.mobilesafe.activity;

import indi.cc.mobilesafe.R;
import indi.cc.mobilesafe.db.domain.ProcessInfo;
import indi.cc.mobilesafe.engine.ProcessInfoProvider;
import indi.cc.mobilesafe.utils.ConstantValue;
import indi.cc.mobilesafe.utils.SpUtil;
import indi.cc.mobilesafe.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ProcessManagerActivity extends Activity implements OnClickListener{
	private TextView tv_process_count,tv_memory_info,tv_des;
	private ListView lv_process_list;
	private Button bt_select_all,bt_select_reverse,bt_clear,bt_setting;
	private int mProcessCount;
	private List<ProcessInfo> mProcessInfoList;
	
	private ArrayList<ProcessInfo> mSystemList;
	private ArrayList<ProcessInfo> mCustomerList;
	private MyAdapter mAdapter;
	
	private ProcessInfo mProcessInfo;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			mAdapter = new MyAdapter();
			lv_process_list.setAdapter(mAdapter);
			
			if(tv_des!=null && mCustomerList!=null){
				tv_des.setText("�û�Ӧ��("+mCustomerList.size()+")");
			}
		};
	};
	private long mAvailSpace;
	private String mStrTotalSpace;

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
			//����sp�е��Ƿ���ʾϵͳ��������ʾ��Ŀ��
			if(SpUtil.getBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM, false)){
				return mCustomerList.size()+mSystemList.size()+2;
			}else{
				return mCustomerList.size()+1;
			}
		}

		@Override
		public ProcessInfo getItem(int position) {
			if(position == 0 || position == mCustomerList.size()+1){
				return null;
			}else{
				if(position<mCustomerList.size()+1){
					return mCustomerList.get(position-1);
				}else{
					//����ϵͳ���̶�Ӧ��Ŀ�Ķ���
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
					holder.tv_title.setText("�û�����("+mCustomerList.size()+")");
				}else{
					holder.tv_title.setText("ϵͳ����("+mSystemList.size()+")");
				}
				return convertView;
			}else{
				//չʾͼƬ+������Ŀ
				ViewHolder holder = null;
				if(convertView == null){
					convertView = View.inflate(getApplicationContext(), R.layout.listview_process_item, null);
					holder = new ViewHolder();
					holder.iv_icon = (ImageView)convertView.findViewById(R.id.iv_icon);
					holder.tv_name = (TextView)convertView.findViewById(R.id.tv_name);
					holder.tv_memory_info = (TextView) convertView.findViewById(R.id.tv_memory_info);
					holder.cb_box = (CheckBox) convertView.findViewById(R.id.cb_box);
					convertView.setTag(holder);
				}else{
					holder = (ViewHolder) convertView.getTag();
				}
				holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
				holder.tv_name.setText(getItem(position).name);
				String strSize = Formatter.formatFileSize(getApplicationContext(), getItem(position).memSize);
				holder.tv_memory_info.setText(strSize);
				
				//�����̲��ܱ�ѡ��,�����Ƚ�checkbox���ص�
				if(getItem(position).packageName.equals(getPackageName())){
					holder.cb_box.setVisibility(View.GONE);
				}else{
					holder.cb_box.setVisibility(View.VISIBLE);
				}
				
				holder.cb_box.setChecked(getItem(position).isCheck);
				
				return convertView;
			}
		}
	}
	
	static class ViewHolder{
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_memory_info;
		CheckBox cb_box;
	}
	
	static class ViewTitleHolder{
		TextView tv_title;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_manager);
		
		initUI();
		initTitleData();
		initListData();
	}

	private void initListData() {
		getData();
	}
	
	private void getData() {
		new Thread(){
			public void run() {
				mProcessInfoList = ProcessInfoProvider.getProcessInfo(getApplicationContext());
				mSystemList = new ArrayList<ProcessInfo>();
				mCustomerList = new ArrayList<ProcessInfo>();
				
				for (ProcessInfo info : mProcessInfoList) {
					if(info.isSystem){
						//ϵͳ����
						mSystemList.add(info);
					}else{
						//�û�����
						mCustomerList.add(info);
					}
				}
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void initTitleData() {
		mProcessCount = ProcessInfoProvider.getProcessCount(this);
		tv_process_count.setText("��������:"+mProcessCount);
		
		//��ȡ�����ڴ��С,���Ҹ�ʽ��
		mAvailSpace = ProcessInfoProvider.getAvailSpace(this);
		String strAvailSpace = Formatter.formatFileSize(this, mAvailSpace);
		
		//�������ڴ��С,���Ҹ�ʽ��
		long totalSpace = ProcessInfoProvider.getTotalSpace(this);
		mStrTotalSpace = Formatter.formatFileSize(this, totalSpace);
		
		tv_memory_info.setText("ʣ��/�ܹ�:"+strAvailSpace+"/"+mStrTotalSpace);
	}

	private void initUI() {
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_memory_info = (TextView) findViewById(R.id.tv_memory_info);
		
		tv_des = (TextView) findViewById(R.id.tv_des);
	
		lv_process_list = (ListView) findViewById(R.id.lv_process_list);

		bt_select_all = (Button) findViewById(R.id.bt_select_all);
		bt_select_reverse = (Button) findViewById(R.id.bt_select_reverse);
		bt_clear = (Button)  findViewById(R.id.bt_clear);
		bt_setting = (Button) findViewById(R.id.bt_setting);
		
		bt_select_all.setOnClickListener(this);
		bt_select_reverse.setOnClickListener(this);
		bt_clear.setOnClickListener(this);
		bt_setting.setOnClickListener(this);
		
		lv_process_list.setOnScrollListener(new OnScrollListener() {
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
						tv_des.setText("ϵͳ����("+mSystemList.size()+")");
					}else{
						//���������û�Ӧ����Ŀ
						tv_des.setText("�û�����("+mCustomerList.size()+")");
					}
				}
				
			}
		});
		
		lv_process_list.setOnItemClickListener(new OnItemClickListener() {
			//viewѡ����Ŀָ���view����
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				if(position == 0 || position == mCustomerList.size()+1){
					return;
				}else{
					if(position<mCustomerList.size()+1){
						mProcessInfo = mCustomerList.get(position-1);
					}else{
						//����ϵͳӦ�ö�Ӧ��Ŀ�Ķ���
						mProcessInfo = mSystemList.get(position - mCustomerList.size()-2);
					}
					if(mProcessInfo!=null){
						if(!mProcessInfo.packageName.equals(getPackageName())){
							//ѡ����Ŀָ��Ķ���ͱ�Ӧ�õİ�����һ��,����Ҫȥ״̬ȡ�������õ�ѡ��״̬
							//״̬ȡ��
							mProcessInfo.isCheck = !mProcessInfo.isCheck;
							//checkbox��ʾ״̬�л�
							//ͨ��ѡ����Ŀ��view����,findViewById�ҵ�����Ŀָ���cb_box,Ȼ���л���״̬
							CheckBox cb_box = (CheckBox) view.findViewById(R.id.cb_box);
							cb_box.setChecked(mProcessInfo.isCheck);
						}
					}
				}
			}
		});
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_select_all:
			selectAll();
			break;
		case R.id.bt_select_reverse:
			selectReverse();
			break;
		case R.id.bt_clear:
			clearAll();
			break;
		case R.id.bt_setting:
			setting();
			break;
		}
	}
	/**
	 * ����
	 */
	private void setting() {
		Intent intent = new Intent(this, ProcessSettingActivity.class);
		startActivityForResult(intent, 0);
	}
	/**
	 * ˢ������
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//֪ͨ����������ˢ��
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * ����ѡ�н���
	 */
	private void clearAll() {
		//1,��ȡѡ�н���
		//2,����һ����¼��Ҫɱ���Ľ��̵ļ���
		List<ProcessInfo> killProcessList = new ArrayList<ProcessInfo>();
		for(ProcessInfo processInfo:mCustomerList){
			if(processInfo.getPackageName().equals(getPackageName())){
				continue;
			}
			if(processInfo.isCheck){
				//�����ڼ���ѭ��������ȥ�Ƴ������еĶ���
//				mCustomerList.remove(processInfo);
				//3,��¼��Ҫɱ�����û�����
				killProcessList.add(processInfo);
			}
		}
		
		for(ProcessInfo processInfo:mSystemList){
			if(processInfo.isCheck){
				//4,��¼��Ҫɱ����ϵͳ����
				killProcessList.add(processInfo);
			}
		}
		//5,ѭ������killProcessList,Ȼ��ȥ�Ƴ�mCustomerList��mSystemList�еĶ���
		long totalReleaseSpace = 0;
		for (ProcessInfo processInfo : killProcessList) {
			//6,�жϵ�ǰ�������Ǹ�������,�����ڼ������Ƴ�
			if(mCustomerList.contains(processInfo)){
				mCustomerList.remove(processInfo);
			}
			
			if(mSystemList.contains(processInfo)){
				mSystemList.remove(processInfo);
			}
			//7,ɱ����¼��killProcessList�еĽ���
			ProcessInfoProvider.killProcess(this,processInfo);
			
			//��¼�ͷſռ���ܴ�С
			totalReleaseSpace += processInfo.memSize;
		}
		//8,�ڼ��ϸı��,��Ҫ֪ͨ����������ˢ��
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
		//9,���������ĸ���
		mProcessCount -= killProcessList.size();
		//10,���¿���ʣ��ռ�(�ͷſռ�+ԭ��ʣ��ռ� == ��ǰʣ��ռ�)
		mAvailSpace += totalReleaseSpace;
		//11,���ݽ���������ʣ��ռ��С
		tv_process_count.setText("��������:"+mProcessCount);
		tv_memory_info.setText("ʣ��/�ܹ�"+Formatter.formatFileSize(this, mAvailSpace)+"/"+mStrTotalSpace);
		//12,ͨ����˾��֪�û�,�ͷ��˶��ٿռ�,ɱ���˼�������,
		String totalRelease = Formatter.formatFileSize(this, totalReleaseSpace);
//		ToastUtil.show(getApplicationContext(), "ɱ����"+killProcessList.size()+"������,�ͷ���"+totalRelease+"�ռ�");
		
//		jni  java--c   c---java
		//ռλ��ָ������%d��������ռλ��,%s�����ַ���ռλ��
		ToastUtil.show(getApplicationContext(), 
				String.format("ɱ����%d����,�ͷ���%s�ռ�", killProcessList.size(),totalRelease));
	}
	/**
	 * ��ѡ
	 */
	private void selectReverse() {
		//1,�����еļ����еĶ�����isCheck�ֶ�ȡ��
		for(ProcessInfo processInfo:mCustomerList){
			if(processInfo.getPackageName().equals(getPackageName())){
				continue;
			}
			processInfo.isCheck = !processInfo.isCheck;
		}
		for(ProcessInfo processInfo:mSystemList){
			processInfo.isCheck = !processInfo.isCheck;
		}
		//2,֪ͨ����������ˢ��
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
	}
	/**
	 * ȫѡ
	 */
	private void selectAll() {
		//1,�����еļ����еĶ�����isCheck�ֶ�����Ϊtrue,����ȫѡ,�ų���ǰӦ��
		for(ProcessInfo processInfo:mCustomerList){
			if(processInfo.getPackageName().equals(getPackageName())){
				continue;
			}
			processInfo.isCheck = true;
		}
		for(ProcessInfo processInfo:mSystemList){
			processInfo.isCheck = true;
		}
		//2,֪ͨ����������ˢ��
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
	}
}
