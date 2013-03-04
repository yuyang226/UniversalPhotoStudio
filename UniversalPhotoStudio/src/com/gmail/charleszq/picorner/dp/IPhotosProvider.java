/**
 * 
 */
package com.gmail.charleszq.picorner.dp;

import java.io.Serializable;

import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;

/**
 * 
 * @author Charles(charleszq@gmail.com)
 *
 */
public interface IPhotosProvider extends Serializable {
	
	/**
	 * Returns the <code>MediaObject</code> at the given index.
	 * @param index
	 * @return
	 */
	MediaObject getMediaObject(int index);
	
	/**
	 * Returns the current size of this data provider.
	 * @return
	 */
	int getCurrentSize();
	
	/**
	 * When the service/task/command is done, this method can be called to populate the data.
	 * @param list
	 * @param source.
	 * @param comparator
	 */
	void loadData(MediaObjectCollection list, Object source, Object comparator);
}
