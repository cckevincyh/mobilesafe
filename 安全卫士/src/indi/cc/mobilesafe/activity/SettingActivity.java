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
	 * �汾���¿���
	 */
	private void initUpdate() {
		final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);
		//��ȡ���еĿ���״̬,������ʾ
		boolean open_update = SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false);
		//�Ƿ�ѡ��,������һ�δ洢�Ľ��ȥ������
		siv_update.setCheck(open_update);
		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//���֮ǰ��ѡ�е�,�������,���δѡ��
				//���֮ǰ��δѡ�е�,�������,���ѡ��
				
				//��ȡ֮ǰ��ѡ��״̬
				boolean isCheck = siv_update.isCheck();
				//��ԭ��״̬ȡ��,��ͬ���ߵ���������
				siv_update.setCheck(!isCheck);
				//��ȡ�����״̬�洢����Ӧsp��
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE,!isCheck);
			}
		});
	}

}
