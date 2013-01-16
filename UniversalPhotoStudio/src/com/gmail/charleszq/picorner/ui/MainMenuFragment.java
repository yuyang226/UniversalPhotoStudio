/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import java.util.ArrayList;
import java.util.List;

import org.jinstagram.auth.model.Token;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.github.yuyang226.j500px.J500px;
import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.model.IOfflineViewAbility;
import com.gmail.charleszq.picorner.model.MediaSourceType;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.FetchFlickrUserPhotoCollectionFromCacheTask;
import com.gmail.charleszq.picorner.task.flickr.FetchFlickrUserPhotoCollectionTask;
import com.gmail.charleszq.picorner.task.ig.InstagramOAuthTask;
import com.gmail.charleszq.picorner.task.px500.PxFetchUserProfileTask;
import com.gmail.charleszq.picorner.ui.command.CommandType;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.command.ICommandDoneListener;
import com.gmail.charleszq.picorner.ui.command.MenuSectionHeaderCommand;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.FlickrGalleryPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.FlickrIntestringCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.FlickrLoginCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.FlickrUserGroupCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.FlickrUserPhotoSetCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.MyFlickrContactPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.MyFlickrFavsCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.MyFlickrPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.MyFlickrPopularPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.ig.InstagramLikesCommand;
import com.gmail.charleszq.picorner.ui.command.ig.InstagramLoginCommand;
import com.gmail.charleszq.picorner.ui.command.ig.InstagramMyFeedsCommand;
import com.gmail.charleszq.picorner.ui.command.ig.InstagramPopularsCommand;
import com.gmail.charleszq.picorner.ui.command.ig.InstagramUserPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.px500.Px500MyPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.px500.PxEditorsPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.px500.PxFreshTodayPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.px500.PxMyFavPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.px500.PxMyFlowCommand;
import com.gmail.charleszq.picorner.ui.command.px500.PxPopularPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.px500.PxSignInCommand;
import com.gmail.charleszq.picorner.ui.command.px500.PxUpcomingPhotosCommand;
import com.gmail.charleszq.picorner.ui.helper.CommandSectionListAdapter;
import com.gmail.charleszq.picorner.ui.helper.MainMenuTextFilter;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.gmail.charleszq.picorner.utils.IConstants;
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
@SuppressLint("DefaultLocale")
public class MainMenuFragment extends AbstractFragmentWithImageFetcher {

	private CommandSectionListAdapter mSectionAdapter;
	private ProgressDialog mProgressDialog = null;
	private SearchView mSearchView;
	private MainMenuTextFilter mTextMenuFilter;

