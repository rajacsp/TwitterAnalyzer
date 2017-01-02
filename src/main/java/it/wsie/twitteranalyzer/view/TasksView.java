package it.wsie.twitteranalyzer.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import it.wsie.twitteranalyzer.TaskListener;
import it.wsie.twitteranalyzer.model.tasks.Task;
import it.wsie.twitteranalyzer.view.table.TasksTable;
import it.wsie.twitteranalyzer.view.table.TasksTableModel;

/**
 * @author Simone Papandrea
 *
 */
public class TasksView extends JPanel {

	private static final long serialVersionUID = 1L;
	private final JTable mTable;

	public TasksView(List<Task<?>> tasks) {

		super(new BorderLayout());

		mTable = new TasksTable(new TasksTableModel(tasks));

		createLayout();
	}

	private void createLayout() {

		JScrollPane pane;

		mTable.setFillsViewportHeight(true);
		mTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mTable.setPreferredScrollableViewportSize(mTable.getPreferredSize());
		mTable.setRowSelectionInterval(0, 0);
		pane = new JScrollPane(mTable);
		pane.setBorder(BorderFactory.createEmptyBorder());
		pane.getViewport().setBackground(Color.WHITE);

		add(pane);
	}

	public void addSelectionListener(TaskListener listener) {

		mTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				if (!e.getValueIsAdjusting())
					listener.selectTask(mTable.getSelectedRow());
			}
		});

		listener.selectTask(mTable.getSelectedRow());
	}

}
