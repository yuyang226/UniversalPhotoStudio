/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.gmail.charleszq.picorner.R;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public class RateCommand extends AbstractCommand<Void> {
	
	private static final String APP_URL = "https://play.google.com/store/apps/details?id="; //$NON-NLS-1$

	public RateCommand(Context context) {
		super(context);
	}

	@Override
	public boolean execute(Object... params) {
		
		String url = APP_URL + R.class.getPackage().getName();
		Intent marketIntent = new Intent(
				Intent.ACTION_VIEW, Uri.parse(url));
		mContext.startActivity(marketIntent);
		Toast.makeText(mContext, R.string.msg_thanks_rating, Toast.LENGTH_LONG).show();
		return true;
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_vote;
	}

	@Override
	public String getLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append(mContext.getString(R.string.menu_item_rate));
		sb.append(" "); //$NON-NLS-1$
		sb.append(mContext.getString(R.string.app_name));
		return sb.toString();
	}

}
