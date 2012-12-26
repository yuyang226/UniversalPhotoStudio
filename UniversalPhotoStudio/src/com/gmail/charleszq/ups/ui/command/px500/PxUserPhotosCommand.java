/**
 * 
 */
package com.gmail.charleszq.ups.ui.command.px500;

import android.content.Context;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.service.IPhotoService;
import com.gmail.charleszq.ups.service.px500.PxUserPhotosService;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PxUserPhotosCommand extends AbstractPx500PhotoListCommand {

	private String mUserId;

	/**
	 * @param context
	 */
	public PxUserPhotosCommand(Context context, String userId) {
		super(context);
		this.mUserId = userId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			return new PxUserPhotosService(mUserId);
		}
		return super.getAdapter(adapterClass);
	}
	
	@Override
	public String getDescription() {
		String s = mContext.getString(R.string.cd_500px_user_photos);
		return String.format(s, mUserId);
	}

}
