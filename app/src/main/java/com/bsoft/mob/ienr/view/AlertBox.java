package com.bsoft.mob.ienr.view;


import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.bsoft.mob.ienr.helper.ViewBuildHelper;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-17 上午10:16:50
 * @类说明
 */
public class AlertBox {

	private AlertBox() {
	}

	public static void Show(Context context, String title, String msg,
			String btname) {
        View txt = ViewBuildHelper.buildDialogTitleTextView(context, title);
		new AlertDialog.Builder(context)
              //  .setTitle(title)
                .setCustomTitle(txt)
				.setIcon(android.R.drawable.ic_dialog_alert).setMessage(msg)
				.setPositiveButton(btname, null).show();
	}

	public static void Show(Context context, String title, String msg,
			String btname,
			android.content.DialogInterface.OnClickListener onClick) {
        View txt = ViewBuildHelper.buildDialogTitleTextView(context, title);
        new AlertDialog.Builder(context)
                //  .setTitle(title)
                .setCustomTitle(txt)
				.setIcon(android.R.drawable.ic_dialog_alert).setMessage(msg)
				.setPositiveButton(btname, onClick).show();
	}
	public static void Show(Context context, String title, String msg,
							String btname,String notName,
							android.content.DialogInterface.OnClickListener onClick,
							android.content.DialogInterface.OnClickListener onCancleClick) {
		View txt = ViewBuildHelper.buildDialogTitleTextView(context, title);
		new AlertDialog.Builder(context)
				//  .setTitle(title)
				.setCustomTitle(txt)
				.setIcon(android.R.drawable.ic_dialog_alert).setMessage(msg)
				.setPositiveButton(btname, onClick)
				.setNegativeButton(notName,onCancleClick).show();
	}

	public static void ShowErr(Context context, String msg) {
		Show(context, "出错了", msg, "出错了");
	}

}
