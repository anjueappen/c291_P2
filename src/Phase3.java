import java.io.FileNotFoundException;
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
	
	protected Database rw_db;
	protected Database pt_db;
	protected Database rt_db;
	protected Database sc_db;

	public Phase3() {
		input = new Scanner(System.in);
		polling = true; 
		qp = new QueryProcessor();
		query_search = new QuerySearch();
		query_range = new QueryRange(pt_db, rt_db, sc_db, rw_db);
		createDatabases();
	}
	
	public static void main(String[] args) {
		Phase3 p3 = new Phase3();
		p3.start();
	}
	
	public void start() {
		while (polling) {
			String query;			
			System.out.print("Enter a query. [E to exit]: ");
			query = input.nextLine();
			if (query.equals("E")) {
			    break;
			}
			try {
				parseInput(query);
			} catch (Exception e) {
				// TODO handle
			}

		}
		
	}

	public void parseInput(String query) {
		String[] split_query = query.split("\\s+");
		
		for (int i = 0;  i < split_query.length; ) {
			String q  = split_query[i];
			String q_type = qp.analyze(q);
			switch (q_type) {
				case "search":
					/*
					 * TODO: QuerySearch will take in arguments searchType and searchKey.
					 * searchType is either 'p' or 'r' or 'b' (for both). Product or reviews.
					 * searchKey is the string to match, can be partial or full. The class
					 * can figure that out. Just check if it ends with a %.
					 */
					query_search.retrieve(q.substring(0,1), q.substring(2));
					break;
				case "range":
					/*
					 * TODO: QuerySearchRange will take in arguments rangeType, operator and rangeKey.
					 * rangeType can be 'rscore', 'pprice' or 'rdate'.
					 * operator can be '>' or '<'.
					 * rangeKey can be a int or a date.
					 */
					query_range.retrieve(q, split_query[i+1], split_query[i+2]);
					i += 2;
					break;
				case "searchKey":
					query_search.retrieve("b", q);
					break;
				case "Error":
					System.out.println("Something's wrong");
					break;
				default:
					break;
			}
			i++;
		}
	}
	
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
	}
}
