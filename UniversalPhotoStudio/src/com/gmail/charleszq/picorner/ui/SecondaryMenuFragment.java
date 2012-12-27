/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.ui.command.AboutCommand;
import com.gmail.charleszq.picorner.ui.command.HelpCommand;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.command.MenuSectionHeaderCommand;
import com.gmail.charleszq.picorner.ui.helper.CommandSectionListAdapter;

/**
 * Represents the fragment to show the secondary menus.
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class SecondaryMenuFragment extends AbstractFragmentWithImageFetcher
		implements OnItemClickListener {

	private ListView mListView;
	private CommandSectionListAdapter mSectionAdapter;

	/**
	 * 
	 */
	public SecondaryMenuFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.main_menu, null);
		mListView = (ListView) v.findViewById(R.id.listView1);
		mSectionAdapter = new CommandSectionListAdapter(getActivity(),
				mImageFetcher) {

			@Override
			public boolean isEnabled(int position) {
				return getItemViewType(position) == CommandSectionListAdapter.ITEM_COMMAND;
			}

		};
		mListView.setAdapter(mSectionAdapter);
		mListView.setOnItemClickListener(this);
		prepareMenuItems();
		return v;
	}

	private void prepareMenuItems() {
		mSectionAdapter.clearSections();
		List<ICommand<?>> commands = new ArrayList<ICommand<?>>();
		ICommand<?> command = new MenuSectionHeaderCommand(getActivity(),
				getString(R.string.secondary_menus));
		commands.add(command);

		command = new AboutCommand(getActivity());
		commands.add(command);
		
		command = new HelpCommand(getActivity());
		commands.add(command);
		
		mSectionAdapter.addCommands(commands);
		mSectionAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// execute the command
		ICommand<?> command = (ICommand<?>) parent.getAdapter().getItem(
				position);
		command.execute();

		// close the menu.
		MainSlideMenuActivity act = (MainSlideMenuActivity) getActivity();
		act.closeMenu();
	}

}
