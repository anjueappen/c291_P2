import java.util.Scanner;

public class Phase3 {
	protected static Scanner input;
	protected static Boolean polling;
	protected QueryProcessor qp;
	protected QuerySearch query_search;
	protected QuerySearchRange query_range;
	
	
	public Phase3() {
		input = new Scanner(System.in);
		polling = true; 
		qp = new QueryProcessor();
		query_search = new QuerySearch();
		query_range = new QuerySearchRange();
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
}
