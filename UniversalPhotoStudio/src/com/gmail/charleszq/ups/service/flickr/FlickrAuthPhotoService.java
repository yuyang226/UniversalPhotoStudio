/**
 * 
 */
package com.gmail.charleszq.ups.service.flickr;

import java.util.HashSet;
import java.util.Set;

import com.gmail.charleszq.ups.service.IPhotoService;
import com.googlecode.flickrjandroid.photos.Extras;


/**
 * @author Charles(charleszq@gmail.com)
 *
 */
public abstract class FlickrAuthPhotoService implements IPhotoService {
	
	protected String mUserId;
	protected String mAuthToken;
	protected String mTokenSecret;
	
	protected Set<String> mExtras;
	
	public FlickrAuthPhotoService(String userId, String token, String secret) {
		this.mAuthToken = token;
		this.mTokenSecret = secret;
		this.mUserId = userId;
		
		mExtras = new HashSet<String>();
		mExtras.add(Extras.URL_S);
		mExtras.add(Extras.URL_L);
	}
	

}
