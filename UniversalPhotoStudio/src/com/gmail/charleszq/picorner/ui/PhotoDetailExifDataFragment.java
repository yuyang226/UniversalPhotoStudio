/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.yuyang226.j500px.photos.Photo;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.ExifData;
import com.gmail.charleszq.picorner.model.GeoLocation;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.FetchFlickrPhotoExifTask;
import com.gmail.charleszq.picorner.task.px500.PxFetchPhotoExifTask;
import com.gmail.charleszq.picorner.ui.helper.ExifAdapter;
import com.gmail.charleszq.picorner.utils.IConstants;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PhotoDetailExifDataFragment extends Fragment {

	private static final String TAG = PhotoDetailExifDataFragment.class.getName();

	private MediaObject mCurrentPhoto;

	// view controls
	private ProgressBar mProgressBar;
	private TextView mNoExifInfoText;
	private ListView mExifList;

	private List<ExifData> mExifs = new ArrayList<ExifData>();

	/**
	 * 
	 */
	public PhotoDetailExifDataFragment() {
	}

	public static PhotoDetailExifDataFragment newInstance(MediaObject photo) {
		PhotoDetailExifDataFragment f = new PhotoDetailExifDataFragment();
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

		if (!mExifs.isEmpty()) {
			mProgressBar.setVisibility(View.INVISIBLE);
			return v;
		}

		switch (mCurrentPhoto.getMediaSource()) {
		case INSTAGRAM:
			break;
		case FLICKR:
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
			break;
		case PX500:
			PxFetchPhotoExifTask pxTask = new PxFetchPhotoExifTask();
			pxTask.addTaskDoneListener(new IGeneralTaskDoneListener<Photo>() {

				@Override
				public void onTaskDone(Photo result) {
					mProgressBar.setVisibility(View.INVISIBLE);
					if (result == null) {
						mNoExifInfoText.setVisibility(View.VISIBLE);
					} else {
						MediaObject p = new MediaObject();
						p = com.gmail.charleszq.picorner.utils.ModelUtils
								.handlePx500PhotoExif(p, result);
						mExifs.clear();
						mExifs.addAll(p.getExifs());
						adapter.notifyDataSetChanged();

						try {
							if (result.getLatitude() != null
									&& result.getLongitude() != null) {
								GeoLocation loc = new GeoLocation();
								loc.setLatitude(result.getLatitude());
								loc.setLongitude(result.getLongitude());
								mCurrentPhoto.setLocation(loc);
								Log.d(TAG, "lat: " + loc.getLatitude()); //$NON-NLS-1$
								Log.d(TAG, "log: " + loc.getLongitude()); //$NON-NLS-1$
								((PhotoDetailActivity) getActivity())
										.notifyDataChanged();
							}
						} catch (Exception e) {
							// try catch number format exception.
							Log.w(TAG,
									e.getMessage() == null ? "Unable to get the location information." : e.getMessage()); //$NON-NLS-1$
						}
					}
				}
			});
			pxTask.execute(mCurrentPhoto.getId());
			break;
		}
		return v;
	}
}
