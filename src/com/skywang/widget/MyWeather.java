package com.skywang.widget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MyWeather extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getPackageManager().getLaunchIntentForPackage("com.huawei.android.totemweather");
		startActivity(intent);
		this.finish();
	}

}
