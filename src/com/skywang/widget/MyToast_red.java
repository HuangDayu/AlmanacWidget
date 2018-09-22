package com.skywang.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ×Ô¶¨ÒåToast http://blog.csdn.net/ccpat/article/details/48296519
 * 
 * @author Administrator
 *
 */
public class MyToast_red {
	private Toast mToast;

	private MyToast_red(Context context, CharSequence text, int duration) {
		View v = LayoutInflater.from(context).inflate(R.layout.mytoast_red, null);
		TextView textView = (TextView) v.findViewById(R.id.textView1);
		textView.setText(text);
		mToast = new Toast(context);
		mToast.setDuration(duration);
		mToast.setView(v);
	}

	public static MyToast_red makeText(Context context, CharSequence text, int duration) {
		return new MyToast_red(context, text, duration);
	}

	public void show() {
		if (mToast != null) {
			mToast.show();
		}
	}

	public void setGravity(int gravity, int xOffset, int yOffset) {
		if (mToast != null) {
			mToast.setGravity(gravity, xOffset, yOffset);
		}
	}
}
