/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.flickr;

import android.content.Context;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.task.flickr.FlickrOAuthTask;
import com.gmail.charleszq.picorner.ui.command.AbstractCommand;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class FlickrLoginCommand extends AbstractCommand<Object> {

	public FlickrLoginCommand(Context context) {
		super(context);
	}

	@Override
	public boolean execute(Object... objects) {
		FlickrOAuthTask task = new FlickrOAuthTask(mContext);
		task.execute();
		return true;
	}

	@Override
	public String getLabel() {
		return mContext.getString(R.string.f_login);
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_menu_login;
	}

}
