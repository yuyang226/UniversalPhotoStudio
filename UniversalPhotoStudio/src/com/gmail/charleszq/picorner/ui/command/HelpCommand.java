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
public class HelpCommand extends AbstractCommand<Void> {

	public HelpCommand(Context context) {
		super(context);
	}

	@Override
	public boolean execute(Object... params) {
		Intent i = new Intent(mContext, AboutActivity.class);
		i.putExtra(IConstants.ABOUT_FILE_FRG_ARG_KEY, mContext.getString(R.string.help_file_name));
		i.putExtra(IConstants.ABOUT_FILE_ENCODING_KEY, mContext.getString(R.string.about_file_encoding, 
				IConstants.ABOUT_FILE_DEFAULT_ENCODING));
		mContext.startActivity(i);
		return true;
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_help;
	}

	@Override
	public String getLabel() {
		return mContext.getString(R.string.cmd_help_label);
	}

}
