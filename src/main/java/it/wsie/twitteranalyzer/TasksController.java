package it.wsie.twitteranalyzer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JComponent;
import javax.swing.SwingWorker;

import it.wsie.twitteranalyzer.model.TasksModel;
import it.wsie.twitteranalyzer.model.tasks.Task;
import it.wsie.twitteranalyzer.model.tasks.analisys.AnalisysTask;
import it.wsie.twitteranalyzer.model.tasks.analisys.index.CoOccurrence;
import it.wsie.twitteranalyzer.model.tasks.classification.ClassificationTask;
import it.wsie.twitteranalyzer.model.tasks.classification.PoliticalPoll;
import it.wsie.twitteranalyzer.model.tasks.crawling.CrawlingTask;
import it.wsie.twitteranalyzer.model.tasks.crawling.TwitterGraph;
import it.wsie.twitteranalyzer.model.tasks.identification.Candidate;
import it.wsie.twitteranalyzer.model.tasks.identification.IdentificationTask;
import it.wsie.twitteranalyzer.model.tasks.identification.Party;
import it.wsie.twitteranalyzer.model.tasks.identification.SocialBuzz;
import it.wsie.twitteranalyzer.model.tasks.partitioning.PartitioningTask;
import it.wsie.twitteranalyzer.model.tasks.partitioning.PoliticalParticipation;
import it.wsie.twitteranalyzer.model.tasks.prediction.PoliticsDataCenter;
import it.wsie.twitteranalyzer.model.tasks.prediction.PredictionTask;
import it.wsie.twitteranalyzer.model.tasks.scraping.ScrapingTask;
import it.wsie.twitteranalyzer.view.ActionsView;
import it.wsie.twitteranalyzer.view.AnalisysView;
import it.wsie.twitteranalyzer.view.ClassificationView;
import it.wsie.twitteranalyzer.view.CrawlingView;
import it.wsie.twitteranalyzer.view.IdentificationView;
import it.wsie.twitteranalyzer.view.PartitioningView;
import it.wsie.twitteranalyzer.view.PredictionView;
import it.wsie.twitteranalyzer.view.ReportsView;
import it.wsie.twitteranalyzer.view.ScrapingView;
import it.wsie.twitteranalyzer.view.StatusView;

/**
 * @author Simone Papandrea
 *
 */
public class TasksController implements ActionListener, TaskListener {

	private final TasksModel mModel;
	private final ActionsView mActionView;
	private final ReportsView mReportView;
	private final StatusView mStatusView;
	private final ReentrantLock mLock;
	private final Config mConfig;
	private Future<?> mFutureTask;
	private int mSeletectedIndex = -1;
	private Status mStatus = Status.STOP;

	private enum Status {

		START, PAUSE, STOP;
	}

	public TasksController(TasksModel model, ActionsView actionView, ReportsView reportView, StatusView statusView,
			Config config) {

		this.mModel = model;
		this.mActionView = actionView;
		this.mReportView = reportView;
		this.mStatusView = statusView;
		this.mActionView.addActionListener(this);
		this.mLock = new ReentrantLock();
		this.mConfig = config;

		init();
	}

