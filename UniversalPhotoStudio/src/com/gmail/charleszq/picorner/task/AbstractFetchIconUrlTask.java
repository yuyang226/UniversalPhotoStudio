/**
 * 
 */
package com.gmail.charleszq.picorner.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.gmail.charleszq.picorner.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Represents the task to fetch the icon url from the server side.
 * <p>
 * The parameters for this task should be [ImageFetcher, ImageView]
 * 
 * @author Charles(charleszq@gmail.com)
 * 
 */
public abstract class AbstractFetchIconUrlTask extends
		AsyncTask<Object, Integer, String> {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Should be an activity, so we can get access to Application.
	 */
	protected Context mContext;
	protected ImageLoader mImageFetcher;
	protected ImageView mImageView;

	public AbstractFetchIconUrlTask(Context ctx) {
		this.mContext = ctx;
	}

	@Override
	protected void onPostExecute(String result) {
		if (result != null) {
			logger.debug("Fetching command icon: " + result); //$NON-NLS-1$
			if (mImageFetcher != null && mImageView != null) {
				DisplayImageOptions options = new DisplayImageOptions.Builder()
						.showStubImage(R.drawable.empty_photo).cacheInMemory()
						.cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565)
						.imageScaleType(ImageScaleType.EXACTLY).build();
				mImageFetcher.displayImage(result, mImageView, options);
			}
		}
	}

	protected void beforeExecute(Object... params) {
		if (params.length == 2) {
			mImageFetcher = (ImageLoader) params[0];
			mImageView = (ImageView) params[1];
		}
	}

}
