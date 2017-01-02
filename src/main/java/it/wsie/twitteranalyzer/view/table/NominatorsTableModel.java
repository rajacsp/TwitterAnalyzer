package it.wsie.twitteranalyzer.view.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.table.AbstractTableModel;

import it.wsie.twitteranalyzer.model.tasks.partitioning.Nominator;

/**
 * @author Simone Papandrea
 *
 */
public class NominatorsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	public final static String[] COLUMNS = { "Users", "Influence", "Frequency", "PageRank", "Centrality" };
	public final static Class<?>[] CLASSES = { String.class, Double.class, Integer.class, Double.class, Double.class };
	private final List<Nominator> mNominators;

	public NominatorsTableModel(Collection<Nominator> nominators) {

		mNominators = new ArrayList<Nominator>(nominators);
		Collections.sort(mNominators, new InfluenceComparator());
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

		Nominator nominator;
		Object value;

		nominator = mNominators.get(arg0);

		switch (arg1) {

		case 1:
			value = nominator.getInfluence();
			break;
		case 2:
			value = nominator.getFrequency();
			break;
		case 3:
			value = nominator.getNode().getRank();
			break;
		case 4:
			value = nominator.getNode().getCentrality();
			break;
		default:
			value = nominator.toString();
			break;
		}

		return value;
	}

	private static class InfluenceComparator implements Comparator<Nominator> {

		@Override
		public int compare(Nominator arg0, Nominator arg1) {

			int result=Double.compare(arg1.getInfluence(), arg0.getInfluence());

			if(result==0)
				result= arg0.getNode().getUser().compareTo(arg1.getNode().getUser());
			
			return result;
		}
	}

	@Override
	public int getRowCount() {

		return mNominators.size();
	}
}
