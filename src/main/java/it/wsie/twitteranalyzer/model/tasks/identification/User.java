package it.wsie.twitteranalyzer.model.tasks.identification;

import java.io.Serializable;

/**
 * @author Simone Papandrea
 *
 */
public class User implements Serializable, Comparable<User> {

	private static final long serialVersionUID = 8104667573979131366L;
	private final String mID;
	private final String mName;
	private final String mScreenName;
	private final String mIcon;
	private final int mFriends;
	
	public User(String id, String name, String screenName, String icon, int friends) {

		this.mID = id;
		this.mName = name;
		this.mScreenName = screenName;
		this.mIcon = icon;
		this.mFriends=friends;
	}

	
	public String getName() {

		return this.mName;
	}

	public String getScreenName() {

		return this.mScreenName;
	}

	public String getID() {

		return this.mID;
	}
	
	public int getFriends(){
		
		return this.mFriends;
	}

	public String getIcon() {

		return mIcon;
	}

	@Override
	public String toString() {

		return this.mName;
	}

	@Override
	public boolean equals(Object arg) {

		if (arg instanceof User) {

			User user = (User) arg;

			return this.getID().equals(user.getID());
		}

		return false;
	}

	@Override
	public int hashCode() {

		return this.getID().hashCode();
	}

	@Override
	public int compareTo(User arg0) {

		int c = this.getName().compareTo(arg0.getName());

		if (c == 0)
			c = this.getID().compareTo(arg0.getID());

		return c;
	}
}
