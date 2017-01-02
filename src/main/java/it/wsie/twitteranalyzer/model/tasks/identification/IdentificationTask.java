package it.wsie.twitteranalyzer.model.tasks.identification;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import it.wsie.twitteranalyzer.model.tasks.Task;
import it.wsie.twitteranalyzer.model.tasks.identification.parser.TweetJSONParser;
import it.wsie.twitteranalyzer.model.tasks.identification.parser.TweetXMLParser;
import it.wsie.twitteranalyzer.model.tasks.identification.parser.TweetXMLParser.TweetListener;

/**
 * @author Simone Papandrea
 *
 */
public class IdentificationTask extends Task<SocialBuzz> {

	private static final long serialVersionUID = 635268321275723037L;
	public static final String ID = "Identification";
	private static final String DESCRIPTION = "Identification mentions of candidates";
	private transient Pattern mPattern;
	private transient Map<String, Candidate> mCandidates;
	private transient List<File> mDataset;

	public IdentificationTask() {

		super(ID, DESCRIPTION);
	}

	@Override
	public void execute(SocialBuzz socialBuzz) throws Exception {

		identify(socialBuzz);
		completed();
	}

	private void identify(final SocialBuzz socialBuzz)
			throws IOException, ParserConfigurationException, SAXException, InterruptedException {

		TweetXMLParser tweetXMLParser;
		final String filterUrl = "(http(s)?|www)\\S*";
		final String filterTW = "((^|\\s+)(RT|rt|MT|mt|FF|ff|RLRT|rlrt|OH|oh)(\\s+|$))|([#|@]\\S*)";
		
		tweetXMLParser = new TweetXMLParser(new TweetListener() {

			@Override
			public void tweetReady(String data) {

				Message message;
				Mention mention;
				String name, text,temp;
				Matcher matcher;

				message = TweetJSONParser.parseTweet(data);
				mention = new Mention(message);
				text = message.getText().replaceAll(filterUrl, "").trim();
				temp = text.replaceAll(filterTW, "").trim();
				
				if (!temp.isEmpty()) {
					
					matcher = mPattern.matcher(text);

					while (matcher.find()) {

						name = matcher.group(1).toLowerCase().replaceAll("\\s", "");

						if (mCandidates.containsKey(name))
							mention.addCandidate(mCandidates.get(name));
					}

					if (!mention.isEmpty()) {
						
						message.setText(text);
						socialBuzz.add(TweetJSONParser.parseUser(data), mention);
					}
				}
			}
		});

		for (File file : mDataset) {

			tweetXMLParser.parse(file);
			progress();
			cache(file);
			checkInterruption();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setArgs(Object... args) {

		List<File> files;
		Set<Candidate> candidates;

		files = (List<File>) args[0];
		setPayload(files.size());
		mDataset = new ArrayList<File>();

		for (File file : files)
			if (!checkCache(file))
				mDataset.add(file);

		candidates = (Set<Candidate>) args[1];

		mPattern = Pattern.compile("\\b(" + String.join("|", candidates).replaceAll("\\s", "\\\\s*") + ")\\b",
				Pattern.CASE_INSENSITIVE);
		mCandidates = new HashMap<String, Candidate>();

		for (Candidate candidate : candidates)
			mCandidates.put(candidate.getName().toLowerCase().replaceAll("\\s", ""), candidate);
	}

	@Override
	public SocialBuzz newObject() {

		return new SocialBuzz();
	}

}
