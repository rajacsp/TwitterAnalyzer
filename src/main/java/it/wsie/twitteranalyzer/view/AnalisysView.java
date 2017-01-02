package it.wsie.twitteranalyzer.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
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
public class AnalisysView extends JTabbedPane {

	private static final long serialVersionUID = -4670394166724624369L;
	private static final ColorPalette[] PALETTE = {

			new ColorPalette(new Color(0x011f4b), new Color(0x005b96), new Color(0x6497b1)),
			new ColorPalette(new Color(0x800080), new Color(0xbe29ec), new Color(0xd896ff)),
			new ColorPalette(new Color(0x006203), new Color(0x30cb00), new Color(0xa4fba6)),
			new ColorPalette(new Color(0xb80000), new Color(0xff0000), new Color(0xff5e5e)),
			new ColorPalette(new Color(0xff2d00), new Color(0xff6027), new Color(0xff9200)) };

	public AnalisysView(Map<Candidate, List<CoOccurrence>> coOccurrencesMap) {

		setBackground(Color.WHITE);
		initGUI(coOccurrencesMap);
	}

	private void initGUI(Map<Candidate, List<CoOccurrence>> coOccurrencesMap) {

		List<CoOccurrence> coOccurences;
		int i = 0;

		for (Entry<Candidate, List<CoOccurrence>> e : coOccurrencesMap.entrySet()) {

			coOccurences = e.getValue();

			if (coOccurences.size() > 0) {

				addTab(e.getKey().getName(), null, createCandidateTab(coOccurences, PALETTE[i]));
				i = (i + 1) % PALETTE.length;
			}
		}

	}

	private JPanel createCandidateTab(List<CoOccurrence> coOccurences, ColorPalette colorPalette) {

		JPanel panel;
		CoOccurrencesTableModel model;
		CoOccurrencesTable table;
		JScrollPane pane;
		ProgressiveHeader rowHeader;
		JLabel label;

		model = new CoOccurrencesTableModel(coOccurences);
		table = new CoOccurrencesTable(model, 10);

		rowHeader = new ProgressiveHeader(table);
		rowHeader.setFixedCellWidth(40);
		rowHeader.setFixedCellHeight(table.getRowHeight());

		pane = new JScrollPane(table);
		pane.setRowHeaderView(rowHeader);
		pane.setBorder(BorderFactory.createEmptyBorder());
		pane.setBackground(Color.WHITE);

		label = new JLabel(new ImageIcon(createWordCloud(coOccurences, 500, colorPalette).getBufferedImage()));
		label.setBackground(Color.WHITE);
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

		panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.add(pane, BorderLayout.CENTER);
		panel.add(label, BorderLayout.WEST);

		return panel;
	}

	private WordCloud createWordCloud(List<CoOccurrence> coOccurrences, int size, ColorPalette palette) {

		Dimension dimension;
		WordCloud wordCloud;
		List<WordFrequency> wordFrequencies;

		wordFrequencies = new ArrayList<WordFrequency>();

		for (CoOccurrence coOccurrence : coOccurrences)
			wordFrequencies.add(new WordFrequency(coOccurrence.getTerm1() + " " + coOccurrence.getTerm2(),
					(int) (coOccurrence.getJaccardIndex() * 100)));

		dimension = new Dimension(size, size);
		wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
		wordCloud.setPadding(2);
		wordCloud.setBackground(new CircleBackground(size / 2));
		wordCloud.setBackgroundColor(Color.WHITE);
		wordCloud.setColorPalette(palette);
		wordCloud.setFontScalar(new LinearFontScalar(10, 40));
		wordCloud.build(wordFrequencies);

		return wordCloud;
	}
}
