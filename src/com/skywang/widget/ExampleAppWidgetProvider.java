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
 * description : 提供App Widget
 */

public class ExampleAppWidgetProvider extends AppWidgetProvider {
	private static final String TAG = "ExampleAppWidgetProvider";
	// 创建农历类对象
	MyLunar mylunar = new MyLunar(null);
	private boolean DEBUG = false;
	private boolean mService;
	// 启动ExampleAppWidgetService服务对应的action
	private final Intent EXAMPLE_SERVICE_INTENT = new Intent("android.appwidget.action.EXAMPLE_APP_WIDGET_SERVICE");
	// 更新 widget 的广播对应的action
	private final String ACTION_UPDATE_ALL = "com.skywang.widget.UPDATE_ALL";
	/** Notification的ID */
	int notifyId = 100;
	private int units;
	private Vibrator vibrator;
	// 保存 widget 的id的HashSet，每新建一个 widget 都会为该 widget 分配一个 id。
	private static Set idsSet = new HashSet();
	// 按钮信息
	private static final int BUTTON_SHOW = 1;
	private static final int BUTTON_SHOW_1 = 2;
	// 图片数组
	private static final int[] ARR_IMAGES = { R.drawable.earth_24, R.drawable.earth_3, R.drawable.earth_6,
			R.drawable.earth_9, R.drawable.earth_12, R.drawable.earth_15, R.drawable.earth_18, R.drawable.earth_21, };
	private static final int[] ARR_WORD = { R.drawable.imageview_dq_yaz, R.drawable.imagview_dq_feiz,
			R.drawable.imageview_dq_meiz };
	private static final int[] ARR_NOON = { R.drawable.imageview_noon_zaoshang, R.drawable.imageview_noon_zhongwu,
			R.drawable.imageview_noon_xiawu, R.drawable.imageview_noon_shangbanye, R.drawable.imageview_noon_xiabanye };

