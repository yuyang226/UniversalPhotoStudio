/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.px500;

import android.content.Context;

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
		return R.drawable.ic_action_500px_myfav;
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
			
			return new PxMyFavPhotosService(getAuthToken(),
					getAuthTokenSecret(), getUserId());
		}
		return super.getAdapter(adapterClass);
	}
}
