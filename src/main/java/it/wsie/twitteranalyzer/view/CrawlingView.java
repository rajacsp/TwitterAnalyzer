package it.wsie.twitteranalyzer.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.jgrapht.graph.DefaultEdge;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import it.wsie.twitteranalyzer.model.tasks.crawling.Node;
import it.wsie.twitteranalyzer.model.tasks.crawling.TwitterGraph;
import it.wsie.twitteranalyzer.view.graph.SocialGraphView;
import it.wsie.twitteranalyzer.view.graph.SocialGraphView.NodeStateListener;
import it.wsie.twitteranalyzer.view.table.NodesTable;
import it.wsie.twitteranalyzer.view.table.NodesTableModel;
import it.wsie.twitteranalyzer.view.table.ProgressiveHeader;

/**
 * @author Simone Papandrea
 *
 */
public class CrawlingView extends JPanel implements ItemListener {

	private static final long serialVersionUID = -3892986705462225648L;
	private final static String GRAPH = "Social graph";
	private final static String MAX_CC = "Max connected component";
	private JPanel mCards;

	public CrawlingView(TwitterGraph socialGraph, String iconsDir, String iconDef, int iconSize) {

		super(new BorderLayout());

		setBackground(Color.WHITE);
		initGUI(socialGraph, iconsDir, iconDef, iconSize);
	}

	private void initGUI(TwitterGraph socialGraph, String iconsDir, String iconDef, int iconSize) {

		JPanel panel, comboBoxPanel;
		JComboBox<String> comboBox;
		JScrollPane pane;
		NodesTable table;
		NodesTableModel model;
		ProgressiveHeader rowHeader;
		DirectedSparseGraph<Node, DefaultEdge> graph;
		NodeStateListener listener;
		SocialGraphView graphView, ccView;

		graph = socialGraph.getGraph();
		model = new NodesTableModel(graph.getVertices());
		table = new NodesTable(model, 10);

		rowHeader = new ProgressiveHeader(table);
		rowHeader.setFixedCellWidth(40);
		rowHeader.setFixedCellHeight(table.getRowHeight());

		pane = new JScrollPane(table);
		pane.setRowHeaderView(rowHeader);
		pane.setBorder(BorderFactory.createEmptyBorder());
		pane.setBackground(Color.WHITE);

		listener = new NodeStateListener() {
			@Override
			public void nodeStateChanged(Node node, boolean selected) {

				int row = -1;

				if (selected)
					row = model.search(node);

				if (row > -1) {
					table.setRowSelectionInterval(row, row);
					table.scrollRectToVisible(new Rectangle(table.getCellRect(row, 0, true)));
				} else {
					table.clearSelection();
					table.scrollRectToVisible(new Rectangle(table.getCellRect(0, 0, true)));
				}
			}
		};

		comboBox = new JComboBox<String>(new String[] { GRAPH, MAX_CC });
		comboBox.addItemListener(this);
		graphView = new SocialGraphView(graph, listener, iconsDir, iconDef, iconSize);
		ccView = new SocialGraphView(socialGraph.findLargestCC(), listener, iconsDir, iconDef, iconSize);
		
		mCards = new JPanel(new CardLayout());
		mCards.add(graphView, GRAPH);
		mCards.add(ccView, MAX_CC);

		comboBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		comboBoxPanel.add(comboBox);
		panel = new JPanel(new BorderLayout());
		panel.add(comboBoxPanel, BorderLayout.PAGE_END);
		panel.add(mCards, BorderLayout.CENTER);

		add(panel, BorderLayout.CENTER);
		add(pane, BorderLayout.EAST);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {

		CardLayout cl = (CardLayout) (mCards.getLayout());
		cl.show(mCards, (String) e.getItem());
	}

}
