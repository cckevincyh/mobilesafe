package indi.cc.mobilesafe.activity;

import indi.cc.mobilesafe.R;
import indi.cc.mobilesafe.utils.ConstantValue;
import indi.cc.mobilesafe.utils.SpUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SetupOverActivity extends Activity {
	private TextView tv_phone;
	private TextView tv_reset_setup;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//��ȡ֮ǰ�Ƿ��������
		boolean setup_over = SpUtil.getBoolean(this, ConstantValue.SETUP_OVER, false);
		if(setup_over){
			//��������ɹ�,�����ĸ����������������----->ͣ����������ɹ����б����
			setContentView(R.layout.activity_setup_over);
			//��ʼ��UI
			initUI();
		}else{
			//��������ɹ�,�ĸ���������û���������----->��ת�����������1��
			Intent intent = new Intent(this, Setup1Activity.class);
			startActivity(intent);
			
			//������һ���µĽ����Ժ�,�رչ����б����
			finish();
		}
	}

	/**
	 * ��ʼ��UI
	 */
	private void initUI() {
		// TODO Auto-generated method stub
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		//������ϵ�˺���
		String phone = SpUtil.getString(this,ConstantValue.CONTACT_PHONE, "");
		tv_phone.setText(phone);
		
		//����������Ŀ�����
		tv_reset_setup = (TextView) findViewById(R.id.tv_reset_setup);
		//ע��һ���¼����������TextView���ܴ���ͼƬѡ��������ͼƬ�л�
		tv_reset_setup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
				startActivity(intent);
				
				finish();
			}
		});
	}	
}
