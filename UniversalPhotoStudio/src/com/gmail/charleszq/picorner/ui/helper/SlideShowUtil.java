/**
 * 
 */
package com.gmail.charleszq.picorner.ui.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.utils.IConstants;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public final class SlideShowUtil {

	public static void showSlideshowDialog(final Context ctx,
			final DialogInterface.OnClickListener listener) {
		final SharedPreferences sp = ctx.getSharedPreferences(
				IConstants.DEF_PREF_NAME, Context.MODE_APPEND);
		// show the dialog
		View dialogView = LayoutInflater.from(ctx).inflate(
				R.layout.slideshow_setting_dlg, null);
		final CheckBox checkDonotShow = (CheckBox) dialogView
				.findViewById(R.id.cb_not_show_slideshow_dlg);
		final SeekBar seekbar = (SeekBar) dialogView
				.findViewById(R.id.seekBar1);
		int interval = Integer.valueOf(sp.getString(
				IConstants.PREF_SLIDE_SHOW_INTERVAL,
				IConstants.DEF_SLIDE_SHOW_INTERVAL));
		seekbar.setProgress(interval / 1000);
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (progress < 5) {
					seekBar.setProgress(5);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		DialogInterface.OnClickListener internalListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_NEGATIVE) {
					dialog.cancel();
				} else {
					if (checkDonotShow.isChecked()) {
						// save settings
						Editor editor = sp.edit();
						editor.putBoolean(
								IConstants.PREF_DONT_SHOW_SLIDE_SHOW_DLG, false);
						editor.putString(IConstants.PREF_SLIDE_SHOW_INTERVAL,
								Integer.toString(seekbar.getProgress() * 1000));
						editor.commit();
					}
					listener.onClick(dialog, which);
				}
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(android.R.string.dialog_alert_title);
		builder.setView(dialogView);
		builder.setPositiveButton(android.R.string.ok, internalListener);
		builder.setNegativeButton(android.R.string.cancel, internalListener);
		builder.create().show();
	}
}
