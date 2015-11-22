import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;

public class SetupDB {
	//These are for the purposes of this class only
	private Cursor pt_cursor;
	private Cursor rt_cursor;
	private Cursor sc_cursor;
	private Cursor rw_cursor;


	Database pt_db;
	Database rt_db;
	Database sc_db;
	Database rw_db;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SetupDB setup = new SetupDB(); 
		setup.configureDatabasesAndCursors();

	}

	public void configureDatabasesAndCursors() {
		DatabaseConfig bTreeConfig= new DatabaseConfig();
		bTreeConfig.setType(DatabaseType.BTREE); // sets storage type to BTree
		bTreeConfig.setAllowCreate(true); // create a database if it doesn't exist

		DatabaseConfig hashConfig= new DatabaseConfig();
		hashConfig.setType(DatabaseType.HASH); // sets storage type to BTree
		hashConfig.setAllowCreate(true); // create a database if it doesn't exist
		hashConfig.setSortedDuplicates(false);

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
			ArrayList<Cursor> cursors = new ArrayList<Cursor>(Arrays.asList(pt_cursor, rt_cursor, sc_cursor, rw_cursor));
			for(Cursor cursor: cursors){
				while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) ==
						OperationStatus.SUCCESS) {
					String keyString = new String(foundKey.getData());
					String dataString = new String(foundData.getData());
					System.out.println("Key | Data : " + keyString + " | " + 
							dataString + "");

					foundKey = new DatabaseEntry();
					foundData = new DatabaseEntry();
				}
			}
			pt_cursor.close();
			rt_cursor.close();
			sc_cursor.close();
			rw_cursor.close();


		} catch (DatabaseException de) {
			System.err.println("Error accessing database." + de);
		}
	}
	
	public void closeDBs() {
		
		try {
			pt_db.close();
			sc_db.close();
			rt_db.close();
			rw_db.close(); 
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}




