import java.util.regex.Pattern;

public class DataPrepResources {

	private static String PID_PATTERN = "product/productId: (.+)(product/title)"; 
	private static String TITLE_PATTERN = "product/title: (.+)(product/price)"; 
	private static String PRICE_PATTERN = "product/price: (.+)(review/userId)"; 
	private static String USERID_PATTERN = "review/userId: (.+)(review/profileName)"; 
	private static String PROFILENAME_PATTERN = "review/profileName: (.+)(review/helpfulness)"; 
	private static String HELPFULNESS_PATTERN = "review/helpfulness: (.+)(review/score)"; 
	private static String SCORE_PATTERN = "review/score: (.+)(review/time)"; 
	private static String TIME_PATTERN = "review/time: (.+)(review/summary)"; 
	private static String SUMMARY_PATTERN = "review/summary: (.+)(review/text)"; 
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
	
	public DataPrepResources() {
		
	}
}
