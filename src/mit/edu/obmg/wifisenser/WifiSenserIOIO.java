package mit.edu.obmg.wifisenser;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.IOIOFactory;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import ioio.lib.util.android.IOIOService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import android.widget.ToggleButton;

public class WifiSenserIOIO extends IOIOService {
	final String TAG = "WiFiSenserIOIO";
	NotificationManager nm;

	private WifiSenserIOIO mWifiSenserIOIO;

	private DigitalOutput led_;

	  int level; 
	  
	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "IOIO setup");
		super.onStart(intent, startId);

		  
		  level = intent.getExtras().getInt("level");	  
		  
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		if (intent != null && intent.getAction() != null
				&& intent.getAction().equals("stop")) {
			// User clicked the notification. Need to stop the service.
			nm.cancel(0);
			stopSelf();
		} else {
			// Service starting. Create a notification.
			Notification notification = new Notification(
					R.drawable.ic_launcher, "IOIO service running",
					System.currentTimeMillis());
			notification
					.setLatestEventInfo(this, "IOIO Service", "Click to stop",
							PendingIntent.getService(this, 0, new Intent(
									"stop", null, this, this.getClass()), 0));
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			nm.notify(0, notification);
		}
	}

	@Override
	protected IOIOLooper createIOIOLooper() {
		return new BaseIOIOLooper() {

			private Vibration vibThread;

			@Override
			protected void setup() throws ConnectionLostException,
					InterruptedException {
				led_ = ioio_.openDigitalOutput(0, true);

				vibThread = new Vibration(ioio_);
				vibThread.start();
				
				Log.i(TAG, "IOIO setup");
			}

			@Override
			public void loop() throws ConnectionLostException,
					InterruptedException {
				

			}
		};
	}
	
	class Vibration extends Thread {

		private IOIO ioio_;
		private boolean run_ = true;
		int vibPin_;
		int threadNum_;
		float inTemp_;
		float _freq = level;

		public Vibration(IOIO ioio) throws InterruptedException {
			ioio_ = ioio;
		}

		@Override
		public void run() {
			Log.d(TAG, "Thread [" + getName() + "] is running.");

			while (run_) {
				try {
					while (true) {
						float rate = map(level, //Sensor value
								(float) -100, 	// min Sensor Value(),
								(float) 0, 		// max Sensor Value(),
								(float) 1000, 	// min output Value(),
								(float) 10);	// max output Value(),

						led_.write(true);
						sleep((long) 100);
						led_.write(false);
						sleep((int) rate);

					}
				} catch (ConnectionLostException e) {
				} catch (Exception e) {
					Log.e(TAG, "Unexpected exception caught", e);
					ioio_.disconnect();
					break;
				} finally {
					try {
						ioio_.waitForDisconnect();
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}
		
		
	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		stopSelf();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	float map(float x, float in_min, float in_max, float out_min, float out_max) {
		if (x < in_min)
			return out_min;
		else if (x > in_max)
			return out_max;
		else
			return (x - in_min) * (out_max - out_min) / (in_max - in_min)
					+ out_min;
	}
}
