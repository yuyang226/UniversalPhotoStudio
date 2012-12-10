/**
 * 
 */
package com.gmail.charleszq.ups.utils;

import org.jinstagram.Instagram;
import org.jinstagram.auth.model.Token;

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

	public Instagram getAuthedInstagram(Token token) {
		Instagram ig = new Instagram(token);
		return ig;
	}
}
