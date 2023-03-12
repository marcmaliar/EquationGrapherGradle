package eg;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Tester {
	
	static ArrayList<Equation> equations = new ArrayList<Equation>();
	
	public static void main(String[] args){
		
	}
		
		
		//equations.add(new Equation("equation1", "y=x"));
		//doCommand("equation1.setname(bork)");
	}
	/*
	public static void doCommand(String input){
		try{
			String command = input.substring(0, input.indexOf("("));
			command = command.toLowerCase();
			String parameterString = input.substring(input.indexOf( "(" )+1, input.indexOf( ")" ));
			String[] parameters = parameterString.split(",");
			
				Equation foundEquation = findEquation(command.substring(0, command.indexOf(".")));
				if (foundEquation != null){
					System.out.println(command.substring(command.indexOf(".") + 1));
					doCommandEquation(input.substring(input.indexOf(".") + 1));
				}
		}
		catch(Exception ex){
			
		}
	}
	
	public static Equation findEquation(String name){
		for (int i = 0; i < equations.size(); i++){
			if (equations.get(i).getName().equalsIgnoreCase(name)){
				return equations.get(i);
			}
		}
		return null;
		
	}
	

	public static void doCommandEquation(String input){
		try{
			String command = input.substring(0, input.indexOf("("));
			command = command.toLowerCase();
			String parameterString = input.substring(input.indexOf( "(" )+1, input.indexOf( ")" ));
			String[] parameters = parameterString.split(",");
			switch (command){
			
			case "setname":
				System.out.println("setnadfme");
				//setName(parameters[0]);
			case "setexpression":
				//setExpression(parameters[0]);
			case "setrounddigits":
				//setRoundDigits(Integer.parseInt(parameters[0]));
			case "settestnumbers":
				//setTestNumbers(Integer.parseInt(parameters[0]));
			
			default:
				//wld.getEquationPopup().setText("Command not recognized.");
			}
		}
		catch(Exception ex){
			//wld.getEquationPopup().setText("Error: " + ex);
		}
	}
	
	
	*/