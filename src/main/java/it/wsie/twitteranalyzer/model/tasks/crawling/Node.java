package it.wsie.twitteranalyzer.model.tasks.crawling;

import java.io.Serializable;
import it.wsie.twitteranalyzer.model.tasks.identification.User;

/**
 * @author Simone Papandrea
 *
 */
public class Node implements Serializable {

	private static final long serialVersionUID = 8104667573979131366L;
	private final User mUser;
	private double mCentrality = 0;
	private double mNormCentrality = 0;
	private double mRank = 0;

	public Node(User user) {

		this.mUser = user;

	}

	public User getUser() {

		return this.mUser;
	}

	public void setRank(double rank) {

		this.mRank = rank;
	}

	public void setCentrality(double centrality) {

		this.mCentrality = centrality;
	}

	public void setNormCentrality(double normCentrality) {

		this.mNormCentrality = normCentrality;
	}

	public double getRank() {

		return this.mRank;
	}

	public double getCentrality() {

		return this.mCentrality;
	}

	public double getNormCentrality() {

		return this.mNormCentrality;
	}

	@Override
	public int hashCode() {

		return getUser().hashCode();
	}

	@Override
	public boolean equals(Object arg) {

		if (arg instanceof Node) {

			Node node = (Node) arg;

			return this.getUser().equals(node.getUser());
		}

		return false;
	}

	@Override
	public String toString() {

		return getUser().toString();
	}

}
