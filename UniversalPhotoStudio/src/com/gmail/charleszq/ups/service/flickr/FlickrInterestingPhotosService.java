/**
 * 
 */
package com.gmail.charleszq.ups.service.flickr;

import java.util.HashSet;
import java.util.Set;

import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.service.IPhotoService;
import com.gmail.charleszq.ups.utils.FlickrHelper;
import com.gmail.charleszq.ups.utils.ModelUtils;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.interestingness.InterestingnessInterface;
import com.googlecode.flickrjandroid.photos.Extras;
import com.googlecode.flickrjandroid.photos.PhotoList;

/**
 * @author charleszq
 * 
 */
public class FlickrInterestingPhotosService implements IPhotoService {
	
	/**
	 * The extra information for flicke interfaces
	 */
	private Set<String> mExtra = null;

	public FlickrInterestingPhotosService() {
		mExtra = new HashSet<String>();
		mExtra.add(Extras.GEO);
		mExtra.add(Extras.URL_S);
		mExtra.add(Extras.URL_L);
		mExtra.add(Extras.OWNER_NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.service.IPhotoListService#getPhotos(int,
	 * int)
	 */
	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		Flickr f = FlickrHelper.getInstance().getFlickr();
		InterestingnessInterface iif = f.getInterestingnessInterface();
		PhotoList list = iif.getList((String) null, mExtra, pageSize, pageNo + 1);
		return ModelUtils.convertFlickrPhotoList(list);
	}
}
