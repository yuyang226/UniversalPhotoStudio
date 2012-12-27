/**
 * 
 */
package com.gmail.charleszq.picorner.service.px500;

import java.util.List;

import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.gmail.charleszq.picorner.utils.ModelUtils;
import com.gmail.charleszq.px500.PX500;
import com.gmail.charleszq.px500.model.Photo;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public class Px500FreshTodayPhotosService implements IPhotoService {

	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		PX500 px = new PX500(IConstants.PX500_CONSUMER_KEY);
		List<Photo> photos = px.getFreshTodayPhotos(pageSize, pageNo+1);
		return ModelUtils.convertPx500Photos(photos);
	}

}
