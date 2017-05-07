package indi.cc.mobilesafe.activity;

import indi.cc.mobilesafe.R;
import indi.cc.mobilesafe.db.dao.AppLockDao;
import indi.cc.mobilesafe.db.domain.AppInfo;
import indi.cc.mobilesafe.engine.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
/**
 * ����������
 * @author c
 *
 */
public class AppLockActivity extends Activity {
	private Button bt_unlock,bt_lock;
	private LinearLayout ll_unlock,ll_lock;
	private TextView tv_unlock,tv_lock;
	private ListView lv_unlock,lv_lock;
	private List<AppInfo> mAppInfoList;
	private List<AppInfo> mLockList;
	private List<AppInfo> mUnLockList;
	private AppLockDao mDao;
	private MyAdapter mLockAdapter;
	private MyAdapter mUnLockAdapter;
	private TranslateAnimation mTranslateAnimation;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			//6.���յ���Ϣ,����Ѽ�����δ����������������
			mLockAdapter = new MyAdapter(true);
			lv_lock.setAdapter(mLockAdapter);
			
			mUnLockAdapter = new MyAdapter(false);
			lv_unlock.setAdapter(mUnLockAdapter);
		};
	};

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);
		//��ʼ��UI
		initUI();
		//��ʼ������
		initData();
		//��ʼ������
		initAnimation();
	}
	
	/**
	 * ������ ��isLock �����Ѽ�����δ������������
	 * @author c
	 *
	 */
	class MyAdapter extends BaseAdapter{
		private boolean isLock;
		/**
		 * @param isLock	���������Ѽ�����δ����Ӧ�õı�ʾ	true�Ѽ�������������	falseδ��������������
		 */
		public MyAdapter(boolean isLock) {
			this.isLock = isLock;
		}
		@Override
		public int getCount() {
			if(isLock){
				tv_lock.setText("�Ѽ���Ӧ��:"+mLockList.size());
				return mLockList.size();
			}else{
				tv_unlock.setText("δ����Ӧ��:"+mUnLockList.size());
				return mUnLockList.size();
			}
		}

		@Override
		public AppInfo getItem(int position) {
			if(isLock){
				return mLockList.get(position);
			}else{
				return mUnLockList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null){
				convertView = View.inflate(getApplicationContext(), R.layout.listview_islock_item, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				holder.iv_lock = (ImageView) convertView.findViewById(R.id.iv_lock);
				
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			final AppInfo appInfo = getItem(position);
			//ר�����ö�����view
			final View animationView = convertView;
			
			holder.iv_icon.setBackgroundDrawable(appInfo.icon);
			holder.tv_name.setText(appInfo.name);
			//��������δ�������ò�ͬ����ͼƬ
			if(isLock){
				holder.iv_lock.setBackgroundResource(R.drawable.lock);
			}else{
				holder.iv_lock.setBackgroundResource(R.drawable.unlock);
			}
			holder.iv_lock.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//��Ӷ���Ч��,����Ĭ���Ƿ�������,����ִ�ж�����ͬʱ,�������µĴ���Ҳ��ִ��
					animationView.startAnimation(mTranslateAnimation);//500����
					//�Զ���ִ�й������¼�����,����������ִ����ɺ�,��ȥ�Ƴ������е�����,�������ݿ�,ˢ�½���
					mTranslateAnimation.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
							//������ʼ���ǵ��÷���
						}
						@Override
						public void onAnimationRepeat(Animation animation) {
							//�����ظ�ʱ����÷���
						}
						//����ִ�н�������÷���
						@Override
						public void onAnimationEnd(Animation animation) {
							if(isLock){
								//�Ѽ���------>δ��������
								//1.�Ѽ�������ɾ��һ��,δ�����������һ��,�������getItem������ȡ�Ķ���
								mLockList.remove(appInfo);
								mUnLockList.add(appInfo);
								//2.���Ѽ��������ݿ���ɾ��һ������
								mDao.delete(appInfo.packageName);
								//3.ˢ������������
								mLockAdapter.notifyDataSetChanged();
							}else{
								//δ����------>�Ѽ�������
								//1.�Ѽ����������һ��,δ���������Ƴ�һ��,�������getItem������ȡ�Ķ���
								mLockList.add(appInfo);
								mUnLockList.remove(appInfo);
								//2.���Ѽ��������ݿ��в���һ������
								mDao.insert(appInfo.packageName);
								//3.ˢ������������
								mUnLockAdapter.notifyDataSetChanged();
							}
						}
					});
				}
			});
			return convertView;
		}
	}
	
	static class ViewHolder{
		ImageView iv_icon;
		TextView tv_name;
		ImageView iv_lock;
	}

	/**
	 * ��ʼ��ƽ�ƶ����ķ���(ƽ�������һ����ȴ�С)
	 */
	private void initAnimation() {
		mTranslateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 1, 
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0);
		mTranslateAnimation.setDuration(500);
	}
	
	
	
	/**
	 * �����Ѽ�����δ����Ӧ�õļ���
	 */
	private void initData() {
		new Thread(){
			public void run() {
				//1.��ȡ�����ֻ��е�Ӧ��
				mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
				//2.�����Ѽ���Ӧ�ú�δ����Ӧ��
				mLockList = new ArrayList<AppInfo>();
				mUnLockList = new ArrayList<AppInfo>();
				
				//3.��ȡ���ݿ����Ѽ���Ӧ�ð����ĵĽ��
				mDao = AppLockDao.getInstance(getApplicationContext());
				List<String> lockPackageList = mDao.findAll();
				for (AppInfo appInfo : mAppInfoList) {
					//4,���ѭ������Ӧ�õİ���,�����ݿ���,��˵�����Ѽ���Ӧ��
					if(lockPackageList.contains(appInfo.packageName)){
						//��ӵ��Ѽ�����Ӧ�ü���
						mLockList.add(appInfo);
					}else{
						//��ӵ�δ������Ӧ�ü���
						mUnLockList.add(appInfo);
					}
				}
				//5.��֪���߳�,����ʹ��ά��������
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	/**
	 * ��ʼ��UI
	 */
	private void initUI() {
		bt_unlock = (Button) findViewById(R.id.bt_unlock);
		bt_lock = (Button) findViewById(R.id.bt_lock);
		
		ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
		ll_lock = (LinearLayout) findViewById(R.id.ll_lock);
		
		tv_unlock = (TextView) findViewById(R.id.tv_unlock);
		tv_lock = (TextView) findViewById(R.id.tv_lock);
		
		lv_unlock = (ListView) findViewById(R.id.lv_unlock);
		lv_lock = (ListView) findViewById(R.id.lv_lock);
		
		//��ť�¼�����
		bt_unlock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//1.�Ѽ����б�����,δ�����б���ʾ
				ll_lock.setVisibility(View.GONE);
				ll_unlock.setVisibility(View.VISIBLE);
				//ˢ��������
				mUnLockAdapter.notifyDataSetChanged();
				//2.δ���������ɫͼƬ,�Ѽ������ǳɫͼƬ
				bt_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
				bt_lock.setBackgroundResource(R.drawable.tab_right_default);
			}
		});
		
		bt_lock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//1.�Ѽ����б���ʾ,δ�����б�����
				ll_lock.setVisibility(View.VISIBLE);
				ll_unlock.setVisibility(View.GONE);
				//ˢ��������
				mLockAdapter.notifyDataSetChanged();
				//2.δ�������ǳɫͼƬ,�Ѽ��������ɫͼƬ
				bt_unlock.setBackgroundResource(R.drawable.tab_left_default);
				bt_lock.setBackgroundResource(R.drawable.tab_right_pressed);
			}
		});
	}
	
}
