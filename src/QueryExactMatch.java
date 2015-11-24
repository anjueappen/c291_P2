import java.util.Scanner;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;
/**
 * Query class for exact String matches 
 * @author anju
 *
 */

public class QueryExactMatch {

	public static void main(String[] args) {

	}
	/**
	 * 
	 * @param std_db
	 */
	public void query(Database std_db) {
		Scanner user_input = new Scanner( System.in );
		Cursor cursor; 
		while(true) {
			String searchKey;
			System.out.print("Enter phrase to query by exact match[E to exit]:  ");
			searchKey = user_input.next();
			if (searchKey.equals("E")) {
				break;
			}
			try {
				// Create DatabaseEntry objects
				// searchKey is some String.
				DatabaseEntry theKey = new DatabaseEntry(searchKey.getBytes("UTF-8"));
				DatabaseEntry theData = new DatabaseEntry();

				// Open a cursor using a database handle
				cursor = std_db.openCursor(null, null);

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
				cursor.close();
				std_db.close();
				// Make sure to close the cursor
			} catch (Exception e) {
				// Exception handling goes here
			} 
			user_input.close();
		}
		
	}

}