	/**
	 * onUpdate() 在更新 widget 时，被执行，
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// Log.d(TAG, "onUpdate(): appWidgetIds.length=" + appWidgetIds.length);

		// 每次 widget 被创建时，对应的将widget的id添加到set中
		for (int appWidgetId : appWidgetIds) {
			idsSet.add(Integer.valueOf(appWidgetId));
		}
		prtSet();
	}

	/**
	 * 当 widget 被初次添加 或者 当 widget 的大小被改变时，被调用
	 */
	@SuppressLint("NewApi")
	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		// Log.d(TAG, "onAppWidgetOptionsChanged");
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
	}

	/***
	 * widget被删除时调用
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// Log.d(TAG, "onDeleted(): appWidgetIds.length=" +
		// appWidgetIds.length);

		// 当 widget 被删除时，对应的删除set中保存的widget的id
		for (int appWidgetId : appWidgetIds) {
			idsSet.remove(Integer.valueOf(appWidgetId));
		}
		prtSet();

		super.onDeleted(context, appWidgetIds);
	}

	/***
	 * 第一个widget被创建时调用
	 */
	@Override
	public void onEnabled(Context context) {
		// Log.d(TAG, "onEnabled");
		// 在第一个 widget 被创建时，开启服务
		context.startService(EXAMPLE_SERVICE_INTENT);
		MyToast.makeText(context, "开启服务", Toast.LENGTH_SHORT).show();
		super.onEnabled(context);
	}

	/***
	 * 最后一个widget被删除时调用
	 */
	@Override
	public void onDisabled(Context context) {
		Log.d(TAG, "onDisabled");

		// 在最后一个 widget 被删除时，终止服务
		context.stopService(EXAMPLE_SERVICE_INTENT);

		super.onDisabled(context);
	}

	/***
	 * 接收广播的回调函数
	 * 
	 * @param context
	 * @param intent
	 */
	@Override
	public void onReceive(Context context, Intent intent) {

		final String action = intent.getAction();
		Log.d(TAG, "  : " + action);
		if (ACTION_UPDATE_ALL.equals(action)) {
			// “更新”广播
			updateAllAppWidgets(context, AppWidgetManager.getInstance(context), idsSet);
		} else if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
			// “按钮点击”广播
			Uri data = intent.getData();
			int buttonId = Integer.parseInt(data.getSchemeSpecificPart());
			if (buttonId == BUTTON_SHOW) {
				// Log.d(TAG, "Button wifi clicked");
				MyToast_red.makeText(context, "点击农历！", Toast.LENGTH_SHORT).show();
			}
			if (buttonId == BUTTON_SHOW_1) {
				MyToast_red.makeText(context, "点击时间！", Toast.LENGTH_SHORT).show();
			}
		}
		super.onReceive(context, intent);
	}

	/***
	 * 更新所有的 widget
	 * 
	 * @param context
	 * @param appWidgetManager
	 * @param set
	 */
	@SuppressLint("NewApi")
	private void updateAllAppWidgets(Context context, AppWidgetManager appWidgetManager, Set set) {

		Log.d(TAG, "updateAllAppWidgets(): size=" + set.size());

		// widget 的id
		int appID;
		// 迭代器，用于遍历所有保存的widget的id
		Iterator it = set.iterator();

		while (it.hasNext()) {
			appID = ((Integer) it.next()).intValue();
			// 随机获取一张图片
			int index = (new java.util.Random().nextInt(ARR_IMAGES.length));

			if (DEBUG)
				Log.d(TAG, "onUpdate(): index=" + index);
			// 获取 example_appwidget.xml 对应的RemoteViews
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
				MyToast.makeText(context, "北京时间:" + mylunar.getHMStime(), Toast.LENGTH_LONG).show();
				remoteView.setTextColor(R.id.tv_datetime, Color.WHITE);
				remoteView.setTextViewText(R.id.tv_time, mylunar.getRequestFocus()); // timedata()详细的公历时间
				open_vibrator(context);
			}else if(  MM % 10 == 0 && SS==0){
				// 更新轮播的textview的详细的信息
				remoteView.setTextViewText(R.id.tv_time, mylunar.getRequestFocus()); // timedata()详细的公历时间
			}else if (MM == 0 && SS < 10) {
				remoteView.setTextColor(R.id.tv_datetime, Color.WHITE);
			} else {
				remoteView.setTextColor(R.id.tv_datetime, Color.BLACK);
			}
			String CAL = mylunar.getCyclicaAndgetLunar();// 显示[干支历 ]农历 时间
			// remoteView.setTextViewTextSize(R.id.textView_lunar_bazi, units, 32);
			remoteView.setTextViewText(R.id.textView_lunar_bazi, mylunar.getshengchenbazi());// 生辰八字
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
			
			if (getJieJiaRi().contains("休") && getJieJiaRi().contains("假")&& !getJieJiaRi().contains("座")) {
				if (SS % 3 == 0) {
					remoteView.setTextColor(R.id.tv_getConstellation, Color.GREEN);
				} else if (SS % 3 == 1) {
					remoteView.setTextColor(R.id.tv_getConstellation, Color.BLUE);
				}else {
					remoteView.setTextColor(R.id.tv_getConstellation, Color.RED);
				}
			} else if (getJieJiaRi().contains("休") && getJieJiaRi().contains("假")) {
				if (SS % 2 == 0) {
					remoteView.setTextColor(R.id.tv_getConstellation, Color.GREEN);
				} else {
					remoteView.setTextColor(R.id.tv_getConstellation, Color.BLUE);
				}
			}  else if (getJieJiaRi().contains("休")) {
				if (SS % 2 == 0) {
					remoteView.setTextColor(R.id.tv_getConstellation, Color.CYAN);
				} else {
					remoteView.setTextColor(R.id.tv_getConstellation, Color.MAGENTA);
				}
			} else if (getJieJiaRi().contains("假")) {
				if (SS % 2 == 0) {
					remoteView.setTextColor(R.id.tv_getConstellation, Color.BLUE);
				} else {
					remoteView.setTextColor(R.id.tv_getConstellation, Color.RED);
				}
			} else if (!getJieJiaRi().contains("座")) {// 判断是否是星座
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
			// 设置点击按钮对应的PendingIntent：即点击按钮时，发送广播。
			// remoteView.setOnClickPendingIntent(R.id.tv_lunar_ymd,
			// getPendingIntent(context, BUTTON_SHOW));
			// Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);//跳转到设置时间的界面
			// Intent intent2 =  getPackageManager().getLaunchIntentForPackage("com.android.deskclock");
			Intent MyDeskclock = new Intent(context, MyDeskclock.class);// 跳转到设置时间的界面
			PendingIntent pendingintent = PendingIntent.getActivity(context, 0, MyDeskclock, 0);// PendingIntent.FLAG_UPDATE_CURRENT
			remoteView.setOnClickPendingIntent(R.id.tv_datetime, pendingintent);// BUTTON_SHOW_1

			Intent MyWeather = new Intent(context, MyWeather.class);
			PendingIntent PIMyWeather = PendingIntent.getActivity(context, 0, MyWeather, 0);// PendingIntent.FLAG_UPDATE_CURRENT
			remoteView.setOnClickPendingIntent(R.id.tv_time, PIMyWeather);

			Intent MyKeyguard = new Intent(context, MyKuaikai.class);// 跳转到设置时间的界面
			PendingIntent PIMyKeyguard = PendingIntent.getActivity(context, 0, MyKeyguard, 0);// PendingIntent.FLAG_UPDATE_CURRENT
			remoteView.setOnClickPendingIntent(R.id.textView_lunar_bazi, PIMyKeyguard);// BUTTON_SHOW_1

			Intent Kuaikai = new Intent(context, MyCalendar.class);// 跳转到设置时间的界面
			PendingIntent PIKuaikai = PendingIntent.getActivity(context, 0, Kuaikai, 0);// PendingIntent.FLAG_UPDATE_CURRENT
			remoteView.setOnClickPendingIntent(R.id.tv_lunar_ymd, PIKuaikai);// BUTTON_SHOW_1
			// 设置显示图片
			// remoteView.setImageViewResource(R.id.iv_sun, ARR_IMAGES[mimageviewSun()]);
			// remoteView.setImageViewResource(R.id.iv_word, ARR_WORD[mimageviewWord()]);
			// remoteView.setImageViewResource(R.id.iv_noon,ARR_NOON[mimageviewNoon()]);
			// 更新 widget
			appWidgetManager.updateAppWidget(appID, remoteView);
		}
	}

	/***
	 * 放回家假日等信息
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
				str1 = "闰";
			}
			if (lunar.isHoliday()) {
				if (lunar.isLeap()) {
					str2 = "|假";
				} else {
					str2 = "假";
				}
			}
			if (mylunar.isWeekday()) {
				if (lunar.isLeap() || lunar.isHoliday()) {
					str3 = "|休";
				} else {
					str3 = "休";
				}
			}
			str = "[" + str1 + str2 + str3 + "]";
		}
		if (lunar.getTermString().length() > 0 || lunar.isSFestival() || lunar.isLFestival()) {
			if (lunar.isLFestival() && lunar.getLunarMonthString().substring(0, 1).equals("闰") == false) {// 且不是闰月
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
		} else if (lunar.getLunarDayString().equals("初一")) {
			str += mylunar.getLunarMonthString() + "月";
		} else {
			str += mylunar.getConstellation();

		}
	}
		return str;
	}

	/***
	 * 设置太阳view
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
	 * 设置地球view
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
	 * 设置月亮view
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
	 * 开启震动 类 数组第一个元素表示等待多长时间才启动震动 之后是开启 和关闭震动的持续时间，单位为毫秒
	 * 第二个参数：重复震动时在pattern中的索引，设置为-1表示不重复震动 2表示重复连词震动
	 * 
	 * @param v
	 */
	public void open_vibrator(Context context) {
		// vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		// vibrator.vibrate(3500);//启动持续震动并指定持续震动的时间
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

	// 调试用：遍历set
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
