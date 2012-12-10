/**
 * 
 */
package com.gmail.charleszq.ups.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jinstagram.auth.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.ups.task.flickr.FetchFlickrUserPhotoCollectionTask;
import com.gmail.charleszq.ups.task.ig.InstagramOAuthTask;
import com.gmail.charleszq.ups.ui.adapter.CommandSectionListAdapter;
import com.gmail.charleszq.ups.ui.command.DummyCommand;
import com.gmail.charleszq.ups.ui.command.ICommand;
import com.gmail.charleszq.ups.ui.command.ICommandDoneListener;
import com.gmail.charleszq.ups.ui.command.PhotoListCommand;
import com.gmail.charleszq.ups.ui.command.flickr.FlickrGalleryPhotosCommand;
import com.gmail.charleszq.ups.ui.command.flickr.FlickrIntestringCommand;
import com.gmail.charleszq.ups.ui.command.flickr.FlickrLoginCommand;
import com.gmail.charleszq.ups.ui.command.flickr.FlickrUserGroupCommand;
import com.gmail.charleszq.ups.ui.command.flickr.FlickrUserPhotoSetCommand;
import com.gmail.charleszq.ups.ui.command.flickr.MyFlickrContactPhotosCommand;
import com.gmail.charleszq.ups.ui.command.flickr.MyFlickrFavsCommand;
import com.gmail.charleszq.ups.ui.command.flickr.MyFlickrPhotosCommand;
import com.gmail.charleszq.ups.ui.command.flickr.MyFlickrPopularPhotosCommand;
import com.gmail.charleszq.ups.ui.command.ig.InstagramLoginCommand;
import com.gmail.charleszq.ups.ui.command.ig.InstagramMyFeedsCommand;
import com.gmail.charleszq.ups.ui.command.ig.InstagramPopularsCommand;
import com.gmail.charleszq.ups.utils.FlickrHelper;
import com.gmail.charleszq.ups.utils.IConstants;
import com.gmail.charleszq.ups.utils.ImageCache.ImageCacheParams;
import com.gmail.charleszq.ups.utils.ImageFetcher;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.galleries.Gallery;
import com.googlecode.flickrjandroid.groups.Group;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthInterface;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photosets.Photoset;

