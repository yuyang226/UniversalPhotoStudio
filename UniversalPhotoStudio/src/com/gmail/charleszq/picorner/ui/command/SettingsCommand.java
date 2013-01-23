/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command;

import android.content.Context;
import android.content.Intent;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.ui.SettingsActivity;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public class SettingsCommand extends AbstractCommand<Void> {

	public SettingsCommand(Context context) {
		super(context);
	}

	@Override
	public boolean execute(Object... params) {
		Intent i = new Intent(mContext, SettingsActivity.class);
		mContext.startActivity(i);
		return true;
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_settings;
	}

	@Override
	public String getLabel() {
		return mContext.getString(R.string.cmd_setting_label);
	}

}
