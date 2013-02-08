package com.gmail.charleszq.picorner.ui.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.ui.PhotoDetailActivity;
import com.gmail.charleszq.picorner.ui.PhotoDetailCommentsFragment;
import com.gmail.charleszq.picorner.ui.PhotoDetailExifDataFragment;
import com.gmail.charleszq.picorner.ui.PhotoDetailGeneralFragment;
import com.gmail.charleszq.picorner.ui.PhotoDetailLikesFragment;
import com.gmail.charleszq.picorner.ui.PhotoDetailMapFragment;
import com.gmail.charleszq.picorner.ui.flickr.ManagePhotoGroupFragment;
import com.gmail.charleszq.picorner.ui.flickr.MyFlickrPhotoGeneralFragment;
import com.gmail.charleszq.picorner.ui.flickr.OrganizeMyFlickrPhotoFragment;

public class PhotoDetailViewPagerAdapter extends FragmentPagerAdapter {

	private MediaObject mPhoto;
	private Context mContext;

	private List<Fragment> mFragments;
	private List<String> mTitles;

	private Map<String, Integer> mPageIndexes = new HashMap<String, Integer>();

	public PhotoDetailViewPagerAdapter(FragmentManager fragmentManager,
			MediaObject photo, Context context) {
		super(fragmentManager);
		mPhoto = photo;
		mContext = context;

		mFragments = new ArrayList<Fragment>();
		mTitles = new ArrayList<String>();
		prepareFragmentsAndTitles();
	}

	private void prepareFragmentsAndTitles() {
		mFragments.clear();
		mTitles.clear();

		PicornerApplication app = (PicornerApplication) ((Activity) mContext)
				.getApplication();

		switch (mPhoto.getMediaSource()) {
		case FLICKR:
			if (app.isMyOwnPhoto(mPhoto)) {
				mFragments
						.add(MyFlickrPhotoGeneralFragment.newInstance(mPhoto));
				mFragments.add(OrganizeMyFlickrPhotoFragment
						.newInstance(mPhoto));
				mPageIndexes.put(PhotoDetailActivity.MY_F_ORG_PHOTO_SET_PAGE,
						mFragments.size() - 1);
				mFragments.add(ManagePhotoGroupFragment.newInstance(mPhoto));
				mPageIndexes.put(PhotoDetailActivity.MY_F_ORG_GROUP_PAGE,
						mFragments.size() - 1);

			} else {
				mFragments.add(PhotoDetailGeneralFragment.newInstance(mPhoto));
			}
			mFragments.add(PhotoDetailCommentsFragment.newInstance(mPhoto));
			mPageIndexes.put(PhotoDetailActivity.COMMENT_PAGE,
					mFragments.size() - 1);
			mFragments.add(PhotoDetailExifDataFragment.newInstance(mPhoto));
			mPageIndexes.put(PhotoDetailActivity.EXIF_PAGE,
					mFragments.size() - 1);
			mFragments.add(PhotoDetailLikesFragment.newInstance(mPhoto));
			break;
		case INSTAGRAM:
			mFragments.add(PhotoDetailGeneralFragment.newInstance(mPhoto));
			mFragments.add(PhotoDetailCommentsFragment.newInstance(mPhoto));
			mPageIndexes.put(PhotoDetailActivity.COMMENT_PAGE,
					mFragments.size() - 1);
			mFragments.add(PhotoDetailLikesFragment.newInstance(mPhoto));
			break;
		case PX500:
			mFragments.add(PhotoDetailGeneralFragment.newInstance(mPhoto));
			mFragments.add(PhotoDetailCommentsFragment.newInstance(mPhoto));
			mPageIndexes.put(PhotoDetailActivity.COMMENT_PAGE,
					mFragments.size() - 1);
			mFragments.add(PhotoDetailExifDataFragment.newInstance(mPhoto));
			mPageIndexes.put(PhotoDetailActivity.EXIF_PAGE,
					mFragments.size() - 1);
			break;
		}
		if (mPhoto.getLocation() != null) {
			mFragments.add(PhotoDetailMapFragment.newMyInstance(mPhoto));
			mPageIndexes.put(PhotoDetailActivity.MAP_PAGE,
					mFragments.size() - 1);
		}

		switch (mPhoto.getMediaSource()) {
		case FLICKR:
			mTitles.add(mContext
					.getString(R.string.flickr_detail_general_title));
			if (app.isMyOwnPhoto(mPhoto)) {
				mTitles.add(mContext
						.getString(R.string.menu_item_org_my_flickr_photo));
				mTitles.add(mContext
						.getString(R.string.menu_item_mng_my_flickr_group));
			}
			mTitles.add(mContext
					.getString(R.string.flickr_detail_comments_title));
			mTitles.add(mContext.getString(R.string.flickr_detail_exif_title));
			mTitles.add(mContext.getString(R.string.ig_like_users));
			break;
		case INSTAGRAM:
			mTitles.add(mContext
					.getString(R.string.flickr_detail_general_title));
			mTitles.add(mContext
					.getString(R.string.flickr_detail_comments_title));
			mTitles.add(mContext.getString(R.string.ig_like_users));
			break;
		case PX500:
			mTitles.add(mContext
					.getString(R.string.flickr_detail_general_title));
			mTitles.add(mContext
					.getString(R.string.flickr_detail_comments_title));
			mTitles.add(mContext.getString(R.string.flickr_detail_exif_title));
			mTitles.add(mContext.getString(R.string.flickr_detail_map));
			break;
		}
		if (mPhoto.getLocation() != null)
			mTitles.add(mContext.getString(R.string.flickr_detail_map));
	}

	public int getPageIndex(String key) {
		if (mPageIndexes.containsKey(key))
			return mPageIndexes.get(key);
		else
			return -1;
	}

	@Override
	public Fragment getItem(int pos) {
		return mFragments.get(pos);
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mTitles.get(position);
	}

	@Override
	public void notifyDataSetChanged() {
		prepareFragmentsAndTitles();
		super.notifyDataSetChanged();
	}

}
