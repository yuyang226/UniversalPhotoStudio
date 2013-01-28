/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.msg.IMessageConsumer;
import com.gmail.charleszq.picorner.msg.Message;
import com.gmail.charleszq.picorner.msg.MessageBus;
import com.gmail.charleszq.picorner.ui.command.AboutCommand;
import com.gmail.charleszq.picorner.ui.command.HelpCommand;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.command.ICommandDoneListener;
import com.gmail.charleszq.picorner.ui.command.MenuSectionHeaderCommand;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;
import com.gmail.charleszq.picorner.ui.command.SettingsCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.FlickrFriendPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.flickr.FlickrTagSearchCommand;
import com.gmail.charleszq.picorner.ui.command.ig.InstagramFollowingPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.ig.InstagramSearchNearPhotosCommand;
import com.gmail.charleszq.picorner.ui.command.px500.Px500FriendPhotosCommand;
import com.gmail.charleszq.picorner.ui.helper.AbstractCommandSectionListAdapter;
import com.gmail.charleszq.picorner.ui.helper.IHiddenView;
import com.gmail.charleszq.picorner.ui.helper.IHiddenView.IHiddenViewActionListener;

/**
 * Represents the fragment to show the secondary menus.
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class SecondaryMenuFragment extends AbstractFragmentWithImageFetcher
		implements OnItemClickListener, IMessageConsumer {

	private ListView							mListView;
	private FrameLayout							mBackViewContainer;
	private AbstractCommandSectionListAdapter	mSectionAdapter;

	private boolean								mHideAnimation			= true;

	private ProgressDialog						mProgressDialog;

	/**
	 * The listener to cancel the hidden view.
	 */
	private IHiddenViewActionListener			mHideViewCancelListener	= new IHiddenViewActionListener() {

																			@SuppressWarnings("unchecked")
																			@Override
																			public void onAction(
																					int action,
																					ICommand<?> command,
																					IHiddenView view,
																					Object... data) {
																				switch (action) {
																				case IHiddenView.ACTION_CANCEL:
																					// animation
																					// to
																					// show
																					// the
																					// list
																					mListView
																							.animate()
																							.setDuration(
																									500)
																							.rotationY(
																									-270f)
																							.rotationY(
																									0f);
																					break;
																				case IHiddenView.ACTION_DO:
																					doCommand(
																							(ICommand<Object>) command,
																							data);
																					mListView
																							.animate()
																							.setDuration(
																									500)
																							.rotationY(
																									-270f)
																							.rotationY(
																									0f);
																					break;
																				case IHiddenView.ACTION_JUST_CMD:
																					doCommand(
																							(ICommand<Object>) command,
																							data);
																					break;
																				}
																			}

																		};

	private ICommandDoneListener<Object>		mCommandDoneListener	= new ICommandDoneListener<Object>() {

																			@Override
																			public void onCommandDone(
																					ICommand<Object> command,
																					Object t) {
																				MainSlideMenuActivity act = (MainSlideMenuActivity) SecondaryMenuFragment.this
																						.getActivity();
																				if (act == null) {
																					// when
																					// configuration
																					// changed,
																					// the
																					// activity
																					// of
																					// this
																					// fragement
																					// might
																					// be
																					// null,
																					// then
																					// try
																					// to
																					// get
																					// it
																					// from
																					// the
																					// command.
																					Context ctx = (Context) command
																							.getAdapter(Context.class);
																					if (ctx != null
																							&& ctx instanceof MainSlideMenuActivity) {
																						act = (MainSlideMenuActivity) ctx;
																					}
																				}
																				if (act != null)
																					act.onCommandDone(
																							command,
																							t);

																				if (mProgressDialog != null
																						&& mProgressDialog
																								.isShowing()) {
																					try {
																						mProgressDialog
																								.cancel();
																					} catch (Exception e) {
																						// do
																						// nothing,
																						// when
																						// configuration
																						// changes,
																						// this
																						// might
																						// happend,
																						// but
																						// no
																						// harm.
																					}
																				}
																			}
																		};

	/**
	 * 
	 */
	public SecondaryMenuFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.secondary_menu, null);
		mListView = (ListView) v.findViewById(R.id.secondary_menu_list);
		mBackViewContainer = (FrameLayout) v
				.findViewById(R.id.back_view_container);

		mSectionAdapter = new AbstractCommandSectionListAdapter(getActivity(),
				mImageFetcher, false) {

			@Override
			public boolean isEnabled(int position) {
				return getItemViewType(position) == AbstractCommandSectionListAdapter.ITEM_COMMAND;
			}
		};
		mListView.setAdapter(mSectionAdapter);
		mListView.setOnItemClickListener(this);
		return v;
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		prepareMenuItems();
	}

	private void prepareMenuItems() {
		PicornerApplication app = (PicornerApplication) getActivity()
				.getApplication();

		mSectionAdapter.clearSections();
		List<ICommand<?>> commands = new ArrayList<ICommand<?>>();

		// general
		ICommand<?> command = new MenuSectionHeaderCommand(getActivity(),
				getString(R.string.secondary_menus_general));
		commands.add(command);

		command = new SettingsCommand(getActivity());
		commands.add(command);

		// search
		command = new MenuSectionHeaderCommand(getActivity(),
				getString(R.string.secondary_menus_search));
		commands.add(command);

		command = new InstagramSearchNearPhotosCommand(getActivity());
		commands.add(command);
		command = new FlickrTagSearchCommand(getActivity());
		commands.add(command);
		
		int index = commands.size();
		boolean accountReady = false;
		if( app.getPxUserProfile() != null ) {
			command = new Px500FriendPhotosCommand(getActivity());
			commands.add(command);
			accountReady = true;
		}
		
		if (app.getFlickrToken() != null) {
			// friends
			command = new FlickrFriendPhotosCommand(getActivity());
			commands.add(command);
			accountReady = true;
		}
		
		if( app.getInstagramUserId() != null ) {
			command = new InstagramFollowingPhotosCommand(getActivity());
			commands.add(command);
			accountReady = true;
		}
		
		if( accountReady ) {
			command = new MenuSectionHeaderCommand(getActivity(),
					getString(R.string.menu_header_friends));
			commands.add(index, command);
		}

		// help & about
		command = new MenuSectionHeaderCommand(getActivity(),
				getString(R.string.cmd_help_label));
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
		@SuppressWarnings("unchecked")
		ICommand<Object> command = (ICommand<Object>) parent.getAdapter()
				.getItem(position);
		Context ctx = (Context) command.getAdapter(Context.class);

		IHiddenView hiddenView = (IHiddenView) command
				.getAdapter(IHiddenView.class);
		if (hiddenView == null) {
			doCommand(command);
		} else {
			hiddenView.init(command, mHideViewCancelListener);
			FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
					Gravity.CENTER_VERTICAL);
			View hv = hiddenView.getView(ctx);
			hv.setLayoutParams(param);
			hv.setVisibility(View.INVISIBLE);
			mBackViewContainer.addView(hv);
			showHiddenView(hv);
		}
	}

	private void doCommand(ICommand<Object> command, Object... params) {
		if (PhotoListCommand.class.isInstance(command)) {
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

	/**
	 * Animate the view to show the hidden view
	 * 
	 * @param view
	 */
	private void showHiddenView(final View view) {
		mHideAnimation = true;
		mListView.animate().setListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				if (!mHideAnimation) {
					view.setVisibility(View.INVISIBLE);
					mListView.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mListView.setVisibility(mHideAnimation ? View.INVISIBLE
						: View.VISIBLE);
				if (mHideAnimation) {
					view.setAlpha(0f);
					view.setVisibility(View.VISIBLE);
					ObjectAnimator
							.ofFloat(view, "alpha", 0f, 1f).setDuration(2000).start(); //$NON-NLS-1$
				} else {
					SecondaryMenuFragment.this.mBackViewContainer
							.removeView(view);
				}
				mHideAnimation = !mHideAnimation;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		}).setDuration(500).rotationY(90f).rotationY(180f);
	}

	@Override
	public boolean consumeMessage(Message msg) {
		if( msg.getMessageType() == Message.USER_LOGIN_IN ) {
			prepareMenuItems();
		}
		//return false, so other UI might also be interested in this message.
		return false;
	}

	/* (non-Javadoc)
	 * @see com.gmail.charleszq.picorner.ui.AbstractFragmentWithImageFetcher#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MessageBus.addConsumer(this);
	}

	/* (non-Javadoc)
	 * @see com.gmail.charleszq.picorner.ui.AbstractFragmentWithImageFetcher#onDestroy()
	 */
	@Override
	public void onDestroy() {
		MessageBus.removeConsumer(this);
		super.onDestroy();
	}
	
	
}
