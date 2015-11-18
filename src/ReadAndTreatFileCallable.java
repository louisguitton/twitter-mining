import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ReadAndTreatFileCallable implements Callable<Void>{
		
		File mFile;
		Context mContext;
		
		public ReadAndTreatFileCallable(File f, Context c){
			mFile = f;
			mContext = c;
		}
		
		
		@Override
		public Void call() throws Exception {
			FileInputStream in = new FileInputStream(mFile);
			BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8")); 
			StringBuilder responseStrBuilder = new StringBuilder();

			String inputStr;
			ArrayList<JSONObject> jsonObjects = new ArrayList<JSONObject>();
			while ((inputStr = streamReader.readLine()) != null)
				jsonObjects.add((JSONObject) JSONValue.parse(responseStrBuilder.toString()));
			JSONObject entities;
			JSONArray hashtags;
			ArrayList<String> tags = new ArrayList<String>();
			for(JSONObject tweet : jsonObjects){
				entities = (JSONObject) tweet.get("entities");
				hashtags = (JSONArray) entities.get("hashtags");
				for(int i = 0; i < hashtags.size(); i++){
					tags.add((String) ((JSONObject) hashtags.get(i)).get("text"));
				}
				for(String tag : tags){
					

				}
			}
			in.close();
			return null;
		}
		
	}