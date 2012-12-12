/**
 * 
 */
package com.gmail.charleszq.ups.service.flickr;

import java.util.HashSet;
import java.util.Set;

import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.service.IPhotoService;
import com.googlecode.flickrjandroid.photos.Extras;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public abstract class FlickrAbstractPhotoListService implements IPhotoService {
	
	protected Set<String> mExtras = null;
	
	public FlickrAbstractPhotoListService() {
		mExtras = new HashSet<String>();
		mExtras.add(Extras.URL_S);
		mExtras.add(Extras.URL_L);
		mExtras.add(Extras.OWNER_NAME);
		mExtras.add(Extras.GEO);
		mExtras.add(Extras.TAGS);
		mExtras.add(Extras.VIEWS);
	}

	/* (non-Javadoc)
	 * @see com.gmail.charleszq.ups.service.IPhotoService#getPhotos(int, int)
	 */
	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
