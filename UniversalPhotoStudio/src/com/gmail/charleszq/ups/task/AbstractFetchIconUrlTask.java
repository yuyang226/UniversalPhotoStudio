/**
 * 
 */
package com.gmail.charleszq.ups.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.gmail.charleszq.ups.utils.ImageFetcher;

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
	protected ImageFetcher mImageFetcher;
	protected ImageView mImageView;

	public AbstractFetchIconUrlTask(Context ctx) {
		this.mContext = ctx;
	}

	@Override
	protected void onPostExecute(String result) {
		if (result != null) {
			logger.debug( "Fetching command icon: " + result ); //$NON-NLS-1$
			if (mImageFetcher != null && mImageView != null) {
				mImageFetcher.loadImage(result, mImageView);
			}
		}
	}
	
	protected void beforeExecute(Object... params) {
		if( params.length == 2 ) {
			mImageFetcher = (ImageFetcher) params[0];
			mImageView = (ImageView) params[1];
		}
	}
	
}
