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
		for (String searchKey: indices) {
			System.out.print(searchKey + "   "); 
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
		    
				// Count the number of duplicates. If the count is greater than 1, 
				// print the duplicates.
				if (cursor.count() > 1) {
					while (retVal == OperationStatus.SUCCESS) {
						String keyString = new String(theKey.getData());
						String dataString = new String(theData.getData());
						System.out.println("Key | Data : " +  keyString + " | " + 
		                               dataString + "");
						retVal = cursor.getNextDup(theKey, theData, LockMode.DEFAULT);
					}
				} else {	//only one value
					String keyString = new String(theKey.getData());
					String dataString = new String(theData.getData());
					System.out.println("Key | Data : " +  keyString + " | " + 
	                               dataString + "");	
				}
				// Make sure to close the cursor
			} catch (Exception e) {
				// Exception handling goes here
			} 
		}
	}
}
