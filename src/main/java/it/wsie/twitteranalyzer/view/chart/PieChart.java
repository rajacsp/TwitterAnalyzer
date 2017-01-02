package it.wsie.twitteranalyzer.view.chart;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * @author Simone Papandrea
 *
 */
public class PieChart extends javafx.scene.chart.PieChart {

	private SliceListener mSliceListener;
	private int mTotal = 0;

	public PieChart(String title) {

		setTitle(title);
		setData(FXCollections.observableArrayList());
		setStyle("-fx-background-color: rgb(255,255,255);");
	}

	public PieChart(String title, SliceListener listener) {

		this(title);
		this.mSliceListener = listener;
	}

	public void addSlice(String term, int val, Label caption) {

		javafx.scene.chart.PieChart.Data data;

		caption.setTextFill(Color.BLACK);
		caption.setStyle("-fx-font: 24 arial;");

		data = new javafx.scene.chart.PieChart.Data(term, val);
		getData().add(data);
		data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				int val = (int) data.getPieValue();

				caption.setTranslateX(e.getSceneX());
				caption.setTranslateY(e.getSceneY());
				caption.setText(
						String.format("%.02f", val * 100f / mTotal) + "%" + " (" + String.format("%d", val)+"/"+String.format("%d", mTotal) + ")");

				if (mSliceListener != null)
					mSliceListener.sliceSelected(data.getName());
			}
		});

		mTotal += val;
	}

	public interface SliceListener {

		public void sliceSelected(String name);
	}

}