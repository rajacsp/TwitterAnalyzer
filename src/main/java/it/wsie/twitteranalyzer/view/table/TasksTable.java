package it.wsie.twitteranalyzer.view.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * @author Simone Papandrea
 *
 */
public class TasksTable extends SimpleTable {

	private static final long serialVersionUID = 1L;

	public TasksTable(AbstractTableModel model) {

		super(model);

		setDefaultRenderer(Object.class, new DefaultRenderer());
		setDefaultRenderer(JProgressBar.class, new ProgressBarRenderer());
		getTableHeader().setDefaultRenderer(new HeaderRenderer());
		getColumn(TasksTableModel.COLUMNS[0]).setPreferredWidth(200);
	}

	class DefaultRenderer extends ObjectDefaultRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (!isSelected) {

				Color color;

				if (row % 2 == 0)
					color = new Color(241, 241, 241);
				else
					color = Color.WHITE;
				setBackground(color);
			}

			setHorizontalAlignment(column == 2 ? JLabel.CENTER : JLabel.LEFT);

			return this;
		}
	}

	class HeaderRenderer extends GenericHeaderRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			setHorizontalAlignment(column == 1 ? JLabel.CENTER : JLabel.LEFT);

			return this;
		}
	}

	class ProgressBarRenderer extends JProgressBar implements TableCellRenderer {

		private static final long serialVersionUID = 1L;

		ProgressBarRenderer() {

			super(0, 100);

			setStringPainted(true);
			setBorderPainted(false);
			setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			Color color;
			int progress;

			progress = (int) ((Float) value).floatValue();

			if (progress < 0)
				color = new Color(255, 0, 0);
			else if (progress == 100)
				color = new Color(45, 184, 45);
			else
				color = new Color(255, 165, 0);

			setValue(Math.abs(progress));
			this.setForeground(color);

			if (isSelected)
				color = new Color(204, 229, 255);
			else if (row % 2 == 0)
				color = new Color(241, 241, 241);
			else
				color = Color.WHITE;

			this.setBackground(color);

			return this;
		}
	}

	@Override
	public String getToolTipText(MouseEvent e) {

		String tip = null;
		java.awt.Point point;
		int row, col;

		point = e.getPoint();
		row = rowAtPoint(point);
		col = convertColumnIndexToModel(columnAtPoint(point));

		if (col == 3)
			tip = ((TasksTableModel) getModel()).getTask(row).getError();

		return tip;
	}

}
