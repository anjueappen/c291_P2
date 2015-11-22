
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;

/**
 * Taken and modified from  https://eclass.srv.ualberta.ca/pluginfile.php/2334524/mod_page/content/13/bdb_range.java 
 * Accessed: Nov. 21, 2015
 * @author anju
 *
 */
//([a-zA-Z0-9\\.]+ |)(([a-zA-Z]+)( > | < )([0-9\\.]+)) 
public class QueryRange {


	Database pt_db;
	Database rt_db;
	Database sc_db;
	Database rw_db;

	public QueryRange(Database pt_db, Database rt_db, Database sc_db, Database rw_db) {
		super();
		this.pt_db = pt_db;
		this.rt_db = rt_db;
		this.sc_db = sc_db;
		this.rw_db = rw_db;
	}
	
	public void retrieve(String rangeType, String operator, String rangeKey) {
		System.out.println("rangeType = " + rangeType);
		System.out.println("operator = " + operator);
		System.out.println("rangeKey = " + rangeKey);
		query(rangeType, operator, rangeKey);	
	}

	public void query(String rangeType, String operator, String value) {
		DatabaseEntry key = new DatabaseEntry();
		key.setData(rangeType.getBytes());
		key.setSize(rangeType.length());

		DatabaseEntry data = new DatabaseEntry();
		data.setData(value.getBytes());
		data.setSize(value.length());

		Cursor c; 
		if(rangeType == "score"){
			c = sc_db.openCursor(null, null); 
		}else if (rangeType == "pprice" | rangeType == "rdate"){
			c = rw_db.openCursor(null, null); 
		}else{
			System.out.println("Range queries can only occur for score, product price (pprice) or review date (rdate)."); 
		}

		
		{
			try {
				ArrayList<String> ids = new ArrayList<>(); 
				if (c.getSearchKeyRange(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
				if(operator == "<"){
					//move cursor up to the value actually greater 

					if(c.getPrevNoDup(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS){
						ids.add(new String(key.getData())); 
					}

					while(c.getPrev(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS){
						ids.add(new String(key.getData())); 
					}
				}else if(operator == ">"){
					//move cursor down to the value actually greater 
					if(c.getNextNoDup(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS){
						ids.add(new String(key.getData())); 
					}
					while(c.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS){
						ids.add(new String(key.getData())); 
					}
				}
			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}

	}



	public void name() {

		try {
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
						System.out.println("No reviews satifying the range was found");
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
					System.out.println("No review satysfying the prefix was found");
				}

				answer = readInp("Do you want to start a new search(press y for yes):");
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
