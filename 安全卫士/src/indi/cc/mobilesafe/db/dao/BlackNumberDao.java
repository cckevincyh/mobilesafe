package indi.cc.mobilesafe.db.dao;

import indi.cc.mobilesafe.db.BlackNumberOpenHelper;
import indi.cc.mobilesafe.db.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BlackNumberDao {

	private BlackNumberOpenHelper blackNumberOpenHelper;
	//BlackNumberDao����ģʽ
	//1,˽�л����췽��
	private BlackNumberDao(Context context){
		//�������ݿ��Ѿ�������
		blackNumberOpenHelper = new BlackNumberOpenHelper(context);
	}
	
	//2,����һ����ǰ��Ķ���
	private static BlackNumberDao blackNumberDao = null;
	//3,�ṩһ����̬����,�����ǰ��Ķ���Ϊ��,����һ���µ�
	public static BlackNumberDao getInstance(Context context){
		if(blackNumberDao == null){
			blackNumberDao = new BlackNumberDao(context);
		}
		return blackNumberDao;
	}
	
	
	/**����һ����Ŀ
	 * @param phone	���صĵ绰����
	 * @param mode	��������(1:����	2:�绰	3:��������(����+�绰))
	 */
	public void insert(String phone,String mode){
		//1,�������ݿ�,׼����д�����
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("phone", phone);
		values.put("mode", mode);
		db.insert("blacknumber", null, values);
		
		db.close();
	}
	
	/**�����ݿ���ɾ��һ���绰����
	 * @param phone	ɾ���绰����
	 */
	public void delete(String phone){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

		db.delete("blacknumber", "phone = ?", new String[]{phone});
		
		db.close();
	}
	
	/**
	 * ���ݵ绰����ȥ,��������ģʽ
	 * @param phone	��������ģʽ�ĵ绰����
	 * @param mode	Ҫ����Ϊ��ģʽ(1:����	2:�绰	3:��������(����+�绰)
	 */
	public void update(String phone,String mode){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

		ContentValues contentValues = new ContentValues();
		contentValues.put("mode", mode);
		
		db.update("blacknumber", contentValues, "phone = ?", new String[]{phone});
		
		db.close();
	}
	
	/**
	 * @return	��ѯ�����ݿ������еĺ����Լ������������ڵļ���
	 */
	public List<BlackNumberInfo> findAll(){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

		Cursor cursor = db.query("blacknumber", new String[]{"phone","mode"}, null, null, null, null, "_id desc");
		List<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()){
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.phone = cursor.getString(0);
			blackNumberInfo.mode = cursor.getString(1);
			blackNumberList.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		
		return blackNumberList;
	}
	
	/**
	 * ÿ�β�ѯ20������
	 * @param index	��ѯ������ֵ
	 */
	public List<BlackNumberInfo> find(int index){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

		Cursor cursor = db.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,20;", new String[]{index+""});
		
		List<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()){
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.phone = cursor.getString(0);
			blackNumberInfo.mode = cursor.getString(1);
			blackNumberList.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		
		return blackNumberList;
	}
	
	/**
	 * @return	���ݿ������ݵ�����Ŀ����,����0����û�����ݻ��쳣
	 */
	public int getCount(){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		int count = 0;
		Cursor cursor = db.rawQuery("select count(*) from blacknumber;", null);
		if(cursor.moveToNext()){
			count = cursor.getInt(0);
		}
		
		cursor.close();
		db.close();
		return count;
	}
	
	/**
	 * @param phone	��Ϊ��ѯ�����ĵ绰����
	 * @return	����绰���������ģʽ	1:����	2:�绰	3:����	0:û�д�������
	 */
	public int getMode(String phone){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		int mode = 0;
		Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "phone = ?", new String[]{phone}, null, null,null);
		if(cursor.moveToNext()){
			mode = cursor.getInt(0);
		}
		
		cursor.close();
		db.close();
		return mode;
	}
}
