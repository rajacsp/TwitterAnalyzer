package it.wsie.twitteranalyzer.model.tasks.identification.parser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.zip.GZIPInputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Simone Papandrea
 *
 */
public class TweetXMLParser {

	private SAXParser mSAXParser;
	private TweetXMLHandler mTweetXMLHandler;

	public TweetXMLParser(TweetListener tweetListener) throws ParserConfigurationException, SAXException {

		SAXParserFactory factory = SAXParserFactory.newInstance();

		mSAXParser = factory.newSAXParser();
		mTweetXMLHandler = new TweetXMLHandler(tweetListener);

	}

	public void parse(File file) throws SAXException, IOException {

		SequenceInputStream inputStream;
		InputSource is;

		inputStream = new SequenceInputStream(Collections.enumeration(Arrays.asList(new InputStream[] {
				new ByteArrayInputStream("<D>".getBytes()), new GZIPInputStream(new FileInputStream(file)),
				new ByteArrayInputStream("</D>".getBytes()), })));
		is = new InputSource(inputStream);
		is.setEncoding("ISO-8859-1");

		try {
			mSAXParser.parse(is, mTweetXMLHandler);

		} finally {

			inputStream.close();
		}
	}

	public interface TweetListener {

		public void tweetReady(String data);
	}
}
