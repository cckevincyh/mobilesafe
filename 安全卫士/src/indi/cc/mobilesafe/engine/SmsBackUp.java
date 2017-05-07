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
	
	//备份短信方法
	//ProgressDialog pd	原来的对话框
	//现在改为进度条
	
	//A,传递一个进度条所在的对话框
	//B,传递一个进度条
	
	public static void backup(Context ctx,String path,CallBack callBack) {
		FileOutputStream fos = null;
		Cursor cursor = null;
		try {
			//需要用到的对象上下文环境,备份文件夹路径,进度条所在的对话框对象用于备份过程中进度的更新
			//1,获取备份短信写入的文件
			File file = new File(path);
			//2,获取内容解析器,获取短信数据库中数据
			cursor = ctx.getContentResolver().query(Uri.parse("content://sms/"), 
					new String[]{"address","date","type","body"}, null, null, null);
			//3,文件相应的输出流
			fos = new FileOutputStream(file);
			//4,序列化数据库中读取的数据,放置到xml中
			XmlSerializer newSerializer = Xml.newSerializer();
			//5,给次xml做相应设置
			newSerializer.setOutput(fos, "utf-8");
			//DTD(xml规范)
			newSerializer.startDocument("utf-8", true);
			
			newSerializer.startTag(null, "smss");
			
			//6,备份短信总数指定
			//A 如果传递进来的是对话框,指定对话框进度条的总数
			//B	如果传递进来的是进度条,指定进度条的总数
//			pd.setMax(cursor.getCount());
			
			if(callBack!=null){
				callBack.setMax(cursor.getCount());
			}
			
			//7,读取数据库中的每一行的数据写入到xml中
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
				
				//8,每循环一次就需要去让进度条叠加
				index++;
				Thread.sleep(500);
				//ProgressDialog可以在子线程中更新相应的进度条的改变
				
				//A 如果传递进来的是对话框,指定对话框进度条的当前百分比
				//B	如果传递进来的是进度条,指定进度条的当前百分比
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
	//回调
	//1.定义一个接口
	//2,定义接口中未实现的业务逻辑方法(短信总数设置,备份过程中短信百分比更新)
	//3.传递一个实现了此接口的类的对象(至备份短信的工具类中),接口的实现类,一定实现了上诉两个为实现方法(就决定了使用对话框,还是进度条)
	//4.获取传递进来的对象,在合适的地方(设置总数,设置百分比的地方)做方法的调用
	public interface CallBack{
		//短信总数设置为实现方法(由自己决定是用	对话框.setMax(max) 还是用	进度条.setMax(max))
		public void setMax(int max);
		//备份过程中短信百分比更新(由自己决定是用	对话框.setProgress(max) 还是用	进度条.setProgress(max))
		public void setProgress(int index);
	}
}
