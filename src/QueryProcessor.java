import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class QueryProcessor {
	// (((r:|p:)[a-zA-Z0-9\.\/]+(\%?))|((rscore|pprice|rdate)\s*(<|>)\s*((\d{4}\/\d{2}\/\d{2})|\d(?:(\.\d+)?)))|([a-zA-Z0-9\.\/]+(\%?)))
	protected static String QUERYMATCHER = "(((r:|p:)[a-zA-Z0-9\\.\\/]+(\\%?))|((rscore|pprice|rdate)\\s*(<|>)\\s*((\\d{4}\\/\\d{2}\\/\\d{2})|\\d+(?:(\\.\\d+)?)))|([a-zA-Z0-9\\.\\/]+(\\%?)))";
	protected Pattern QUERY;	
	
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
		QUERY = Pattern.compile(QUERYMATCHER);
	}
	
	public ArrayList<String> parseQuery(String query) {
		System.out.println("Parsing query: " + query);
		Matcher queryMatch = QUERY.matcher(query);
		ArrayList<String> queries = new ArrayList<String>();
		while(queryMatch.find()) {
			queries.add(queryMatch.group(1));
		}
		System.out.println("Matched queries: " + queries);
		queries = convertToValidQueries(queries);
		return queries;
	}
	
	public ArrayList<String> convertToValidQueries(ArrayList<String> queries) {
		ArrayList<String> validQueries = new ArrayList<String>();
		for (String q: queries) {
			String qtype = analyze(q);
			System.out.println("qtype: " + qtype);
			if (qtype.equals("range")) {
				if (q.contains(">")) {
					validQueries.add(q.split(">")[0].trim());
					validQueries.add(">");
					validQueries.add(q.split(">")[1].trim());
				} else {
					validQueries.add(q.split("<")[0].trim());
					validQueries.add("<");
					validQueries.add(q.split("<")[1].trim());
				}
			} else {
				validQueries.add(q);
			}
		}
		System.out.println("Valid queries: " + validQueries);
		return validQueries;
	}

	public String analyze(String query) {
		if(query.contains("p:") | query.contains("r:")) {
			return "search";
		}
		else if (query.startsWith(RSCOREMATCH) | 
				query.startsWith(PPRICEMATCH) | 
				query.startsWith(RDATEMATCH)) {
			return "range";
		} else {
			return "searchKey";
		}
	}
}
