/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.offline.IOfflineViewParameter;
import com.gmail.charleszq.picorner.offline.OfflineControlFileUtil;
import com.gmail.charleszq.picorner.offline.OfflineHandleService;
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
	 * the marker to say whether we show the '^'/'v' sign on the header.
	 */
	private boolean mShowHeaderIndicator = true;

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

	public CommandSectionListAdapter(Context ctx, ImageLoader fetcher,
			boolean showHeaderMarker) {
		this(ctx, fetcher);
		mShowHeaderIndicator = showHeaderMarker;
	}

	public void addCommands(Collection<ICommand<?>> commands) {
		boolean add = true;
		for (ICommand<?> cmd : commands) {
			if (cmd instanceof MenuSectionHeaderCommand) {
				add = true;
				mCommands.add(cmd);
				MenuSectionHeaderCommand headerCmd = (MenuSectionHeaderCommand) cmd;
				if (headerCmd.isFiltering()) {
					add = false;
				}
			} else {
				if (add) {
					mCommands.add(cmd);
				}
			}
		}
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
			MenuSectionHeaderCommand hc = (MenuSectionHeaderCommand) command;
			if (mShowHeaderIndicator) {
				if (hc.isFiltering()) {
					((TextView) view).setCompoundDrawablesWithIntrinsicBounds(
							0, 0, R.drawable.ic_find_previous_holo_dark, 0);
				} else {
					((TextView) view).setCompoundDrawablesWithIntrinsicBounds(
							0, 0, R.drawable.ic_find_next_holo_dark, 0);
				}
			}
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

		prepareBackView(view, command, text);

		return view;
	}

	private void prepareBackView(final View view, final ICommand<?> command,
			final TextView text) {
		// offline view back view
		final IOfflineViewParameter offline = (IOfflineViewParameter) command
				.getAdapter(IOfflineViewParameter.class);
		if (offline != null) {
			final View backView = LayoutInflater.from(mContext).inflate(
					R.layout.main_menu_item_backview, null);
			ViewGroup container = (ViewGroup) view
					.findViewById(R.id.menu_item_container);
			backView.setVisibility(View.INVISIBLE);
			container.addView(backView);

			// find the back button and hook listener on it
			ImageButton button = (ImageButton) view
					.findViewById(R.id.btn_menu_item_back_view_back);
			final View frontView = view
					.findViewById(R.id.menu_item_container_2);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					backView.setVisibility(View.INVISIBLE);
					frontView.setVisibility(View.VISIBLE);
					ObjectAnimator
							.ofFloat(frontView, "alpha", 0f, 1f).setDuration(1500).start(); //$NON-NLS-1$
				}
			});

			// the checkbox
			CheckBox offlineCheckBox = (CheckBox) view
					.findViewById(R.id.cb_offline);
			offlineCheckBox.setChecked(OfflineControlFileUtil
					.isOfflineViewEnabled(offline));
			offlineCheckBox
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							Intent serviceIntent = new Intent(mContext,
									OfflineHandleService.class);
							serviceIntent
									.putExtra(
											IOfflineViewParameter.OFFLINE_PARAM_INTENT_KEY,
											offline);
							serviceIntent
									.putExtra(
											IOfflineViewParameter.OFFLINE_PARAM_INTENT_ADD_REMOVE_KEY,
											Boolean.toString(isChecked));
							mContext.startService(serviceIntent);

						}
					});

			text.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.ic_offline_indicator, 0);
		}
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
