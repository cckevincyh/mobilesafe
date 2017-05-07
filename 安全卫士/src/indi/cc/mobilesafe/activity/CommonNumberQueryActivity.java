package indi.cc.mobilesafe.activity;

import indi.cc.mobilesafe.R;
import indi.cc.mobilesafe.engine.CommonnumDao;
import indi.cc.mobilesafe.engine.CommonnumDao.Child;
import indi.cc.mobilesafe.engine.CommonnumDao.Group;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;


/**
 * 常用号码查询界面
 * @author c
 *
 */
public class CommonNumberQueryActivity extends Activity {
	private ExpandableListView elv_common_number;
	private List<Group> mGroup;
	private MyAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number);
		initUI();
		initData();
	}
	
	/**
	 * 给可扩展ListView准备数据,并且填充
	 */
	private void initData() {
		CommonnumDao commonnumDao = new CommonnumDao();
		mGroup = commonnumDao.getGroup();
		
		mAdapter = new MyAdapter();
		elv_common_number.setAdapter(mAdapter);
		//给可扩展listview注册点击事件
		elv_common_number.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				//拨打电话
				startCall(mAdapter.getChild(groupPosition, childPosition).number);
				return false;
			}
		});
	}
	/**
	 * 点击列表后拨打此电话
	 * @param number
	 */
	protected void startCall(String number) {
		//开启系统的打电话界面
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:"+number));
		startActivity(intent);
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);
	}
	
	class MyAdapter extends BaseExpandableListAdapter{
		@Override
		public Child getChild(int groupPosition, int childPosition) {
			return mGroup.get(groupPosition).childList.get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.elv_child_item, null);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_number = (TextView) view.findViewById(R.id.tv_number);
			
			tv_name.setText(getChild(groupPosition, childPosition).name);
			tv_number.setText(getChild(groupPosition, childPosition).number);
			
			return view;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mGroup.get(groupPosition).childList.size();
		}

		@Override
		public Group getGroup(int groupPosition) {
			return mGroup.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return mGroup.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		
		//dip = dp
		//dpi == ppi	像素密度(每一个英寸上分布的像素点的个数)
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = new TextView(getApplicationContext());
			textView.setText("			"+getGroup(groupPosition).name);
			textView.setTextColor(Color.RED);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			return textView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		//孩子节点是否响应事件
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}
}
