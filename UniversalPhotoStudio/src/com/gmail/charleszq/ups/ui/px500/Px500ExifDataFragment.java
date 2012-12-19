/**
 * 
 */
package com.gmail.charleszq.ups.ui.px500;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.ui.adapter.ExifAdapter;
import com.gmail.charleszq.ups.utils.IConstants;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class Px500ExifDataFragment extends Fragment {

	private MediaObject mCurrentPhoto;

	/**
	 * 
	 */
	public Px500ExifDataFragment() {

	}

	public static Px500ExifDataFragment newInstance(MediaObject photo) {
		Px500ExifDataFragment f = new Px500ExifDataFragment();
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
		ListView listView = new ListView(getActivity());
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		listView.setLayoutParams(params);

		final ExifAdapter adapter = new ExifAdapter(getActivity(),
				mCurrentPhoto.getExifs());
		listView.setAdapter(adapter);
		return listView;
	}
}
