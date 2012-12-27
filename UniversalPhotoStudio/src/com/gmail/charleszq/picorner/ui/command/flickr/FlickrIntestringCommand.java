/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.flickr;

import android.content.Context;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.flickr.FlickrInterestingPhotosService;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;

/**
 * 
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class FlickrIntestringCommand extends PhotoListCommand {

	/**
	 * @param context
	 */
	public FlickrIntestringCommand(Context context) {
		super(context);
	}

	@Override
	public String getLabel() {
		return mContext.getResources().getString(R.string.f_interesting_photos);
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.f_interest;
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			if (mCurrentPhotoService == null) {
				mCurrentPhotoService = new FlickrInterestingPhotosService();
			}
			return mCurrentPhotoService;
		}
		return super.getAdapter(adapterClass);
	}

	@Override
	public String getDescription() {
		return mContext.getString(R.string.cd_flickr_interesting);
	}

}
