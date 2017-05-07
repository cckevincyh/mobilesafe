package indi.cc.mobilesafe.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

public class SmsBackUp {
private static int index = 0;
	
	//���ݶ��ŷ���
	//ProgressDialog pd	ԭ���ĶԻ���
	//���ڸ�Ϊ������
	
	//A,����һ�����������ڵĶԻ���
	//B,����һ��������
	
	public static void backup(Context ctx,String path,CallBack callBack) {
		FileOutputStream fos = null;
		Cursor cursor = null;
		try {
			//��Ҫ�õ��Ķ��������Ļ���,�����ļ���·��,���������ڵĶԻ���������ڱ��ݹ����н��ȵĸ���
			//1,��ȡ���ݶ���д����ļ�
			File file = new File(path);
			//2,��ȡ���ݽ�����,��ȡ�������ݿ�������
			cursor = ctx.getContentResolver().query(Uri.parse("content://sms/"), 
					new String[]{"address","date","type","body"}, null, null, null);
			//3,�ļ���Ӧ�������
			fos = new FileOutputStream(file);
			//4,���л����ݿ��ж�ȡ������,���õ�xml��
			XmlSerializer newSerializer = Xml.newSerializer();
			//5,����xml����Ӧ����
			newSerializer.setOutput(fos, "utf-8");
			//DTD(xml�淶)
			newSerializer.startDocument("utf-8", true);
			
			newSerializer.startTag(null, "smss");
			
			//6,���ݶ�������ָ��
			//A ������ݽ������ǶԻ���,ָ���Ի��������������
			//B	������ݽ������ǽ�����,ָ��������������
//			pd.setMax(cursor.getCount());
			
			if(callBack!=null){
				callBack.setMax(cursor.getCount());
			}
			
			//7,��ȡ���ݿ��е�ÿһ�е�����д�뵽xml��
			while(cursor.moveToNext()){
				newSerializer.startTag(null, "sms");
				
				newSerializer.startTag(null, "address");
				newSerializer.text(cursor.getString(0));
				newSerializer.endTag(null, "address");
				
				newSerializer.startTag(null, "date");
				newSerializer.text(cursor.getString(1));
				newSerializer.endTag(null, "date");
				
				newSerializer.startTag(null, "type");
				newSerializer.text(cursor.getString(2));
				newSerializer.endTag(null, "type");
				
				newSerializer.startTag(null, "body");
				newSerializer.text(cursor.getString(3));
				newSerializer.endTag(null, "body");
				
				newSerializer.endTag(null, "sms");
				
				//8,ÿѭ��һ�ξ���Ҫȥ�ý���������
				index++;
				Thread.sleep(500);
				//ProgressDialog���������߳��и�����Ӧ�Ľ������ĸı�
				
				//A ������ݽ������ǶԻ���,ָ���Ի���������ĵ�ǰ�ٷֱ�
				//B	������ݽ������ǽ�����,ָ���������ĵ�ǰ�ٷֱ�
//				pd.setProgress(index);
				
				if(callBack!=null){
					callBack.setProgress(index);
				}
			}
			newSerializer.endTag(null, "smss");
			newSerializer.endDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(cursor!=null && fos!=null){
					cursor.close();
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	//�ص�
	//1.����һ���ӿ�
	//2,����ӿ���δʵ�ֵ�ҵ���߼�����(������������,���ݹ����ж��Űٷֱȸ���)
	//3.����һ��ʵ���˴˽ӿڵ���Ķ���(�����ݶ��ŵĹ�������),�ӿڵ�ʵ����,һ��ʵ������������Ϊʵ�ַ���(�;�����ʹ�öԻ���,���ǽ�����)
	//4.��ȡ���ݽ����Ķ���,�ں��ʵĵط�(��������,���ðٷֱȵĵط�)�������ĵ���
	public interface CallBack{
		//������������Ϊʵ�ַ���(���Լ���������	�Ի���.setMax(max) ������	������.setMax(max))
		public void setMax(int max);
		//���ݹ����ж��Űٷֱȸ���(���Լ���������	�Ի���.setProgress(max) ������	������.setProgress(max))
		public void setProgress(int index);
	}
}
