import java.util.Scanner;
import java.util.concurrent.Callable;

public class InputTaker {
	
	private Callable<Void> promptFunction; 
	private Callable<Boolean> inputCheckFunction; 
	
	private Boolean validInt(String value){
		try {
			Integer num = Integer.parseInt(value);
			return Boolean.TRUE; 
		} catch (NumberFormatException e) {
			// TODO: handle exception
			System.out.print("Please enter a valid number from the options given: ");
		}
		return Boolean.FALSE; 
	}
	
	public InputTaker(Callable<Void> promptFunction, Callable<Boolean> inputCheckFunction) {
		super();
		this.promptFunction = promptFunction;
		this.inputCheckFunction = inputCheckFunction;
	}

	public InputTaker(Callable<Void> promptFunc) {
		super();
		this.promptFunction = promptFunc;
		this.inputCheckFunction = null; 
	}

	public void getIntegerInput(String prompt){
		Scanner input =  new Scanner(System.in); 
		Boolean polling = true;
		String choice = ""; 
		Integer num; 
		while(polling){
			if(input.hasNext()){
				choice = input.next(); 
				
		}
	}
}
}
