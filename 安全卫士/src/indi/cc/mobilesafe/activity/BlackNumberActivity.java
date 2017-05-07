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




//1,复用convertView
//2,对findViewById次数的优化,使用ViewHolder
//3,将ViewHolder定义成静态,不会去创建多个对象
//4,listView如果有多个条目的时候,可以做分页算法,每一次加载20条,逆序返回

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
			//4,告知listView可以去设置数据适配器
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
			
			//1,复用convertView
			
			//复用viewHolder步骤一
			ViewHolder holder = null;
			if(convertView == null){
				convertView = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);
				//2,减少findViewById()次数
				//复用viewHolder步骤三
				holder = new ViewHolder();
				//复用viewHolder步骤四
				holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
				holder.tv_mode = (TextView)convertView.findViewById(R.id.tv_mode);
				holder.iv_delete = (ImageView)convertView.findViewById(R.id.iv_delete);
				//复用viewHolder步骤五
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//1,数据库删除
					mDao.delete(mBlackNumberList.get(position).phone);
					//2,集合中的删除
					mBlackNumberList.remove(position);
					//3,通知数据适配器刷新
					if(mAdapter!=null){
						mAdapter.notifyDataSetChanged();
					}
				}
			});
			
			holder.tv_phone.setText(mBlackNumberList.get(position).phone);
			int mode = Integer.parseInt(mBlackNumberList.get(position).mode);
			switch (mode) {
			case 1:
				holder.tv_mode.setText("拦截短信");
				break;
			case 2:
				holder.tv_mode.setText("拦截电话");
				break;
			case 3:
				holder.tv_mode.setText("拦截所有");
				break;
			}
			return convertView;
		}
	}
	//复用viewHolder步骤二
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
	 * 初始化数据
	 */
	private void initData() {
		//获取数据库中所有电话号码
		new Thread(){
			public void run() {
				//1,获取操作黑名单数据库的对象
				mDao = BlackNumberDao.getInstance(getApplicationContext());
				//2,查询部分数据
				mBlackNumberList = mDao.find(0);
				mCount = mDao.getCount();
				
				//3,通过消息机制告知主线程可以去使用包含数据的集合
				mHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	/**
	 * 初始化UI
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
		
		//监听其滚动状态
		lv_blacknumber.setOnScrollListener(new OnScrollListener() {
			//滚动过程中,状态发生改变调用方法()
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				OnScrollListener.SCROLL_STATE_FLING	飞速滚动
//				OnScrollListener.SCROLL_STATE_IDLE	 空闲状态
//				OnScrollListener.SCROLL_STATE_TOUCH_SCROLL	拿手触摸着去滚动状态
				
				if(mBlackNumberList!=null){
					//条件一:滚动到停止状态
					//条件二:最后一个条目可见(最后一个条目的索引值>=数据适配器中集合的总条目个数-1)
					if(scrollState == OnScrollListener.SCROLL_STATE_IDLE
							&& lv_blacknumber.getLastVisiblePosition()>=mBlackNumberList.size()-1
							&& !mIsLoad){
						/*mIsLoad防止重复加载的变量
						如果当前正在加载mIsLoad就会为true,本次加载完毕后,再将mIsLoad改为false
						如果下一次加载需要去做执行的时候,会判断上诉mIsLoad变量,是否为false,如果为true,就需要等待上一次加载完成,将其值
						改为false后再去加载*/
						
						//如果条目总数大于集合大小的时,才可以去继续加载更多
						if(mCount>mBlackNumberList.size()){
							//加载下一页数据
							mIsLoad = true;
							new Thread(){
								public void run() {
									//1,获取操作黑名单数据库的对象
									mDao = BlackNumberDao.getInstance(getApplicationContext());
									//2,查询部分数据
									List<BlackNumberInfo> moreData = mDao.find(mBlackNumberList.size());
									//3,添加下一页数据的过程
									mBlackNumberList.addAll(moreData);
									//4,通知数据适配器刷新
									mHandler.sendEmptyMessage(0);
									mIsLoad = false;
								}
							}.start();
						}
					}
				}
				
			}
			
			//滚动过程中调用方法
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
		
		//监听其选中条目的切换过程
		rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_sms:
					//拦截短信
					mode = 1;
					break;
				case R.id.rb_phone:
					//拦截电话
					mode = 2;
					break;
				case R.id.rb_all:
					//拦截所有
					mode = 3;
					break;
				}
			}
		});
		
		bt_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//1,获取输入框中的电话号码
				String phone = et_phone.getText().toString();
				if(!TextUtils.isEmpty(phone)){
					//2,数据库插入当前输入的拦截电话号码
					mDao.insert(phone, mode+""); 
					//3,让数据库和集合保持同步(1.数据库中数据重新读一遍,2.手动向集合中添加一个对象(插入数据构建的对象))
					BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
					blackNumberInfo.phone = phone;
					blackNumberInfo.mode = mode+"";
					//4,将对象插入到集合的最顶部
					mBlackNumberList.add(0, blackNumberInfo);
					//5,通知数据适配器刷新(数据适配器中的数据有改变了)
					if(mAdapter!=null){
						mAdapter.notifyDataSetChanged();
					}
					//6,隐藏对话框
					dialog.dismiss();
				}else{
					ToastUtil.show(getApplicationContext(), "请输入拦截号码");
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
