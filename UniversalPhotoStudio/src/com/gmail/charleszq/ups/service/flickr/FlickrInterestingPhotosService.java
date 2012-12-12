/**
 * 
 */
package com.gmail.charleszq.ups.service.flickr;

import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.utils.FlickrHelper;
import com.gmail.charleszq.ups.utils.ModelUtils;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.interestingness.InterestingnessInterface;
import com.googlecode.flickrjandroid.photos.PhotoList;

/**
 * @author charleszq
 * 
 */
public class FlickrInterestingPhotosService extends
		FlickrAbstractPhotoListService {

	public FlickrInterestingPhotosService() {
		super();
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
		PhotoList list = iif.getList((String) null, mExtras, pageSize,
				pageNo + 1);
		return ModelUtils.convertFlickrPhotoList(list);
	}
}
