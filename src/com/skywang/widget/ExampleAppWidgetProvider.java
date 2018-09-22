package com.skywang.widget;

import android.R.layout;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.provider.Telephony.MmsSms.PendingMessages;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.io.File;
import java.text.SimpleDateFormat;
import com.skywang.widget.MyBazi;
import com.skywang.widget.R;
import com.thinkpage.lib.api.TPCity;
import com.thinkpage.lib.api.TPListeners;
import com.thinkpage.lib.api.TPWeatherManager;
import com.thinkpage.lib.api.TPWeatherNow;

/*
 * @author : skywang <wangkuiwu@gmail.com>
 * description : �ṩApp Widget
 */

public class ExampleAppWidgetProvider extends AppWidgetProvider {
	private static final String TAG = "ExampleAppWidgetProvider";
	// ����ũ�������
	MyLunar mylunar = new MyLunar(null);
	private boolean DEBUG = false;
	private boolean mService;
	// ����ExampleAppWidgetService�����Ӧ��action
	private final Intent EXAMPLE_SERVICE_INTENT = new Intent("android.appwidget.action.EXAMPLE_APP_WIDGET_SERVICE");
	// ���� widget �Ĺ㲥��Ӧ��action
	private final String ACTION_UPDATE_ALL = "com.skywang.widget.UPDATE_ALL";
	/** Notification��ID */
	int notifyId = 100;
	private int units;
	private Vibrator vibrator;
	// ���� widget ��id��HashSet��ÿ�½�һ�� widget ����Ϊ�� widget ����һ�� id��
	private static Set idsSet = new HashSet();
	// ��ť��Ϣ
	private static final int BUTTON_SHOW = 1;
	private static final int BUTTON_SHOW_1 = 2;
	// ͼƬ����
	private static final int[] ARR_IMAGES = { R.drawable.earth_24, R.drawable.earth_3, R.drawable.earth_6,
			R.drawable.earth_9, R.drawable.earth_12, R.drawable.earth_15, R.drawable.earth_18, R.drawable.earth_21, };
	private static final int[] ARR_WORD = { R.drawable.imageview_dq_yaz, R.drawable.imagview_dq_feiz,
			R.drawable.imageview_dq_meiz };
	private static final int[] ARR_NOON = { R.drawable.imageview_noon_zaoshang, R.drawable.imageview_noon_zhongwu,
			R.drawable.imageview_noon_xiawu, R.drawable.imageview_noon_shangbanye, R.drawable.imageview_noon_xiabanye };

