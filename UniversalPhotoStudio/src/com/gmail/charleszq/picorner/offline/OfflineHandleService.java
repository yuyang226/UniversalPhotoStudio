/**
 * 
 */
package com.gmail.charleszq.picorner.offline;

import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

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
			return;
		}
		Log.d(TAG, param.toString());
		List<IOfflineViewParameter> params = OfflineControlFileUtil
				.getExistingOfflineParameters();

		if (isAdd) {
			if (!params.contains(param))
				params.add(param);
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
			OfflineControlFileUtil.save(params);
		} catch (Exception e) {
			Log.w(TAG, e.getMessage());
		}

		if (isAdd) {
			// TODO start the task/service to fetch server information.
			processOfflineParameter(param, true);
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
			if (p != null) {
				p.process(this,param);
			} else {
				// should not happen
				Log.e(TAG, "no processor for this offline parameter?"); //$NON-NLS-1$
				throw new IllegalArgumentException(
						"no processor for this offline parameter."); //$NON-NLS-1$
			}
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
}
