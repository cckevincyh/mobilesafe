package indi.cc.mobilesafe.db.domain;


import android.graphics.drawable.Drawable;

public class ProcessInfo {
	public String name;//Ӧ������
	public Drawable icon;//Ӧ��ͼ��
	public long memSize;//Ӧ����ʹ�õ��ڴ���
	public boolean isCheck;//�Ƿ�ѡ��
	public boolean isSystem;//�Ƿ�ΪϵͳӦ��
	public String packageName;//�������û������,��������Ӧ�õİ�����Ϊ����
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public long getMemSize() {
		return memSize;
	}
	public void setMemSize(long memSize) {
		this.memSize = memSize;
	}
	public boolean isCheck() {
		return isCheck;
	}
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
	public boolean isSystem() {
		return isSystem;
	}
	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
}
