import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			if (arg[0].equals("rw.idx")) {
				dbConfig.setType(DatabaseType.HASH);
				dbConfig.setSortedDuplicates(false); // to allow duplicate keys
			}
			std_db = new Database(arg[0], null, dbConfig);
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
		        
		        foundKey = new DatabaseEntry();
		        foundData = new DatabaseEntry();
		    }
			
		} catch (DatabaseException de) {
		    System.err.println("Error accessing database." + de);
		}
		
		
		
		
		
		
		
		
		
		/**	
		 * http://download.oracle.com/berkeley-db/docs/je/1.5.3/GettingStartedGuide/Positioning.html
		 * Accessed November 16, 2015
		 * Working with duplicate objects
		 */
		
		/**
		 * TESTING PARTIAL MATCHES
		 */
		
		Scanner user_input = new Scanner( System.in );
		while(true) {
			String searchKey;
			System.out.print("What would you like to search for? [E to exit]:  ");
			searchKey = user_input.next( );
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
			    OperationStatus retVal = cursor.getSearchKeyRange(theKey, theData, 
			                                                 LockMode.DEFAULT);
			    
			    // Count the number of duplicates. If the count is greater than 1, 
			    // print the duplicates.
			    if (cursor.count() > 1) {
			    	System.out.println("Cursor count: " + cursor.count());
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
			    
			    theKey = new DatabaseEntry();
	            theData = new DatabaseEntry();
	            
			    // move cursor forward
		    	retVal = cursor.getNext(theKey, theData, LockMode.DEFAULT);
			    while (retVal == OperationStatus.SUCCESS) {
			    	String keyString = new String(theKey.getData());
		            String dataString = new String(theData.getData());
		            
		            if (keyString.startsWith(searchKey)) {
		            	 if (cursor.count() > 1) {
		 			    	System.out.println("Cursor count: " + cursor.count());
		 			        while (retVal == OperationStatus.SUCCESS) {
		 			            keyString = new String(theKey.getData());
		 			            dataString = new String(theData.getData());
		 			            System.out.println("Key | Data : " +  keyString + " | " + dataString + "");
		 			            retVal = cursor.getNextDup(theKey, theData, LockMode.DEFAULT);
		 			        }
		            	 } else {	// only one value
		 			    	keyString = new String(theKey.getData());
		 		            dataString = new String(theData.getData());
		 		            System.out.println("Key | Data : " +  keyString + " | " +  dataString + "");
		 			    }
		            } // done with all partial matches
		            else {
		            	break;
		            }
		            theKey = new DatabaseEntry();
		            theData = new DatabaseEntry();
		            retVal = cursor.getNext(theKey, theData, LockMode.DEFAULT);
			    }
			    // Make sure to close the cursor
			} catch (Exception e) {
			    // Exception handling goes here
			} 
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*
		Scanner user_input = new Scanner( System.in );
		while(true) {
			String searchKey;
			System.out.print("What would you like to search for? [E to exit]:  ");
			searchKey = user_input.next( );
			if (searchKey.equals("E")) {
			    break;
			}
			String dataKey = "4";
			try {
			    // Create DatabaseEntry objects
			    // searchKey is some String.
			    DatabaseEntry theKey = new DatabaseEntry(searchKey.getBytes("UTF-8"));
			    DatabaseEntry theData = new DatabaseEntry(dataKey.getBytes("UTF-8"));

			    // Open a cursor using a database handle
			    cursor = std_db.openCursor(null, null);

			    // Position the cursor
			    // Ignoring the return value for clarity
			    OperationStatus retVal = cursor.getSearchBoth(theKey, theData, 
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
		try {
			cursor.close();
			std_db.close();
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	*/
	}
	
}
