package it.wsie.twitteranalyzer.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import it.wsie.twitteranalyzer.model.tasks.identification.SocialBuzz;

/**
 * @author Simone Papandrea
 *
 */
public class IdentificationView extends JPanel {

	private static final long serialVersionUID = -3892986705462225648L;

	public IdentificationView(SocialBuzz buzz) {

		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);
		initGUI(buzz);
	}

	private void initGUI(SocialBuzz buzz) {

		this.add(new JLabel(new ImageIcon("img/Twitter_Logo_Blue.png")));
		this.add(getLabel("Users : " + String.format("%,d", buzz.getUsersCount())));
		this.add(getLabel("Tweets : " + String.format("%,d", buzz.getTweetsCount())));

	}

	private static JLabel getLabel(String text) {

		JLabel label;

		label = new JLabel(text);
		label.setFont(new Font("Verdana", Font.BOLD, 40));
		label.setBorder(new EmptyBorder(0, 10, 0, 40));

		return label;
	}

}
