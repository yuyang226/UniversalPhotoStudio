/**
 * 
 */
package com.gmail.charleszq.picorner.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.AndroidHttpClient;

import com.nostra13.universalimageloader.core.download.HttpClientImageDownloader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.utils.FileUtils;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public final class ImageUtils {

	private static final int BUFFER_SIZE = 8 * 1024; // 8 Kb

	public static boolean saveImageToFile(File destFile, Bitmap bitmap) {

		if (bitmap == null) {
			return false;
		}

		FileOutputStream fos = null;
		try {
			if (destFile.exists()) {
				destFile.delete();
			}
			fos = new FileOutputStream(destFile);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		} catch (FileNotFoundException fnfe) {
			return false;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ioe) {
				}
			}
		}
		return true;
	}

	public static boolean saveImageToFile(Context ctx, String filename,
			Bitmap bitmap) {
		if (bitmap == null) {
			return false;
		}
		FileOutputStream fos = null;
		try {
			fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
		} catch (FileNotFoundException e) {
			// will create if not exists
			return false;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
		return true;
	}

	public static boolean saveImageToFile(Context ctx, String filename,
			String url) {

		AndroidHttpClient client = AndroidHttpClient.newInstance("Android"); //$NON-NLS-1$
		ImageDownloader downloader = new HttpClientImageDownloader(client);

		FileOutputStream fos = null;
		InputStream is = null;
		try {
			fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
			is = downloader.getStream(new URI(url));
			OutputStream os = new BufferedOutputStream(fos, BUFFER_SIZE);
			FileUtils.copyStream(is, os);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}

			if (client != null) {
				client.close();
			}

		}
	}
}
