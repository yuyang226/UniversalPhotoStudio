/**
 * 
 */
package com.gmail.charleszq.ups.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;

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
}
