/**
 * 
 */
package com.gmail.charleszq.ups.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.utils.IConstants;
import com.gmail.charleszq.ups.utils.ImageFetcher;
import com.gmail.charleszq.ups.utils.ImageCache.ImageCacheParams;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrDetailGeneralFragment extends Fragment {
	
	private static Logger logger = LoggerFactory.getLogger(FlickrDetailGeneralFragment.class);
	

	static final String PHOTO_ARG_KEY = "photo.frg.arg"; //$NON-NLS-1$

	private MediaObject mCurrentPhoto;
	private ImageFetcher mImageFetcher;

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

		// The ImageFetcher takes care of loading images into our ImageView
		// children asynchronously
		mImageFetcher = new ImageFetcher(getActivity(), thumbSize);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo);
		
		ImageCacheParams cacheParams = new ImageCacheParams(getActivity(),
				IConstants.BUDDY_ICON_DIR);

		// Set memory cache to 25% of mem class
		cacheParams.setMemCacheSizePercent(getActivity(), 0.25f);
		mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(),
				cacheParams);

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
		ImageView image = (ImageView) v
				.findViewById(R.id.flickr_detail_general_author_image);
		if (mCurrentPhoto != null) {
			title.setText(mCurrentPhoto.getTitle());

			String name = null;
			if (mCurrentPhoto.getAuthor() != null) {
				name = mCurrentPhoto.getAuthor().getUserName();
				if (name == null) {
					name = mCurrentPhoto.getAuthor().getUserId();
				}

				String iconUrl = mCurrentPhoto.getAuthor().getBuddyIconUrl();
				if (iconUrl != null) {
					logger.debug("author buddy icon url: " + iconUrl ); //$NON-NLS-1$
					mImageFetcher.setLoadingImage(R.drawable.empty_photo);
					mImageFetcher.loadImage(iconUrl, image);
				}
			}
			if (name != null) {
				author.setText(name);
			}
		}

		return v;
	}
	
	@Override
	public void onDestroy() {
		mImageFetcher.closeCache();
		super.onDestroy();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mImageFetcher.setExitTasksEarly(false);
	}

	@Override
	public void onPause() {
		mImageFetcher.setExitTasksEarly(true);
		mImageFetcher.flushCache();
		super.onPause();
	}

}
