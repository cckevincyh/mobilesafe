package indi.cc.mobilesafe.activity;

import indi.cc.mobilesafe.R;
import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;

public class TrafficActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic);
		
		//��ȡ�ֻ���������
		//��ȡ����(R �ֻ�(2G,3G,4G)��������)
		long mobileRxBytes = TrafficStats.getMobileRxBytes();
		//��ȡ�ֻ���������(�ϴ�+����)
		//T total(�ֻ�(2G,3G,4G)������(�ϴ�+����))
		long mobileTxBytes = TrafficStats.getMobileTxBytes();
		//total(���������ܺ�(�ֻ�+wifi))
		long totalRxBytes = TrafficStats.getTotalRxBytes();
		//(������(�ֻ�+wifi),(�ϴ�+����))
		long totalTxBytes = TrafficStats.getTotalTxBytes();
		
		//���岻��
		//������ȡģ��(���Ͷ���),��Ӫ��(��ͨ,�ƶ�....),(��������)�������ӿ�,���
		//����ע��
		
		
		
	}
}
