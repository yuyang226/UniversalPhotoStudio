/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.ig;

import org.jinstagram.auth.model.Token;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.ig.InstagramMyLikesService;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class InstagramLikesCommand extends AbstractInstagramPhotoListCommand {

	/**
	 * @param context
	 */
	public InstagramLikesCommand(Context context) {
		super(context);
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_instagram_my_likes;
	}

	@Override
	public String getLabel() {
		return mContext.getString(R.string.ig_my_likes);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			if (mCurrentPhotoService == null) {
				PicornerApplication app = (PicornerApplication) ((Activity) mContext)
						.getApplication();
				Token token = app.getInstagramAuthToken();
				mCurrentPhotoService = new InstagramMyLikesService(token);
			}
			return mCurrentPhotoService;
		}
		return super.getAdapter(adapterClass);
	}
	
	@Override
	public String getDescription() {
		return mContext.getString(R.string.cd_ig_my_likes);
	}

}
