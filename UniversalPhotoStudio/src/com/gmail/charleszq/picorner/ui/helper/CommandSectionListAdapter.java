/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

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

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.command.MenuSectionHeaderCommand;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class CommandSectionListAdapter extends BaseAdapter {

	public static final int ITEM_HEADER = 0;
	public static final int ITEM_COMMAND = 1;

	/**
	 * The current filtered commands
	 */
	List<ICommand<?>> mCommands;

	/**
	 * All commands.
	 */
	List<ICommand<?>> mAllCommands;

	protected Context mContext;
	protected ImageLoader mImageFetcher;

	/**
	 * Constructor.
	 */
	public CommandSectionListAdapter(Context ctx, ImageLoader fetcher) {
		mContext = ctx;
		mImageFetcher = fetcher;
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				mContext.getApplicationContext())
				.discCacheSize(IConstants.IMAGE_CACHE_SIZE).threadPoolSize(5)
				.memoryCache(new WeakMemoryCache()).build();
		mImageFetcher.init(config);
		mCommands = new ArrayList<ICommand<?>>();
		mAllCommands = new ArrayList<ICommand<?>>();
	}

	public void addCommands(Collection<ICommand<?>> commands) {
		mCommands.addAll(commands);
		mAllCommands.addAll(commands);
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
		if (obj instanceof MenuSectionHeaderCommand) {
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
		return true;
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
			view = LayoutInflater.from(mContext).inflate(
					R.layout.section_header, null);
			((TextView) view).setText(command.getLabel());
			return view;
		}

		// command items
		view = LayoutInflater.from(mContext).inflate(R.layout.main_menu_item,
				null);

		TextView text = (TextView) view.findViewById(R.id.nav_item_title);
		ImageView image = (ImageView) view.findViewById(R.id.nav_item_image);
		text.setText(command.getLabel());
		int iconId = command.getIconResourceId();
		if (iconId != -1) {
			image.setImageDrawable(mContext.getResources().getDrawable(iconId));
		} else {
			image.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.empty_photo));
			AbstractFetchIconUrlTask task = (AbstractFetchIconUrlTask) command
					.getAdapter(AbstractFetchIconUrlTask.class);
			if (task != null) {
				task.execute(mImageFetcher, image);
			} else {
			}
		}

		return view;
	}

	public void clearSections() {
		mCommands.clear();
		mAllCommands.clear();
	}

	/**
	 * 
	 * @param filterString
	 * @param count
	 * @param commands
	 */
	void publishFilterResult(CharSequence filterString, int count,
			List<ICommand<?>> commands) {
		mCommands = commands;
		notifyDataSetChanged();
	}

}
