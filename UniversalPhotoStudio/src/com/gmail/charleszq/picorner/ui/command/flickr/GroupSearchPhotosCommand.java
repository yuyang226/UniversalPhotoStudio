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
import com.gmail.charleszq.picorner.ui.flickr.GroupSearchHiddenListView;
import com.gmail.charleszq.picorner.ui.helper.IHiddenView;
import com.googlecode.flickrjandroid.groups.Group;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class GroupSearchPhotosCommand extends PhotoListCommand {

	private Group mFlickrGroup;
	private IHiddenView mHiddenView;

	/**
	 * @param context
	 */
	public GroupSearchPhotosCommand(Context context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_f_group_search;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mContext.getString(R.string.menu_item_flickr_group_search);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IHiddenView.class) {
			if (mHiddenView == null) {
				mHiddenView = new GroupSearchHiddenListView();
			}
			return mHiddenView;
		}
		if( adapterClass == AbstractFetchIconUrlTask.class) {
			return new FetchFlickrGroupIconUrlTask(mContext, mFlickrGroup);
		}
		if (adapterClass == IPhotoService.class) {
			mCurrentPhotoService = new FlickrPhotoGroupPhotosService(
					SPUtil.getFlickrUserId(mContext),
					SPUtil.getFlickrAuthToken(mContext),
					SPUtil.getFlickrAuthTokenSecret(mContext),
					mFlickrGroup.getId());
			return mCurrentPhotoService;
		}
		if( adapterClass == Comparator.class) {
			return mFlickrGroup;
		}
		return super.getAdapter(adapterClass);
	}

	@Override
	public boolean execute(Object... params) {
		this.mFlickrGroup = (Group) params[0];
		return super.execute();
	}
	
	@Override
	public String getDescription() {
		String msg = mContext.getString(R.string.cd_flickr_group_photos);
		return String.format(msg, mFlickrGroup != null ? mFlickrGroup.getName() : ""); //$NON-NLS-1$
	}


}
