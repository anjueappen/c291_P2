import java.util.Scanner;

public class Search {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Search s = new Search();
		String choice = s.promptUserForSearch(); 
		
		//get choice and process it 
	}

	public String promptUserForSearch(){

		Scanner input =  new Scanner(System.in); 
		Boolean polling = true;
		String choice = ""; 
		System.out.print("Enter your query in the correct format: ");
		while(polling){
			if(input.hasNext()){
				choice = input.next(); 				
			}
		}
		return choice; 
	}
}

