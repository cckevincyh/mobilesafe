package indi.cc.mobilesafe.receiver;

import indi.cc.mobilesafe.utils.ConstantValue;
import indi.cc.mobilesafe.utils.SpUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	private static final String tag = "BootReceiver";
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i(tag, "�����ֻ��ɹ���,���Ҽ���������Ӧ�Ĺ㲥......");
		//1,��ȡ�������ֻ���sim�������к�
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String simSerialNumber = tm.getSimSerialNumber()+"xxx";	//�����ɲ�һ��
		//2,sp�д洢�����п���
		String sim_number = SpUtil.getString(context,ConstantValue.SIM_NUMBER, "");
		//3,�ȶԲ�һ��
		if(!simSerialNumber.equals(sim_number)){
			//4,���Ͷ��Ÿ�ѡ����ϵ�˺���
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage("5556", null, "sim change!!!", null, null);
		}
	}

}
