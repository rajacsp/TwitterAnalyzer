package it.wsie.twitteranalyzer.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import it.wsie.twitteranalyzer.model.tasks.identification.Candidate;
import it.wsie.twitteranalyzer.model.tasks.partitioning.Nominator;
import it.wsie.twitteranalyzer.model.tasks.partitioning.PoliticalParticipation;
import it.wsie.twitteranalyzer.view.table.NominatorsTable;
import it.wsie.twitteranalyzer.view.table.NominatorsTableModel;
import it.wsie.twitteranalyzer.view.table.ProgressiveHeader;

/**
 * @author Simone Papandrea
 *
 */
public class PartitioningView extends JTabbedPane {

	private static final long serialVersionUID = -3892986705462225648L;

	public PartitioningView(PoliticalParticipation partition) {

		setBackground(Color.WHITE);
		initGUI(partition);
	}

	private void initGUI(PoliticalParticipation partition) {

		for (Candidate candidate : partition)
			addTab(candidate.getName(), null, createCandidateTab(partition.getNominators(candidate)));
	}

	private JPanel createCandidateTab(List<Nominator> nominators) {

		JPanel panel;
		JScrollPane pane;
		NominatorsTable table;
		NominatorsTableModel model;
		ProgressiveHeader rowHeader;

		model = new NominatorsTableModel(nominators);
		table = new NominatorsTable(model, 10);
		// table.setTableHeader(null);

		rowHeader = new ProgressiveHeader(table);
		rowHeader.setFixedCellWidth(40);
		rowHeader.setFixedCellHeight(table.getRowHeight());

		pane = new JScrollPane(table);
		pane.setRowHeaderView(rowHeader);
		pane.setBorder(BorderFactory.createEmptyBorder());
		pane.setBackground(Color.WHITE);

		panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.add(pane, BorderLayout.CENTER);

		return panel;
	}
}
