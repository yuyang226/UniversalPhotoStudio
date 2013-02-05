/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.flickr;

import java.util.Comparator;

import android.content.Context;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.SPUtil;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.flickr.FlickrPhotoGroupPhotosService;
import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.picorner.task.flickr.FetchFlickrGroupIconUrlTask;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;
import com.gmail.charleszq.picorner.ui.flickr.MyPhotoGroupsHiddenView;
import com.gmail.charleszq.picorner.ui.helper.IHiddenView;
import com.googlecode.flickrjandroid.groups.Group;

/**
 * Represents the command get all my groups in the main menu back view.
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class MyGroupsCommand extends PhotoListCommand {

	private Group mGroup;
	private IHiddenView mHiddenView;

	/**
	 * 
	 * @param context
	 */
	public MyGroupsCommand(Context context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		// TODO icon
		return R.drawable.ic_action_flickr_my_favourites;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mContext.getString(R.string.f_my_groups);
	}

	@Override
	public boolean execute(Object... params) {
		mGroup = (Group) params[0];
		return super.execute();
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IHiddenView.class) {
			if (mHiddenView == null) {
				mHiddenView = new MyPhotoGroupsHiddenView();
			}
			return mHiddenView;
		}
		if (adapterClass == AbstractFetchIconUrlTask.class) {
			FetchFlickrGroupIconUrlTask task = new FetchFlickrGroupIconUrlTask(
					mContext);
			return task;
		}
		if (adapterClass == IPhotoService.class) {
			mCurrentPhotoService = new FlickrPhotoGroupPhotosService(
					SPUtil.getFlickrUserId(mContext),
					SPUtil.getFlickrAuthToken(mContext),
					SPUtil.getFlickrAuthTokenSecret(mContext),
					mGroup.getId());
			return mCurrentPhotoService;
		}
		if (adapterClass == Comparator.class) {
			return mGroup;
		}
		return super.getAdapter(adapterClass);
	}

	@Override
	public String getDescription() {
		String msg = mContext.getString(R.string.cd_flickr_group_photos);
		return String.format(msg, mGroup != null ? mGroup.getName() : ""); //$NON-NLS-1$
	}

}
