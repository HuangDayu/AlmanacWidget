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
		// �������ļ�������assets/fonts/Ŀ¼�£�����Typeface����
		Typeface typeFace = Typeface.createFromAsset(getAssets(),
				"fonts/sao.ttf");
		// Ӧ������
		//textView.setTypeface(typeFace);
		
		// ��ȡ���п��õ�λ���ṩ��
		// ���GPS�����þ���GPS��GPS��������������
		// �������õ�����µ���Toast��ʾ�û�
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
		// ʹ��getLastKnownLocation�Ϳ��Ի�ȡ����¼��ǰλ����Ϣ��Location������
		// ������showLocation()��ʾ��ǰ�豸��λ����Ϣ
		// requestLocationUpdates��������λ�ü�����
		// �˴���������ʱ����Ϊ5�룬��������5��
		// Ҳ����˵ÿ��5�����ÿ�ƶ�5�ף�locationListener�л����һ��λ����Ϣ
		Location location = locationManager.getLastKnownLocation(provider);
		if (location != null) {

			showLocation(location);
		}
		locationManager.requestLocationUpdates(provider, 5000, 5,
				locationListener);
	}
	public String  getGps(){
		// ��ȡ���п��õ�λ���ṩ��
				// ���GPS�����þ���GPS��GPS��������������
				// �������õ�����µ���Toast��ʾ�û�
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
				// ʹ��getLastKnownLocation�Ϳ��Ի�ȡ����¼��ǰλ����Ϣ��Location������
				// ������showLocation()��ʾ��ǰ�豸��λ����Ϣ
				// requestLocationUpdates��������λ�ü�����
				// �˴���������ʱ����Ϊ5�룬��������5��
				// Ҳ����˵ÿ��5�����ÿ�ƶ�5�ף�locationListener�л����һ��λ����Ϣ
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
			// goBack()��ʾ����webView����һҳ��
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
			// �رճ���ʱ���������Ƴ�
			locationManager.removeUpdates(locationListener);
		}
	}

	// locationListener������3���������ֲ�̫�õõ��������ڴ�Ҳ����˵�ˣ�����Ȥ�Ŀ����Լ�ȥ�˽�һ��
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
			// ���µ�ǰ�豸��λ����Ϣ
			showLocation(location);
		}
	};

	// ��ʾ��γ����Ϣ
	private void showLocation(final Location location) {
		 currentPosition = "latitude is " + location.getLatitude() + "\n"
				+ "longitude is " + location.getLongitude();
		//positionTextView.setText(currentPosition);
	}

}