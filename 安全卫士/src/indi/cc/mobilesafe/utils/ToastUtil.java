package indi.cc.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	/**
	 * @param ctx	�����Ļ���
	 * @param msg	��ӡ�ı�����
	 */
	public static void show(Context ctx,String msg) {
		Toast.makeText(ctx, msg, 0).show();
	}
}
