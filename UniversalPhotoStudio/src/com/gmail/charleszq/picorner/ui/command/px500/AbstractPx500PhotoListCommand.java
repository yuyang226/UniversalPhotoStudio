/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.px500;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;
import com.gmail.charleszq.picorner.utils.IConstants;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public abstract class AbstractPx500PhotoListCommand extends PhotoListCommand {

	/**
	 * @param context
	 */
	public AbstractPx500PhotoListCommand(Context context) {
		super(context);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == Integer.class) {
			return IConstants.DEF_500PX_PAGE_SIZE;
		}
		return super.getAdapter(adapterClass);
	}

	protected String getAuthToken() {
		PicornerApplication app = (PicornerApplication) ((Activity) mContext)
				.getApplication();
		return app.getPx500OauthToken();
	}

	protected String getAuthTokenSecret() {
		PicornerApplication app = (PicornerApplication) ((Activity) mContext)
				.getApplication();
		return app.getPx500OauthTokenSecret();
	}

	protected String getUserId() {
		PicornerApplication app = (PicornerApplication) ((Activity) mContext)
				.getApplication();
		Author a = app.getPxUserProfile();
		if (a != null) {
			return a.getUserId();
		} else {
			return null;
		}
	}

}
