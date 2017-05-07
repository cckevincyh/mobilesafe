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
	//BlackNumberDao单例模式
	//1,私有化构造方法
	private BlackNumberDao(Context context){
		//创建数据库已经其表机构
		blackNumberOpenHelper = new BlackNumberOpenHelper(context);
	}
	
	//2,声明一个当前类的对象
	private static BlackNumberDao blackNumberDao = null;
	//3,提供一个静态方法,如果当前类的对象为空,创建一个新的
	public static BlackNumberDao getInstance(Context context){
		if(blackNumberDao == null){
			blackNumberDao = new BlackNumberDao(context);
		}
		return blackNumberDao;
	}
	
	
	/**增加一个条目
	 * @param phone	拦截的电话号码
	 * @param mode	拦截类型(1:短信	2:电话	3:拦截所有(短信+电话))
	 */
	public void insert(String phone,String mode){
		//1,开启数据库,准备做写入操作
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("phone", phone);
		values.put("mode", mode);
		db.insert("blacknumber", null, values);
		
		db.close();
	}
	
	/**从数据库中删除一条电话号码
	 * @param phone	删除电话号码
	 */
	public void delete(String phone){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

		db.delete("blacknumber", "phone = ?", new String[]{phone});
		
		db.close();
	}
	
	/**
	 * 根据电话号码去,更新拦截模式
	 * @param phone	更新拦截模式的电话号码
	 * @param mode	要更新为的模式(1:短信	2:电话	3:拦截所有(短信+电话)
	 */
	public void update(String phone,String mode){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

		ContentValues contentValues = new ContentValues();
		contentValues.put("mode", mode);
		
		db.update("blacknumber", contentValues, "phone = ?", new String[]{phone});
		
		db.close();
	}
	
	/**
	 * @return	查询到数据库中所有的号码以及拦截类型所在的集合
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
	 * 每次查询20条数据
	 * @param index	查询的索引值
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
	 * @return	数据库中数据的总条目个数,返回0代表没有数据或异常
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
	 * @param phone	作为查询条件的电话号码
	 * @return	传入电话号码的拦截模式	1:短信	2:电话	3:所有	0:没有此条数据
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
