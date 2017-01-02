package it.wsie.twitteranalyzer.view;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.plaf.LayerUI;

/**
 * @author Simone Papandrea
 *
 */
public class ReportsView extends JPanel {

	private static final long serialVersionUID = -1907090037027949230L;
	private final WaitLayerUI mLayerUI;
	private final JPanel mCards;
	private final Map<String, Component> mComponents;

	public ReportsView() {

		super(new CardLayout());

		mLayerUI = new WaitLayerUI();
		mCards = new JPanel(new CardLayout());
		mComponents = new HashMap<String, Component>();
		initGUI();
	}

	private void initGUI() {

		JPanel panel = new JPanel();

		panel.setBackground(Color.WHITE);
		mCards.add(panel);
		add(new JLayer<JPanel>(mCards, mLayerUI));

	}

	public void addCard(String name, Component component) {

		mCards.add(name, component);
		mComponents.put(name, component);
	}

	public Component getCard(String name) {

		return mComponents.get(name);
	}

	public void showCard(String name) {

		if (!mComponents.containsKey(name)) {

			hideCard();
			mLayerUI.start();

		} else {

			mLayerUI.stop();
			((CardLayout) mCards.getLayout()).show(mCards, name);
		}
	}

	public void deleteCard(String name) {

		hideCard();

		if (mComponents.containsKey(name))
			mCards.remove(mComponents.get(name));
	}

	public void hideCard() {

		mLayerUI.stop();
		((CardLayout) mCards.getLayout()).first(mCards);
	}

	class WaitLayerUI extends LayerUI<JPanel> implements ActionListener {

		private static final long serialVersionUID = -7869407344398077765L;
		private boolean mIsRunning;
		private boolean mIsFadingOut;
		private Timer mTimer;
		private int mAngle;
		private int mFadeCount;
		private int mFadeLimit = 15;

		@Override
		public void paint(Graphics g, JComponent c) {

			Composite composite;
			Graphics2D g2D;
			final int w, h, s, cx, cy;
			float fade, scale;

			super.paint(g, c);

			if (!mIsRunning)
				return;

			w = c.getWidth();
			h = c.getHeight();
			s = Math.min(w, h) / 5;
			cx = w / 2;
			cy = h / 2;
			fade = (float) mFadeCount / (float) mFadeLimit;

			g2D = (Graphics2D) g.create();
			composite = g2D.getComposite();
			g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f * fade));
			g2D.fillRect(0, 0, w, h);
			g2D.setComposite(composite);
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2D.setStroke(new BasicStroke(s / 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2D.setPaint(Color.white);
			g2D.rotate(Math.PI * mAngle / 180, cx, cy);

			for (int i = 0; i < 12; i++) {
				scale = (11.0f - (float) i) / 11.0f;
				g2D.drawLine(cx + s, cy, cx + s * 2, cy);
				g2D.rotate(-Math.PI / 6, cx, cy);
				g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, scale * fade));
			}

			g2D.dispose();
		}

		public void actionPerformed(ActionEvent e) {

			if (mIsRunning) {

				firePropertyChange("tick", 0, 1);
				mAngle += 3;

				if (mAngle >= 360)
					mAngle = 0;

				if (mIsFadingOut) {
					if (--mFadeCount == 0) {
						mIsRunning = false;
						mTimer.stop();
					}
				} else if (mFadeCount < mFadeLimit)
					mFadeCount++;
			}
		}

		public void start() {

			final int fps = 24, tick;

			if (mIsRunning)
				return;

			tick = 1000 / fps;
			mIsRunning = true;
			mIsFadingOut = false;
			mFadeCount = 0;
			mTimer = new Timer(tick, this);
			mTimer.start();
		}

		public void stop() {

			mIsRunning = false;
			mIsFadingOut = true;
		}

		@Override
		public void applyPropertyChange(PropertyChangeEvent pce, @SuppressWarnings("rawtypes") JLayer l) {

			if ("tick".equals(pce.getPropertyName()))
				l.repaint();
		}
	}
}
