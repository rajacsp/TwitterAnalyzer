package it.wsie.twitteranalyzer.view.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;

import it.wsie.twitteranalyzer.model.tasks.classification.Audience;
import it.wsie.twitteranalyzer.model.tasks.classification.sentiment.Sentiment;
import it.wsie.twitteranalyzer.model.tasks.identification.User;

/**
 * @author Simone Papandrea
 *
 */
public class AudienceTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private final List<User> mUsers;
	private final Audience mAudience;
	private Sentiment mSentiment;

	public AudienceTableModel(Audience audience) {

		mUsers = new ArrayList<User>();
		mAudience = audience;
		setAudienceSentiment(Sentiment.SUPPORTER);
	}

	public Class<?> getColumnClass(int column) {

		return String.class;
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {

		return mUsers.get(arg0);
	}

	@Override
	public int getColumnCount() {

		return 1;
	}

	@Override
	public int getRowCount() {

		return mAudience.getSize(mSentiment);
	}

	@Override
	public String getColumnName(int column) {

		return mSentiment.name();
	}

	public void setAudienceSentiment(Sentiment sentiment) {

		mSentiment = sentiment;
		mUsers.clear();
		mUsers.addAll(mAudience.get(sentiment));
		Collections.sort(mUsers);
		this.fireTableStructureChanged();
	}

	protected Sentiment getSentiment() {

		return this.mSentiment;
	}
}
