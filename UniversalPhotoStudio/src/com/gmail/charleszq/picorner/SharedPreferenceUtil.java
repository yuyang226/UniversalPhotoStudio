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
public final class SharedPreferenceUtil {

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
}
