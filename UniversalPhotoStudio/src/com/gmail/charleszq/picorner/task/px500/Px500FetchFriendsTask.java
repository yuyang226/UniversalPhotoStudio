/**
 * 
 */
package com.gmail.charleszq.picorner.task.px500;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.github.yuyang226.j500px.J500px;
import com.github.yuyang226.j500px.users.User;
import com.github.yuyang226.j500px.users.UserList;
import com.github.yuyang226.j500px.users.UsersInterface;
import com.gmail.charleszq.picorner.BuildConfig;
import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.gmail.charleszq.picorner.utils.J500pxHelper;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class Px500FetchFriendsTask extends
		AbstractContextAwareTask<Void, Integer, List<Author>> {

	public Px500FetchFriendsTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected List<Author> doInBackground(Void... params) {
		PicornerApplication app = (PicornerApplication) ((Activity) mContext)
				.getApplication();
		Author me = app.getPxUserProfile();
		J500px px = J500pxHelper.getJ500pxAuthedInstance(mContext);
		UsersInterface ui = px.getUsersInterface();
		List<Author> friends = new ArrayList<Author>();
		int page = 1;
		try {
			UserList ul = ui.getUserFriends(Integer.valueOf(me.getUserId()),
					page, IConstants.DEF_500PX_PAGE_SIZE);
			while (ul != null && !ul.isEmpty()) {
				for (User u : ul) {
					Author a = new Author();
					a.setUserId(Integer.toString(u.getId()));
					a.setUserName(u.getUserName());
					a.setBuddyIconUrl(u.getUserPicUrl());
					friends.add(a);
				}
				page++;
				ul = ui.getUserFriends(Integer.valueOf(me.getUserId()), page,
						IConstants.DEF_500PX_PAGE_SIZE);
			}
		} catch (Exception e) {
			if (BuildConfig.DEBUG) {
				Log.w(TAG, "unable to get friend list: " + e.getMessage()); //$NON-NLS-1$
			}
		}
		return friends;
	}

}
