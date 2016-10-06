package indi.cc.mobilesafe.view;

import indi.cc.mobilesafe.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {
	private static final String NAMESPACE = "http://schemas.android.com/apk/res/indi.cc.mobilesafe";
	private static final String tag = "SettingItemView";
	private CheckBox cb_box;
	private TextView tv_des;
	private String mDestitle;
	private String mDesoff;
	private String mDeson;
	
	
	public SettingItemView(Context context) {
		this(context,null);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		//xml--->view	�����ý����һ����Ŀת����view����,ֱ����ӵ��˵�ǰSettingItemView��Ӧ��view��
		View.inflate(context, R.layout.setting_item_view, this);

		//��ͬ���������д���
		/*View view = View.inflate(context, R.layout.setting_item_view, null);
		this.addView(view);*/
		
		//�Զ�����Ͽؼ��еı�������
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_des = (TextView) findViewById(R.id.tv_des);
		cb_box = (CheckBox) findViewById(R.id.cb_box);
		
		//��ȡ�Զ����Լ�ԭ�����ԵĲ���,д�ڴ˴�,AttributeSet attrs�����л�ȡ
		initAttrs(attrs);
		//��ȡ�����ļ��ж�����ַ���,��ֵ���Զ�����Ͽؼ��ı���
		tv_title.setText(mDestitle);
	}
	
	
	
	/**
	 * �������Լ������Զ�����������ֵ
	 * @param attrs	���췽����ά���õ����Լ���
	 */
	private void initAttrs(AttributeSet attrs) {
	/*
		//��ȡ���Ե��ܸ���
		Log.i(tag, "attrs.getAttributeCount() = "+attrs.getAttributeCount());
		//��ȡ���������Լ�����ֵ
		for(int i=0;i<attrs.getAttributeCount();i++){
			Log.i(tag, "name = "+attrs.getAttributeName(i));
			Log.i(tag, "value = "+attrs.getAttributeValue(i));
			Log.i(tag, "�ָ��� ================================= ");
		}
	*/
		
		//ͨ�����ռ�+�������ƻ�ȡ����ֵ

		mDestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
		mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
		mDeson = attrs.getAttributeValue(NAMESPACE, "deson");
		
		Log.i(tag, mDestitle);
		Log.i(tag, mDesoff);
		Log.i(tag, mDeson);
	}

	/**
	 * �ж��Ƿ����ķ���
	 * @return	���ص�ǰSettingItemView�Ƿ�ѡ��״̬	true����(checkBox����true)	false�ر�(checkBox����true)
	 */
	public boolean isCheck(){
		//��checkBox��ѡ�н��,������ǰ��Ŀ�Ƿ���
		return cb_box.isChecked();
	}

	/**
	 * @param isCheck	�Ƿ���Ϊ�����ı���,�ɵ��������ȥ������
	 */
	public void setCheck(boolean isCheck){
		//��ǰ��Ŀ��ѡ��Ĺ�����,cb_boxѡ��״̬Ҳ�ڸ���(isCheck)�仯
		cb_box.setChecked(isCheck);
		if(isCheck){
			//����
			tv_des.setText(mDeson);
		}else{
			//�ر�
			tv_des.setText(mDesoff);
		}
	}

}
