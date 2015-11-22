
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
		return query(rangeType, operator, rangeKey);
	}

	public ArrayList<String> query(String rangeType, String operator, String value) {
		DatabaseEntry key = new DatabaseEntry();
		key.setData(rangeType.getBytes());
		key.setSize(rangeType.length());

		DatabaseEntry data = new DatabaseEntry();
		data.setData(value.getBytes());
		data.setSize(value.length());

		Cursor c; 
		ArrayList<String> ids = new ArrayList<>(); 

		try {
			if(rangeType == "rscore"){
				c = sc_db.openCursor(null, null); 
			}else if (rangeType == "pprice" ){
				c = rw_db.openCursor(null, null); 
			}else if(rangeType == "rdate"){
				c = rw_db.openCursor(null, null); 
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
				String date = String.valueOf(((Date) fmt.parse(value)).getTime()); 
				data.setData(date.getBytes());
				data.setSize(date.length());
				
			}else{
				System.out.println("Range queries can only occur for score, product price (pprice) or review date (rdate)."); 
				c = rw_db.openCursor(null, null);  
			}

			
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
			c.close();
			
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
