package it.wsie.twitteranalyzer.view.table;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 * @author Simone Papandrea
 *
 */
public class CoOccurrencesTable extends NodesTable {

	private static final long serialVersionUID = 1L;

	public CoOccurrencesTable(AbstractTableModel model, int top) {

		super(model, top);

		getTableHeader().setDefaultRenderer(new HeaderRenderer());
		setDefaultRenderer(Integer.class, new IntegerFormatRenderer());
	}

	class HeaderRenderer extends GenericHeaderRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			setHorizontalAlignment(column <= 1 ? JLabel.LEFT : JLabel.CENTER);

			return this;
		}
	}

	class IntegerFormatRenderer extends DefaultRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			setHorizontalAlignment(JLabel.CENTER);

			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}

}
