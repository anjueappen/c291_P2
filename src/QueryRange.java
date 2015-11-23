
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
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

	public ArrayList<String> retrieve(String rangeType, String operator, String rangeKey, ArrayList<String> subquery_results) {
		System.out.println("rangeType = " + rangeType);
		System.out.println("operator = " + operator);
		System.out.println("rangeKey = " + rangeKey);
		/*		if (!subquery_results.isEmpty()){
			for(String id: subquery_results){

			}
		}*/
		return query(rangeType, operator, rangeKey);
	}

	public ArrayList<String> query(String rangeType, String operator, String value) {

		DatabaseEntry searchVal = new DatabaseEntry();
	
		DatabaseEntry output = new DatabaseEntry();
		//data.setData(value.getBytes());
		//data.setSize(value.length());

		Cursor c; 
		ArrayList<String> ids = new ArrayList<>(); 

		try {
			if(rangeType.equals("rscore")){
				//Set the key as the rscore value and data as empty
				if(value.indexOf('.') == -1){
					value = value + ".0"; 
					System.out.println("Modified value: " + value);
				}
				
				c = sc_db.openCursor(null, null); 
				searchVal.setData(value.getBytes());
				searchVal.setSize(value.length());

			}else if (rangeType.equals("pprice")){
				c = rw_db.openCursor(null, null); 
			
			}else if(rangeType.equals("rdate")){
				c = rw_db.openCursor(null, null); 
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
				String date = String.valueOf(((Date) fmt.parse(value)).getTime()); 
				output.setData(date.getBytes());
				output.setSize(date.length());

			}else{
				System.out.println("Range queries can only occur for score, product price (pprice) or review date (rdate)."); 
				c = rw_db.openCursor(null, null);  
			}


			if (c.getSearchKey(searchVal, output, LockMode.DEFAULT) == OperationStatus.SUCCESS){
				System.out.println("Starting search: " + output.getData());
				if(operator == "<"){
					//move cursor up to the value actually lesser 
					if(c.getPrevNoDup(searchVal, output, LockMode.DEFAULT) == OperationStatus.SUCCESS){
						System.out.println("Greater than: "+ searchVal.getData());
						ids.add(new String(searchVal.getData())); 
					}

					while(c.getPrev(searchVal, output, LockMode.DEFAULT) == OperationStatus.SUCCESS){
						System.out.println(searchVal.getData());
						ids.add(new String(searchVal.getData())); 
					}
				}else if(operator == ">"){

					//move cursor down to the value actually greater 
					if(c.getNextNoDup(searchVal, output, LockMode.DEFAULT) == OperationStatus.SUCCESS){
						ids.add(new String(searchVal.getData())); 
					}
					while(c.getNext(searchVal, output, LockMode.DEFAULT) == OperationStatus.SUCCESS){
						ids.add(new String(searchVal.getData())); 
					}
				}
				c.close();
			}

		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ids; 
	}

}
