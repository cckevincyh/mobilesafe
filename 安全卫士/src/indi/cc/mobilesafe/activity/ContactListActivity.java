package indi.cc.mobilesafe.activity;

import indi.cc.mobilesafe.R;
import indi.cc.mobilesafe.activity.HomeActivity.MyAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
/**
 * ��ϵ��ѡ�����
 * @author c
 *
 */
public class ContactListActivity extends Activity {
	protected static final String tag = "ContactListActivity";
	private ListView lv_contact;
	private List<HashMap<String,String>> contactList = new ArrayList<HashMap<String,String>>();
	private MyAdapter mAdapter;
	
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			//8,�������������
			
			mAdapter = new MyAdapter();
			lv_contact.setAdapter(mAdapter);
		};
	};
	
	
	
	class MyAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return contactList.size();
		}

		@Override
		public HashMap<String, String> getItem(int position) {
			return contactList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.listview_contact_item, null);
			
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
			
			tv_name.setText(getItem(position).get("name"));
			tv_phone.setText(getItem(position).get("phone"));
			
			return view;
		}

		
		
		
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);
		//��ʼ��UI
		initUI();
		//��ʼ������
		initData();
	}
	
	/**
	 * ��ȡϵͳ��ϵ�����ݷ���
	 */
	private void initData() {
		//��Ϊ��ȡϵͳ��ϵ��,������һ����ʱ����,���õ����߳��д���
		new Thread(){

			public void run() {
				//1.��ȡ���ݽ���������
				ContentResolver contentResolver = getContentResolver();
				//2.����ѯϵͳ��ϵ�����ݿ�����(��ȡ��ϵ��Ȩ��)
				Cursor cursor = contentResolver.query(
						Uri.parse("content://com.android.contacts/raw_contacts"),
						new String[]{"contact_id"}, 
						null, null, null);
				contactList.clear();	//ʹ��ǰ���������
				//3.ѭ���αֱ꣬��û������Ϊֹ
				while(cursor.moveToNext()){
					String id = cursor.getString(0);
				//	Log.i(tag, "id = "+id);
					//4,�����û�Ψһ��idֵ,��ѯdata���mimetype�����ɵ���ͼ,��ȡdata�Լ�mimetype�ֶ�
					Cursor indexCursor = contentResolver.query(
							Uri.parse("content://com.android.contacts/data"), 
							new String[]{"data1","mimetype"}, 
							"raw_contact_id = ?", new String[]{id}, null);
					//5,ѭ����ȡÿһ����ϵ�˵ĵ绰�����Լ�����,��������
					HashMap<String, String> hashMap = new HashMap<String, String>();
					while(indexCursor.moveToNext()){
						String data = indexCursor.getString(0);	
						String type = indexCursor.getString(1);
						
						//6,��������ȥ��hashMap�������
						if(type.equals("vnd.android.cursor.item/phone_v2")){
							//���ݷǿ��ж�
							if(!TextUtils.isEmpty(data)){
								hashMap.put("phone", data);
							}
						}else if(type.equals("vnd.android.cursor.item/name")){
							if(!TextUtils.isEmpty(data)){
								hashMap.put("name", data);
							}
						}
					}
					indexCursor.close();
					contactList.add(hashMap);
				}
				cursor.close();
				//7,��Ϣ����,����һ���յ���Ϣ,��֪���߳̿���ȥʹ�����߳��Ѿ����õ����ݼ���
				mHandler.sendEmptyMessage(0);
			};
		}.start();
		
	}
	
	/**
	 * ��ʼ��UI
	 */
	private void initUI() {
		lv_contact = (ListView) findViewById(R.id.lv_contact);
		lv_contact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				// 1.��ȡ������Ŀ������ָ�򼯺��еĶ���
				if(mAdapter!=null){
					HashMap<String, String> hashMap = (HashMap<String, String>) mAdapter.getItem(position);
					//2,��ȡ��ǰ��Ŀָ�򼯺϶�Ӧ�ĵ绰����
					String phone = hashMap.get("phone");
					//3,�˵绰������Ҫ����������������ʹ��
					
					//4,�ڽ����˽���ص�ǰһ�����������ʱ��,��Ҫ�����ݷ��ع�ȥ
					Intent intent = new Intent();
					intent.putExtra("phone", phone);
					setResult(0, intent);
					
					finish();
				}
				
			}
		});
		
	}
}
