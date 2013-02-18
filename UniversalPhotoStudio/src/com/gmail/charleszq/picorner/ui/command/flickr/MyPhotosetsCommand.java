/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.flickr;

import java.util.Comparator;

import android.app.ActionBar;
import android.content.Context;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.SPUtil;
import com.gmail.charleszq.picorner.offline.FlickrOfflineParameter;
import com.gmail.charleszq.picorner.offline.IOfflineViewParameter;
import com.gmail.charleszq.picorner.offline.OfflineControlFileUtil;
import com.gmail.charleszq.picorner.offline.OfflinePhotoCollectionType;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.flickr.FlickrPhotoSetPhotosService;
import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.picorner.task.flickr.FetchFlickrPhotosetIconUrlTask;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;
import com.gmail.charleszq.picorner.ui.flickr.MyPhotoSetsHiddenView;
import com.gmail.charleszq.picorner.ui.helper.IHiddenView;
import com.googlecode.flickrjandroid.photosets.Photoset;

/**
 * Represents the command to show all my photo sets in a menu item back view.
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class MyPhotosetsCommand extends PhotoListCommand {

	private IHiddenView mHiddenView;
	private Photoset mPhotoSet;
	private IOfflineViewParameter mOfflineParameter;

	/**
	 * @param context
	 */
	public MyPhotosetsCommand(Context context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_flickr_ps;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mContext.getString(R.string.f_my_photo_sets);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IHiddenView.class) {
			if (mHiddenView == null) {
				mHiddenView = new MyPhotoSetsHiddenView();
			}
			return mHiddenView;
		}
		if (adapterClass == AbstractFetchIconUrlTask.class) {
			FetchFlickrPhotosetIconUrlTask task = new FetchFlickrPhotosetIconUrlTask(
					mContext);
			return task;
		}
		if (adapterClass == IPhotoService.class) {
			mCurrentPhotoService = new FlickrPhotoSetPhotosService(
					SPUtil.getFlickrUserId(mContext),
					SPUtil.getFlickrAuthToken(mContext),
					SPUtil.getFlickrAuthTokenSecret(mContext),
					mPhotoSet);
			return mCurrentPhotoService;
		}
		if (adapterClass == IOfflineViewParameter.class) {
			mOfflineParameter = new FlickrOfflineParameter(
					OfflinePhotoCollectionType.PHOTO_SET, mPhotoSet.getId(), mPhotoSet.getTitle());
			if (OfflineControlFileUtil.isOfflineViewEnabled(mContext,
					mOfflineParameter)
					&& OfflineControlFileUtil.isOfflineControlFileReady(
							mContext, mOfflineParameter))
				return mOfflineParameter;
			else
				return null;
		}
		if (adapterClass == Comparator.class) {
			return mPhotoSet;
		}
		if( adapterClass == ActionBar.class ) {
			return Boolean.FALSE.toString();
		}
		return super.getAdapter(adapterClass);
	}

	@Override
	public boolean execute(Object... params) {
		this.mPhotoSet = (Photoset) params[0];
		return super.execute();
	}

	@Override
	public String getDescription() {
		String msg = mContext.getString(R.string.cd_flickr_photo_set_photos);
		return String
				.format(msg, mPhotoSet != null ? mPhotoSet.getTitle() : ""); //$NON-NLS-1$
	}

}
