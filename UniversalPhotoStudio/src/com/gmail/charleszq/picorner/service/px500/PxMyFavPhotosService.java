/**
 * 
 */
package com.gmail.charleszq.picorner.service.px500;

import java.util.List;

import com.github.yuyang226.j500px.J500px;
import com.github.yuyang226.j500px.photos.GlobalFeatures;
import com.github.yuyang226.j500px.photos.ImageSize;
import com.github.yuyang226.j500px.photos.Photo;
import com.github.yuyang226.j500px.photos.PhotoCategory;
import com.github.yuyang226.j500px.photos.SearchSort;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.utils.ModelUtils;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PxMyFavPhotosService extends AbstractPxPhotoListService {

	private String mUserId;

	/**
	 * constructor.
	 */
	public PxMyFavPhotosService(String token, String secret, String userId) {
		super(token, secret);
		this.mUserId = userId;
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
		J500px px = getJ500px();
		List<Photo> photos = px.getPhotosInterface().getUserPhotos(
				GlobalFeatures.USER_FAVORITES,
				mUserId,
				null,
				SearchSort.CREATED_AT,
				PhotoCategory.Uncategorized.equals(mPhotoCategory) ? null
						: mPhotoCategory, null,
				new ImageSize[] { ImageSize.LARGE, ImageSize.LARGEST },
				pageNo + 1, pageSize);
		return ModelUtils.convertPx500Photos(photos);
	}

}
