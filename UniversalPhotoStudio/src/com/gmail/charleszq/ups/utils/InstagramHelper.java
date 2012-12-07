/**
 * 
 */
package com.gmail.charleszq.ups.utils;

import org.jinstagram.Instagram;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public final class InstagramHelper {

	private static InstagramHelper mInstance = null;

	public static InstagramHelper getInstance() {
		if (mInstance == null) {
			mInstance = new InstagramHelper();
		}
		return mInstance;
	}

	private InstagramHelper() {

	}
	
	public Instagram getInstagram() {
		Instagram ig = new Instagram(IConstants.INSTAGRAM_CLIENT_ID);
		return ig;
	}
}
