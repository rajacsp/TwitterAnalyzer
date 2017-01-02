package it.wsie.twitteranalyzer;

import java.io.IOException;

/**
 * @author Simone Papandrea
 *
 */
public interface TaskListener {

	void selectTask(int index);
	void clearSelectedTask() throws IOException;
	void clearAllTasks() throws IOException;
}