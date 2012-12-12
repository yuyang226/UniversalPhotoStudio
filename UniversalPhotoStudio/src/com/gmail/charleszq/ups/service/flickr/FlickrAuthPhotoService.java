/**
 * 
 */
package com.gmail.charleszq.ups.service.flickr;


/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public abstract class FlickrAuthPhotoService extends
		FlickrAbstractPhotoListService {

	protected String mUserId;
	protected String mAuthToken;
	protected String mTokenSecret;

	public FlickrAuthPhotoService(String userId, String token, String secret) {
		super();
		this.mAuthToken = token;
		this.mTokenSecret = secret;
		this.mUserId = userId;
	}

}
