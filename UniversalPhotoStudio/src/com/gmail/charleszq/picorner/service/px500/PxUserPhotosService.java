package com.gmail.charleszq.picorner.service.px500;

import java.util.List;

import com.github.yuyang226.j500px.J500px;
import com.github.yuyang226.j500px.photos.ImageSize;
import com.github.yuyang226.j500px.photos.Photo;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.gmail.charleszq.picorner.utils.ModelUtils;

public class PxUserPhotosService implements IPhotoService {
	
	private String mUserId;

	public PxUserPhotosService(String userId ) {
		this.mUserId = userId;
	}

	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		J500px px = new J500px(IConstants.PX500_CONSUMER_KEY);
		List<Photo> photos = px.getPhotosInterface().getUserPhotos(null, mUserId, null, null, 
				null, null, ImageSize.LARGEST, pageNo + 1, pageSize);
		return ModelUtils.convertPx500Photos(photos);
	}

}
