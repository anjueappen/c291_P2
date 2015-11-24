import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;

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
/**
 * Project phase three: take the user input and delegate each portion to appropriate Query class. 
 */
	public Phase3() {
		input = new Scanner(System.in);
		polling = true; 
		qp = new QueryProcessor();
		createDatabases();
		query_search = new QuerySearch(pt_db, rt_db);
		query_range = new QueryRange(pt_db, rt_db, sc_db, rw_db);
		query_reviews = new QueryReviews(rw_db);
	}
	
	public static void main(String[] args) {
		Phase3 p3 = new Phase3();
		p3.start();
	}
	
	public void start() {
		ArrayList<String> results = new ArrayList<String>();
		while (polling) {
			String query;			
			System.out.print("Enter a query. [E to exit]: ");
			query = input.nextLine();
			if (query.equals("E")) {
			    break;
			}
			try {
				results = parseInput(query);
				query_reviews.getReviews(results);
			} catch (Exception e) {
				// TODO handle
			}

		}
		
	}
	/**
	 * Determine the query type and delegate to query classes
	 * @param query
	 * @return List of review ids filtered from the queries. 
	 */
	// TODO go back and make case insensitive
	public ArrayList<String> parseInput(String query) {
		ArrayList<String> parsed_query = qp.parseQuery(query);
		String[] split_query = parsed_query.toArray(new String[parsed_query.size()]);
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
					results = query_search.retrieve("b", q, results);
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
		
		System.out.println("Finished creating databases.");
	}
}
