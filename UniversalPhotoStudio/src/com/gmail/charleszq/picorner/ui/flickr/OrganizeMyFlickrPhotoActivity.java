/**
 * 
 */
package com.gmail.charleszq.picorner.ui.flickr;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.FlickrUserPhotoPool;
import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.FetchFlickrPhotoContextTask;
import com.gmail.charleszq.picorner.task.flickr.FetchFlickrUserPhotoCollectionFromCacheTask;
import com.gmail.charleszq.picorner.task.flickr.FetchFlickrUserPhotoCollectionTask;
import com.gmail.charleszq.picorner.task.flickr.FlickrOrganizePhotoTask;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.command.MenuSectionHeaderCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.FlickrUserGroupCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.FlickrUserPhotoSetCommand;
import com.gmail.charleszq.picorner.ui.helper.CommandSectionListAdapter;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.googlecode.flickrjandroid.galleries.Gallery;
import com.googlecode.flickrjandroid.groups.Group;
import com.googlecode.flickrjandroid.photos.PhotoPlace;
import com.googlecode.flickrjandroid.photosets.Photoset;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author charleszq
 * 
 */
public class OrganizeMyFlickrPhotoActivity extends FragmentActivity implements
		OnItemClickListener {

	public static final String PHOTO_ID_KEY = "photo.id.key"; //$NON-NLS-1$

	private static final String TAG = OrganizeMyFlickrPhotoActivity.class
			.getSimpleName();

	private Button mCancelButton, mOkButton;
	private OnClickListener mButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == mCancelButton) {
				finish();
			} else if (v == mOkButton) {
				performOk();
			}
		}
	};
	private ImageView mImageView;
	private ListView mListView;
	private ProgressBar mProgressBar;
	private OrganizeAdapter mAdapter;

	/**
	 * The photo id. need to save this when configuration changes.
	 */
	private String mPhotoId;

	/**
	 * The current photo context, before doing the save.
	 */
	private Set<String> mCurrentPhotoContext = null;

	/**
	 * This instance will be given to adapter to track the updates.
	 */
	private Set<String> mUpdatePhotoContext = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.org_my_flickr_photo);

		// get data from intent
		Intent i = getIntent();
		mPhotoId = i.getStringExtra(PHOTO_ID_KEY);
		if (mPhotoId == null) {
			Log.w(TAG, "no photo id passed in."); //$NON-NLS-1$
		}

		// buttons
		mCancelButton = (Button) findViewById(R.id.button_org_my_f_cancel);
		mCancelButton.setOnClickListener(mButtonClickListener);
		mOkButton = (Button) findViewById(R.id.button_org_my_f_ok);
		mOkButton.setOnClickListener(mButtonClickListener);

		// the preview image
		mImageView = (ImageView) findViewById(R.id.image_org_my_f_photo);
		loadImage();

		// the list view.
		mListView = (ListView) findViewById(R.id.list_org_flickr_photo);
		mAdapter = new OrganizeAdapter(this, ImageLoader.getInstance());
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

		// the progress bar
		mProgressBar = (ProgressBar) findViewById(R.id.pb_org_flickr_photo);
		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		FetchFlickrPhotoContextTask t = new FetchFlickrPhotoContextTask();
		t.addTaskDoneListener(new IGeneralTaskDoneListener<List<PhotoPlace>>() {

			@Override
			public void onTaskDone(List<PhotoPlace> result) {
				onPhotoContextFetched(result);

			}
		});
		t.execute(mPhotoId);
	}

	private void onPhotoContextFetched(List<PhotoPlace> result) {
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

	/**
	 * my pool information is fetched, now need to populate the list.
	 * 
	 * @param result
	 */
	private void onPoolsFetched(List<Object> result) {

		List<ICommand<?>> commands = new ArrayList<ICommand<?>>();
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
						this, (Photoset) obj);
				psCommands.add(cmd);
			}

			if (Group.class.isInstance(obj)) {
				// group
				FlickrUserGroupCommand cmd = new FlickrUserGroupCommand(this,
						(Group) obj);
				groupCommands.add(cmd);
			}
		}
		if (!psCommands.isEmpty()) {
			MenuSectionHeaderCommand cmd = new MenuSectionHeaderCommand(this,
					getString(R.string.menu_header_flickr_sets));
			psCommands.add(0, cmd);
		}

		if (!groupCommands.isEmpty()) {
			MenuSectionHeaderCommand cmd = new MenuSectionHeaderCommand(this,
					getString(R.string.menu_header_flickr_groups));
			groupCommands.add(0, cmd);
		}
		commands.addAll(psCommands);
		commands.addAll(groupCommands);

		mAdapter.addCommands(commands);
		mAdapter.notifyDataSetChanged();
		if (mProgressBar != null) {
			mProgressBar.setVisibility(View.INVISIBLE);
		}
	}

	private void fetchMySetsGroupsFromServer() {
		FetchFlickrUserPhotoCollectionTask task = new FetchFlickrUserPhotoCollectionTask(
				this);
		task.addTaskDoneListener(new IGeneralTaskDoneListener<List<Object>>() {
			@Override
			public void onTaskDone(List<Object> result) {
				onPoolsFetched(result);
			}
		});
		task.execute();
	}

	private void loadImage() {
		File bsRoot = new File(Environment.getExternalStorageDirectory(),
				IConstants.SD_CARD_FOLDER_NAME);
		if (!bsRoot.exists() && !bsRoot.mkdir()) {
			return;
		}
		File shareFile = new File(bsRoot, IConstants.SHARE_TEMP_FILE_NAME);
		FileInputStream fis;
		try {
			fis = new FileInputStream(shareFile);
			Bitmap bmp = BitmapFactory.decodeFileDescriptor(fis.getFD());
			mImageView.setImageBitmap(bmp);
		} catch (Exception e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Does the actual action to manage the photo's sets and groups.
	 */
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
		FlickrOrganizePhotoTask task = new FlickrOrganizePhotoTask(this, add,
				remove);
		task.addTaskDoneListener(new IGeneralTaskDoneListener<Integer>() {

			@Override
			public void onTaskDone(Integer result) {
				OrganizeMyFlickrPhotoActivity.this.finish();
			}
		});
		task.execute(this.mPhotoId);
	}

	private static class OrganizeAdapter extends CommandSectionListAdapter {

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
	public void onItemClick(AdapterView<?> parentView, View view, int position,
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
