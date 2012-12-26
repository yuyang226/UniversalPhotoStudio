/**
 * 
 */
package com.gmail.charleszq.ups.ui.command.ig;

import org.jinstagram.auth.model.Token;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.service.IPhotoService;
import com.gmail.charleszq.ups.service.ig.InstagramMyFeedsService;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class InstagramMyFeedsCommand extends AbstractInstagramPhotoListCommand {

	public InstagramMyFeedsCommand(Context context) {
		super(context);
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_ig_myfeed;
	}

	@Override
	public String getLabel() {
		return mContext.getString(R.string.ig_my_feeds);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			if (mCurrentPhotoService == null) {
				UPSApplication app = (UPSApplication) ((Activity) mContext)
						.getApplication();
				Token token = app.getInstagramAuthToken();
				mCurrentPhotoService = new InstagramMyFeedsService(token);
			}
			return mCurrentPhotoService;
		}
		return super.getAdapter(adapterClass);
	}
	
	@Override
	public String getDescription() {
		return mContext.getString(R.string.cd_ig_my_feeds);
	}
}
