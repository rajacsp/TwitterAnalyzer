package it.wsie.twitteranalyzer.model.tasks.crawling;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

import it.wsie.twitteranalyzer.model.tasks.Task;
import it.wsie.twitteranalyzer.model.tasks.identification.User;
import twitter4j.Twitter;

/**
 * @author Simone Papandrea
 *
 */
public class CrawlingTask extends Task<TwitterGraph> {

	private static final long serialVersionUID = -3865298245679362712L;
	public static final String ID = "Crawling";
	private static final String DESCRIPTION = "Creating graph of Twitter users";
	private transient List<User> mUsers;
	private transient String mCrawlers;
	private transient int mGraphIconSize;
	private transient String mGraphDir;
	private final int mGraphSize;

	public CrawlingTask(Integer graphSize) {

		super(ID, DESCRIPTION);
		
		mGraphSize =graphSize;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setArgs(Object... args) {

		int count, size;
		Path path;
		
		mUsers = new ArrayList<User>((Set<User>) args[0]);
		mCrawlers = (String) args[1];
		mGraphDir = (String) args[2];
		mGraphIconSize = (int) args[3];
		
		path=Paths.get(mGraphDir);
		
		if(!Files.exists(path))
			path.toFile().mkdir();
		
		size = mUsers.size();
		count = Math.min(mGraphSize, size);
		Collections.sort(mUsers, new SorterByFriends());
		mUsers.subList(count, size).clear();
		setPayload(count);

		for (Iterator<User> iter = mUsers.iterator(); iter.hasNext();)
			if (checkCache(iter.next()))
				iter.remove();
	}

	@Override
	public void execute(TwitterGraph graph) throws Exception {
		
		buildGraph(graph);
		graph.pageRank();
		graph.centrality();
		completed();
	}

	private void buildGraph(TwitterGraph graph) throws Exception {

		CompletionService<Void> service;
		ExecutorService executor;
		List<Twitter> crawlers;
		List<Future<Void>> futures;
		int count, size, min, max, pool = 0, threads;

		crawlers = getTwitterCrawlers();
		threads = crawlers.size();
		size = mUsers.size();
		count = (int) Math.ceil((double) size / threads);
		executor = Executors.newFixedThreadPool(threads);
		service = new ExecutorCompletionService<Void>(executor);
		futures = new ArrayList<Future<Void>>();

		try {

			for (int i = 0; i < threads; i++) {

				min = count * i;

				if (min < size) {
					max = Math.min(min + count, size);
					pool++;
					futures.add(service.submit(new CrawlerThread(crawlers.get(i), mUsers.subList(min, max), graph)));
				}
			}

			while (pool-- > 0)
				service.take().get();

		} finally {
			executor.shutdownNow();
			executor.awaitTermination(5, TimeUnit.SECONDS);
		}
	}

	private List<Twitter> getTwitterCrawlers()
			throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {

		SAXParserFactory factory;
		SAXParser parser;
		TwitterCrawler twitterCrawler;

		factory = SAXParserFactory.newInstance();
		parser = factory.newSAXParser();
		twitterCrawler = new TwitterCrawler();
		parser.parse(new FileInputStream(mCrawlers), twitterCrawler);

		return twitterCrawler.getTwitters();
	}

	@Override
	public TwitterGraph newObject() {

		return new TwitterGraph(mUsers, mGraphDir, mGraphIconSize);
	}

	private class CrawlerThread implements Callable<Void> {

		private final Twitter mTwitter;
		private final List<User> mUsers;
		private final TwitterGraph mGraph;

		CrawlerThread(Twitter twitter, List<User> users, TwitterGraph graph) {

			this.mTwitter = twitter;
			this.mUsers = users;
			this.mGraph = graph;
		}

		@Override
		public Void call() throws Exception {

			for (User user : mUsers) {

				mGraph.build(mTwitter, user);
				progress();
				cache(user);
				checkInterruption();
			}

			return null;
		}
	}

	static class SorterByFriends implements Comparator<User> {

		@Override
		public int compare(User arg0, User arg1) {

			return Integer.compare(arg1.getFriends(), arg0.getFriends());
		}

	}
}
