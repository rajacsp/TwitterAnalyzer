package it.wsie.twitteranalyzer.model.tasks.crawling;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @author Simone Papandrea
 *
 */
class TwitterCrawler extends DefaultHandler {

	enum TwitterCrawlerTag {

		CRAWLERS, TWITTER, ACCESS_TOKEN, ACCESS_TOKEN_SECRET, CONSUMER_KEY, CONSUMER_SECRET
	}

	private final LinkedList<TwitterCrawlerTag> mTAGs;
	private ConfigurationBuilder mCB;
	private final List<Twitter> mTwitters;

	protected TwitterCrawler() {

		this.mTAGs = new LinkedList<TwitterCrawlerTag>();
		this.mTwitters = new ArrayList<Twitter>();
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {

		TwitterCrawlerTag tag;

		tag = TwitterCrawlerTag.valueOf(name.toUpperCase());

		switch (tag) {

		case TWITTER:

			mCB = new ConfigurationBuilder();
			break;

		default:
			break;
		}

		this.push(tag);
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {

		TwitterCrawlerTag tag = TwitterCrawlerTag.valueOf(name.toUpperCase());

		switch (tag) {

		case TWITTER:

			this.mTwitters.add(new TwitterFactory(mCB.build()).getInstance());
			break;

		default:

			break;
		}

		this.pop();
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {

		String value = new String(ch, start, length).replaceAll("\\t|\\r|\\n", "");

		if (!value.isEmpty()) {
			switch ((TwitterCrawlerTag) this.get()) {

			case ACCESS_TOKEN:

				mCB.setOAuthAccessToken(value);
				break;

			case ACCESS_TOKEN_SECRET:

				mCB.setOAuthAccessTokenSecret(value);
				break;

			case CONSUMER_KEY:

				mCB.setOAuthConsumerKey(value);
				break;

			case CONSUMER_SECRET:

				mCB.setOAuthConsumerSecret(value);
				break;

			default:
				break;
			}
		}
	}

	private void push(TwitterCrawlerTag tag) {

		this.mTAGs.push(tag);
	}

	private TwitterCrawlerTag pop() {

		return mTAGs.pop();
	}

	private TwitterCrawlerTag get() {

		return mTAGs.getFirst();
	}

	List<Twitter> getTwitters() {

		return this.mTwitters;
	}
}
