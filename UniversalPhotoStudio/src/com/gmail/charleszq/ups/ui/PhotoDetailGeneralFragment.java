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
import com.gmail.charleszq.ups.task.flickr.FetchFlickrUserIconUrlTask;
import com.gmail.charleszq.ups.task.flickr.FlickrGetPhotoFavCountTask;
import com.gmail.charleszq.ups.task.flickr.FlickrGetPhotoGeneralInfoTask;
import com.gmail.charleszq.ups.utils.IConstants;
import com.gmail.charleszq.ups.utils.ModelUtils;
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

	private MediaObject mCurrentPhoto;

	/**
	 * Must have this according the android document.
	 */
	public PhotoDetailGeneralFragment() {
	}

	public static PhotoDetailGeneralFragment newInstance(MediaObject photo) {
		PhotoDetailGeneralFragment f = new PhotoDetailGeneralFragment();
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
				if (mCurrentPhoto.getMediaSource() == MediaSourceType.INSTAGRAM
						|| mCurrentPhoto.getMediaSource() == MediaSourceType.PX500) {
					mImageFetcher.loadImage(mCurrentPhoto.getAuthor()
							.getBuddyIconUrl(), image);
				} else {
					FetchFlickrUserIconUrlTask task = new FetchFlickrUserIconUrlTask(
							getActivity(), mCurrentPhoto.getAuthor()
									.getUserId());
					task.execute(mImageFetcher, image);
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

				if (mCurrentPhoto.getMediaSource() == MediaSourceType.FLICKR)
					loadFlickrFavs(mCurrentPhoto.getId(), photoFavs);
			}

			String desc = mCurrentPhoto.getDescription();
			if (desc != null && desc.trim().length() > 0) {
				ModelUtils.formatHtmlString(desc, description);
			}
		}

		return v;
	}

	private void loadFlickrFavs(String photoId, final TextView text) {
		FlickrGetPhotoFavCountTask task = new FlickrGetPhotoFavCountTask();
		task.addTaskDoneListener(new IGeneralTaskDoneListener<Integer>() {

			@Override
			public void onTaskDone(Integer result) {
				text.setText(String.valueOf(result));
				mCurrentPhoto.setFavorites(result);
			}
		});
		task.execute(photoId);
	}
}
