import java.io.FileNotFoundException;
import java.util.Scanner;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;

public class SetupDB {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DatabaseConfig bTreeConfig= new DatabaseConfig();
		bTreeConfig.setType(DatabaseType.BTREE); // sets storage type to BTree
		bTreeConfig.setAllowCreate(true); // create a database if it doesn't exist

		DatabaseConfig hashConfig= new DatabaseConfig();
		hashConfig.setType(DatabaseType.HASH); // sets storage type to BTree
		hashConfig.setAllowCreate(true); // create a database if it doesn't exist
		hashConfig.setSortedDuplicates(false);

		Database pt_db = null;
		Database rt_db = null;
		Database sc_db = null;
		Database rw_db = null;
		try {
			pt_db = new Database("pt.idx", null, hashConfig);
			rt_db = new Database("rt.idx", null, hashConfig);
			sc_db = new Database("sc.idx", null, hashConfig);
			rw_db = new Database("rw.idx", null, bTreeConfig);
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

		Cursor pt_cursor = null;
		Cursor rt_cursor = null;
		Cursor sc_cursor = null;
		Cursor rw_cursor = null;
		try {
			// Open the cursor. 
			pt_cursor = pt_db.openCursor(null, null);
			rt_cursor = rt_db.openCursor(null, null);
			sc_cursor = sc_db.openCursor(null, null);
			rw_cursor = rw_db.openCursor(null, null);

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
		try {
			cursor.close();
			std_db.close();
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}


