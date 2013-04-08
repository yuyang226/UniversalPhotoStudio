/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.github.yuyang226.j500px.users.User;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.SPUtil;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.model.MediaSourceType;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.ig.InstagramCheckRelationshipTask;
import com.gmail.charleszq.picorner.task.ig.InstagramFollowUserTask;
import com.gmail.charleszq.picorner.task.px500.PxFetchUserProfileTask;
import com.gmail.charleszq.picorner.task.px500.PxFollowUserTask;
import com.gmail.charleszq.picorner.ui.command.flickr.FlickrUserPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.ig.InstagramUserPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.px500.PxUserPhotosCommand;
import com.gmail.charleszq.picorner.utils.IConstants;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class UserPhotoListFragment extends AbstractPhotoGridFragment {

	/**
	 * The current user of the photos to be shown.
	 */
	private Author mCurrentUser;

	/**
	 * the ordinal of <code>MediaSourceType</code>
	 */
	private int mMediaSourceType = 0;

	/**
	 * The marker to show the follow menu item or not, when this fragment is
	 * attached on activity, we need to check the relationship with the photo
	 * owner, if it's instagram photo, we will show the menu item, and according
	 * the current relationship, we change the menu item title.
	 */
	private boolean mShowFollowMenuItem = false;

	/**
	 * 0: not ready yet, we don't know the relaitonship now; <br/>
	 * 1: following <br/>
	 * 2: not following
	 */
	private int mFollowing = 0;

	/**
	 * Constructor
	 */
	public UserPhotoListFragment() {
	}

	@Override
	protected void loadFirstPage() {
		if (mMediaSourceType == MediaSourceType.FLICKR.ordinal()) {
			mCurrentCommand = new FlickrUserPhotosCommand(getActivity(),
					mCurrentUser);
		} else if (mMediaSourceType == MediaSourceType.INSTAGRAM.ordinal()) {
			mCurrentCommand = new InstagramUserPhotosCommand(getActivity(),
					mCurrentUser);
		} else {
			// 500px
			mCurrentCommand = new PxUserPhotosCommand(getActivity(),
					mCurrentUser);
		}
		mCurrentCommand.setCommndDoneListener(mCommandDoneListener);
		mCurrentCommand.execute();
		if (getActivity() != null) {
			getActivity().getActionBar().setSubtitle(
					mCurrentCommand.getDescription());
		}
	}

	@Override
	protected void initialIntentData(Intent intent) {
		mMediaSourceType = intent.getIntExtra(
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

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_ig_follow, menu);
		inflater.inflate(R.menu.menu_flickr_user_photo_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_item_follow) {
			final ProgressDialog dialog1 = ProgressDialog.show(getActivity(),
					"", //$NON-NLS-1$
					getString(R.string.msg_working));
			dialog1.setCanceledOnTouchOutside(true);
			IGeneralTaskDoneListener<Boolean> relationshipListener = new IGeneralTaskDoneListener<Boolean>() {

				@Override
				public void onTaskDone(Boolean result) {
					if (dialog1 != null && dialog1.isShowing()) {
						try {
							dialog1.dismiss();
						} catch (Exception e) {

						}
					}
					if (result) {
						mFollowing = mFollowing == 1 ? 2 : 1;
						getActivity().invalidateOptionsMenu();
					} else {
						Toast.makeText(
								getActivity(),
								getString(R.string.msg_ig_chg_relationship_failed),
								Toast.LENGTH_SHORT).show();
					}
				}
			};
			AbstractContextAwareTask<String, Integer, Boolean> followTask = null;
			if (mMediaSourceType == MediaSourceType.INSTAGRAM.ordinal()) {
				followTask = new InstagramFollowUserTask(getActivity());
			} else if (mMediaSourceType == MediaSourceType.PX500.ordinal()) {
				followTask = new PxFollowUserTask(getActivity());
			}
			if (followTask != null) {
				followTask.addTaskDoneListener(relationshipListener);
				followTask.execute(mCurrentUser.getUserId(),
						mFollowing == 1 ? Boolean.FALSE.toString()
								: Boolean.TRUE.toString());
			}
			return true;
		}

		// show flickr user's web site.
		if (item.getItemId() == R.id.menu_item_f_user_website) {
			String url = IConstants.FLICKR_WEB_SITE_URL
					+ this.mCurrentUser.getUserId();
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			getActivity().startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.menu_item_follow);
		if (!mShowFollowMenuItem) {
			item.setVisible(false);
		}

		switch (mFollowing) {
		case 0:
			item.setEnabled(false);
			break;
		case 1:
			item.setEnabled(true);
			item.setTitle(getString(R.string.menu_item_ig_unfollow_user));
			break;
		case 2:
			item.setEnabled(true);
			item.setTitle(getString(R.string.menu_item_ig_follow_user));
			break;
		}

		menu.setGroupVisible(R.id.group_f_u_photo_list,
				this.mMediaSourceType == MediaSourceType.FLICKR.ordinal());
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// now we have the user information, we need to check the relationship.
		if (mMediaSourceType == MediaSourceType.INSTAGRAM.ordinal()) {
			if (SPUtil.getInstagramUserId(getActivity()) == null) {
				mShowFollowMenuItem = false;
				getActivity().invalidateOptionsMenu();
			} else {
				mShowFollowMenuItem = true;
				InstagramCheckRelationshipTask task = new InstagramCheckRelationshipTask(
						getActivity());
				task.addTaskDoneListener(new IGeneralTaskDoneListener<Boolean>() {

					@Override
					public void onTaskDone(Boolean result) {
						mFollowing = result ? 1 : 2;
						mShowFollowMenuItem = true;
						if (getActivity() != null)
							getActivity().invalidateOptionsMenu();

					}
				});
				task.execute(mCurrentUser.getUserId());
			}
		} else if (mMediaSourceType == MediaSourceType.PX500.ordinal()) {
			if (SPUtil.getPx500OauthToken(getActivity()) == null) {
				mShowFollowMenuItem = false;
				getActivity().invalidateOptionsMenu();
			} else {
				PxFetchUserProfileTask pxUserTask = new PxFetchUserProfileTask(
						getActivity());
				pxUserTask
						.addTaskDoneListener(new IGeneralTaskDoneListener<User>() {

							@Override
							public void onTaskDone(User result) {
								if (result != null) {
									mFollowing = result.isFollowing() ? 1 : 2;
									mShowFollowMenuItem = true;
									if (getActivity() != null) {
										getActivity().invalidateOptionsMenu();
									}
								}
							}
						});
				pxUserTask.execute(mCurrentUser.getUserId());
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Intent i = new Intent(getActivity(), ImageDetailActivity.class);
		i.putExtra(ImageDetailActivity.DP_KEY, mPhotosProvider);
		i.putExtra(ImageDetailActivity.LARGE_IMAGE_POSITION, position);
		startActivity(i);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mCurrentCommand != null) {
			getActivity().getActionBar().setSubtitle(
					mCurrentCommand.getDescription());
		}
	}

}
