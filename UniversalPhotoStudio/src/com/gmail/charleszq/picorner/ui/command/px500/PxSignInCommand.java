/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.px500;

import android.content.Context;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.ui.command.AbstractCommand;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PxSignInCommand extends AbstractCommand<Object> {

	public PxSignInCommand(Context context) {
		super(context);
	}

	@Override
	public boolean execute(Object... params) {
		return false;
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_sign_in;
	}

	@Override
	public String getLabel() {
		return mContext.getString(R.string.f_login);
	}

}
