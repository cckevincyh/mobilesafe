package indi.cc.mobilesafe.activity;

import indi.cc.mobilesafe.R;
import indi.cc.mobilesafe.utils.ConstantValue;
import indi.cc.mobilesafe.utils.SpUtil;
import indi.cc.mobilesafe.view.SettingItemView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		initUpdate();
	}
	
	
	/**
	 * 版本更新开关
	 */
	private void initUpdate() {
		final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);
		//获取已有的开关状态,用作显示
		boolean open_update = SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false);
		//是否选中,根据上一次存储的结果去做决定
		siv_update.setCheck(open_update);
		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//如果之前是选中的,点击过后,变成未选中
				//如果之前是未选中的,点击过后,变成选中
				
				//获取之前的选中状态
				boolean isCheck = siv_update.isCheck();
				//将原有状态取反,等同上诉的两部操作
				siv_update.setCheck(!isCheck);
				//将取反后的状态存储到相应sp中
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE,!isCheck);
			}
		});
	}

}
