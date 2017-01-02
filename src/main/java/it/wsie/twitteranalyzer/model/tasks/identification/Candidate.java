package it.wsie.twitteranalyzer.model.tasks.identification;

import java.io.Serializable;

/**
 * @author Simone Papandrea
 *
 */
public class Candidate implements CharSequence, Serializable {

	private static final long serialVersionUID = -6796473018672945532L;
	private final String mName;
	private final Party mParty;

	public Candidate(String name) {

		this(name, null);
	}

	public Candidate(String name, Party party) {

		this.mName = name;
		this.mParty = party;
	}

	public String getName() {

		return this.mName;
	}

	@Override
	public String toString() {

		return getName();
	}

	public Party getParty() {

		return this.mParty;
	}

	@Override
	public char charAt(int arg0) {

		return this.getName().charAt(arg0);
	}

	@Override
	public int length() {

		return this.getName().length();
	}

	@Override
	public CharSequence subSequence(int arg0, int arg1) {

		return this.getName().subSequence(arg0, arg1);
	}

	@Override
	public boolean equals(Object arg) {

		if (arg instanceof Candidate) {

			Candidate candidate = (Candidate) arg;

			return this.getName().equals(candidate.getName());
		}

		return false;
	}

	@Override
	public int hashCode() {

		return this.getName().hashCode();
	}
}
