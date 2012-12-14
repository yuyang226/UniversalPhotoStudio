/**
 * 
 */
package com.gmail.charleszq.ups.utils;

import org.jinstagram.AdvancedInstagram;
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

	public AdvancedInstagram getInstagram() {
		AdvancedInstagram ig = new AdvancedInstagram(
				IConstants.INSTAGRAM_CLIENT_ID);
		return ig;
	}

	public AdvancedInstagram getAuthedInstagram(Token token) {
		AdvancedInstagram ig = new AdvancedInstagram(token);
		return ig;
	}
}
