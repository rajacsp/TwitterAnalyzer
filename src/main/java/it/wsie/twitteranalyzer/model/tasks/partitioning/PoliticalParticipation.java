package it.wsie.twitteranalyzer.model.tasks.partitioning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.wsie.twitteranalyzer.model.tasks.crawling.Node;
import it.wsie.twitteranalyzer.model.tasks.identification.Candidate;
import it.wsie.twitteranalyzer.model.tasks.identification.Message;

/**
 * @author Simone Papandrea
 *
 */
public class PoliticalParticipation implements Serializable, Iterable<Candidate> {

	private static final long serialVersionUID = 8244407514768783071L;
	private Map<Candidate, List<Nominator>> mNominatorsMap;
	private int mMessagesCount = 0;
	private int mNominatorsCount = 0;

	PoliticalParticipation() {

		mNominatorsMap = new HashMap<Candidate, List<Nominator>>();
	}

	void add(Node node, Message message, Candidate candidate) {

		List<Nominator> nominators;
		Nominator nominator;
		int index;

		nominators = mNominatorsMap.get(candidate);

		if (nominators == null) {
			nominators = new ArrayList<Nominator>();
			mNominatorsMap.put(candidate, nominators);
		}
		
		nominator = new Nominator(node);
		index = nominators.indexOf(nominator);

		if (index > -1)
			nominator = nominators.get(index);
		else {
			nominators.add(nominator);
			mNominatorsCount++;
		}

		nominator.addMessage(message);
		mMessagesCount++;
	}

	public int getMessagesCount() {

		return mMessagesCount;
	}

	public int getNominatorsCount() {

		return mNominatorsCount;
	}

	public int getCandidatesCount() {

		return mNominatorsMap.keySet().size();
	}
	
	public List<Nominator> getNominators(Candidate candidate) {

		return mNominatorsMap.get(candidate);
	}

	public List<Nominator> getNominators() {

		List<Nominator> nominators;

		nominators = new ArrayList<Nominator>();

		for (List<Nominator> l : mNominatorsMap.values())
			nominators.addAll(l);

		return nominators;
	}

	@Override
	public Iterator<Candidate> iterator() {

		return mNominatorsMap.keySet().iterator();
	}

}
