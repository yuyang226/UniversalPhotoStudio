/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import java.util.ArrayList;
import java.util.List;

import org.jinstagram.auth.model.Token;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.github.yuyang226.j500px.J500px;
import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.SPUtil;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.model.MediaSourceType;
import com.gmail.charleszq.picorner.msg.Message;
import com.gmail.charleszq.picorner.msg.MessageBus;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.ig.InstagramOAuthTask;
import com.gmail.charleszq.picorner.task.px500.PxFetchUserProfileTask;
import com.gmail.charleszq.picorner.ui.command.CommandType;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.command.ICommandDoneListener;
import com.gmail.charleszq.picorner.ui.command.MenuSectionHeaderCommand;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.FlickrIntestringCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.FlickrLoginCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.MyFlickrContactPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.MyFlickrFavsCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.MyFlickrPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.MyFlickrPopularPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.MyGalleriesCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.MyGroupsCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.MyPhotosetsCommand;
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
import com.gmail.charleszq.picorner.ui.helper.IHiddenView;
import com.gmail.charleszq.picorner.ui.helper.IHiddenView.IHiddenViewActionListener;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthInterface;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;

/**
 * 
 * @author Charles(charleszq@gmail.com)
 * 
 */
@SuppressLint("DefaultLocale")
public class MainMenuFragment extends AbstractFragmentWithImageFetcher {

	private CommandSectionListAdapter mSectionAdapter;
	private ProgressDialog mProgressDialog = null;
	private FrameLayout mBackViewContainer;
	private ListView mListView;

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

