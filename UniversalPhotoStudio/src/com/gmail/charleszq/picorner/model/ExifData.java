/**
 * 
 */
package com.gmail.charleszq.picorner.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public final class ExifData implements Serializable {

	public static final String LABEL_MAKE = "Make"; //$NON-NLS-1$
	public static final String LABEL_MODEL = "Model"; //$NON-NLS-1$
	public static final String LABEL_LEN = "Lens"; //$NON-NLS-1$
	public static final String LABEL_FOCAL_LEN = "Focal Length"; //$NON-NLS-1$
	public static final String LABEL_SOFTWARE = "Software"; //$NON-NLS-1$
	public static final String LABEL_EXPOSURE = "Exposure"; //$NON-NLS-1$
	public static final String LABEL_EXP_BIAS = "Exposure Bias"; //$NON-NLS-1$
	public static final String LABEL_EXP_PRG = "Exposure Program"; //$NON-NLS-1$
	public static final String LABEL_METERING_METHOD = "Metering Mode"; //$NON-NLS-1$
	public static final String LABEL_ISO = "ISO Speed"; //$NON-NLS-1$
	public static final String LABEL_WB = "White Balance"; //$NON-NLS-1$
	public static final String LABEL_APERTURE = "Aperture"; //$NON-NLS-1$
	public static final String LABEL_CRT_TIME = "Date and Time (Original)"; //$NON-NLS-1$

	/**
	 * 
	 */
	private static final long serialVersionUID = -5246251338975444403L;

	public String label;
	public String value;
	
	public ExifData() {
		
	}
	
	public ExifData(String label) {
		this.label = label;
	}

	/**
	 * 
	 */
	public static Map<String, ExifData> getPredefinedExifList() {
		Map<String, ExifData> map = new LinkedHashMap<String, ExifData>();
		map.put(LABEL_MAKE, new ExifData(LABEL_MAKE));
		map.put(LABEL_MODEL, new ExifData(LABEL_MODEL));
		map.put(LABEL_LEN, new ExifData(LABEL_LEN));
		map.put(LABEL_FOCAL_LEN, new ExifData(LABEL_FOCAL_LEN));
		map.put(LABEL_SOFTWARE, new ExifData(LABEL_SOFTWARE));
		map.put(LABEL_EXPOSURE, new ExifData(LABEL_EXPOSURE));
		map.put(LABEL_EXP_BIAS, new ExifData(LABEL_EXP_BIAS));
		map.put(LABEL_EXP_PRG, new ExifData(LABEL_EXP_PRG));
		map.put(LABEL_METERING_METHOD, new ExifData(LABEL_METERING_METHOD));
		map.put(LABEL_ISO, new ExifData(LABEL_ISO));
		map.put(LABEL_WB, new ExifData(LABEL_WB));
		map.put(LABEL_APERTURE, new ExifData(LABEL_APERTURE));
		map.put(LABEL_CRT_TIME, new ExifData(LABEL_CRT_TIME));
		return map;
	}

}
