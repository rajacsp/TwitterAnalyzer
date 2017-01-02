package it.wsie.twitteranalyzer.model.tasks.analisys.index;

import java.io.IOException;

/**
 * @author Simone Papandrea
 *
 */
interface Indexer {

	static final String TWEET_FIELD = "tweet";
	static final String INDEX_FOLDER = "index";
	public void open(String dir) throws IOException;
	public void close() throws IOException;
}
