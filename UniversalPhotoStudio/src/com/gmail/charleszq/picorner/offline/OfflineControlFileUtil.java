/**
 * 
 */
package com.gmail.charleszq.picorner.offline;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.widget.ImageView;

import com.gmail.charleszq.picorner.BuildConfig;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.model.MediaSourceType;
import com.gmail.charleszq.picorner.utils.IConstants;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public final class OfflineControlFileUtil {

	private static final String TAG = OfflineControlFileUtil.class
			.getSimpleName();

	/**
	 * Tries to load the image from cache.
	 * 
	 * @param photo
	 * @param imageView
	 * @return
	 */
	public static boolean loadImageFromCache(MediaObject photo,
			ImageView imageView) {
		boolean result = false;
		File offlineFolder = createOfflineFolderIfNeccessary();
		if (offlineFolder == null) {
			return result;
		}

		File photoSourceFolder = null;
		switch (photo.getMediaSource()) {
		case FLICKR:
			photoSourceFolder = new File(offlineFolder,
					IOfflineViewParameter.OFFLINE_FLICKR_FOLDER_NAME);
			break;
		case INSTAGRAM:
			photoSourceFolder = new File(offlineFolder,
					IOfflineViewParameter.OFFLINE_INSTAGRAM_FOLDER_NAME);
			break;
		case PX500:
			photoSourceFolder = new File(offlineFolder,
					IOfflineViewParameter.OFFLINE_500PX_FOLDER_NAME);
			break;
		}

		File imageFolder = new File(photoSourceFolder,
				IOfflineViewParameter.OFFLINE_IMAGE_FOLDER_NAME);
		File imageFile = new File(imageFolder, photo.getId() + ".png"); //$NON-NLS-1$
		if (!imageFile.exists()) {
			return result;
		}

		result = true;
		imageView.setImageBitmap(BitmapFactory.decodeFile(imageFile
				.getAbsolutePath()));
		if (BuildConfig.DEBUG) {
			String title = photo.getTitle();
			if (title == null) {
				title = "no title"; //$NON-NLS-1$
			}
			Log.d(TAG,
					String.format("photo %s loaded from offline cache.", title)); //$NON-NLS-1$

		}
		return result;
	}

	/**
	 * 
	 * @return
	 */
	public static List<IOfflineViewParameter> getExistingOfflineParameters() {
		File offlineFolder = createOfflineFolderIfNeccessary();
		if (offlineFolder == null) {
			return null;
		}
		File repoControlFile = new File(offlineFolder,
				IOfflineViewParameter.OFFLINE_REPO_FILE_NAME);
		if (!repoControlFile.exists()) {
			return null;
		}

		// read offline parameters from repo file.
		List<IOfflineViewParameter> params = new ArrayList<IOfflineViewParameter>();
		try {
			JsonReader reader = new JsonReader(new FileReader(repoControlFile));
			reader.beginArray();
			while (reader.hasNext()) {
				int photoSourceTypeOrdinal = -1;
				String photoCollectionId = null;
				int photoCollectionTypeOrdinal = -1;
				long time = -1;

				reader.beginObject();
				while (reader.hasNext()) {
					String name = reader.nextName();
					if (IOfflineViewParameter.KEY_PHOTO_SOURCE_TYPE
							.equals(name)) {
						photoSourceTypeOrdinal = reader.nextInt();
					} else if (IOfflineViewParameter.KEY_PHOTO_COLLECTION_TYPE
							.equals(name)) {
						photoCollectionTypeOrdinal = reader.nextInt();
					} else if (IOfflineViewParameter.KEY_PHOTO_COLLECTION_ID
							.equals(name)) {
						photoCollectionId = reader.nextString();
					} else {
						time = reader.nextLong();
					}
				}

				AbstractOfflineParameter param = null;
				if (photoSourceTypeOrdinal == MediaSourceType.FLICKR.ordinal()) {
					param = new FlickrOfflineParameter(
							photoCollectionTypeOrdinal, photoCollectionId);
				}
				// TODO other type of parameters.
				param.setLastUpdateTime(time);
				params.add(param);

				reader.endObject();
			}
			reader.endArray();
			reader.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}

		return params;
	}

	static File createOfflineFolderIfNeccessary() {
		File bsRoot = new File(Environment.getExternalStorageDirectory(),
				IConstants.SD_CARD_FOLDER_NAME);
		if (!bsRoot.exists() && !bsRoot.mkdir()) {
			return null;
		}
		File offlineFolder = new File(bsRoot,
				IOfflineViewParameter.OFFLINE_CONTROL_FOLDER_NAME);
		if (!offlineFolder.exists() && !offlineFolder.mkdir()) {
			return null;
		}
		return offlineFolder;
	}

	public static void save(List<IOfflineViewParameter> params)
			throws Exception {
		File folder = createOfflineFolderIfNeccessary();
		if (folder == null) {
			throw new Exception("error to create offline folder."); //$NON-NLS-1$
		}
		File controlFile = new File(folder,
				IOfflineViewParameter.OFFLINE_REPO_FILE_NAME);

		JsonWriter writer = new JsonWriter(new FileWriter(controlFile));
		writer.beginArray();
		for (IOfflineViewParameter param : params) {
			writer.beginObject();
			writer.name(IOfflineViewParameter.KEY_PHOTO_SOURCE_TYPE).value(
					param.getPhotoSourceType().ordinal());
			writer.name(IOfflineViewParameter.KEY_PHOTO_COLLECTION_TYPE).value(
					param.getPhotoCollectionType().ordinal());
			writer.name(IOfflineViewParameter.KEY_PHOTO_COLLECTION_ID).value(
					param.getPhotoCollectionId());
			writer.name(IOfflineViewParameter.KEY_LAST_UPDATE_TS).value(
					System.currentTimeMillis());
			writer.endObject();
		}
		writer.endArray();
		writer.flush();
		writer.close();

	}

	public static boolean isOfflineViewEnabled(IOfflineViewParameter param) {
		List<IOfflineViewParameter> params = getExistingOfflineParameters();
		return params != null && params.contains(param);
	}
}
