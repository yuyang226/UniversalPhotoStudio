/**
 * 
 */
package com.gmail.charleszq.picorner.service.px500;

import com.github.yuyang226.j500px.J500px;
import com.github.yuyang226.j500px.photos.PhotoCategory;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.utils.J500pxHelper;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public abstract class AbstractPxPhotoListService implements IPhotoService {

	protected String mToken = null;
	protected String mSecret = null;
	
	protected PhotoCategory mPhotoCategory = PhotoCategory.Uncategorized;

	/**
	 * 
	 */
	public AbstractPxPhotoListService() {
	}

	public AbstractPxPhotoListService(String token, String secret) {
		mToken = token;
		mSecret = secret;
	}

	protected J500px getJ500px() {
		J500px px = null;
		if (mToken == null) {
			px = J500pxHelper.getJ500pxInstance();
		} else {
			px = J500pxHelper.getJ500pxAuthedInstance(mToken, mSecret);
		}
		return px;
	}
	
	public void setPhotoCategory(PhotoCategory category) {
		this.mPhotoCategory = category;
	}

}
