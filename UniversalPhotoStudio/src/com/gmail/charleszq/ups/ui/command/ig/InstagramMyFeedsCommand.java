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
import com.gmail.charleszq.ups.ui.command.PhotoListCommand;
import com.gmail.charleszq.ups.utils.IConstants;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public class InstagramMyFeedsCommand extends
		PhotoListCommand {

	public InstagramMyFeedsCommand(Context context) {
		super(context);
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_ation_ig_login;
	}

	@Override
	public String getLabel() {
		return mContext.getString(R.string.ig_my_feeds);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if( adapterClass == IPhotoService.class ) {
			UPSApplication app = (UPSApplication) ((Activity)mContext).getApplication();
			Token token = app.getInstagramAuthToken();
			return new InstagramMyFeedsService(token);
		}
		if( adapterClass == Integer.class ) {
			return IConstants.DEF_IG_PAGE_SIZE;
		}
		return super.getAdapter(adapterClass);
	}
	
	

}
