/**
 * 
 */
package com.gmail.charleszq.picorner.service.flickr;

import java.util.HashSet;
import java.util.Set;

import com.gmail.charleszq.picorner.service.IPhotoService;
import com.googlecode.flickrjandroid.photos.Extras;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public abstract class FlickrAbstractPhotoListService implements IPhotoService {
	
	protected String TAG = getClass().getSimpleName();
	
	protected Set<String> mExtras = null;
	
	public FlickrAbstractPhotoListService() {
		mExtras = new HashSet<String>();
		mExtras.add(Extras.URL_S);
		mExtras.add(Extras.URL_L);
		mExtras.add(Extras.OWNER_NAME);
		mExtras.add(Extras.GEO);
		mExtras.add(Extras.TAGS);
		mExtras.add(Extras.VIEWS);
		mExtras.add(Extras.DESCRIPTION);
	}

}
