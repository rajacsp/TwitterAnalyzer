package it.wsie.twitteranalyzer.model.tasks.classification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import edu.stanford.nlp.util.RuntimeInterruptedException;
import it.wsie.twitteranalyzer.model.tasks.Task;
import it.wsie.twitteranalyzer.model.tasks.classification.sentiment.SentimentDetector;
import it.wsie.twitteranalyzer.model.tasks.identification.Candidate;
import it.wsie.twitteranalyzer.model.tasks.identification.Message;
import it.wsie.twitteranalyzer.model.tasks.partitioning.Nominator;
import it.wsie.twitteranalyzer.model.tasks.partitioning.PoliticalParticipation;
import net.didion.jwnl.JWNLException;

/**
 * @author Simone Papandrea
 *
 */
public class ClassificationTask extends Task<PoliticalPoll> {

	private static final long serialVersionUID = 571577801583694152L;
	public static final String ID = "Classification";
	private static final String DESCRIPTION = "Supporters, opponents and neutral";
	private transient List<Pair<Nominator, Candidate>> mNominators;

	public ClassificationTask() {

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
	}

	@Override
	public void execute(PoliticalPoll poll) throws Exception {

		classify(poll);
		completed();
	}

	private void classify(PoliticalPoll poll) throws IOException, JWNLException, InterruptedException {

		SentimentDetector detector;
		Map<String, Double> scores;
		Nominator nominator;
		Pattern pattern;
		String text;
		double score;

		pattern = Pattern.compile("((^|\\s+)(RT|rt|MT|mt|FF|ff|RLRT|rlrt|OH|oh)(\\s+|$))|([#|@]\\s*)", Pattern.CASE_INSENSITIVE);
		scores = new HashMap<String, Double>();
		detector = new SentimentDetector();

		for (Pair<Nominator, Candidate> pair : mNominators) {

			nominator = pair.getLeft();

			for (Message message : nominator.getMessages()) {

				try {
					
					text=message.getText();
					text= pattern.matcher(text).replaceAll("");
					score = detector.detect(text);
					
				} catch (RuntimeInterruptedException e) {
					throw new InterruptedException();
				}

				scores.put(message.getID(), score);
			}

			poll.put(pair.getRight(), nominator.getNode().getUser(), scores);
			scores.clear();
			progress();
			cache(pair);
			checkInterruption();
		}

		detector.close();
	}

	@Override
	public PoliticalPoll newObject() {

		return new PoliticalPoll();
	}

}