	private void init() {

		Task<?> task;

		for (int i = 0; i < mModel.getTasksSize(); i++) {

			task = mModel.getTask(i);

			if (task.isCompleted())
				new TaskReportLoader(task.getResult(), task, i).execute();
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		switch (arg0.getActionCommand().toLowerCase()) {

		case "start":
			start();
			break;

		case "pause":
			pause();
			break;

		case "stop":
			stop();
			break;
		}
	}

	public void start() {

		if (mStatus != Status.START) {

			mLock.lock();
			execute();
			mStatus = Status.START;
			mLock.unlock();
		}
	}

	public void pause() {

		if (mStatus == Status.START) {

			mLock.lock();

			if (mFutureTask != null) {

				mFutureTask.cancel(true);

				try {
					mFutureTask.get();
				} catch (Exception e) {

				}
			}

			mStatus = Status.PAUSE;
			mLock.unlock();
		}
	}

	public void stop() {

		if (mStatus == Status.START) {

			mLock.lock();

			if (mFutureTask != null) {

				mFutureTask.cancel(true);

				try {
					mFutureTask.get();
				} catch (Exception e) {
				}
			}

			mStatus = Status.STOP;
			mLock.unlock();
		}
	}

	public void execute() {

		new Thread() {

			public void run() {

				SocialBuzz buzz;
				PoliticalParticipation partition;
				PoliticalPoll poll;
				TwitterGraph graph;

				try {

					buzz = (SocialBuzz) executeTask(0, readDataset(mConfig.getDatasetDir()),
							readCandidates(mConfig.getCandidates()));
					graph = (TwitterGraph) executeTask(1, buzz.getUsers(), mConfig.getTwitterCrawlers(),
							mConfig.getGraphDir(), mConfig.getGraphIconSize());
					partition = (PoliticalParticipation) executeTask(2, buzz, graph.getNodes());
					poll = (PoliticalPoll) executeTask(3, partition);
					executeTask(4, partition);
					executeTask(5);
					executeTask(6, partition, poll);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
				finally{
					
					mFutureTask=null;
					TasksController.this.stop();
				}

			}
		}.start();
	}

	private Object executeTask(int index, Object... args) throws Exception {

		Object result;
		Task<?> task;
		ExecutorService executor;

		task = mModel.getTask(index);
		result = task.getResult();

		if (!task.isCompleted()) {

			task.setArgs(args);
			executor = Executors.newSingleThreadExecutor();
			mFutureTask = executor.submit(task);

			try {

				result = mFutureTask.get();

			} finally {

				executor.shutdown();

				try {
					executor.awaitTermination(10, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
				}

				if (mStatus == Status.STOP)
					clearTask(task);
				else
					saveTask(task);
			}

			if (mSeletectedIndex == index)
				mReportView.showCard(task.getID());

			new TaskReportLoader(result, task, index).execute();
		}

		return result;
	}

	@Override
	public void selectTask(int index) {

		Task<?> task;

		mSeletectedIndex = index;
		task = mModel.getTask(index);

		if (task.isCompleted())
			mReportView.showCard(task.getID());
		else
			mReportView.hideCard();
	}

	private void saveTask(Task<?>task) {

		mStatusView.setStatus("Saving "+task.getID()+" data...");
		mModel.saveTask(task);
		mStatusView.reset();
	}

	class TaskReportLoader extends SwingWorker<JComponent, Void> {

		private final Object mResult;
		private final int mIndex;
		private final Task<?> mTask;

		TaskReportLoader(Object result, Task<?> task, int index) {

			mResult = result;
			mTask = task;
			mIndex = index;
		}

		@SuppressWarnings("unchecked")
		@Override
		public JComponent doInBackground() {

			JComponent component = null;

			switch (mTask.getID()) {

			case IdentificationTask.ID:
				component = new IdentificationView((SocialBuzz) mResult);
				break;

			case CrawlingTask.ID:
				component = new CrawlingView((TwitterGraph) mResult, mConfig.getGraphDir(), mConfig.getGraphIcon(),
						mConfig.getGraphIconSize());
				break;

			case PartitioningTask.ID:
				component = new PartitioningView((PoliticalParticipation) mResult);
				break;

			case ClassificationTask.ID:
				component = new ClassificationView((PoliticalPoll) mResult);
				break;

			case AnalisysTask.ID:
				component = new AnalisysView((Map<Candidate, List<CoOccurrence>>) mResult);
				break;

			case ScrapingTask.ID:

				Candidate candidate;
				Map<Candidate, List<CoOccurrence>> mapCoOccurrences;

				candidate = ((ScrapingTask) mTask).getCandidate();
				mapCoOccurrences = (Map<Candidate, List<CoOccurrence>>) mModel.getTask(mIndex - 1).getResult();
				component = new ScrapingView(candidate, (List<CoOccurrence>) mResult, mapCoOccurrences.get(candidate));
				break;

			case PredictionTask.ID:
				component = new PredictionView((PoliticsDataCenter) mResult);
				break;
			}

			return component;
		}

		@Override
		protected void done() {

			try {
				mReportView.addCard(mTask.getID(), get());

				if (mSeletectedIndex == mIndex)
					selectTask(mIndex);

			} catch (InterruptedException | ExecutionException e) {

			}
		}
	}

	@Override
	public void clearAllTasks() throws IOException {

		for (Task<?> task:mModel.getTasks())
			clearTask(task);
	}

	@Override
	public void clearSelectedTask() throws IOException {

		if (mSeletectedIndex > -1)
			clearTask(mModel.getTask(mSeletectedIndex));
	}

	private void clearTask(Task<?> task) throws IOException {

		mModel.clearTask(task);
		mReportView.deleteCard(task.getID());
	}

	private Set<Candidate> readCandidates(String candidatesFile) throws IOException {

		Set<Candidate> candidates;
		BufferedReader reader = null;
		final String regex = "\\t";
		String line, values[];

		candidates = new LinkedHashSet<Candidate>();

		try {

			reader = new BufferedReader(new InputStreamReader(new FileInputStream(candidatesFile)));

			while ((line = reader.readLine()) != null) {

				values = line.split(regex);
				candidates.add(new Candidate(values[0], Party.valueOf(values[1].toUpperCase())));
			}

		} finally {

			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {

				}
		}

		return candidates;
	}

	private List<File> readDataset(String dataSetDir) {

		List<File> files;
		File directory;

		directory = new File(dataSetDir);
		files = new ArrayList<File>();

		for (File file : directory.listFiles()) {

			if (file.isDirectory())
				files.addAll(readDataset(file.getAbsolutePath()));
			else
				files.add(file);
		}

		return files;
	}
}
