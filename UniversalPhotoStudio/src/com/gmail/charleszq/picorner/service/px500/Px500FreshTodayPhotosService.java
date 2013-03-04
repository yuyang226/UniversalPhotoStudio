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
import com.gmail.charleszq.picorner.utils.ModelUtils;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public class Px500FreshTodayPhotosService extends AbstractPxPhotoListService {

	public Px500FreshTodayPhotosService() {
		super();
	}

	public Px500FreshTodayPhotosService(String token, String secret) {
		super(token, secret);
	}

	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		J500px px = getJ500px();
		List<Photo> photos = px.getPhotosInterface().getPhotos(GlobalFeatures.FRESH_TODAY, 
				null, mPhotoCategory, null, new ImageSize[]{ImageSize.LARGEST}, null, false, false, false, pageNo + 1, pageSize);
		return ModelUtils.convertPx500Photos(photos);
	}

}
