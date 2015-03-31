package fr.ign.cogit.geoxygene.contrib.leastsquaresgeneric;

import java.util.ArrayList;

import Jama.Matrix;

//=======================================================================
// Class for solving least squares problem
// Date : 30/03/2015
// Contact : yann.meneroux@ign.fr
//=======================================================================

public class Solver {


	// Unknown parameters
	private Parameters parameters;

	// Constraints list
	private ArrayList<Constraint> CONSTRAINTS;

	// Process parameters
	private double h;     // Numerical derivation step (default 10e-1)
	private double f;	  // Reducing factor (default 1.0)
	private double e;	  // Convergence criteria (default 0)
	private int order;    // Numerical derivation order (default 2)
	private int iter;     // Iterations number (default 1)

	// Effective number of iterations
	private int iterations;

	// Residuals savings
	private ArrayList<ArrayList<Double>> RESIDUALS;

	// -------------------------------------------------------------------
	// Getters
	// -------------------------------------------------------------------
	public Parameters getParameters(){return parameters;}
	public ArrayList<Constraint> getConstraints(){return CONSTRAINTS;}
	public double getParameter(String name){return parameters.getParameter(name);}
	public double getParameter(int index){return parameters.getParameter(index);}
	public String getParameterName(int index){return parameters.getParameterName(index);}
	public int getParametersNumber(){return parameters.getParametersNumber();}
	public Constraint getConstraint(int index){return CONSTRAINTS.get(index);}
	public double getDerivationStep(){return this.h;}
	public double getReducingFactor(){return this.f;}
	public double getConvergenceCriteria(){return this.e;}
	public int getDerivationOrder(){return this.order;}
	public int getIterationsNumber(){return this.iter;}
	public int getEffectiveIterationsNumber(){return this.iterations;}


	// -------------------------------------------------------------------
	// Setters
	// -------------------------------------------------------------------
	public void setParameters(Parameters parameters){this.parameters = parameters;}
	public void setConstraints(ArrayList<Constraint> constraints){this.CONSTRAINTS = constraints;}
	public void setParameter(String name, double value){parameters.setParameter(name, value);}
	public void setParameter(int index, double value){parameters.setParameter(index, value);}
	public void setConstraint(int index, Constraint newConstraint){CONSTRAINTS.set(index, newConstraint);}
	public void setDerivationStep(double h){this.h =h;}
	public void setReducingFactor(double f){this.f = f;}
	public void setConvergenceCriteria(double epsilon){this.e = epsilon;}
	public void setDerivationOrder(int order){this.order = order;}
	public void setIterationsNumber(int n){this.iter = n;}

	// -------------------------------------------------------------------
	// New constraints
	// -------------------------------------------------------------------
	public void addConstraint(Constraint constraint){

		CONSTRAINTS.add(constraint); 
		RESIDUALS.add(new ArrayList<Double>());

	}


	// -------------------------------------------------------------------
	// New parameters
	// -------------------------------------------------------------------
	public void addParameter(String name, double value){

		if(parameters.contains(name)){

			System.out.println("Error : parameter "+name+" has already been defined");
			System.exit(0);

		}
		else{

			setParameter(name, value);

		}

	}

	// -------------------------------------------------------------------
	// General method to build a solver
	// -------------------------------------------------------------------
	public Solver(ArrayList<Constraint> constraints, Parameters parameters){

		// Default parameters
		this.h = 0.1;
		this.f = 1.0;
		this.e = 0;
		this.order = 2;
		this.iter = 1;
		this.iterations = 0;

		// Instanciation
		this.CONSTRAINTS = constraints;
		this.parameters = parameters;

		this.RESIDUALS = new ArrayList<ArrayList<Double>>();

	}

	// -------------------------------------------------------------------
	// Alternative method to build a solver
	// -------------------------------------------------------------------
	public Solver(Parameters parameters){

		this(new ArrayList<Constraint>(), parameters);

	}

	// -------------------------------------------------------------------
	// Alternative method to build a solver
	// -------------------------------------------------------------------
	public Solver(){

		this(new ArrayList<Constraint>(), new Parameters());

	}

