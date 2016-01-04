package fr.ign.cogit.geoxygene.contrib.leastsquaresgeneric;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;


public class Console {

	
	static Hashtable<String, Double> VARIABLES = new Hashtable<String, Double>();
	
	static Solver solver = new Solver();
	
	static boolean verbose = false;

	public static void main(String[] args) {


		boolean stop = false;

		solver.setConvergenceCriteria(0.001);

		System.out.println("----------------------------");
		System.out.println("NEW LEAST SQUARES ESTIMATION");
		System.out.println("----------------------------");

		while(!stop){

			System.out.print("> ");

			@SuppressWarnings("resource")
			String s = new Scanner(System.in).nextLine();

			if (s.equals("exit")){

				stop = true;

			}
			
			if (s.contains("set verbose true")){

				verbose = true;
				System.out.println("Verbose mode activated");
				continue;

			}
			
			if (s.contains("set verbose false")){

				verbose = false;
				continue;

			}

			if (s.contains(":=")){

				String name = s.split(":=")[0];
				double value = ExpressionComputer.eval(new ReversePolishNotation(s.split(":=")[1]));

				solver.getParameters().setParameter(name, value);
				VARIABLES.put(name, value);

				continue;

			}

			if (s.contains("=")){

				solver.addConstraint(new Constraint(s));
				
				if (verbose){System.out.println("New constraint defined : "+solver.getConstraint(solver.getConstraintsNumber()-1));}

			}

			if (s.contains("<-")){

				String name = s.split("<-")[0];
				double value = Double.parseDouble(s.split("<-")[1]);
				
				solver.addParameter(name, value);
				
				if (verbose){System.out.println("Parameter "+name+" initialized at "+value);}

			}

			if (s.startsWith("disp(")){

				String var = s.substring(5,s.length()-1);

				if (solver.getParameters().contains(var)){

					System.out.println(solver.getParameter(var));

				}
				else{

					System.out.println("Error : variable "+var+" doesn't exist");

				}

				continue;

			}

			if (s.equals("disp_constraints()")){

				if (solver.getConstraints().size() == 0){

					System.out.println("No constraints set yet");

				}

				for(int i=0; i<solver.getConstraints().size(); i++){

					System.out.println("constraint "+i+" : "+solver.getConstraints().get(i)+" +/-"+solver.getConstraints().get(i).getStddev());

				}

			}

			if (s.equals("disp_parameters()")){

				if (solver.getParametersNumber() == 0){

					System.out.println("No parameters set yet");

				}

				for(int i=0; i<solver.getParametersNumber(); i++){

					System.out.println("parameter "+solver.getParameterName(i)+" : "+solver.getParameter(i));

				}

			}

			if (s.equals("solve()")){

				// Removing additional variables
				delAdd();

				solver.compute();

				// Replacing additional variables
				replaceAdd();
				

				System.out.println("----------------------------");
				System.out.println("Computation done with succes");
				System.out.println("----------------------------");

			}


			if (s.equals("results()")){

				// Removing additional variables
				delAdd();

				solver.displayResults();

				// Replacing additional variables
				replaceAdd();

			}

			if (s.equals("all_results()")){

				// Removing additional variables
				delAdd();
				
				solver.displayFullResults();
				
				// Replacing additional variables
				replaceAdd();

			}

		}

	}
	
	private static void delAdd(){
		
		Enumeration<String> KEYS = VARIABLES.keys();

		while(KEYS.hasMoreElements()){

			String name = KEYS.nextElement();

			if (solver.getParameters().contains(name)){

				solver.getParameters().remove(name);

			}

		}
		
	}
	
	private static void replaceAdd(){
		
		Enumeration<String> KEYS = VARIABLES.keys();

		while(KEYS.hasMoreElements()){

			String name = KEYS.nextElement();

			if (!solver.getParameters().contains(name)){

				solver.addParameter(name, VARIABLES.get(name));

			}

		}
		
	}

}
