package it.wsie.twitteranalyzer.model.tasks.analisys.index;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

/**
 * @author Simone Papandrea
 *
 */
public class CoOccurrence implements Serializable {

	private static final long serialVersionUID = 3395687774186688042L;
	private final String mTerm1;
	private final String mTerm2;
	private int mDocFreq;
	private double mJaccardIndex = 0;

	CoOccurrence(String term1, String term2) {

		this.mTerm1 = term1;
		this.mTerm2 = term2;
	}

	public String getTerm1() {

		return mTerm1;
	}

	public String getTerm2() {

		return mTerm2;
	}

	public double getJaccardIndex() {

		return this.mJaccardIndex;
	}

	public int getDocFreq() {

		return this.mDocFreq;
	}

	void setCorrelation(double jaccardIndex, int docFreq) {

		this.mJaccardIndex = jaccardIndex;
		this.mDocFreq = docFreq;
	}

	@Override
	public boolean equals(Object arg0) {

		if (arg0 != null && arg0 instanceof CoOccurrence) {

			CoOccurrence coOccurrence = (CoOccurrence) arg0;

			return this.getTerm1().equals(coOccurrence.getTerm1()) && this.getTerm2().equals(coOccurrence.getTerm2());
		}

		return false;
	}

	@Override
	public int hashCode() {

		return Objects.hash(this.getTerm1(), this.getTerm2());
	}

	public static class JaccardIndexComparator implements Comparator<CoOccurrence> {

		@Override
		public int compare(CoOccurrence arg0, CoOccurrence arg1) {

			int result;

			result = Double.compare(arg1.getJaccardIndex(), arg0.getJaccardIndex());

			if (result == 0)
				result = Integer.compare(arg1.getDocFreq(), arg0.getDocFreq());

			return result;
		}
	}
}
