import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.events.StartDocument;

public class QueryProcessor {
	
	public void rangeSearchOptimized(){
		Pattern pattern = Pattern.compile("([a-zA-Z0-9\\.]+ |)(([a-zA-Z]+)( > | < )([0-9\\./]+))"); 
		Matcher m = pattern.matcher("r < 5 x > 6 x < 2015/09/07"); 
		while(m.find()){
			if(m.group(1) != ""){
				//Do this first 
			}else{
				//start with the ranges 
			}
		}
	}
	
	
	
	
	protected String PMATCH = "p:";
	protected String RMATCH = "r:";
	protected String RSCOREMATCH = "rscore";
	protected String PPRICEMATCH = "pprice";
	protected String RDATEMATCH = "rdate";
	protected String LESSTHAN = "<";
	protected String GREATERTHAN = ">";
	protected ArrayList<String> KEYWORDS = 
			new ArrayList<String>(Arrays.asList(
			PMATCH, RMATCH, RSCOREMATCH, PPRICEMATCH, RDATEMATCH, LESSTHAN, GREATERTHAN));
	protected ArrayList<String> RANGE = 
			new ArrayList<String>(Arrays.asList(
					RSCOREMATCH, PPRICEMATCH, RDATEMATCH));
	protected ArrayList<String> SEARCH = 
			new ArrayList<String>(Arrays.asList(
					PMATCH, RMATCH));
	
	public QueryProcessor() {
		
	}
	
	public String analyze(String query) {
		if(query.contains("p:") | query.contains("r:")) {
			return "search";
		}
		else if (RANGE.contains(query)) {
			return "range";
		} else {
			return "searchKey";
		}
	}
}
