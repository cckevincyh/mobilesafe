package indi.cc.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AddressDao {
	private static final String tag = "AddressDao";
	//1,ָ���������ݿ��·��
	public static String path = "data/data/indi.cc.mobilesafe/files/address.db";
	private static String mAddress = "δ֪����";
	/**����һ���绰����,�������ݿ�����,���з���,����һ��������
	 * @param phone	��ѯ�绰����
	 */
	public static String getAddress(String phone){
		mAddress = "δ֪����";
		//������ʽ,ƥ���ֻ�����
		//�ֻ������������ʽ
		String regularExpression = "^1[3-8]\\d{9}";
		//2,�������ݿ�����(ֻ������ʽ��)
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		if(phone.matches(regularExpression)){
			phone = phone.substring(0,7);
			//3,���ݿ��ѯ
			Cursor cursor = db.query("data1", new String[]{"outkey"}, "id = ?", new String[]{phone}, null, null, null);
			//4,�鵽����
			if(cursor.moveToNext()){
				String outkey = cursor.getString(0);
				Log.i(tag, "outkey = "+outkey);
				//5,ͨ��data1��ѯ���Ľ��,��Ϊ�����ѯdata2
				Cursor indexCursor = db.query("data2", new String[]{"location"}, "id = ?", new String[]{outkey}, null, null, null);
				if(indexCursor.moveToNext()){
					//6,��ȡ��ѯ���ĵ绰������
					mAddress = indexCursor.getString(0);
					Log.i(tag, "address = "+mAddress);
				}
			}else{
				mAddress = "δ֪����";
			}
		}else{
			int length = phone.length();
			switch (length) {
			case 3://119 110 120 114
				mAddress = "�����绰";
				break;
			case 4://119 110 120 114
				mAddress = "ģ����";
				break;
			case 5://10086 99555
				mAddress = "����绰";
				break;
			case 7:
				mAddress = "���ص绰";
				break;
			case 8:
				mAddress = "���ص绰";
				break;
			case 11:
				//(3+8) ����+��������(���),��ѯdata2
				String area = phone.substring(1, 3);
				Cursor cursor = db.query("data2", new String[]{"location"}, "area = ?", new String[]{area}, null, null, null);
				if(cursor.moveToNext()){
					mAddress = cursor.getString(0);
				}else{
					mAddress = "δ֪����";
				}
				break;
			case 12:
				//(4+8) ����(0791(�����ϲ�))+��������(���),��ѯdata2
				String area1 = phone.substring(1, 4);
				Cursor cursor1 = db.query("data2", new String[]{"location"}, "area = ?", new String[]{area1}, null, null, null);
				if(cursor1.moveToNext()){
					mAddress = cursor1.getString(0);
				}else{
					mAddress = "δ֪����";
				}
				break;
			}
		}
		return mAddress;
	}
}
