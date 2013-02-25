/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.SPUtil;
import com.gmail.charleszq.picorner.dp.SinglePagePhotosProvider;
import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.offline.FlickrOfflineParameter;
import com.gmail.charleszq.picorner.offline.IOfflineViewParameter;
import com.gmail.charleszq.picorner.offline.OfflineControlFileUtil;
import com.gmail.charleszq.picorner.offline.OfflineHandleService;
import com.gmail.charleszq.picorner.offline.OfflinePhotoCollectionType;
import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.picorner.ui.ImageDetailActivity;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.command.SettingsCommand;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.googlecode.flickrjandroid.photosets.Photoset;

/**
 * Represents the list adapter for my photo sets, each view will have a back
 * view.
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PhotoSetItemAdapter extends PhotoCollectionItemAdapter {

	/**
	 * @param ctx
	 * @param command
	 */
	public PhotoSetItemAdapter(Context ctx, ICommand<?> command) {
		super(ctx, command);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			v = LayoutInflater.from(mContext).inflate(R.layout.main_menu_item,
					null);
		}
		ViewHolder holder = (ViewHolder) v.getTag();
		ViewGroup menuFrontView;
		ImageView avatar;
		TextView text;
		ImageView settingButton;
		if (holder != null) {
			text = holder.text;
			avatar = holder.image;
			settingButton = holder.settingButton;
			menuFrontView = holder.menuFrontView;
		} else {
			avatar = (ImageView) v.findViewById(R.id.nav_item_image);
			text = (TextView) v.findViewById(R.id.nav_item_title);
			settingButton = (ImageView) v
					.findViewById(R.id.btn_offline_settings);
			menuFrontView = (ViewGroup) v
					.findViewById(R.id.main_menu_item_front_view);
			holder = new ViewHolder();
			holder.image = avatar;
			holder.text = text;
			holder.settingButton = settingButton;
			holder.menuFrontView = menuFrontView;
			v.setTag(holder);
		}

		// bind data
		Object data = getItem(position);
		text.setText(getTitle(data));

		final View frontView = menuFrontView;
		final ViewGroup container = (ViewGroup) v;
		// hide the backview if it's there already
		View backView = container.findViewById(R.id.menu_item_back_view);
		if (backView != null) {
			backView.setVisibility(View.INVISIBLE);
			frontView.setVisibility(View.VISIBLE);
			frontView.setAlpha(1f);
		}

		// setting button preparation
		final Photoset ps = (Photoset) getItem(position);
		final IOfflineViewParameter param = new FlickrOfflineParameter(
				OfflinePhotoCollectionType.PHOTO_SET, ps.getId(), ps.getTitle());
		settingButton.setVisibility(View.VISIBLE);
		boolean enabled = OfflineControlFileUtil.isOfflineViewEnabled(mContext,
				param);
		settingButton
				.setImageResource(enabled ? R.drawable.ic_action_settings_holo_light
						: R.drawable.ic_action_settings);
		settingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showBackView(container, frontView, param);
			}
		});
		AbstractFetchIconUrlTask task = (AbstractFetchIconUrlTask) mCommand
				.getAdapter(AbstractFetchIconUrlTask.class);
		if (task != null)
			task.execute(data, avatar);
		return v;
	}

	/**
	 * Shows the back view
	 * 
	 * @param container
	 * @param frontView
	 */
	private void showBackView(ViewGroup container, final View frontView,
			IOfflineViewParameter param) {
		boolean offlineEnabled = SPUtil.isOfflineEnabled(mContext);
		if (!offlineEnabled) {
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
			return;
		}

		View backView = container.findViewById(R.id.menu_item_back_view);
		if (backView == null) {
			backView = LayoutInflater.from(mContext).inflate(
					R.layout.main_menu_item_backview, null);
			container.addView(backView);
			// hook up listeners for the buttons in the back view.
			prepareOfflineActionItem(backView, frontView, param);
		}
		backView.setAlpha(0f); // hide it first
		backView.setVisibility(View.VISIBLE);

		// animation to show the back view.
		ObjectAnimator a1 = ObjectAnimator
				.ofFloat(frontView, "alpha", 1f, 0f).setDuration(500); //$NON-NLS-1$
		ObjectAnimator a2 = ObjectAnimator
				.ofFloat(backView, "alpha", 0f, 1f).setDuration(500); //$NON-NLS-1$
		AnimatorSet set = new AnimatorSet();
		set.playTogether(a1, a2);
		set.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationEnd(Animator animation) {
				frontView.setVisibility(View.INVISIBLE);
			}
		});
		set.start();
	}

	private void prepareOfflineActionItem(final View backView,
			final View frontView, final IOfflineViewParameter offline) {
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
					frontView.setVisibility(View.VISIBLE);
					ObjectAnimator a1 = ObjectAnimator.ofFloat(frontView,
							"alpha", 0f, 1f) //$NON-NLS-1$
							.setDuration(500);
					ObjectAnimator a2 = ObjectAnimator.ofFloat(backView,
							"alpha", 1f, 0f).setDuration(500); //$NON-NLS-1$
					AnimatorSet set = new AnimatorSet();
					set.playTogether(a1, a2);
					set.addListener(new AnimatorListenerAdapter() {

						@Override
						public void onAnimationEnd(Animator animation) {
							backView.setVisibility(View.INVISIBLE);
							frontView.setVisibility(View.VISIBLE);
						}
					});
					set.start();
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
					startSlideshow(mContext, offline);
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
				case R.id.btn_offline_export_photos:
					LayoutInflater factory = LayoutInflater.from(mContext);
					final View textEntryView = factory.inflate(
							R.layout.alert_dialog_text_entry, null);
					final TextView labelPath = (TextView) textEntryView
							.findViewById(R.id.txt_alert_dlg);
					String label = mContext
							.getString(R.string.msg_offline_export_path_label);
					label = String
							.format(label, IConstants.SD_CARD_FOLDER_NAME);
					labelPath.setText(label);
					final EditText editPath = (EditText) textEntryView
							.findViewById(R.id.edit_alert_dlg);
					final CheckBox overwriteCheckBox = (CheckBox) textEntryView
							.findViewById(R.id.cb_offline_export_overwrite);
					editPath.setText(offline.getTitle());
					final AlertDialog dlg = new AlertDialog.Builder(mContext)
							.setTitle(
									R.string.msg_offline_export_path_dlg_title)
							.setView(textEntryView)
							.setPositiveButton(android.R.string.ok,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
										}
									})
							.setNegativeButton(android.R.string.cancel, null)
							.create();
					dlg.setOnShowListener(new OnShowListener() {
						@Override
						public void onShow(DialogInterface dialog) {
							Button btn = dlg
									.getButton(DialogInterface.BUTTON_POSITIVE);
							btn.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									String foldername = editPath.getText()
											.toString();
									boolean overwrite = overwriteCheckBox.isChecked();
									if (foldername == null
											|| foldername.trim().length() == 0) {
										Toast.makeText(
												mContext,
												mContext.getString(R.string.msg_offline_export_empty_path),
												Toast.LENGTH_SHORT).show();
										return;
									}
									//notify the service to export photos
									Intent exportPhotos = new Intent(mContext,
											OfflineHandleService.class);
									exportPhotos
											.putExtra(
													IOfflineViewParameter.OFFLINE_PARAM_INTENT_KEY,
													offline);
									exportPhotos
											.putExtra(
													IOfflineViewParameter.OFFLINE_PARAM_INTENT_ADD_REMOVE_REFRESH_KEY,
													OfflineHandleService.EXPORT_OFFLINE_PHOTO_PARAM);
									exportPhotos.putExtra(IOfflineViewParameter.OFFLINE_EXPORT_FOLDER_NAME_KEY, foldername);
									exportPhotos.putExtra(IOfflineViewParameter.OFFLINE_EXPORT_OVERWRITE_KEY, overwrite);
									mContext.startService(exportPhotos);
									dlg.dismiss();
								}
							});
						}
					});
					dlg.show();
					break;
				}
			}
		};

		TextView btnBack = (TextView) backView
				.findViewById(R.id.btn_offline_back);
		btnBack.setOnClickListener(listener);

		CheckBox btnOffline = (CheckBox) backView
				.findViewById(R.id.btn_enable_offline);
		boolean isOfflineEnabled = OfflineControlFileUtil.isOfflineViewEnabled(
				mContext, offline);
		btnOffline.setChecked(isOfflineEnabled);
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

		TextView btnExportPhoto = (TextView) backView
				.findViewById(R.id.btn_offline_export_photos);
		btnExportPhoto.setOnClickListener(listener);
		btnExportPhoto.setVisibility(isOfflineEnabled ? View.VISIBLE
				: View.GONE);

		TextView btnDeletePhoto = (TextView) backView
				.findViewById(R.id.btn_offline_delete_photos);
		btnDeletePhoto.setOnClickListener(listener);
	}

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

	private static class ViewHolder {
		ViewGroup menuFrontView;
		ImageView image;
		TextView text;
		ImageView settingButton;
	}

}
