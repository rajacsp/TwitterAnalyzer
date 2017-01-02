package it.wsie.twitteranalyzer.model.tasks.classification;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import it.wsie.twitteranalyzer.model.tasks.classification.sentiment.Sentiment;
import it.wsie.twitteranalyzer.model.tasks.identification.Candidate;
import it.wsie.twitteranalyzer.model.tasks.identification.User;

/**
 * @author Simone Papandrea
 *
 */
public class PoliticalPoll implements Iterable<Candidate>, Serializable {

	private static final long serialVersionUID = -3384415569288404793L;
	private final Map<Candidate, Audience> mPoll;
	private final Map<String, Double> mScores;

	PoliticalPoll() {

		mPoll = new HashMap<Candidate, Audience>();
		mScores = new HashMap<String, Double>();
	}

	public Audience getAudience(Candidate candidate) {

		return mPoll.get(candidate);

	}

	public Set<User> getAudience(Candidate candidate, Sentiment sentiment) {

		Set<User> users = null;
		Audience audience;

		audience = mPoll.get(candidate);

		if (audience != null)
			users = audience.get(sentiment);

		return users;
	}

	public int getAudienceSize(Candidate candidate, Sentiment sentiment) {

		int size = 0;
		Audience audience;

		audience = mPoll.get(candidate);

		if (audience != null)
			size = audience.getSize(sentiment);

		return size;
	}

	void put(Candidate candidate, User user, Map<String, Double> scores) {

		Audience audience;

		audience = mPoll.get(candidate);

		if (audience == null) {

			audience = new Audience();
			this.mPoll.put(candidate, audience);
		}

		mScores.putAll(scores);

		audience.put(user, getSentiment(scores.values()));
	}

	public double getMessageScore(String messageID) {

		return mScores.get(messageID);
	}

	@Override
	public Iterator<Candidate> iterator() {

		return this.mPoll.keySet().iterator();
	}

	private Sentiment getSentiment(Collection<Double> scores) {

		Sentiment sentiment;
		int objective;
		int supporter = 0, opponent = 0;// neutral=0;

		for (Double score : scores) {

			if (score > 0)
				supporter++;
			else if (score < 0)
				opponent++;
			// else neutral++;
		}

		objective = supporter - opponent;

		if (objective == 0)
			sentiment = Sentiment.NEUTRAL;
		else if (objective > 0)
			sentiment = Sentiment.SUPPORTER;
		else
			sentiment = Sentiment.OPPONENT;

		return sentiment;
	}
}
