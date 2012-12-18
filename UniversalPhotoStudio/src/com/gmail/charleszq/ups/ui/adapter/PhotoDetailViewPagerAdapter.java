package com.gmail.charleszq.ups.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.ui.PhotoDetailCommentsFragment;
import com.gmail.charleszq.ups.ui.PhotoDetailGeneralFragment;
import com.gmail.charleszq.ups.ui.PhotoDetailLikesFragment;
import com.gmail.charleszq.ups.ui.PhotoDetailMapFragment;
import com.gmail.charleszq.ups.ui.flickr.FlickrExifDataFragment;

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
			switch (mPhoto.getMediaSource()) {
			case PX500:
				return PhotoDetailMapFragment.newMyInstance(mPhoto);
			default:
				PhotoDetailCommentsFragment commentsFrag = PhotoDetailCommentsFragment
						.newInstance(mPhoto);
				return commentsFrag;
			}
		case 2:
			switch (mPhoto.getMediaSource()) {
			case INSTAGRAM:
				return PhotoDetailLikesFragment.newInstance(mPhoto);
			case FLICKR:
				return FlickrExifDataFragment.newInstance(mPhoto);
			}
		case 3:
			switch (mPhoto.getMediaSource()) {
			case INSTAGRAM:
				return PhotoDetailMapFragment.newMyInstance(mPhoto);
			case FLICKR:
				return PhotoDetailLikesFragment.newInstance(mPhoto);
			}
		case 4:
			return PhotoDetailMapFragment.newMyInstance(mPhoto);
		default:
			PhotoDetailGeneralFragment f = PhotoDetailGeneralFragment
					.newInstance(mPhoto);
			return f;
		}
	}

	@Override
	public int getCount() {
		switch (mPhoto.getMediaSource()) {
		case INSTAGRAM:
			return mPhoto.getLocation() != null ? 4 : 3;
		case FLICKR:
			return mPhoto.getLocation() != null ? 5 : 4;
		case PX500:
			return mPhoto.getLocation() != null ? 2 : 1;
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
			switch (mPhoto.getMediaSource()) {
			case FLICKR:
				return mContext.getString(R.string.flickr_detail_exif_title);
			case INSTAGRAM:
				return mContext.getString(R.string.ig_like_users);
			}
		case 3:
			switch (mPhoto.getMediaSource()) {
			case FLICKR:
				return mContext.getString(R.string.ig_like_users);
			case INSTAGRAM:
				return mContext.getString(R.string.flickr_detail_map);
			}
		case 4:
			return mContext.getString(R.string.flickr_detail_map);
		}
		return super.getPageTitle(position);
	}

}
