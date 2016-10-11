package indi.cc.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.util.Log;


public class ServiceUtil {
	private static final String tag = "ServiceUtil";

	/**
	 * @param ctx	�����Ļ���
	 * @param serviceName �ж��Ƿ��������еķ���
	 * @return true ����	false û������
	 */
	public static boolean isRunning(Context ctx,String serviceName){
		//1,��ȡactivityMananger�����߶���,����ȥ��ȡ��ǰ�ֻ��������е����з���
		ActivityManager mAM = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//2,��ȡ�ֻ����������еķ��񼯺�(���ٸ�����)
		List<RunningServiceInfo> runningServices = mAM.getRunningServices(1000);
		//3,������ȡ�����еķ��񼯺�,�õ�ÿһ��������������,�ʹ��ݽ���������������ȶ�,���һ��,˵��������������
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			Log.i(tag, "runningServiceInfo.service.getClassName() = "+runningServiceInfo.service.getClassName());
			//4,��ȡÿһ���������з��������
			if(serviceName.equals(runningServiceInfo.service.getClassName())){
				return true;
			}
		}
		return false;
	}
}
