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
	//��ȡ���������ķ���
	public static int getProcessCount(Context ctx){
		//1,��ȡactivityManager
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//2,��ȡ�������н��̵ļ���
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		//3,���ؼ��ϵ�����
		return runningAppProcesses.size();
	}
	
	
	/**
	 * @param ctx	
	 * @return ���ؿ��õ��ڴ���	bytes
	 */
	public static long getAvailSpace(Context ctx){
		//1,��ȡactivityManager
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//2,�����洢�����ڴ�Ķ���
		MemoryInfo memoryInfo = new MemoryInfo();
		//3,��memoryInfo����(�����ڴ�)ֵ
		am.getMemoryInfo(memoryInfo);
		//4,��ȡmemoryInfo����Ӧ�����ڴ��С
		return memoryInfo.availMem;
	}   
	
	
	/**
	 * @param ctx	
	 * @return �����ܵ��ڴ���	��λΪbytes ����0˵���쳣
	 */
	public static long getTotalSpace(Context ctx){
		/*//1,��ȡactivityManager
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//2,�����洢�����ڴ�Ķ���
		MemoryInfo memoryInfo = new MemoryInfo();
		//3,��memoryInfo����(�����ڴ�)ֵ
		am.getMemoryInfo(memoryInfo);
		//4,��ȡmemoryInfo����Ӧ�����ڴ��С
		return memoryInfo.totalMem;*/
		
		//�ڴ��Сд���ļ���,��ȡproc/meminfo�ļ�,��ȡ��һ��,��ȡ�����ַ�,ת����bytes����
		FileReader fileReader  = null;
		BufferedReader bufferedReader = null;
		try {
			fileReader= new FileReader("proc/meminfo");
			bufferedReader = new BufferedReader(fileReader);
			String lineOne = bufferedReader.readLine();
			//���ַ���ת�����ַ�������
			char[] charArray = lineOne.toCharArray();
			//ѭ������ÿһ���ַ�,������ַ���ASCII����0��9��������,˵�����ַ���Ч
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
	 * @param ctx	�����Ļ���
	 * @return		��ǰ�ֻ��������еĽ��̵������Ϣ
	 */
	public static List<ProcessInfo> getProcessInfo(Context ctx){
		//��ȡ���������Ϣ
		List<ProcessInfo> processInfoList = new ArrayList<ProcessInfo>();
		//1,activityManager�����߶����PackageManager�����߶���
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = ctx.getPackageManager();
		//2,��ȡ�������еĽ��̵ļ���
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
	
		//3,ѭ���������߼���,��ȡ���������Ϣ(����,����,ͼ��,ʹ���ڴ��С,�Ƿ�Ϊϵͳ����(״̬��))
		for (RunningAppProcessInfo info : runningAppProcesses) {
			ProcessInfo processInfo = new ProcessInfo();
			//4,��ȡ���̵����� == Ӧ�õİ���
			processInfo.packageName = info.processName;
			//5,��ȡ����ռ�õ��ڴ��С(����һ�����̶�Ӧ��pid����)
			android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
			//6,��������������λ��Ϊ0�Ķ���,Ϊ��ǰ���̵��ڴ���Ϣ�Ķ���
			android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
			//7,��ȡ��ʹ�õĴ�С
			processInfo.memSize = memoryInfo.getTotalPrivateDirty()*1024;
			
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.packageName, 0);
				//8,��ȡӦ�õ�����
				processInfo.name = applicationInfo.loadLabel(pm).toString();
				//9,��ȡӦ�õ�ͼ��
				processInfo.icon = applicationInfo.loadIcon(pm);
				//10,�ж��Ƿ�Ϊϵͳ����
				if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM){
					processInfo.isSystem = true;
				}else{
					processInfo.isSystem = false;
				}
			} catch (NameNotFoundException e) {
				//��Ҫ����
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
	 * ɱ���̷���
	 * @param ctx	�����Ļ���
	 * @param processInfo	ɱ���������ڵ�javabean�Ķ���
	 */
	public static void killProcess(Context ctx,ProcessInfo processInfo) {
		//1,��ȡactivityManager
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//2,ɱ��ָ����������(Ȩ��)
		am.killBackgroundProcesses(processInfo.packageName);
	}
	
	/**
	 * ɱ�����н���
	 * @param ctx	�����Ļ���
	 */
	public static void killAll(Context ctx) {
		//1,��ȡactivityManager
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//2,��ȡ�������н��̵ļ���
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		//3,ѭ���������еĽ���,����ɱ��
		for (RunningAppProcessInfo info : runningAppProcesses) {
			//4,�����ֻ���ʿ����,�����Ľ��̶���Ҫȥɱ��
			if(info.processName.equals(ctx.getPackageName())){
				//���ƥ�������ֻ���ʿ,����Ҫ��������ѭ��,������һ��Ѱ,����ɱ������
				continue;
			}
			am.killBackgroundProcesses(info.processName);
		}
	}
	
}
