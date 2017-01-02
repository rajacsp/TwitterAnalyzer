package it.wsie.twitteranalyzer.view.chart;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.SortedSet;

import it.wsie.twitteranalyzer.model.tasks.prediction.Point;

/**
 * @author Simone Papandrea
 *
 */
public class LineChart extends javafx.scene.chart.LineChart<String, Number> {

	private final String mFormat;

	public LineChart(String title, String xLabel, String yLabel) {

		this(title, xLabel, yLabel, "yyyy-MM-dd");
		setStyle("-fx-background-color: rgb(255,255,255);");
	}

	public LineChart(String title, String xLabel, String yLabel, String format) {

		super(new CategoryAxis(), new NumberAxis());

		this.getXAxis().setLabel(xLabel);
		this.getYAxis().setLabel(yLabel);
		this.setTitle(title);
		this.mFormat = format;
	}

	public void createSeries(String term, SortedSet<Point> points) {

		XYChart.Series<String, Number> series;
		Calendar calendar;
		SimpleDateFormat sdf;

		sdf = new SimpleDateFormat(getFormat());
		calendar = Calendar.getInstance();
		series = new XYChart.Series<String, Number>();
		series.setName(term);

		for (Point point : points) {
			calendar.setTimeInMillis(point.getTime());
			series.getData().add(new XYChart.Data<String, Number>(sdf.format(calendar.getTime()), point.getValue()));
		}

		getData().add(series);
	}

	protected String getFormat() {

		return this.mFormat;
	}

}
