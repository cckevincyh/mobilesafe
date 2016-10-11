package indi.cc.mobilesafe.activity;

import indi.cc.mobilesafe.R;
import indi.cc.mobilesafe.utils.ConstantValue;
import indi.cc.mobilesafe.utils.SpUtil;
import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class ToastLocationActivity extends Activity{
	private ImageView iv_drag;
	private Button bt_top,bt_bottom;
	private WindowManager mWM;
	private int mScreenHeight;
	private int mScreenWidth;
	
	private long[] mHits = new long[2];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toast_location);
		
		initUI();
	}

	private void initUI() {
		//����ק˫�����е�ͼƬ�ؼ�
		iv_drag = (ImageView) findViewById(R.id.iv_drag);
		bt_top = (Button) findViewById(R.id.bt_top);
		bt_bottom = (Button) findViewById(R.id.bt_bottom);
		
		mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
		mScreenHeight = mWM.getDefaultDisplay().getHeight();
		mScreenWidth = mWM.getDefaultDisplay().getWidth();
		
		int locationX = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
		int locationY = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, 0);
		
		//���Ͻ�����������iv_drag��
		//iv_drag����Բ�����,����������λ�õĹ�����Ҫ����Բ����ṩ
		
		//ָ����߶�ΪWRAP_CONTENT
		LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, 
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		//�����Ͻǵ�����������iv_drag��Ӧ���������
		layoutParams.leftMargin = locationX;
		layoutParams.topMargin = locationY;
		//�����Ϲ���������iv_drag��
		iv_drag.setLayoutParams(layoutParams);
		
		if(locationY>mScreenHeight/2){
			bt_bottom.setVisibility(View.INVISIBLE);
			bt_top.setVisibility(View.VISIBLE);
		}else{
			bt_bottom.setVisibility(View.VISIBLE);
			bt_top.setVisibility(View.INVISIBLE);
		}
		
		//˫���¼�
		iv_drag.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
		        mHits[mHits.length-1] = SystemClock.uptimeMillis();
		        if(mHits[mHits.length-1]-mHits[0]<500){
		        	//����˫���¼���,���ô���
		        	int left = mScreenWidth/2 - iv_drag.getWidth()/2;
		        	int top = mScreenHeight/2 - iv_drag.getHeight()/2;
		        	int right = mScreenWidth/2+iv_drag.getWidth()/2;
		        	int bottom = mScreenHeight/2+iv_drag.getHeight()/2;
		        	
		        	//�ؼ������Ϲ�����ʾ
		        	iv_drag.layout(left, top, right, bottom);
		        	
		        	//�洢����λ��
		        	SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X, iv_drag.getLeft());
					SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, iv_drag.getTop());
		        }
			}
		});
		
		
		//����ĳһ���ؼ�����ק����(����(1),�ƶ�(���),̧��(1))
		iv_drag.setOnTouchListener(new OnTouchListener() {
			private int startX;
			private int startY;
			//�Բ�ͬ���¼�����ͬ���߼�����
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();
					
					int disX = moveX-startX;
					int disY = moveY-startY;
					
					//1,��ǰ�ؼ�������Ļ��(��,��)�ǵ�λ��
					int left = iv_drag.getLeft()+disX;//�������
					int top = iv_drag.getTop()+disY;//��������
					int right = iv_drag.getRight()+disX;//�Ҳ�����
					int bottom = iv_drag.getBottom()+disY;//�ײ�����
					
					//�ݴ���(iv_drag������ק���ֻ���Ļ)
					//���Ե���ܳ�����Ļ
					if(left<0){
						return true;
					}
					
					//�ұ߱�Ե���ܳ�����Ļ
					if(right>mScreenWidth){
						return true;
					}
					
					//�ϱ�Ե���ܳ�����Ļ����ʵ����
					if(top<0){
						return true;
					}
					
					//�±�Ե(��Ļ�ĸ߶�-22 = �ױ�Ե��ʾ���ֵ)
					if(bottom>mScreenHeight - 22){
						return true;
					}
					
					if(top>mScreenHeight/2){
						bt_bottom.setVisibility(View.INVISIBLE);
						bt_top.setVisibility(View.VISIBLE);
					}else{
						bt_bottom.setVisibility(View.VISIBLE);
						bt_top.setVisibility(View.INVISIBLE);
					}
					
					//2,��֪�ƶ��Ŀؼ�,���������������ȥ��չʾ
					iv_drag.layout(left, top, right, bottom);
					
					//3,����һ����ʵ����
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					
					break;
				case MotionEvent.ACTION_UP:
					//4,�洢�ƶ�����λ��
					SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X, iv_drag.getLeft());
					SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, iv_drag.getTop());
					break;
				}
				//�ڵ�ǰ������·���false����Ӧ�¼�,����true�Ż���Ӧ�¼�
				//return true;
				//��Ҫ��Ӧ����¼�,��Ҫ��Ӧ��ק����,��˷���ֵ�����Ҫ�޸�Ϊfalse
				return false;
			}
		});
	}
}

