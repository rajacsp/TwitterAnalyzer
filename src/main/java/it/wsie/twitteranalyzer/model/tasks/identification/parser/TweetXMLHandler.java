package it.wsie.twitteranalyzer.model.tasks.identification.parser;

import java.util.LinkedList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import it.wsie.twitteranalyzer.model.tasks.identification.parser.TweetXMLParser.TweetListener;

/**
 * @author Simone Papandrea
 *
 */
class TweetXMLHandler extends DefaultHandler {

	private final LinkedList<TweetXMLTags> mTAGs;
	private final TweetListener mTweetListener;
	private String mTweet;

	protected TweetXMLHandler(TweetListener tweetListener) {

		this.mTAGs = new LinkedList<TweetXMLTags>();
		this.mTweetListener = tweetListener;
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {

		TweetXMLTags tag;

		tag = TweetXMLTags.valueOf(name.toUpperCase());

		switch (tag) {

		case P:

			mTweet = "";
			break;

		default:
			break;
		}

		this.push(tag);
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {

		TweetXMLTags tag = TweetXMLTags.valueOf(name.toUpperCase());

		switch (tag) {

		case P:

			this.mTweetListener.tweetReady(mTweet);
			break;
		default:

			break;
		}

		this.pop();
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {

		switch ((TweetXMLTags) this.get()) {

		case P:
			mTweet += new String(ch, start, length);
			break;

		default:
			break;
		}

	}

	private void push(TweetXMLTags tag) {

		this.mTAGs.push(tag);
	}

	private TweetXMLTags pop() {

		return mTAGs.pop();
	}

	private TweetXMLTags get() {

		return mTAGs.getFirst();
	}

}
