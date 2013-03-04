/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.px500;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.px500.PxUserPhotosService;

/**
 * @author charleszq
 * 
 */
public class Px500MyPhotosCommand extends AbstractPx500PhotoListCommand {

	/**
	 * @param context
	 */
	public Px500MyPhotosCommand(Context context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_px500_you;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mContext.getString(R.string.ig_my_photos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.picorner.ui.command.PhotoListCommand#execute(java
	 * .lang.Object[])
	 */
	@Override
	public boolean execute(Object... params) {
		// first need to check if my 500px user id is saved or not.
		PicornerApplication app = (PicornerApplication) ((Activity) mContext)
				.getApplication();
		Author a = app.getPxUserProfile();
		if (a == null) {
			fetchUserProfile(params);
			return true;
		} else {
			return super.execute(params);
		}
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			PicornerApplication app = (PicornerApplication) ((Activity) mContext)
					.getApplication();
			Author a = app.getPxUserProfile();
			String token = getAuthToken();
			PxUserPhotosService s = null;
			if (token == null) {
				s = new PxUserPhotosService(a.getUserId());
			} else {
				s = new PxUserPhotosService(token, getAuthTokenSecret(),
						a.getUserId());
			}
			s.setPhotoCategory(mPhotoCategory);
			return s;
		}
		if( adapterClass == ActionBar.class ) {
			return Boolean.FALSE.toString();
		}
		return super.getAdapter(adapterClass);
	}

	@Override
	public String getDescription() {
		return mContext.getString(R.string.cd_500px_my_photos);
	}

}
