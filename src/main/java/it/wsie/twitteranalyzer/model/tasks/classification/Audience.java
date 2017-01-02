package it.wsie.twitteranalyzer.model.tasks.classification;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.wsie.twitteranalyzer.model.tasks.classification.sentiment.Sentiment;
import it.wsie.twitteranalyzer.model.tasks.identification.User;

/**
 * @author Simone Papandrea
 *
 */
public class Audience implements Serializable {

	private static final long serialVersionUID = -9012262558333113284L;
	private final Map<Sentiment, Set<User>> mUsers;
	private int mSize = 0;

	Audience() {

		mUsers = new HashMap<Sentiment, Set<User>>();
	}

	public Set<User> get(Sentiment sentiment) {

		return mUsers.get(sentiment);
	}

	public int getSize(Sentiment sentiment) {

		return mUsers.containsKey(sentiment) ? mUsers.get(sentiment).size() : 0;
	}

	public int getSize() {

		return mSize;
	}

	void put(User user, Sentiment sentiment) {

		Set<User> users;

		users = get(sentiment);

		if (users == null) {

			users = new HashSet<User>();
			this.mUsers.put(sentiment, users);
		}

		if (users.add(user))
			mSize++;
	}
}
