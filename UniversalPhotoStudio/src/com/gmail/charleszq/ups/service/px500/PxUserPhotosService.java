package com.gmail.charleszq.ups.service.px500;

import java.util.List;

import com.gmail.charleszq.px500.PX500;
import com.gmail.charleszq.px500.model.Photo;
import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.service.IPhotoService;
import com.gmail.charleszq.ups.utils.IConstants;
import com.gmail.charleszq.ups.utils.ModelUtils;

public class PxUserPhotosService implements IPhotoService {
	
	private String mUserId;

	public PxUserPhotosService(String userId ) {
		this.mUserId = userId;
	}

	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {

		PX500 px = new PX500(IConstants.PX500_CONSUMER_KEY);
		List<Photo> photos = px.getUserPublicPhotos(mUserId, pageSize, pageNo+1);
		return ModelUtils.convertPx500Photos(photos);
	}

}
