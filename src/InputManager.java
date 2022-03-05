/*
 * @author Markus Larsson mala3165
 */

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class InputManager{
	//ArrayList could be final.
	private static ArrayList<InputStream> inputStreams = new ArrayList<>();
	private final Scanner scanner;
	
	public InputManager(){
		this(System.in);
	}
	
	public InputManager(InputStream inputStream){
		if(inputStreams.contains(inputStream)){
			throw new IllegalStateException("Error: Illegal state exception!");
		}
		inputStreams.add(inputStream);
		scanner = new Scanner(inputStream);
	}
	
	public int getInt(String question){
		askUser(question);
		int output = scanner.nextInt();
		scanner.nextLine();
		return output;
	}
	
	public double getDouble(String question){
		askUser(question);
		double output = scanner.nextDouble();
		scanner.nextLine();
		return output;
	}
	
	public String getString(String question){
		askUser(question);
		String output;
		
		do{
			output = scanner.nextLine().trim();
			if(output.isEmpty()){System.out.println("Error: Input is empty!");}
		}while(output.isEmpty());
		
		return output.substring(0, 1).toUpperCase() + output.substring(1).toLowerCase();
	}
	
	//---HELPER METHODS---
	
	private void askUser(String input){
		System.out.print(input + "?> ");
	}
}