package it.wsie.twitteranalyzer.view.graph;

import java.awt.Color;
import org.jgrapht.graph.DefaultEdge;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import it.wsie.twitteranalyzer.model.tasks.crawling.Node;

/**
 * @author Simone Papandrea
 *
 */
public class SocialGraphVisualization extends VisualizationImageServer<Node, DefaultEdge>  {

	private static final long serialVersionUID = -9173479696513515894L;

	public SocialGraphVisualization(SocialGraphView graph) {

		super(graph.getGraphLayout(),graph.getGraphLayout().getSize());

		this.setRenderContext(graph.getRenderContext());
		this.setBackground(Color.WHITE);
		getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
	}
	}