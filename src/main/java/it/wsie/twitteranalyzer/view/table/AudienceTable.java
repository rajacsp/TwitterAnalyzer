package it.wsie.twitteranalyzer.view.table;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;

import it.wsie.twitteranalyzer.model.tasks.classification.sentiment.Sentiment;

/**
 * @author Simone Papandrea
 *
 */
public class AudienceTable extends SimpleTable {

	private static final long serialVersionUID = 1L;

	public AudienceTable(AudienceTableModel model) {

		super(model);

		getTableHeader().setDefaultRenderer(new HeaderRenderer());
	}

	class HeaderRenderer extends GenericHeaderRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			Color color;
			Sentiment sentiment;

			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			sentiment = ((AudienceTableModel) getModel()).getSentiment();

			switch (sentiment) {

			case SUPPORTER:
				color = new Color(243, 98, 45);
				break;

			case OPPONENT:
				color = new Color(251, 167, 27);
				break;

			default:
				color = new Color(87, 183, 87);
				break;
			}

			this.setForeground(color);
			return this;
		}
	}

}
