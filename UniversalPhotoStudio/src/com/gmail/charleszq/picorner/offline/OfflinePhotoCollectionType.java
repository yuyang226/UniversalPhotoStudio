/**
 * 
 */
package com.gmail.charleszq.picorner.offline;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public enum OfflinePhotoCollectionType {

	PHOTO_SET("PS"); //$NON-NLS-1$

	/**
	 * The label of this collection type
	 */
	private String mLabel;

	private OfflinePhotoCollectionType(String label) {
		this.mLabel = label;
	}

	@Override
	public String toString() {
		return mLabel;
	}

}
