import java.util.ArrayList;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;

/**
 * This class takes an array of indices, accesses the review database to retrieve and print records.
 */
public class QueryReviews {
	protected Database rw_db;
	
	public QueryReviews(Database rw_db) {
		this.rw_db = rw_db;
	}
	
	/**
	 * Retrieves reviews from the database using the review index and prints in a readable format.
	 * @param indices for results of a query
	 */
	public void getReviews(ArrayList<String> indices) {
		Cursor cursor = null;
		if (indices.size() == 0) {
			System.out.println("NO RESULTS FOUND. PLEASE TRY AGAIN.");
			return;
		}
		
		System.out.println("THESE ARE THE FULL RESULTS OF YOUR QUERY: ");
		for (String searchKey: indices) {
			try {
				// Create DatabaseEntry objects
				DatabaseEntry theKey = new DatabaseEntry(searchKey.getBytes("UTF-8"));
				DatabaseEntry theData = new DatabaseEntry(searchKey.getBytes("UTF-8"));

				// Open a cursor using a database handle
				cursor = rw_db.openCursor(null, null);

				// Position the cursor
				OperationStatus retVal = cursor.getSearchKey(theKey, theData, 
		                                                 LockMode.DEFAULT);
		    
				String keyString = new String(theKey.getData());
				String dataString = new String(theData.getData());
				
				// Splitting string by commas except if inside quotation marks.
				// http://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes
				// Accessed November 22, 2015
				String[] split_data = dataString.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				System.out.println("-------------------");
				System.out.println(keyString);
				printData(split_data);
				System.out.println("-------------------");
				
			} catch (Exception e) { } 
		}
		System.out.println("FOUND " +  indices.size() + " RESULT(S)!");
		System.out.println("QUICK SUMMARY OF RECORDS FOUND AND THEIR INDEX " +  indices + ".");
	}
	
	public void printData(String[] values) {
		String[] fields = {"product_id", "product_title", "product_price", "userid", 
				"profile_name", "helpfulness", "review_score", "review_timestamp",
				"summary", "full_text"};
			
		for (int i = 0; i < fields.length; i++) {
			System.out.println(fields[i] + ": " + values[i]);
		}
	}
}
