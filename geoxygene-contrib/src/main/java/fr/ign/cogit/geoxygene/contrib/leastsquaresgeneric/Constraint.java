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



//=======================================================================
// Class to handle constraints in mathematical expressions
// Date : 30/03/2015
// Contact : yann.meneroux@ign.fr
//=======================================================================

public class Constraint {
	
	// Type of constrait
	private boolean type = false;

	// Mathematical expression
	private String expression;

	// right part (numerical)
	private double rightPart;

	// left part (literal)
	private String leftPart;

	// Reverse Polish Notation
	private ReversePolishNotation rpn;

	// Weights
	private double weight = 1;
	private double variance = 1;
	private double stddev = 1;

	// Getters
	public boolean isImperative(){return type;}
	public boolean isIndicative(){return !type;}
	public double getRightPart(){return rightPart;}
	public double getWeight(){return weight;}
	public double getVariance(){return variance;}
	public double getStddev(){return stddev;}
	public String getExpression(){return expression;}
	public String getLeftPart(){return leftPart;}
	public ReversePolishNotation getReversePolishNotation(){return rpn;}

	// Setters
	public void setImperative(boolean type){this.type = type;}
	public void setExpression(String expression){this.expression = expression;}

	// Set weight systems

	public void setWeight(double weight){

		this.weight = weight;
		this.stddev = 1/weight;
		this.variance = stddev*stddev;

	}

	public void setStddev(double stddev){

		this.stddev = stddev;
		this.weight = 1/stddev;
		this.variance = stddev*stddev;

	}

	public void setVariance(double variance){

		this.variance = variance;
		this.stddev = Math.sqrt(variance);
		this.weight = 1/stddev;

	}

	// -------------------------------------------------------------------
	// General method to build a constraint
	// Input : mathematical expression (string) and type (boolean)
	// Type = true -> imperative constraint
	// Type = false -> indicative constraint
	// -------------------------------------------------------------------
	public Constraint(String expression, boolean type){
		
		// Découpage éventuel de l'écart-type
		
		int pos = expression.indexOf("+/-");
		
		if (pos == -1){
		
			this.expression = expression;
		
		}
		else{
			
			this.expression = expression.substring(0, pos);
			this.stddev = Double.parseDouble(expression.substring(pos+3, expression.length()));
			this.variance = stddev*stddev;
			this.weight = 1/stddev;
			
		}
		
		this.type = type;

		split();

		this.rpn = new ReversePolishNotation(leftPart);

	}

	// -------------------------------------------------------------------
	// Method to build an indicative constraint
	// Input : mathematical expression (string)
	// -------------------------------------------------------------------
	public Constraint(String expression){

		this(expression, false);

	}

	// -------------------------------------------------------------------
	// ToString method redefinition
	// Output : constraint in expressive form
	// -------------------------------------------------------------------
	public String toString(){

		return this.expression;

	}

	// -------------------------------------------------------------------
	// Method to split the constraint in right part and left part
	// -------------------------------------------------------------------
	private void split(){

		// String copy
		String exp = new String(expression);

		// Supressing spaces in string
		exp = exp.replaceAll("\\s+","");

		// Splitting
		int posEqual = exp.indexOf('=');

		if (posEqual == -1){

			System.out.println("Error : constraint ["+expression+"] should be an equation");
			System.exit(0);

		}

		this.leftPart = exp.substring(0,posEqual);

		String temp = exp.substring(posEqual+1, exp.length());

		if (!ExpressionComputer.isNumeric(temp)){

			System.out.println("Error in constraint ["+expression+"] : right part should be numeric");
			System.exit(0);

		}

		this.rightPart = Double.parseDouble(temp);

	}

}
