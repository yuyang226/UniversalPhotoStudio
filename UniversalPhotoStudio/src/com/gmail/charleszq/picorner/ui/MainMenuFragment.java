/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jinstagram.auth.model.Token;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.flickr.FetchFlickrUserPhotoCollectionTask;
import com.gmail.charleszq.picorner.task.ig.InstagramOAuthTask;
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
import com.gmail.charleszq.picorner.ui.command.px500.PxEditorsPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.px500.PxFreshTodayPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.px500.PxPopularPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.px500.PxUpcomingPhotosCommand;
import com.gmail.charleszq.picorner.ui.helper.CommandSectionListAdapter;
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
public class MainMenuFragment extends AbstractFragmentWithImageFetcher {

	private CommandSectionListAdapter mSectionAdapter;
	private ProgressDialog mProgressDialog = null;

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
		this.setRetainInstance(true);

		View v = inflater.inflate(R.layout.main_menu, null);
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
					view.animate().setDuration(3000).rotationX(90)
							.rotationX(180).rotationX(270).rotationX(360);
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

		String photoSetHeaderName = getActivity().getString(
				R.string.menu_header_flickr_sets);
		String groupHeaderName = getActivity().getString(
				R.string.menu_header_flickr_groups);
		String galleryHeaderName = getActivity().getString(
				R.string.menu_header_flickr_gallery);
		for (Object obj : list) {
			if (obj instanceof Photoset) {
				ICommand<?> cmd = new FlickrUserPhotoSetCommand(
						this.getActivity(), (Photoset) obj);
				cmd.setCommandCategory(photoSetHeaderName);
				photosetCommands.add(cmd);
			}

			if (obj instanceof Group) {
				ICommand<?> cmd = new FlickrUserGroupCommand(
						this.getActivity(), (Group) obj);
				cmd.setCommandCategory(groupHeaderName);
				groupCommands.add(cmd);
			}

			if (obj instanceof Gallery) {
				ICommand<?> cmd = new FlickrGalleryPhotosCommand(getActivity(),
						(Gallery) obj);
				cmd.setCommandCategory(galleryHeaderName);
				galleryCommands.add(cmd);
			}
		}
		if (!photosetCommands.isEmpty()) {
			ICommand<?> sectionCommand = new MenuSectionHeaderCommand(
					getActivity(), photoSetHeaderName);
			photosetCommands.add(0, sectionCommand);
			mSectionAdapter.addCommands(photosetCommands);
		}

		if (!groupCommands.isEmpty()) {
			ICommand<?> groupCommand = new MenuSectionHeaderCommand(
					getActivity(), groupHeaderName);
			groupCommands.add(0, groupCommand);
			mSectionAdapter.addCommands(groupCommands);
		}

		if (!galleryCommands.isEmpty()) {
			ICommand<?> galleryCommand = new MenuSectionHeaderCommand(
					getActivity(), galleryHeaderName);
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
		return app.getPx500UserId() != null;
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
					//since we don't save my own user name
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

		if (!isUserAuthedPx500()) {
			// command = new PxSignInCommand(getActivity());
			// command.setCommandCategory(headerName);
			// commands.add(command);
		}

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

			// if user already login, just return
			PicornerApplication app = (PicornerApplication) getActivity()
					.getApplication();
			if (app.getFlickrUserId() != null) {
				return;
			}

			Uri uri = intent.getData();
			String query = uri.getQuery();
			String[] data = query.split("&"); //$NON-NLS-1$
			if (data != null && data.length == 2) {
				String oauthToken = data[0].substring(data[0].indexOf("=") + 1); //$NON-NLS-1$
				String oauthVerifier = data[1]
						.substring(data[1].indexOf("=") + 1); //$NON-NLS-1$

				String secret = getTokenSecret();
				if (secret != null) {
					GetOAuthTokenTask task = new GetOAuthTokenTask(this);
					task.execute(oauthToken, secret, oauthVerifier);
				}
			}
		} else if (IConstants.ID_IG_SCHEME.equals(schema)) {
			Uri uri = intent.getData();
			instagramAuth(uri);
		} else if (IConstants.PX500_OAUTH_CALLBACK_SCHEMA.equals(schema)) {
			Uri pxUri = intent.getData();
			px500Auth(pxUri);
		}
	}

	private void px500Auth(Uri pxUri) {
		// TODO 500px oauth
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
				Log.e(getClass().getName(),
						"Instagram request token code not returned."); //$NON-NLS-1$
			}
		}
	}

	private void onInstagramAuthDone(Token result) {
		if (result == null) {
			// TODO revisit here, what to do if auth failed?
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
		PicornerApplication app = (PicornerApplication) getActivity()
				.getApplication();
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
			PicornerApplication app = (PicornerApplication) getActivity()
					.getApplication();
			app.saveFlickrAuthToken(result);

			prepareSections();
		}
	}

}
