package it.wsie.twitteranalyzer.model.tasks.classification.sentiment;

/**
 * @author Simone Papandrea
 *
 */
class Polarity {

	private double mPos;
	private double mNeg;

	Polarity() {
		mPos = 0;
		mNeg = 0;
	}

	void add(double v) {

		if (v > 0)
			mPos += v;
		else
			mNeg += v;
	}

	boolean zero() {

		return mPos == 0 && mNeg == 0;
	}

	double score() {

		return Math.abs(mPos) - Math.abs(mNeg);
	}

	void invert() {

		double t = mPos;

		mPos = mNeg;
		mNeg = t;
	}

}