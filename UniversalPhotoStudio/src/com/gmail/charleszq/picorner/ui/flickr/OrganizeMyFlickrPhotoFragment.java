/**
 * 
 */
package com.gmail.charleszq.picorner.ui.flickr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.FlickrUserPhotoPool;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.FetchFlickrPhotoContextTask;
import com.gmail.charleszq.picorner.task.flickr.FetchFlickrUserPhotoCollectionFromCacheTask;
import com.gmail.charleszq.picorner.task.flickr.FetchFlickrUserPhotoCollectionTask;
import com.gmail.charleszq.picorner.task.flickr.FlickrOrganizePhotoTask;
import com.gmail.charleszq.picorner.ui.AbstractFragmentWithImageFetcher;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.command.MenuSectionHeaderCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.FlickrUserGroupCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.FlickrUserPhotoSetCommand;
import com.gmail.charleszq.picorner.ui.helper.AbstractCommandSectionListAdapter;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.googlecode.flickrjandroid.galleries.Gallery;
import com.googlecode.flickrjandroid.groups.Group;
import com.googlecode.flickrjandroid.photos.PhotoPlace;
import com.googlecode.flickrjandroid.photosets.Photoset;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class OrganizeMyFlickrPhotoFragment extends
		AbstractFragmentWithImageFetcher implements OnItemClickListener {

	private ListView mListView;
	private ProgressBar mProgressBar;
	private OrganizeAdapter mAdapter;
	private MediaObject mCurrentPhoto;
	private Set<String> mCurrentPhotoContext;
	private Set<String> mUpdatePhotoContext;
	
	private List<ICommand<?>> mCommands;

	/**
	 * default constructor.
	 */
	public OrganizeMyFlickrPhotoFragment() {
	}

	public static OrganizeMyFlickrPhotoFragment newInstance(MediaObject photo) {
		OrganizeMyFlickrPhotoFragment f = new OrganizeMyFlickrPhotoFragment();
		final Bundle bundle = new Bundle();
		bundle.putSerializable(IConstants.DETAIL_PAGE_PHOTO_ARG_KEY, photo);
		f.setArguments(bundle);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.frg_org_my_f_photo, container, false);

		// the list view.
		mListView = (ListView) v.findViewById(R.id.list_org_flickr_photo);
		mAdapter = new OrganizeAdapter(getActivity(), mImageFetcher);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

		// the progress bar
		mProgressBar = (ProgressBar) v.findViewById(R.id.pb_org_flickr_photo);
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getArguments();
		mCurrentPhoto = (MediaObject) bundle
				.getSerializable(IConstants.DETAIL_PAGE_PHOTO_ARG_KEY);
		this.setHasOptionsMenu(true);
		this.setRetainInstance(true);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (this.mCurrentPhotoContext == null) {
			FetchFlickrPhotoContextTask t = new FetchFlickrPhotoContextTask();
			t.addTaskDoneListener(new IGeneralTaskDoneListener<List<PhotoPlace>>() {

				@Override
				public void onTaskDone(List<PhotoPlace> result) {
					onPhotoContextFetched(result);

				}
			});
			t.execute(mCurrentPhoto.getId());
		} else {
			if( mProgressBar != null ) {
				mProgressBar.setVisibility(View.INVISIBLE);
			}
			if( mCommands != null ) {
				mAdapter.clearSections();
				mAdapter.setCurrentPhotoContext(mUpdatePhotoContext);
				mAdapter.addCommands(mCommands);
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_save, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_item_save) {
			performOk();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void performOk() {
		Set<String> add = new HashSet<String>();
		Set<String> remove = new HashSet<String>();

		for (String s : mUpdatePhotoContext) {
			if (!mCurrentPhotoContext.contains(s)) {
				add.add(s);
			}
		}

		for (String ss : mCurrentPhotoContext) {
			if (!mUpdatePhotoContext.contains(ss)) {
				remove.add(ss);
			}
		}
		FlickrOrganizePhotoTask task = new FlickrOrganizePhotoTask(
				getActivity(), add, remove);
		task.execute(mCurrentPhoto.getId());
	}

	protected void onPhotoContextFetched(List<PhotoPlace> result) {
		Log.d(TAG,
				"photo context fetched, size: " + (result == null ? 0 : result.size())); //$NON-NLS-1$

		mCurrentPhotoContext = new HashSet<String>();
		mUpdatePhotoContext = new HashSet<String>();
		if (result != null)
			for (PhotoPlace place : result) {
				mCurrentPhotoContext.add(place.getKind() + place.getId());
				mUpdatePhotoContext.add(place.getKind() + place.getId());
			}
		mAdapter.setCurrentPhotoContext(mUpdatePhotoContext);
		mAdapter.notifyDataSetChanged();

		// start another task to fetch all my photo sets and groups
		FetchFlickrUserPhotoCollectionFromCacheTask task = new FetchFlickrUserPhotoCollectionFromCacheTask();
		task.addTaskDoneListener(new IGeneralTaskDoneListener<List<Object>>() {

			@Override
			public void onTaskDone(List<Object> result) {
				if (result == null) {
					fetchMySetsGroupsFromServer();
				} else {
					onPoolsFetched(result);
				}
			}
		});
		task.execute();
	}

	private void fetchMySetsGroupsFromServer() {
		FetchFlickrUserPhotoCollectionTask task = new FetchFlickrUserPhotoCollectionTask(
				getActivity());
		task.addTaskDoneListener(new IGeneralTaskDoneListener<List<Object>>() {
			@Override
			public void onTaskDone(List<Object> result) {
				onPoolsFetched(result);
			}
		});
		task.execute();
	}

	private void onPoolsFetched(List<Object> result) {
		
		if( getActivity() == null ) {
			return;
		}
		
		mCommands = new ArrayList<ICommand<?>>();
		List<ICommand<?>> psCommands = new ArrayList<ICommand<?>>();
		List<ICommand<?>> groupCommands = new ArrayList<ICommand<?>>();

		for (Object obj : result) {
			// we need to ignore the gallery information in 'result'
			if (Gallery.class.isInstance(obj)) {
				continue;
			}

			if (Photoset.class.isInstance(obj)) {
				// photo set
				FlickrUserPhotoSetCommand cmd = new FlickrUserPhotoSetCommand(
						getActivity(), (Photoset) obj);
				psCommands.add(cmd);
			}

			if (Group.class.isInstance(obj)) {
				// group
				FlickrUserGroupCommand cmd = new FlickrUserGroupCommand(
						getActivity(), (Group) obj);
				groupCommands.add(cmd);
			}
		}
		if (!psCommands.isEmpty()) {
			MenuSectionHeaderCommand cmd = new MenuSectionHeaderCommand(
					getActivity(), getString(R.string.menu_header_flickr_sets));
			psCommands.add(0, cmd);
		}

		if (!groupCommands.isEmpty()) {
			MenuSectionHeaderCommand cmd = new MenuSectionHeaderCommand(
					getActivity(),
					getString(R.string.menu_header_flickr_groups));
			groupCommands.add(0, cmd);
		}
		mCommands.addAll(psCommands);
		mCommands.addAll(groupCommands);

		mAdapter.addCommands(mCommands);
		mAdapter.notifyDataSetChanged();
		if (mProgressBar != null) {
			mProgressBar.setVisibility(View.INVISIBLE);
		}
	}

	private static class OrganizeAdapter extends AbstractCommandSectionListAdapter {

		private Set<String> mCurrentPhotoContext;

		public OrganizeAdapter(Context ctx, ImageLoader fetcher) {
			super(ctx, fetcher);
		}

		void setCurrentPhotoContext(Set<String> set) {
			mCurrentPhotoContext = set;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.gmail.charleszq.picorner.ui.helper.CommandSectionListAdapter#
		 * isEnabled(int)
		 */
		@Override
		public boolean isEnabled(int position) {
			return getItemViewType(position) == ITEM_COMMAND;
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
					task.execute(mImageFetcher, imageView);
				} else {
				}
			}
			return view;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ICommand<?> cmd = (ICommand<?>) mAdapter.getItem(position);
		if (cmd == null) {
			return;
		}

		String poolid = cmd.getAdapter(FlickrUserPhotoPool.class).toString();
		if (poolid == null) {
			return;
		}

		if (mUpdatePhotoContext.contains(poolid)) {
			mUpdatePhotoContext.remove(poolid);
		} else {
			mUpdatePhotoContext.add(poolid);
		}
		mAdapter.notifyDataSetChanged();

	}

}