	/**
	 * onUpdate() �ڸ��� widget ʱ����ִ�У�
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// Log.d(TAG, "onUpdate(): appWidgetIds.length=" + appWidgetIds.length);

		// ÿ�� widget ������ʱ����Ӧ�Ľ�widget��id��ӵ�set��
		for (int appWidgetId : appWidgetIds) {
			idsSet.add(Integer.valueOf(appWidgetId));
		}
		prtSet();
	}

	/**
	 * �� widget ��������� ���� �� widget �Ĵ�С���ı�ʱ��������
	 */
	@SuppressLint("NewApi")
	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		// Log.d(TAG, "onAppWidgetOptionsChanged");
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
	}

	/***
	 * widget��ɾ��ʱ����
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// Log.d(TAG, "onDeleted(): appWidgetIds.length=" +
		// appWidgetIds.length);

		// �� widget ��ɾ��ʱ����Ӧ��ɾ��set�б����widget��id
		for (int appWidgetId : appWidgetIds) {
			idsSet.remove(Integer.valueOf(appWidgetId));
		}
		prtSet();

		super.onDeleted(context, appWidgetIds);
	}

	/***
	 * ��һ��widget������ʱ����
	 */
	@Override
	public void onEnabled(Context context) {
		// Log.d(TAG, "onEnabled");
		// �ڵ�һ�� widget ������ʱ����������
		context.startService(EXAMPLE_SERVICE_INTENT);
		MyToast.makeText(context, "��������", Toast.LENGTH_SHORT).show();
		super.onEnabled(context);
	}

	/***
	 * ���һ��widget��ɾ��ʱ����
	 */
	@Override
	public void onDisabled(Context context) {
		Log.d(TAG, "onDisabled");

		// �����һ�� widget ��ɾ��ʱ����ֹ����
		context.stopService(EXAMPLE_SERVICE_INTENT);

		super.onDisabled(context);
	}

	/***
	 * ���չ㲥�Ļص�����
	 * 
	 * @param context
	 * @param intent
	 */
	@Override
	public void onReceive(Context context, Intent intent) {

		final String action = intent.getAction();
		Log.d(TAG, "  : " + action);
		if (ACTION_UPDATE_ALL.equals(action)) {
			// �����¡��㲥
			updateAllAppWidgets(context, AppWidgetManager.getInstance(context), idsSet);
		} else if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
			// ����ť������㲥
			Uri data = intent.getData();
			int buttonId = Integer.parseInt(data.getSchemeSpecificPart());
			if (buttonId == BUTTON_SHOW) {
				// Log.d(TAG, "Button wifi clicked");
				MyToast_red.makeText(context, "���ũ����", Toast.LENGTH_SHORT).show();
			}
			if (buttonId == BUTTON_SHOW_1) {
				MyToast_red.makeText(context, "���ʱ�䣡", Toast.LENGTH_SHORT).show();
			}
		}
		super.onReceive(context, intent);
	}

	/***
	 * �������е� widget
	 * 
	 * @param context
	 * @param appWidgetManager
	 * @param set
	 */
	@SuppressLint("NewApi")
	private void updateAllAppWidgets(Context context, AppWidgetManager appWidgetManager, Set set) {

		Log.d(TAG, "updateAllAppWidgets(): size=" + set.size());

		// widget ��id
		int appID;
		// �����������ڱ������б����widget��id
		Iterator it = set.iterator();

		while (it.hasNext()) {
			appID = ((Integer) it.next()).intValue();
			// �����ȡһ��ͼƬ
			int index = (new java.util.Random().nextInt(ARR_IMAGES.length));

			if (DEBUG)
				Log.d(TAG, "onUpdate(): index=" + index);
			// ��ȡ example_appwidget.xml ��Ӧ��RemoteViews
			RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.example_appwidget);
			//RemoteViews remoteView2 = new RemoteViews(context.getPackageName(), R.layout.keyguardlayout);
			//remoteView2.setTextViewText(R.id.tv_lunar_ymd2, mylunar.getBigMoth());
			Calendar hour = Calendar.getInstance();
			int HH = hour.get(Calendar.HOUR_OF_DAY);
			int MM = hour.get(Calendar.MINUTE);
			int SS = hour.get(Calendar.SECOND);
			if (MM == 59 && SS >= 50 && SS <= 59) {
				remoteView.setTextColor(R.id.tv_datetime, Color.WHITE);
				if (SS == 59) {
					remoteView.setTextColor(R.id.tv_datetime, Color.BLUE);
				}
			} else if (MM == 0 && SS == 0) {
				remoteView.setTextColor(R.id.tv_datetime, Color.BLUE);
				MyToast.makeText(context, "����ʱ��:" + mylunar.getHMStime(), Toast.LENGTH_LONG).show();
				remoteView.setTextColor(R.id.tv_datetime, Color.WHITE);
				remoteView.setTextViewText(R.id.tv_time, mylunar.getRequestFocus()); // timedata()��ϸ�Ĺ���ʱ��
				open_vibrator(context);
			}else if(  MM % 10 == 0 && SS==0){
				// �����ֲ���textview����ϸ����Ϣ
				remoteView.setTextViewText(R.id.tv_time, mylunar.getRequestFocus()); // timedata()��ϸ�Ĺ���ʱ��
			}else if (MM == 0 && SS < 10) {
				remoteView.setTextColor(R.id.tv_datetime, Color.WHITE);
			} else {
				remoteView.setTextColor(R.id.tv_datetime, Color.BLACK);
			}
			String CAL = mylunar.getCyclicaAndgetLunar();// ��ʾ[��֧�� ]ũ�� ʱ��
			// remoteView.setTextViewTextSize(R.id.textView_lunar_bazi, units, 32);
			remoteView.setTextViewText(R.id.textView_lunar_bazi, mylunar.getshengchenbazi());// ��������
			remoteView.setTextViewText(R.id.tv_lunar_ymd, mylunar.getBigMoth());
			remoteView.setTextViewText(R.id.tv_getConstellation, getJieJiaRi());

			if( ! mylunar.get_24_SolarTerms().equals("") ){
				if (SS % 3 == 0) {
					remoteView.setTextColor(R.id.tv_time, Color.BLACK);
				} else if (SS % 3 == 1) {
					remoteView.setTextColor(R.id.tv_time, Color.BLUE);
				}else {
					remoteView.setTextColor(R.id.tv_time, Color.RED);
				}
			}else{
				remoteView.setTextColor(R.id.tv_time, Color.BLACK);
			}
			
			if (getJieJiaRi().contains("��") && getJieJiaRi().contains("��")&& !getJieJiaRi().contains("��")) {
				if (SS % 3 == 0) {
					remoteView.setTextColor(R.id.tv_getConstellation, Color.GREEN);
				} else if (SS % 3 == 1) {
					remoteView.setTextColor(R.id.tv_getConstellation, Color.BLUE);
				}else {
					remoteView.setTextColor(R.id.tv_getConstellation, Color.RED);
				}
			} else if (getJieJiaRi().contains("��") && getJieJiaRi().contains("��")) {
				if (SS % 2 == 0) {
					remoteView.setTextColor(R.id.tv_getConstellation, Color.GREEN);
				} else {
					remoteView.setTextColor(R.id.tv_getConstellation, Color.BLUE);
				}
			}  else if (getJieJiaRi().contains("��")) {
				if (SS % 2 == 0) {
					remoteView.setTextColor(R.id.tv_getConstellation, Color.CYAN);
				} else {
					remoteView.setTextColor(R.id.tv_getConstellation, Color.MAGENTA);
				}
			} else if (getJieJiaRi().contains("��")) {
				if (SS % 2 == 0) {
					remoteView.setTextColor(R.id.tv_getConstellation, Color.BLUE);
				} else {
					remoteView.setTextColor(R.id.tv_getConstellation, Color.RED);
				}
			} else if (!getJieJiaRi().contains("��")) {// �ж��Ƿ�������
				if (SS % 2 == 0) {
					remoteView.setTextColor(R.id.tv_getConstellation, Color.GRAY);
				} else {
					remoteView.setTextColor(R.id.tv_getConstellation, Color.BLUE);
				}
			} else {
				remoteView.setTextColor(R.id.tv_getConstellation, Color.BLACK);
			}
			if (getJieJiaRi().length() > 5) {
				remoteView.setTextViewTextSize(R.id.tv_getConstellation, units, 28);
			}else if (getJieJiaRi().length() > 7) {
				remoteView.setTextViewTextSize(R.id.tv_getConstellation, units, 26);
			}
			remoteView.setTextViewText(R.id.tv_datetime, mylunar.getHMStime());
			// ���õ����ť��Ӧ��PendingIntent���������ťʱ�����͹㲥��
			// remoteView.setOnClickPendingIntent(R.id.tv_lunar_ymd,
			// getPendingIntent(context, BUTTON_SHOW));
			// Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);//��ת������ʱ��Ľ���
			// Intent intent2 =  getPackageManager().getLaunchIntentForPackage("com.android.deskclock");
			Intent MyDeskclock = new Intent(context, MyDeskclock.class);// ��ת������ʱ��Ľ���
			PendingIntent pendingintent = PendingIntent.getActivity(context, 0, MyDeskclock, 0);// PendingIntent.FLAG_UPDATE_CURRENT
			remoteView.setOnClickPendingIntent(R.id.tv_datetime, pendingintent);// BUTTON_SHOW_1

			Intent MyWeather = new Intent(context, MyWeather.class);
			PendingIntent PIMyWeather = PendingIntent.getActivity(context, 0, MyWeather, 0);// PendingIntent.FLAG_UPDATE_CURRENT
			remoteView.setOnClickPendingIntent(R.id.tv_time, PIMyWeather);

			Intent MyKeyguard = new Intent(context, MyKuaikai.class);// ��ת������ʱ��Ľ���
			PendingIntent PIMyKeyguard = PendingIntent.getActivity(context, 0, MyKeyguard, 0);// PendingIntent.FLAG_UPDATE_CURRENT
			remoteView.setOnClickPendingIntent(R.id.textView_lunar_bazi, PIMyKeyguard);// BUTTON_SHOW_1

			Intent Kuaikai = new Intent(context, MyCalendar.class);// ��ת������ʱ��Ľ���
			PendingIntent PIKuaikai = PendingIntent.getActivity(context, 0, Kuaikai, 0);// PendingIntent.FLAG_UPDATE_CURRENT
			remoteView.setOnClickPendingIntent(R.id.tv_lunar_ymd, PIKuaikai);// BUTTON_SHOW_1
			// ������ʾͼƬ
			// remoteView.setImageViewResource(R.id.iv_sun, ARR_IMAGES[mimageviewSun()]);
			// remoteView.setImageViewResource(R.id.iv_word, ARR_WORD[mimageviewWord()]);
			// remoteView.setImageViewResource(R.id.iv_noon,ARR_NOON[mimageviewNoon()]);
			// ���� widget
			appWidgetManager.updateAppWidget(appID, remoteView);
		}
	}

	/***
	 * �ŻؼҼ��յ���Ϣ
	 * 
	 * @return
	 */
	public String getJieJiaRi() {
		String str = "", str1 = "", str2 = "", str3 = "";
		Lunar lunar = new Lunar(null);
		if( ! mylunar.getBirthday().equals("")){
			str = mylunar.getBirthday();
		}else{
		  if (lunar.isLeap() || lunar.isHoliday() || mylunar.isWeekday()) {
			if (lunar.isLeap()) {
				str1 = "��";
			}
			if (lunar.isHoliday()) {
				if (lunar.isLeap()) {
					str2 = "|��";
				} else {
					str2 = "��";
				}
			}
			if (mylunar.isWeekday()) {
				if (lunar.isLeap() || lunar.isHoliday()) {
					str3 = "|��";
				} else {
					str3 = "��";
				}
			}
			str = "[" + str1 + str2 + str3 + "]";
		}
		if (lunar.getTermString().length() > 0 || lunar.isSFestival() || lunar.isLFestival()) {
			if (lunar.isLFestival() && lunar.getLunarMonthString().substring(0, 1).equals("��") == false) {// �Ҳ�������
				str += lunar.getLFestivalName();
				if (lunar.getTermString().length() > 0) {
					str += "|" + lunar.getTermString();
				}
				if (lunar.isSFestival()) {
					str += "|" + lunar.getSFestivalName();
				}
			} else if (lunar.getTermString().length() > 0) {
				str += lunar.getTermString();
				if (lunar.isSFestival()) {
					str += "|" + lunar.getSFestivalName();
				}
			} else if (lunar.isSFestival()) {
				str += lunar.getSFestivalName();
			}
		} else if (lunar.getLunarDayString().equals("��һ")) {
			str += mylunar.getLunarMonthString() + "��";
		} else {
			str += mylunar.getConstellation();

		}
	}
		return str;
	}

	/***
	 * ����̫��view
	 * 
	 * @return
	 */
	public int mimageviewSun() {
		Calendar mcalendar = Calendar.getInstance();
		double HourAndMinute = mcalendar.get(Calendar.HOUR_OF_DAY) + mcalendar.get(Calendar.MINUTE) * 0.01;
		if (HourAndMinute >= 0 && HourAndMinute < 3) {
			return 0;
		} else if (HourAndMinute >= 3 && HourAndMinute < 6) {
			return 1;
		} else if (HourAndMinute >= 6 && HourAndMinute < 9) {
			return 2;
		} else if (HourAndMinute >= 9 && HourAndMinute < 12) {
			return 3;
		} else if (HourAndMinute >= 12 && HourAndMinute < 15) {
			return 4;
		} else if (HourAndMinute >= 15 && HourAndMinute < 18) {
			return 5;
		} else if (HourAndMinute >= 18 && HourAndMinute < 21) {
			return 6;
		} else if (HourAndMinute >= 21 && HourAndMinute < 24) {
			return 7;
		} else {
			return 0;
		}
	}

	/***
	 * ���õ���view
	 * 
	 * @return
	 */
	public int mimageviewWord() {
		Calendar mcalendar = Calendar.getInstance();
		double HourAndMinute = mcalendar.get(Calendar.HOUR_OF_DAY) + mcalendar.get(Calendar.MINUTE) * 0.01;
		if (HourAndMinute > 6 && HourAndMinute <= 12) {
			return 0;
		} else if (HourAndMinute > 12 && HourAndMinute <= 18) {
			return 1;
		} else if (HourAndMinute > 18 && HourAndMinute <= 24) {
			return 2;
		} else if (HourAndMinute > 0 && HourAndMinute <= 6) {
			return 2;
		} else {
			return 0;
		}
	}

	/***
	 * ��������view
	 * 
	 * @return
	 */
	public int mimageviewNoon() {
		Calendar mcalendar = Calendar.getInstance();
		double HourAndMinute = mcalendar.get(Calendar.HOUR_OF_DAY) + mcalendar.get(Calendar.MINUTE) * 0.01;
		if (HourAndMinute >= 6 && HourAndMinute < 11) {
			return 0;
		} else if (HourAndMinute >= 11 && HourAndMinute < 15) {
			return 1;
		} else if (HourAndMinute >= 15 && HourAndMinute < 17) {
			return 2;
		} else if (HourAndMinute >= 17 && HourAndMinute < 24) {
			return 3;
		} else if (HourAndMinute >= 0 && HourAndMinute < 6) {
			return 4;
		} else {
			return 0;
		}
	}

	/***
	 * ������ �� �����һ��Ԫ�ر�ʾ�ȴ��೤ʱ��������� ֮���ǿ��� �͹ر��𶯵ĳ���ʱ�䣬��λΪ����
	 * �ڶ����������ظ���ʱ��pattern�е�����������Ϊ-1��ʾ���ظ��� 2��ʾ�ظ�������
	 * 
	 * @param v
	 */
	public void open_vibrator(Context context) {
		// vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		// vibrator.vibrate(3500);//���������𶯲�ָ�������𶯵�ʱ��
		// vibrator.cancel();
		// vibrator = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
		// vibrator.vibrate(new long[]{50,50,50,100,50},-1);
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		if (notification == null)
			return;
		Ringtone r = RingtoneManager.getRingtone(context, notification);
		r.play();
	}

	private PendingIntent getPendingIntent(Context context, int buttonId) {
		Intent intent = new Intent();
		intent.setClass(context, ExampleAppWidgetProvider.class);
		intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		intent.setData(Uri.parse("custom:" + buttonId));
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		return pi;
	}

	// �����ã�����set
	private void prtSet() {
		if (DEBUG) {
			int index = 0;
			int size = idsSet.size();
			Iterator it = idsSet.iterator();
			Log.d(TAG, "total:" + size);
			while (it.hasNext()) {
				Log.d(TAG, index + " -- " + ((Integer) it.next()).intValue());
			}
		}
	}
}
