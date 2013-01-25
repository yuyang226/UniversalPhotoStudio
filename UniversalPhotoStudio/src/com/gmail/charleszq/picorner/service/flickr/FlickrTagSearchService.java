/**
 * 
 */
package com.gmail.charleszq.picorner.service.flickr;

import com.gmail.charleszq.picorner.model.FlickrTagSearchParameter;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.gmail.charleszq.picorner.utils.ModelUtils;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.PhotosInterface;
import com.googlecode.flickrjandroid.photos.SearchParameters;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public class FlickrTagSearchService extends FlickrAbstractPhotoListService {
	
	private SearchParameters mSearchParameter;

	/**
	 * 
	 */
	public FlickrTagSearchService() {
		super();
		mSearchParameter =  new SearchParameters();
		mSearchParameter.setExtras( this.mExtras );
		mSearchParameter.setMachineTagMode("any" ); //$NON-NLS-1$
		mSearchParameter.setSort(SearchParameters.DATE_POSTED_DESC);
	}
	
	public void setSearchParameter( FlickrTagSearchParameter param ) {
		String[] ts = param.getTags().split(" "); //$NON-NLS-1$
		mSearchParameter.setTags(ts);
		mSearchParameter.setTagMode(param.getSearchMode().toString());
		mSearchParameter.setHasGeo(param.isHasGeoInformation());
		mSearchParameter.setInCommons(param.isSearchInCommon());
	}

	/* (non-Javadoc)
	 * @see com.gmail.charleszq.picorner.service.IPhotoService#getPhotos(int, int)
	 */
	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		Flickr f = FlickrHelper.getInstance().getFlickr();
		PhotosInterface pi = f.getPhotosInterface();
		PhotoList list = pi.search(mSearchParameter, pageSize, pageNo + 1);
		return ModelUtils.convertFlickrPhotoList(list);
	}

}
