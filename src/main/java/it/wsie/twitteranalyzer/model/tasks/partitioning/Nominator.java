package it.wsie.twitteranalyzer.model.tasks.partitioning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.wsie.twitteranalyzer.model.tasks.crawling.Node;
import it.wsie.twitteranalyzer.model.tasks.identification.Message;

/**
 * @author Simone Papandrea
 *
 */
public class Nominator implements Serializable {

	private static final long serialVersionUID = -7266106862397664867L;
	private final Node mNode;
	private final List<Message> mMessages;
	private int mFrequency = 0;
	private double mNormFrequency = 0;
	private double mInfluence = 0;

	public Nominator(Node node) {

		this.mNode = node;
		this.mMessages = new ArrayList<Message>();

	}

	public void addMessage(Message message) {

		if(this.mMessages.contains(message))
			System.nanoTime();
		
		this.mMessages.add(message);
	}

	public Node getNode() {

		return mNode;
	}

	public List<Message> getMessages() {

		return this.mMessages;
	}

	public int getMessagesCount() {

		return this.mMessages.size();
	}

	public float getFrequency() {

		return this.mFrequency;
	}

	public double getInfluence() {

		return this.mInfluence;
	}

	public double getNormFrequency() {

		return this.mNormFrequency;
	}

	public void setFrequency(int frequency) {

		this.mFrequency = frequency;
	}

	public void setNormFrequency(float frequency) {

		this.mNormFrequency = frequency;
	}

	public void setInfluence(double influence) {

		this.mInfluence = influence;
	}

	@Override
	public String toString() {

		return this.getNode().toString();
	}

	@Override
	public int hashCode() {

		return getNode().hashCode();
	}

	@Override
	public boolean equals(Object arg) {

		if (arg instanceof Nominator) {

			Nominator nominator = (Nominator) arg;

			return this.getNode().equals(nominator.getNode());
		}

		return false;
	}
}
