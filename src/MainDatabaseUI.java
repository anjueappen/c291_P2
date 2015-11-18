import java.util.Scanner;
import com.sleepycat.db.Database;


public class MainDatabaseUI {
	
	public static Database connection; 

	public static void main(String[] args) {
		 
		MainDatabaseUI d = new MainDatabaseUI(); 
		d.startUI(); 
	}
	
	public void displayMenu() {
		System.out.println("Welcome to the Database!");
		System.out.println("Please select from one of the options below: ");
		System.out.println("(1) Create and populate database.");
		System.out.println("(2) Conduct a single query on database with a key.");
		System.out.println("(3) Conduct a range query on a database with a range of keys.");
		System.out.println("(4) Conduct a single query on database with data.");
		System.out.println("(5) Destroy the database.");
		System.out.println("(6) Quit");
		System.out.print("Enter your choice: ");
	}
	
	public void notImplemented() {
		System.out.println("Not implemented! Please hold try again soon!");
	}
	public void startUI() {
		Scanner input = new Scanner(System.in);
		displayMenu(); 
		Boolean polling = true; 
		String choice;
		Integer num = 0; //get rid of once stuf below done... 
		//TODO: get the class below working!
		//InputTaker i = new InputTaker(); 
		//Integer num = i.getValidIntegerInput();; 
		while(polling){
			if(input.hasNext()){
				choice = input.next(); 
				try {
					num = Integer.parseInt(choice); 	
				} catch (NumberFormatException e) {
					// TODO: handle exception
					System.out.print("Please enter a valid number from the options selected: ");
					continue; 
				}
				switch (num) {
				case 1:
					notImplemented();
					break;
				case 2: 
					notImplemented();
					break;
				case 3: 
					notImplemented();
					break;
				case 4:
					notImplemented();
					break;
				case 5:
					notImplemented();
					break;
				case 6:
					notImplemented();
					break;
					
				default:
					System.out.print("Please enter a number from the options given: ");;
				}
			}
			
		}
	
	}
}
