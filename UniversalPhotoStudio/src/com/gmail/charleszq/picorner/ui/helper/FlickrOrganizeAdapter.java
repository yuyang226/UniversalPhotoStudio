/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

import java.util.Set;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.FlickrUserPhotoPool;
import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.picorner.ui.command.ICommand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Represents the adapter for organize my flickr photo set, and groups.
 * @author charles(charleszq@gmail.com)
 *
 */
public class FlickrOrganizeAdapter extends CommandSectionListAdapter {

	/**
	 * @param ctx
	 */
	public FlickrOrganizeAdapter(Context ctx) {
		super(ctx);
	}
	
	private Set<String> mCurrentPhotoContext;

	/**
	 * The set contains the current photo context, which photo set, group the photo is in.
	 * @param set
	 */
	public void setCurrentPhotoContext(Set<String> set) {
		mCurrentPhotoContext = set;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.picorner.ui.helper.CommandSectionListAdapter#
	 * getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ICommand<?> command = (ICommand<?>) getItem(position);
		if (getItemViewType(position) == ITEM_HEADER) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.section_header, null);
			((TextView) view).setText(command.getLabel());
			return view;
		}

		view = LayoutInflater.from(mContext).inflate(
				R.layout.org_my_flickr_photo_list_item, null);

		// pool label
		CheckedTextView textView = (CheckedTextView) view
				.findViewById(android.R.id.text1);
		textView.setText(command.getLabel());
		String id = command.getAdapter(FlickrUserPhotoPool.class)
				.toString();
		if (mCurrentPhotoContext.contains(id)) {
			textView.setChecked(true);
		} else {
			textView.setChecked(false);
		}

		// fetch the pool icon
		ImageView imageView = (ImageView) view
				.findViewById(R.id.photo_pool_icon);
		int iconId = command.getIconResourceId();
		if (iconId != -1) {
			imageView.setImageDrawable(mContext.getResources().getDrawable(
					iconId));
		} else {
			imageView.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.empty_photo));
			AbstractFetchIconUrlTask task = (AbstractFetchIconUrlTask) command
					.getAdapter(AbstractFetchIconUrlTask.class);
			if (task != null) {
				task.execute(imageView);
			} else {
			}
		}
		return view;
	}

}
