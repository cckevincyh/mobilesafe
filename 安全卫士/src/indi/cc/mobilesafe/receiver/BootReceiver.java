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
		Log.i(tag, "重启手机成功了,并且监听到了相应的广播......");
		//1,获取开机后手机的sim卡的序列号
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String simSerialNumber = tm.getSimSerialNumber()+"xxx";	//故意搞成不一样
		//2,sp中存储的序列卡号
		String sim_number = SpUtil.getString(context,ConstantValue.SIM_NUMBER, "");
		//3,比对不一致
		if(!simSerialNumber.equals(sim_number)){
			//4,发送短信给选中联系人号码
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage("5556", null, "sim change!!!", null, null);
		}
	}

}
