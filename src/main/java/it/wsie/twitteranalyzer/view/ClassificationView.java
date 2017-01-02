package it.wsie.twitteranalyzer.view;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import it.wsie.twitteranalyzer.model.tasks.classification.Audience;
import it.wsie.twitteranalyzer.model.tasks.classification.PoliticalPoll;
import it.wsie.twitteranalyzer.model.tasks.classification.sentiment.Sentiment;
import it.wsie.twitteranalyzer.model.tasks.identification.Candidate;
import it.wsie.twitteranalyzer.view.chart.PieChart;
import it.wsie.twitteranalyzer.view.table.AudienceTable;
import it.wsie.twitteranalyzer.view.table.AudienceTableModel;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * @author Simone Papandrea
 *
 */
public class ClassificationView extends JTabbedPane {

	private static final long serialVersionUID = -3892986705462225648L;

	public ClassificationView(PoliticalPoll poll) {

		setBackground(Color.WHITE);
		initGUI(poll);
	}

	private void initGUI(PoliticalPoll poll) {

		for (Candidate candidate : poll)
			addTab(candidate.getName(), null, createCandidateTab(poll.getAudience(candidate)));

	}

	private JPanel createCandidateTab(Audience audience) {

		JPanel panel;
		JFXPanel fxPanel;
		JScrollPane pane;
		AudienceTable table;
		AudienceTableModel model;

		model = new AudienceTableModel(audience);
		table = new AudienceTable(model);
		pane = new JScrollPane(table);
		pane.setBorder(BorderFactory.createEmptyBorder());
		pane.getViewport().setBackground(Color.WHITE);
		fxPanel = new JFXPanel();
		panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.add(fxPanel, BorderLayout.CENTER);
		panel.add(pane, BorderLayout.EAST);

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				PieChart candidatesChart;
				AnchorPane pane;
				Label caption;

				caption = new Label();
				candidatesChart = new PieChart("Users classification based on Twitter sentiment analysis",
						new PieChart.SliceListener() {

							@Override
							public void sliceSelected(String name) {
								model.setAudienceSentiment(Sentiment.valueOf(name));
							}
						});

				for (Sentiment sentiment : Sentiment.values())
					candidatesChart.addSlice(sentiment.name(), audience.getSize(sentiment), caption);

				pane = getAnchor(candidatesChart);
				pane.getChildren().add(caption);
				fxPanel.setScene(new Scene(pane, javafx.scene.paint.Color.WHITE));
			}
		});

		return panel;
	}

	private static AnchorPane getAnchor(Chart chart) {

		AnchorPane anchorPane = new AnchorPane();

		AnchorPane.setTopAnchor(chart, 0.0);
		AnchorPane.setBottomAnchor(chart, 0.0);
		AnchorPane.setLeftAnchor(chart, 0.0);
		AnchorPane.setRightAnchor(chart, 0.0);
		anchorPane.getChildren().add(chart);

		return anchorPane;
	}
}
