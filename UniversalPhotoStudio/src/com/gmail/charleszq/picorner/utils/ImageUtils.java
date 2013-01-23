/**
 * 
 */
package com.gmail.charleszq.picorner.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.nostra13.universalimageloader.core.assist.FlushedInputStream;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public final class ImageUtils {

	
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

	/**
	 * This method must be called in a thread other than UI.
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap downloadImage(String url) {
		// final int IO_BUFFER_SIZE = 4 * 1024;

		// AndroidHttpClient is not allowed to be used from the main thread
		final HttpClient client = AndroidHttpClient.newInstance("Android"); //$NON-NLS-1$
		final HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode //$NON-NLS-1$//$NON-NLS-2$
						+ " while retrieving bitmap from " + url); //$NON-NLS-1$
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					// return BitmapFactory.decodeStream(inputStream);
					// Bug on slow connections, fixed in future release.
					return BitmapFactory.decodeStream(new FlushedInputStream(
							inputStream));
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (IOException e) {
			getRequest.abort();
			Log.w("I/O error while retrieving bitmap from " + url, e); //$NON-NLS-1$
		} catch (IllegalStateException e) {
			getRequest.abort();
			Log.w("Incorrect URL:" + url, e); //$NON-NLS-1$
		} catch (Exception e) {
			getRequest.abort();
			Log.w("Error while retrieving bitmap from " + url, e); //$NON-NLS-1$
		} finally {
			if ((client instanceof AndroidHttpClient)) {
				((AndroidHttpClient) client).close();
			}
		}
		return null;
	}
}
