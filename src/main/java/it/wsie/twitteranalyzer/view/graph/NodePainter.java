package it.wsie.twitteranalyzer.view.graph;

import java.awt.Color;
import java.awt.Paint;
import com.google.common.base.Function;

import it.wsie.twitteranalyzer.model.tasks.crawling.Node;

/**
 * @author Simone Papandrea
 *
 */
class NodePainter implements Function<Node, Paint> {

	static final Color color = new Color(0, 128, 255);


	@Override
	public Paint apply(Node arg0) {
		
		return (color);
	}
}
