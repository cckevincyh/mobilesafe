package indi.cc.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

public class LocationService extends Service {
	@Override
	public void onCreate() {
		super.onCreate();
		//��ȡ�ֻ��ľ�γ������
		//1,��ȡλ�ù����߶���
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		//2,�����ŵķ�ʽ��ȡ��γ������()
		Criteria criteria = new Criteria();
		//������
		criteria.setCostAllowed(true);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);//ָ����ȡ��γ�ȵľ�ȷ��
		String bestProvider = lm.getBestProvider(criteria, true);
		//3,��һ��ʱ����,�ƶ�һ��������ȡ��γ������
		MyLocationListener myLocationListener = new MyLocationListener();
		lm.requestLocationUpdates(bestProvider, 0, 0, myLocationListener);
	}
	
	class MyLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			//����
			double longitude = location.getLongitude();
			//γ��
			double latitude = location.getLatitude();
			
			//4,���Ͷ���(���Ȩ��)
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage("5556", null, "longitude = "+longitude+",latitude = "+latitude, null, null);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
