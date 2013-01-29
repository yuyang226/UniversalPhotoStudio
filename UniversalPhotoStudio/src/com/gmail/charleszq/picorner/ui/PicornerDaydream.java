/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import android.annotation.TargetApi;
import android.service.dreams.DreamService;

import com.gmail.charleszq.picorner.R;

/**
 * @author charleszq
 *
 */
@TargetApi(17)
public class PicornerDaydream extends DreamService {

	/* (non-Javadoc)
	 * @see android.service.dreams.DreamService#onDreamingStarted()
	 */
	@Override
	public void onDreamingStarted() {
		super.onDreamingStarted();
		this.setContentView(R.layout.day_dream);
	}


}
