package it.wsie.twitteranalyzer.model.tasks.identification;

import java.io.Serializable;

/**
 * @author Simone Papandrea
 *
 */
public class Message implements Serializable {

	private static final long serialVersionUID = -6184872996305013962L;
	private final String mID;
	private final long mTimestamp;
	private String mText;

	public Message(String id, String text, long timestamp) {

		this.mID = id;
		this.mText = text;
		this.mTimestamp = timestamp;
	}

	public String getID() {

		return this.mID;
	}

	public String getText() {

		return this.mText;
	}

	public long getTimestamp() {

		return this.mTimestamp;
	}

	void setText(String text) {

		this.mText = text;
	}

	@Override
	public String toString() {

		return this.getText();
	}

	@Override
	public boolean equals(Object arg) {

		if (arg instanceof Message) {

			Message message = (Message) arg;

			return this.getID().equals(message.getID());
		}

		return false;
	}

	@Override
	public int hashCode() {

		return this.getID().hashCode();
	}
}
