/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.flickr;

import android.app.ActionBar;
import android.content.Context;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.SPUtil;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.flickr.FlickrUserPhotoStreamService;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class MyFlickrPhotosCommand extends PhotoListCommand {

	/**
	 * @param context
	 */
	public MyFlickrPhotosCommand(Context context) {
		super(context);
	}

	@Override
	public String getLabel() {
		return mContext.getResources().getString(R.string.f_my_photos);
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_flickr_my_photos;
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			if (mCurrentPhotoService == null) {
				String userId = SPUtil.getFlickrUserId(mContext);
				String token = SPUtil.getFlickrAuthToken(mContext);
				String secret = SPUtil.getFlickrAuthTokenSecret(mContext);
				mCurrentPhotoService = new FlickrUserPhotoStreamService(userId,token,secret);
			}
			return mCurrentPhotoService;
		}
		if( adapterClass == ActionBar.class ) {
			return Boolean.FALSE.toString();
		}
		return super.getAdapter(adapterClass);
	}
	
	@Override
	public String getDescription() {
		return mContext.getString(R.string.cd_flickr_my_photos);
	}

}
