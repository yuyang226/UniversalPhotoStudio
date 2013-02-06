/**
 * 
 */
package com.gmail.charleszq.picorner;

import com.gmail.charleszq.picorner.utils.IConstants;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;

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
		return getSharedPreferences(ctx).getString(IConstants.FLICKR_USER_NAME,
				null);
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

	public static OAuth loadFlickrSavedOAuth(Context ctx) {
		String userId = SPUtil.getFlickrUserId(ctx);
		String userName = SPUtil.getFlickrUserName(ctx);
		String token = SPUtil.getFlickrAuthToken(ctx);
		String tokenSecret = SPUtil.getFlickrAuthTokenSecret(ctx);
		if (userId == null || token == null || tokenSecret == null) {
			return null;
		}
		OAuth oauth = new com.googlecode.flickrjandroid.oauth.OAuth();
		oauth.setToken(new OAuthToken(token, tokenSecret));
		User user = new User();
		user.setId(userId);
		user.setRealName(userName);
		oauth.setUser(user);
		RequestContext.getRequestContext().setOAuth(oauth);
		return oauth;
	}

	public static String getPx500TokenSecret(Context ctx) {
		return getSharedPreferences(ctx).getString(
				IConstants.PX500_TOKEN_SECRET_KEY, null);
	}

	/**
	 * Used to check if user log into px500 or not, currently.
	 * 
	 * @return
	 */
	public static String getPx500OauthToken(Context ctx) {
		return getSharedPreferences(ctx).getString(
				IConstants.PX500_OAUTH_TOKEN_KEY, null);
	}

	public static String getPx500OauthTokenSecret(Context ctx) {
		return getSharedPreferences(ctx).getString(
				IConstants.PX500_OAUTH_TOKEN_SECRET_KEY, null);
	}
}
