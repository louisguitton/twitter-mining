import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.Callable;

public class ReadAndTreatFileCallable implements Callable<Void>{
		
		File mFile;

		public ReadAndTreatFileCallable(File f){
			mFile = f;
		}
		
		
		@Override
		public Void call() throws Exception {
			FileInputStream in = new FileInputStream(mFile);
			return null;
		}
		
	}