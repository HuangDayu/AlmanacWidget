package com.skywang.widget;

import java.util.List;



import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;


public class MyGps extends Activity {

	private TextView positionTextView;

	private LocationManager locationManager;

	private String provider;

	private TextView textView1;

	private TextView textView2;

	private WebView wv;
	String currentPosition;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		
		//positionTextView = (TextView) findViewById(R.id.position_text_view);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//TextView textView = (TextView) findViewById(R.id.position_text_view);
		// 将字体文件保存在assets/fonts/目录下，创建Typeface对象
		Typeface typeFace = Typeface.createFromAsset(getAssets(),
				"fonts/sao.ttf");
		// 应用字体
		//textView.setTypeface(typeFace);
		
		// 获取所有可用的位置提供器
		// 如果GPS可以用就用GPS，GPS不能用则用网络
		// 都不能用的情况下弹出Toast提示用户
		List<String> providerList = locationManager.getProviders(true);
		if (providerList.contains(LocationManager.GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER;
		} else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
			provider = LocationManager.NETWORK_PROVIDER;
		} else {
			Toast.makeText(this, "No location provider to use",
					Toast.LENGTH_SHORT).show();
			return;
		}
		// 使用getLastKnownLocation就可以获取到记录当前位置信息的Location对象了
		// 并且用showLocation()显示当前设备的位置信息
		// requestLocationUpdates用于设置位置监听器
		// 此处监听器的时间间隔为5秒，距离间隔是5米
		// 也就是说每隔5秒或者每移动5米，locationListener中会更新一下位置信息
		Location location = locationManager.getLastKnownLocation(provider);
		if (location != null) {

			showLocation(location);
		}
		locationManager.requestLocationUpdates(provider, 5000, 5,
				locationListener);
	}
	public String  getGps(){
		// 获取所有可用的位置提供器
				// 如果GPS可以用就用GPS，GPS不能用则用网络
				// 都不能用的情况下弹出Toast提示用户
				List<String> providerList = locationManager.getProviders(true);
				if (providerList.contains(LocationManager.GPS_PROVIDER)) {
					provider = LocationManager.GPS_PROVIDER;
				} else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
					provider = LocationManager.NETWORK_PROVIDER;
				} else {
					Toast.makeText(this, "No location provider to use",
							Toast.LENGTH_SHORT).show();
					return provider;
				}
				// 使用getLastKnownLocation就可以获取到记录当前位置信息的Location对象了
				// 并且用showLocation()显示当前设备的位置信息
				// requestLocationUpdates用于设置位置监听器
				// 此处监听器的时间间隔为5秒，距离间隔是5米
				// 也就是说每隔5秒或者每移动5米，locationListener中会更新一下位置信息
				Location location = locationManager.getLastKnownLocation(provider);
				if (location != null) {

					showLocation(location);
				}
				locationManager.requestLocationUpdates(provider, 5000, 5,
						locationListener);
				
				return currentPosition;
	}
	protected void loadurl(WebView view, String url) {
		// TODO Auto-generated method stub
		
	}
			// goBack()表示返回webView的上一页面
			public boolean onKeyDown(int keyCoder, KeyEvent event) {
			if (wv.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK) {
			wv.goBack();
			return true;
			}
			return false;
			}
	protected void onDestroy() {
		super.onDestroy();
		if (locationManager != null) {
			// 关闭程序时将监听器移除
			locationManager.removeUpdates(locationListener);
		}
	}

	// locationListener中其他3个方法新手不太用得到，笔者在此也不多说了，有兴趣的可以自己去了解一下
	LocationListener locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location location) {
			// 更新当前设备的位置信息
			showLocation(location);
		}
	};

	// 显示经纬度信息
	private void showLocation(final Location location) {
		 currentPosition = "latitude is " + location.getLatitude() + "\n"
				+ "longitude is " + location.getLongitude();
		//positionTextView.setText(currentPosition);
	}

}