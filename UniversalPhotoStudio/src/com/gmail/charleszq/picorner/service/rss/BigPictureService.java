/**
 * 
 */
package com.gmail.charleszq.picorner.service.rss;

import java.util.List;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import android.util.Log;

import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.model.MediaSourceType;
import com.gmail.charleszq.picorner.service.IPhotoService;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class BigPictureService implements IPhotoService {

	private static final String BIG_PIC_RSS_LINK = "http://syndication.boston.com/bigpicture/index.xml"; //$NON-NLS-1$
	private static final String TAG = BigPictureService.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.service.IPhotoService#getPhotos(int,
	 * int)
	 */
	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		if (pageNo > 0)
			return null;
		
		RSSReader reader = new RSSReader();
		try {
			RSSFeed feed = reader.load(BIG_PIC_RSS_LINK);
			List<RSSItem> items = feed.getItems();
			Log.d(TAG, items.size() + " items"); //$NON-NLS-1$

			MediaObjectCollection coll = new MediaObjectCollection();
			for (RSSItem item : items) {
				String url = findImageLink(item.getDescription());
				if( url == null || url.trim().length() == 0 )
					continue;
				
				Log.d(TAG, url);
				MediaObject photo = new MediaObject();
				photo.setMediaSource(MediaSourceType.RSS);
				photo.setThumbUrl(url);
				photo.setLargeUrl(url);
				photo.setTitle(item.getTitle());
				coll.addPhoto(photo);
				photo.setId(url);
			}
			return coll;
		} catch (RSSReaderException e) {
			Log.w(TAG, e.getMessage());
		}
		return null;
	}
	
	private String findImageLink(String str) {
		int index = str.indexOf("<img src="); //$NON-NLS-1$
		if( index == -1 )
			return null;
		StringBuilder sb = new StringBuilder();
		while (true) {
			char ch = str.charAt(index + 10);
			if (ch == '"') {
				break;
			}
			sb.append(ch);
			index++;
			if( index >= str.length() )
				break;
		}
		return sb.toString();
	}

}
