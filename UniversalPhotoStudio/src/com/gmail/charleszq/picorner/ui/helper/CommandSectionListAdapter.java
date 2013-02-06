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

	/**
	 * Constructor.
	 */
	public CommandSectionListAdapter(Context ctx) {
		mContext = ctx;
		mCommands = new ArrayList<ICommand<?>>();
		mAllCommands = new ArrayList<ICommand<?>>();
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
		return getItemViewType(position) == CommandSectionListAdapter.ITEM_COMMAND;
	}

	public void clearSections() {
		mCommands.clear();
		mAllCommands.clear();
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
			if (view == null)
				view = LayoutInflater.from(mContext).inflate(
						R.layout.section_header, null);
			((TextView) view).setText(command.getLabel());
			return view;
		}

		// command items
		if (view == null)
			view = LayoutInflater.from(mContext).inflate(
					R.layout.main_menu_item, null);

		ViewHolder holder = (ViewHolder) view.getTag();
		TextView text = null;
		ImageView image = null;
		ImageView settingButton = null;
		if (holder != null) {
			text = holder.text;
			image = holder.imageView;
			settingButton = holder.settingButton;
		} else {
			text = (TextView) view.findViewById(R.id.nav_item_title);
			image = (ImageView) view.findViewById(R.id.nav_item_image);
			settingButton = (ImageView) view
					.findViewById(R.id.btn_offline_settings);
			holder = new ViewHolder();
			holder.text = text;
			holder.imageView = image;
			holder.settingButton = settingButton;
			view.setTag(holder);
		}

		text.setText(command.getLabel());
		settingButton.setVisibility(View.INVISIBLE);
		int iconId = command.getIconResourceId();
		if (iconId != -1) {
			image.setImageDrawable(mContext.getResources().getDrawable(iconId));
		} else {
			image.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.empty_photo));
			AbstractFetchIconUrlTask task = (AbstractFetchIconUrlTask) command
					.getAdapter(AbstractFetchIconUrlTask.class);
			if (task != null) {
				task.execute(image);
			} else {
			}
		}
		return view;
	}

	class ViewHolder {
		TextView text;
		ImageView imageView;
		ImageView settingButton;
	}

}
