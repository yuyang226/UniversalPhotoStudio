/**
 * 
 */
package com.gmail.charleszq.ups.ui.command.px500;

import android.content.Context;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.service.IPhotoService;
import com.gmail.charleszq.ups.service.px500.Px500UpcomingPhotosService;
import com.gmail.charleszq.ups.ui.command.PhotoListCommand;
import com.gmail.charleszq.ups.utils.IConstants;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PxUpcomingPhotosCommand extends PhotoListCommand {

	/**
	 * @param context
	 */
	public PxUpcomingPhotosCommand(Context context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_px_upcoming;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mContext.getString(R.string.px_upcoming);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			return new Px500UpcomingPhotosService();
		}
		if (adapterClass == Integer.class) {
			return IConstants.PX500_DEF_PAGE_SIZE;
		}
		return super.getAdapter(adapterClass);
	}

}
