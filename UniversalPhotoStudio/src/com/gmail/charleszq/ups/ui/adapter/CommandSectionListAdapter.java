/**
 * 
 */
package com.gmail.charleszq.ups.ui.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.ups.ui.command.DummyCommand;
import com.gmail.charleszq.ups.ui.command.ICommand;
import com.gmail.charleszq.ups.utils.ImageFetcher;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class CommandSectionListAdapter extends BaseAdapter {

	public static final int ITEM_HEADER = 0;
	public static final int ITEM_COMMAND = 1;

	private List<ICommand<?>> mCommands;
	private Context mContext;
	private ImageFetcher mImageFetcher;

	/**
	 * Constructor.
	 */
	public CommandSectionListAdapter(Context ctx, ImageFetcher fetcher) {
		mContext = ctx;
		mImageFetcher = fetcher;
		mCommands = new ArrayList<ICommand<?>>();
	}

	public void addCommands(Collection<ICommand<?>> commands) {
		mCommands.addAll(commands);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return mCommands.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return mCommands.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public int getItemViewType(int position) {
		Object obj = getItem(position);
		if (obj instanceof DummyCommand) {
			return ITEM_HEADER;
		} else {
			return ITEM_COMMAND;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public boolean isEnabled(int position) {
		Object obj = getItem(position);
		if (obj instanceof DummyCommand) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;
		ICommand<?> command = (ICommand<?>) getItem(position);
		if (getItemViewType(position) == ITEM_HEADER) {
			if (view == null) {
				view = LayoutInflater.from(mContext).inflate(
						R.layout.section_header, null);
			}
			((TextView) view).setText(command.getLabel());
			return view;
		}

		// command items
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.main_menu_item, null);
		}

		ViewHolder holder = (ViewHolder) view.getTag();
		TextView text;
		ImageView image;
		if (holder == null) {
			text = (TextView) view.findViewById(R.id.nav_item_title);
			image = (ImageView) view.findViewById(R.id.nav_item_image);
			holder = new ViewHolder();
			holder.image = image;
			holder.title = text;
			view.setTag(holder);
		} else {
			text = holder.title;
			image = holder.image;
		}
		text.setText(command.getLabel());
		int iconId = command.getIconResourceId();
		if (iconId != -1) {
			image.setImageDrawable(mContext.getResources().getDrawable(iconId));
		} else {
			image.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.icon));
			AbstractFetchIconUrlTask task = (AbstractFetchIconUrlTask) command
					.getAdapter(AbstractFetchIconUrlTask.class);
			if (task != null) {
				task.execute(mImageFetcher, image);
			} else {
			}
		}

		return view;
	}

	class ViewHolder {
		ImageView image;
		TextView title;
	}

	public void clearSections() {
		mCommands.clear();
	}

}
