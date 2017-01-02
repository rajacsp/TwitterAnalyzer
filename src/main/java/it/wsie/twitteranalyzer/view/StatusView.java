package it.wsie.twitteranalyzer.view;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * @author Simone Papandrea
 *
 */
public class StatusView extends JPanel {

	private static final long serialVersionUID = -1907090037027949230L;
	private final JLabel mStatus;


	public StatusView() {

		super(new BorderLayout());

		mStatus=new JLabel("",SwingConstants.RIGHT);
		initGUI();
	}

	private void initGUI() {

		add(mStatus);

	}

	public void setStatus(String status) {

		mStatus.setText(status);
	}
	
	public void reset() {

		mStatus.setText("");
	}
}
