package indi.cc.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;


/**
 * @author c
 * �ܹ���ȡ������Զ���TextView
 */
public class FocusTextView extends TextView {
	//ʹ����ͨ��java���봴���ؼ�
	public FocusTextView(Context context) {
		super(context);
	}
	
	//��ϵͳ����(������+�����Ļ������췽��)
	public FocusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	//��ϵͳ����(������+�����Ļ������췽��+�����ļ��ж�����ʽ�ļ����췽��)
	public FocusTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	//��д��ȡ����ķ���,��ϵͳ����,���õ�ʱ��Ĭ�Ͼ��ܻ�ȡ����
	@Override
	public boolean isFocused() {
		return true;
	}
}
