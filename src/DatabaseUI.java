import java.util.Scanner;
import com.sleepycat.db.Database;


public class DatabaseUI {
	
	public static Database connection; 

	public static void main(String[] args) {
		 
		DatabaseUI d = new DatabaseUI(); 
		d.startUI(); 
	}
	
	public void displayMenu() {
		System.out.println("Welcome to the Database!");
		System.out.println("Please select from one of the options below: ");
		System.out.println("(1) Query by whole words.");
		System.out.println("(2) Query by partial words");
		System.out.println("(3) Query by ranges");
		System.out.println("(4) Destroy the database.");
		System.out.println("(5) Quit");
		System.out.print("Enter your choice: ");
	}
	
	public void notImplemented() {
		System.out.println("Not implemented! Please hold on and try again soon!");
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
