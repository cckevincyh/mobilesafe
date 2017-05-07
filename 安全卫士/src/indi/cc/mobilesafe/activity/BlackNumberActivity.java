package indi.cc.mobilesafe.activity;

import indi.cc.mobilesafe.R;
import indi.cc.mobilesafe.db.dao.BlackNumberDao;
import indi.cc.mobilesafe.db.domain.BlackNumberInfo;
import indi.cc.mobilesafe.utils.ToastUtil;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;




//1,����convertView
//2,��findViewById�������Ż�,ʹ��ViewHolder
//3,��ViewHolder����ɾ�̬,����ȥ�����������
//4,listView����ж����Ŀ��ʱ��,��������ҳ�㷨,ÿһ�μ���20��,���򷵻�

public class BlackNumberActivity extends Activity{
	private Button bt_add;
	private ListView lv_blacknumber;
	private BlackNumberDao mDao;
	private List<BlackNumberInfo> mBlackNumberList;
	private MyAdapter mAdapter;
	private int mode = 1;
	private boolean mIsLoad = false;
	private int mCount;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			//4,��֪listView����ȥ��������������
			if(mAdapter == null){
				mAdapter = new MyAdapter();
				lv_blacknumber.setAdapter(mAdapter);
			}else{
				mAdapter.notifyDataSetChanged();
			}
		};
	};
	
	
	class MyAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return mBlackNumberList.size();
		}

		@Override
		public Object getItem(int position) {
			return mBlackNumberList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
//			View view = null;
/*			if(convertView == null){
				view = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);
			}else{
				view = convertView;
			}*/
			
			//1,����convertView
			
			//����viewHolder����һ
			ViewHolder holder = null;
			if(convertView == null){
				convertView = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);
				//2,����findViewById()����
				//����viewHolder������
				holder = new ViewHolder();
				//����viewHolder������
				holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
				holder.tv_mode = (TextView)convertView.findViewById(R.id.tv_mode);
				holder.iv_delete = (ImageView)convertView.findViewById(R.id.iv_delete);
				//����viewHolder������
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//1,���ݿ�ɾ��
					mDao.delete(mBlackNumberList.get(position).phone);
					//2,�����е�ɾ��
					mBlackNumberList.remove(position);
					//3,֪ͨ����������ˢ��
					if(mAdapter!=null){
						mAdapter.notifyDataSetChanged();
					}
				}
			});
			
			holder.tv_phone.setText(mBlackNumberList.get(position).phone);
			int mode = Integer.parseInt(mBlackNumberList.get(position).mode);
			switch (mode) {
			case 1:
				holder.tv_mode.setText("���ض���");
				break;
			case 2:
				holder.tv_mode.setText("���ص绰");
				break;
			case 3:
				holder.tv_mode.setText("��������");
				break;
			}
			return convertView;
		}
	}
	//����viewHolder�����
	static class ViewHolder{
		TextView tv_phone;
		TextView tv_mode;
		ImageView iv_delete;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacknumber);
		
		initUI();
		initData();
	}

	/**
	 * ��ʼ������
	 */
	private void initData() {
		//��ȡ���ݿ������е绰����
		new Thread(){
			public void run() {
				//1,��ȡ�������������ݿ�Ķ���
				mDao = BlackNumberDao.getInstance(getApplicationContext());
				//2,��ѯ��������
				mBlackNumberList = mDao.find(0);
				mCount = mDao.getCount();
				
				//3,ͨ����Ϣ���Ƹ�֪���߳̿���ȥʹ�ð������ݵļ���
				mHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	/**
	 * ��ʼ��UI
	 */
	private void initUI() {
		bt_add = (Button) findViewById(R.id.bt_add);
		lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
		
		bt_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
		
		//���������״̬
		lv_blacknumber.setOnScrollListener(new OnScrollListener() {
			//����������,״̬�����ı���÷���()
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				OnScrollListener.SCROLL_STATE_FLING	���ٹ���
//				OnScrollListener.SCROLL_STATE_IDLE	 ����״̬
//				OnScrollListener.SCROLL_STATE_TOUCH_SCROLL	���ִ�����ȥ����״̬
				
				if(mBlackNumberList!=null){
					//����һ:������ֹͣ״̬
					//������:���һ����Ŀ�ɼ�(���һ����Ŀ������ֵ>=�����������м��ϵ�����Ŀ����-1)
					if(scrollState == OnScrollListener.SCROLL_STATE_IDLE
							&& lv_blacknumber.getLastVisiblePosition()>=mBlackNumberList.size()-1
							&& !mIsLoad){
						/*mIsLoad��ֹ�ظ����صı���
						�����ǰ���ڼ���mIsLoad�ͻ�Ϊtrue,���μ�����Ϻ�,�ٽ�mIsLoad��Ϊfalse
						�����һ�μ�����Ҫȥ��ִ�е�ʱ��,���ж�����mIsLoad����,�Ƿ�Ϊfalse,���Ϊtrue,����Ҫ�ȴ���һ�μ������,����ֵ
						��Ϊfalse����ȥ����*/
						
						//�����Ŀ�������ڼ��ϴ�С��ʱ,�ſ���ȥ�������ظ���
						if(mCount>mBlackNumberList.size()){
							//������һҳ����
							mIsLoad = true;
							new Thread(){
								public void run() {
									//1,��ȡ�������������ݿ�Ķ���
									mDao = BlackNumberDao.getInstance(getApplicationContext());
									//2,��ѯ��������
									List<BlackNumberInfo> moreData = mDao.find(mBlackNumberList.size());
									//3,�����һҳ���ݵĹ���
									mBlackNumberList.addAll(moreData);
									//4,֪ͨ����������ˢ��
									mHandler.sendEmptyMessage(0);
									mIsLoad = false;
								}
							}.start();
						}
					}
				}
				
			}
			
			//���������е��÷���
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
	}
	
	protected void showDialog() {
		Builder builder = new AlertDialog.Builder(this);
		
		final AlertDialog dialog = builder.create();
		View view = View.inflate(getApplicationContext(), R.layout.dialog_add_blacknumber, null);
		dialog.setView(view, 0, 0, 0, 0);
		
		final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
		RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
		
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button)view.findViewById(R.id.bt_cancel);
		
		//������ѡ����Ŀ���л�����
		rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_sms:
					//���ض���
					mode = 1;
					break;
				case R.id.rb_phone:
					//���ص绰
					mode = 2;
					break;
				case R.id.rb_all:
					//��������
					mode = 3;
					break;
				}
			}
		});
		
		bt_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//1,��ȡ������еĵ绰����
				String phone = et_phone.getText().toString();
				if(!TextUtils.isEmpty(phone)){
					//2,���ݿ���뵱ǰ��������ص绰����
					mDao.insert(phone, mode+""); 
					//3,�����ݿ�ͼ��ϱ���ͬ��(1.���ݿ����������¶�һ��,2.�ֶ��򼯺������һ������(�������ݹ����Ķ���))
					BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
					blackNumberInfo.phone = phone;
					blackNumberInfo.mode = mode+"";
					//4,��������뵽���ϵ����
					mBlackNumberList.add(0, blackNumberInfo);
					//5,֪ͨ����������ˢ��(�����������е������иı���)
					if(mAdapter!=null){
						mAdapter.notifyDataSetChanged();
					}
					//6,���ضԻ���
					dialog.dismiss();
				}else{
					ToastUtil.show(getApplicationContext(), "���������غ���");
				}
			}
		});
		
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}
}
