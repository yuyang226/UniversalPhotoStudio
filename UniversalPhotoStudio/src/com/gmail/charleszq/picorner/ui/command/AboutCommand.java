/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command;

import android.content.Context;
import android.content.Intent;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.ui.AboutActivity;
import com.gmail.charleszq.picorner.utils.IConstants;

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
		i.putExtra(IConstants.ABOUT_FILE_FRG_ARG_KEY, mContext.getString(R.string.about_file_name));
		mContext.startActivity(i);
		return true;
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_launcher;
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
