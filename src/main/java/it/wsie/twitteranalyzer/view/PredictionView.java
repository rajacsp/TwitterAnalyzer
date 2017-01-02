package it.wsie.twitteranalyzer.view;

import java.util.Map;
import java.util.SortedSet;
import java.util.Map.Entry;
import javax.swing.JTabbedPane;

import it.wsie.twitteranalyzer.model.tasks.identification.Candidate;
import it.wsie.twitteranalyzer.model.tasks.identification.Party;
import it.wsie.twitteranalyzer.model.tasks.prediction.Point;
import it.wsie.twitteranalyzer.model.tasks.prediction.PoliticsDataCenter;
import it.wsie.twitteranalyzer.view.chart.LineChart;
import it.wsie.twitteranalyzer.view.chart.PieChart;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

/**
 * @author Simone Papandrea
 *
 */
public class PredictionView extends JTabbedPane {

	private static final long serialVersionUID = 6534648653781784701L;

	public PredictionView(PoliticsDataCenter pdc) {

		setBackground(java.awt.Color.WHITE);
		addTemporalScene(pdc.getMentionsSeries());
		addTrendScene(pdc.getTrendSeries());
		addCandidatePreferences(pdc.getCandidatePreferences());
		addPartyPreferences(pdc.getPartyPreferences());
	}

	private void addTemporalScene(Map<Candidate, SortedSet<Point>> series) {

		JFXPanel fxPanel = new JFXPanel();

		addTab("Temporal Series", null, fxPanel);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				LineChart lineChart;

				lineChart = new LineChart("Temporal series of candidates mentions", "Days", "Mentions");

				for (Entry<Candidate, SortedSet<Point>> e : series.entrySet())
					lineChart.createSeries(e.getKey().getName(), e.getValue());

				fxPanel.setScene(new Scene(getAnchor(lineChart), Color.WHITE));
			}
		});
	}

	private void addTrendScene(Map<Candidate, SortedSet<Point>> series) {

		JFXPanel fxPanel = new JFXPanel();

		addTab("Sentiment Trend", null, fxPanel);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				LineChart lineChart;

				lineChart = new LineChart("Sentiment Trend for each candidate", "Days", "Sentiment");

				for (Entry<Candidate, SortedSet<Point>> e : series.entrySet())
					lineChart.createSeries(e.getKey().getName(), e.getValue());

				fxPanel.setScene(new Scene(getAnchor(lineChart), Color.WHITE));
			}
		});
	}

	private void addCandidatePreferences(Map<Candidate, Integer> preferences) {

		JFXPanel fxPanel = new JFXPanel();
		addTab("Candidate preferences", null, fxPanel);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				PieChart candidatesChart;
				AnchorPane pane;
				Label caption = new Label();

				candidatesChart = new PieChart("Candidate Preferences");

				for (Entry<Candidate, Integer> e : preferences.entrySet())
					candidatesChart.addSlice(e.getKey().getName(), e.getValue(), caption);

				pane = getAnchor(candidatesChart);
				pane.getChildren().add(caption);
				fxPanel.setScene(new Scene(pane, Color.WHITE));
			}
		});

	}

	private void addPartyPreferences(Map<Party, Integer> preferences) {

		JFXPanel fxPanel = new JFXPanel();

		addTab("Political Party Preferences", null, fxPanel);

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				PieChart partiesChart;
				Label caption = new Label();
				AnchorPane pane;

				partiesChart = new PieChart("Parties preferences");

				for (Entry<Party, Integer> e : preferences.entrySet())
					partiesChart.addSlice(e.getKey().name(), e.getValue(), caption);

				pane = getAnchor(partiesChart);
				pane.getChildren().add(caption);
				fxPanel.setScene(new Scene(pane, Color.WHITE));
			}
		});
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
