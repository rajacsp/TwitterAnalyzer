package it.wsie.twitteranalyzer.model.tasks.prediction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import it.wsie.twitteranalyzer.model.tasks.Task;
import it.wsie.twitteranalyzer.model.tasks.classification.PoliticalPoll;
import it.wsie.twitteranalyzer.model.tasks.classification.sentiment.Sentiment;
import it.wsie.twitteranalyzer.model.tasks.identification.Candidate;
import it.wsie.twitteranalyzer.model.tasks.identification.Message;
import it.wsie.twitteranalyzer.model.tasks.identification.Party;
import it.wsie.twitteranalyzer.model.tasks.partitioning.Nominator;
import it.wsie.twitteranalyzer.model.tasks.partitioning.PoliticalParticipation;

/**
 * @author Simone Papandrea
 *
 */
public class PredictionTask extends Task<PoliticsDataCenter> {

	private static final long serialVersionUID = -6195026081676253476L;
	public static final String ID = "Prediction";
	private static final String DESCRIPTION = "Election forecast";
	private transient PoliticalPoll mPoll;
	private transient List<Pair<Nominator, Candidate>> mNominators;

	public PredictionTask() {

		super(ID, DESCRIPTION);
	}

	@Override
	public void setArgs(Object... args) {

		PoliticalParticipation partition;
		Pair<Nominator, Candidate> pair;

		partition = (PoliticalParticipation) args[0];
		setPayload(partition.getNominatorsCount());
		mNominators = new ArrayList<Pair<Nominator, Candidate>>();

		for (Candidate candidate : partition) {

			for (Nominator nominator : partition.getNominators(candidate)) {

				pair = new ImmutablePair<Nominator, Candidate>(nominator, candidate);

				if (!checkCache(pair))
					mNominators.add(pair);
			}
		}

		mPoll = (PoliticalPoll) args[1];
	}

	@Override
	public void execute(PoliticsDataCenter pdc) throws Exception {

		formTemporalSeries(pdc);
		formPreferences(pdc);
		completed();
	}

	private void formTemporalSeries(PoliticsDataCenter pdc) throws InterruptedException {

		Nominator nominator;
		Calendar calendar;
		Candidate candidate;
		long timestamp;

		calendar = Calendar.getInstance();

		for (Pair<Nominator, Candidate> pair : mNominators) {

			nominator = pair.getLeft();
			candidate = pair.getRight();

			for (Message message : nominator.getMessages()) {

				calendar.setTimeInMillis(message.getTimestamp());
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				timestamp = calendar.getTimeInMillis();
				pdc.addMentionPoint(candidate, timestamp);
				pdc.addTrendPoint(candidate, timestamp, mPoll.getMessageScore(message.getID()));
			}

			progress();
			cache(pair);
			checkInterruption();
		}
	}

	private void formPreferences(PoliticsDataCenter pdc) {

		Map<Candidate, Integer> candidatesPreferences;
		Map<Party, Integer> partyPreferences;
		Party party;
		int count;

		candidatesPreferences = new HashMap<Candidate, Integer>();
		partyPreferences = new HashMap<Party, Integer>();

		for (Party p : Party.values())
			partyPreferences.put(p, 0);

		for (Candidate candidate : mPoll) {

			party = candidate.getParty();
			count = mPoll.getAudienceSize(candidate, Sentiment.SUPPORTER)
					- mPoll.getAudienceSize(candidate, Sentiment.OPPONENT);
			candidatesPreferences.put(candidate, count);
			partyPreferences.put(party, partyPreferences.get(party) + count);
		}

		pdc.setCandidatePreferences(candidatesPreferences);
		pdc.setPartyPreferences(partyPreferences);
	}

	@Override
	public PoliticsDataCenter newObject() {

		return new PoliticsDataCenter();
	}

}
