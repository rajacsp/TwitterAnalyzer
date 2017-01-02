package it.wsie.twitteranalyzer.view.table;

import java.awt.Component;
import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;

/**
 * @author Simone Papandrea
 *
 */
public class ProgressiveHeader extends JList<Integer> {

	private static final long serialVersionUID = 1L;

	public ProgressiveHeader(JTable table) {

		this.setModel(new AbstractListModel<Integer>() {

			private static final long serialVersionUID = 1L;

			public int getSize() {
				return table.getRowCount();
			}

			public Integer getElementAt(int index) {
				return index + 1;
			}
		});

		setCellRenderer(new RowHeaderRenderer(table));

	}

	class RowHeaderRenderer extends JLabel implements ListCellRenderer<Integer> {

		private static final long serialVersionUID = 1L;

		RowHeaderRenderer(JTable table) {

			setHorizontalAlignment(CENTER);

		}

		@Override
		public Component getListCellRendererComponent(JList<? extends Integer> list, Integer value, int index,
				boolean isSelected, boolean cellHasFocus) {
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

}
