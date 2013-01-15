/**
 * 
 */
package com.gmail.charleszq.picorner.ui.flickr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.FetchPhotoPermissionTask;
import com.gmail.charleszq.picorner.task.flickr.FlickrGetPhotoFavCountTask;
import com.gmail.charleszq.picorner.task.flickr.FlickrGetPhotoGeneralInfoTask;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.googlecode.flickrjandroid.photos.Permissions;
import com.googlecode.flickrjandroid.photos.Photo;

/**
 * Represents the general information fragment of my own flickr photo.
 * 
 * @author charleszq
 * 
 */
public class MyFlickrPhotoGeneralFragment extends Fragment {

	/**
	 * The current photo
	 */
	private MediaObject mCurrentPhoto;

	/**
	 * ui controls
	 */
	private TextView mTextComments, mTextFavs, mTextViews;
	private EditText mEditTitle, mEditDesc;
	private RadioButton mRadioPrivate, mRadioPublic;
	private CheckBox mCheckFriends, mCheckFamily;

	/**
	 * Default constructor.
	 */
	public MyFlickrPhotoGeneralFragment() {
	}

	public static MyFlickrPhotoGeneralFragment newInstance(MediaObject photo) {
		MyFlickrPhotoGeneralFragment f = new MyFlickrPhotoGeneralFragment();
		final Bundle bundle = new Bundle();
		bundle.putSerializable(IConstants.DETAIL_PAGE_PHOTO_ARG_KEY, photo);
		f.setArguments(bundle);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getArguments();
		mCurrentPhoto = (MediaObject) bundle
				.getSerializable(IConstants.DETAIL_PAGE_PHOTO_ARG_KEY);
		this.setHasOptionsMenu(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu,
	 * android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_save, menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.flickr_photo_prop_comp, container,
				false);

		// text
		mTextViews = (TextView) v.findViewById(R.id.flickr_detail_gen_views);
		mTextComments = (TextView) v
				.findViewById(R.id.flickr_detail_gen_comments);
		mTextFavs = (TextView) v.findViewById(R.id.flickr_detail_gen_favs);

		// edit
		mEditTitle = (EditText) v.findViewById(R.id.edit_f_p_title);
		mEditDesc = (EditText) v.findViewById(R.id.edit_f_p_desc);

		// radio and checkbox
		mRadioPrivate = (RadioButton) v.findViewById(R.id.rb_f_p_private);
		mRadioPublic = (RadioButton) v.findViewById(R.id.rb_f_p_public);
		mCheckFriends = (CheckBox) v.findViewById(R.id.cb_f_p_friend);
		mCheckFamily = (CheckBox) v.findViewById(R.id.cb_f_p_family);
		
		enablePermControls(false);

		return v;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();

		// title and desc
		mEditTitle.setText(mCurrentPhoto.getTitle());
		mEditDesc.setText(mCurrentPhoto.getDescription());

		// hide the keyboard by default.
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		// fetch comments/views/favs count
		FlickrGetPhotoGeneralInfoTask ptask = new FlickrGetPhotoGeneralInfoTask();
		ptask.addTaskDoneListener(new IGeneralTaskDoneListener<Photo>() {

			@Override
			public void onTaskDone(Photo result) {
				if (result != null) {
					enablePermControls(true);
					mCurrentPhoto.setViews(result.getViews());
					mCurrentPhoto.setComments(result.getComments());
					mCurrentPhoto.setFavorites(result.getFavorites());
				}
				mTextViews.setText(String
						.valueOf(mCurrentPhoto.getViews() == -1 ? 0
								: mCurrentPhoto.getViews()));
				mTextComments.setText(String.valueOf(mCurrentPhoto
						.getComments() == -1 ? 0 : mCurrentPhoto.getComments()));
				mTextFavs.setText(String
						.valueOf(mCurrentPhoto.getFavorites() == -1 ? 0
								: mCurrentPhoto.getFavorites()));
			}
		});
		ptask.execute(mCurrentPhoto.getId(), mCurrentPhoto.getSecret());

		// fav count
		FlickrGetPhotoFavCountTask task = new FlickrGetPhotoFavCountTask();
		task.addTaskDoneListener(new IGeneralTaskDoneListener<Integer>() {

			@Override
			public void onTaskDone(Integer result) {
				mCurrentPhoto.setFavorites(result);
				mTextFavs.setText(String.valueOf(result));
			}
		});
		task.execute(mCurrentPhoto.getId());

		// permissions
		FetchPhotoPermissionTask permTask = new FetchPhotoPermissionTask(
				getActivity());
		permTask.addTaskDoneListener(new IGeneralTaskDoneListener<Permissions>() {

			@Override
			public void onTaskDone(Permissions result) {
				if (result != null) {
					mRadioPublic.setChecked(result.isPublicFlag());
					mRadioPrivate.setChecked(!result.isPublicFlag());
					mCheckFamily.setChecked(result.isFamilyFlag());
					mCheckFriends.setChecked(result.isFriendFlag());
				} else {
					Toast.makeText(getActivity(),
							getString(R.string.msg_fail_get_f_perm),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		permTask.execute(mCurrentPhoto.getId());
	}
	
	private void enablePermControls(boolean enable) {
		mRadioPublic.setEnabled(enable);
		mRadioPrivate.setEnabled(enable);
		mCheckFamily.setEnabled(!mRadioPrivate.isChecked());
		mCheckFriends.setEnabled(!mRadioPrivate.isChecked());
	}

}
