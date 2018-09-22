package com.skywang.widget;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

/*
 * @author : skywang <wangkuiwu@gmail.com>
 * description : �����Ը���AppWidget�ķ���
 */

public class ExampleAppWidgetService extends Service {

	private static final String TAG = "ExampleAppWidgetService";

	// ���� widget �Ĺ㲥��Ӧ��action
	private final String ACTION_UPDATE_ALL = "com.skywang.widget.UPDATE_ALL";
	// �����Ը��� widget ������
	private static final int UPDATE_TIME = 1000;
	// �����Ը��� widget ���߳�
	private UpdateThread mUpdateThread;
	private Context mContext;
	// �������ڵļ���
	private int count = 0;

	@Override
	public void onCreate() {

		// �����������߳�UpdateThread
		mUpdateThread = new UpdateThread();
		mUpdateThread.start();

		mContext = this.getApplicationContext();

		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// �ж��̣߳��������̡߳�
		if (mUpdateThread != null) {
			mUpdateThread.interrupt();
		}

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/*
	 * ����ʼʱ��������startService()ʱ��onStartCommand()��ִ�С� onStartCommand() �������Ҫ���ã�
	 * (01) �� appWidgetIds ��ӵ�����sAppWidgetIds�� (02) �����߳�
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		super.onStartCommand(intent, flags, startId);

		return START_STICKY;// START_STICKY��service��kill�����Զ�
	}	
	private class UpdateThread extends Thread {

		@Override
		public void run() {
			super.run();
			try {
				// count = 0;
				while (true) {
					// Log.d(TAG, "run ... count:"+count);
					/// count++;

					Intent updateIntent = new Intent(ACTION_UPDATE_ALL);
					mContext.sendBroadcast(updateIntent);

					Thread.sleep(UPDATE_TIME);
				}
			} catch (InterruptedException e) {
				// �� InterruptedException ������whileѭ��֮�⣬��ζ���׳�
				// InterruptedException �쳣ʱ����ֹ�̡߳�
				e.printStackTrace();
			}
		}
	}
}
