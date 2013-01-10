/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

import java.util.ArrayList;
import java.util.List;

import android.widget.Filter;

import com.gmail.charleszq.picorner.ui.command.CommandType;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.command.MenuSectionHeaderCommand;

/**
 * Represents the text filter for main menu items.
 * 
 * @author charleszq
 * 
 */
public class MainMenuTextFilter extends Filter {

	private CommandSectionListAdapter mAdapter;

	/**
	 * Constructor.
	 */
	public MainMenuTextFilter(CommandSectionListAdapter adapter) {
		this.mAdapter = adapter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Filter#performFiltering(java.lang.CharSequence)
	 */
	@Override
	protected FilterResults performFiltering(CharSequence constraint) {
		List<ICommand<?>> filteredCommands = new ArrayList<ICommand<?>>();
		for (ICommand<?> cmd : mAdapter.mAllCommands) {
			if (cmd.getCommandType().equals(CommandType.MENU_HEADER_CMD)) {
				filteredCommands.add(cmd);
				((MenuSectionHeaderCommand)cmd).setFiltering(true);
			} else {
				if (cmd.getLabel().toLowerCase()
						.contains(constraint.toString().toLowerCase())) {
					filteredCommands.add(cmd);
				}
			}
		}
		FilterResults results = new FilterResults();
		results.values = filteredCommands;
		results.count = filteredCommands.size();
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Filter#publishResults(java.lang.CharSequence,
	 * android.widget.Filter.FilterResults)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void publishResults(CharSequence constraint, FilterResults results) {
		mAdapter.publishFilterResult(constraint.toString(), results.count,
				(List<ICommand<?>>) results.values);

	}

}
