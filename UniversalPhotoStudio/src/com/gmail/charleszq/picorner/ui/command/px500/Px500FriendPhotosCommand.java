/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.px500;

import java.lang.ref.WeakReference;
import java.util.Comparator;

import android.content.Context;
import android.view.View;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.SPUtil;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.px500.PxUserPhotosService;
import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.picorner.ui.helper.IHiddenView;
import com.gmail.charleszq.picorner.ui.px500.Px500FriendsView;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class Px500FriendPhotosCommand extends AbstractPx500PhotoListCommand {

	private Author mFriend;
	private IHiddenView mHiddenView;

	/**
	 * @param context
	 */
	public Px500FriendPhotosCommand(Context context) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mContext.getString(R.string.menu_header_px500);
	}

	@Override
	public boolean execute(Object... params) {
		//when category changes, there is no params passed in
		if (params.length > 0)
			mFriend = (Author) params[0];
		return super.execute();
	}

	@Override
	public String getDescription() {
		String msg = mContext.getString(R.string.cd_500px_user_photos);
		return String.format(
				msg,
				mFriend.getUserName() == null ? String.valueOf(mFriend
						.getUserId()) : mFriend.getUserName());
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IHiddenView.class) {
			if (mHiddenView == null) {
				mHiddenView = new Px500FriendsView();
			}
			return mHiddenView;
		}
		if (adapterClass == IPhotoService.class) {
			mCurrentPhotoService = new PxUserPhotosService(
					SPUtil.getPx500OauthToken(mContext),
					SPUtil.getPx500OauthTokenSecret(mContext),
					mFriend.getUserId());
			((PxUserPhotosService) mCurrentPhotoService)
					.setPhotoCategory(mPhotoCategory);
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
			return this.mFriend.getUserId() + mPhotoCategory.toString();
		}
		return super.getAdapter(adapterClass);
	}

}
