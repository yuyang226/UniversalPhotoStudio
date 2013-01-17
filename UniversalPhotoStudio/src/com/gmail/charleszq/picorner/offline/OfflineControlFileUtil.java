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

import android.os.Environment;
import android.util.JsonReader;
import android.util.JsonWriter;

import com.gmail.charleszq.picorner.model.MediaSourceType;
import com.gmail.charleszq.picorner.utils.IConstants;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public final class OfflineControlFileUtil {

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
			return null;
		}

		return params;
	}

	private static File createOfflineFolderIfNeccessary() {
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
