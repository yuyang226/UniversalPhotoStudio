package com.gmail.charleszq.ups.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.ui.PhotoDetailCommentsFragment;
import com.gmail.charleszq.ups.ui.PhotoDetailGeneralFragment;

public class PhotoDetailViewPagerAdapter extends FragmentPagerAdapter {

	private MediaObject mPhoto;
	private Context mContext;

	public PhotoDetailViewPagerAdapter(FragmentManager fragmentManager,
			MediaObject photo, Context context) {
		super(fragmentManager);
		mPhoto = photo;
		mContext = context;
	}

	@Override
	public Fragment getItem(int pos) {
		switch (pos) {
		case 1:
			PhotoDetailCommentsFragment commentsFrag = PhotoDetailCommentsFragment
					.newInstance(mPhoto);
			return commentsFrag;
		default:
			PhotoDetailGeneralFragment f = PhotoDetailGeneralFragment
					.newInstance(mPhoto);
			return f;
		}
	}

	@Override
	public int getCount() {
		switch (mPhoto.getMediaSource()) {
		case FLICKR:
			return 5;
		case INSTAGRAM:
			return 2;
		}
		return 0;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case 0:
			return mContext.getString(R.string.flickr_detail_general_title);
		case 1:
			return mContext.getString(R.string.flickr_detail_comments_title);
		case 2:
			return mContext.getString(R.string.flickr_detail_exif_title);
		case 3:
			return mContext.getString(R.string.flickr_detail_in_sets);
		case 4:
			return mContext.getString(R.string.flickr_detail_map);
		}
		return super.getPageTitle(position);
	}

}
