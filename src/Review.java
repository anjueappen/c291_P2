import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//This is a Review Class
public class Review {
	private int recordId; 
	private String productId; 
	private String productTitle;
	private String price;
	private String userId;
	private String profileName;
	private String helpfulness;
	private String score;
	private String time;
	private String summary;
	private String text; 
	private ArrayList<String> pTerms;
	
	public Review() {

	}
	
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	public int getRecordId() {
		return recordId;
	}
	public void setRecordId(int recordId) {
		this.recordId = recordId; 
	}
	public String getProductTitle() {
		return productTitle;
	}
	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
		this.pTerms = calculatePTerms();
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public String getHelpfulness() {
		return helpfulness;
	}
	public void setHelpfulness(String helpfulness) {
		this.helpfulness = helpfulness;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * Finds the terms in the review's product title.
	 * Terms are of length 3 or more characters extracted from product titles. 
	 * A term is a consecutive sequence of alphanumeric and underscore '_' characters, i.e [0-9a-zA-Z_] 
	 * Terms are all lowercase.
	 * @return ArrayList<String> of product title terms.
	 */
	public ArrayList<String> calculatePTerms() {
		String pTitle = this.productTitle;
		ArrayList<String> pTerms = new ArrayList<String>();
		
		String PTERM_PATTERN = "[0-9a-zA-Z_]{3,}+"; 
		Pattern pterm = Pattern.compile(PTERM_PATTERN);
		Matcher matchPTerm = pterm.matcher(pTitle);
		
		while (matchPTerm.find()) {
			pTerms.add((matchPTerm.group()).toLowerCase());
		}
			
		return pTerms;
	}
	
	public ArrayList<String> getPTerms() {
		return this.pTerms;
	}

}
