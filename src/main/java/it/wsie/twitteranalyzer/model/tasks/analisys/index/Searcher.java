package it.wsie.twitteranalyzer.model.tasks.analisys.index;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 * @author Simone Papandrea
 *
 */
public class Searcher implements Indexer {

	private IndexReader mReader;

	@Override
	public void open(String dir) throws IOException {

		mReader = DirectoryReader.open(new SimpleFSDirectory(Paths.get(INDEX_FOLDER + File.separator + dir)));
	}

	@Override
	public void close() throws IOException {

		if (mReader != null)
			mReader.close();
	}

	public List<CoOccurrence> getCoOccurrences(int topK) throws Exception {

		List<CoOccurrence> coOccurrences;
		List<ImmutablePair<Term, Integer>> frequencies;
		BooleanQuery.Builder builder;
		IndexSearcher searcher;
		CoOccurrence coOccurrence;
		TermStats[] terms;
		Term term2;
		String t1, t2;
		int freq1, freq2, freq;
		double jaccard;

		frequencies = new ArrayList<ImmutablePair<Term, Integer>>();
		coOccurrences = new ArrayList<CoOccurrence>();
		searcher = new IndexSearcher(mReader);;
		terms = HighFreqTerms.getHighFreqTerms(mReader, topK,TWEET_FIELD,new HighFreqTerms.DocFreqComparator());

		for (TermStats term:terms) {

			term2 = new Term(TWEET_FIELD,term.termtext.utf8ToString());
			freq2 = term.docFreq;

			if (freq2 > 1) {

				for (ImmutablePair<Term, Integer> pair : frequencies) {

					t1 = pair.left.text();
					t2 = term2.text();
					freq1 = pair.right;
					coOccurrence = new CoOccurrence(t1, t2);
					builder = new BooleanQuery.Builder();
					builder.add(new TermQuery(pair.left), Occur.MUST);
					builder.add(new TermQuery(term2), Occur.MUST);
					freq = searcher.count(builder.build());

					if (freq > 1) {

						jaccard = freq * 1.0 / (freq1 + freq2 - freq);						
						coOccurrence.setCorrelation(jaccard, freq);
						coOccurrences.add(coOccurrence);
					}
				}

				frequencies.add(new ImmutablePair<Term,Integer>(term2, freq2));
			}
		}

		return coOccurrences;
	}

}
