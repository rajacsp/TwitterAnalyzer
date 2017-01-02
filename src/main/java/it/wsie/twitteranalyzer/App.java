package it.wsie.twitteranalyzer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import it.wsie.twitteranalyzer.model.TasksModel;
import it.wsie.twitteranalyzer.model.tasks.Task;
import it.wsie.twitteranalyzer.view.ActionsView;
import it.wsie.twitteranalyzer.view.ReportsView;
import it.wsie.twitteranalyzer.view.StatusView;
import it.wsie.twitteranalyzer.view.TasksView;
import javafx.application.Platform;

/**
 * @author Simone Papandrea
 *
 */
public class App {

	public static void main(String[] args) throws Exception {

		TasksModel model;
		TasksView taskListView;
		TasksController tasksController;
		ActionsView actionView;
		ReportsView reportView;
		StatusView statusView;
		Config config;
		SplashScreen splash;

		splash = new SplashScreen();

		try {
		
			config = Config.getInstance();
			model = new TasksModel(config, splash);
			actionView = new ActionsView();
			reportView = new ReportsView();
			statusView = new StatusView();
			taskListView = new TasksView(model.getTasks());
			tasksController = new TasksController(model, actionView, reportView, statusView, config);
			taskListView.addSelectionListener(tasksController);

		} finally {
			splash.dispose();
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				JFrame frame;
				JSplitPane splitPane;
				JMenuBar menubar;
				JMenu menu;
				JMenuItem menuClear, menuClearAll;

				frame = new JFrame("Twitter Anlyzer");
				menuClearAll = new JMenuItem("Clear all data");
				menuClear = new JMenuItem("Clear task");
				menu = new JMenu("Data");
				menu.add(menuClear);
				menu.add(menuClearAll);
				menubar = new JMenuBar();
				menubar.add(menu);
				menuClearAll.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						try {

							if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to clear all tasks data?",
									"Clear all?", JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
								tasksController.clearAllTasks();
								JOptionPane.showMessageDialog(frame, "All task cleared.", "Done",
										JOptionPane.INFORMATION_MESSAGE);
							}

						} catch (IOException ex) {
							JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				});

				menuClear.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						try {

							if (JOptionPane.showConfirmDialog(frame,
									"Are you sure you want to clear data for the selected task?", "Clear task?",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

								tasksController.clearSelectedTask();
								JOptionPane.showMessageDialog(frame, "Task cleared.", "Done",
										JOptionPane.INFORMATION_MESSAGE);
							}

						} catch (IOException ex) {
							JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						}

					}
				});

				splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, taskListView, reportView);
				frame.add(actionView, BorderLayout.PAGE_START);
				frame.add(splitPane, BorderLayout.CENTER);
				frame.add(statusView, BorderLayout.PAGE_END);
				frame.setJMenuBar(menubar);
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				frame.setVisible(true);

				frame.addWindowListener(new java.awt.event.WindowAdapter() {
					@Override
					public void windowClosing(java.awt.event.WindowEvent windowEvent) {
						if (JOptionPane.showConfirmDialog(frame, "Are you sure to close the program?",
								"Really Closing?", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
							tasksController.stop();
							System.exit(0);
						}
					}
				});
			}
		});

		Platform.setImplicitExit(false);

	}

	private static class SplashScreen extends JWindow implements TasksModel.ModelLoaderListener {

		private static final long serialVersionUID = -1100469271096656740L;
		private final JLabel mProgressLabel;

		SplashScreen() {

			mProgressLabel = new JLabel("", SwingConstants.CENTER);
			mProgressLabel.setForeground(Color.WHITE);
			mProgressLabel.setBorder(BorderFactory.createEmptyBorder(0, 40, 40, 40));
			createSplash();
		}

		private void createSplash() {

			JPanel panel;
			JLabel title;

			panel = new JPanel();
			panel.setLayout(new BorderLayout());
			title = new JLabel("Twitter Analyzer");
			title.setForeground(Color.WHITE);
			title.setFont(title.getFont().deriveFont(40f));
			title.setHorizontalAlignment(SwingConstants.CENTER);
			title.setBorder(BorderFactory.createEmptyBorder(40, 40, 10, 40));
			panel.add(title);
			panel.add(mProgressLabel, BorderLayout.SOUTH);
			panel.setBackground(new Color(29, 161, 242));
			this.setContentPane(panel);
			this.pack();
			this.setLocationRelativeTo(null);
			this.setVisible(true);
		}

		@Override
		public void loading(Class<? extends Task<?>> task) {
			mProgressLabel.setText("Loading " + task.getSimpleName()+"...");

		}

	}
}