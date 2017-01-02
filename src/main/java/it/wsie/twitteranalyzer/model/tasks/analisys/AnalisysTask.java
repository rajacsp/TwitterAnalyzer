package it.wsie.twitteranalyzer.model.tasks.analisys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import it.wsie.twitteranalyzer.model.tasks.Task;
import it.wsie.twitteranalyzer.model.tasks.analisys.index.Analyzer;
import it.wsie.twitteranalyzer.model.tasks.analisys.index.CoOccurrence;
import it.wsie.twitteranalyzer.model.tasks.analisys.index.Searcher;
import it.wsie.twitteranalyzer.model.tasks.identification.Candidate;
import it.wsie.twitteranalyzer.model.tasks.identification.Message;
import it.wsie.twitteranalyzer.model.tasks.partitioning.Nominator;
import it.wsie.twitteranalyzer.model.tasks.partitioning.PoliticalParticipation;

/**
 * @author Simone Papandrea
 *
 */
public class AnalisysTask extends Task<Map<Candidate, List<CoOccurrence>>> {

	private static final long serialVersionUID = -3250920716417352612L;
	private static final String DESCRIPTION = "Co-occurrence analysis";
	public static final String ID = "Analysis";
	private final int mTopK;
	private final int mWords;
	private transient PoliticalParticipation mPartition;

	public AnalisysTask(Integer topK,Integer words) {

		super(ID, DESCRIPTION);

		this.mTopK = topK;
		this.mWords=words;
	}

	@Override
	public void setArgs(Object... args) {

		mPartition = (PoliticalParticipation) args[0];
		setPayload(mPartition.getCandidatesCount());
	}

	@Override
	public void execute(Map<Candidate, List<CoOccurrence>> coOccurrencesMap) throws Exception {

		for (Candidate candidate : mPartition) {

			if (!checkCache(candidate)) {

				indexes(candidate);
				coOccurrencesMap.put(candidate, analyze(candidate));
				progress();
				cache(candidate);				
				checkInterruption();				
			}
		}
		
		completed();
	}

	private void indexes(Candidate candidate) throws IOException, InterruptedException {

		Analyzer analyzer;
		Iterator<Nominator> iterator;
		List<String> messages;
		Nominator nominator;
		Pattern pattern;
		String text;
		
		pattern = Pattern.compile("((^|\\s+)(RT|rt|MT|mt|FF|ff|RLRT|rlrt|OH|oh)(\\s+|$))|([#|@]\\S+)", Pattern.CASE_INSENSITIVE);
		analyzer = new Analyzer();
		messages = new ArrayList<String>();
		analyzer.open(candidate.getName());
		iterator = mPartition.getNominators(candidate).iterator();

		while (iterator.hasNext()) {

			nominator = iterator.next();

			for (Message message : nominator.getMessages()){
			
				text=message.getText();
				text= pattern.matcher(text).replaceAll("");
				messages.add(text);
			}
		}

		analyzer.write(messages);
		analyzer.close();
	}

	private List<CoOccurrence> analyze(Candidate candidate) throws Exception {

		List<CoOccurrence> coOccurrences;
		Searcher searcher;
		int size;

		searcher = new Searcher();
		searcher.open(candidate.getName());
		coOccurrences = searcher.getCoOccurrences(mWords);
		Collections.sort(coOccurrences,new CoOccurrence.JaccardIndexComparator());
		size = coOccurrences.size();

		if (size > mTopK)
			coOccurrences.subList(mTopK, size).clear();

		searcher.close();

		return coOccurrences;
	}

	@Override
	public Map<Candidate, List<CoOccurrence>> newObject() {

		return new HashMap<Candidate, List<CoOccurrence>>();
	}

}