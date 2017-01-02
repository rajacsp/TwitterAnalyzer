package it.wsie.twitteranalyzer.model.tasks.crawling;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import edu.uci.ics.jung.algorithms.filters.FilterUtils;
import edu.uci.ics.jung.algorithms.scoring.DistanceCentralityScorer;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import it.wsie.twitteranalyzer.model.tasks.identification.User;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * @author Simone Papandrea
 *
 */
public class TwitterGraph implements Serializable {

	private static final long serialVersionUID = -1914210312860175851L;
	private final HashMap<String,Node> mNodes;
	private final DirectedSparseGraph<Node, DefaultEdge> mGraph;
	private final int mGraphIconSize;
	private final String mGraphDir;
	
	public TwitterGraph(Collection<User> users,String graphDir, int graphIconSize) {

		mGraph = new DirectedSparseGraph<Node, DefaultEdge>();
		mNodes = new HashMap<String,Node>();
		mGraphIconSize =  graphIconSize;
		mGraphDir= graphDir;
		
		Node node;
		
		for (User user : users){
		
			node=new Node(user);
			mGraph.addVertex(node);
			mNodes.put(user.getID(),node);
		}
	}

	void build(Twitter twitter, User user) throws TwitterException, InterruptedException {

		boolean repeat;
		Node node;
		String id;
		long  time=0,sleep;
		final long wait=15 * 60 * 1000;
		
		id = user.getID();
		node = mNodes.get(id);

		do {

			repeat = false;

			try {

				addRelations(node,twitter.getFriendsIDs(Long.valueOf(id), -1));
				downloadIcon(user);
				time=0;
				
			} catch (TwitterException ex) {

				switch (ex.getStatusCode()) {

				case 429:

					sleep=Math.max(wait-time,60000);
					time=System.currentTimeMillis();
					Thread.sleep(sleep);
					time=System.currentTimeMillis()-time;
					repeat = true;
					break;

				case 401:
				case 404:
					break;

				default:
					throw ex;
				}
			}

		} while (repeat);
		
	}

	private synchronized void addRelations(Node node,IDs friends){
		
		Node n;

		for (long friend : friends.getIDs()) {

			n=mNodes.get(String.valueOf(friend));
			
			if (n!=null)	
				mGraph.addEdge(new DefaultEdge(), node, n);	
		}
	}
	
	public DirectedSparseGraph<Node, DefaultEdge> findLargestCC() {

		ConnectivityInspector<Node, DefaultEdge> connInspector;
		SimpleDirectedGraph<Node, DefaultEdge> graph;
		List<Set<Node>> ccs;
		Set<Node> maxCC = null;
		long max = 0, size;

		graph = new SimpleDirectedGraph<Node, DefaultEdge>(DefaultEdge.class);

		for (Node node : mGraph.getVertices())
			graph.addVertex(node);

		for (DefaultEdge e : mGraph.getEdges())
			graph.addEdge(mGraph.getSource(e), mGraph.getDest(e));

		connInspector = new ConnectivityInspector<Node, DefaultEdge>(graph);
		ccs = connInspector.connectedSets();

		for (Set<Node> cc : ccs) {

			size = cc.size();

			if (size > max) {
				maxCC = cc;
				max = size;
			}
		}

		return FilterUtils.createInducedSubgraph(maxCC, mGraph);
	}

	public void pageRank() {

		PageRank<Node, DefaultEdge> ranker;

		ranker = new PageRank<Node, DefaultEdge>(mGraph, 0.1);
		ranker.evaluate();

		for (Node node : mGraph.getVertices())
			node.setRank(ranker.getVertexScore(node));
	}

	public void centrality() {

		DistanceCentralityScorer<Node, DefaultEdge> ranker;
		double centrality, d, min = Integer.MAX_VALUE, max = 0;

		ranker = new DistanceCentralityScorer<Node, DefaultEdge>(mGraph, true);

		for (Node node : mGraph.getVertices()) {

			centrality = ranker.getVertexScore(node);

			if (Double.isNaN(centrality))
				centrality = 0;

			if (centrality < min)
				min = centrality;

			if (centrality > max)
				max = centrality;

			node.setCentrality(centrality);
		}

		d = max - min;

		if (d > 0)
			for (Node node : mGraph.getVertices())
				node.setNormCentrality((node.getCentrality() - min) / d);

	}

	public DirectedSparseGraph<Node, DefaultEdge> getGraph() {

		return this.mGraph;
	}

	public Collection<Node> getNodes() {

		return mGraph.getVertices();
	}

	public void remove(Node node){
		
		mNodes.remove(node.getUser().getID());
		mGraph.removeVertex(node);
	}
	
	private void downloadIcon(User user) {

		URL url;
		BufferedImage image;
		Graphics2D g;

		try {
			url = new URL(user.getIcon());
			image = ImageIO.read(url);

			if (image != null) {
				g = new BufferedImage(mGraphIconSize, mGraphIconSize, image.getType()).createGraphics();
				g.drawImage(image, 0, 0, mGraphIconSize, mGraphIconSize, null);
				g.dispose();
				ImageIO.write(image, "jpg", new File(mGraphDir + File.separator + user.getID() + ".jpg"));
			}

		} catch (Exception e) {

		}
	}
}