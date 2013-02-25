/**
 * 
 */
package com.gmail.charleszq.picorner.offline;

import java.io.IOException;
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
import com.gmail.charleszq.picorner.SPUtil;

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
	private static final int REMOVE_OFFLINE_PHOTOS_MSG_ID = 100003;
	private static final int EXPORT_OFFLINE_PHOTOS_MSG_ID = 100004;

	public static final int ADD_OFFLINE_PARAM = 1;
	public static final int REMOVE_OFFLINE_PARAM = 2;
	public static final int REFRESH_OFFLINE_PARAM = 3;
	public static final int DOWNLOAD_OFFLINE_PARAM = 4;
	public static final int DELETE_OFFLINE_PHOTO_PARAM = 5;
	public static final int EXPORT_OFFLINE_PHOTO_PARAM = 6;

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
		// check if offline is enabled or not
		boolean offlineEnabled = SPUtil.isOfflineEnabled(this);
		boolean isDownloadWhenCharging = SPUtil
				.isDownloadingWhenChargingEnabled(this);
		if (!offlineEnabled)
			return;

		// get the caller information to see if the call comes from battery
		// charging
		String caller = intent
				.getStringExtra(IOfflineViewParameter.OFFLINE_INVOKER_INTENT_KEY);
		if (BatteryStatusReceiver.class.getName().equals(caller)
				&& !isDownloadWhenCharging) {
			if (BuildConfig.DEBUG)
				Log.d(TAG, "user disabled the setting: download when charging."); //$NON-NLS-1$
			return;
		}

		// process
		IOfflineViewParameter param = (IOfflineViewParameter) intent
				.getSerializableExtra(IOfflineViewParameter.OFFLINE_PARAM_INTENT_KEY);
		int actionType = intent
				.getIntExtra(
						IOfflineViewParameter.OFFLINE_PARAM_INTENT_ADD_REMOVE_REFRESH_KEY,
						ADD_OFFLINE_PARAM);
		if (param == null) {
			downlaodPhotos(param, false);
		} else {
			switch (actionType) {
			case ADD_OFFLINE_PARAM:
				manageRepository(param, true);
				break;
			case REMOVE_OFFLINE_PARAM:
				manageRepository(param, false);
				break;
			case REFRESH_OFFLINE_PARAM:
				downlaodPhotos(param, false);
				break;
			case DOWNLOAD_OFFLINE_PARAM:
				downlaodPhotos(param, true);
				break;
			case DELETE_OFFLINE_PHOTO_PARAM:
				removeCachedPhotos(param);
				break;
			case EXPORT_OFFLINE_PHOTO_PARAM:
				String foldername = intent
						.getStringExtra(IOfflineViewParameter.OFFLINE_EXPORT_FOLDER_NAME_KEY);
				if (foldername == null) {
					Log.e(TAG, "missing folder name."); //$NON-NLS-1$
					foldername = param.getTitle();
				}
				boolean overwrite = intent.getBooleanExtra(IOfflineViewParameter.OFFLINE_EXPORT_OVERWRITE_KEY, false);
				exportPhotos(param, foldername, overwrite );
				break;
			}
		}
	}

	/**
	 * Exports the photos to a folder inside 'picorner'
	 * 
	 * @param param
	 * @param foldername
	 */
	private void exportPhotos(IOfflineViewParameter param, String foldername, boolean overwrite ) {
		IOfflinePhotoCollectionProcessor p = param
				.getPhotoCollectionProcessor();
		String msg = getString(R.string.msg_offline_exporting);
		sendNotification(EXPORT_OFFLINE_PHOTOS_MSG_ID, msg);
		msg = getString(R.string.msg_offline_export_photos_error);
		try {
			int count = p.exportCachedPhotos(this, param, foldername, overwrite );
			msg = getString(R.string.msg_offline_export_photos);
			msg = String.format(msg, foldername);
			msg = count + " " + msg; //$NON-NLS-1$
		} catch (IOException e) {

		}
		sendNotification(EXPORT_OFFLINE_PHOTOS_MSG_ID, msg);
	}

	/**
	 * Removes all the cached photos for the given <code>param</code>
	 * 
	 * @param param
	 */
	private void removeCachedPhotos(IOfflineViewParameter param) {
		IOfflinePhotoCollectionProcessor p = param
				.getPhotoCollectionProcessor();
		int count = p.removeCachedPhotos(this, param);
		if (count == -1) {
			sendNotification(REMOVE_OFFLINE_PHOTOS_MSG_ID,
					getString(R.string.msg_offline_delete_photos_minus_one));
		} else {
			String msg = getString(R.string.msg_offline_delete_photos);
			msg = String.format(msg, count);
			sendNotification(REMOVE_OFFLINE_PHOTOS_MSG_ID, msg);
		}
	}

	/**
	 * 
	 * @param offline
	 * @param redownload
	 *            <code>true</code> to download photos even there is no update
	 *            on server; <code>false</code> otherwise.
	 */
	private void downlaodPhotos(IOfflineViewParameter offline,
			boolean redownload) {

		// check network connection type
		ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork == null) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "not network available."); //$NON-NLS-1$
			}
			return;
		}
		boolean isWifiOnly = SPUtil.isOfflineWifiOnly(this);
		boolean isConnected = activeNetwork.isConnectedOrConnecting();
		boolean isWifi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
		if (!isConnected) {
			if (BuildConfig.DEBUG)
				Log.d(TAG,
						"network is not connected, don't start the offline download process."); //$NON-NLS-1$
			return;
		} else {
			if (isWifiOnly && !isWifi) {
				if (BuildConfig.DEBUG)
					Log.d(TAG,
							"wifi only offline download, but the network is not wifi."); //$NON-NLS-1$
				return;
			}
		}

		// nofify user in status bar.
		sendNotification(DOWNLOAD_NOTIF_ID,
				getString(R.string.msg_offline_downloading));

		List<IOfflineViewParameter> params = OfflineControlFileUtil
				.getExistingOfflineParameters(this);
		if (params == null) {
			Log.w(TAG, "repository file not found."); //$NON-NLS-1$
			return;
		}

		if (offline != null) {
			if (params.contains(offline)) {
				int pos = params.indexOf(offline);
				offline = params.get(pos);
				((AbstractOfflineParameter) offline).setLastUpdateTime(System
						.currentTimeMillis());
				processOfflineParameter(offline, true, redownload);
			} else {
				// not enabled, just return
				return;
			}
		} else {
			for (IOfflineViewParameter param : params) {
				this.processOfflineParameter(param, false, redownload);
			}
		}
		try {
			OfflineControlFileUtil.saveRepositoryControlFile(this, params);
		} catch (Exception e) {
			if (BuildConfig.DEBUG)
				Log.w(TAG,
						"error to save offline control file: " + e.getMessage()); //$NON-NLS-1$
		}
	}

	@SuppressWarnings("deprecation")
	private void sendNotification(int id, String msg) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(msg);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(id, mBuilder.getNotification());
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
				.getExistingOfflineParameters(this);
		if (params == null) {
			params = new ArrayList<IOfflineViewParameter>();
		}

		if (add) {
			if (!params.contains(param))
				params.add(0, param);
		} else {
			params.remove(param);
		}
		try {
			OfflineControlFileUtil.saveRepositoryControlFile(this, params);
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
			boolean doitnow, boolean redownload) {
		if (doitnow || longerThanFiveHours(param)) {
			// do it.
			IOfflinePhotoCollectionProcessor p = param
					.getPhotoCollectionProcessor();
			((AbstractOfflineParameter) param).setLastUpdateTime(System
					.currentTimeMillis());
			p.process(this, param, redownload);
		}
	}

	private boolean longerThanFiveHours(IOfflineViewParameter param) {
		long lastUpdateTime = param.getLastUpdateTime();
		if (lastUpdateTime == 0) {
			if (BuildConfig.DEBUG)
				Log.d(TAG,
						"the offline parameter has no last update time saved."); //$NON-NLS-1$
			return true;
		}

		long delta = System.currentTimeMillis() - lastUpdateTime;
		boolean ret = delta > 5 * 60 * 60 * 1000;
		if (BuildConfig.DEBUG)
			Log.d(TAG, "longer than 5 hours? " + Boolean.toString(ret)); //$NON-NLS-1$
		return ret;
	}

	@Override
	public void onDestroy() {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(DOWNLOAD_NOTIF_ID);
		super.onDestroy();
	}

}
