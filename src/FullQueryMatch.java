import java.io.FileNotFoundException;
import java.util.Scanner;

import com.sleepycat.db.*;

Public class FullMatchQuery{
	/* futue implementation will involve this class
	 * accessing databases which have already been created
	 * (ie. not creating a new database for each query) 
	 * 
	 * Each method returns the results which were found*/
	
	// take in database to search?
	public FullMatchQuery(){
		
	}
	
	public static void main(String[] arg) {
		
	}
	
	/* "p:" search using pt.idx*/
	public void ptermsQuery(String key){
		String searchKey = key;
	}
	
	/* "r:" search using rt.idx*/
	public void rtermsQuery(String key){
		String searchKey = key;
	}
	
	/* search both pt.idx and rt.idx*/
	public void ptermsAndRtermsQuery(String key){
		String searchKey = key;
	}
}