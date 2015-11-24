
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class QueryRangeTest {
	Phase3 p3;
	QueryRange q;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		p3 = new Phase3();
		p3.createDatabases(); 
		q = new QueryRange(p3.pt_db, p3.rt_db, p3.sc_db, p3.rw_db);  
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRscoreAlone() {
		q.retrieve("rscore", ">", "1", new ArrayList<String>()); 
	}


}
