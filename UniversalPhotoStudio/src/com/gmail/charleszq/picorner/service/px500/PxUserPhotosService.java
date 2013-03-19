package com.gmail.charleszq.picorner.service.px500;

import java.util.List;

import com.github.yuyang226.j500px.J500px;
import com.github.yuyang226.j500px.photos.GlobalFeatures;
import com.github.yuyang226.j500px.photos.ImageSize;
import com.github.yuyang226.j500px.photos.Photo;
import com.github.yuyang226.j500px.photos.PhotoCategory;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.utils.ModelUtils;

public class PxUserPhotosService extends AbstractPxPhotoListService {

	private String mUserId;

	public PxUserPhotosService(String userId) {
		super();
		this.mUserId = userId;
	}

	public PxUserPhotosService(String token, String secret, String userId) {
		super(token, secret);
		this.mUserId = userId;
	}

	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		J500px px = getJ500px();
		List<Photo> photos = px.getPhotosInterface().getUserPhotos(
				GlobalFeatures.USER,
				mUserId,
				null,
				null,
				PhotoCategory.Uncategorized.equals(mPhotoCategory) ? null
						: mPhotoCategory, null,
				new ImageSize[] { ImageSize.LARGE, ImageSize.LARGEST },
				pageNo + 1, pageSize);
		return ModelUtils.convertPx500Photos(photos);
	}

}
