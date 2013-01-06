/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.px500;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.github.yuyang226.j500px.users.User;
import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.px500.PxUserPhotosService;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.px500.PxFetchUserProfileTask;

/**
 * @author charleszq
 * 
 */
public class Px500MyPhotosCommand extends AbstractPx500PhotoListCommand {

	/**
	 * @param context
	 */
	public Px500MyPhotosCommand(Context context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_px500_you;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mContext.getString(R.string.ig_my_photos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.picorner.ui.command.PhotoListCommand#execute(java
	 * .lang.Object[])
	 */
	@Override
	public boolean execute(Object... params) {
		// first need to check if my 500px user id is saved or not.
		PicornerApplication app = (PicornerApplication) ((Activity) mContext)
				.getApplication();
		Author a = app.getPxUserProfile();
		if (a == null) {
			fetchUserProfile(params);
			return true;
		} else {
			return super.execute(params);
		}
	}

	private void fetchUserProfile(final Object... params) {
		PxFetchUserProfileTask task = new PxFetchUserProfileTask(mContext);
		task.addTaskDoneListener(new IGeneralTaskDoneListener<User>() {

			@Override
			public void onTaskDone(User result) {
				if (result == null) {
					// error
					Toast.makeText(
							mContext,
							mContext.getString(R.string.msg_px_error_fetch_user_profile),
							Toast.LENGTH_SHORT).show();
				} else {
					PicornerApplication app = (PicornerApplication) ((Activity) mContext)
							.getApplication();
					app.savePxUserProfile(String.valueOf(result.getId()),
							result.getUserName(), result.getUserPicUrl());
					//then execute the real command.
					execute(params);
				}
			}
		});
		task.execute();
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			PicornerApplication app = (PicornerApplication) ((Activity) mContext)
					.getApplication();
			Author a = app.getPxUserProfile();
			return new PxUserPhotosService(a.getUserId());
		}
		return super.getAdapter(adapterClass);
	}
}
