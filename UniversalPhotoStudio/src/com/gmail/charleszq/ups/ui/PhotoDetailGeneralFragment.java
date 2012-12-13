/**
 * 
 */
package com.gmail.charleszq.ups.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.model.MediaSourceType;
import com.gmail.charleszq.ups.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.ups.task.flickr.FlickrGetPhotoGeneralInfoTask;
import com.gmail.charleszq.ups.task.flickr.FlickrGetUserInfoTask;
import com.gmail.charleszq.ups.utils.IConstants;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photos.Photo;

/**
 * Represents the fragment to show the detail information of photo, currently
 * for both flickr photo and instagram photo
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PhotoDetailGeneralFragment extends
		AbstractFragmentWithImageFetcher {

	private static final String PHOTO_ARG_KEY = "photo.frg.arg"; //$NON-NLS-1$

	private MediaObject mCurrentPhoto;

	/**
	 * Must have this according the android document.
	 */
	public PhotoDetailGeneralFragment() {
	}

	public static PhotoDetailGeneralFragment newInstance(MediaObject photo) {
		PhotoDetailGeneralFragment f = new PhotoDetailGeneralFragment();
		final Bundle bundle = new Bundle();
		bundle.putSerializable(PHOTO_ARG_KEY, photo);
		f.setArguments(bundle);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getArguments();
		mCurrentPhoto = (MediaObject) bundle.getSerializable(PHOTO_ARG_KEY);

		int thumbSize = getResources().getDimensionPixelSize(
				R.dimen.cmd_icon_size);
		initializeImageFetcher(IConstants.BUDDY_ICON_DIR, thumbSize);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.flickr_detail_general, container,
				false);
		TextView title = (TextView) v
				.findViewById(R.id.flickr_detail_general_photo_title);
		TextView author = (TextView) v
				.findViewById(R.id.flickr_detail_general_photo_author);
		final ImageView image = (ImageView) v
				.findViewById(R.id.flickr_detail_general_author_image);

		final TextView photoViews = (TextView) v
				.findViewById(R.id.flickr_detail_gen_views);
		final TextView photoComments = (TextView) v
				.findViewById(R.id.flickr_detail_gen_comments);
		final TextView photoFavs = (TextView) v
				.findViewById(R.id.flickr_detail_gen_favs);
		TextView description = (TextView) v
				.findViewById(R.id.flickr_detail_general_photo_desc);
		if (mCurrentPhoto != null) {
			// title
			if (mCurrentPhoto.getTitle() != null
					&& mCurrentPhoto.getTitle().trim().length() > 0)
				title.setText(mCurrentPhoto.getTitle());

			// author name.
			String name = null;
			if (mCurrentPhoto.getAuthor() != null) {
				name = mCurrentPhoto.getAuthor().getUserName();
				if (name == null) {
					name = mCurrentPhoto.getAuthor().getUserId();
				}

				// try loading the buddy icon
				if (mCurrentPhoto.getMediaSource() == MediaSourceType.INSTAGRAM) {
					mImageFetcher.loadImage(mCurrentPhoto.getAuthor()
							.getBuddyIconUrl(), image);
				} else {
					FlickrGetUserInfoTask task = new FlickrGetUserInfoTask();
					task.addTaskDoneListener(new IGeneralTaskDoneListener<User>() {

						@Override
						public void onTaskDone(User result) {
							if (result != null) {
								logger.debug("author buddy icon url: " + result.getBuddyIconUrl()); //$NON-NLS-1$
								mImageFetcher.loadImage(
										result.getBuddyIconUrl(), image);
							}
						}
					});
					task.execute(mCurrentPhoto.getAuthor().getUserId());
				}
			}
			if (name != null) {
				author.setText(name);
			}

			// photo views
			int comments = mCurrentPhoto.getComments();
			if (comments != -1
					|| mCurrentPhoto.getMediaSource() == MediaSourceType.INSTAGRAM) {
				photoViews
						.setText(String.valueOf(mCurrentPhoto.getViews() == -1 ? 0
								: mCurrentPhoto.getViews()));
				photoComments.setText(String.valueOf(comments == -1 ? 0
						: comments));
				photoFavs
						.setText(String
								.valueOf(mCurrentPhoto.getFavorites() == -1 ? 0
										: mCurrentPhoto.getFavorites()));
			} else {
				// flickr
				FlickrGetPhotoGeneralInfoTask ptask = new FlickrGetPhotoGeneralInfoTask();
				ptask.addTaskDoneListener(new IGeneralTaskDoneListener<Photo>() {

					@Override
					public void onTaskDone(Photo result) {
						if (result != null) {
							mCurrentPhoto.setViews(result.getViews());
							mCurrentPhoto.setComments(result.getComments());
							mCurrentPhoto.setFavorites(result.getFavorites());
						}
						photoViews.setText(String.valueOf(mCurrentPhoto
								.getViews() == -1 ? 0 : mCurrentPhoto
								.getViews()));
						photoComments.setText(String.valueOf(mCurrentPhoto
								.getComments() == -1 ? 0 : mCurrentPhoto
								.getComments()));
						photoFavs.setText(String.valueOf(mCurrentPhoto
								.getFavorites() == -1 ? 0 : mCurrentPhoto
								.getFavorites()));
					}
				});
				ptask.execute(mCurrentPhoto.getId());
			}

			String desc = mCurrentPhoto.getDescription();
			if (desc != null && desc.trim().length() > 0) {
				description.setText(desc);
			}
		}

		return v;
	}
}
