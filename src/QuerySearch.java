import java.util.ArrayList;
import java.util.Collections;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;

public class QuerySearch {

	Database pt_db;
	Database rt_db;

	public QuerySearch(Database pt_db, Database rt_db) {
		super();
		this.pt_db = pt_db;
		this.rt_db = rt_db;
	}
	
	public ArrayList<String> retrieve(String searchType, String searchKey, ArrayList<String> subquery_results) {
		System.out.println("searchType = " + searchType);
		System.out.println("searchKey = " + searchKey);
		
		if (searchKey.endsWith("%")) {
			return partialMatchQuery(searchType, searchKey.substring(0, searchKey.length()-1), subquery_results);
		}
		return query(searchType, searchKey, subquery_results);
	}
	
	public ArrayList<String> query(String searchType, String searchKey, ArrayList<String> subquery_results) {
		
		ArrayList<String> results = new ArrayList<String>();
		Database std_db = null;
		Cursor cursor;
		try {
		    // Create DatabaseEntry objects
		    // searchKey is some String.
		    DatabaseEntry theKey = new DatabaseEntry(searchKey.getBytes("UTF-8"));
		    DatabaseEntry theData = new DatabaseEntry();
		    
		    if (searchType.equals("p")) {
		    	std_db = pt_db;
		    } else if (searchType.equals("r")){
		    	std_db = rt_db;
		    }else{ // b
		    	// implement
		    	ArrayList<String> pResults = query("p", searchKey, subquery_results);
		    	ArrayList<String> rResults = query("r", searchKey, subquery_results);
		    	
		    	// get the final results, merge but no duplicates
		    	/*
		    	for(String id : rResults){
		    		if (!pResults.contains(id)){
		    			pResults.add(id);
		    		}
		    	}
				*/
		    	// TODO Kirsten needs to sort and check optimization
		    	// Ediz Turkoglu, Accessed 2015-11-23, 
		    	// http://stackoverflow.com/questions/9917787/merging-two-arraylists-into-a-new-arraylist-with-no-duplicates-and-in-order-in
		    	pResults.removeAll(rResults);
		    	pResults.addAll(rResults);
		    	
		    	return pResults;
		    }
		    
		    System.out.println("Database name being used: " + std_db.getDatabaseName());
	    	System.out.print("Previous subquery results: " );
		    for (String s: subquery_results) {
		    	System.out.print(s + ", ");
		    }

		    // Open a cursor using a database handle
		    cursor = std_db.openCursor(null, null);

		    // Position the cursor
		    // Ignoring the return value for clarity
		    
		    OperationStatus retVal;
		    
		    // first subquery in the overall query
		    if (subquery_results.size() == 0) {
		    	retVal = cursor.getSearchKey(theKey, theData, 
                        LockMode.DEFAULT);
		    	// Count the number of duplicates. If the count is greater than 1, 
			    // print the duplicates.
			    if (cursor.count() > 1) {
			        while (retVal == OperationStatus.SUCCESS) {
			            String keyString = new String(theKey.getData());
			            String dataString = new String(theData.getData());
			            System.out.println("Key | Data : " +  keyString + " | " + 
			                               dataString + "");
			   
			            retVal = cursor.getNextDup(theKey, theData, LockMode.DEFAULT);
			            if (!results.contains(dataString)) {
			            	results.add(dataString);
			            }
			            System.out.println(searchType + " dup "+ dataString);
			        }
			    } else {	//only one value
			    	String keyString = new String(theKey.getData());
		            String dataString = new String(theData.getData());
		            System.out.println("Key | Data : " +  keyString + " | " + 
		                               dataString + "");
		            if (!results.contains(dataString)) {
		            	results.add(dataString);
		            }
		            System.out.println(searchType + " sing "+ dataString);
			    }
			    // Make sure to close the cursor
			}  //end if
		    
		    else {	//subquery, so use getSearchBoth 
		    	System.out.println("There was a previous subquery!");
		    	for(int i = 0; i < subquery_results.size(); i++) {
		    		theData = new DatabaseEntry(subquery_results.get(i).getBytes("UTF-8"));
		    		retVal = cursor.getSearchBoth(theKey, theData, 
	                        LockMode.DEFAULT);
		    		if (retVal == OperationStatus.SUCCESS) {	// it's a valid result
		    			String keyString = new String(theKey.getData());
				        String dataString = new String(theData.getData());
				        System.out.println("Key | Data : " +  keyString + " | " + 
				                               dataString + "");
				        if (!results.contains(dataString)) {
			            	results.add(dataString);
			            }
		    		}
		    		
				}
		    }
		    
		} catch (Exception e) {
			    // Exception handling goes here
		} 
		    
		    
		return results;
	}
	
	public ArrayList<String> partialMatchQuery(String searchType, String searchKey, ArrayList<String> subquery_results) {
		System.out.println("Partial match query!");
		System.out.println("searchType = " + searchType);
		System.out.println("searchKey = " + searchKey);
		Cursor cursor = null;
		Database std_db = null;
		ArrayList<String> results = new ArrayList<String>();
		
		if (searchType.equals("p")) {
	    	std_db = pt_db;
	    } else if (searchType.equals("r")){
	    	std_db = rt_db;
	    }else{//b
	    	// implement
	    }
		try {
		    // Create DatabaseEntry objects
		    // searchKey is some String.
		    DatabaseEntry theKey = new DatabaseEntry(searchKey.getBytes("UTF-8"));
		    DatabaseEntry theData = new DatabaseEntry();

		    // Open a cursor using a database handle
		    cursor = std_db.openCursor(null, null);

		    // Position the cursor
		    // Ignoring the return value for clarity
		    OperationStatus retVal = cursor.getSearchKeyRange(theKey, theData, 
		                                                 LockMode.DEFAULT);
		    
		     
		    	// Count the number of duplicates. If the count is greater than 1, 
			    // print the duplicates.
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
			    
			    theKey = new DatabaseEntry();
	            theData = new DatabaseEntry();
	            
			    // move cursor forward
		    	retVal = cursor.getNext(theKey, theData, LockMode.DEFAULT);
			    while (retVal == OperationStatus.SUCCESS) {
			    	String keyString = new String(theKey.getData());
		            String dataString = new String(theData.getData());
		            
		            if (keyString.startsWith(searchKey)) {
		            	 if (cursor.count() > 1) {
		 			    	System.out.println("Cursor count: " + cursor.count());
		 			        while (retVal == OperationStatus.SUCCESS) {
		 			            keyString = new String(theKey.getData());
		 			            dataString = new String(theData.getData());
		 			            System.out.println("Key | Data : " +  keyString + " | " + dataString + "");
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
		 		            System.out.println("Key | Data : " +  keyString + " | " +  dataString + "");
		 			    }
		            } // done with all partial matches
		            else {
		            	break;
		            }
		            theKey = new DatabaseEntry();
		            theData = new DatabaseEntry();
		            retVal = cursor.getNext(theKey, theData, LockMode.DEFAULT);
			    } // first subquery
			
		    
		    // subquery, so use getSearchBoth
		    if ((subquery_results.size() > 0)) {
		    	subquery_results.retainAll(results);
		    	results = subquery_results;
		    }
		    
		    // Make sure to close the cursor
		} catch (Exception e) {
		    // Exception handling goes here
		} 
		
		return results;
	}	// end of partial match result function
	
}
