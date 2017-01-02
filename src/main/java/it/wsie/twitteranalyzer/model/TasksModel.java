package it.wsie.twitteranalyzer.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.wsie.twitteranalyzer.Config;
import it.wsie.twitteranalyzer.model.tasks.Task;
import it.wsie.twitteranalyzer.model.tasks.analisys.AnalisysTask;
import it.wsie.twitteranalyzer.model.tasks.classification.ClassificationTask;
import it.wsie.twitteranalyzer.model.tasks.crawling.CrawlingTask;
import it.wsie.twitteranalyzer.model.tasks.identification.Candidate;
import it.wsie.twitteranalyzer.model.tasks.identification.IdentificationTask;
import it.wsie.twitteranalyzer.model.tasks.partitioning.PartitioningTask;
import it.wsie.twitteranalyzer.model.tasks.prediction.PredictionTask;
import it.wsie.twitteranalyzer.model.tasks.scraping.ScrapingTask;

/**
 * @author Simone Papandrea
 *
 */
public class TasksModel {

	private final List<Task<?>> mTasks;
	private final ModelLoaderListener mListener;
	
	public TasksModel(Config config,ModelLoaderListener listener) throws Exception {

		Serializer.setOutputDir("temp");

		mListener=listener;
		mTasks = new ArrayList<Task<?>>();
		mTasks.add(loadTask(IdentificationTask.class,null,null));
		mTasks.add(loadTask(CrawlingTask.class, new Class<?>[] { Integer.class },
				new Object[] {config.getGraphSize() }));
		mTasks.add(loadTask(PartitioningTask.class,null,null));
		mTasks.add(loadTask(ClassificationTask.class,null,null));
		mTasks.add(loadTask(AnalisysTask.class, new Class<?>[] { Integer.class ,Integer.class},
				new Object[] { config.getTopK(),config.getWords() }));
		mTasks.add(loadTask(ScrapingTask.class, new Class<?>[] { Candidate.class, Integer.class,Integer.class, Integer.class },
				new Object[] { new Candidate(config.getCandidate()), config.getTopK(),config.getWords(),
						config.getNews() }));
		mTasks.add(loadTask(PredictionTask.class,null,null));
	}

	private Task<?> loadTask(Class<? extends Task<?>> cls, Class<?>[] types, Object[] params) throws Exception {

		Task<?> task;

		if(mListener!=null)
			mListener.loading(cls);
		
		task = (Task<?>) Serializer.read(cls);

		if (task == null) {

			if(types==null)
				task = cls.getConstructor().newInstance();
			else
			task = cls.getConstructor(types).newInstance(params);
		}

		return task;
	}

	public boolean saveTask(Task<?> task) {

		return Serializer.write(task);
	}

	public List<Task<?>> getTasks() {

		return mTasks;
	}

	public Task<?> getTask(int index) {

		return mTasks.get(index);
	}

	public int getTasksSize() {

		return mTasks.size();
	}

	public boolean isTaskClear(int index) {

		return !Serializer.exists(getTask(index).getClass());
	}

	public void clearTask(Task<?> task) throws IOException {

		Serializer.delete(task.getClass());
		task.reset();
	}

	public interface ModelLoaderListener{
		
		public void loading(Class<? extends Task<?>>  task);
	}
}
