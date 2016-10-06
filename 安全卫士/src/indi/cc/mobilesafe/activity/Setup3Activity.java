package indi.cc.mobilesafe.activity;

import indi.cc.mobilesafe.R;
import indi.cc.mobilesafe.utils.ConstantValue;
import indi.cc.mobilesafe.utils.SpUtil;
import indi.cc.mobilesafe.utils.ToastUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Setup3Activity extends BaseSetupActivity {
	private EditText et_phone_number;
	private Button bt_select_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		//��ʼ��UI
		initUI();
	}

	/**
	 * ��ʼ��UI
	 */
	private void initUI() {
		// ��ʾ�绰����������
		et_phone_number = (EditText)findViewById(R.id.et_phone_number);
		//��ȡ��ϵ�˵绰������Թ���
		String phone = SpUtil.getString(this, ConstantValue.CONTACT_PHONE, "");
		et_phone_number.setText(phone);
		//���ѡ����ϵ�˵ĶԻ���
		bt_select_number = (Button) findViewById(R.id.bt_select_number);
		bt_select_number.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);
				startActivityForResult(intent, 0);//��ѡ����ϵ�˷���
			}
		});

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// ����ѡ�����ϵ��
		if(data!=null){
			//��Ϊ�գ���˵��ѡ������ϵ��
			//1.���ص�ǰ�����ʱ�򣬽��ܽ���ķ���
			String phone = data.getStringExtra("phone");
			//2.�������ַ�����(�л���ת���ɿ��ַ�)
			phone = phone.replace("-", "").replace(" ", "").trim();
			et_phone_number.setText(phone);
			//3.�洢��ϵ����sp��
			SpUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	@Override
	protected void showNextPage() {

		//�����ť�Ժ�,��Ҫ��ȡ������е���ϵ��,������һҳ����
		String phone = et_phone_number.getText().toString();
		
		//��sp�洢�������ϵ���Ժ�ſ�����ת����һ������
//		String contact_phone = SpUtil.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
		if(!TextUtils.isEmpty(phone)){
			Intent intent = new Intent(getApplicationContext(), Setup4Activity.class);
			startActivity(intent);
			
			finish();
			
			//�������������绰����,����Ҫȥ����
			SpUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);
			
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		}else{
			ToastUtil.show(this,"������绰����");
		}
	
	}
	@Override
	protected void showPrePage() {

		Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
		startActivity(intent);
		
		finish();
		
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	
	}
}
