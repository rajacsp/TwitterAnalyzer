package it.wsie.twitteranalyzer.view.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author Simone Papandrea
 *
 */
public class SimpleTable extends JTable {

	private static final long serialVersionUID = 1L;

	public SimpleTable(AbstractTableModel model) {

		super(model);

		setFont(new Font("Verdana", Font.PLAIN, 14));
		setRowHeight(30);
		setGridColor(Color.LIGHT_GRAY);
		getTableHeader().setDefaultRenderer(new GenericHeaderRenderer());
		setDefaultRenderer(Object.class, new ObjectDefaultRenderer());
		setRowSelectionAllowed(true);
		setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		setShowGrid(false);
		setIntercellSpacing(new Dimension(0, this.getRowMargin()));
		setShowHorizontalLines(true);
		setRowSelectionAllowed(true);
	}

	class ObjectDefaultRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			Color color;

			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (isSelected)
				color = new Color(204, 229, 255);
			else
				color = Color.WHITE;

			setBackground(color);
			setBorder(noFocusBorder);

			return this;
		}
	}

	class GenericHeaderRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			setBackground(Color.WHITE);
			setFont(new Font("Verdana", Font.BOLD, 14));
			setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

			return this;
		}
	}

}
