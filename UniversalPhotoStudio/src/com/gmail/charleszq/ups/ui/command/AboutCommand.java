/**
 * 
 */
package com.gmail.charleszq.ups.ui.command;

import android.content.Context;
import android.content.Intent;

import com.gmail.charleszq.ups.AboutActivity;
import com.gmail.charleszq.ups.R;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class AboutCommand extends AbstractCommand<Void> {

	public AboutCommand(Context context) {
		super(context);
	}

	@Override
	public boolean execute(Object... params) {
		Intent i = new Intent(mContext, AboutActivity.class);
		mContext.startActivity(i);
		return true;
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.icon;
	}

	@Override
	public String getLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append(mContext.getString(R.string.cmd_name_about));
		sb.append(" "); //$NON-NLS-1$
		sb.append(mContext.getString(R.string.app_name));
		return sb.toString();
	}

}
