/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.flickr;

import java.lang.ref.WeakReference;
import java.util.Comparator;

import android.content.Context;
import android.view.View;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.SPUtil;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.flickr.FlickrUserPhotoStreamService;
import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;
import com.gmail.charleszq.picorner.ui.flickr.FlickrContactsView;
import com.gmail.charleszq.picorner.ui.helper.IHiddenView;

/**
 * Represents the command to fetch my friend list, then after click one of them,
 * go get the photos of this friend, it used in the secondary menu.
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrFriendPhotosCommand extends PhotoListCommand {

	private IHiddenView mHiddenView;

	/**
	 * the friend whose photos to be shown.
	 */
	private Author mFriend;

	public FlickrFriendPhotosCommand(Context context) {
		super(context);
	}

	@Override
	public boolean execute(Object... params) {
		mFriend = (Author) params[0];
		return super.execute();
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_friends;
	}

	@Override
	public String getLabel() {
		return mContext.getString(R.string.menu_header_flickr);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IHiddenView.class) {
			if (mHiddenView == null) {
				mHiddenView = new FlickrContactsView();
			}
			return mHiddenView;
		}
		if (adapterClass == IPhotoService.class) {
			String token = SPUtil.getFlickrAuthToken(mContext);
			String secret = SPUtil.getFlickrAuthTokenSecret(mContext);
			mCurrentPhotoService = new FlickrUserPhotoStreamService(
					mFriend.getUserId(), token, secret);
			return mCurrentPhotoService;
		}
		if (adapterClass == AbstractFetchIconUrlTask.class) {
			// this task is a little special, since we don't know the friend at
			// this time
			AbstractFetchIconUrlTask task = new AbstractFetchIconUrlTask(
					mContext) {

				@Override
				protected String doInBackground(Object... params) {
					Author a = (Author) params[0];
					mIconViewRef = new WeakReference<View>((View) params[1]);
					return a.getBuddyIconUrl();
				}

			};
			return task;
		}
		if (adapterClass == Comparator.class) {
			return this.mFriend;
		}
		return super.getAdapter(adapterClass);
	}

	@Override
	public String getDescription() {
		String s = mContext.getString(R.string.cd_flickr_user_photos);
		return String.format(
				s,
				mFriend.getUserName() == null ? mFriend.getUserId() : mFriend
						.getUserName());
	}

}
