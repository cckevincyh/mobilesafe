package indi.cc.mobilesafe.activity;

import indi.cc.mobilesafe.R;
import indi.cc.mobilesafe.engine.AddressDao;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class QueryAddressActivity extends Activity {
	private EditText et_phone;
	private Button bt_query;
	private TextView tv_query_result;
	private String mAddress;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			//4,�ؼ�ʹ�ò�ѯ���
			tv_query_result.setText(mAddress);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_address);
		
		initUI();
	}

	private void initUI() {
		et_phone = (EditText) findViewById(R.id.et_phone);
		bt_query = (Button) findViewById(R.id.bt_query);
		tv_query_result = (TextView) findViewById(R.id.tv_query_result);
		
		//1,���ѯ����,ע�ᰴť�ĵ���¼�
		bt_query.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone = et_phone.getText().toString();
				if(!TextUtils.isEmpty(phone)){
					//2,��ѯ�Ǻ�ʱ����,�������߳�
					query(phone);
				}else{
					//����
					Animation shake = AnimationUtils.loadAnimation(
							getApplicationContext(), R.anim.shake);
					
					//interpolator�岹��,��ѧ����
					//�Զ���岹��
//					shake.setInterpolator(new Interpolator() {
//						//y = 2x+1
//						@Override
//						public float getInterpolation(float arg0) {
//							return 0;
//						}
//					});
					
//					Interpolator
//					CycleInterpolator
					//����򶶶�
					et_phone.startAnimation(shake);
					//�ֻ���Ч��(vibrator ��)
					Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					//�𶯺���ֵ
					vibrator.vibrate(2000);
					//������(�𶯹���(����ʱ��,��ʱ��,����ʱ��,��ʱ��.......),�ظ�����)
					vibrator.vibrate(new long[]{2000,5000,2000,5000}, -1);
				}
				
			}
		});
		
		//5,ʵʱ��ѯ(����������ı��仯)
		et_phone.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String phone = et_phone.getText().toString();
				query(phone);
			}
		});
	}

	/**
	 * ��ʱ����
	 * ��ȡ�绰���������
	 * @param phone	��ѯ�绰����
	 */
	protected void query(final String phone) {
		new Thread(){
			public void run() {
				mAddress = AddressDao.getAddress(phone);
				//3,��Ϣ����,��֪���̲߳�ѯ����,����ȥʹ�ò�ѯ���
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}
}
