
public class QueryInputParser {
	private static String PTERM= "p:(.)"; 
	private static String RTERM= "r:(.)"; 
	private static String DATE_RANGE = "([a-zA-Z]+)( greater than | less than | equals )([0-9]+/[0-9]+/[0-9]+)"; 
	private static String NUMBER_RANGE = "([a-zA-Z]+)( greater than | less than | equals )([0-9]+)"; 
	private static String WHOLE_WORD = ""; 
	private static String SUFFIX = "(%+)([a-zA-Z0-9\\.]+)([a-zA-Z0-9\\.]+| (?! )){2,10}"; 
	

}
