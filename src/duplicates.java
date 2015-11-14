
import com.sleepycat.db.*;

public class duplicates{

public static void main(String[] args){

	try {
		//database configuration
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setType(DatabaseType.BTREE);
		dbConfig.setAllowCreate(true);
		dbConfig.setSortedDuplicates(true); // setting flag for apllowing duplicates
		
		//Create a database 
		Database std_db = new Database("alphabets.db", null, dbConfig);
		OperationStatus oprStatus;

		//Inserting Data into a database
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry data = new DatabaseEntry();

		//Other variables
		String id = "1";
		String name="I";
		data.setData(name.getBytes());
		data.setSize(name.length()); 
		key.setData(id.getBytes()); 
		key.setSize(id.length());
		oprStatus = std_db.put(null, key, data);

		id = "1";
		name="J";
		data.setData(name.getBytes());
		data.setSize(name.length()); 
		key.setData(id.getBytes()); 
		key.setSize(id.length());
		oprStatus = std_db.put(null, key, data);

		id = "1";
		name="K";
		data.setData(name.getBytes());
		data.setSize(name.length()); 
		key.setData(id.getBytes()); 
		key.setSize(id.length());
		oprStatus = std_db.put(null, key, data);

		// Closing the connection
    		std_db.close();

  } // end of try 

  catch (Exception ex) 
   { ex.getMessage();} 

}

}
