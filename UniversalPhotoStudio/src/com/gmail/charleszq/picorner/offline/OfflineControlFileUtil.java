/**
 * 
 */
package com.gmail.charleszq.picorner.offline;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.gmail.charleszq.picorner.BuildConfig;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.utils.ImageUtils;

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
	public static Bitmap loadImageFromCache(Context ctx, MediaObject photo) {
		Bitmap result = null;
		String photoFileName = OfflineControlFileUtil
				.getOfflinePhotoFileName(photo);
		if (ctx != null && isFileExist(ctx, photoFileName)) {
			try {
				result = BitmapFactory.decodeStream(ctx
						.openFileInput(photoFileName));
			} catch (FileNotFoundException e) {
				if (BuildConfig.DEBUG)
					Log.w(TAG,
							String.format("file %s not found.", photoFileName)); //$NON-NLS-1$
			}
		}
		return result;
	}

	public static void saveBitmapForOfflineView(Context context, Bitmap bmp,
			MediaObject photo) {
		String photoFilename = OfflineControlFileUtil
				.getOfflinePhotoFileName(photo);
		if (isFileExist(context, photoFilename)) {
			if (BuildConfig.DEBUG)
				Log.d(TAG, String.format("%s exists.", photoFilename)); //$NON-NLS-1$
			return;
		}
		ImageUtils.saveImageToFile(context, photoFilename, bmp);
	}

	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<IOfflineViewParameter> getExistingOfflineParameters(
			Context ctx) {
		if (isFileExist(ctx, IOfflineViewParameter.OFFLINE_REPO_FILE_NAME)) {
			FileInputStream fis = null;
			try {
				fis = ctx
						.openFileInput(IOfflineViewParameter.OFFLINE_REPO_FILE_NAME);
				ObjectInputStream ois = new ObjectInputStream(fis);
				return (List<IOfflineViewParameter>) ois.readObject();
			} catch (Exception e) {
				return null;
			} finally {
				if (fis != null)
					try {
						fis.close();
					} catch (IOException e) {
					}
			}
		}

		return null;
	}

	public static void saveRepositoryControlFile(Context ctx,
			List<IOfflineViewParameter> params) throws Exception {
		if (params == null || ctx == null) {
			return;
		}

		FileOutputStream fos = null;
		fos = ctx.openFileOutput(IOfflineViewParameter.OFFLINE_REPO_FILE_NAME,
				Context.MODE_PRIVATE);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(params);
		oos.flush();
		oos.close();
	}

	public static boolean isOfflineViewEnabled(Context ctx,
			IOfflineViewParameter param) {
		if (ctx == null) {
			return false;
		}
		List<IOfflineViewParameter> params = getExistingOfflineParameters(ctx);
		return params != null && params.contains(param);
	}

	public static boolean isOfflineControlFileReady(Context ctx,
			IOfflineViewParameter param) {
		String controlFileName = param.getControlFileName();
		boolean ready = isFileExist(ctx, controlFileName);
		if (BuildConfig.DEBUG) {
			Log.d(TAG, String.format("Offline control file %s is ready? %s", //$NON-NLS-1$
					param.getControlFileName(), Boolean.toString(ready)));
		}
		return ready;
	}

	public static boolean isFileExist(Context ctx, String filename) {
		File rootDir = ctx.getFilesDir();
		if (rootDir.exists()) {
			File file = new File(rootDir, filename);
			return file.exists();
		}
		return false;
	}

	public static String getOfflinePhotoFileName(MediaObject photo) {
		StringBuilder sb = new StringBuilder();
		switch (photo.getMediaSource()) {
		case FLICKR:
			sb.append(IOfflineViewParameter.OFFLINE_FLICKR_PHOTO_FILE_PREFIX);
			break;
		case INSTAGRAM:
			sb.append(IOfflineViewParameter.OFFLINE_INSTAGRAM_PHOTO_FILE_PREFIX);
			break;
		case PX500:
			sb.append(IOfflineViewParameter.OFFLINE_500PX_PHOTO_FILE_PREFIX);
			break;
		}
		sb.append("_"); //$NON-NLS-1$
		sb.append(photo.getId());
		sb.append(".png"); //$NON-NLS-1$
		return sb.toString();
	}
}
