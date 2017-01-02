package it.wsie.twitteranalyzer.view.table;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JProgressBar;
import javax.swing.table.AbstractTableModel;

import it.wsie.twitteranalyzer.model.tasks.Task;
import it.wsie.twitteranalyzer.model.tasks.Task.Status;

/**
 * @author Simone Papandrea
 *
 */
public class TasksTableModel extends AbstractTableModel implements Observer {

	private static final long serialVersionUID = 1L;
	public final static String[] COLUMNS = { "Task", "Progress", "", "Status" };
	public final static Class<?>[] CLASSES = { String.class, JProgressBar.class, String.class, String.class };
	private final List<Task<?>> mTasks;

	public TasksTableModel(List<Task<?>> tasks) {

		mTasks = tasks;

		for (Task<?> task : tasks)
			task.addObserver(this);
	}

	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}

	@Override
	public int getRowCount() {

		return mTasks.size();
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {

		Object obj = null;
		Task<?> task;

		task = mTasks.get(arg0);

		switch (arg1) {

		case 1:
			
			int payload = task.getPayload();
			obj = 0f;

			if (payload > 0)
				obj = task.getProgress() * 100f / payload * (task.getStatus() == Status.ERROR ? -1 : 1);

			break;

		case 2:
			obj = task.getProgress() + " / " + task.getPayload();
			break;

		case 3:
			obj = task.getStatus();
			break;

		default:
			obj = task.getDescription();
			break;
		}

		return obj;
	}

	@Override
	public String getColumnName(int column) {

		return COLUMNS[column];
	}

	@Override
	public boolean isCellEditable(int row, int column) {

		return false;
	}

	@Override
	public Class<?> getColumnClass(int c) {

		return CLASSES[c];
	}

	@Override
	public void update(Observable arg0, Object arg1) {

		int index = mTasks.indexOf(arg1);

		if (index != -1)
			fireTableRowsUpdated(index, index);
	}

	protected Task<?> getTask(int index) {

		return mTasks.get(index);
	}
}
