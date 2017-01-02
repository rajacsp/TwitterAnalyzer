package it.wsie.twitteranalyzer.model.tasks.prediction;

import java.io.Serializable;

/**
 * @author Simone Papandrea
 *
 */
public class Point implements Comparable<Point>, Serializable {

	private static final long serialVersionUID = -7197494734688721484L;
	private final long mTime;
	private final Number mValue;

	public Point(long time, Number value) {

		this.mTime = time;
		this.mValue = value;
	}

	public long getTime() {

		return mTime;
	}

	public Number getValue() {

		return mValue;
	}

	@Override
	public int compareTo(Point arg0) {

		if (mTime < arg0.mTime)
			return -1;
		else if (mTime > arg0.mTime)
			return 1;
		else
			return 0;
	}

}
