/**
 * 
 */
package com.gmail.charleszq.picorner.ui.flickr;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.command.flickr.FlickrLoginCommand;
import com.gmail.charleszq.picorner.ui.helper.CommandSectionListAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Represents the dialog to add my own photo to a photo set or photo group.
 * 
 * @author charleszq
 *
 */
public class AddPhotoToGroupDialog extends DialogFragment {
	
	public static final String PHOTO_ARG_KEY = "my.photo"; //$NON-NLS-1$
	public static final String DLG_TAG = "add.photo.to.ps"; //$NON-NLS-1$
	
	private ListView mListView;
	private PhotoSetGroupAdapter mAdapter;
	private ImageLoader mImageLoader;

	/*
	 * Default constructor.
	 */
	public AddPhotoToGroupDialog() {
		
	}
	
	public static AddPhotoToGroupDialog newInstance(MediaObject photo) {
		AddPhotoToGroupDialog dialog = new AddPhotoToGroupDialog();
		Bundle bundle = new Bundle();
		bundle.putSerializable(PHOTO_ARG_KEY, photo);
		dialog.setArguments(bundle);
		return dialog;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageLoader = ImageLoader.getInstance();
		this.setRetainInstance(true);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle(R.string.menu_item_add_my_flickr_photo_to_group);
		View v = inflater.inflate(R.layout.add_my_flickr_photo_to_group, null);
		mListView = (ListView) v.findViewById(R.id.list_photo_add_to_group);
		mAdapter = new PhotoSetGroupAdapter(getActivity(), mImageLoader);
		List<ICommand<?>> commands = new ArrayList<ICommand<?>>();
		ICommand<Object> cmd = new FlickrLoginCommand(getActivity());
		commands.add(cmd);
		commands.add(cmd);
		commands.add(cmd);
		commands.add(cmd);
		commands.add(cmd);
		commands.add(cmd);
		commands.add(cmd);
		commands.add(cmd);
		commands.add(cmd);
		commands.add(cmd);
		commands.add(cmd);
		
		commands.add(cmd);
		commands.add(cmd);
		mAdapter.addCommands(commands);
		mListView.setAdapter(mAdapter);
		return v;
	}
	
	/**
	 * The adapter to show my photo sets and groups.
	 * @author charleszq
	 *
	 */
	private static class PhotoSetGroupAdapter extends CommandSectionListAdapter {

		public PhotoSetGroupAdapter(Context ctx, ImageLoader fetcher) {
			super(ctx, fetcher);
		}

		
	}

}
