/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.px500;

import android.content.Context;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.px500.Px500EditorsPhotosService;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PxEditorsPhotosCommand extends AbstractPx500PhotoListCommand {

	/**
	 * @param context
	 */
	public PxEditorsPhotosCommand(Context context) {
		super(context);
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_px500_editors;
	}

	@Override
	public String getLabel() {
		return mContext.getString(R.string.px_editors);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			Px500EditorsPhotosService s = null;
			if (getAuthToken() != null) {
				s = new Px500EditorsPhotosService(getAuthToken(),
						getAuthTokenSecret());
			} else {
				s = new Px500EditorsPhotosService();
			}
			s.setPhotoCategory(mPhotoCategory);
			return s;
		}
		return super.getAdapter(adapterClass);
	}

	@Override
	public String getDescription() {
		return mContext.getString(R.string.cd_500px_editor);
	}
}
