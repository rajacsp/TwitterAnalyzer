package it.wsie.twitteranalyzer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Simone Papandrea
 *
 */
public class Config {

	private static final String NAME = "twitteranalyzer.properties";
	private final Properties mProps;
	private static Config instance;

	private Config() throws IOException {

		InputStream stream;

		mProps = new Properties();
		stream = new FileInputStream(NAME);

		try {
			mProps.load(stream);

		} finally {
			stream.close();
		}

	}

	public static Config getInstance() throws IOException {

		if (instance == null)
			instance = new Config();

		return instance;
	}


	public String getDatasetDir() {

		return mProps.getProperty("twitteranalyzer.identification.datasetDir", "dataset");
	}

	public String getGraphDir() {

		return mProps.getProperty("twitteranalyzer.crawling.graphDir", "graph");
	}

	public String getCandidates() {

		return mProps.getProperty("twitteranalyzer.candidates", "candidates.txt");
	}

	public String getCandidate() {

		return mProps.getProperty("twitteranalyzer.scraping.candidate");
	}

	public String getGraphIcon() {

		return mProps.getProperty("twitteranalyzer.crawling.graphIcon");
	}

	public int getGraphIconSize() {

		return Integer.valueOf(mProps.getProperty("twitteranalyzer.crawling.graphIconSize"));
	}

	public int getGraphSize() {

		return Integer.valueOf(mProps.getProperty("twitteranalyzer.crawling.graphSize"));
	}
	
	public int getTopK() {

		return Integer.valueOf(mProps.getProperty("twitteranalyzer.analyzer.topK"));
	}

	public int getWords() {

		return Integer.valueOf(mProps.getProperty("twitteranalyzer.analyzer.words"));
	}
	
	public int getNews() {

		return Integer.valueOf(mProps.getProperty("twitteranalyzer.scraping.news"));
	}
	
	public String getTwitterCrawlers() {

		return mProps.getProperty("twitteranalyzer.crawling.crawlers");
	}
}
