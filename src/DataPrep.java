import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;



public class DataPrep {
    public static void main(String[] args) throws IOException { 
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		Scanner console = new Scanner(System.in);
		while(console.hasNextLine()){
			String line = console.nextLine().replaceAll("\"", "&quot;"); 
			line = line.replaceAll("\\\\", "\\\\\\\\"); 
			System.out.println(line); 
    	}
		System.out.println("Success!!"); 
	}
}
