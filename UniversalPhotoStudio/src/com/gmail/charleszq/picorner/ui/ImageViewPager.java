package com.gmail.charleszq.picorner.ui;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * 
 * @author Charles(charleszq@gmail.com)
 *
 */
public class ImageViewPager extends ViewPager {

	public ImageViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ImageViewPager(Context context) {
		super(context);
	}

	@Override
	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
		if (v instanceof TouchImageView) {
			TouchImageView imageView = (TouchImageView) v;
			return imageView.canScrollHorizontally(dx);
		}

		return super.canScroll(v, checkV, dx, x, y);
	}
}