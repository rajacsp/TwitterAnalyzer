package it.wsie.twitteranalyzer.model.tasks.identification;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Simone Papandrea
 *
 */
public class Mention implements Serializable {

	private static final long serialVersionUID = -169075175627787222L;
	private Message mMessage;
	private Set<Candidate> mCandidates;

	Mention(Message message) {

		this.mMessage = message;
		this.mCandidates = new LinkedHashSet<Candidate>();
	}

	public Message getMessage() {

		return this.mMessage;
	}

	public Set<Candidate> getCandidates() {

		return this.mCandidates;
	}

	void addCandidate(Candidate candidate) {

		this.mCandidates.add(candidate);
	}

	boolean isEmpty() {

		return this.mCandidates.isEmpty();
	}

	@Override
	public boolean equals(Object arg) {

		if (arg instanceof Mention) {

			Mention mention = (Mention) arg;

			return this.getMessage().equals(mention.getMessage());
		}

		return false;
	}

	@Override
	public int hashCode() {

		return this.getMessage().hashCode();
	}
}
