/**
 * 
 */
package com.gmail.charleszq.picorner.utils;

/**
 * @author charleszq
 * 
 */
public interface IConstants {

	/**
	 * For each service call, what's the page size, by default, it's 10 times of
	 * the UI page size.
	 */
	static final int DEF_SERVICE_PAGE_SIZE = 50;
	static final int DEF_500PX_PAGE_SIZE = 20;
	static final int DEF_IG_PAGE_SIZE = 30;
	
	/**
	 * The image cache size.
	 */
	static final int IMAGE_CACHE_SIZE = 200000000; //200M
	
	/**
	 * Use this as the marker to say there is no more data, if server returns photos less than this,
	 * we say there is no more there.
	 */
	static final int DEF_MIN_PAGE_SIZE = 10;
	static final int DEF_MAX_TOTAL_PHOTOS = 300;

	static final String SD_CARD_FOLDER_NAME = "picorner"; //$NON-NLS-1$
	static final String SHARE_TEMP_FILE_NAME = "picornershare.png"; //$NON-NLS-1$
	static final String SHARE_INTENT_TMP_FILE_PREFIX = "sharetmp_"; //$NON-NLS-1$

	/**
	 * the flickr oauth scheme
	 */
	static final String ID_SCHEME = "ups-flickr-oauth"; //$NON-NLS-1$
	static final String DEF_PREF_NAME = "picorner"; //$NON-NLS-1$

	/**
	 * Instagram oauth scheme, these constants cannot be modified, they are
	 * defined in the client app of instagram.
	 */
	static final String ID_IG_SCHEME = "schema"; //$NON-NLS-1$
	static final String IG_AUTHORITY = "upsigoauth"; //$NON-NLS-1$
	static final String IG_CALL_BACK_STR = "schema://upsigoauth"; //$NON-NLS-1$
	static final String IG_USER_ID = "ig.user.id"; //$NON-NLS-1$
	static final String IG_USER_BUDDY_ICON_URL = "ig.user.buddy.icon.url";  //$NON-NLS-1$
	static final String IG_AUTH_TOKEN = "ig.auth.token"; //$NON-NLS-1$
	static final String IG_AUTH_TOKEN_SECRET = "ig.auth.token.secret"; //$NON-NLS-1$
	static final String IG_AUTH_TOKEN_RAW_RES = "ig.auth.token.raw.response"; //$NON-NLS-1$

	// flickr
	static final String FLICKR_TOKEN_SECRENT = "token.secret"; //$NON-NLS-1$
	static final String FLICKR_TOKEN = "flickr.token"; //$NON-NLS-1$
	static final String FLICKR_USER_ID = "flickr.user.id"; //$NON-NLS-1$
	static final String FLICKR_USER_NAME = "flickr.user.name"; //$NON-NLS-1$

	// instagram
	static final String INSTAGRAM_CLIENT_ID = "2588f081b4c2432cbfcc5d27aef4fecb"; //$NON-NLS-1$
	static final String INSTAGRAM_CLIENT_SECRET = "76ab8f0afb954671bf28ec80fca75ad6"; //$NON-NLS-1$
	
	//
	static final String DETAIL_PAGE_PHOTO_ARG_KEY = "photo.frg.arg"; //$NON-NLS-1$
	
	//500px related constants
	static final String PX500_CONSUMER_KEY = "XLvGaTDTlDL7UH9njjxpc9VmpoSKYQwRHjV0Qujx"; //$NON-NLS-1$
	static final String PX500_OAUTH_CALLBACK_SCHEMA = "schemapx500"; //$NON-NLS-1$
	
	static final String ABOUT_FILE_FRG_ARG_KEY = "frg.file.name"; //$NON-NLS-1$
	static final String ABOUT_FILE_ENCODING_KEY = "frg.file.encoding"; //$NON-NLS-1$
	static final String ABOUT_FILE_DEFAULT_ENCODING = "utf-8"; //$NON-NLS-1$
	
	static final String ASSET_FOLDER = "file:///android_asset/"; //$NON-NLS-1$

}
