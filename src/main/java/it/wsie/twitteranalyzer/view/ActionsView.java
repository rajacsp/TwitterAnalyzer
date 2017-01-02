package it.wsie.twitteranalyzer.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author Simone Papandrea
 *
 */
public class ActionsView extends JPanel {

	private static final long serialVersionUID = 1L;
	private final JButton mStart, mPause, mStop;

	public ActionsView() {

		super(new FlowLayout(FlowLayout.RIGHT));

		mStart = createButton("Start", new Color(45, 184, 45));
		mPause = createButton("Pause", new Color(255, 165, 0));
		mStop = createButton("Stop", new Color(255, 0, 0));

		this.createLayout();
	}

	private void createLayout() {

		setBackground(Color.WHITE);
		add(mStart, BorderLayout.PAGE_END);
		add(mPause, BorderLayout.PAGE_END);
		add(mStop, BorderLayout.PAGE_END);
	}

	private static JButton createButton(String action, Color color) {

		JButton button;

		button = new JButton(action);
		button.setBackground(color);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setOpaque(true);
		button.setBorderPainted(false);

		return button;
	}

	public void addActionListener(ActionListener listener) {

		mStart.addActionListener(listener);
		mStop.addActionListener(listener);
		mPause.addActionListener(listener);
	}

}
