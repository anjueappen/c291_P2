
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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
		return query(rangeType, operator, rangeKey, subquery_results);
	}

	public ArrayList<String> query(String rangeType, String operator, String value, ArrayList<String> subquery_results) {
		Cursor c; 
		ArrayList<String> ids = new ArrayList<>(); 

		try {
			DatabaseEntry key = new DatabaseEntry(value.getBytes("UTF-8"));
			DatabaseEntry data = new DatabaseEntry();

			/**
			 * RSCORE
			 */
			if(rangeType.equals("rscore")){
				// keys of scores database are in the form of i.0 where i is an integer
				value = value.concat(".0");
				key = new DatabaseEntry(value.getBytes("UTF-8"));	// changed value so we need to change again
				c = sc_db.openCursor(null, null); 
				
				if (c.getSearchKey(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
					if(operator.equals("<")){
						//move cursor up to the value actually greater 

						if(c.getPrevNoDup(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS){
							ids.add(new String(data.getData())); 
						}

						while(c.getPrev(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS){
							ids.add(new String(data.getData())); 
						}
					}else if(operator.equals(">")){
						//move cursor down to the value actually greater 
						if(c.getNextNoDup(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS){
							ids.add(new String(data.getData())); 
						}
						while(c.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS){
							ids.add(new String(data.getData())); 
						}
					}
				} 
			}
			
			/**
			 * PPRICE
			 */
			else if (rangeType.equals("pprice") ){
				System.out.println("PRICE CHECK!");
				c = rw_db.openCursor(null, null); 
				
				// first subquery!
				if (subquery_results.size() == 0) {
					System.out.println("FIRST SUBQUERY");
					DatabaseEntry foundKey = new DatabaseEntry();
					DatabaseEntry foundData = new DatabaseEntry();
					
					// we need to just iterate through ALL the reviews and compare pprice
					while (c.getNext(foundKey, foundData, LockMode.DEFAULT) ==
							OperationStatus.SUCCESS) {
						String keyString = new String(foundKey.getData());

						Double price = getPPrice(new String(foundData.getData())); 
						
						if (price != null) {
							if(operator.equals(">") && price > Double.parseDouble(value)){
								ids.add(keyString); 
							}else if(operator.equals("<") && price < Double.parseDouble(value)){
								ids.add(keyString); 
							}
						}
						
						foundKey = new DatabaseEntry();
						foundData = new DatabaseEntry();
					}
					
					return ids;
				}
				
				else {
					System.out.println("GOT PREVIOUS SUBQUERY RESULTS");
					for(String id: subquery_results){
						System.out.println("ID:" + id);
						key = new DatabaseEntry(id.getBytes("UTF-8"));
						data = new DatabaseEntry(id.getBytes("UTF-8"));	
						if (c.getSearchKey(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
							Double price = getPPrice(new String(data.getData()));
							// price is null if pprice is "unknown"
							if (price != null) {
								if(operator.equals(">") && price > Double.parseDouble(value)){
									ids.add(id); 
								}else if(operator.equals("<") && price < Double.parseDouble(value)){
									ids.add(id); 
								}
							}
						}
					}
					
					return ids;
				}

			}

			/**
			 * RDATE
			 */
			
			else if(rangeType.equals("rdate")){
				c = rw_db.openCursor(null, null); 
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
				Long date = ((Date) fmt.parse(value)).getTime(); 

				for(String id: subquery_results){
					key.setData(id.getBytes());
					key.setSize(id.length());
					c.getSearchKey(key, data, LockMode.DEFAULT);  //get the full review 
					Long reviewDate = getRDate(new String(data.getData())); 
					
					if(operator.equals(">") && reviewDate > date){
						ids.add(id); 
					}else if(operator.equals("<") && reviewDate < date){
						ids.add(id); 
					}
				}
			}else{
				System.out.println("Range queries can only occur for score, product price (pprice) or review date (rdate)."); 
				c = rw_db.openCursor(null, null);  
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


	public Long getRDate(String review) {
		// split by comma except for if inside quotations
		String[] pieces = review.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
		if(pieces[8].equals("unknown")){
			return (long) -1; 
		}
		return Long.parseLong(pieces[8]);  
	}

	public Double getPPrice(String review){
		// split by comma except for if inside quotations
		String[] pieces = review.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
		if(pieces[2].contains("unknown")) {
			return null; 
		}
		return Double.parseDouble(pieces[2]);
	}
	public String getID(String review){
		String[] pieces = review.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
		return pieces[0];
	}


}
