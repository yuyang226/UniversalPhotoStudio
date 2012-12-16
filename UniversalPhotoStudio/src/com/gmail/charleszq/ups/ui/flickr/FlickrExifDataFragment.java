/**
 * 
 */
package com.gmail.charleszq.ups.ui.flickr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.task.flickr.FetchFlickrPhotoExifTask;
import com.gmail.charleszq.ups.utils.IConstants;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrExifDataFragment extends Fragment {

	private MediaObject mCurrentPhoto;

	/**
	 * 
	 */
	public FlickrExifDataFragment() {
	}

	public static FlickrExifDataFragment newInstance(MediaObject photo) {
		FlickrExifDataFragment f = new FlickrExifDataFragment();
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.flickr_exif_list, null);
		
		FetchFlickrPhotoExifTask task = new FetchFlickrPhotoExifTask(getActivity());
		task.execute(mCurrentPhoto.getId());
		return v;
	}

}
