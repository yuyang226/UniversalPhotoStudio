/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.px500;

import com.gmail.charleszq.picorner.R;

import android.content.Context;

/**
 * @author charleszq
 *
 */
public class Px500MyPhotosCommand extends AbstractPx500PhotoListCommand {

	/**
	 * @param context
	 */
	public Px500MyPhotosCommand(Context context) {
		super(context);
	}

	/* (non-Javadoc)
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_px500_you;
	}

	/* (non-Javadoc)
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mContext.getString(R.string.ig_my_photos);
	}

}
