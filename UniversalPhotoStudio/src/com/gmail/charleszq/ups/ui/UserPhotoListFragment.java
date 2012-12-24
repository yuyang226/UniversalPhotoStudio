/**
 * 
 */
package com.gmail.charleszq.ups.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.model.Author;
import com.gmail.charleszq.ups.model.MediaSourceType;
import com.gmail.charleszq.ups.ui.command.flickr.FlickrUserPhotosCommand;
import com.gmail.charleszq.ups.ui.command.ig.InstagramUserPhotosCommand;
import com.gmail.charleszq.ups.ui.command.px500.PxUserPhotosCommand;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class UserPhotoListFragment extends AbstractPhotoGridFragment {

	/**
	 * The current user.
	 */
	private Author mCurrentUser;

	/**
	 * the ordinal of <code>MediaSourceType</code>
	 */
	private int mMedisSourceType = 0;

	/**
	 * Constructor
	 */
	public UserPhotoListFragment() {
	}

	@Override
	protected void loadFirstPage() {
		if (mMedisSourceType == MediaSourceType.FLICKR.ordinal()) {
			mCurrentCommand = new FlickrUserPhotosCommand(getActivity(),
					mCurrentUser.getUserId());
		} else if (mMedisSourceType == MediaSourceType.INSTAGRAM.ordinal()) {
			mCurrentCommand = new InstagramUserPhotosCommand(getActivity(),
					mCurrentUser);
		} else {
			// 500px
			mCurrentCommand = new PxUserPhotosCommand(getActivity(),
					mCurrentUser.getUserId());
		}
		mCurrentCommand.addCommndDoneListener(mCommandDoneListener);
		mCurrentCommand.execute();
	}

	@Override
	protected void initialIntentData(Intent intent) {
		mMedisSourceType = intent.getIntExtra(
				UserPhotoListActivity.MD_TYPE_KEY, 0);
		mCurrentUser = (Author) intent
				.getSerializableExtra(UserPhotoListActivity.USER_KEY);
	}

	@Override
	protected String getLoadingMessage() {
		return getString(R.string.msg_loading_more_photo_of_user);
	}

	@Override
	protected void bindData() {
		if (mCurrentUser != null && mLoadingMessageText != null) {
			String s = String.format(mLoadingMessage,
					mCurrentUser.getUserName());
			mLoadingMessageText.setText(s);
			if (mCurrentCommand != null) {
				// configuraton change
				mLoadingMessageText.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

}
