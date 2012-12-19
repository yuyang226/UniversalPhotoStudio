/**
 * 
 */
package com.gmail.charleszq.ups.ui.flickr;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.model.ExifData;
import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.ups.task.flickr.FetchFlickrPhotoExifTask;
import com.gmail.charleszq.ups.ui.adapter.ExifAdapter;
import com.gmail.charleszq.ups.utils.IConstants;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrExifDataFragment extends Fragment {

	private MediaObject mCurrentPhoto;

	// view controls
	private ProgressBar mProgressBar;
	private TextView mNoExifInfoText;
	private ListView mExifList;

	private List<ExifData> mExifs = new ArrayList<ExifData>();

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
		this.setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.flickr_exif_list, null);

		mProgressBar = (ProgressBar) v
				.findViewById(R.id.exif_load_progress_bar);
		mNoExifInfoText = (TextView) v.findViewById(R.id.no_exif_info_text);
		mExifList = (ListView) v.findViewById(R.id.exif_info_list_view);
		final ExifAdapter adapter = new ExifAdapter(getActivity(), mExifs);
		mExifList.setAdapter(adapter);

		FetchFlickrPhotoExifTask task = new FetchFlickrPhotoExifTask(
				getActivity());
		task.addTaskDoneListener(new IGeneralTaskDoneListener<List<ExifData>>() {

			@Override
			public void onTaskDone(List<ExifData> result) {
				mProgressBar.setVisibility(View.INVISIBLE);
				if (result == null || result.isEmpty()) {
					mNoExifInfoText.setVisibility(View.VISIBLE);
				} else {
					mExifs.clear();
					mExifs.addAll(result);
					adapter.notifyDataSetChanged();
				}

			}
		});
		task.execute(mCurrentPhoto.getId(), mCurrentPhoto.getSecret());
		return v;
	}
}