/**
 * 
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class MainMenuFragment extends Fragment {

	private static Logger logger = LoggerFactory
			.getLogger(MainMenuFragment.class);

	private static final String IMAGE_CACHE_DIR = "cmdicon"; //$NON-NLS-1$

	private CommandSectionListAdapter mSectionAdapter;
	private ProgressDialog mProgressDialog = null;
	private ImageFetcher mImageFetcher;

	private IGeneralTaskDoneListener<Collection<?>> mPhotoSetsListener = new IGeneralTaskDoneListener<Collection<?>>() {

		@Override
		public void onTaskDone(Collection<?> result) {
			populatePhotoSetMenuItems(result);

		}
	};

	private ICommandDoneListener<Object> mCommandDoneListener = new ICommandDoneListener<Object>() {

		@Override
		public void onCommandDone(ICommand<Object> command, Object t) {
			MainSlideMenuActivity act = (MainSlideMenuActivity) MainMenuFragment.this
					.getActivity();
			if (act == null) {
				// when configuration changed, the activity of this fragement
				// might be null,
				// then try to get it from the command.
				Context ctx = (Context) command.getAdapter(Context.class);
				if (ctx != null && ctx instanceof MainSlideMenuActivity) {
					act = (MainSlideMenuActivity) ctx;
				}
			}
			if (act != null)
				act.onCommandDone(command, t);
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
		}

	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		int thumbSize = getResources().getDimensionPixelSize(
				R.dimen.cmd_icon_size);

		ImageCacheParams cacheParams = new ImageCacheParams(getActivity(),
				IMAGE_CACHE_DIR);

		// Set memory cache to 25% of mem class
		cacheParams.setMemCacheSizePercent(getActivity(), 0.25f);

		// The ImageFetcher takes care of loading images into our ImageView
		// children asynchronously
		mImageFetcher = new ImageFetcher(getActivity(), thumbSize);
		mImageFetcher.setLoadingImage(R.drawable.icon);
		mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(),
				cacheParams);

		View v = inflater.inflate(R.layout.main_menu, null);
		ListView lv = (ListView) v.findViewById(R.id.listView1);
		lv.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView absListView,
					int scrollState) {
				// Pause fetcher to ensure smoother scrolling when flinging
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
					mImageFetcher.setPauseWork(true);
				} else {
					mImageFetcher.setPauseWork(false);
				}
			}

			@Override
			public void onScroll(AbsListView absListView, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});

		mSectionAdapter = new CommandSectionListAdapter(getActivity(),
				mImageFetcher);
		prepareSections();

		lv.setAdapter(mSectionAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long itemId) {
				ListAdapter adapter = ((ListView) parent).getAdapter();
				@SuppressWarnings("unchecked")
				ICommand<Object> command = (ICommand<Object>) adapter
						.getItem(pos);
				command.addCommndDoneListener(mCommandDoneListener);
				command.execute();
				if (PhotoListCommand.class.isInstance(command)) {
					mProgressDialog = ProgressDialog.show(
							parent.getContext(),
							"", //$NON-NLS-1$
							parent.getContext().getString(
									R.string.loading_photos));
					mProgressDialog.setCancelable(true);
				}
				if (!(command instanceof FlickrLoginCommand)) {
					MainSlideMenuActivity act = (MainSlideMenuActivity) MainMenuFragment.this
							.getActivity();
					act.closeMenu();
				}
			}
		});
		return v;
	}

	@Override
	public void onDestroy() {
		mImageFetcher.closeCache();
		super.onDestroy();
	}

	@Override
	public void onPause() {
		mImageFetcher.setExitTasksEarly(true);
		mImageFetcher.flushCache();
		super.onPause();
	}

	private void prepareSections() {

		mSectionAdapter.clearSections();
		mSectionAdapter.addCommands(createInstagramMenuItems());
		mSectionAdapter.addCommands(createFlickrGeneralMenuItems());
		mSectionAdapter.notifyDataSetChanged();

		// get photo sets.
		if (isUserAuthedFlickr()) {
			FetchFlickrUserPhotoCollectionTask task = new FetchFlickrUserPhotoCollectionTask(
					this.getActivity());
			task.addTaskDoneListener(mPhotoSetsListener);
			task.execute();
		}
	}

	/**
	 * After photo sets, groups and gallery information are returned, populate
	 * the menu items.
	 * 
	 * @param list
	 */
	private void populatePhotoSetMenuItems(Collection<?> list) {

		if (!this.isVisible())
			return;

		final List<ICommand<?>> photosetCommands = new ArrayList<ICommand<?>>();
		final List<ICommand<?>> groupCommands = new ArrayList<ICommand<?>>();
		final List<ICommand<?>> galleryCommands = new ArrayList<ICommand<?>>();

		for (Object obj : list) {
			if (obj instanceof Photoset) {
				ICommand<?> cmd = new FlickrUserPhotoSetCommand(
						this.getActivity(), (Photoset) obj);
				photosetCommands.add(cmd);
			}

			if (obj instanceof Group) {
				ICommand<?> cmd = new FlickrUserGroupCommand(
						this.getActivity(), (Group) obj);
				groupCommands.add(cmd);
			}

			if (obj instanceof Gallery) {
				ICommand<?> cmd = new FlickrGalleryPhotosCommand(getActivity(),
						(Gallery) obj);
				galleryCommands.add(cmd);
			}
		}
		if (!photosetCommands.isEmpty()) {
			ICommand<?> sectionCommand = new DummyCommand(getActivity(),
					getActivity().getResources().getString(
							R.string.menu_header_flickr_sets));
			photosetCommands.add(0, sectionCommand);
			mSectionAdapter.addCommands(photosetCommands);
		}

		if (!groupCommands.isEmpty()) {
			ICommand<?> groupCommand = new DummyCommand(getActivity(),
					getActivity().getResources().getString(
							R.string.menu_header_flickr_groups));
			groupCommands.add(0, groupCommand);
			mSectionAdapter.addCommands(groupCommands);
		}

		if (!galleryCommands.isEmpty()) {
			ICommand<?> galleryCommand = new DummyCommand(getActivity(),
					getActivity()
							.getString(R.string.menu_header_flickr_gallery));
			galleryCommands.add(0, galleryCommand);
			mSectionAdapter.addCommands(galleryCommands);
		}
		mSectionAdapter.notifyDataSetChanged();
	}

	private boolean isUserAuthedFlickr() {
		UPSApplication app = (UPSApplication) this.getActivity()
				.getApplication();
		return app.getUserId() != null;
	}

	private boolean isUserAuthedInstagram() {
		UPSApplication app = (UPSApplication) this.getActivity()
				.getApplication();
		return app.getInstagramAuthToken() != null;
	}

	private List<ICommand<?>> createInstagramMenuItems() {
		List<ICommand<?>> commands = new ArrayList<ICommand<?>>();
		Context ctx = getActivity();
		ICommand<?> command = new DummyCommand(ctx,
				ctx.getString(R.string.menu_header_ig));
		commands.add(command);

		command = new InstagramPopularsCommand(ctx);
		commands.add(command);

		if (isUserAuthedInstagram()) {
			command = new InstagramMyFeedsCommand(ctx);
			commands.add(command);
		} else {
			command = new InstagramLoginCommand(ctx);
			commands.add(command);
		}
		return commands;
	}

	private List<ICommand<?>> createFlickrGeneralMenuItems() {
		List<ICommand<?>> commands = new ArrayList<ICommand<?>>();
		Context ctx = getActivity();

		// section header.
		ICommand<?> command = new DummyCommand(ctx, ctx.getResources()
				.getString(R.string.menu_header_flickr));
		commands.add(command);

		// real commands
		command = new FlickrIntestringCommand(this.getActivity());
		commands.add(command);

		if (!isUserAuthedFlickr()) {
			command = new FlickrLoginCommand(ctx);
			commands.add(command);
		} else {
			command = new MyFlickrPhotosCommand(ctx);
			commands.add(command);

			command = new MyFlickrFavsCommand(ctx);
			commands.add(command);

			command = new MyFlickrPopularPhotosCommand(ctx);
			commands.add(command);

			command = new MyFlickrContactPhotosCommand(ctx);
			commands.add(command);

		}

		return commands;
	}

	@Override
	public void onResume() {
		super.onResume();
		mImageFetcher.setExitTasksEarly(false);
		Intent intent = getActivity().getIntent();
		String schema = intent.getScheme();
		if (IConstants.ID_SCHEME.equals(schema)) {

			// if user already login, just return
			UPSApplication app = (UPSApplication) getActivity()
					.getApplication();
			if (app.getUserId() != null) {
				return;
			}

			Uri uri = intent.getData();
			String query = uri.getQuery();
			logger.debug("Returned Query: {}", query); //$NON-NLS-1$
			String[] data = query.split("&"); //$NON-NLS-1$
			if (data != null && data.length == 2) {
				String oauthToken = data[0].substring(data[0].indexOf("=") + 1); //$NON-NLS-1$
				String oauthVerifier = data[1]
						.substring(data[1].indexOf("=") + 1); //$NON-NLS-1$
				logger.debug(
						"OAuth Token: {}; OAuth Verifier: {}", oauthToken, oauthVerifier); //$NON-NLS-1$

				String secret = getTokenSecret();
				if (secret != null) {
					GetOAuthTokenTask task = new GetOAuthTokenTask(this);
					task.execute(oauthToken, secret, oauthVerifier);
				}
			}
		} else if (IConstants.ID_IG_SCHEME.equals(schema)) {
			Uri uri = intent.getData();
			instagramAuth(uri);
		}
	}

	/**
	 * After recieves the auth token of instagram.
	 * 
	 * @param uri
	 */
	private void instagramAuth(Uri uri) {
		String authority = uri.getAuthority();
		if (IConstants.IG_AUTHORITY.equals(authority)) {
			// instagram
			String query = uri.getQuery();
			int index = query.indexOf("="); //$NON-NLS-1$
			if (index != -1) {
				String code = query.substring(index + 1);
				InstagramOAuthTask task = new InstagramOAuthTask(getActivity());
				task.addTaskDoneListener(new IGeneralTaskDoneListener<Token>() {

					@Override
					public void onTaskDone(Token result) {
						MainMenuFragment.this.onInstagramAuthDone(result);
					}
				});
				task.execute(code);
			} else {
				logger.error("Instagram request token code not returned."); //$NON-NLS-1$
			}
		}
	}

	private void onInstagramAuthDone(Token result) {
		if( result == null ) {
			
		} else {
			prepareSections();
		}
	}

	/**
	 * Represents the task to get the oauth token and user information.
	 * <p>
	 * This task should be called only after you got the request oauth request
	 * token and the verifier.
	 * 
	 * @author charles
	 * 
	 */
	private static class GetOAuthTokenTask extends
			AsyncTask<String, Integer, OAuth> {

		private MainMenuFragment mAuthDialog;

		GetOAuthTokenTask(MainMenuFragment context) {
			this.mAuthDialog = context;
		}

		@Override
		protected OAuth doInBackground(String... params) {
			String oauthToken = params[0];
			String oauthTokenSecret = params[1];
			String verifier = params[2];

			Flickr f = FlickrHelper.getInstance().getFlickr();
			OAuthInterface oauthApi = f.getOAuthInterface();
			try {
				return oauthApi.getAccessToken(oauthToken, oauthTokenSecret,
						verifier);
			} catch (Exception e) {
				logger.error(e.getMessage());
				return null;
			}

		}

		@Override
		protected void onPostExecute(OAuth result) {
			if (mAuthDialog != null) {
				mAuthDialog.onOAuthDone(result);
			}
		}
	}

	private String getTokenSecret() {
		UPSApplication app = (UPSApplication) getActivity().getApplication();
		return app.getFlickrTokenSecret();
	}

	void onOAuthDone(OAuth result) {

		if (result == null) {
			Toast.makeText(getActivity(),
					getActivity().getString(R.string.fail_to_oauth),
					Toast.LENGTH_LONG).show();
		} else {
			User user = result.getUser();
			OAuthToken token = result.getToken();
			if (user == null || user.getId() == null || token == null
					|| token.getOauthToken() == null
					|| token.getOauthTokenSecret() == null) {
				Toast.makeText(getActivity(),
						getActivity().getString(R.string.fail_to_oauth),
						Toast.LENGTH_LONG).show();
				return;
			}
			UPSApplication app = (UPSApplication) getActivity()
					.getApplication();
			app.saveFlickrAuthToken(result);

			prepareSections();
		}
	}

}
