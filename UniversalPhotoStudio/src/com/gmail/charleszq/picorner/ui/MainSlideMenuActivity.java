/**
 * 
 */
package com.gmail.charleszq.picorner.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.ui.command.CommandType;
import com.gmail.charleszq.picorner.ui.command.ICommand;
import com.gmail.charleszq.picorner.ui.command.ICommandDoneListener;
import com.gmail.charleszq.picorner.ui.command.px500.PxPopularPhotosCommand;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class MainSlideMenuActivity extends SlidingFragmentActivity {

	private static final Logger logger = LoggerFactory
			.getLogger(MainSlideMenuActivity.class);

	private Fragment mContent;
	private ICommand<MediaObjectCollection> mCommand;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		StringBuilder sb = new StringBuilder();
		sb.append(getString(R.string.app_name));
		sb.append(" - "); //$NON-NLS-1$
		sb.append(getString(R.string.main_photo_grid));
		getActionBar().setTitle( sb.toString() );

		customizeSlideMenu();

		// set the Above View
		boolean retained = true;
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent"); //$NON-NLS-1$
		if (mContent == null) {
			mContent = new PhotoGridFragment();
			retained = false;
		}

		// set the Above View
		setContentView(R.layout.content_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();

		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new MainMenuFragment()).commit();

		// secondary menu
		getSlidingMenu().setSecondaryMenu(R.layout.menu_frame_two);
		getSlidingMenu().setSecondaryShadowDrawable(R.drawable.shadowright);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame_two, new SecondaryMenuFragment()).commit();

		// customize the SlidingMenu
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		if (!retained)
			loadDefaultPhotoList();
		
	}

	/**
	 * When first time this activity starts, load default photo list, now it's
	 * flickr interesting photos.
	 */
	private void loadDefaultPhotoList() {
		mCommand = new PxPopularPhotosCommand(this);
		final ProgressDialog dialog = ProgressDialog.show(this,
				"", getString(R.string.loading_photos)); //$NON-NLS-1$
		dialog.setCancelable(true);
		mCommand.addCommndDoneListener(new ICommandDoneListener<MediaObjectCollection>() {

			@Override
			public void onCommandDone(ICommand<MediaObjectCollection> command,
					MediaObjectCollection t) {
				MainSlideMenuActivity.this.onCommandDone(command, t);
				if (dialog != null && dialog.isShowing()) {
					try {
						dialog.dismiss();
					} catch (Exception ex) {

					}
				}
			}
		});
		mCommand.execute();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
	}

	private void customizeSlideMenu() {
		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBackgroundColor(getResources().getColor(
				R.color.menu_frame_bg_color));
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);

		sm.setMode(SlidingMenu.LEFT_RIGHT);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			getSlidingMenu().showMenu();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent); //$NON-NLS-1$
	}

	void onCommandDone(ICommand<?> command, Object result) {
		CommandType type = command.getCommandType();
		switch (type) {
		case PHOTO_LIST_CMD:
			if (mContent instanceof PhotoGridFragment) {
				((PhotoGridFragment) mContent).populatePhotoList(
						(MediaObjectCollection) result, command);
			} else {
				logger.warn("Not photo grid fragment?"); //$NON-NLS-1$
			}
			break;
		}
	}

	void closeMenu() {
		this.getSlidingMenu().toggle();
	}

}
