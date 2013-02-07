/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.ig;

import java.lang.ref.WeakReference;
import java.util.Comparator;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.ig.InstagramUserPhotosService;
import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;
import com.gmail.charleszq.picorner.ui.helper.IHiddenView;
import com.gmail.charleszq.picorner.ui.ig.InstagramContactView;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class InstagramFollowingPhotosCommand extends PhotoListCommand {

	private Author		mFollowingFriend;
	private IHiddenView	mHiddenView;

	/**
	 * @param context
	 */
	public InstagramFollowingPhotosCommand(Context context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_contacts;
	}

	@Override
	public boolean execute(Object... params) {
		mFollowingFriend = (Author) params[0];
		return super.execute();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mContext.getString(R.string.menu_header_ig);
	}

	@Override
	public String getDescription() {
		String s = mContext.getString(R.string.cd_ig_user_photos);
		return String.format(
				s,
				mFollowingFriend.getUserName() == null ? mFollowingFriend
						.getUserId() : mFollowingFriend.getUserName());
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IHiddenView.class) {
			if (mHiddenView == null) {
				mHiddenView = new InstagramContactView();
			}
			return mHiddenView;
		}
		if (adapterClass == IPhotoService.class) {
			Activity act = (Activity) mContext;
			PicornerApplication app = (PicornerApplication) act
					.getApplication();
			mCurrentPhotoService = new InstagramUserPhotosService(
					app.getInstagramAuthToken(),
					Long.parseLong(mFollowingFriend.getUserId()));
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
			return this.mFollowingFriend;
		}
		return super.getAdapter(adapterClass);
	}
}
