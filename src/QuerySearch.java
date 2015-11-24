import java.util.ArrayList;
import java.util.Collections;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;

/**
 * This class deals with term searches and returns results that contain the given search term in either the 
 * pterms, rterms or both pterms and rterms databases. Queries should have p:, r: or simply just the search terms 
 * in the query. Full matching is assumed unless the term ends with a % sign, then the query deals with 
 * partial matches. Queries with no p: or r: as prefixes return results that search both the pterms and 
 * rterms databases.
 */
public class QuerySearch {

	Database pt_db;
	Database rt_db;

	public QuerySearch(Database pt_db, Database rt_db) {
		super();
		this.pt_db = pt_db;
		this.rt_db = rt_db;
	}
	
	/**
	 * This function is called from Phase3. Delegates to full or partial matching functions as needed.
	 * @param searchType should be p, r, or b for pterms, rterms or both pterms and rterms respectively.
	 * @param searchKey the term to be searched.
	 * @param subquery_results if a subquery was performed before this function then the size of this
	 * 	array list must be greater than 0. This should be handled differently since the indices to be checked
	 *  have been narrowed down. Used for optimization.
	 * @return ArrayList of returns.
	 */
	public ArrayList<String> retrieve(String searchType, String searchKey, ArrayList<String> subquery_results) {
		System.out.println("searchType = " + searchType);
		System.out.println("searchKey = " + searchKey);
		
		// partial matching! remember to get rid of '%' when passing into query function
		if (searchKey.endsWith("%")) {
			return partialMatchQuery(searchType, searchKey.substring(0, searchKey.length()-1), subquery_results);
		}
		
		return query(searchType, searchKey, subquery_results);
	}
	
	/**
	 * For full matching.	
	 * @param searchType should be p, r, or b for pterms, rterms or both pterms and rterms respectively.
	 * @param searchKey the term to be searched.
	 * @param subquery_results if a subquery was performed before this function then the size of this
	 * 	array list must be greater than 0. This should be handled differently since the indices to be checked
	 *  have been narrowed down. Used for optimization.
	 * @return ArrayList of Strings of review record indices
	 */
	public ArrayList<String> query(String searchType, String searchKey, ArrayList<String> subquery_results) {
		// initialize
		ArrayList<String> results = new ArrayList<String>();
		Database std_db = null;
		Cursor cursor;
		try {
		    // Create DatabaseEntry objects
		    DatabaseEntry theKey = new DatabaseEntry(searchKey.getBytes("UTF-8"));
		    DatabaseEntry theData = new DatabaseEntry();
		    
		    if (searchType.equals("p")) {
		    	std_db = pt_db;
		    } else if (searchType.equals("r")){
		    	std_db = rt_db;
		    } else { // recursively find p results, r results, merge with no duplicates.
		    	// implement
		    	ArrayList<String> pResults = query("p", searchKey, subquery_results);
		    	ArrayList<String> rResults = query("r", searchKey, subquery_results);
		    	
		    	// Ediz Turkoglu, Accessed 2015-11-23, 
		    	// http://stackoverflow.com/questions/9917787/merging-two-arraylists-into-a-new-arraylist-with-no-duplicates-and-in-order-in
		    	pResults.removeAll(rResults);
		    	pResults.addAll(rResults);
		    	Collections.sort(pResults);
		    	
		    	return pResults;
		    }
		    
		    // TODO: remove this chunk
	    	System.out.print("Previous subquery results: " );
		    for (String s: subquery_results) {
		    	System.out.print(s + ", ");
		    }

		    // Open a cursor using a database handle
		    cursor = std_db.openCursor(null, null);

		    OperationStatus retVal;
		 
		    // do this if FIRST SUBQUERY in the overall query
		    if (subquery_results.size() == 0) {
		    	
		    	retVal = cursor.getSearchKey(theKey, theData, 
                        LockMode.DEFAULT);
		    	
		    	// Count the number of duplicates. If the count is greater than 1, 
			    // print the duplicates.
			    if (cursor.count() > 1) {
			        while (retVal == OperationStatus.SUCCESS) {
			            String keyString = new String(theKey.getData());
			            String dataString = new String(theData.getData());
			            // TODO: REMOVE PRINT LINE
			            System.out.println("Key | Data : " +  keyString + " | " + 
			                               dataString + "");
			   
			            retVal = cursor.getNextDup(theKey, theData, LockMode.DEFAULT);
			            if (!results.contains(dataString)) {
			            	results.add(dataString);
			            }
			            
			            // TODO: REMOVE PRINT LINE
			            System.out.println(searchType + " dup "+ dataString);
			        }
			    } else {	// only one value
			    	String keyString = new String(theKey.getData());
		            String dataString = new String(theData.getData());
		            // TODO: REMOVE PRINT LINE
		            System.out.println("Key | Data : " +  keyString + " | " + 
		                               dataString + "");
		            if (!results.contains(dataString)) {	// no duplicates in results!
		            	results.add(dataString);
		            }
		            // TODO: REMOVE PRINT LINE
		            System.out.println(searchType + " sing "+ dataString);
			    }
			}  //end if first subquery
		    
		    // THIS IS A SUBQUERY, results have been narrowed down, so use getSearchBoth 
		    else {	
		    	for(int i = 0; i < subquery_results.size(); i++) {
		    		theData = new DatabaseEntry(subquery_results.get(i).getBytes("UTF-8"));
		    		retVal = cursor.getSearchBoth(theKey, theData, 
	                        LockMode.DEFAULT);
		    		if (retVal == OperationStatus.SUCCESS) {	// it's a valid result
		    			String keyString = new String(theKey.getData());
				        String dataString = new String(theData.getData());
			            // TODO: REMOVE PRINT LINE
				        System.out.println("Key | Data : " +  keyString + " | " + 
				                               dataString + "");
				        if (!results.contains(dataString)) {	// no duplicates
			            	results.add(dataString);
			            }
		    		}
		    		
				}
		    } // end if subquery
		    
		} catch (Exception e) {
			
		}    
		    
		return results;
	}
	
