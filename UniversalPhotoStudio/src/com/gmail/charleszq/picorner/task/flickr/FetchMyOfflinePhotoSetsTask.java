/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.gmail.charleszq.picorner.offline.IOfflineViewParameter;
import com.gmail.charleszq.picorner.offline.OfflineControlFileUtil;
import com.gmail.charleszq.picorner.offline.OfflinePhotoCollectionType;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.googlecode.flickrjandroid.photosets.Photoset;

/**
 * Represents the task to fetch all my offline photo sets.
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FetchMyOfflinePhotoSetsTask extends
		AbstractContextAwareTask<Integer, Integer, List<Photoset>> {

	public FetchMyOfflinePhotoSetsTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected List<Photoset> doInBackground(Integer... params) {
		List<IOfflineViewParameter> offlines = OfflineControlFileUtil
				.getExistingOfflineParameters(mContext);
		List<Photoset> sets = new ArrayList<Photoset>();
		int page = params[0];
		if (page != 1) {
			return sets; // only 1 page here.
		}
		for (IOfflineViewParameter offline : offlines) {
			if (OfflinePhotoCollectionType.PHOTO_SET.equals(offline
					.getPhotoCollectionType())) {
				Photoset ps = new Photoset();
				ps.setId(offline.getPhotoCollectionId());
				ps.setTitle(offline.getTitle() == null ? offline
						.getPhotoCollectionId() : offline.getTitle());
				sets.add(ps);
			}
		}
		return sets;
	}

}
