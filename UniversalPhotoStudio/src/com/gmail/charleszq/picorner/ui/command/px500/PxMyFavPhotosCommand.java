/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.px500;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.px500.PxMyFavPhotosService;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PxMyFavPhotosCommand extends AbstractPx500PhotoListCommand {

	/**
	 * @param context
	 */
	public PxMyFavPhotosCommand(Context context) {
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
		return mContext.getString(R.string.f_my_fav);
	}

	@Override
	public String getDescription() {
		return mContext.getString(R.string.cd_500px_my_favs);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			PicornerApplication app = (PicornerApplication) ((Activity) mContext)
					.getApplication();
			return new PxMyFavPhotosService(app.getPx500OauthToken(),
					app.getPx500OauthTokenSecret(), app.getPxUserProfile()
							.getUserId());
		}
		return super.getAdapter(adapterClass);
	}
}
