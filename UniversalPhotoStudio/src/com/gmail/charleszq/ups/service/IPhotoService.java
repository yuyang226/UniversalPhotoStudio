/**
 * 
 */
package com.gmail.charleszq.ups.service;

import com.gmail.charleszq.ups.model.MediaObjectCollection;

/**
 * Represents the service to get the photo list.
 * @author charleszq
 * 
 */
public interface IPhotoService {

	/**
	 * 
	 * @param pageSize
	 *            the page size
	 * @param pageNo
	 *            the page number, it means which page to return, instead of how many pages to returns.
	 * @return the photo list.
	 */
	MediaObjectCollection getPhotos(int pageSize, int pageNo) throws Exception;

}
