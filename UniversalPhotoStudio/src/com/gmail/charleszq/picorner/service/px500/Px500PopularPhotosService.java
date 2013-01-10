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
public class Px500PopularPhotosService implements IPhotoService {

	private String mToken;
	private String mSecret;

	public Px500PopularPhotosService() {

	}

	public Px500PopularPhotosService(String token, String secret) {
		this.mToken = token;
		this.mSecret = secret;
	}

	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		J500px px = mToken == null ? J500pxHelper.getJ500pxInstance()
				: J500pxHelper.getJ500pxAuthedInstance(mToken, mSecret);
		List<Photo> photos = px.getPhotosInterface().getPhotos(
				GlobalFeatures.POPULAR, null, null, null, ImageSize.LARGEST,
				null, pageNo + 1, pageSize);
		return ModelUtils.convertPx500Photos(photos);
	}

}
