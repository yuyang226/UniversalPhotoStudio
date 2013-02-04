/**
 * 
 */
package com.gmail.charleszq.picorner.task;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.charleszq.picorner.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

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

	/**
	 * The log tag.
	 */
	protected String	TAG	= getClass().getSimpleName();

	/**
	 * Should be an activity, so we can get access to Application.
	 */
	protected Context	mContext;
	/**
	 * Either an ImageView or a TextView.
	 */
	protected WeakReference<View> mIconViewRef;
	
	

	public AbstractFetchIconUrlTask(Context ctx) {
		this.mContext = ctx;
	}

	@Override
	protected void onPostExecute(String result) {
		if (result != null) {
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.empty_photo).cacheInMemory()
					.cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565)
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).build();
			if (mIconViewRef != null) {
				View iconView = mIconViewRef.get();
				if( iconView == null ) {
					return;
				}
				ImageLoader imageLoader = ImageLoader.getInstance();
				if (ImageView.class.isInstance(iconView)) {
					imageLoader.displayImage(result, (ImageView) iconView,
							options);
				} else {
					final TextView text = (TextView) iconView;
					imageLoader.loadImage(mContext, result,
							new SimpleImageLoadingListener() {

								@Override
								public void onLoadingComplete(Bitmap loadedImage) {
									BitmapDrawable drawable = new BitmapDrawable(
											mContext.getResources(),
											loadedImage);
									text.setCompoundDrawables(drawable, null,
											null, null);
								}
							});
				}
			}
		}
	}

	protected void beforeExecute(Object... params) {
		mIconViewRef = new WeakReference<View>((View) params[0]);
	}

}
