package it.wsie.twitteranalyzer.model.tasks.identification;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Simone Papandrea
 *
 */
public class SocialBuzz implements Serializable {

	private static final long serialVersionUID = -130143892628262801L;
	private final Map<User, Set<Mention>> mMentions;
	private int mTweetsCount = 0;

	SocialBuzz() {

		mMentions = new HashMap<User,Set<Mention>>();
	}

	void add(User user, Mention mention) {

		Set<Mention> mentions;

		mentions = mMentions.get(user);

		if (mentions == null) {
			mentions = new HashSet<Mention>();
			mMentions.put(user, mentions);
		}
		
		if(mentions.add(mention))	
			mTweetsCount++;
	}

	public Set<User> getUsers() {

		return mMentions.keySet();
	}

	public Set<Mention> getMentions(User user) {

		return mMentions.get(user);
	}

	public int getMentionsCount(User user) {

		return  getMentions(user).size();
	}
	
	public int getUsersCount() {

		return this.getUsers().size();
	}

	public int getTweetsCount() {

		return this.mTweetsCount;
	}
}
