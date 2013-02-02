/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

import java.util.List;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.dp.SinglePagePhotosProvider;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.offline.IOfflineViewParameter;
import com.gmail.charleszq.picorner.offline.OfflineControlFileUtil;
import com.gmail.charleszq.picorner.offline.OfflineHandleService;
import com.gmail.charleszq.picorner.ui.ImageDetailActivity;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.command.SettingsCommand;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class MainMenuCommandSectionListAdapter extends
		AbstractCommandSectionListAdapter {

	public MainMenuCommandSectionListAdapter(Context ctx, ImageLoader fetcher,
			boolean showHeaderMarker) {
		super(ctx, fetcher, showHeaderMarker);
	}

	public MainMenuCommandSectionListAdapter(Context ctx, ImageLoader fetcher) {
		super(ctx, fetcher);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		ICommand<?> command = (ICommand<?>) getItem(position);
		prepareBackView(view, command);
		return view;
	}

	private void prepareBackView(final View view, final ICommand<?> command) {
		final IOfflineViewParameter offline = (IOfflineViewParameter) command
				.getAdapter(IOfflineViewParameter.class);
		if (offline != null) {
			// prepare the back view
			View bv = view.findViewById(R.id.menu_item_back_view);
			if (bv == null) {
				bv = LayoutInflater.from(mContext).inflate(
						R.layout.main_menu_item_backview, null);
				((ViewGroup) view).addView(bv);
			}
			final View backView = bv;
			backView.setVisibility(View.INVISIBLE);

			// get the front view
			final View frontView = view
					.findViewById(R.id.main_menu_item_front_view);

			// hook up action item with click listener.
			prepareOfflineActionItem(backView, frontView, command, offline);

			// hook the action on the setting icon
			ImageView settingButton = (ImageView) view
					.findViewById(R.id.btn_offline_settings);
			boolean isOfflineEnabledOnThis = OfflineControlFileUtil
					.isOfflineViewEnabled(mContext, offline);
			settingButton
					.setImageResource(isOfflineEnabledOnThis ? R.drawable.ic_action_settings_holo_light
							: R.drawable.ic_action_settings);
			settingButton.setVisibility(View.VISIBLE);
			settingButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showBackView(backView, frontView);
				}
			});
		}
	}

	/**
	 * Shows the back view, but first we need to check if user enables the
	 * offline feature or not.
	 * 
	 * @param backView
	 * @param frontView
	 */
	private void showBackView(View backView, View frontView) {
		PicornerApplication app = (PicornerApplication) ((Activity) mContext)
				.getApplication();
		if (!app.isOfflineEnabled()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(android.R.string.dialog_alert_title).setMessage(
					R.string.msg_pls_enable_offline_first);
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == DialogInterface.BUTTON_POSITIVE) {
						SettingsCommand cmd = new SettingsCommand(mContext);
						cmd.execute();
					} else {
						dialog.cancel();
					}
				}
			};
			builder.setPositiveButton(android.R.string.ok, listener);
			builder.setNegativeButton(android.R.string.cancel, listener);
			AlertDialog dialog = builder.create();
			dialog.show();
		} else {
			frontView.setVisibility(View.INVISIBLE);
			backView.setVisibility(View.VISIBLE);
			ObjectAnimator.ofFloat(backView, "alpha", 0f, 1f) //$NON-NLS-1$
					.setDuration(1000).start();
		}
	}

	private void prepareOfflineActionItem(final View backView,
			final View frontView, ICommand<?> command,
			final IOfflineViewParameter offline) {
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				final boolean isOfflineEnabled = OfflineControlFileUtil
						.isOfflineViewEnabled(mContext, offline);
				switch (v.getId()) {
				case R.id.btn_offline_back:
					backView.setVisibility(View.INVISIBLE);
					frontView.setVisibility(View.VISIBLE);
					ImageView settingButtonImage = (ImageView) frontView
							.findViewById(R.id.btn_offline_settings);
					settingButtonImage
							.setImageResource(isOfflineEnabled ? R.drawable.ic_action_settings_holo_light
									: R.drawable.ic_action_settings);
					ObjectAnimator.ofFloat(frontView, "alpha", 0f, 1f) //$NON-NLS-1$
							.setDuration(1000).start();
					break;
				case R.id.btn_offline_refresh:
					if (!isOfflineEnabled) {
						Toast.makeText(
								mContext,
								mContext.getString(R.string.msg_pls_enable_offline_for_ps),
								Toast.LENGTH_SHORT).show();
						break;
					}
					Intent serviceIntent = new Intent(mContext,
							OfflineHandleService.class);
					serviceIntent.putExtra(
							IOfflineViewParameter.OFFLINE_PARAM_INTENT_KEY,
							offline);
					serviceIntent
							.putExtra(
									IOfflineViewParameter.OFFLINE_PARAM_INTENT_ADD_REMOVE_REFRESH_KEY,
									OfflineHandleService.REFRESH_OFFLINE_PARAM);
					mContext.startService(serviceIntent);
					break;
				case R.id.btn_offline_slide_show:
					final Context ctx = backView.getContext();
					startSlideshow(ctx, offline);
					break;
				case R.id.btn_offline_download:
					if (!isOfflineEnabled) {
						Toast.makeText(
								mContext,
								mContext.getString(R.string.msg_pls_enable_offline_for_ps),
								Toast.LENGTH_SHORT).show();
						break;
					}
					Intent download = new Intent(mContext,
							OfflineHandleService.class);
					download.putExtra(
							IOfflineViewParameter.OFFLINE_PARAM_INTENT_KEY,
							offline);
					download.putExtra(
							IOfflineViewParameter.OFFLINE_PARAM_INTENT_ADD_REMOVE_REFRESH_KEY,
							OfflineHandleService.DOWNLOAD_OFFLINE_PARAM);
					mContext.startService(download);
					break;
				case R.id.btn_offline_delete_photos:
					AlertDialog.Builder builder = new AlertDialog.Builder(
							mContext).setTitle(
							android.R.string.dialog_alert_title).setMessage(
							R.string.msg_offline_delete_photo_dialog_msg);
					DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == DialogInterface.BUTTON_POSITIVE) {
								Intent deletePhotos = new Intent(mContext,
										OfflineHandleService.class);
								deletePhotos
										.putExtra(
												IOfflineViewParameter.OFFLINE_PARAM_INTENT_KEY,
												offline);
								deletePhotos
										.putExtra(
												IOfflineViewParameter.OFFLINE_PARAM_INTENT_ADD_REMOVE_REFRESH_KEY,
												OfflineHandleService.DELETE_OFFLINE_PHOTO_PARAM);
								mContext.startService(deletePhotos);
							} else {
								dialog.cancel();
							}
						}
					};
					builder.setPositiveButton(android.R.string.ok, listener);
					builder.setNegativeButton(android.R.string.cancel, listener);
					builder.create().show();
					break;
				}
			}
		};

		TextView btnBack = (TextView) backView
				.findViewById(R.id.btn_offline_back);
		btnBack.setOnClickListener(listener);

		CheckBox btnOffline = (CheckBox) backView
				.findViewById(R.id.btn_enable_offline);
		btnOffline.setChecked(OfflineControlFileUtil.isOfflineViewEnabled(
				mContext, offline));
		btnOffline.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Intent serviceIntent = new Intent(mContext,
						OfflineHandleService.class);
				serviceIntent
						.putExtra(
								IOfflineViewParameter.OFFLINE_PARAM_INTENT_KEY,
								offline);
				serviceIntent
						.putExtra(
								IOfflineViewParameter.OFFLINE_PARAM_INTENT_ADD_REMOVE_REFRESH_KEY,
								isChecked ? OfflineHandleService.ADD_OFFLINE_PARAM
										: OfflineHandleService.REMOVE_OFFLINE_PARAM);
				mContext.startService(serviceIntent);
			}
		});

		TextView btnRefresh = (TextView) backView
				.findViewById(R.id.btn_offline_refresh);
		btnRefresh.setOnClickListener(listener);

		TextView btnSlideShow = (TextView) backView
				.findViewById(R.id.btn_offline_slide_show);
		btnSlideShow.setOnClickListener(listener);
		btnSlideShow
				.setVisibility(OfflineControlFileUtil
						.isOfflineControlFileReady(backView.getContext(),
								offline) ? View.VISIBLE : View.GONE);

		TextView btnDownload = (TextView) backView
				.findViewById(R.id.btn_offline_download);
		btnDownload.setOnClickListener(listener);

		TextView btnDeletePhoto = (TextView) backView
				.findViewById(R.id.btn_offline_delete_photos);
		btnDeletePhoto.setOnClickListener(listener);
	}

	/**
	 * Starts the slideshow.
	 * 
	 * @param ctx
	 * @param offline
	 */
	private void startSlideshow(final Context ctx,
			final IOfflineViewParameter offline) {
		final boolean isOfflineViewEnabled = OfflineControlFileUtil
				.isOfflineViewEnabled(ctx, offline);
		new Handler().post(new Runnable() {

			@Override
			public void run() {
				Intent slideshow = new Intent(ctx, ImageDetailActivity.class);
				slideshow.putExtra(ImageDetailActivity.OFFLINE_COMMAND_KEY,
						Boolean.toString(isOfflineViewEnabled));
				slideshow.putExtra(ImageDetailActivity.SHOW_ACTION_BAR_KEY,
						false);
				slideshow.putExtra(ImageDetailActivity.LARGE_IMAGE_POSITION, 0);
				slideshow.putExtra(ImageDetailActivity.SLIDE_SHOW_KEY, true);
				List<MediaObject> photos = offline
						.getPhotoCollectionProcessor().getCachedPhotos(ctx,
								offline);
				MediaObjectCollection col = new MediaObjectCollection();
				for (MediaObject photo : photos) {
					col.addPhoto(photo);
				}
				SinglePagePhotosProvider dp = new SinglePagePhotosProvider(col);
				slideshow.putExtra(ImageDetailActivity.DP_KEY, dp);
				ctx.startActivity(slideshow);
			}
		});
	}
}