	/**
	 * For partial matching.	
	 * @param searchType should be p, r, or b for pterms, rterms or both pterms and rterms respectively.
	 * @param searchKey the term to be searched.
	 * @param subquery_results if a subquery was performed before this function then the size of this
	 * 	array list must be greater than 0. This should be handled differently since the indices to be checked
	 *  have been narrowed down. Used for optimization.
	 * @return ArrayList of Strings of review record indices
	 */
	public ArrayList<String> partialMatchQuery(String searchType, String searchKey, ArrayList<String> subquery_results) {
		Cursor cursor = null;
		Database std_db = null;
		ArrayList<String> results = new ArrayList<String>();
		
		if (searchType.equals("p")) {
	    	std_db = pt_db;
	    } else if (searchType.equals("r")){
	    	std_db = rt_db;
	    } else {
	    	// fresh copies of subquery_results are needed because they get changed inside this function!
	    	ArrayList<String> copy_subqueries= new ArrayList<String>();
	    	copy_subqueries.addAll(subquery_results);
	    	ArrayList<String> pResults = partialMatchQuery("p", searchKey, copy_subqueries);
	    	
	    	copy_subqueries= new ArrayList<String>();
	    	copy_subqueries.addAll(subquery_results);
	    	ArrayList<String> rResults = partialMatchQuery("r", searchKey, copy_subqueries);

	    	pResults.removeAll(rResults);
	    	pResults.addAll(rResults);
	    	Collections.sort(pResults);
	    	
	    	return pResults;
	    }
		
		try {
		    // Create DatabaseEntry objects
		    DatabaseEntry theKey = new DatabaseEntry(searchKey.getBytes("UTF-8"));
		    DatabaseEntry theData = new DatabaseEntry();

		    // Open a cursor using a database handle
		    cursor = std_db.openCursor(null, null);

		    // Position the cursor
		    OperationStatus retVal = cursor.getSearchKeyRange(theKey, theData, 
		                                                 LockMode.DEFAULT);
		
		    // If we have a match, have to check if it starts with the given search key!
            if (new String(theKey.getData()).startsWith(searchKey)) {

			    if (cursor.count() > 1) {
			    	System.out.println("Cursor count: " + cursor.count());
			        while (retVal == OperationStatus.SUCCESS) {
			            String keyString = new String(theKey.getData());
			            String dataString = new String(theData.getData());
			            System.out.println("Key | Data : " +  keyString + " | " + 
			                               dataString + "");
			            if (!results.contains(dataString)) {
			            	results.add(dataString);
			            }
			            retVal = cursor.getNextDup(theKey, theData, LockMode.DEFAULT);
			        }
			    } else {	//only one value
			    	String keyString = new String(theKey.getData());
		            String dataString = new String(theData.getData());
		            System.out.println("Key | Data : " +  keyString + " | " + 
		                               dataString + "");			            
		            if (!results.contains(dataString)) {
		            	results.add(dataString);
		            }

			    }
			    
            } // end if
            
			theKey = new DatabaseEntry();
	        theData = new DatabaseEntry();
	            
			// Keep going through the database entry by entry and add if it partial matches
	        retVal = cursor.getNext(theKey, theData, LockMode.DEFAULT);
			while (retVal == OperationStatus.SUCCESS) {
				String keyString = new String(theKey.getData());
		        String dataString = new String(theData.getData());
		            
		        if (keyString.startsWith(searchKey)) {
		        	if (cursor.count() > 1) {
		 			    while (retVal == OperationStatus.SUCCESS) {
		 			    	keyString = new String(theKey.getData());
		 			        dataString = new String(theData.getData());
		 			        if (!results.contains(dataString)) {
					            results.add(dataString);
					        }
		 			        retVal = cursor.getNextDup(theKey, theData, LockMode.DEFAULT);
		 			    }
		            } else {	// only one value
		 			    keyString = new String(theKey.getData());
		 		        dataString = new String(theData.getData());
		 		        if (!results.contains(dataString)) {
		 		        	results.add(dataString);
				        }
		 			}
		        } // done with all partial matches
		        else {
		            break;
		        }
		        
		        theKey = new DatabaseEntry();
		        theData = new DatabaseEntry();
		        retVal = cursor.getNext(theKey, theData, LockMode.DEFAULT);
		   } // first subquery
			
		    
		   // subquery results are present so only retain results that exist in the subquery.
		   if ((subquery_results.size() > 0)) {
		    	subquery_results.retainAll(results);
		    	return subquery_results;
		   }
		    
		    // Make sure to close the cursor
		   cursor.close();
		} catch (Exception e) {
		    // Exception handling goes here
		} 
		
		return results;
	}	// end of partial match result function
	
}
