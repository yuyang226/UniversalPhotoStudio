/**
 * 
 */
package com.gmail.charleszq.ups.service.px500;

import java.util.List;

import com.gmail.charleszq.px500.PX500;
import com.gmail.charleszq.px500.model.Photo;
import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.service.IPhotoService;
import com.gmail.charleszq.ups.utils.IConstants;
import com.gmail.charleszq.ups.utils.ModelUtils;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public class Px500UpcomingPhotosService implements IPhotoService {

	/* (non-Javadoc)
	 * @see com.gmail.charleszq.ups.service.IPhotoService#getPhotos(int, int)
	 */
	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		PX500 px = new PX500(IConstants.PX500_CONSUMER_KEY);
		List<Photo> photos = px.getUpcomingPhotos(pageSize);
		return ModelUtils.convertPx500Photos(photos);
	}

}
