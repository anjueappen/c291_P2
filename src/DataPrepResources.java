import java.util.regex.Pattern;

/**
 * Class that handles regex and pattern matching. 
 * Patterns assume that review fields read from standard in will be separated by a newline.
 *
 */
public class DataPrepResources {
	
	public DataPrepResources() {
		
	}
	
	private static String PID_PATTERN = "product/productId: (.+)(\n)"; 
	private static String TITLE_PATTERN = "product/title: (.+)(\n)"; 
	private static String PRICE_PATTERN = "product/price: (.+)(\n)"; 
	private static String USERID_PATTERN = "review/userId: (.+)(\n)"; 
	private static String PROFILENAME_PATTERN = "review/profileName: (.+)(\n)"; 
	private static String HELPFULNESS_PATTERN = "review/helpfulness: (.+)(\n)"; 
	private static String SCORE_PATTERN = "review/score: (.+)(\n)"; 
	private static String TIME_PATTERN = "review/time: (.+)(\n)"; 
	private static String SUMMARY_PATTERN = "review/summary: (.+)(\n)"; 
	private static String TEXT_PATTERN = "review/text: (.+)"; 
	
	public Pattern pid = Pattern.compile(PID_PATTERN);
	public Pattern title = Pattern.compile(TITLE_PATTERN);
	public Pattern price = Pattern.compile(PRICE_PATTERN);
	public Pattern uid = Pattern.compile(USERID_PATTERN);
	public Pattern pname = Pattern.compile(PROFILENAME_PATTERN);
	public Pattern help = Pattern.compile(HELPFULNESS_PATTERN);
	public Pattern score = Pattern.compile(SCORE_PATTERN);
	public Pattern time = Pattern.compile(TIME_PATTERN);
	public Pattern summary = Pattern.compile(SUMMARY_PATTERN);
	public Pattern text = Pattern.compile(TEXT_PATTERN);
	
}
