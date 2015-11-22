
import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseType;

public class QueryRange {
	public return_type name() {
		
	}

	public static void main(String args[]) {
	try {
		 
	       //database configuration

	       DatabaseConfig dbConfig = new DatabaseConfig();

	       //dbConfig.setErrorStream(System.err);
	       //dbConfig.setErrorPrefix("MyDbs");
	 
	       dbConfig.setType(DatabaseType.BTREE);
	       dbConfig.setAllowCreate(false);
	   
	      //database 

	      Database std_db = new Database("students.db", null, dbConfig);

	      //DatabaseEntry key,data;

	      DatabaseEntry key = new DatabaseEntry();
	      DatabaseEntry data = new DatabaseEntry();

	      //Other variables

	      String startName;
		  StringBuilder endName;// it's defined as a StringBuilder object since, we'll need to change some of the characters in it
		  Double mark;

	      int ret;
		  String aKey;
		  Double aData;

		  byte[] bytes = new byte[Double.SIZE];
	      //DatabaseEntry key = new DatabaseEntry(aKey.getBytes("UTF-8"));
	      //DatabaseEntry data = new DatabaseEntry(aData.getBytes("UTF-8"));

		  //bdb_sample obj = new bdb_sample();
		  //Outputting database
		  Cursor std_cursor = std_db.openCursor(null, null);
		  String answer;
		  do
		  {
			System.out.print("Enter a prefix on which the student's name is starting:");
			startName = readInp();

			System.out.print("Enter a prefix on which the student's name is ending:");
			endName = new StringBuilder(readInp());
			
			//change the last character, so that we create a criteria for stopping iterating through the keys
			endName.setCharAt(endName.length()-1,(char)(endName.charAt(endName.length()-1) + 1));
			
			key = new DatabaseEntry();
			data = new DatabaseEntry();
			
			key.setData(startName.getBytes());
			key.setSize(startName.length());
			
			//get the record that have the smallest key greater than or equal to the specified key
			if (std_cursor.getSearchKeyRange(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
			{
				aKey = new String(key.getData());
				// if there are no records that satisfying the range of prefixes
				if (endName.toString().compareTo(aKey)<=0)
				{
					System.out.println("No student satysfying the prefix was found");
					break;
				}
				aData = ByteBuffer.wrap(data.getData()).getDouble ();
				System.out.println("Student: " + aKey + ", Mark: " + aData.toString()); 
				key = new DatabaseEntry();
				data = new DatabaseEntry();
				//since BDB doesn't support range search in conventional sence, but BTREE keeps close keys together we can just
				//iterate till we get to the record that has a key, out of the specified range
				while (std_cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
				{
					
					aKey = new String(key.getData());
					aData = ByteBuffer.wrap(data.getData()).getDouble ();
					// if current key satisfies the range of prefixes
					if (endName.toString().compareTo(aKey)>0)
					{
						System.out.println("Student: " + aKey + ", Mark: " + aData.toString()); 
						key = new DatabaseEntry();
						data = new DatabaseEntry();
					}
					// if there are no records that satisfying the range of prefixes
					else
					{
						break;
					}
				}			
			}
			else
			{
				System.out.println("No student satysfying the prefix was found");
			}
			
			System.out.print("Do you want to start a new search(press y for yes):");
			answer = readInp();
			}
		  while(answer.compareTo("y")==0);
		  
		/*  if (std_cursor.getFirst(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
		  {
			aKey = new String(key.getData());
			aData = ByteBuffer.wrap(data.getData()).getDouble ();
			System.out.println("Student: " + aKey + ", Mark: " + aData.toString()); 
			key = new DatabaseEntry();
			data = new DatabaseEntry();
			while (std_cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
			{
				
				aKey = new String(key.getData());
				aData = ByteBuffer.wrap(data.getData()).getDouble ();
				System.out.println("Student: " + aKey + ", Mark: " + aData.toString()); 
						key = new DatabaseEntry();
			data = new DatabaseEntry();
			}
		  }
		  else
		  {
			System.out.println("The database is empty\n");  
		  }*/

		  std_cursor.close();
	       
	    std_db.close();

	  } // end of try 

	  catch (Exception ex) 
	   { ex.getMessage();} 
	}
}
