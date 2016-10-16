package indi.cc.mobilesafe.activity;

import java.io.File;

import indi.cc.mobilesafe.R;
import indi.cc.mobilesafe.engine.SmsBackUp;
import indi.cc.mobilesafe.engine.SmsBackUp.CallBack;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AToolActivity extends Activity {
	private TextView tv_query_phone_address,tv_sms_backup;
	private ProgressBar pb_bar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atool);
		
		//�绰�����ز�ѯ����
		initPhoneAddress();
		//���ű��ݷ���
		initSmsBackUp();
	}

	/**
	 * ���ű��ݷ���
	 */
	private void initSmsBackUp() {
		tv_sms_backup = (TextView) findViewById(R.id.tv_sms_backup);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		tv_sms_backup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showSmsBackUpDialog();
			}

		});
		
	}

	private void initPhoneAddress() {
		tv_query_phone_address = (TextView) findViewById(R.id.tv_query_phone_address);
		tv_query_phone_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), QueryAddressActivity.class));
			}
		});
	}
	
	protected void showSmsBackUpDialog() {
		//1,����һ�����������ĶԻ���
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setIcon(R.drawable.ic_launcher);
		progressDialog.setTitle("���ű���");
		//2,ָ������������ʽΪˮƽ
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		//3,չʾ������
		progressDialog.show();
		//4,ֱ�ӵ��ñ��ݶ��ŷ�������
		new Thread(){
			@Override
			public void run() {
				String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"sms.xml";
				SmsBackUp.backup(getApplicationContext(), path, new CallBack() {
					@Override
					public void setProgress(int index) {
						//�ɿ������Լ�����,ʹ�öԻ����ǽ�����
						progressDialog.setProgress(index);
						pb_bar.setProgress(index);
					}
					
					@Override
					public void setMax(int max) {
						//�ɿ������Լ�����,ʹ�öԻ����ǽ�����
						progressDialog.setMax(max);
						pb_bar.setMax(max);
					}
				});
				
				progressDialog.dismiss();
			}
		}.start();
	}

	
}
