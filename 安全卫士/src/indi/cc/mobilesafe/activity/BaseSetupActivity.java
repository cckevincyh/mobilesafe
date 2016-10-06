package indi.cc.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
/**
 * 抽取方法的Activity
 * @author c
 *
 */
public abstract class BaseSetupActivity extends Activity {

	private GestureDetector gestureDetector;

	//1,监听屏幕上响应的事件类型(按下(1次),移动(多次),抬起(1次))
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//3,通过手势处理类,接收多种类型的事件,用作处理
		gestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		//2,创建手势管理的对象,用作管理在onTouchEvent(event)传递过来的手势动作
		gestureDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				//监听手势的移动
				if(e1.getX()-e2.getX()>0){
					//调用子类的下一页方法,抽象方法
					
					//在第一个界面上的时候,跳转到第二个界面
					//在第二个界面上的时候,跳转到第三个界面
					//.......
					showNextPage();
				}
				
				if(e1.getX()-e2.getX()<0){
					//调用子类的上一页方法
					//在第一个界面上的时候,无响应,空实现
					//在第二个界面上的时候,跳转到第1个界面
					//.......
					showPrePage();
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}



	//下一页的抽象方法,由子类决定具体跳转到那个界面
	protected abstract void showNextPage();
	//上一页的抽象方法,由子类决定具体跳转到那个界面
	protected abstract void showPrePage();

	
	//点击下一页按钮的时候,根据子类的showNextPage方法做相应跳转
	public void nextPage(View view){
		showNextPage();
	}
	
	//点击上一页按钮的时候,根据子类的showPrePage方法做相应跳转
	public void prePage(View view){
		showPrePage();
	}
	
}
