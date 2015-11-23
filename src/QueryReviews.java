import java.util.ArrayList;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;

public class QueryReviews {
	protected Database rw_db;
	
	public QueryReviews(Database rw_db) {
		this.rw_db = rw_db;
	}
	
	public void getReviews(ArrayList<String> indices) {
		Cursor cursor = null;
		System.out.println("THESE ARE THE RESULTS OF YOUR QUERY: ");
		for (String searchKey: indices) {
			try {
				// Create DatabaseEntry objects
				// searchKey is some String.
				DatabaseEntry theKey = new DatabaseEntry(searchKey.getBytes("UTF-8"));
				DatabaseEntry theData = new DatabaseEntry();

				// Open a cursor using a database handle
				cursor = rw_db.openCursor(null, null);

				// Position the cursor
				// Ignoring the return value for clarity
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
					//System.out.println("Key | Data : " +  keyString + " | " + 
	                               //dataString + "");	
				
				// Make sure to close the cursor
			} catch (Exception e) {
				// Exception handling goes here
			} 
		}
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
