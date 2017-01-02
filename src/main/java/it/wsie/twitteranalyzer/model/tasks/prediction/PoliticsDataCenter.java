package it.wsie.twitteranalyzer.model.tasks.prediction;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import it.wsie.twitteranalyzer.model.tasks.identification.Candidate;
import it.wsie.twitteranalyzer.model.tasks.identification.Party;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Simone Papandrea
 *
 */
public class PoliticsDataCenter implements Serializable {

	private static final long serialVersionUID = 4409164873980084589L;
	private final Map<Candidate, Map<Long, Integer>> mMentionsSeries;
	private final Map<Candidate, Map<Long, Double>> mTrendSeries;
	private Map<Candidate, Integer> mCandidatePreferences;
	private Map<Party, Integer> mPartyPreferences;

	public PoliticsDataCenter() {

		mMentionsSeries = new HashMap<Candidate, Map<Long, Integer>>();
		mTrendSeries = new HashMap<Candidate, Map<Long, Double>>();
	}

	public Map<Candidate, SortedSet<Point>> getMentionsSeries() {

		Map<Candidate, SortedSet<Point>> series;
		SortedSet<Point> points;

		series = new HashMap<Candidate, SortedSet<Point>>();

		for (Entry<Candidate, Map<Long, Integer>> e : mMentionsSeries.entrySet()) {

			points = new TreeSet<Point>();

			for (Entry<Long, Integer> p : e.getValue().entrySet())
				points.add(new Point(p.getKey(), p.getValue()));

			series.put(e.getKey(), points);
		}

		return series;
	}

	public Map<Candidate, SortedSet<Point>> getTrendSeries() {

		Map<Candidate, SortedSet<Point>> series;
		SortedSet<Point> points;

		series = new HashMap<Candidate, SortedSet<Point>>();

		for (Entry<Candidate, Map<Long, Double>> e : mTrendSeries.entrySet()) {

			points = new TreeSet<Point>();

			for (Entry<Long, Double> p : e.getValue().entrySet())
				points.add(new Point(p.getKey(), p.getValue()));

			series.put(e.getKey(), points);
		}

		return series;
	}

	public Map<Candidate, Integer> getCandidatePreferences() {

		return mCandidatePreferences;
	}

	public Map<Party, Integer> getPartyPreferences() {

		return mPartyPreferences;
	}

	void setCandidatePreferences(Map<Candidate, Integer> candidatePreferences) {

		mCandidatePreferences = candidatePreferences;
	}

	void setPartyPreferences(Map<Party, Integer> partyPreferences) {

		mPartyPreferences = partyPreferences;
	}

	void addMentionPoint(Candidate candidate, long timestamp) {

		Map<Long, Integer> points;
		Integer value = 1;

		points = mMentionsSeries.get(candidate);

		if (points == null) {
			points = new HashMap<Long, Integer>();
			mMentionsSeries.put(candidate, points);
		}

		if (points.containsKey(timestamp))
			value += points.get(timestamp);

		points.put(timestamp, value);
	}

	void addTrendPoint(Candidate candidate, long timestamp, Double value) {

		Map<Long, Double> points;

		points = mTrendSeries.get(candidate);

		if (points == null) {
			points = new HashMap<Long, Double>();
			mTrendSeries.put(candidate, points);
		}

		if (points.containsKey(timestamp))
			value += points.get(timestamp);

		points.put(timestamp, value);
	}

}
