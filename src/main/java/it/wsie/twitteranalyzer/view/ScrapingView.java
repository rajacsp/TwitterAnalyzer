package it.wsie.twitteranalyzer.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.PolarBlendMode;
import com.kennycason.kumo.PolarWordCloud;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.palette.ColorPalette;

import it.wsie.twitteranalyzer.model.tasks.analisys.index.CoOccurrence;
import it.wsie.twitteranalyzer.model.tasks.identification.Candidate;
import it.wsie.twitteranalyzer.view.table.CoOccurrencesTable;
import it.wsie.twitteranalyzer.view.table.CoOccurrencesTableModel;
import it.wsie.twitteranalyzer.view.table.ProgressiveHeader;

/**
 * @author Simone Papandrea
 *
 */
public class ScrapingView extends JTabbedPane {

	private static final long serialVersionUID = -4670394166724624369L;
	private final ColorPalette PALETTE1 = new ColorPalette(new Color(0xff2d00), new Color(0xff6027),
			new Color(0xff9200));

	private final ColorPalette PALETTE2 = new ColorPalette(new Color(0x011f4b), new Color(0x005b96),
			new Color(0x6497b1));

	public ScrapingView(Candidate candidate, List<CoOccurrence> coOccurrences1, List<CoOccurrence> coOccurrences2) {

		setBackground(Color.WHITE);
		initGUI(candidate, coOccurrences1, coOccurrences2);
	}

	private void initGUI(Candidate candidate, List<CoOccurrence> coOccurrences1, List<CoOccurrence> coOccurrences2) {

		addTab(candidate.getName(), null, createCandidateTab(coOccurrences1, coOccurrences2));

	}

	private JPanel createCandidateTab(List<CoOccurrence> coOccurrences1, List<CoOccurrence> coOccurrences2) {

		JPanel panel;
		JLabel label;
		CoOccurrencesTableModel model;
		CoOccurrencesTable table;
		JScrollPane pane;
		ProgressiveHeader rowHeader;

		model = new CoOccurrencesTableModel(coOccurrences1);

		label = new JLabel(
				new ImageIcon(createPolarityWordCloud(coOccurrences1, coOccurrences2, 500).getBufferedImage()));
		label.setBackground(Color.WHITE);
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
		table = new CoOccurrencesTable(model, 10);

		rowHeader = new ProgressiveHeader(table);
		rowHeader.setFixedCellWidth(40);
		rowHeader.setFixedCellHeight(table.getRowHeight());

		pane = new JScrollPane(table);
		pane.setRowHeaderView(rowHeader);
		pane.setBorder(BorderFactory.createEmptyBorder());
		pane.setBackground(Color.WHITE);

		panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.add(label, BorderLayout.WEST);
		panel.add(pane, BorderLayout.CENTER);

		return panel;
	}

	private WordCloud createPolarityWordCloud(List<CoOccurrence> coOccurrences1, List<CoOccurrence> coOccurrences2,
			int size) {

		PolarWordCloud wordCloud;
		List<WordFrequency> wordFrequencies1, wordFrequencies2;
		Dimension dimension;

		wordFrequencies1 = new ArrayList<WordFrequency>();

		for (CoOccurrence coOccurrence : coOccurrences1)
			wordFrequencies1.add(new WordFrequency(coOccurrence.getTerm1() + " " + coOccurrence.getTerm2(),
					(int) (coOccurrence.getJaccardIndex() * 100)));

		wordFrequencies2 = new ArrayList<WordFrequency>();

		for (CoOccurrence coOccurrence : coOccurrences2)
			wordFrequencies2.add(new WordFrequency(coOccurrence.getTerm1() + " " + coOccurrence.getTerm2(),
					(int) (coOccurrence.getJaccardIndex() * 100)));

		dimension = new Dimension(size, size);
		wordCloud = new PolarWordCloud(dimension, CollisionMode.PIXEL_PERFECT, PolarBlendMode.BLUR);
		wordCloud.setPadding(2);
		wordCloud.setBackground(new CircleBackground(size / 2));
		wordCloud.setBackgroundColor(Color.WHITE);
		wordCloud.setColorPalette(PALETTE1);
		wordCloud.setColorPalette2(PALETTE2);
		wordCloud.setFontScalar(new SqrtFontScalar(10, 40));
		wordCloud.build(wordFrequencies1, wordFrequencies2);

		return wordCloud;
	}

}