	/**
	 * The hidden view listener.
	 */
	private IHiddenViewActionListener mHideViewCancelListener = new IHiddenViewActionListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void onAction(int action, ICommand<?> command, IHiddenView view,
				Object... data) {
			Context ctx = (Context) command.getAdapter(Context.class);
			switch (action) {
			case IHiddenView.ACTION_CANCEL:
				hideHiddenView(view.getView(ctx));
				break;
			case IHiddenView.ACTION_DO:
				doCommand((ICommand<Object>) command, data);
				hideHiddenView(view.getView(ctx));
				break;
			case IHiddenView.ACTION_JUST_CMD:
				doCommand((ICommand<Object>) command, data);
				break;
			}
		}
	};

	private void doCommand(ICommand<Object> command, Object... params) {
		if (PhotoListCommand.class.isInstance(command)) {
			Message msg = new Message(Message.CANCEL_COMMAND, null, null,
					command);
			MessageBus.broadcastMessage(msg);
			mProgressDialog = ProgressDialog.show(getActivity(),
					"", getActivity() //$NON-NLS-1$
							.getString(R.string.loading_photos));
			mProgressDialog.setCanceledOnTouchOutside(true);
		}
		command.setCommndDoneListener(mCommandDoneListener);
		command.execute(params);
		// close the menu.
		MainSlideMenuActivity act = (MainSlideMenuActivity) getActivity();
		act.closeMenu();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.setRetainInstance(true);

		View v = inflater.inflate(R.layout.main_menu, null);
		mBackViewContainer = (FrameLayout) v
				.findViewById(R.id.main_menu_container);

		// menu list
		mListView = (ListView) v.findViewById(R.id.listView1);
		mSectionAdapter = new CommandSectionListAdapter(getActivity());
		prepareSections();

		mListView.setAdapter(mSectionAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long itemId) {
				ListAdapter adapter = ((ListView) parent).getAdapter();
				@SuppressWarnings("unchecked")
				ICommand<Object> command = (ICommand<Object>) adapter
						.getItem(pos);
				Context ctx = (Context) command.getAdapter(Context.class);

				IHiddenView hiddenView = (IHiddenView) command
						.getAdapter(IHiddenView.class);
				if (hiddenView == null) {
					command.setCommndDoneListener(mCommandDoneListener);
					command.execute();
				} else {
					hiddenView.init(command, mHideViewCancelListener);
					FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT, Gravity.CENTER_VERTICAL);
					View hv = hiddenView.getView(ctx);
					hv.setLayoutParams(param);
					mBackViewContainer.addView(hv);
					showHiddenView(hv);
					return;
				}

				if (PhotoListCommand.class.isInstance(command)) {
					Message msg = new Message(Message.CANCEL_COMMAND, null,
							null, command);
					MessageBus.broadcastMessage(msg);
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

	private void hideHiddenView(final View view) {
		ObjectAnimator a1 = ObjectAnimator
				.ofFloat(mListView, "alpha", 0f, 1f).setDuration(1000); //$NON-NLS-1$
		ObjectAnimator a2 = ObjectAnimator
				.ofFloat(view, "alpha", 1f, 0f).setDuration(1000); //$NON-NLS-1$
		AnimatorSet set = new AnimatorSet();
		set.playTogether(a1, a2);
		set.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationEnd(Animator animation) {
				mBackViewContainer.removeView(view);
				mListView.setVisibility(View.VISIBLE);
			}

		});
		set.start();
	}

	/**
	 * Shows the hidden view.
	 * 
	 * @param hv
	 */
	private void showHiddenView(View hv) {
		hv.setAlpha(0f); // hide the hidden view first
		ObjectAnimator a1 = ObjectAnimator
				.ofFloat(hv, "alpha", 0f, 1f).setDuration(1000); //$NON-NLS-1$
		ObjectAnimator a2 = ObjectAnimator
				.ofFloat(mListView, "alpha", 1f, 0f).setDuration(1000); //$NON-NLS-1$
		AnimatorSet set = new AnimatorSet();
		set.playTogether(a2, a1);
		set.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationEnd(Animator animation) {
				mListView.setVisibility(View.INVISIBLE);
			}
			
		});
		set.start();
	}

	private void prepareSections() {
		mSectionAdapter.clearSections();
		mSectionAdapter.addCommands(createPx500MenuItems());
		mSectionAdapter.addCommands(createFlickrGeneralMenuItems());
		mSectionAdapter.addCommands(createInstagramMenuItems());
		mSectionAdapter.notifyDataSetChanged();
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

		commands.add(command);

		if (isUserAuthedInstagram()) {
			command = new InstagramMyFeedsCommand(ctx);

			commands.add(command);

			command = new InstagramLikesCommand(ctx);

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

				@Override
				public Object getAdapter(Class<?> adapterClass) {
					if (adapterClass == ActionBar.class) {
						return Boolean.FALSE.toString();
					}
					return super.getAdapter(adapterClass);
				}
			};

			commands.add(command);
		} else {
			command = new InstagramLoginCommand(ctx);

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

		commands.add(command);

		command = new PxEditorsPhotosCommand(getActivity());

		commands.add(command);

		command = new PxUpcomingPhotosCommand(getActivity());

		commands.add(command);

		command = new PxFreshTodayPhotosCommand(getActivity());

		commands.add(command);

		if (isUserAuthedPx500()) {
			command = new Px500MyPhotosCommand(getActivity());

			commands.add(command);

			command = new PxMyFavPhotosCommand(getActivity());

			commands.add(command);

			command = new PxMyFlowCommand(getActivity());

			commands.add(command);
		} else {
			command = new PxSignInCommand(getActivity());

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

		commands.add(command);

		if (!SPUtil.isFlickrAuthed(ctx)) {
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

			command = new MyPhotosetsCommand(ctx);
			commands.add(command);

			command = new MyGroupsCommand(ctx);
			commands.add(command);

			command = new MyGalleriesCommand(ctx);
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
			if (SPUtil.isFlickrAuthed(getActivity())) {
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
			return SPUtil.getFlickrAuthTokenSecret(getActivity());
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
				signedin = SPUtil.isFlickrAuthed(getActivity());
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
			MessageBus.broadcastMessage(Message.PUBLIC_USER_LOGIN_MSG);
		}
	}

}
