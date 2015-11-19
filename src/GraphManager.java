import java.util.concurrent.Executors;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class GraphManager {

	private double mEpsilon;

	
	
	public GraphManager(double e) {
		mEpsilon = e;
	}

	public static double computeGraphDensity(SimpleWeightedGraph G){
		return G.edgeSet().size()/(double) G.vertexSet().size();
	}
	
	public static void computeNodeWeightedDegrees(SimpleWeightedGraph G) {

	}

	public static void findDensestSubgraph(SimpleWeightedGraph G) {

	}

}
