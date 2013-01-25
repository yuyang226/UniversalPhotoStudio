/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.gmail.charleszq.picorner.BuildConfig;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.dp.IPhotosProvider;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.offline.OfflineControlFileUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PhotoGridAdapter extends BaseAdapter {

	private Context									mContext;
	private IPhotosProvider							mPhotos;
	private ImageLoader								mImageFetcher;

	private int										mNumColumns	= 0;
	private int										mItemHeight;
	private android.widget.AbsListView.LayoutParams	mImageViewLayoutParams;

	private DisplayImageOptions						mImageDisplayOptions;

	/**
	 * 
	 */
	public PhotoGridAdapter(Context context, IPhotosProvider provider,
			ImageLoader fetcher) {
		this.mContext = context;
		this.mPhotos = provider;
		this.mImageFetcher = fetcher;

		mImageViewLayoutParams = new GridView.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		mImageDisplayOptions = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.empty_photo).cacheInMemory()
				.showImageForEmptyUri(R.drawable.empty_photo).cacheOnDisc()
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return mPhotos.getCurrentSize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return mPhotos.getMediaObject(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) { // if it's not recycled, instantiate and
									// initialize
			imageView = new ImageView(mContext);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setLayoutParams(mImageViewLayoutParams);
		} else { // Otherwise re-use the converted view
			imageView = (ImageView) convertView;
		}

		// Check the height matches our calculated column width
		if (imageView.getLayoutParams().height != mItemHeight) {
			imageView.setLayoutParams(mImageViewLayoutParams);
		}

		MediaObject photo = mPhotos.getMediaObject(position);
		if (photo != null) {
			String filename = OfflineControlFileUtil
					.getOfflinePhotoFileName(photo);
			if (OfflineControlFileUtil.isFileExist(mContext, filename)) {
				File f = mContext.getFileStreamPath(filename);
				Uri uri = Uri.fromFile(f);
				mImageFetcher.displayImage(uri.toString(), imageView,
						mImageDisplayOptions);
				if (BuildConfig.DEBUG)
					Log.d(PhotoGridAdapter.class.getSimpleName(),
							"Load thumb image from offline cache."); //$NON-NLS-1$
			} else {
				mImageFetcher.displayImage(photo.getThumbUrl(), imageView,
						mImageDisplayOptions);
			}
		}
		return imageView;
	}

	public void setNumColumns(int numColumns) {
		this.mNumColumns = numColumns;
	}

	public void setItemHeight(int height) {
		if (height == mItemHeight) {
			return;
		}
		mItemHeight = height;
		mImageViewLayoutParams = new GridView.LayoutParams(
				LayoutParams.MATCH_PARENT, mItemHeight);
		// mImageFetcher.setImageSize(height);
		notifyDataSetChanged();
	}

	public int getNumColumns() {
		return mNumColumns;
	}

}
