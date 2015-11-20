import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Executors;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class GraphManager<K, E> {

	private double mEpsilon = 1.0e-1;
	private SimpleWeightedGraph<K, E> mGraph;
	private HashSet<K> mRemovedVertices = new HashSet<K>();
	private HashSet<E> mRemovedEdges = new HashSet<E>();
	private HashSet<K> mMaxRemovedVertices = new HashSet<K>();
	private HashSet<E> mMaxRemovedEdges = new HashSet<E>();
	private HashMap<K, Integer> mWeights = new HashMap<K, Integer>();
	private double mDensity = 0;
	public GraphManager(SimpleWeightedGraph<K, E> g, double e) {
		mEpsilon = e;
		mGraph = g;
	}

	public double computeGraphDensity(){
		return getEdgesWeight()/(((double) mGraph.vertexSet().size() - mRemovedVertices.size())*2);
	}
	
	public int getEdgesWeight(){
		int out = 0;
		for(K v : mGraph.vertexSet()){
			if(!mRemovedVertices.contains(v)){
				out += computeNodeWeightedDegrees(v);
			}
		}
		return out;
	}
	
	public int computeNodeWeightedDegrees(K o) {
		Collection<E> edges = mGraph.edgesOf(o);
		int weight = 0;
		for(E edge : edges){
			if(!mRemovedEdges.contains(edge))
				weight += mGraph.getEdgeWeight(edge);
		}
		mWeights.put(o, weight);
		return weight;
	}

	public void findDensestSubgraph() {
		while(mRemovedVertices.size() < mGraph.vertexSet().size()){
			double density = computeGraphDensity();
			System.out.println("Density: " + density);
			if(density > mDensity){
				mDensity = density;
				mMaxRemovedVertices.clear();
				for(K v : mRemovedVertices){
					mMaxRemovedVertices.add(v);
				}
				
				mMaxRemovedEdges.clear();
				for(E e : mMaxRemovedEdges){
					mMaxRemovedEdges.add(e);
				}
			}
			for(K v : mGraph.vertexSet()){
				if(!mRemovedVertices.contains(v)){
					if(mWeights.get(v) < (1+mEpsilon)*density){
						mRemovedVertices.add(v);
						mRemovedEdges.addAll(mGraph.edgesOf(v));
					}
				}
			}
		}
	}

}
