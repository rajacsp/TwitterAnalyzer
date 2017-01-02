package it.wsie.twitteranalyzer.view.graph;

import java.awt.Rectangle;
import java.awt.Shape;
import com.google.common.base.Function;

import it.wsie.twitteranalyzer.model.tasks.crawling.Node;

/**
 * @author Simone Papandrea
 *
 */
class NodeShape<node> implements Function<Node, Shape> {

	private final int mSize;

	public NodeShape(int size) {

		mSize = size;
	}


	@Override
	public Shape apply(Node t) {
		
		return new Rectangle(mSize,mSize);
	}
}
