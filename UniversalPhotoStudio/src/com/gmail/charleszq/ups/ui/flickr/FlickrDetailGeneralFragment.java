/**
 * 
 */
package com.gmail.charleszq.ups.ui.flickr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.ups.task.flickr.FlickrGetUserInfoTask;
import com.gmail.charleszq.ups.ui.AbstractFragmentWithImageFetcher;
import com.gmail.charleszq.ups.utils.IConstants;
import com.googlecode.flickrjandroid.people.User;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrDetailGeneralFragment extends
		AbstractFragmentWithImageFetcher {

	static final String PHOTO_ARG_KEY = "photo.frg.arg"; //$NON-NLS-1$

	private MediaObject mCurrentPhoto;

	/**
	 * 
	 */
	public FlickrDetailGeneralFragment() {
	}

	public static FlickrDetailGeneralFragment newInstance(MediaObject photo) {
		FlickrDetailGeneralFragment f = new FlickrDetailGeneralFragment();
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
		if (mCurrentPhoto != null) {
			title.setText(mCurrentPhoto.getTitle());

			String name = null;
			if (mCurrentPhoto.getAuthor() != null) {
				name = mCurrentPhoto.getAuthor().getUserName();
				if (name == null) {
					name = mCurrentPhoto.getAuthor().getUserId();
				}

				// try loading the buddy icon
				FlickrGetUserInfoTask task = new FlickrGetUserInfoTask();
				task.addTaskDoneListener(new IGeneralTaskDoneListener<User>() {

					@Override
					public void onTaskDone(User result) {
						if (result != null) {
							logger.debug("author buddy icon url: " + result.getBuddyIconUrl()); //$NON-NLS-1$
							mImageFetcher.loadImage(result.getBuddyIconUrl(),
									image);
						}
					}
				});
				task.execute(mCurrentPhoto.getAuthor().getUserId());
			}
			if (name != null) {
				author.setText(name);
			}
		}

		return v;
	}
}
