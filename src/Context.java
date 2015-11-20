import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Context {
	
	private ExecutorService mExecutor;
	private HashSet<String> mTagsCombination = new HashSet<String>();
	private SimpleWeightedGraph<String, DefaultWeightedEdge> mGraph = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
	static HashMap<String, Integer> mTweetsPerTags = new HashMap<String, Integer>();

	public Context(){
		mExecutor = Executors.newFixedThreadPool(LocalConf.NB_CORES);	
	}
	
	
	public boolean tagCombinationExists(String s){
		synchronized(mTagsCombination){
			return mTagsCombination.contains(s);
		}
	}
	
	public void addTagCombination(String s){
		synchronized(mTagsCombination){
			mTagsCombination.add(s);
		}
	}
	
	public synchronized void addVertex(String tag){
		mGraph.addVertex(tag);
	}
	
	public synchronized boolean doesVertexExist(String tag){
		return mGraph.containsVertex(tag);
	}
	
	public synchronized void addEdge(String tag1, String tag2){
		//System.out.println(tag1 + "<->" + tag2);
		mGraph.addEdge(tag1, tag2);
	}
	
	public synchronized boolean doesEdgeExist(String tag1,String tag2){
		return mGraph.containsEdge(tag1, tag2);
	}
	
	public void incrementEdgeWeight(String tag1, String tag2){
		DefaultWeightedEdge e = mGraph.getEdge(tag1, tag2);
		synchronized(e){
			mGraph.setEdgeWeight(e, mGraph.getEdgeWeight(e)+1);
		}
	}
	
	
	
	
	public void serializeGraph(){
		File v = new File(LocalConf.SAVE_PATH + "vertexes");
		File e = new File(LocalConf.SAVE_PATH + "edges");
		try {
			BufferedWriter vout = new BufferedWriter(new FileWriter(v));
			BufferedWriter eout = new BufferedWriter(new FileWriter(e));
			WriteVertexes w1 = new WriteVertexes(vout);
			WriteEdges w2 = new WriteEdges(eout);
			ExecutorService executor = Executors.newFixedThreadPool(LocalConf.NB_CORES);
			executor.submit(w1);	
			executor.submit(w2);
			executor.shutdown();
			executor.awaitTermination(100, TimeUnit.SECONDS);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	
	public void readSerializedFileAndCreateGraph(File v, File e) throws IOException{
//		new ReadAndTreatFileCallable(v, c);
		System.out.println("Reading file: " + v.getName());
		FileInputStream in = new FileInputStream(v);
		BufferedReader streamReader;
		streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		String inputStr;
		while ((inputStr = streamReader.readLine()) != null){
			String[] split = inputStr.split(" ");
			this.addVertex(split[0]);
			mTweetsPerTags.put(split[0], Integer.parseInt(split[1]));
		}
		
		
//		new ReadAndTreatFileCallable(e, c);
		System.out.println("Reading file: " + e.getName());
		in = new FileInputStream(e);
		streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		while ((inputStr = streamReader.readLine()) != null){
			String [] line = inputStr.split(" ");
			String tag1 = line[0];
			String tag2 = line[1];
			double weight = Double.parseDouble(line[2]);
			DefaultWeightedEdge edge = this.mGraph.addEdge(tag1,tag2);
			this.mGraph.setEdgeWeight(edge, weight);
		}
	}
	
	public static void readAndSerializeData(){
		File file = new File(LocalConf.ROOT_PATH);
		File[] children = file.listFiles();
		Context c = new Context();
		for(File f : children){
			if(!f.isDirectory()){
				c.mExecutor.submit(new ReadAndTreatFileCallable(f, c));
			}
		}
		c.mExecutor.shutdown();
		try {
			c.mExecutor.awaitTermination(300, TimeUnit.SECONDS);
			System.out.println(ReadAndTreatFileCallable.getNumberTweets() + " tweets");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("nb of edges: " + c.mGraph.edgeSet().size());
		System.out.println("nb of vertexes: " + c.mGraph.vertexSet().size());
		c.serializeGraph();
	}
	
	public static void readAndFindDensest(){
		File v = new File(LocalConf.SAVE_PATH + "vertexes");
		File e = new File(LocalConf.SAVE_PATH + "edges");
		Context c = new Context();
		
		
		try {
			c.readSerializedFileAndCreateGraph(v,e);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		GraphManager gm = new GraphManager<String, DefaultWeightedEdge>(c.mGraph,0.5);
		gm.wrapper();
	}
	
	public static void main(String[] args) {
		readAndSerializeData();
		readAndFindDensest();
	}
	
	
	
	class WriteVertexes implements Callable<Void>{
		BufferedWriter mOut;
		public WriteVertexes(BufferedWriter out){
			mOut = out;
		}
		@Override
		public Void call() throws Exception {
			Collection<String> vertexes = mGraph.vertexSet();
			for(String vertex : vertexes){
				mOut.write(vertex+" " + ReadAndTreatFileCallable.getOccurrencesForTag(vertex)+"\n");
			}
			mOut.flush();
			mOut.close();
			return null;
		}
		
	}
	
	class WriteEdges implements Callable<Void>{
		BufferedWriter mOut;
		public WriteEdges(BufferedWriter out){
			mOut = out;
		}
		@Override
		public Void call() throws Exception {
			Collection<DefaultWeightedEdge> edges = mGraph.edgeSet();
			for(DefaultWeightedEdge e : edges){
				mOut.write(mGraph.getEdgeSource(e) + " " + mGraph.getEdgeTarget(e) + " " + mGraph.getEdgeWeight(e)+"\n");
			}
			mOut.flush();
			mOut.close();
			return null;
		}
		
	}
	public static int getOccurrencesForTag(String tag){
		return mTweetsPerTags.get(tag);
	}
}
