package indi.cc.mobilesafe.engine;

import indi.cc.mobilesafe.R;
import indi.cc.mobilesafe.db.domain.ProcessInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;


public class ProcessInfoProvider {
	//获取进程总数的方法
	public static int getProcessCount(Context ctx){
		//1,获取activityManager
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//2,获取正在运行进程的集合
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		//3,返回集合的总数
		return runningAppProcesses.size();
	}
	
	
	/**
	 * @param ctx	
	 * @return 返回可用的内存数	bytes
	 */
	public static long getAvailSpace(Context ctx){
		//1,获取activityManager
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//2,构建存储可用内存的对象
		MemoryInfo memoryInfo = new MemoryInfo();
		//3,给memoryInfo对象赋(可用内存)值
		am.getMemoryInfo(memoryInfo);
		//4,获取memoryInfo中相应可用内存大小
		return memoryInfo.availMem;
	}   
	
	
	/**
	 * @param ctx	
	 * @return 返回总的内存数	单位为bytes 返回0说明异常
	 */
	public static long getTotalSpace(Context ctx){
		/*//1,获取activityManager
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//2,构建存储可用内存的对象
		MemoryInfo memoryInfo = new MemoryInfo();
		//3,给memoryInfo对象赋(可用内存)值
		am.getMemoryInfo(memoryInfo);
		//4,获取memoryInfo中相应可用内存大小
		return memoryInfo.totalMem;*/
		
		//内存大小写入文件中,读取proc/meminfo文件,读取第一行,获取数字字符,转换成bytes返回
		FileReader fileReader  = null;
		BufferedReader bufferedReader = null;
		try {
			fileReader= new FileReader("proc/meminfo");
			bufferedReader = new BufferedReader(fileReader);
			String lineOne = bufferedReader.readLine();
			//将字符串转换成字符的数组
			char[] charArray = lineOne.toCharArray();
			//循环遍历每一个字符,如果此字符的ASCII码在0到9的区域内,说明此字符有效
			StringBuffer stringBuffer = new StringBuffer();
			for (char c : charArray) {
				if(c>='0' && c<='9'){
					stringBuffer.append(c);
				}
			}
			return Long.parseLong(stringBuffer.toString())*1024;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(fileReader!=null && bufferedReader!=null){
					fileReader.close();
					bufferedReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}  
	
	/**
	 * @param ctx	上下文环境
	 * @return		当前手机正在运行的进程的相关信息
	 */
	public static List<ProcessInfo> getProcessInfo(Context ctx){
		//获取进程相关信息
		List<ProcessInfo> processInfoList = new ArrayList<ProcessInfo>();
		//1,activityManager管理者对象和PackageManager管理者对象
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = ctx.getPackageManager();
		//2,获取正在运行的进程的集合
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
	
		//3,循环遍历上诉集合,获取进程相关信息(名称,包名,图标,使用内存大小,是否为系统进程(状态机))
		for (RunningAppProcessInfo info : runningAppProcesses) {
			ProcessInfo processInfo = new ProcessInfo();
			//4,获取进程的名称 == 应用的包名
			processInfo.packageName = info.processName;
			//5,获取进程占用的内存大小(传递一个进程对应的pid数组)
			android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
			//6,返回数组中索引位置为0的对象,为当前进程的内存信息的对象
			android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
			//7,获取已使用的大小
			processInfo.memSize = memoryInfo.getTotalPrivateDirty()*1024;
			
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.packageName, 0);
				//8,获取应用的名称
				processInfo.name = applicationInfo.loadLabel(pm).toString();
				//9,获取应用的图标
				processInfo.icon = applicationInfo.loadIcon(pm);
				//10,判断是否为系统进程
				if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM){
					processInfo.isSystem = true;
				}else{
					processInfo.isSystem = false;
				}
			} catch (NameNotFoundException e) {
				//需要处理
				processInfo.name = info.processName;
				processInfo.icon = ctx.getResources().getDrawable(R.drawable.ic_launcher);
				processInfo.isSystem = true;
				e.printStackTrace();
			}
			processInfoList.add(processInfo);
		}
		return processInfoList;
	}


	/**
	 * 杀进程方法
	 * @param ctx	上下文环境
	 * @param processInfo	杀死进程所在的javabean的对象
	 */
	public static void killProcess(Context ctx,ProcessInfo processInfo) {
		//1,获取activityManager
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//2,杀死指定包名进程(权限)
		am.killBackgroundProcesses(processInfo.packageName);
	}
	
	/**
	 * 杀死所有进程
	 * @param ctx	上下文环境
	 */
	public static void killAll(Context ctx) {
		//1,获取activityManager
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//2,获取正在运行进程的集合
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		//3,循环遍历所有的进程,并且杀死
		for (RunningAppProcessInfo info : runningAppProcesses) {
			//4,除了手机卫士以外,其他的进程都需要去杀死
			if(info.processName.equals(ctx.getPackageName())){
				//如果匹配上了手机卫士,则需要跳出本次循环,进行下一次寻,继续杀死进程
				continue;
			}
			am.killBackgroundProcesses(info.processName);
		}
	}
	
}
