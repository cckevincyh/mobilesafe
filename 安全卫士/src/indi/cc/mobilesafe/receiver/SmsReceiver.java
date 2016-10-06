package indi.cc.mobilesafe.receiver;

import indi.cc.mobilesafe.R;
import indi.cc.mobilesafe.service.LocationService;
import indi.cc.mobilesafe.utils.ConstantValue;
import indi.cc.mobilesafe.utils.SpUtil;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

	private String tag = "SmsReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//1,�ж��Ƿ����˷�������
		boolean open_security = SpUtil.getBoolean(context,ConstantValue.OPEN_SECURITY, false);
		if(open_security){
			//2.��ȡ��������
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			//3.ѭ���������Ź���
			for (Object object : objects) {
				//4.��ȡ���Ŷ���
				SmsMessage sms = SmsMessage.createFromPdu((byte[])object);
				//5.��ȡ���Ŷ���Ļ�����Ϣ
				String originatingAddress = sms.getOriginatingAddress();
				String messageBody = sms.getMessageBody();
				//6,�ж��Ƿ�����������ֵĹؼ���
				if(messageBody.contains("#*alarm*#")){
					//7,��������(׼������,MediaPlayer)
					MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
					mediaPlayer.setLooping(true);
					mediaPlayer.start();
					//��ֹ�����㲥....
				
				}
				if(messageBody.contains("#*location*#")){
					//8,������ȡλ�÷���
					context.startService(new Intent(context,LocationService.class));
				}
				if(messageBody.contains("#*lockscreen*#")){
					Log.i(tag, "��Ļ����");
				}
				
				if(messageBody.contains("#*wipedata*#")){
					Log.i(tag, "�������");
				}
				
			
			}
		}
	}

}
