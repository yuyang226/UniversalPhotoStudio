/**
 * 
 */
package com.gmail.charleszq.ups.ui.command.ig;

import android.content.Context;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.service.IPhotoService;
import com.gmail.charleszq.ups.service.ig.InstagramPopularsService;
import com.gmail.charleszq.ups.ui.command.PhotoListCommand;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class InstagramPopularsCommand extends PhotoListCommand {

	/**
	 * @param context
	 */
	public InstagramPopularsCommand(Context context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_ig_popular;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mContext.getString(R.string.ig_popular);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			if (mCurrentPhotoService == null) {
				mCurrentPhotoService = new InstagramPopularsService();
			}
			return mCurrentPhotoService;
		}
		return super.getAdapter(adapterClass);
	}
}
