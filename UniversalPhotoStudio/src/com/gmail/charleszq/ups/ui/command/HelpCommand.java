/**
 * 
 */
package com.gmail.charleszq.ups.ui.command;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.ui.AboutActivity;
import com.gmail.charleszq.ups.utils.IConstants;

import android.content.Context;
import android.content.Intent;

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
		mContext.startActivity(i);
		return true;
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_help;
	}

	@Override
	public String getLabel() {
		return mContext.getString(R.string.btn_help);
	}

}
