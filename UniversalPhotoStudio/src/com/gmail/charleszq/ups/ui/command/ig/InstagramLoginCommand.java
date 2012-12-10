/**
 * 
 */
package com.gmail.charleszq.ups.ui.command.ig;

import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.oauth.InstagramService;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.ui.command.AbstractCommand;
import com.gmail.charleszq.ups.utils.IConstants;

/**
 * @author I062692
 * @param <T>
 * 
 */
public class InstagramLoginCommand extends AbstractCommand<Object> {

	public InstagramLoginCommand(Context context) {
		super(context);
	}

	@Override
	public boolean execute(Object... params) {
		InstagramService service = new InstagramAuthService()
				.apiKey(IConstants.INSTAGRAM_CLIENT_ID)
				.apiSecret(IConstants.INSTAGRAM_CLIENT_SECRET)
				.callback(IConstants.IG_CALL_BACK_STR).build();
		String authUrl = service.getAuthorizationUrl(null);
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
		mContext.startActivity(i);
		return true;
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_ation_ig_login;
	}

	@Override
	public String getLabel() {
		return mContext.getString(R.string.f_login);
	}

}
