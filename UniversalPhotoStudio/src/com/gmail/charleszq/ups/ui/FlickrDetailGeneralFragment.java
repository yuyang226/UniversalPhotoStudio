/**
 * 
 */
package com.gmail.charleszq.ups.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.charleszq.ups.R;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrDetailGeneralFragment extends Fragment {

	/**
	 * 
	 */
	public FlickrDetailGeneralFragment() {
	}

	public static FlickrDetailGeneralFragment newInstance(String photoId) {
		FlickrDetailGeneralFragment f = new FlickrDetailGeneralFragment();
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.flickr_detail_general, container,
				false);
		TextView title = (TextView) v
				.findViewById(R.id.flickr_detail_general_photo_title);
		title.setText("Hello world");
		return v;
	}

}
