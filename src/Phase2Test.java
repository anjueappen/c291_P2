import java.io.FileNotFoundException;

import com.sleepycat.db.*;

public class Phase2Test {
	
	public Phase2Test() {
		
	}
	
	
	public static void main(String[] arg) {
		DatabaseConfig dbConfig= new DatabaseConfig();
		//dbConfig.setSortedDuplicates(true); // to allow duplicate keys
		dbConfig.setType(DatabaseType.BTREE); // sets storage type to BTree
		dbConfig.setAllowCreate(true); // create a database if it doesn't exist
		
		Database std_db = null;
		try {
			std_db = new Database("sc.idx", null, dbConfig);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/**
		 * http://download.oracle.com/berkeley-db/docs/je/1.5.3/GettingStartedGuide/Positioning.html
		 * Accessed November 16, 2015
		 */
		Cursor cursor = null;
		try {
		    // Open the cursor. 
		    cursor = std_db.openCursor(null, null);

		    // Cursors need a pair of DatabaseEntry objects to operate. These hold
		    // the key and data found at any given position in the database.
		    DatabaseEntry foundKey = new DatabaseEntry();
		    DatabaseEntry foundData = new DatabaseEntry();

		    
		    // To iterate, just call getNext() until the last database record has been 
		    // read. All cursor operations return an OperationStatus, so just read 
		    // until we no longer see OperationStatus.SUCCESS
		    while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) ==
		        OperationStatus.SUCCESS) {
		        // getData() on the DatabaseEntry objects returns the byte array
		        // held by that object. We use this to get a String value. If the
		        // DatabaseEntry held a byte array representation of some other data
		        // type (such as a complex object) then this operation would look 
		        // considerably different.
		        String keyString = new String(foundKey.getData());
		        String dataString = new String(foundData.getData());
		        System.out.println("Key | Data : " + keyString + " | " + 
		                       dataString + "");
		    }
		    cursor.close();
		} catch (DatabaseException de) {
		    System.err.println("Error accessing database." + de);
		}
		
		
		/**	
		 * http://download.oracle.com/berkeley-db/docs/je/1.5.3/GettingStartedGuide/Positioning.html
		 * Accessed November 16, 2015
		 * Working with duplicate objects
		 */
		/*
		try {
		    // Create DatabaseEntry objects
		    // searchKey is some String.
			String searchKey = "3.0,3";
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
		    }
		    // Make sure to close the cursor
			  cursor.close();
		} catch (Exception e) {
		    // Exception handling goes here
		} 
		*/
		
	}
}
