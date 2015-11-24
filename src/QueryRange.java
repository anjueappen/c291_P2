import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;

/**
 * This class deals with range searches for rscore, pprice and rdate.
 * Taken and modified from  https://eclass.srv.ualberta.ca/pluginfile.php/2334524/mod_page/content/13/bdb_range.java 
 * Accessed: Nov. 21, 2015
 */
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
		return query(rangeType, operator, rangeKey, subquery_results);
	}

	/**
	 * This function 
	 * @param rangeType is either rscore, pprice or rdate.
	 * @param operator is either > or <
	 * @param value is a string that represents an integer, double or a date in YYYY/MM/DD format
	 * @param subquery_results if a subquery was performed before this function then the size of this
	 * 	array list must be greater than 0. This should be handled differently since the indices to be checked
	 *  have been narrowed down. Used for optimization.
	 * @return
	 */
	public ArrayList<String> query(String rangeType, String operator, String value, ArrayList<String> subquery_results) {
		Cursor c; 
		ArrayList<String> ids = new ArrayList<>(); 

		try {
			DatabaseEntry key = new DatabaseEntry(value.getBytes("UTF-8"));
			DatabaseEntry data = new DatabaseEntry();

			/**
			 * rangeType = RSCORE.
			 * Assumptions: maximum review score is 5.0. minimum review score is 1.0. review score cannot be unknown.
			 */
			if(rangeType.equals("rscore")) {
				// keys of scores database are in the form of i.0 where i is an integer
				value = value.concat(".0");
				key = new DatabaseEntry(value.getBytes("UTF-8"));	// changed value so we need to change again
				c = sc_db.openCursor(null, null); 

				Double MAX_RSCORE = 5.0;
				
				// if value is less than max rscore, everything in the database is valid!
				if ((Double.parseDouble(value) > MAX_RSCORE) && operator.equals("<")){
					key = new DatabaseEntry();
					data = new DatabaseEntry();
					c.getLast(key, data, LockMode.DEFAULT);
					ids.add(new String(data.getData()));
					key = new DatabaseEntry();
					data = new DatabaseEntry();
					while(c.getPrev(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS){
						ids.add(new String(data.getData())); 
						key = new DatabaseEntry();
						data = new DatabaseEntry();
					}
					if (subquery_results.size() == 0 ) {
						return ids;
					} else {
						return subquery_results;
					}
				} 
				
				// if greater than max rscore, nothing is valid
				else if ((Double.parseDouble(value) > MAX_RSCORE) && operator.equals(">")) {
					if (subquery_results.size() == 0 ) {
						return ids;
					}				
				}
				
				if (subquery_results.size() == 0) {
					// get the closest matching key
					if (c.getSearchKeyRange(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
						
						// does it actually equal the key we want?? i.e. does it exist in the data??
						// if not, then do not skip over this!
						if (!(new String(key.getData())).equals(value)) {
							ids.add(new String(data.getData())); 
						} 
						
						if(operator.equals("<")){
							// going up the database
							if ((new String(key.getData())).equals(value)) {
								if(c.getPrevNoDup(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS){
									ids.add(new String(data.getData())); 
									key = new DatabaseEntry();
									data = new DatabaseEntry();
								}
							}
							

							while(c.getPrev(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS){
								ids.add(new String(data.getData())); 
								key = new DatabaseEntry();
								data = new DatabaseEntry();
							}
							
						} else if(operator.equals(">")) {
							// going down the database
							if ((new String(key.getData())).equals(value)) {
								if(c.getNextNoDup(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS){
									ids.add(new String(data.getData()));
									key = new DatabaseEntry();
									data = new DatabaseEntry();
								}
							}
							
							while(c.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS){
								ids.add(new String(data.getData())); 
								key = new DatabaseEntry();
								data = new DatabaseEntry();
							}
						}
					} 
					c.close();
					return ids;
				}

				else {
					for(String id: subquery_results){
						key = new DatabaseEntry(value.getBytes("UTF-8"));
						data = new DatabaseEntry(id.getBytes("UTF-8"));

						if (c.getSearchKeyRange(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
							
							// does it actually equal the key we want?? i.e. does it exist in the data??
							// if not, then do not skip over this!
							if (!(new String(key.getData())).equals(value)) {
								ids.add(new String(data.getData())); 
							} 
							
							if(operator.equals(">")){
								while(c.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS){
									if ( (new String(data.getData())).equals(id) ) {
										ids.add(new String(data.getData())); 
										key = new DatabaseEntry(value.getBytes("UTF-8"));
										data = new DatabaseEntry(id.getBytes("UTF-8"));
									}

								}
							} 
							
							else if(operator.equals("<")){
								while(c.getPrev(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS){
									if ( (new String(data.getData())).equals(id) ) {
										ids.add(new String(data.getData())); 
										key = new DatabaseEntry(value.getBytes("UTF-8"));
										data = new DatabaseEntry(id.getBytes("UTF-8"));
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
			 * rangeType = PPRICE
			 */
			else if (rangeType.equals("pprice") ){
				c = rw_db.openCursor(null, null); 

				// first subquery!
				if (subquery_results.size() == 0) {
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
					for(String id: subquery_results){
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
			 * rangeType = RDATE
			 */

			else if(rangeType.equals("rdate")){
				c = rw_db.openCursor(null, null); 
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
				Long userInputDate = (fmt.parse(value)).getTime(); //date that user inputs for query
				if(subquery_results.size() == 0){
					DatabaseEntry outputKey = new DatabaseEntry();
					DatabaseEntry outputData = new DatabaseEntry();

					
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
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return ids; 
	}

	/**
	 * Returns the date of the review given the full text of review details.
	 * @param review Full text of review details
	 * @return date of the review in UNIX time format. null if date is unknown.
	 */
	public Long getRDate(String review) {
		ArrayList<String> pieces = splitIntoParts(review);
		if(pieces.get(7).equals("unknown")){
			return null; 
		}
		return Long.parseLong(pieces.get(7).trim())*1000;  
	}

	/**
	 * Returns the price of the product given the full text of review details.
	 * @param review
	 * @return price of the product as a double. null if price is unknown.
	 */
	public Double getPPrice(String review){
		// split by comma except for if inside quotations
		String[] pieces = review.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
		if(pieces[2].contains("unknown")) {
			return null; 
		}
		return Double.parseDouble(pieces[2]);
	}
	
	/**
	 * Returns the product id given the full text of review details.
	 * @param review
	 * @return id of the product as a string.
	 */
	public String getID(String review){
		String[] pieces = review.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
		return pieces[0];
	}

	/**
	 * Custom function to split review into parts.
	 * @param input, the review full text details
	 * @return an ArrayList of strings with each element being a field of the review.
	 */
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
