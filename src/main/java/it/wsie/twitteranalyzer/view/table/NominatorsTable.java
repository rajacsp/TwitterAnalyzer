package it.wsie.twitteranalyzer.view.table;

import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 * @author Simone Papandrea
 *
 */
public class NominatorsTable extends NodesTable {

	private static final long serialVersionUID = 1L;

	public NominatorsTable(AbstractTableModel model, int top) {

		super(model, top);

		setDefaultRenderer(Integer.class, new IntegerFormatRenderer());

	}

	class IntegerFormatRenderer extends DefaultRenderer {

		private static final long serialVersionUID = 1L;
		private final DecimalFormat formatter = new DecimalFormat("0");

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			setHorizontalAlignment(JLabel.CENTER);
			value = formatter.format((Number) value);

			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}
}
