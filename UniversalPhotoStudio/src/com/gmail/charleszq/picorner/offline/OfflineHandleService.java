/**
 * 
 */
package com.gmail.charleszq.picorner.offline;

import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gmail.charleszq.picorner.BuildConfig;
import com.gmail.charleszq.picorner.R;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class OfflineHandleService extends IntentService {

	/**
	 * The log tag
	 */
	private static final String TAG = OfflineHandleService.class
			.getSimpleName();

	private static final int DOWNLOAD_NOTIF_ID = 100001;

	/**
	 * @param name
	 */
	public OfflineHandleService() {
		super("PiCorner offline view photo service"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		IOfflineViewParameter param = (IOfflineViewParameter) intent
				.getSerializableExtra(IOfflineViewParameter.OFFLINE_PARAM_INTENT_KEY);
		boolean isAdd = Boolean
				.parseBoolean(intent
						.getStringExtra(IOfflineViewParameter.OFFLINE_PARAM_INTENT_ADD_REMOVE_KEY));
		if (param == null) {
			Log.d(TAG, "charging, start the download process."); //$NON-NLS-1$
			downlaodPhotos();
			return;
		} else {
			manageRepository(param, isAdd);
		}
	}

	private void downlaodPhotos() {

		// check network connection type
		ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork.isConnectedOrConnecting();
		boolean isWifi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
		if (!isConnected || !isWifi) {
			Log.d(TAG, "Not wifi, don't start the offline download process."); //$NON-NLS-1$
			return;
		}

		if (BuildConfig.DEBUG) {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					this).setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle("Offline View") //$NON-NLS-1$
					.setContentText("Downloading photos for offline view"); //$NON-NLS-1$
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(DOWNLOAD_NOTIF_ID,
					mBuilder.getNotification());
		}
		List<IOfflineViewParameter> params = OfflineControlFileUtil
				.getExistingOfflineParameters();
		if (params == null) {
			Log.w(TAG, "repository file not found."); //$NON-NLS-1$
			return;
		}
		for (IOfflineViewParameter param : params) {
			this.processOfflineParameter(param, true);
		}
	}

	/**
	 * Manages the repository.
	 * 
	 * @param param
	 * @param add
	 */
	private void manageRepository(IOfflineViewParameter param, boolean add) {
		Log.d(TAG, param.toString());
		List<IOfflineViewParameter> params = OfflineControlFileUtil
				.getExistingOfflineParameters();
		if (params == null) {
			params = new ArrayList<IOfflineViewParameter>();
		}

		if (add) {
			if (!params.contains(param))
				params.add(0, param);
			else {
				// use the saved version so we can check the time interval.
				int index = params.indexOf(param);
				if (index != -1) {
					param = params.get(index);
				}
			}
		} else {
			params.remove(param);
		}
		try {
			if (!params.isEmpty())
				OfflineControlFileUtil.save(params);
		} catch (Exception e) {
			Log.w(TAG, e.getMessage());
		}
	}

	/**
	 * Starts the process to get the photo collection information, and download
	 * large image
	 * 
	 * @param param
	 */
	private void processOfflineParameter(IOfflineViewParameter param,
			boolean doitnow) {
		if (doitnow || longerThanAday(param)) {
			// do it.
			IOfflinePhotoCollectionProcessor p = param
					.getPhotoCollectionProcessor();
			((AbstractOfflineParameter) param).setLastUpdateTime(System
					.currentTimeMillis());
			p.process(this, param);
		}
	}

	private boolean longerThanAday(IOfflineViewParameter param) {
		long lastUpdateTime = param.getLastUpdateTime();
		if (lastUpdateTime == 0) {
			// just got from UI, and this could not happen.
			return false;
		}

		long delta = System.currentTimeMillis() - lastUpdateTime;
		// TODO later need to put this into preference.
		return delta > 24 * 60 * 60 * 1000;
	}

	@Override
	public void onDestroy() {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(DOWNLOAD_NOTIF_ID);
		super.onDestroy();
	}

}
