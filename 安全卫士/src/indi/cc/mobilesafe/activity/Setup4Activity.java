package indi.cc.mobilesafe.activity;

import indi.cc.mobilesafe.R;
import indi.cc.mobilesafe.utils.ConstantValue;
import indi.cc.mobilesafe.utils.SpUtil;
import indi.cc.mobilesafe.utils.ToastUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends BaseSetupActivity {
	private CheckBox cb_box;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		//��ʼ��UI
		initUI();
	}

	
	/**
	 * ��ʼ��UI
	 */
	private void initUI() {
		cb_box = (CheckBox) findViewById(R.id.cb_box);
		//1.�Ƿ�ѡ��״̬�Ļ���
		boolean open_security = SpUtil.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
		//2.����״̬���޸�checkbox������������ʾ
		if(open_security){
			cb_box.setText("��ȫ�����ѿ���");
		}else{
			cb_box.setText("��ȫ�����ѹر�");
		}
		//3,���������,����ѡ��״̬�����ı����,
		cb_box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//4,isChecked������״̬,�洢�����״̬
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_SECURITY, isChecked);
				//5,���ݿ����ر�״̬,ȥ�޸���ʾ������
				if(isChecked){
					cb_box.setText("��ȫ�����ѿ���");
				}else{
					cb_box.setText("��ȫ�����ѹر�");
				}
			}
		});
		
	}
	
	@Override
	protected void showNextPage() {
		boolean open_security = SpUtil.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
		if(open_security){
			Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
			startActivity(intent);
			
			finish();
			SpUtil.putBoolean(this, ConstantValue.SETUP_OVER, true);
			
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		}else{
			ToastUtil.show(getApplicationContext(), "�뿪����������");
		}
	}

	@Override
	protected void showPrePage() {
		Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
		startActivity(intent);
		
		finish();
		
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}
}
