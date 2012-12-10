/**
 * 
 */
package com.gmail.charleszq.ups.ui.command.flickr;

import android.content.Context;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.task.flickr.FlickrOAuthTask;
import com.gmail.charleszq.ups.ui.command.AbstractCommand;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class FlickrLoginCommand extends AbstractCommand<Object> {

	public FlickrLoginCommand(Context context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#execute()
	 */
	@Override
	public boolean execute(Object... objects) {
		FlickrOAuthTask task = new FlickrOAuthTask(mContext);
		task.execute();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mContext.getString(R.string.f_login);
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_login;
	}

}
