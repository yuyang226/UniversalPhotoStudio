/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.px500;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.task.px500.Px500OAuthTask;
import com.gmail.charleszq.picorner.ui.command.AbstractCommand;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PxSignInCommand extends AbstractCommand<Object> {

	public PxSignInCommand(Context context) {
		super(context);
	}

	@Override
	public boolean execute(Object... params) {
		Px500OAuthTask task = new Px500OAuthTask(mContext);
		task.addTaskDoneListener(new IGeneralTaskDoneListener<String>() {
			@Override
			public void onTaskDone(String result) {
				if (result == null) {
					// error
					Toast.makeText(mContext,
							mContext.getString(R.string.msg_px500_auth_fail),
							Toast.LENGTH_SHORT).show();
				} else {
					mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse(result)));
				}
			}
		});
		task.execute();
		return true;
	}

	@Override
	public int getIconResourceId() {
		return R.drawable.ic_menu_login;
	}

	@Override
	public String getLabel() {
		return mContext.getString(R.string.f_login);
	}

}
