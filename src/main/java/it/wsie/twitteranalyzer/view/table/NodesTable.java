package it.wsie.twitteranalyzer.view.table;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 * @author Simone Papandrea
 *
 */
public class NodesTable extends SimpleTable {

	private static final long serialVersionUID = 1L;
	private final int mTop;

	public NodesTable(AbstractTableModel model, int top) {

		super(model);

		setDefaultRenderer(String.class, new DefaultRenderer());
		setDefaultRenderer(Double.class, new DecimalFormatRenderer());
		getTableHeader().setDefaultRenderer(new HeaderRenderer());
		this.mTop = top;
	}

	class HeaderRenderer extends GenericHeaderRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			setHorizontalAlignment(column == 0 ? JLabel.LEFT : JLabel.CENTER);

			return this;
		}
	}

	class DefaultRenderer extends ObjectDefaultRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			Color color;

			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (row < mTop)
				color = new Color(238, 118, 0);
			else
				color = Color.BLACK;

			this.setForeground(color);

			return this;
		}
	}

	class DecimalFormatRenderer extends DefaultRenderer {

		private static final long serialVersionUID = 1L;
		private final DecimalFormat formatter = new DecimalFormat("0.###");

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			value = formatter.format((Number) value);
			setHorizontalAlignment(JLabel.CENTER);

			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}
}
