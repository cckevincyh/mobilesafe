package indi.cc.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
/**
 * ��ȡ������Activity
 * @author c
 *
 */
public abstract class BaseSetupActivity extends Activity {

	private GestureDetector gestureDetector;

	//1,������Ļ����Ӧ���¼�����(����(1��),�ƶ�(���),̧��(1��))
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//3,ͨ�����ƴ�����,���ն������͵��¼�,��������
		gestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		//2,�������ƹ���Ķ���,����������onTouchEvent(event)���ݹ��������ƶ���
		gestureDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				//�������Ƶ��ƶ�
				if(e1.getX()-e2.getX()>0){
					//�����������һҳ����,���󷽷�
					
					//�ڵ�һ�������ϵ�ʱ��,��ת���ڶ�������
					//�ڵڶ��������ϵ�ʱ��,��ת������������
					//.......
					showNextPage();
				}
				
				if(e1.getX()-e2.getX()<0){
					//�����������һҳ����
					//�ڵ�һ�������ϵ�ʱ��,����Ӧ,��ʵ��
					//�ڵڶ��������ϵ�ʱ��,��ת����1������
					//.......
					showPrePage();
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}



	//��һҳ�ĳ��󷽷�,���������������ת���Ǹ�����
	protected abstract void showNextPage();
	//��һҳ�ĳ��󷽷�,���������������ת���Ǹ�����
	protected abstract void showPrePage();

	
	//�����һҳ��ť��ʱ��,���������showNextPage��������Ӧ��ת
	public void nextPage(View view){
		showNextPage();
	}
	
	//�����һҳ��ť��ʱ��,���������showPrePage��������Ӧ��ת
	public void prePage(View view){
		showPrePage();
	}
	
}
