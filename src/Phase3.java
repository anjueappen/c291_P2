import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;

/**
 * PHASE 3: Takes user input, parses the query, delegates each subquery to appropriate Query class and prints results.
 */
public class Phase3 {
	protected static Scanner input;
	protected static Boolean polling;
	protected QueryProcessor qp;
	protected QuerySearch query_search;
	protected QueryRange query_range;
	protected QueryReviews query_reviews;
	
	protected Database rw_db;
	protected Database pt_db;
	protected Database rt_db;
	protected Database sc_db;

	public Phase3() {
		input = new Scanner(System.in);
		polling = true; 
		qp = new QueryProcessor();
		
		// create databases and pass them onto appropriate query classes.
		createDatabases();
		query_search = new QuerySearch(pt_db, rt_db);
		query_range = new QueryRange(pt_db, rt_db, sc_db, rw_db);
		query_reviews = new QueryReviews(rw_db);
	}
	
	public static void main(String[] args) {
		Phase3 p3 = new Phase3();
		p3.start();
	}
	
	/**
	 * This function is the main loop that asks the user for their query and returns results.
	 */
	public void start() {
		ArrayList<String> results = new ArrayList<String>();
		while (polling) {
			String query;			
			System.out.print("Enter a query. [E to exit]: ");
			query = input.nextLine();
			if (query.equals("E")) {
				try {
					pt_db.close();
					sc_db.close();
					rt_db.close();
					rw_db.close(); 
				} catch (DatabaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    break;
			}
			try {
				// User query must be parsed by the Query Processor. Convert to an array of strings to be analyzed.
				ArrayList<String> parsed_query = qp.parseQuery(query);
				String[] split_query = parsed_query.toArray(new String[parsed_query.size()]);
				results = analyze(split_query);
				
				// match to reviews, they will print.
				query_reviews.getReviews(results);
			} catch (Exception e) {
				System.out.println("Something went wrong.");
				e.printStackTrace();
			}

		}
		
	}
	
	/**
	 * Takes a query split into valid subqueries.
	 * Valid in this context means each element of the String array is a keyword with type search, range or searchKey.
	 * 		The type of keyword is determined by the Query Processor.
	 * 		Range keywords are always followed by an operator and a rangeKey.
	 * Range searches are completed after all other subqueries.
	 * @param split_query is an array of strings split into valid subqueries.
	 * @return an ArrayList of strings that correspond to review indices.
	 */
	public ArrayList<String> analyze(String[] split_query) {
		ArrayList<String> results = new ArrayList<String>();
		
		for (int i = 0;  i < split_query.length; ) {
			String q  = split_query[i];
			String q_type = qp.analyze(q);
			switch (q_type) {
				case "search":
					results = query_search.retrieve(q.substring(0,1), q.substring(2).toLowerCase(), results);
					break;
				case "range":
					i += 2;
					break;
				case "searchKey":
					results = query_search.retrieve("b", q.toLowerCase(), results);
					break;
				case "Error":
					System.out.println("Something's wrong");
					break;
				default:
					break;
			}
			i++;
		}
		
		for (int i = 0;  i < split_query.length; ) {
			String q  = split_query[i];
			String q_type = qp.analyze(q);
			if (q_type.equals("range")) {
				results = query_range.retrieve(q, split_query[i+1], split_query[i+2], results);
				i += 2;
			}
			i++;
		}
		
		return results;
	}
	
	/**
	 * Setup DBs with .idx files
	 */
	public void createDatabases() {
		DatabaseConfig dbConfig= new DatabaseConfig();
		DatabaseConfig rwConfig= new DatabaseConfig();
		
		//dbConfig.setSortedDuplicates(true); // to allow duplicate keys
		dbConfig.setType(DatabaseType.BTREE); // sets storage type to BTree
		dbConfig.setAllowCreate(true); // create a database if it doesn't exist
		
		rwConfig.setType(DatabaseType.HASH);
		rwConfig.setSortedDuplicates(false); // to allow duplicate keys
			
		try {
			rw_db = new Database("rw.idx", null, rwConfig);
			pt_db = new Database("pt.idx", null, dbConfig);
			rt_db = new Database("rt.idx", null, dbConfig);
			sc_db = new Database("sc.idx", null, dbConfig);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		
		System.out.println("Databases ready.");
	}
}
