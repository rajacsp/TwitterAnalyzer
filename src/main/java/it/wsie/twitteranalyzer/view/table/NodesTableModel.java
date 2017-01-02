package it.wsie.twitteranalyzer.view.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.table.AbstractTableModel;

import it.wsie.twitteranalyzer.model.tasks.crawling.Node;

/**
 * @author Simone Papandrea
 *
 */
public class NodesTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private final static String[] COLUMNS = { "Nodes", "PageRank", "Centrality" };
	private final static Class<?>[] CLASSES = { String.class, Double.class, Double.class };
	private final List<Node> mNodes;

	public NodesTableModel(Collection<Node> nominators) {

		mNodes = new ArrayList<Node>(nominators);
		Collections.sort(mNodes, new PageRankComparator());
	}

	public Class<?> getColumnClass(int column) {

		return CLASSES[column];
	}

	@Override
	public int getColumnCount() {

		return COLUMNS.length;
	}

	@Override
	public String getColumnName(int column) {

		return COLUMNS[column];
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {

		Node user;
		Object value;

		user = mNodes.get(arg0);

		switch (arg1) {

		case 1:
			value = user.getRank();
			break;
		case 2:
			value = user.getCentrality();
			break;
		default:
			value = user.toString();
			break;
		}

		return value;
	}

	@Override
	public int getRowCount() {

		return mNodes.size();
	}

	public final int search(Node user) {

		return mNodes.indexOf(user);
	}

	private static class PageRankComparator implements Comparator<Node> {

		@Override
		public int compare(Node arg0, Node arg1) {

			double f0;
			double f1;

			f0 = arg0.getRank();
			f1 = arg1.getRank();

			if (f0 > f1)
				return -1;
			if (f0 < f1)
				return 1;
			else
				return arg0.getUser().compareTo(arg1.getUser());
		}

	}
}
