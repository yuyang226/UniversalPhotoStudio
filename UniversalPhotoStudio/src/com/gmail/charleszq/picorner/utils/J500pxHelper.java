/**
 * 
 */
package com.gmail.charleszq.picorner.utils;

import javax.xml.parsers.ParserConfigurationException;

import com.github.yuyang226.j500px.J500px;
import com.github.yuyang226.j500px.oauth.OAuth;
import com.github.yuyang226.j500px.oauth.OAuthToken;
import com.github.yuyang226.j500px.oauth.RequestContext;

/**
 * @author charleszq
 * 
 */
public final class J500pxHelper {

	public static J500px getJ500pxInstance() {
		try {
			return new J500px(IConstants.PX500_CONSUMER_KEY,
					IConstants.PX500_CONSUMER_SECRET);
		} catch (ParserConfigurationException e) {
			return null;
		}
	}

	public static J500px getJ500pxAuthedInstance(String token, String secret) {
		try {
			J500px j500px = new J500px(IConstants.PX500_CONSUMER_KEY,
					IConstants.PX500_CONSUMER_SECRET);
			OAuth auth = new OAuth();
			auth.setToken(new OAuthToken(token, secret));
			RequestContext.getRequestContext().setOAuth(auth);
			return j500px;
		} catch (ParserConfigurationException e) {
			return null;
		}
	}

}
