/**
 * 
 */
package com.gmail.charleszq.picorner;

import com.gmail.charleszq.picorner.utils.IConstants;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public final class SPUtil {

	private static final SharedPreferences getSharedPreferences(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(
				IConstants.DEF_PREF_NAME, Context.MODE_APPEND);
		return sp;
	}

	public static String getFlickrAuthToken(Context ctx) {
		return getSharedPreferences(ctx).getString(IConstants.FLICKR_TOKEN,
				null);
	}

	public static String getFlickrAuthTokenSecret(Context ctx) {
		return getSharedPreferences(ctx).getString(
				IConstants.FLICKR_TOKEN_SECRENT, null);
	}

	public static String getFlickrUserId(Context ctx) {
		return getSharedPreferences(ctx).getString(IConstants.FLICKR_USER_ID,
				null);
	}
	
	public static String getFlickrUserName(Context ctx) {
		return getSharedPreferences(ctx).getString(IConstants.FLICKR_USER_NAME, null);
	}
	
	public static boolean isFlickrAuthed(Context ctx) {
		String userId = getFlickrUserId(ctx);
		return userId != null;
	}

	public static boolean isOfflineEnabled(Context ctx) {
		return getSharedPreferences(ctx).getBoolean(
				IConstants.PREF_ENABLE_OFFLINE, false);
	}

	public static int getMaxPhotoSize(Context ctx) {
		String size = getSharedPreferences(ctx).getString(
				IConstants.PREF_OFFLINE_MAX_PHOTO_GRID_SIZE,
				Integer.toString(IConstants.DEF_MAX_TOTAL_PHOTOS));
		return Integer.parseInt(size);
	}

	public static boolean isOfflineWifiOnly(Context ctx) {
		return getSharedPreferences(ctx).getBoolean(
				IConstants.PREF_OFFLINE_WIFI_ONLY, true);
	}

	public static boolean isDownloadingWhenChargingEnabled(Context ctx) {
		return getSharedPreferences(ctx).getBoolean(
				IConstants.PREF_DOWNLOAD_WHEN_CHARGING, true);
	}
}
