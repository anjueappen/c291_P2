
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
				System.out.println("rscore: " + value);
				if (subquery_results.size() == 0) {
					if (c.getSearchKeyRange(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
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
					c.close();
					return ids;
				}

				else {
					System.out.println("GOT PREVIOUS SUBQUERY RESULTS");
					for(String id: subquery_results){
						System.out.println("ID:" + id);
						key = new DatabaseEntry(value.getBytes("UTF-8"));
						data = new DatabaseEntry(id.getBytes("UTF-8"));

						if (c.getSearchKeyRange(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
							if(operator.equals(">")){
								while(c.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS){
									System.out.println("Data: " + new String(data.getData()));
									if ( (new String(data.getData())).equals(id) ) {
										ids.add(new String(data.getData())); 
									}

								}
							} 
							else if(operator.equals("<")){
								while(c.getPrev(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS){
									if ( (new String(data.getData())).equals(id) ) {
										ids.add(new String(data.getData())); 
									}
								}
							}
						}	

					}
					c.close();
					return ids;
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
					c.close();
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
					c.close();
					return ids;
				}

			}

			/**
			 * RDATE
			 */

			else if(rangeType.equals("rdate")){
				c = rw_db.openCursor(null, null); 
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
				Long userInputDate = (fmt.parse(value)).getTime(); //date that user inputs for query
				if(subquery_results.size() == 0){
					DatabaseEntry outputKey = new DatabaseEntry();
					DatabaseEntry outputData = new DatabaseEntry();

					// we need to just iterate through ALL the reviews and compare pprice
					while (c.getNext(outputKey, outputData, LockMode.DEFAULT) ==
							OperationStatus.SUCCESS) {
						String keyString = new String(outputKey.getData());

						Long reviewDate = getRDate(new String(outputData.getData())); //date from the review in db

						if (reviewDate != null) {
							if(operator.equals(">") && (reviewDate > userInputDate)){
								ids.add(keyString); 
							}else if(operator.equals("<") && (reviewDate < userInputDate)){
								ids.add(keyString); 
							}
						}

						outputKey = new DatabaseEntry();
						outputData = new DatabaseEntry();
					}

					return ids;
				}else {
					DatabaseEntry inputKey = new DatabaseEntry(value.getBytes("UTF-8"));
					DatabaseEntry outputData = new DatabaseEntry();
					for(String id: subquery_results){ //iterate over all the valid ids from prev query
						inputKey.setData(id.getBytes()); //set the id inside the DataBaseEntry
						inputKey.setSize(id.length());
						c.getSearchKey(inputKey, outputData, LockMode.DEFAULT);  //get the full review- searching by ID
						Long reviewDate = getRDate(new String(outputData.getData())); 

						if (reviewDate != null) {
							if(operator.equals(">") && reviewDate > userInputDate){
								ids.add(id); 
							}else if(operator.equals("<") && reviewDate < userInputDate){
								ids.add(id); 
							}	
						}

					}
					return ids; 
				}

			}else{
				System.out.println("Range queries can only occur for score, product price (pprice) or review date (rdate)."); 
				c = rw_db.openCursor(null, null);  
				c.close();
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
		/*String[] pieces = review.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
		int i = 0; 
		for(String p: pieces){
			System.out.println(i + "........" + p);
			i++;
		}*/
		ArrayList<String> pieces = splitIntoParts(review); 
		System.out.println(pieces);
		System.out.println("");
		if(pieces.get(7).equals("unknown")){
			return null; 
		}
		return Long.parseLong(pieces.get(7).trim())*1000;  
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

	public ArrayList<String> splitIntoParts(String input) {
		ArrayList<String> result = new ArrayList<String>();
		int start = 0;
		boolean inQuotes = false;
		for (int current = 0; current < input.length(); current++) {
		    if (input.charAt(current) == '\"') inQuotes = !inQuotes; // toggle state
		    boolean atLastChar = (current == input.length() - 1);
		    if(atLastChar) result.add(input.substring(start));
		    else if (input.charAt(current) == ',' && !inQuotes) {
		        result.add(input.substring(start, current));
		        start = current + 1;
		    }
		}
		return result; 
		
	}

}
