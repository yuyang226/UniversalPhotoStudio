/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.px500;

import android.content.Context;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.px500.Px500FreshTodayPhotosService;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PxFreshTodayPhotosCommand extends AbstractPx500PhotoListCommand {

	/**
	 * @param context
	 */
	public PxFreshTodayPhotosCommand(Context context) {
		super(context);
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_px500_fresh;
	}

	@Override
	public String getLabel() {
		return mContext.getString(R.string.px_fresh_today);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			String token = getAuthToken();
			Px500FreshTodayPhotosService s = null;
			if (token == null) {
				s = new Px500FreshTodayPhotosService();
			} else {
				s = new Px500FreshTodayPhotosService(token,
						getAuthTokenSecret());
			}
			s.setPhotoCategory(mPhotoCategory);
			return s;
		}
		return super.getAdapter(adapterClass);
	}

	@Override
	public String getDescription() {
		return mContext.getString(R.string.cd_500px_fresh_today);
	}

}
