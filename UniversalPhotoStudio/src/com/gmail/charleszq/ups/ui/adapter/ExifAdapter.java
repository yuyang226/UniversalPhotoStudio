/**
 * 
 */
package com.gmail.charleszq.ups.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.model.ExifData;

/**
 * Represents the adapter to show exif information for a photo.
 * 
 * @author charles(charleszq@gmail.com)
 *
 */
public class ExifAdapter extends BaseAdapter {

	private Context mContext;
	private List<ExifData> mExifs;

	public ExifAdapter(Context ctx, List<ExifData> data) {
		mContext = ctx;
		mExifs = data;
	}

	@Override
	public int getCount() {
		return mExifs.size();
	}

	@Override
	public Object getItem(int position) {
		return mExifs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = LayoutInflater.from(mContext).inflate(
				R.layout.flikckr_exif_list_item, null);
		ExifData exif = (ExifData) getItem(position);
		TextView label = (TextView) v.findViewById(R.id.exif_label);
		TextView value = (TextView) v.findViewById(R.id.exif_value);
		label.setText(exif.label);
		value.setText(exif.value == null ? "" : exif.value); //$NON-NLS-1$
		return v;
	}

}