	/**
	 * The listener to handle the menu filter.
	 */
	private SearchView.OnQueryTextListener mQueryTextListener = new SearchView.OnQueryTextListener() {

		@Override
		public boolean onQueryTextSubmit(String query) {
			if (query == null || query.trim().length() == 0) {
				return false;
			}

			if (mTextMenuFilter == null) {
				mTextMenuFilter = new MainMenuTextFilter(mSectionAdapter);
			}
			mTextMenuFilter.filter(query);

			// hide the soft keyboard
			InputMethodManager imm = (InputMethodManager) getActivity()
					.getSystemService(Service.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
			return true;
		}

		@Override
		public boolean onQueryTextChange(String newText) {
			if (newText == null || newText.trim().length() == 0) {
				prepareSections();
				return true;
			} else
				return false;
		}
	};

	/**
	 * The listener to handle user's flickr photo set /group/gallery menu items.
	 */
	private IGeneralTaskDoneListener<List<Object>> mPhotoSetsListener = new IGeneralTaskDoneListener<List<Object>>() {

		@Override
		public void onTaskDone(List<Object> result) {
			populatePhotoSetMenuItems(result);

		}
	};

	/**
	 * The command done listener
	 */
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
				try {
					mProgressDialog.cancel();
				} catch (Exception ex) {
					// do nothing.
				}
			}
		}

	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.setRetainInstance(true);

		View v = inflater.inflate(R.layout.main_menu, null);
		// filter view
		mSearchView = (SearchView) v.findViewById(R.id.main_menu_search_view);
		mSearchView.setOnQueryTextListener(mQueryTextListener);

		// menu list
		ListView lv = (ListView) v.findViewById(R.id.listView1);
		mSectionAdapter = new CommandSectionListAdapter(getActivity(),
				mImageFetcher);
		prepareSections();

		lv.setAdapter(mSectionAdapter);
		lv.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					MainMenuFragment.this.mImageFetcher.resume();
				} else {
					MainMenuFragment.this.mImageFetcher.pause();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListAdapter adapter = ((ListView) parent).getAdapter();
				@SuppressWarnings("unchecked")
				ICommand<Object> command = (ICommand<Object>) adapter
						.getItem(position);
				Object offline = command.getAdapter(IOfflineViewAbility.class);
				if (offline == null) {
					return false;
				} else {
					View frontView = view
							.findViewById(R.id.menu_item_container_2);
					View backView = view.findViewById(R.id.menu_item_back_view);
					frontView.setVisibility(View.INVISIBLE);
					backView.setVisibility(View.VISIBLE);
					ObjectAnimator
							.ofFloat(backView, "alpha", 0f, 1f).setDuration(1500).start(); //$NON-NLS-1$
					return true;
				}
			}
		});
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long itemId) {
				ListAdapter adapter = ((ListView) parent).getAdapter();
				@SuppressWarnings("unchecked")
				ICommand<Object> command = (ICommand<Object>) adapter
						.getItem(pos);
				if (command.getCommandType() == CommandType.MENU_HEADER_CMD) {
					command.execute(adapter);
					// view.animate().setDuration(3000).rotationX(90)
					// .rotationX(180).rotationX(270).rotationX(360);
				} else {
					command.setCommndDoneListener(mCommandDoneListener);
					command.execute();
				}
				if (PhotoListCommand.class.isInstance(command)) {
					mProgressDialog = ProgressDialog.show(
							parent.getContext(),
							"", //$NON-NLS-1$
							parent.getContext().getString(
									R.string.loading_photos));
					mProgressDialog.setCancelable(true);
				}
				if (!(command instanceof FlickrLoginCommand)
						&& !(command instanceof PxSignInCommand)
						&& !CommandType.MENU_HEADER_CMD.equals(command
								.getCommandType())) {
					MainSlideMenuActivity act = (MainSlideMenuActivity) MainMenuFragment.this
							.getActivity();
					act.closeMenu();
				}
			}
		});
		return v;
	}

	private void prepareSections() {

		mSectionAdapter.clearSections();
		mSectionAdapter.addCommands(createPx500MenuItems());
		mSectionAdapter.addCommands(createInstagramMenuItems());
		mSectionAdapter.addCommands(createFlickrGeneralMenuItems());
		mSectionAdapter.notifyDataSetChanged();

		// get photo sets.
		if (isUserAuthedFlickr()) {
			// first, try get information from cache.
			FetchFlickrUserPhotoCollectionFromCacheTask cacheTask = new FetchFlickrUserPhotoCollectionFromCacheTask();
			cacheTask
					.addTaskDoneListener(new IGeneralTaskDoneListener<List<Object>>() {

						@Override
						public void onTaskDone(List<Object> result) {

							if (result != null) {
								mPhotoSetsListener.onTaskDone(result);
							}

							Activity act = getActivity();
							// activity might be null due to the configuration
							// change.
							if (act != null) {
								FetchFlickrUserPhotoCollectionTask task = new FetchFlickrUserPhotoCollectionTask(
										getActivity());
								if (result == null) {
									task.addTaskDoneListener(mPhotoSetsListener);
								}
								task.execute();
							}
						}
					});
			cacheTask.execute();
		}
	}

	/**
	 * After photo sets, groups and gallery information are returned, populate
	 * the menu items.
	 * 
	 * @param list
	 */
	private void populatePhotoSetMenuItems(List<Object> list) {

		Activity act = getActivity();
		if (act == null) {
			return;
		}

		final List<ICommand<?>> photosetCommands = new ArrayList<ICommand<?>>();
		final List<ICommand<?>> groupCommands = new ArrayList<ICommand<?>>();
		final List<ICommand<?>> galleryCommands = new ArrayList<ICommand<?>>();

		String photoSetHeaderName = act
				.getString(R.string.menu_header_flickr_sets);
		String groupHeaderName = act
				.getString(R.string.menu_header_flickr_groups);
		String galleryHeaderName = act
				.getString(R.string.menu_header_flickr_gallery);
		for (Object obj : list) {
			if (obj instanceof Photoset) {
				ICommand<?> cmd = new FlickrUserPhotoSetCommand(act,
						(Photoset) obj);
				cmd.setCommandCategory(photoSetHeaderName);
				photosetCommands.add(cmd);
			}

			if (obj instanceof Group) {
				ICommand<?> cmd = new FlickrUserGroupCommand(act, (Group) obj);
				cmd.setCommandCategory(groupHeaderName);
				groupCommands.add(cmd);
			}

			if (obj instanceof Gallery) {
				ICommand<?> cmd = new FlickrGalleryPhotosCommand(act,
						(Gallery) obj);
				cmd.setCommandCategory(galleryHeaderName);
				galleryCommands.add(cmd);
			}
		}
		if (!photosetCommands.isEmpty()) {
			ICommand<?> photosetCommand = new MenuSectionHeaderCommand(act,
					photoSetHeaderName, true);
			photosetCommands.add(0, photosetCommand);
			mSectionAdapter.addCommands(photosetCommands);
		}

		if (!groupCommands.isEmpty()) {
			ICommand<?> groupCommand = new MenuSectionHeaderCommand(act,
					groupHeaderName, true);
			groupCommands.add(0, groupCommand);
			mSectionAdapter.addCommands(groupCommands);
		}

		if (!galleryCommands.isEmpty()) {
			ICommand<?> galleryCommand = new MenuSectionHeaderCommand(act,
					galleryHeaderName, true);
			galleryCommands.add(0, galleryCommand);
			mSectionAdapter.addCommands(galleryCommands);
		}
		mSectionAdapter.notifyDataSetChanged();
	}

	private boolean isUserAuthedFlickr() {
		PicornerApplication app = (PicornerApplication) this.getActivity()
				.getApplication();
		return app.getFlickrUserId() != null;
	}

	private boolean isUserAuthedPx500() {
		PicornerApplication app = (PicornerApplication) this.getActivity()
				.getApplication();
		return app.getPx500OauthToken() != null;
	}

	private boolean isUserAuthedInstagram() {
		PicornerApplication app = (PicornerApplication) this.getActivity()
				.getApplication();
		return app.getInstagramAuthToken() != null;
	}

	private List<ICommand<?>> createInstagramMenuItems() {
		List<ICommand<?>> commands = new ArrayList<ICommand<?>>();
		Context ctx = getActivity();

		String headerName = ctx.getString(R.string.menu_header_ig);
		ICommand<?> command = new MenuSectionHeaderCommand(ctx, headerName);
		commands.add(command);

		command = new InstagramPopularsCommand(ctx);
		command.setCommandCategory(headerName);
		commands.add(command);

		if (isUserAuthedInstagram()) {
			command = new InstagramMyFeedsCommand(ctx);
			command.setCommandCategory(headerName);
			commands.add(command);

			command = new InstagramLikesCommand(ctx);
			command.setCommandCategory(headerName);
			commands.add(command);

			PicornerApplication app = (PicornerApplication) getActivity()
					.getApplication();
			String myUserId = app.getInstagramUserId();
			Author a = new Author();
			a.setUserId(myUserId);
			command = new InstagramUserPhotosCommand(ctx, a) {
				@Override
				public String getDescription() {
					// since we don't save my own user name
					return getString(R.string.cd_ig_my_photos);
				}

			};
			command.setCommandCategory(headerName);
			commands.add(command);
		} else {
			command = new InstagramLoginCommand(ctx);
			command.setCommandCategory(headerName);
			commands.add(command);
		}
		return commands;
	}

	private List<ICommand<?>> createPx500MenuItems() {
		List<ICommand<?>> commands = new ArrayList<ICommand<?>>();
		String headerName = getActivity().getString(R.string.menu_header_px500);

		ICommand<?> command = new MenuSectionHeaderCommand(getActivity(),
				headerName);
		commands.add(command);

		command = new PxPopularPhotosCommand(getActivity());
		command.setCommandCategory(headerName);
		commands.add(command);

		command = new PxEditorsPhotosCommand(getActivity());
		command.setCommandCategory(headerName);
		commands.add(command);

		command = new PxUpcomingPhotosCommand(getActivity());
		command.setCommandCategory(headerName);
		commands.add(command);

		command = new PxFreshTodayPhotosCommand(getActivity());
		command.setCommandCategory(headerName);
		commands.add(command);

		if (isUserAuthedPx500()) {
			command = new Px500MyPhotosCommand(getActivity());
			command.setCommandCategory(headerName);
			commands.add(command);

			command = new PxMyFavPhotosCommand(getActivity());
			command.setCommandCategory(headerName);
			commands.add(command);

			command = new PxMyFlowCommand(getActivity());
			command.setCommandCategory(headerName);
			commands.add(command);
		} else {
			command = new PxSignInCommand(getActivity());
			command.setCommandCategory(headerName);
			commands.add(command);
		}
		return commands;
	}

	private List<ICommand<?>> createFlickrGeneralMenuItems() {
		List<ICommand<?>> commands = new ArrayList<ICommand<?>>();
		Context ctx = getActivity();

		// section header.
		String headerName = ctx.getString(R.string.menu_header_flickr);
		ICommand<?> command = new MenuSectionHeaderCommand(ctx, headerName);
		commands.add(command);

		// real commands
		command = new FlickrIntestringCommand(this.getActivity());
		command.setCommandCategory(headerName);
		commands.add(command);

		if (!isUserAuthedFlickr()) {
			command = new FlickrLoginCommand(ctx);
			command.setCommandCategory(headerName);
			commands.add(command);
		} else {
			command = new MyFlickrPhotosCommand(ctx);
			command.setCommandCategory(headerName);
			commands.add(command);

			command = new MyFlickrFavsCommand(ctx);
			command.setCommandCategory(headerName);
			commands.add(command);

			command = new MyFlickrPopularPhotosCommand(ctx);
			command.setCommandCategory(headerName);
			commands.add(command);

			command = new MyFlickrContactPhotosCommand(ctx);
			command.setCommandCategory(headerName);
			commands.add(command);

		}

		return commands;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.mProgressDialog = null;

		Intent intent = getActivity().getIntent();
		String schema = intent.getScheme();
		if (IConstants.ID_SCHEME.equals(schema)) {

			// if flickr already authed.
			if (isUserAuthedFlickr()) {
				return;
			}

			Uri uri = intent.getData();
			String query = uri.getQuery();
			String[] data = query.split("&"); //$NON-NLS-1$
			if (data != null && data.length == 2) {
				String oauthToken = data[0].substring(data[0].indexOf("=") + 1); //$NON-NLS-1$
				String oauthVerifier = data[1]
						.substring(data[1].indexOf("=") + 1); //$NON-NLS-1$

				String secret = getTokenSecret(MediaSourceType.FLICKR);
				if (secret != null) {
					GetOAuthTokenTask task = new GetOAuthTokenTask(this,
							MediaSourceType.FLICKR);
					task.execute(oauthToken, secret, oauthVerifier);
				}
			}
		} else if (IConstants.ID_IG_SCHEME.equals(schema)) {
			if (isUserAuthedInstagram()) {
				return;
			}
			Uri uri = intent.getData();
			instagramAuth(uri);
		} else if (IConstants.PX500_OAUTH_CALLBACK_SCHEMA.equals(schema)) {
			if (isUserAuthedPx500()) {
				return;
			}
			Uri pxUri = intent.getData();
			px500Auth(pxUri);
		}
	}

	private void px500Auth(Uri pxUri) {
		Log.d(TAG, pxUri.toString());
		String query = pxUri.getQuery();
		String[] data = query.split("&"); //$NON-NLS-1$
		if (data != null && data.length == 2) {
			String oauthToken = data[0].substring(data[0].indexOf("=") + 1); //$NON-NLS-1$
			String oauthVerifier = data[1].substring(data[1].indexOf("=") + 1); //$NON-NLS-1$

			String secret = getTokenSecret(MediaSourceType.PX500);
			if (secret != null) {
				GetOAuthTokenTask task = new GetOAuthTokenTask(this,
						MediaSourceType.PX500);
				task.execute(oauthToken, secret, oauthVerifier);
			}
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
						MainMenuFragment.this.onOAuthDone(result,
								MediaSourceType.INSTAGRAM);
					}
				});
				task.execute(code);
			} else {
				Log.e(getClass().getName(),
						"Instagram request token code not returned."); //$NON-NLS-1$
			}
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
			AsyncTask<String, Integer, Object> {

		private MainMenuFragment mAuthDialog;
		private MediaSourceType mMediaSourceType;

		GetOAuthTokenTask(MainMenuFragment context, MediaSourceType type) {
			this.mAuthDialog = context;
			this.mMediaSourceType = type;
		}

		@Override
		protected Object doInBackground(String... params) {
			String oauthToken = params[0];
			String oauthTokenSecret = params[1];
			String verifier = params[2];

			if (mMediaSourceType == MediaSourceType.FLICKR) {
				Flickr f = FlickrHelper.getInstance().getFlickr();
				OAuthInterface oauthApi = f.getOAuthInterface();
				try {
					return oauthApi.getAccessToken(oauthToken,
							oauthTokenSecret, verifier);
				} catch (Exception e) {
					return null;
				}
			} else if (mMediaSourceType == MediaSourceType.PX500) {
				try {
					J500px px = new J500px(IConstants.PX500_CONSUMER_KEY,
							IConstants.PX500_CONSUMER_SECRET);
					return px.getOAuthInterface().getAccessToken(oauthToken,
							oauthTokenSecret, verifier);
				} catch (Exception e) {
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			if (mAuthDialog != null) {
				mAuthDialog.onOAuthDone(result, mMediaSourceType);
			}
		}
	}

	private String getTokenSecret(MediaSourceType type) {
		PicornerApplication app = (PicornerApplication) getActivity()
				.getApplication();
		switch (type) {
		case FLICKR:
			return app.getFlickrTokenSecret();
		case PX500:
			return app.getPx500TokenSecret();
		default:
			return null; // not support
		}
	}

	void onOAuthDone(Object result, MediaSourceType type) {

		if (result == null) {
			boolean signedin = false;
			String msg = getString(R.string.fail_to_oauth);
			switch (type) {
			case FLICKR:
				msg = String
						.format(msg, getString(R.string.menu_header_flickr));
				signedin = isUserAuthedFlickr();
				break;
			case PX500:
				msg = String.format(msg, getString(R.string.menu_header_px500));
				signedin = isUserAuthedPx500();
				break;
			case INSTAGRAM:
				msg = String.format(msg, getString(R.string.menu_header_ig));
				signedin = isUserAuthedInstagram();
				break;
			}
			if (!signedin) {
				msg = msg.toLowerCase();
				Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
			}
		} else {
			PicornerApplication app = (PicornerApplication) getActivity()
					.getApplication();
			if (type == MediaSourceType.FLICKR) {
				OAuth oauth = (OAuth) result;
				User user = oauth.getUser();
				OAuthToken token = oauth.getToken();
				if (user == null || user.getId() == null || token == null
						|| token.getOauthToken() == null
						|| token.getOauthTokenSecret() == null) {
					Toast.makeText(getActivity(),
							getActivity().getString(R.string.fail_to_oauth),
							Toast.LENGTH_LONG).show();
					return;
				}
				app.saveFlickrAuthToken(oauth);
			} else if (type == MediaSourceType.PX500) {
				com.github.yuyang226.j500px.oauth.OAuth pxoauth = (com.github.yuyang226.j500px.oauth.OAuth) result;
				com.github.yuyang226.j500px.oauth.OAuthToken token = pxoauth
						.getToken();
				app.savePxAuthToken(token);

				// fetch user profile
				PxFetchUserProfileTask userTask = new PxFetchUserProfileTask(
						getActivity());
				userTask.execute();
			} else {

			}
			prepareSections();
		}
	}

}
