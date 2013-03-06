/**
 * 
 */
package com.gmail.charleszq.picorner.service.px500;

import java.util.List;

import com.github.yuyang226.j500px.J500px;
import com.github.yuyang226.j500px.photos.ImageSize;
import com.github.yuyang226.j500px.photos.Photo;
import com.github.yuyang226.j500px.photos.PhotosInterface;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.utils.ModelUtils;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class Px500SearchService extends AbstractPxPhotoListService {

	private String mTerm, mTag;

	public Px500SearchService() {
		super();
	}

	public Px500SearchService(String token, String secret) {
		super(token, secret);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.service.IPhotoService#getPhotos(int,
	 * int)
	 */
	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		J500px px = this.getJ500px();
		PhotosInterface pi = px.getPhotosInterface();
		List<Photo> photos = pi.searchPhotos(mTerm, mTag, false,
				ImageSize.LARGEST, pageNo + 1, pageSize);
		return ModelUtils.convertPx500Photos(photos);
	}

	public void setSearchCondition(String term, String tag) {
		this.mTerm = term;
		this.mTag = tag;
	}

}
