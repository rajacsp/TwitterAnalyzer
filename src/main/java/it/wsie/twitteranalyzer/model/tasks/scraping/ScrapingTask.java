package it.wsie.twitteranalyzer.model.tasks.scraping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.wsie.twitteranalyzer.model.tasks.Task;
import it.wsie.twitteranalyzer.model.tasks.analisys.index.Analyzer;
import it.wsie.twitteranalyzer.model.tasks.analisys.index.CoOccurrence;
import it.wsie.twitteranalyzer.model.tasks.analisys.index.Searcher;
import it.wsie.twitteranalyzer.model.tasks.identification.Candidate;
import it.wsie.twitteranalyzer.model.tasks.scraping.GoogleNews.GoogleNewsListener;

/**
 * @author Simone Papandrea
 *
 */
public class ScrapingTask extends Task<List<CoOccurrence>> {

	private static final long serialVersionUID = 2716705745960586483L;
	public static final String ID = "Scraping";
	private static final String DESCRIPTION = "Scraping Google News";
	private static final int PAGE_SIZE= 10;
	private final int mPayload;
	private final Candidate mCandidate;
	private final int mTopK;
	private final int mWords;
	private int mPage = -1;

	public ScrapingTask(Candidate candidate, Integer topK, Integer words,Integer payload) {

		super(ID, DESCRIPTION);

		this.mTopK = topK;
		this.mWords=words;
		this.mCandidate = candidate;
		this.mPayload = payload;
	}

	@Override
	public void execute(List<CoOccurrence> coOccurrences) throws Exception {

		indexes(scraping());
		analyze(coOccurrences);
		completed();
	}

	private List<String> scraping() throws IOException, InterruptedException {

		List<String> messages;

		messages = new ArrayList<String>();

		new GoogleNews(new GoogleNewsListener() {

			@Override
			public boolean GoogleNewsReady(List<String> news, int page) {
			
				messages.addAll(news);
				progress(news.size());
				cache(page);
				
				return isCompleted();
			}

		}, PAGE_SIZE).get(mCandidate.getName(), mPage);

		return messages;
	}

	private void indexes(List<String> messages) throws IOException {

		Analyzer analyzer;
		String name;
		
		name=mCandidate.getName();
		analyzer = new Analyzer();
		analyzer.open(name);
		analyzer.write(messages);
		analyzer.close();
	}

	private void analyze(List<CoOccurrence> coOccurrences) throws Exception {

		Searcher searcher;
		int size;
		
		searcher = new Searcher();
		searcher.open(mCandidate.getName());
		coOccurrences.addAll(searcher.getCoOccurrences(mWords));
		Collections.sort(coOccurrences,new CoOccurrence.JaccardIndexComparator());
		size=coOccurrences.size();
		
		if(size>mTopK)
			coOccurrences.subList(mTopK, size).clear();
		
		searcher.close();
	}

	public final Candidate getCandidate() {

		return this.mCandidate;
	}

	@Override
	public void setArgs(Object... args) {

		setPayload(mPayload);

		for (int i = 0; i < mPayload && mPage < 0; i += PAGE_SIZE)
			if (!checkCache(i))
				mPage = i;
	}

	@Override
	public List<CoOccurrence> newObject() {

		return new ArrayList<CoOccurrence>();
	}
}
