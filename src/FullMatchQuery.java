import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

import com.sleepycat.db.*;

public class FullMatchQuery{
	/* future implementation will involve this class
	 * accessing databases which have already been created
	 * (ie. not creating a new database for each query) 
	 * 
	 * Each method returns the results which were found*/
	
	// take in database to search?
	Database std_db;
	public FullMatchQuery(Database std_db){
		std_db = std_db;
	}
	
	public static void main(String[] arg) {
		DatabaseConfig dbConfig= new DatabaseConfig();
		//dbConfig.setSortedDuplicates(true); // to allow duplicate keys
		dbConfig.setType(DatabaseType.BTREE); // sets storage type to BTree
		dbConfig.setAllowCreate(true); // create a database if it doesn't exist
		
		Database std_db = null;
		FullMatchQuery fmq = new FullMatchQuery(std_db);
		fmq.parseMatchType();
		
	}
	
	/* Based on input, decides which method to call*/
	public void parseMatchType(){
		
	}
	
	/* "p:" search using pt.idx*/
	/*TESTING SO DID NOT ACCOUNT FOR DUPLICATES OPTIMIZATION YET
	 * Would be an if statement to check*/
	public void ptermsQuery(String key){
		String searchKey = key;
		ArrayList<String> reviewIds = new ArrayList<>();
		
		try {
		    // Create DatabaseEntry objects
		    // searchKey is some String.
		    DatabaseEntry theKey = new DatabaseEntry(searchKey.getBytes("UTF-8"));
		    DatabaseEntry theData = new DatabaseEntry();
			
		    // Open a cursor using a database handle
		    Cursor cursor = std_db.openCursor(null, null);

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
	
	/* "r:" search using rt.idx*/
	/*TESTING SO DID NOT ACCOUNT FOR DUPLICATES OPTIMIZATION YET
	 * Would be an if statement to check*/
	public void rtermsQuery(String key){
		String searchKey = key;
		ArrayList<String> reviewIds = new ArrayList<>();
		
		try {
		    // Create DatabaseEntry objects
		    // searchKey is some String.
		    DatabaseEntry theKey = new DatabaseEntry(searchKey.getBytes("UTF-8"));
		    DatabaseEntry theData = new DatabaseEntry();
			
		    // Open a cursor using a database handle
		    Cursor cursor = std_db.openCursor(null, null);

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
		            // add reviewId
		            reviewIds.add(dataString);
		            
		            retVal = cursor.getNextDup(theKey, theData, LockMode.DEFAULT);
		        }
		    } else {	//only one value
		    	String keyString = new String(theKey.getData());
	            String dataString = new String(theData.getData());
	            System.out.println("Key | Data : " +  keyString + " | " + 
	                               dataString + "");
	            reviewIds.add(dataString);
		    }
		    // Make sure to close the cursor
		} catch (Exception e) {
		    // Exception handling goes here
		} 
	}


	/* search both pt.idx and rt.idx*/
	public void ptermsAndRtermsQuery(String key){
		String searchKey = key;
	
	}
	
	/* Display results of search
	 * Use reviewIds to find the reviews to display.
	 * Use Review class to format display.*/
	public void displayResults(){
		
	}
}
