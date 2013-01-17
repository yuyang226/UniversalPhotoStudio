package com.gmail.charleszq.picorner.ui.helper;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.ui.PhotoDetailCommentsFragment;
import com.gmail.charleszq.picorner.ui.PhotoDetailExifDataFragment;
import com.gmail.charleszq.picorner.ui.PhotoDetailGeneralFragment;
import com.gmail.charleszq.picorner.ui.PhotoDetailLikesFragment;
import com.gmail.charleszq.picorner.ui.PhotoDetailMapFragment;
import com.gmail.charleszq.picorner.ui.flickr.MyFlickrPhotoGeneralFragment;
import com.gmail.charleszq.picorner.ui.flickr.OrganizeMyFlickrPhotoFragment;

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
		PicornerApplication app = (PicornerApplication) ((Activity) mContext)
				.getApplication();
		List<Fragment> fragments = new ArrayList<Fragment>();
		switch (mPhoto.getMediaSource()) {
		case FLICKR:
			if (app.isMyOwnPhoto(mPhoto)) {
				fragments.add(MyFlickrPhotoGeneralFragment.newInstance(mPhoto));
				fragments
						.add(OrganizeMyFlickrPhotoFragment.newInstance(mPhoto));
			} else {
				fragments.add(PhotoDetailGeneralFragment.newInstance(mPhoto));
			}
			fragments.add(PhotoDetailCommentsFragment.newInstance(mPhoto));
			fragments.add(PhotoDetailExifDataFragment.newInstance(mPhoto));
			fragments.add(PhotoDetailLikesFragment.newInstance(mPhoto));
			fragments.add(PhotoDetailMapFragment.newMyInstance(mPhoto));
			break;
		case INSTAGRAM:
			fragments.add(PhotoDetailGeneralFragment.newInstance(mPhoto));
			fragments.add(PhotoDetailCommentsFragment.newInstance(mPhoto));
			fragments.add(PhotoDetailLikesFragment.newInstance(mPhoto));
			fragments.add(PhotoDetailMapFragment.newMyInstance(mPhoto));
			break;
		case PX500:
			fragments.add(PhotoDetailGeneralFragment.newInstance(mPhoto));
			fragments.add(PhotoDetailCommentsFragment.newInstance(mPhoto));
			fragments.add(PhotoDetailExifDataFragment.newInstance(mPhoto));
			fragments.add(PhotoDetailMapFragment.newMyInstance(mPhoto));
			break;
		}
		return fragments.get(pos);
	}

	@Override
	public int getCount() {
		PicornerApplication app = (PicornerApplication) ((Activity) mContext)
				.getApplication();
		int count = 0;
		switch (mPhoto.getMediaSource()) {
		case INSTAGRAM:
			count = 4;
			break;
		case FLICKR:
			count = 5;
			if (app.isMyOwnPhoto(mPhoto))
				count++;
			break;
		case PX500:
			count = 4;
			break;
		}
		if (mPhoto.getLocation() == null) {
			count--;
		}
		return count >= 0 ? count : 0;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		PicornerApplication app = (PicornerApplication) ((Activity) mContext)
				.getApplication();
		List<CharSequence> titles = new ArrayList<CharSequence>();
		switch (mPhoto.getMediaSource()) {
		case FLICKR:
			titles.add(mContext.getString(R.string.flickr_detail_general_title));
			if (app.isMyOwnPhoto(mPhoto)) {
				titles.add(mContext
						.getString(R.string.menu_item_org_my_flickr_photo));
			}
			titles.add(mContext
					.getString(R.string.flickr_detail_comments_title));
			titles.add(mContext.getString(R.string.flickr_detail_exif_title));
			titles.add(mContext.getString(R.string.ig_like_users));
			titles.add(mContext.getString(R.string.flickr_detail_map));
			break;
		case INSTAGRAM:
			titles.add(mContext.getString(R.string.flickr_detail_general_title));
			titles.add(mContext
					.getString(R.string.flickr_detail_comments_title));
			titles.add(mContext.getString(R.string.ig_like_users));
			titles.add(mContext.getString(R.string.flickr_detail_map));
			break;
		case PX500:
			titles.add(mContext.getString(R.string.flickr_detail_general_title));
			titles.add(mContext
					.getString(R.string.flickr_detail_comments_title));
			titles.add(mContext.getString(R.string.flickr_detail_exif_title));
			titles.add(mContext.getString(R.string.flickr_detail_map));
			break;
		}
		return titles.get(position);
	}

}
