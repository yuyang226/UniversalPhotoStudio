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
		}
	}
}
