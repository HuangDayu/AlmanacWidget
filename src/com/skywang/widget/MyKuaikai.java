package com.skywang.widget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MyKuaikai extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getPackageManager().getLaunchIntentForPackage("com.appinfo.kuaikai");//com.android.keyguard
		startActivity(intent);
		this.finish();
	}

}
