package it.wsie.twitteranalyzer.view.graph;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import org.jgrapht.graph.DefaultEdge;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.AbstractGraph;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import it.wsie.twitteranalyzer.model.tasks.crawling.Node;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;

/**
 * @author Simone Papandrea
 *
 */
public class SocialGraphView extends VisualizationViewer<Node, DefaultEdge> implements ItemListener {

	private static final long serialVersionUID = -9173479696513515894L;
	private final NodeStateListener mNodeStateListener;

	public SocialGraphView(AbstractGraph<Node, DefaultEdge> graph, NodeStateListener listener, String iconsDir,
			String iconDef, int iconSize) {

		super(new FRLayout<Node, DefaultEdge>(graph));

		this.mNodeStateListener = listener;
		initGUI(iconsDir, iconDef, iconSize);
	}

	private void initGUI(String iconsDir, String iconDef, int iconSize) {

		RenderContext<Node, DefaultEdge> renderContext;
		PluggableGraphMouse pgm;

		pgm = new PluggableGraphMouse();
		pgm.add(new PickingGraphMousePlugin<Node, DefaultEdge>());
		pgm.add(new TranslatingGraphMousePlugin());
		pgm.add(new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, 1 / 1.1f, 1.1f));
		setGraphMouse(pgm);

		renderContext = getRenderContext();
		renderContext.setVertexFillPaintTransformer(new NodePainter());
		renderContext.setVertexLabelRenderer(new DefaultVertexLabelRenderer(new Color(0, 76, 0)));
		renderContext.setVertexIconTransformer(new NodeIcon(iconsDir, iconDef, iconSize));
		renderContext.setVertexShapeTransformer(new NodeShape<Node>(iconSize));
		renderContext.setEdgeDrawPaintTransformer(new EdgePainter());
		renderContext.setEdgeArrowPredicate(new RenderContext.UndirectedEdgeArrowPredicate<Node, DefaultEdge>());
		getPickedVertexState().addItemListener(this);
		getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		setAlignmentX(Component.CENTER_ALIGNMENT);
		setAlignmentY(Component.CENTER_ALIGNMENT);
		// this.setBackground(Color.WHITE);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {

		Node user = (Node) e.getItem();

		mNodeStateListener.nodeStateChanged(user, getPickedVertexState().isPicked(user));
	}

	public interface NodeStateListener {

		public void nodeStateChanged(Node node, boolean selected);

	}
}
