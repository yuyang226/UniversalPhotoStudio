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
import com.gmail.charleszq.picorner.utils.IConstants;
import com.gmail.charleszq.picorner.utils.ModelUtils;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public class Px500FreshTodayPhotosService implements IPhotoService {

	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		J500px px = new J500px(IConstants.PX500_CONSUMER_KEY);
		List<Photo> photos = px.getPhotosInterface().getPhotos(GlobalFeatures.FRESH_TODAY, 
				null, null, null, ImageSize.LARGEST, null, pageNo + 1, pageSize);
		return ModelUtils.convertPx500Photos(photos);
	}

}
