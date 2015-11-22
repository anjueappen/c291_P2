import java.io.FileNotFoundException;
import java.util.Scanner;

import com.sleepycat.db.*;

Public class FullMatchQuery{
	/* futue implementation will involve this class
	 * accessing databases which have already been created
	 * (ie. not creating a new database for each query) 
	 * 
	 * Each method returns the results which were found*/
	
	// take in database to search?
	public FullMatchQuery(){
		
	}
	
	public static void main(String[] arg) {
		DatabaseConfig dbConfig= new DatabaseConfig();
		//dbConfig.setSortedDuplicates(true); // to allow duplicate keys
		dbConfig.setType(DatabaseType.BTREE); // sets storage type to BTree
		dbConfig.setAllowCreate(true); // create a database if it doesn't exist
		
		Database std_db = null;
		
		
	}
	
	/* "p:" search using pt.idx*/
	public void ptermsQuery(String key){
		String searchKey = key;
		
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
		    // Make sure to close the cursor
		} catch (Exception e) {
		    // Exception handling goes here
		} 
	}

	user_input.close();

	}
	
	/* "r:" search using rt.idx*/
	public void rtermsQuery(String key){
		String searchKey = key;
		
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
		    // Make sure to close the cursor
		} catch (Exception e) {
		    // Exception handling goes here
		} 
	}

	user_input.close();

	}
	
	/* search both pt.idx and rt.idx*/
	public void ptermsAndRtermsQuery(String key){
		String searchKey = key;
		
	}
}