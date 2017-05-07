package indi.cc.mobilesafe.engine;

import indi.cc.mobilesafe.db.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;


public class AppInfoProvider {
	/**
	 * 返回当前手机所有的应用的相关信息(名称,包名,图标,(手机内存,sd卡),(系统,用户));
	 * @param ctx	获取包管理者的上下文环境
	 * @return	包含手机安装应用相关信息的集合
	 */
	public static List<AppInfo> getAppInfoList(Context ctx){
		//1,包的管理者对象
		PackageManager pm = ctx.getPackageManager();
		//2,获取安装在手机上应用相关信息的集合
		List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
		List<AppInfo> appInfoList = new ArrayList<AppInfo>();
		//3,循环遍历应用信息的集合
		for (PackageInfo packageInfo : packageInfoList) {
			AppInfo appInfo = new AppInfo();
			//4,获取应用的包名
			appInfo.packageName = packageInfo.packageName;
			//5,应用名称
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			appInfo.name = applicationInfo.loadLabel(pm).toString();
			//6,获取图标
			appInfo.icon = applicationInfo.loadIcon(pm);
			//7,判断是否为系统应用(每一个手机上的应用对应的flag都不一致)
			if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
				//系统应用
				appInfo.isSystem = true;
			}else{
				//非系统应用
				appInfo.isSystem = false;
			}
			//8,是否为sd卡中安装应用
			if((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)==ApplicationInfo.FLAG_EXTERNAL_STORAGE){
				//系统应用
				appInfo.isSdCard = true;
			}else{
				//非系统应用
				appInfo.isSdCard = false;
			}
			appInfoList.add(appInfo);
		}
		return appInfoList;
	}
}
