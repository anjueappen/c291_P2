import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sleepycat.db.*;

/**
 * Test class for Phase 2  
 * @author anju
 */
public class Phase2Test {

	public Phase2Test() {

	}


	public static void main(String[] arg) {
		
		String QUERYMATCHER = "(((r:|p:)[a-zA-Z0-9\\.\\/]+(\\%?))|((rscore|pprice|rdate)\\s*(<|>)\\s*((\\d{4}\\/\\d{2}\\/\\d{2})|\\d(?:(\\.\\d+)?)))|([a-zA-Z0-9\\.\\/]+(\\%?)))";
		Pattern QUERY = Pattern.compile(QUERYMATCHER);
		
		Scanner user_input = new Scanner( System.in );
		while(true){
			String query = user_input.nextLine();
			Matcher queryMatch = QUERY.matcher(query);
			while(queryMatch.find()) {
				System.out.println("1: " + queryMatch.group(1));
				System.out.println("2: " + queryMatch.group(1));
				System.out.println("3: " + queryMatch.group(1));
				System.out.println("4: " + queryMatch.group(1));
				System.out.println("5: " + queryMatch.group(1));
				System.out.println("6: " + queryMatch.group(1));
				System.out.println("7: " + queryMatch.group(1));
				System.out.println("8: " + queryMatch.group(1));
				System.out.println("9: " + queryMatch.group(1));
				System.out.println("10: " + queryMatch.group(1));
				System.out.println("11: " + queryMatch.group(1));
				System.out.println("12: " + queryMatch.group(1));
			}
		}
		
	}
}
		
