/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.px500;

import android.content.Context;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.px500.Px500UpcomingPhotosService;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PxUpcomingPhotosCommand extends AbstractPx500PhotoListCommand {

	/**
	 * @param context
	 */
	public PxUpcomingPhotosCommand(Context context) {
		super(context);
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_px500_upcoming;
	}

	@Override
	public String getLabel() {
		return mContext.getString(R.string.px_upcoming);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			String token = getAuthToken();
			if (token == null) {
				return new Px500UpcomingPhotosService();
			} else {
				return new Px500UpcomingPhotosService(token,
						getAuthTokenSecret());
			}
		}
		return super.getAdapter(adapterClass);
	}

	@Override
	public String getDescription() {
		return mContext.getString(R.string.cd_500px_upcoming);
	}

}
