import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
	private HashSet<K> mDensestSubgraph = new HashSet<K>();
	
	
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
			int n  =mRemovedVertices.size();
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
					if(mWeights.get(v) < (1+(mGraph.vertexSet().size())/(mRemovedVertices.size()+300.))*density){
						mRemovedVertices.add(v);
						mRemovedEdges.addAll(mGraph.edgesOf(v));
					}
				}
			}
			if(n==mRemovedVertices.size()){
				break;
			}
		}
		for(K v : mGraph.vertexSet()){
			if(!mMaxRemovedVertices.contains(v)){
				System.out.println(v);
				mDensestSubgraph.add(v);
			}
		}
	}
	
	public void postProcessDensestSubgraph(int i){
		System.out.println("Writing file "+i);
		File d = new File(LocalConf.SAVE_PATH + "ParisJanuary"+i+".subgr");
		
		try {
			BufferedWriter dout = new BufferedWriter(new FileWriter(d));
			for(K v : mDensestSubgraph){
				dout.write(v.toString() + " ");
				
			}
			dout.write(mDensity + " ");
			dout.flush();
			dout.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	
	public void wrapper(){
		for(int i =1; i<11;i++){
			findDensestSubgraph();
			postProcessDensestSubgraph(i);
			for(K v : this.mDensestSubgraph){
				for (K targetV: this.mDensestSubgraph){
					if(!(targetV==v)){
						mGraph.removeEdge(v, targetV);
					}
				}
				mGraph.removeVertex(v);
			}
			mRemovedVertices.clear();
			mRemovedEdges.clear();
			mMaxRemovedVertices.clear();
			mMaxRemovedEdges.clear();
			mDensestSubgraph.clear();
			mDensity=0;
		}
	}

}
