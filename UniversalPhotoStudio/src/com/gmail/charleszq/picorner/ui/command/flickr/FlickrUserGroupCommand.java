/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.flickr;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.FlickrUserPhotoPool;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.flickr.FlickrPhotoGroupPhotosService;
import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.picorner.task.flickr.FetchFlickrGroupIconUrlTask;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;
import com.googlecode.flickrjandroid.groups.Group;
import com.googlecode.flickrjandroid.photos.PhotoPlace;

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

	@Override
	public int getIconResourceId() {
		return -1;
	}

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
			if (mCurrentPhotoService == null) {
				Activity act = (Activity) mContext;
				PicornerApplication app = (PicornerApplication) act.getApplication();
				mCurrentPhotoService = new FlickrPhotoGroupPhotosService(
						app.getFlickrUserId(), app.getFlickrToken(),
						app.getFlickrTokenSecret(), mGroup.getId());
			}
			return mCurrentPhotoService;
		}
		if( adapterClass == FlickrUserPhotoPool.class ) {
			return PhotoPlace.POOL + mGroup.getId();
		}
		return super.getAdapter(adapterClass);
	}
	
	@Override
	public String getDescription() {
		String s = mContext.getString(R.string.cd_flickr_group_photos);
		return String.format(s, mGroup.getName());
	}

}
