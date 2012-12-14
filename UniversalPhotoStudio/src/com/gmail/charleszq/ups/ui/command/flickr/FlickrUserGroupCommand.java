/**
 * 
 */
package com.gmail.charleszq.ups.ui.command.flickr;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.service.IPhotoService;
import com.gmail.charleszq.ups.service.flickr.FlickrPhotoGroupPhotosService;
import com.gmail.charleszq.ups.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.ups.task.flickr.FetchFlickrGroupIconUrlTask;
import com.gmail.charleszq.ups.ui.command.PhotoListCommand;
import com.googlecode.flickrjandroid.groups.Group;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class FlickrUserGroupCommand extends PhotoListCommand {

	private Group mGroup;

	/**
	 * @param context
	 */
	public FlickrUserGroupCommand(Context context, Group group) {
		super(context);
		this.mGroup = group;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mGroup.getName();
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == AbstractFetchIconUrlTask.class) {
			return new FetchFlickrGroupIconUrlTask(mContext, mGroup);
		}
		if (adapterClass == IPhotoService.class) {
			Activity act = (Activity) mContext;
			UPSApplication app = (UPSApplication) act.getApplication();
			return new FlickrPhotoGroupPhotosService(app.getUserId(),
					app.getFlickrToken(), app.getFlickrTokenSecret(),
					mGroup.getId());
		}
		return super.getAdapter(adapterClass);
	}

}
