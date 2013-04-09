/**
 * 
 */
package com.gmail.charleszq.picorner.offline;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.gmail.charleszq.picorner.BuildConfig;
import com.gmail.charleszq.picorner.SPUtil;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.offline.OfflineHandleService.IProgressReporter;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.gmail.charleszq.picorner.utils.ImageUtils;
import com.gmail.charleszq.picorner.utils.ModelUtils;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photos.Extras;
import com.googlecode.flickrjandroid.photosets.Photoset;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrPhotoSetOfflineProcessor implements
		IOfflinePhotoCollectionProcessor {

	private static final String TAG = FlickrPhotoSetOfflineProcessor.class
			.getSimpleName();
	private static final int PAGE_SIZE = 100;

	/**
	 * The extras.
	 */
	private Set<String> mExtras = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.picorner.offline.IOfflinePhotoCollectionService#process
	 * (com.gmail.charleszq.picorner.offline.IOfflineViewParameter)
	 */
	@Override
	public void process(Context ctx, IOfflineViewParameter param,
			boolean download, IProgressReporter reporter) {

		String controlFileName = param.getControlFileName();
		boolean isControlFileExist = OfflineControlFileUtil.isFileExist(ctx,
				controlFileName);
		if (isControlFileExist) {
			List<MediaObject> read = readPhotos(ctx, param);
			if (read != null) {
				Log.d(TAG, read.size() + " photos saved in file before."); //$NON-NLS-1$
				boolean hasUpdatesOnServer = saveDeltaHandle(ctx, param, read);
				if (!hasUpdatesOnServer && !download) {
					return;
				}
			}
		} else {
			firstTimeHandle(ctx, param);
		}

		// starts the download
		List<MediaObject> photos = readPhotos(ctx, param);
		if (photos == null) {
			if (BuildConfig.DEBUG)
				Log.e(TAG, "error to read cache photo collection file."); //$NON-NLS-1$
			return;
		}

		if (BuildConfig.DEBUG)
			Log.d(TAG, "Begin to download photos."); //$NON-NLS-1$
		int progress = 0;
		for (MediaObject photo : photos) {
			if (BuildConfig.DEBUG)
				Log.d(TAG,
						String.format(
								"processing url '%s'.", photo.getLargeUrl())); //$NON-NLS-1$
			String photoFileName = OfflineControlFileUtil
					.getOfflinePhotoFileName(photo);
			if (OfflineControlFileUtil.isFileExist(ctx, photoFileName)) {
				if (BuildConfig.DEBUG)
					Log.d(TAG, String.format(
							"photo %s was downloaded before.", photo.getId())); //$NON-NLS-1$
				reporter.reportProgress(photos.size(), progress++);
				continue;
			}

			String url = photo.getLargeUrl();
			try {
				FileOutputStream fos = ctx.openFileOutput(photoFileName,
						Context.MODE_PRIVATE);
				boolean ret = ImageUtils.downloadUrlToStream(url, fos);
				if (ret) {
					if (BuildConfig.DEBUG)
						Log.d(TAG, String.format(
								"photo %s saved for offline view later.", url)); //$NON-NLS-1$
				} else {
					ctx.deleteFile(photoFileName);
					if (BuildConfig.DEBUG)
						Log.w(TAG, "unable to download the image: " + url); //$NON-NLS-1$
				}
			} catch (Exception e) {
				ctx.deleteFile(photoFileName);
				if (BuildConfig.DEBUG)
					Log.e(TAG,
							"error to download and save photo: " + e.getMessage()); //$NON-NLS-1$
			}
			reporter.reportProgress(photos.size(), progress++);
		}
	}

	/**
	 * Returns <code>false</code> if there is no update; <code>true</code> if
	 * there is, and update the local cache photo collection control file.
	 * 
	 * @param ctx
	 * @param param
	 * @param photos
	 * @return
	 */
	private boolean saveDeltaHandle(Context ctx, IOfflineViewParameter param,
			List<MediaObject> photos) {
		int serverPhotoCount = getCurrentCollectionPhotoCount(ctx, param);
		if (serverPhotoCount <= photos.size()) {
			// no addition on server for this photo set.
			if (BuildConfig.DEBUG)
				Log.d(TAG, "no update for this photo set."); //$NON-NLS-1$
			return false;
		}

		Log.d(TAG, String.format("before, there are %d photos", photos.size())); //$NON-NLS-1$
		int delta = serverPhotoCount - photos.size();
		int lastPage = getLastPage(serverPhotoCount, delta);
		boolean duplicateFound = false;
		int newPhotoAdded = 0;
		while (lastPage > 0 && newPhotoAdded < delta) {
			MediaObjectCollection col = getPhotoForPage(ctx, param, lastPage,
					delta);
			if (col != null) {
				for (MediaObject p : col.getPhotos()) {
					if (photos.contains(p)) {
						continue;
					} else {
						photos.add(0, p);
						newPhotoAdded++;
					}
				}
			}

			if (duplicateFound)
				break;
			lastPage--;
		}
		if (BuildConfig.DEBUG)
			Log.d(TAG, String.format(
					"after,  there are %d photos.", photos.size())); //$NON-NLS-1$

		// if exceeded the limit, remove some old photos.
		int maxSize = SPUtil.getMaxPhotoSize(ctx);
		if (photos.size() > maxSize) {
			photos = photos.subList(0, maxSize);
		}

		savePhotoList(ctx, param, photos);
		return true;
	}

	/**
	 * Saves the whole photo information.
	 * 
	 * @param ctx
	 * @param param
	 */
	private void firstTimeHandle(Context ctx, IOfflineViewParameter param) {
		int serverPhotoCount = getCurrentCollectionPhotoCount(ctx, param);
		if (serverPhotoCount == -1) {
			return;
		}

		int lastPageNo = getLastPage(serverPhotoCount, PAGE_SIZE);
		List<MediaObject> photos = new ArrayList<MediaObject>();
		int maxSize = SPUtil.getMaxPhotoSize(ctx);

		int pageIndex = 0;
		while (lastPageNo > 0) {
			MediaObjectCollection col = getPhotoForPage(ctx, param, lastPageNo,
					PAGE_SIZE);

			if (col != null) {
				for (MediaObject p : col.getPhotos()) {
					photos.add(pageIndex, p);
					if (photos.size() >= maxSize)
						break;
				}
			} else {
				break;
			}
			pageIndex = photos.size();
			if (photos.size() >= maxSize)
				break;
			lastPageNo--;
		}

		if (!photos.isEmpty()) {
			savePhotoList(ctx, param, photos);
		}
	}

	private MediaObjectCollection getPhotoForPage(Context ctx,
			IOfflineViewParameter param, int pageNo, int pageSize) {
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(ctx);
		if (mExtras == null) {
			prepareExtras();
		}
		try {
			Photoset ps = f.getPhotosetsInterface().getPhotos(
					param.getPhotoCollectionId(), mExtras,
					Flickr.PRIVACY_LEVEL_NO_FILTER, pageSize, pageNo);
			User user = ps.getOwner();
			MediaObjectCollection col = ModelUtils.convertFlickrPhotoList(
					ps.getPhotoList(), user);
			return col;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns the last page which contains the most recent photos.
	 * 
	 * @param serverPhotoCount
	 * @return
	 */
	private int getLastPage(int serverPhotoCount, int pageSize) {
		int maxPage = serverPhotoCount / pageSize;
		int remain = serverPhotoCount % pageSize;
		if (remain > 0) {
			maxPage++;
		}
		return maxPage;
	}

	private int getCurrentCollectionPhotoCount(Context ctx,
			IOfflineViewParameter param) {
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(ctx);
		try {
			Photoset ps = f.getPhotosetsInterface().getInfo(
					param.getPhotoCollectionId());
			if (BuildConfig.DEBUG)
				Log.d(TAG,
						"offline photo set photo count: " + ps.getPhotoCount()); //$NON-NLS-1$
			return ps.getPhotoCount();
		} catch (Exception e) {
			if (BuildConfig.DEBUG)
				Log.w(TAG, "unable to get the photo set information: " //$NON-NLS-1$
						+ e.getMessage());
			return -1;
		}
	}

	private void prepareExtras() {
		mExtras = new HashSet<String>();
		mExtras.add(Extras.URL_S);
		mExtras.add(Extras.URL_L);
		mExtras.add(Extras.OWNER_NAME);
		mExtras.add(Extras.GEO);
		mExtras.add(Extras.TAGS);
		mExtras.add(Extras.VIEWS);
		mExtras.add(Extras.DESCRIPTION);
	}

	private void savePhotoList(Context ctx, IOfflineViewParameter param,
			List<MediaObject> photos) {

		String controlFileName = param.getControlFileName();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(ctx.openFileOutput(controlFileName,
					Context.MODE_PRIVATE));
			oos.writeObject(photos);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		} finally {
			if (oos != null) {
				try {
					oos.flush();
					oos.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<MediaObject> readPhotos(Context ctx,
			IOfflineViewParameter param) {
		String controlFileName = param.getControlFileName();
		if (!OfflineControlFileUtil.isFileExist(ctx, controlFileName)) {
			return null;
		}
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(ctx.openFileInput(controlFileName));
			List<MediaObject> photos = (List<MediaObject>) ois.readObject();
			return photos;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return null;
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	public List<MediaObject> getCachedPhotos(Context ctx,
			IOfflineViewParameter param) {
		return readPhotos(ctx, param);
	}

	@Override
	public int removeCachedPhotos(Context ctx, IOfflineViewParameter param) {
		List<MediaObject> photos = readPhotos(ctx, param);
		if (photos == null) {
			return -1;
		}
		int count = 0;
		for (MediaObject photo : photos) {
			String photoFileName = OfflineControlFileUtil
					.getOfflinePhotoFileName(photo);
			if (ctx.deleteFile(photoFileName)) {
				count++;
			}
		}
		return count;
	}

	@Override
	public int exportCachedPhotos(Context ctx, IOfflineViewParameter param,
			String foldername, boolean overwrite, IProgressReporter reporter ) throws IOException {
		List<MediaObject> photos = readPhotos(ctx, param);
		int count = 0;
		if (photos != null) {
			// create the target folder
			File root = new File(Environment.getExternalStorageDirectory(),
					IConstants.SD_CARD_FOLDER_NAME);
			if (!root.exists())
				root.mkdir();
			File targetFolder = new File(root, foldername);
			if (!targetFolder.exists())
				if( !targetFolder.mkdir() ) {
					throw new IOException("unable to create the folder."); //$NON-NLS-1$
				}

			int progress = 0;
			for (MediaObject photo : photos) {
				String filename = OfflineControlFileUtil
						.getOfflinePhotoFileName(photo);
				File sourceFile = ctx.getFileStreamPath(filename);
				File targetFile = new File(targetFolder, filename);
				reporter.reportProgress(photos.size(), progress++);
				if( targetFile.exists() && !overwrite ) 
					continue;
				try {
					copyFile(sourceFile,targetFile);
					count ++;
				} catch (IOException e) {
				}
			}
		}
		return count;
	}

	private void copyFile(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
}
