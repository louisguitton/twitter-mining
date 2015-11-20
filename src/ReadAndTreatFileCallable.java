import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.Callable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class ReadAndTreatFileCallable implements Callable<Void>{

	File mFile;
	Context mContext;
	static int tweets = 0;
	public ReadAndTreatFileCallable(File f, Context c){
		mFile = f;
		mContext = c;
	}
	
	public static int getNumberTweets(){
		return tweets;
	}


	@Override
	public Void call(){
		long e0 = System.nanoTime();
		try {
			System.out.println("Reading file: " + mFile.getName());
			FileInputStream in = new FileInputStream(mFile);
			BufferedReader streamReader;
			streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			String inputStr;
			ArrayList<JSONObject> jsonObjects = new ArrayList<JSONObject>();
			JSONObject js;
			while ((inputStr = streamReader.readLine()) != null){
				js = (JSONObject) JSONValue.parse(inputStr.toString());
				jsonObjects.add(js);
			}
			JSONObject entities;
			JSONArray hashtags;
			ArrayList<String> tags = new ArrayList<String>();
			HashSet<String> duplicates = new HashSet<String>();
			for(JSONObject tweet : jsonObjects){
				if(tweet == null){
					System.out.println("null");
					continue;
				}
				entities = (JSONObject) tweet.get("entities");
				hashtags = (JSONArray) entities.get("hashtags");
				
				for(int i = 0; i < hashtags.size(); i++){
					String tweetText = ((String) ((JSONObject) hashtags.get(i)).get("text")).toLowerCase();
					if(!duplicates.contains(tweetText)){
						tags.add(tweetText);
						duplicates.add(tweetText);

					}
				}
				if(tags.size() <= 1){
					continue;
				}
				tweets++;

				/*String[] tagsArray = new String[tags.size()];
				tags.toArray(tagsArray);
				Arrays.sort(tagsArray);
				
				String s = ((new StringBuilder()).append(tagsArray)).toString();
				if(mContext.tagCombinationExists(s)){
					continue;
				}
				else{
					mContext.addTagCombination(s);
				}*/
				for(int i = 0; i < tags.size(); i++){
					String tag = tags.get(i);
					if(!mContext.doesVertexExist(tag)){
						mContext.addVertex(tag);
					}
					for(int j = i-1; j >= 0; j--){
						if(!mContext.doesEdgeExist(tag, tags.get(j))){
							mContext.addEdge(tag, tags.get(j));
						}
						else{
							mContext.incrementEdgeWeight(tag, tags.get(j));
						}
					}
				}
				tags.clear();
				duplicates.clear();
			}
			in.close();

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(ClassCastException e){
			e.printStackTrace();
		} catch(NullPointerException e){
			e.printStackTrace();
		}
		long e1 = System.nanoTime();
		double t = (e1-e0)/1.0e9;
		System.out.println("Finished reading file: " + mFile.getName() + " in " + t + " s");


		return null;

	}

}