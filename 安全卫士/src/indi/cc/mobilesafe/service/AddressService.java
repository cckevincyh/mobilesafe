package indi.cc.mobilesafe.service;

import indi.cc.mobilesafe.R;
import indi.cc.mobilesafe.engine.AddressDao;
import indi.cc.mobilesafe.utils.ConstantValue;
import indi.cc.mobilesafe.utils.SpUtil;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

public class AddressService extends Service{
	public static final String tag = "AddressService";
	private TelephonyManager mTM;
	private MyPhoneStateListener mPhoneStateListener;
	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private View mViewToast;
	private WindowManager mWM;
	private String mAddress;
	private TextView tv_toast;
	private int[] mDrawableIds;
	
	private int mScreenHeight;
	private int mScreenWidth;
	
	private InnerOutCallReceiver mInnerOutCallReceiver;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			tv_toast.setText(mAddress);
		};
	};
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void onCreate() {
		//��һ�ο��������Ժ�,����Ҫȥ������˾����ʾ
		//�绰״̬�ļ���(��������ʱ��,��Ҫȥ������,�رյ�ʱ��绰״̬�Ͳ���Ҫ����)
		//1,�绰�����߶���
		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		//2,�����绰״̬
		mPhoneStateListener = new MyPhoneStateListener();
		mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		//��ȡ�������
		mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
		
		mScreenHeight = mWM.getDefaultDisplay().getHeight();
		mScreenWidth = mWM.getDefaultDisplay().getWidth();
		
		//���������绰�Ĺ㲥��������(Ȩ��)
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		//�����㲥������
		mInnerOutCallReceiver = new InnerOutCallReceiver();
		registerReceiver(mInnerOutCallReceiver, intentFilter);
		
		super.onCreate();
	}
	
	
	class InnerOutCallReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			//���յ��˹㲥��,��Ҫ��ʾ�Զ������˾,��ʾ���������غ���
			//��ȡ�����绰������ַ���
			String phone = getResultData();
			showToast(phone);
		}
	}
	
	class MyPhoneStateListener extends PhoneStateListener{
		//3,�ֶ���д,�绰״̬�����ı�ᴥ���ķ���
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				//����״̬,û���κλ(�Ƴ���˾)
				Log.i(tag, "�Ҷϵ绰,������.......................");
				//�Ҷϵ绰��ʱ������Ҫ�Ƴ���˾
				if(mWM!=null && mViewToast!=null){
					mWM.removeView(mViewToast);
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				//ժ��״̬�������и��绰����û���ǲ���dialing������ͨ��
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				//����(չʾ��˾)
				Log.i(tag, "������.......................");
				showToast(incomingNumber);
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	public void showToast(String incomingNumber) {
	    final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE	Ĭ���ܹ�������
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        //�������ʱ����ʾ��˾,�͵绰����һ��
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        
        //ָ����˾������λ��(����˾ָ�������Ͻ�)
        params.gravity = Gravity.LEFT+Gravity.TOP;
        
        //��˾��ʾЧ��(��˾�����ļ�),xml-->view(��˾),����˾���ڵ�windowManager������
        mViewToast = View.inflate(this, R.layout.toast_view, null);
        tv_toast = (TextView) mViewToast.findViewById(R.id.tv_toast);
        
        
        mViewToast.setOnTouchListener(new OnTouchListener() {
			private int startX;
			private int startY;

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
					
					params.x = params.x+disX;
					params.y = params.y+disY;
					
					//�ݴ���
					if(params.x<0){
						params.x = 0;
					}
					
					if(params.y<0){
						params.y=0;
					}
					
					if(params.x>mScreenWidth-mViewToast.getWidth()){
						params.x = mScreenWidth-mViewToast.getWidth();
					}
					
					if(params.y>mScreenHeight-mViewToast.getHeight()-22){
						params.y = mScreenHeight-mViewToast.getHeight()-22;
					}
					
					//��֪������˾��Ҫ�������Ƶ��ƶ�,ȥ��λ�õĸ���
					mWM.updateViewLayout(mViewToast, params);
					
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					
					break;
				case MotionEvent.ACTION_UP:
					SpUtil.putInt(getApplicationContext(),ConstantValue.LOCATION_X, params.x);
					SpUtil.putInt(getApplicationContext(),ConstantValue.LOCATION_Y, params.y);
					break;
				}
				//true ��Ӧ��ק�������¼�
				return true;
			}
		});
        
        //��ȡsp�д洢��˾λ�õ�x,y����ֵ
        
        // params.xΪ��˾���Ͻǵ�x������
        params.x = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
        // params.yΪ��˾���Ͻǵ�y������
        params.y = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, 0);
        
        
        //��sp�л�ȡɫֵ���ֵ�����,ƥ��ͼƬ,����չʾ
        mDrawableIds = new int[]{
        		R.drawable.call_locate_white,
        		R.drawable.call_locate_orange,
        		R.drawable.call_locate_blue,
        		R.drawable.call_locate_gray,
        		R.drawable.call_locate_green};
        //ȡ���洢����ɫ
        int toastStyleIndex = SpUtil.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);
        //������ɫ
        tv_toast.setBackgroundResource(mDrawableIds[toastStyleIndex]);
        
        //�ڴ����Ϲ���һ��view(Ȩ��)
        mWM.addView(mViewToast, params);
        
        //��ȡ������������Ժ�,��Ҫ����������ѯ
        query(incomingNumber);
	}
	
	private void query(final String incomingNumber) {
		new Thread(){
			public void run() {
				mAddress = AddressDao.getAddress(incomingNumber);
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	@Override
	public void onDestroy() {
		//ȡ���Ե绰״̬�ļ���(���������ʱ������绰�Ķ���)
		if(mTM!=null && mPhoneStateListener!=null){
			mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
		if(mInnerOutCallReceiver!=null){
			//ȥ��㲥�����ߵ�ע������
			unregisterReceiver(mInnerOutCallReceiver);
		}
		super.onDestroy();
	}
	
}
