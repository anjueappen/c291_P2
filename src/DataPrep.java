import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;

import com.sleepycat.je.rep.RestartRequiredException;



public class DataPrep {
	
	private static DataPrepResources dp = new DataPrepResources(); 
    
	public static void main(String[] args) throws IOException { 
		Scanner console = new Scanner(System.in);
			 
	}
	
	private Review getNextReview(Scanner console) {
		String record = ""; 
		Review r = new Review(); 
		while(console.hasNextLine()){
			String line = console.nextLine().replaceAll("\"", "&quot;"); 
			if(line.equals("")){
				break; 
			}
			line = line.replaceAll("\\\\", "\\\\\\\\"); 
			record = record + line; 
			
			Matcher pid = dp.pid.matcher(record); 
			Matcher title = dp.title.matcher(record);
			Matcher price = dp.price.matcher(record); 
			Matcher uid = dp.uid.matcher(record);
			Matcher pname = dp.pname.matcher(record);
			Matcher help = dp.help.matcher(record);
			Matcher score = dp.score.matcher(record);
			Matcher time = dp.time.matcher(record);
			Matcher summary = dp.summary.matcher(record);
			Matcher text = dp.text.matcher(record);
			
			if(pid.find()){
				r.setProductId(pid.group(0));
			}
			if(title.find()){
				r.setProductTitle(title.group(0));
			}
			if(price.find()){
				r.setPrice(price.group(0));
			}
			if(uid.find()){
				r.setUserId(uid.group(0));	
			}
			if(pname.find()){
				r.setProfileName(pname.group(0));
			}
			if(help.find()){
				r.setHelpfulness(help.group(0));
			}
			if(score.find()){
				r.setScore(score.group(0));
			}
			if(time.find()){
				r.setTime(time.group(0));
			}
			if(summary.find()){
				r.setSummary(summary.group(0));
			}
			if(text.find()){
				r.setText(text.group(0));
			}
		}
		return r; 
	}
}
