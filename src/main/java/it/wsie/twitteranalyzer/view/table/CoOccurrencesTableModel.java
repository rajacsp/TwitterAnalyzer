package it.wsie.twitteranalyzer.view.table;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

import it.wsie.twitteranalyzer.model.tasks.analisys.index.CoOccurrence;

/**
 * @author Simone Papandrea
 *
 */
public class CoOccurrencesTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private final List<CoOccurrence> mCoOccurrences;
	private static final String[] COLUMNS = { "Term1", "Term2", "Jaccard", "Frequency" };

	public CoOccurrencesTableModel(List<CoOccurrence> coOccurrence) {

		mCoOccurrences = new ArrayList<CoOccurrence>(coOccurrence);

	}

	public Class<?> getColumnClass(int column) {

		Class<?> cls;

		switch (column) {

		case 2:
			cls = Double.class;
			break;
		case 3:
			cls = Integer.class;
			break;
		default:
			cls = String.class;
		}

		return cls;
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {

		CoOccurrence coOccurrence;
		Object value = null;

		coOccurrence = mCoOccurrences.get(arg0);

		switch (arg1) {

		case 0:
			value = coOccurrence.getTerm1();
			break;
		case 1:
			value = coOccurrence.getTerm2();
			break;
		case 2:
			value = coOccurrence.getJaccardIndex();
			break;
		case 3:
			value = coOccurrence.getDocFreq();
		}

		return value;
	}

	@Override
	public int getColumnCount() {

		return COLUMNS.length;
	}

	@Override
	public int getRowCount() {

		return mCoOccurrences.size();
	}

	@Override
	public String getColumnName(int column) {

		return COLUMNS[column];
	}

}
