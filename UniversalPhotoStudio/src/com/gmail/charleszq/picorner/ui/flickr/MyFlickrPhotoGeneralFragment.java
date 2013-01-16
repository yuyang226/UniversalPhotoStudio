/**
 * 
 */
package com.gmail.charleszq.picorner.ui.flickr;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.FetchPhotoPermissionTask;
import com.gmail.charleszq.picorner.task.flickr.FlickrGetPhotoFavCountTask;
import com.gmail.charleszq.picorner.task.flickr.FlickrGetPhotoGeneralInfoTask;
import com.gmail.charleszq.picorner.task.flickr.SetPhotoMetaPermissionTask;
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
	private RadioGroup mRadioGroup;
	private ViewSwitcher mViewSwitcher;

	private boolean mGeneralInfoLoaded = false;
	private boolean mPermInfoLoaded = false;

	/**
	 * The marker to say that we can get the permission info from server,
	 * otherwise, we don't update it when save.
	 */
	private boolean isPermGetFromServer = false;

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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_item_save) {
			final String title = mEditTitle.getText().toString();
			String desc = mEditDesc.getText().toString();

			if (title == null || title.trim().length() == 0) {
				mEditTitle
						.setError(getString(R.string.msg_pls_input_photo_title));
				return false;
			}
			final String description = desc.trim().length() == 0 ? "" : desc.trim(); //$NON-NLS-1$

			// save
			final ProgressDialog dialog = ProgressDialog.show(getActivity(),
					"", getString(R.string.msg_working)); //$NON-NLS-1$
			SetPhotoMetaPermissionTask task = new SetPhotoMetaPermissionTask(
					getActivity());
			task.addTaskDoneListener(new IGeneralTaskDoneListener<Boolean>() {

				@Override
				public void onTaskDone(Boolean result) {
					if (dialog != null && dialog.isShowing()) {
						try {
							dialog.cancel();
						} catch (Exception e) {

						}
					}
					if (result) {
						mCurrentPhoto.setTitle(title);
						mCurrentPhoto.setDescription(description);
						// TODO broadcast the message.
						Toast.makeText(getActivity(),
								getString(R.string.msg_photo_meta_save_done),
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getActivity(),
								getString(R.string.msg_photo_meta_save_fail),
								Toast.LENGTH_SHORT).show();
					}
					
				}
			});

			if (isPermGetFromServer) {
				// save perm to server
				task.execute(mCurrentPhoto.getId(), title, description,
						Boolean.toString(mCheckFriends.isChecked()),
						Boolean.toString(mCheckFamily.isChecked()),
						Boolean.toString(mRadioPublic.isChecked()));
			} else {
				// only save title and desc
				task.execute(mCurrentPhoto.getId(), title, description);
			}

		}
		return super.onOptionsItemSelected(item);
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
		View v = inflater.inflate(R.layout.frg_f_prop, container,
				false);
		mViewSwitcher = (ViewSwitcher) v;

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
		mRadioGroup = (RadioGroup) v.findViewById(R.id.radio_group_perm);

		enablePermControls(false);
		hookListeners();
		return v;
	}

	/**
	 * Listeners for radio and checkbox
	 */
	private void hookListeners() {
		OnCheckedChangeListener lis = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_f_p_private:
					mCheckFamily.setEnabled(true);
					mCheckFriends.setEnabled(true);
					break;
				case R.id.rb_f_p_public:
					mCheckFamily.setEnabled(false);
					mCheckFriends.setEnabled(false);
					mCheckFriends.setChecked(false);
					mCheckFamily.setChecked(false);
					break;
				}
			}
		};

		mRadioGroup.setOnCheckedChangeListener(lis);
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
					mCurrentPhoto.setViews(result.getViews());
					mCurrentPhoto.setComments(result.getComments());
					mCurrentPhoto.setFavorites(result.getFavorites());
					mCurrentPhoto.setTitle(result.getTitle());
					mCurrentPhoto.setDescription(result.getDescription());
				}
				mTextViews.setText(String
						.valueOf(mCurrentPhoto.getViews() == -1 ? 0
								: mCurrentPhoto.getViews()));
				mTextComments.setText(String.valueOf(mCurrentPhoto
						.getComments() == -1 ? 0 : mCurrentPhoto.getComments()));
				mTextFavs.setText(String
						.valueOf(mCurrentPhoto.getFavorites() == -1 ? 0
								: mCurrentPhoto.getFavorites()));
				mEditTitle.setText(mCurrentPhoto.getTitle());
				mEditDesc.setText(mCurrentPhoto.getDescription());
				mGeneralInfoLoaded = true;
				if (mGeneralInfoLoaded && mPermInfoLoaded) {
					mViewSwitcher.showNext();
				}
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
					isPermGetFromServer = true;
					mRadioPublic.setChecked(result.isPublicFlag());
					mRadioPrivate.setChecked(!result.isPublicFlag());
					mCheckFamily.setChecked(result.isFamilyFlag());
					mCheckFriends.setChecked(result.isFriendFlag());
					enablePermControls(true);
				} else {
					Toast.makeText(getActivity(),
							getString(R.string.msg_fail_get_f_perm),
							Toast.LENGTH_SHORT).show();
				}
				mPermInfoLoaded = true;
				if (mGeneralInfoLoaded && mPermInfoLoaded) {
					mViewSwitcher.showNext();
				}
			}
		});
		permTask.execute(mCurrentPhoto.getId());
	}

	private void enablePermControls(boolean enable) {
		mRadioPublic.setEnabled(enable);
		mRadioPrivate.setEnabled(enable);
		mCheckFamily.setEnabled(mRadioPrivate.isChecked());
		mCheckFriends.setEnabled(mRadioPrivate.isChecked());
	}

}
