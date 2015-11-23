import java.util.ArrayList;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;

public class QuerySearch {
	
	Database pt_db;
	Database rt_db;

	public QuerySearch(Database pt_db, Database rt_db) {
		super();
		this.pt_db = pt_db;
		this.rt_db = rt_db;
	}
	
	public ArrayList<String> retrieve(String searchType, String searchKey, ArrayList<String> subquery_results) {
		System.out.println("searchType = " + searchType);
		System.out.println("searchKey = " + searchKey);
		
		ArrayList<String> results = new ArrayList<String>();
		Database std_db;
		Cursor cursor;
		try {
		    // Create DatabaseEntry objects
		    // searchKey is some String.
		    DatabaseEntry theKey = new DatabaseEntry(searchKey.getBytes("UTF-8"));
		    DatabaseEntry theData = new DatabaseEntry();
		    
		    if (searchType.equals("p")) {
		    	std_db = pt_db;
		    } else {
		    	std_db = rt_db;
		    }
		    
		    System.out.println("Database name being used: " + std_db.getDatabaseName());
	    	System.out.print("Previous subquery results: " );
		    for (String s: subquery_results) {
		    	System.out.print(s + ", ");
		    }

		    // Open a cursor using a database handle
		    cursor = std_db.openCursor(null, null);

		    // Position the cursor
		    // Ignoring the return value for clarity
		    
		    OperationStatus retVal;
		    
		    // first subquery in the overall query
		    if (subquery_results.size() == 0) {
		    	retVal = cursor.getSearchKey(theKey, theData, 
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
			            results.add(dataString);
			            System.out.println("dup "+ dataString);
			        }
			    } else {	//only one value
			    	String keyString = new String(theKey.getData());
		            String dataString = new String(theData.getData());
		            System.out.println("Key | Data : " +  keyString + " | " + 
		                               dataString + "");
		            results.add(dataString);
			    }
			    // Make sure to close the cursor
			}  //end if
		    
		    else {	//subquery, so use getSearchBoth 
		    	System.out.println("There was a previous subquery!");
		    	for(int i = 0; i < subquery_results.size(); i++) {
		    		theData = new DatabaseEntry(subquery_results.get(i).getBytes("UTF-8"));
		    		retVal = cursor.getSearchBoth(theKey, theData, 
	                        LockMode.DEFAULT);
		    		if (retVal == OperationStatus.SUCCESS) {	// it's a valid result
		    			String keyString = new String(theKey.getData());
				        String dataString = new String(theData.getData());
				        System.out.println("Key | Data : " +  keyString + " | " + 
				                               dataString + "");
				        results.add(dataString);
		    		}
		    		
				}
		    }
		    
		} catch (Exception e) {
			    // Exception handling goes here
		} 
		    
		    
		return results;
	}
	
	
}
