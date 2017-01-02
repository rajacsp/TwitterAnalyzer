package it.wsie.twitteranalyzer.model.tasks;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @author Simone Papandrea
 *
 */
public abstract class Task<T> extends Observable implements Callable<T>, Serializable {

	private static final long serialVersionUID = -6138267660060178414L;
	private final String mID;
	private final String mDescription;
	private final Set<Object> mCache;
	private int mProgress;
	private int mPayload;
	private Status mStatus;
	private String mError;
	private T mResult;

	public enum Status {

		COMPLETED, PENDING, RUNNING, ERROR, INTERRUPTED;
	}

	public Task(String id, String description) {

		this.mID = id;
		this.mDescription = description;
		this.mPayload = 0;
		this.mProgress = 0;
		this.mStatus = Status.PENDING;
		this.mCache =Collections.synchronizedSet( new HashSet<Object>());
	}

	public abstract void setArgs(Object... args);

	public void reset() {

		this.mPayload = 0;
		this.mProgress = 0;
		this.mStatus = Status.PENDING;
		this.mError = null;
		this.mResult = null;
		this.mCache.clear();
		notifyChanged();
	}

	public String getID() {

		return this.mID;
	}

	public String getDescription() {

		return this.mDescription;
	}

	public Status getStatus() {

		return this.mStatus;
	}

	public boolean isCompleted() {

		return this.mStatus.equals(Status.COMPLETED);
	}

	public int getPayload() {

		return this.mPayload;
	}

	public int getProgress() {

		return this.mProgress;
	}

	public String getError() {

		return this.mError;
	}

	protected final void setPayload(int payload) {

		this.mPayload = payload;
		notifyChanged();
	}

	protected final void progress() {

		progress(1);
	}

	protected  synchronized final void progress(int p) {

		int diff = mPayload - mProgress;

		p = Math.min(p, diff);

		if (p > 0) {
			
			this.mProgress += p;
			diff-=p;
			
			if (diff == 0)
				completed();
		}

		notifyChanged();
	}

	protected final void completed(){
		
		setStatus(Status.COMPLETED);
	}
	
	private void error(String error) {

		mError = error;
		setStatus(Status.ERROR);
	}

	private void setStatus(Status status) {

		this.mStatus = status;
	}

	private final void notifyChanged() {

		setChanged();
		notifyObservers(this);
	}

	protected void checkInterruption() throws InterruptedException {

		if (Thread.interrupted())
			throw new InterruptedException();
	}

	protected void setResult(T result) {

		this.mResult = result;
	}

	public T getResult() {

		return mResult;
	}

	protected void cache(Object o) {

		mCache.add(o);
	}

	protected boolean checkCache(Object o) {

		return mCache.contains(o);
	}

	@Override
	public final T call() throws Exception {

		T result = getResult();

		try {

			setStatus(Status.RUNNING);
			mError = null;
			notifyChanged();

			if (result == null)
				result = newObject();

			execute(result);

		} catch (Exception ex) {

			ex.printStackTrace();
			
			if (ex instanceof InterruptedException || Thread.interrupted())
				setStatus(Status.INTERRUPTED);
			else
				error(ex.getMessage());

		} finally {

			setResult(result);
			notifyChanged();
		}

		return result;
	}

	public abstract void execute(T data) throws Exception;

	public abstract T newObject();
}
