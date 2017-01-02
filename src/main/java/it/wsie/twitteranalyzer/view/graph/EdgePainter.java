package it.wsie.twitteranalyzer.view.graph;

import java.awt.Color;
import java.awt.Paint;
import org.jgrapht.graph.DefaultEdge;
import com.google.common.base.Function;

/**
 * @author Simone Papandrea
 *
 */
class EdgePainter implements Function<DefaultEdge, Paint> {

	static final Color color = new Color(182, 34, 180);


	@Override
	public Paint apply(DefaultEdge arg0) {
		
		return (color);
	}
}
