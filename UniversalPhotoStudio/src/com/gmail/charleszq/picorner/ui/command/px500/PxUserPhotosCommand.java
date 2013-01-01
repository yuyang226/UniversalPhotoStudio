/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.px500;

import android.content.Context;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.px500.PxUserPhotosService;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PxUserPhotosCommand extends AbstractPx500PhotoListCommand {

	private Author mAuthor;

	/**
	 * @param context
	 */
	public PxUserPhotosCommand(Context context, Author user) {
		super(context);
		this.mAuthor = user;
	}

	@Override
	public int getIconResourceId() {
		return 0;
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			return new PxUserPhotosService(mAuthor.getUserId());
		}
		return super.getAdapter(adapterClass);
	}

	@Override
	public String getDescription() {
		String s = mContext.getString(R.string.cd_500px_user_photos);
		return String.format(
				s,
				mAuthor.getUserName() == null ? mAuthor.getUserId() : mAuthor
						.getUserName());
	}

}
