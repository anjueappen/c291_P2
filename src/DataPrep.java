import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;

public class DataPrep {
	
	private static DataPrepResources dp = new DataPrepResources(); 
    
	public static void main(String[] args) throws IOException { 
		DataPrep d = new DataPrep(); 
		Scanner console = new Scanner(System.in);
		
		// reviews.txt
		ArrayList<Review> reviews = new ArrayList<Review>(); 
		while(console.hasNextLine()){
			reviews.add(d.getNextReview(console)); 
		}
		d.makeReviewsFile(reviews);
		
		// pterms.txt
		d.makePTermsFile(reviews);
		
		// rterms.txt
		d.makeRTermsFile(reviews);
	}
	
	/**
	 * This function creates the reviews.txt file from an ArrayList of reviews.
	 */
	private void makeReviewsFile(ArrayList<Review> reviews){
		try {
			PrintWriter writer = new PrintWriter("reviews.txt", "UTF-8");
			Integer index = 1; 
			for(Review review: reviews){
				writer.println(index.toString() + "," + makeReviewsTxtFormat(review));
				index++; 
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This function creates the review string to be included into reviews.txt.
	 * Called by makesReviewsFile().
	 */
	private String makeReviewsTxtFormat(Review r){
		String review = "";
		review += r.getProductId() + ", "; 
		review +=  "\"" + r.getProductTitle() + "\"" + ", ";
		review +=  r.getPrice() + ", "; 
		review +=  r.getUserId() + ", "; 
		review +=  "\"" + r.getProfileName() + "\"" + ", ";
		review +=  r.getHelpfulness() + ", "; 
		review +=  r.getScore() + ", ";
		review +=  r.getTime() + ", ";
		review +=  "\"" + r.getSummary() + "\"" + ", ";
		review +=  "\"" + r.getText() + "\"";
		return review; 
	}
	
	/**
	 * Returns a Review from line read from standard input. Uses Regex matching from DataPrepResources.
	 */
	private Review getNextReview(Scanner console) {
		String record = ""; 
		Review r = new Review(); 
		while(console.hasNextLine()){
			String line = console.nextLine().replaceAll("\"", "&quot;");
			if(line.equals("")){
				break; 
			}
			line = line.replaceAll("\\\\", "\\\\\\\\"); 
			record += line + '\n'; 
		}
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
			r.setProductId((pid.group(1)));
		}
		
		if(title.find()){
			r.setProductTitle((title.group(1)));	
		}
		if(price.find()){
			r.setPrice((price.group(1)));
		}
		if(uid.find()){
			r.setUserId((uid.group(1)));	
		}
		if(pname.find()){
			r.setProfileName((pname.group(1)));
		}
		if(help.find()){
			r.setHelpfulness((help.group(1)));
		}
		if(score.find()){
			r.setScore((score.group(1)));
		}
		if(time.find()){
			r.setTime((time.group(1)));
		}
		if(summary.find()){
			r.setSummary((summary.group(1)));
		}
		if(text.find()){
			r.setText((text.group(1)));
		}
		
		return r; 
	}

	/**
	 * This function creates the pterms.txt file from an ArrayList of reviews.
	 * The Review class takes care of calculating its own terms.
	 */
	private void makePTermsFile(ArrayList<Review> reviews) {
		try {
			PrintWriter writer = new PrintWriter("pterms.txt", "UTF-8");
			Integer index = 1; 
			for (Review review: reviews){
				ArrayList<String> pterms = review.getPTerms();
				for (String pterm: pterms) {
					writer.println(pterm + "," + index);
				}
				index++; 
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 *  This function creates the rterms.txt file from an ArrayList of reviews.
	 * The Review class takes care of calculating its own review terms.
	 */
	private void makeRTermsFile(ArrayList<Review> reviews) {
		try {
			PrintWriter writer = new PrintWriter("rterms.txt", "UTF-8");
			Integer index = 1; 
			for (Review review: reviews){
				ArrayList<String> rterms = review.getRTerms();
				for (String rterm: rterms) {
					writer.println(rterm + "," + index);
				}
				index++; 
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
