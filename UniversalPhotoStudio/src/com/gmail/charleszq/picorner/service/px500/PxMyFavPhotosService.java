/**
 * 
 */
package com.gmail.charleszq.picorner.service.px500;

import java.util.List;

import com.github.yuyang226.j500px.J500px;
import com.github.yuyang226.j500px.photos.GlobalFeatures;
import com.github.yuyang226.j500px.photos.ImageSize;
import com.github.yuyang226.j500px.photos.Photo;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.utils.J500pxHelper;
import com.gmail.charleszq.picorner.utils.ModelUtils;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PxMyFavPhotosService implements IPhotoService {

	private String mUserId;
	private String mToken;
	private String mSecret;

	/**
	 * constructor.
	 */
	public PxMyFavPhotosService(String token, String secret, String userId) {
		this.mUserId = userId;
		this.mToken = token;
		this.mSecret = secret;
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
		J500px px = J500pxHelper.getJ500pxAuthedInstance(mToken, mSecret);
		List<Photo> photos = px.getPhotosInterface().getUserPhotos(
				GlobalFeatures.USER_FAVORITES, mUserId, null, null, null, null,
				ImageSize.LARGEST, pageNo + 1, pageSize);
		return ModelUtils.convertPx500Photos(photos);
	}

}
