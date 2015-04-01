/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/

package fr.ign.cogit.geoxygene.contrib.leastsquaresgeneric;

import java.util.Stack;

//=======================================================================
// General static class to eval expressions in RPN from Parameters table
// Date : 29/03/2015
// Contact : yann.meneroux@ign.fr
//=======================================================================

public class ExpressionComputer {

	public static Parameters parameters = new Parameters(); 

	private static String FM[] = Functions.getNamesTable();

	// -------------------------------------------------------------------
	// Method to define parameters
	// -------------------------------------------------------------------
	public static void setParameters(Parameters param){

		parameters = param;

	}

	// -------------------------------------------------------------------
	// Method to eval RPN 
	// -------------------------------------------------------------------
	public static double eval(ReversePolishNotation rpn){

		// Pile
		Stack<String> PILE = rpn.PILE;

		// Taille de la pile
		int NP = PILE.size();

		// Elément de pile
		String item;

		// Code-index : 0 -> Opérateur   1 -> Identificateur  2 ->  Fonction   3 -> Constante
		Double[] PILE_NUM = new Double[NP];
		Integer[] PILE_INDEX = new Integer[NP];


		for (int i=0; i<NP; i++){

			item = PILE.get(i);

			if (isNumeric(item)){ // Constante
				PILE_NUM[i] = Double.parseDouble(item);
				PILE_INDEX[i] = 3;
			}

			else{

				boolean fonc = false;

				int fonc_index = 0;
				while ((!fonc) && (fonc_index<FM.length)){
					if ((FM[fonc_index]+"$").equals(item)){fonc = true;}
					else{fonc_index++;}
				}

				if (fonc){ // Fonction
					PILE_NUM[i] = (double) fonc_index;
					PILE_INDEX[i] = 2;
				}

				else{
					if ((item.equals("+"))||(item.equals("-"))||(item.equals("*"))||(item.equals("/"))||(item.equals("^"))||(item.equals("!f"))||(item.equals("%"))||(item.equals("~"))){ //Opérateur
						if (item.equals("+")){PILE_NUM[i] = 1.0;}       // ADDITION
						if (item.equals("-")){PILE_NUM[i] = 2.0;}       // SOUSTRACTION
						if (item.equals("*")){PILE_NUM[i] = 3.0;}       // MULTIPLICATION
						if (item.equals("/")){PILE_NUM[i] = 4.0;}       // DIVISION FLOTTANTE
						if (item.equals("^")){PILE_NUM[i] = 5.0;}       // EXPONENTIATION
						if (item.equals("!f")){PILE_NUM[i] = 6.0;}      // FONCTION
						if (item.equals("%")){PILE_NUM[i] = 7.0;}       // MODULO
						if (item.equals("~")){PILE_NUM[i] = 8.0;}       // DIVISION ENTIERE

						PILE_INDEX[i] = 0;

					}
					else{ // Identificateur
						PILE_NUM[i] = parameters.getParameter(item);
						PILE_INDEX[i] = 1;
					}
				}
			}
		}


		// Evaluation de l'expression

		int nb = NP;
		double calcul = 0;
		double arg1;
		double arg2;

		while(nb > 1){

			int index = 0;

			while(PILE_INDEX[index]!=0){

				index++;

			}

			arg1 = PILE_NUM[index-2];
			arg2 = PILE_NUM[index-1];

			if (PILE_NUM[index]!=6){calcul = operation(arg1, arg2, PILE_NUM[index]);}
			if (PILE_NUM[index]==6){calcul = Functions.eval(arg1, arg2);}

			//Translations
			PILE_NUM[index-2] = calcul;

			PILE_INDEX[index-2] = 3; //Constante

			for (int j=index; j<nb-1; j++){

				PILE_NUM[j-1] = PILE_NUM[j+1];
				PILE_INDEX[j-1] = PILE_INDEX[j+1];

			}

			nb = nb-2;

		}

		return PILE_NUM[0];

	}

	// -------------------------------------------------------------------
	// Method to eval a constraint 
	// -------------------------------------------------------------------
	public static double eval(Constraint constraint){
		
		return eval(constraint.getReversePolishNotation());
		
	}

	// -------------------------------------------------------------------
	// Method to test if a string is numeric
	// Input : mathematical expression (string)
	// -------------------------------------------------------------------
	public static boolean isNumeric(String str)  
	{  
		try  
		{  
			@SuppressWarnings("unused")
			double d = Double.parseDouble(str);  
		}  
		catch(NumberFormatException nfe)  
		{  
			return false;  
		}  
		return true;  
	}


	// -------------------------------------------------------------------
	// Method to process an operation
	// Input : arguments (double) and operator index (double)
	// -------------------------------------------------------------------
	public static double operation(double arg1, double arg2, double opIndex){

		// Addition
		if (opIndex==1){return (arg1 + arg2);}

		// Soustraction
		if (opIndex==2){return (arg1 - arg2);}

		// Multiplication
		if (opIndex==3){return (arg1 * arg2);}

		// Division flottante
		if (opIndex==4){

			if (arg2 == 0){
				System.out.println("Error divide by 0");
				System.exit(0);
			}

			return (arg1 / arg2);
		}
		// Exponentiation
		if (opIndex==5){return Math.pow(arg1,arg2);}

		// Congruences
		if (opIndex==7){return arg1-Math.floor(arg1/arg2)*arg2;}

		// Division entière
		if (opIndex==8){return Math.floor(arg1/arg2);}

		return Double.NaN;

	}

	// -------------------------------------------------------------------
	// Method to compute numerical derivation of an expression
	// Input : expression (RPN), variable derivation name (String), 
	// step (h), order (integer in : {1,2,4})
	// Output : numerical derivation taken in the current paramaters
	// -------------------------------------------------------------------
	public static double numericalDerivation(ReversePolishNotation expression, String variable, double step, int order){

		// Approximation O(h) -> forward difference
		if (order == 1){

			double f0 = eval(expression);

			parameters.incrementParameter(variable, step);
			double f1 = eval(expression);
			parameters.incrementParameter(variable, -step);

			return (f1-f0)/step;

		}

		// Approximation O(h²) -> centered difference
		if (order == 2){

			parameters.incrementParameter(variable, -step);
			double f0 = eval(expression);
			parameters.incrementParameter(variable, 2*step);
			double f1 = eval(expression);
			parameters.incrementParameter(variable, -step);

			return (f1-f0)/(2*step);

		}

		// Approximation O(h^4) 2nd order centered difference
		if (order == 4){

			parameters.incrementParameter(variable, -2*step);
			double f0 = eval(expression);
			parameters.incrementParameter(variable, step);
			double f1 = eval(expression);
			parameters.incrementParameter(variable, 2*step);
			double f2 = eval(expression);
			parameters.incrementParameter(variable, step);
			double f3 = eval(expression);
			parameters.incrementParameter(variable, -2*step);

			return (-f3+8*f2-8*f1+f0)/(12*step);

		}

		System.out.println("Error : numerical derivation order must be in [1;2;4]");
		System.exit(0);

		return 0;

	}

	// -------------------------------------------------------------------
	// Method to compute numerical derivation of a constraint
	// Input : constraint (Constraint), variable derivation name (string), 
	// step (h), order (integer in : {1,2,4})
	// Output : numerical derivation taken in the current paramaters
	// -------------------------------------------------------------------
	public static double numericalDerivation(Constraint constraint, String variable, double step, int order){

		return numericalDerivation(constraint.getReversePolishNotation(), variable, step, order);

	}

}
