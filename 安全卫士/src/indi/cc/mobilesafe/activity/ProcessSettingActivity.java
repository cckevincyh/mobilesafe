package indi.cc.mobilesafe.activity;

import indi.cc.mobilesafe.R;
import indi.cc.mobilesafe.service.LockScreenService;
import indi.cc.mobilesafe.utils.ConstantValue;
import indi.cc.mobilesafe.utils.ServiceUtil;
import indi.cc.mobilesafe.utils.SpUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ProcessSettingActivity extends Activity {
	
	private CheckBox cb_show_system,cb_lock_clear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_setting);
		
		initSystemShow();
		initLockScreenClear();
	}

	/**
	 * ��������
	 */
	private void initLockScreenClear() {
		cb_lock_clear = (CheckBox) findViewById(R.id.cb_lock_clear);
		//����������������Ƿ���ȥ,�����Ƿ�ѡ��ѡ��
		boolean isRunning = ServiceUtil.isRunning(this, "indi.cc.mobilesafe.service.LockScreenService");
		if(isRunning){
			cb_lock_clear.setText("���������ѿ���");
		}else{
			cb_lock_clear.setText("���������ѹر�");
		}
		//cb_lock_clearѡ��״̬ά��
		cb_lock_clear.setChecked(isRunning);
		
		//��ѡ��״̬���м���
		cb_lock_clear.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//isChecked����Ϊ�Ƿ�ѡ�е�״̬
				if(isChecked){
					cb_lock_clear.setText("���������ѿ���");
					//��������
					startService(new Intent(getApplicationContext(), LockScreenService.class));
				}else{
					cb_lock_clear.setText("���������ѹر�");
					//�رշ���
					stopService(new Intent(getApplicationContext(), LockScreenService.class));
				}
			}
		});
	
	}

	/**
	 * ��ʼ����ʾ(�Ƿ���ʾϵͳ����)
	 */
	private void initSystemShow() {	
		cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
		//��֮ǰ�洢����״̬���л���
		boolean showSystem = SpUtil.getBoolean(this, ConstantValue.SHOW_SYSTEM, false);
		//��ѡ�����ʾ״̬
		cb_show_system.setChecked(showSystem);
		
		if(showSystem){
			cb_show_system.setText("��ʾϵͳ����");
		}else{
			cb_show_system.setText("����ϵͳ����");
		}
		
		//��ѡ��״̬���м���
		cb_show_system.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//isChecked����Ϊ�Ƿ�ѡ�е�״̬
				if(isChecked){
					cb_show_system.setText("��ʾϵͳ����");
				}else{
					cb_show_system.setText("����ϵͳ����");
				}
				//�洢�Ƿ���ʾϵͳ����
				SpUtil.putBoolean(ProcessSettingActivity.this,ConstantValue.SHOW_SYSTEM, isChecked);
			}
		});
	}
}
