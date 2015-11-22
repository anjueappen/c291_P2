import com.sleepycat.db.Database;

public class QuerySearch {
	
	Database pt_db;
	Database rt_db;

	public QuerySearch(Database pt_db, Database rt_db) {
		super();
		this.pt_db = pt_db;
		this.rt_db = rt_db;
	}
	
	public void retrieve(String searchType, String searchKey) {
		System.out.println("searchType = " + searchType);
		System.out.println("searchKey = " + searchKey);
		
	}
}
