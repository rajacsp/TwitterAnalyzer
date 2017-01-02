package it.wsie.twitteranalyzer.view.graph;

import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.google.common.base.Function;

import it.wsie.twitteranalyzer.model.tasks.crawling.Node;

/**
 * @author Simone Papandrea
 *
 */
class NodeIcon implements Function<Node, Icon> {

	private final String mDir;
	private final ImageIcon mIcon;
	private final int mSize;

	public NodeIcon(String dir, String icon, int size) {

		this.mDir = dir + File.separator;
		this.mIcon = new ImageIcon(mDir + icon);
		this.mSize = size;
	}


	@Override
	public Icon apply(Node t) {
		
		ImageIcon icon;
		String path;

		path = mDir + t.getUser().getID() + ".jpg";

		if (new File(path).exists())
			icon = new ImageIcon(path);
		else
			icon = mIcon;

		 return new ImageIcon(icon.getImage().getScaledInstance(mSize,mSize,  java.awt.Image.SCALE_SMOOTH));
	}
}
