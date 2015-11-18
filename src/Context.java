import java.io.File;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class Context {
	
	private ExecutorService mExecutor;
	private HashSet<String> mTagsCombination = new HashSet<String>();
	private SimpleWeightedGraph<String, DefaultEdge> mGraph = new SimpleWeightedGraph<String, DefaultEdge>(DefaultEdge.class);
	
	public Context(){
		mExecutor = Executors.newFixedThreadPool(LocalConf.NB_CORES);	
	}
	
	
	public synchronized void addVertex(String tag){
		mGraph.addVertex(tag);
	}
	
	public synchronized boolean doesVertexExist(String tag){
		return mGraph.containsVertex(tag);
	}
	
	public synchronized void addEdge(String tag1, String tag2){
		mGraph.addEdge(tag1, tag2);
	}
	
	public synchronized boolean doesEdgeExist(String tag1,String tag2){
		return mGraph.containsEdge(tag1, tag2);
	}
	
	public void incrementEdgeWeight(String tag1, String tag2){
		DefaultEdge e = mGraph.getEdge(tag1, tag2);
		synchronized(e){
			mGraph.setEdgeWeight(e, mGraph.getEdgeWeight(e)+1);
		}
	}
	
	public static void main(String[] args){
		File file = new File(LocalConf.ROOT_PATH);
		File[] children = file.listFiles();
		Context c = new Context();
		for(File f : children){
			if(!f.isDirectory()){
				c.mExecutor.submit(new ReadAndTreatFileCallable(f, c));
			}
		}
		c.mExecutor.shutdown();

	}
}
