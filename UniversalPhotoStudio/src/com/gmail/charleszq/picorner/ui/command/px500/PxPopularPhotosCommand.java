/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.px500;

import android.content.Context;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.px500.Px500PopularPhotosService;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PxPopularPhotosCommand extends AbstractPx500PhotoListCommand {

	/**
	 * @param context
	 */
	public PxPopularPhotosCommand(Context context) {
		super(context);
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_px500_popular;
	}

	@Override
	public String getLabel() {
		return mContext.getString(R.string.ig_popular);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			String token = getAuthToken();
			Px500PopularPhotosService s = null;
			if (token != null) {
				s = new Px500PopularPhotosService(token,
						getAuthTokenSecret());
			} else {
				s = new Px500PopularPhotosService();
			}
			s.setPhotoCategory(mPhotoCategory);
			return s;
		}
		return super.getAdapter(adapterClass);
	}

	@Override
	public String getDescription() {
		return mContext.getString(R.string.cd_500px_popular);
	}

}