	// -------------------------------------------------------------------
	// Main method to process least squares
	// Output : unknown variables are directly updated in 'parameters'
	// -------------------------------------------------------------------
	public void compute(){

		int ne = CONSTRAINTS.size();                // Number of equations 
		int np = parameters.getParametersNumber();  // Number of parameters

		// Redundancy test
		if (np > ne){

			System.out.println("Error : number of unknown parameters ("+np+") is greater than number of constraints ("+ne+")");
			System.exit(0);

		}

		// Parameters definfition
		ExpressionComputer.setParameters(parameters);

		// Matrices dimensions
		Matrix A = new Matrix(ne, np);
		Matrix X = new Matrix(np, 1);
		Matrix B = new Matrix(ne, 1);
		Matrix P = new Matrix(ne, ne);

		// Variables
		Constraint constraint;
		String parameter;

		// Loop on iterations
		for (int k=0; k<iter; k++){

			// -------------------------------------------------------------------
			// Filling Jacobian matrix A with partial derivatives dci/dxj
			// -------------------------------------------------------------------
			for (int i=0; i<ne; i++){

				for (int j=0; j<np; j++){

					constraint = CONSTRAINTS.get(i);
					parameter = parameters.getParameterName(j);

					double partialDerivative = ExpressionComputer.numericalDerivation(constraint, parameter, h, order);

					A.set(i, j, partialDerivative);

				}

			}

			// -------------------------------------------------------------------
			// Filling matrix B with observations minus Taylor constant term
			// -------------------------------------------------------------------
			for (int i=0; i<ne; i++){

				constraint = CONSTRAINTS.get(i);

				double obs = constraint.getRightPart();
				double cst = ExpressionComputer.eval(constraint);

				B.set(i, 0, obs-cst);

			}

			// -------------------------------------------------------------------
			// Weight matrix
			// -------------------------------------------------------------------
			for (int i=0; i<ne; i++){

				P.set(i, i, CONSTRAINTS.get(i).getWeight());

			}

			// -------------------------------------------------------------------
			// Computing normal equation
			// -------------------------------------------------------------------
			Matrix N = A.transpose().times(P).times(A);
			Matrix Y = A.transpose().times(P).times(B);

			// -------------------------------------------------------------------
			// Computing incremental matrix X
			// -------------------------------------------------------------------
			X = N.solve(Y);

			// -------------------------------------------------------------------
			// Parameters update with reducing factor
			// -------------------------------------------------------------------
			for (int j=0; j<np; j++){

				parameters.incrementParameter(j, f*X.get(j, 0));

			}

			// -------------------------------------------------------------------
			// Residuals computation
			// -------------------------------------------------------------------
			for (int i=0; i<CONSTRAINTS.size(); i++){

				constraint = CONSTRAINTS.get(i);
				double b = constraint.getRightPart();

				RESIDUALS.get(i).add(ExpressionComputer.eval(constraint)-b);

			}

			// -------------------------------------------------------------------
			// Convergence test
			// -------------------------------------------------------------------
			boolean test = true;

			for (int j=0; j<np; j++){

				test = (test) && (Math.abs(X.get(j, 0)) < e); 

			} 

			if ((test) || (k == iter-1)){this.iterations = k+1;   break;}

		}

	}

	// -------------------------------------------------------------------
	// Method to get least squares residuals
	// Input : constraint index (integer), iteration (integer)
	// Output : residual in constraint index at given iteration
	// -------------------------------------------------------------------
	public double getResidual(int index, int iteration){

		return RESIDUALS.get(index).get(iteration);

	}

	// -------------------------------------------------------------------
	// Method to get least squares final residuals
	// Input : constraint index (integer)
	// Output : residual in constraint index after final iteration
	// -------------------------------------------------------------------
	public double getFinalResidual(int index){

		return RESIDUALS.get(index).get(RESIDUALS.get(index).size()-1);

	}

	// -------------------------------------------------------------------
	// Method to get least squares total residuals
	// Input : iteration (integer)
	// Output : residual in all constraints at given iteration
	// -------------------------------------------------------------------
	public double getTotalResidual(int iteration){

		double residual = 0;

		for (int i=0; i<RESIDUALS.size(); i++){ 

			residual += Math.abs(RESIDUALS.get(i).get(iteration));

		}

		return residual;

	}

	// -------------------------------------------------------------------
	// Method to get least squares max residuals
	// Input : iteration (integer)
	// Output : max residual in all constraints at given iteration
	// -------------------------------------------------------------------
	public double getMaxResidual(int iteration){

		double residual = 0;
		double residual_temp;

		for (int i=0; i<RESIDUALS.size(); i++){ 

			residual_temp = Math.pow(RESIDUALS.get(i).get(iteration),2);

			if (residual_temp > residual){residual = residual_temp;}

		}

		return residual;

	}

	// -------------------------------------------------------------------
	// Method to get least squares squared residuals
	// Input : iteration (integer)
	// Output : squared residuals sum in all constraints at iteration
	// -------------------------------------------------------------------
	public double getTotalSquaredResidual(int iteration){

		double residual = 0;

		for (int i=0; i<RESIDUALS.size(); i++){ 

			residual += Math.pow(RESIDUALS.get(i).get(iteration),2);

		}

		return residual;

	}

	// -------------------------------------------------------------------
	// Method to dispaly main results
	// -------------------------------------------------------------------
	public void displayResults(){

		System.out.println("-----------------------------");
		System.out.println("After "+getEffectiveIterationsNumber()+" iterations");
		System.out.println("-----------------------------");
		
		for (int i=0; i<getParametersNumber(); i++){
			
			System.out.println(parameters.getParameterName(i)+" = "+parameters.getParameter(i));
			
		}

	}

}