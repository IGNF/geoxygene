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

	// Constraints lists
	private ArrayList<Constraint> CONSTRAINTS;
	private ArrayList<Constraint> IMP_CONSTRAINTS;

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

	// Normal equation matrix
	private Matrix N;

	// Variance factor
	private double sigma02;
	
	// Verbose mode
	private boolean verbose;

	// -------------------------------------------------------------------
	// Getters
	// -------------------------------------------------------------------
	public int getParametersNumber(){return parameters.getParametersNumber();}
	public int getIndicativeConstraintsNumber(){return CONSTRAINTS.size();}
	public int getImperativeConstraintsNumber(){return IMP_CONSTRAINTS.size();}
	public int getConstraintsNumber(){return CONSTRAINTS.size()+IMP_CONSTRAINTS.size();}
	public int getDerivationOrder(){return this.order;}
	public int getIterationsNumber(){return this.iter;}
	public int getEffectiveIterationsNumber(){return this.iterations;}
	public double getParameter(String name){return parameters.getParameter(name);}
	public double getParameter(int index){return parameters.getParameter(index);}
	public double getDerivationStep(){return this.h;}
	public double getReducingFactor(){return this.f;}
	public double getConvergenceCriteria(){return this.e;}
	public boolean getVerbose(){return verbose;}
	public Constraint getConstraint(int index){return CONSTRAINTS.get(index);}
	public String getParameterName(int index){return parameters.getParameterName(index);}
	public Parameters getParameters(){return parameters;}
	public ArrayList<Constraint> getConstraints(){return CONSTRAINTS;}


	// -------------------------------------------------------------------
	// Setters
	// -------------------------------------------------------------------
	public void setParameters(Parameters parameters){this.parameters = parameters;}
	public void setIndicativeConstraints(ArrayList<Constraint> constraints){this.CONSTRAINTS = constraints;}
	public void setimperativeConstraints(ArrayList<Constraint> constraints){this.IMP_CONSTRAINTS = constraints;}
	public void setParameter(String name, double value){parameters.setParameter(name, value);}
	public void setParameter(int index, double value){parameters.setParameter(index, value);}
	public void setIndicativeConstraint(int index, Constraint newConstraint){CONSTRAINTS.set(index, newConstraint);}
	public void setImperativeConstraint(int index, Constraint newConstraint){IMP_CONSTRAINTS.set(index, newConstraint);}
	public void setVerbose(boolean verbose){this.verbose = verbose;}
	public void setDerivationStep(double h){this.h =h;}
	public void setReducingFactor(double f){this.f = f;}
	public void setConvergenceCriteria(double epsilon){this.e = epsilon;}
	public void setDerivationOrder(int order){this.order = order;}
	public void setIterationsNumber(int n){this.iter = n;}


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
		this.parameters = parameters;
		this.CONSTRAINTS = constraints;
		this.IMP_CONSTRAINTS = new ArrayList<Constraint>();

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
	// New constraints
	// -------------------------------------------------------------------
	public void addConstraint(Constraint constraint){

		if (constraint.isIndicative()){

			CONSTRAINTS.add(constraint); 
			RESIDUALS.add(new ArrayList<Double>());

		}
		else{

			IMP_CONSTRAINTS.add(constraint); 

		}

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
	// Main method to process least squares
	// Output : unknown variables are directly updated in 'parameters'
	// -------------------------------------------------------------------
	public void compute(){

		int ne = CONSTRAINTS.size();                // Number of  indicative equations 
		int nc = IMP_CONSTRAINTS.size();            // Number of  imperative equations 
		int np = parameters.getParametersNumber();  // Number of parameters

		// Redundancy tests
		if (np > ne + nc){

			System.out.println("Error : number of unknown parameters ("+np+") is greater than number of constraints ("+(ne+nc)+")");
			System.exit(0);

		}

		if (np < nc){

			System.out.println("Error : number of unknown parameters ("+np+") is smaller than number of imperative constraints ("+nc+")");
			System.exit(0);

		}

		// Parameters definfition
		ExpressionComputer.setParameters(parameters);

		// Matrices dimensions
		Matrix A = new Matrix(ne, np);
		Matrix C = new Matrix(nc, np);
		Matrix X = new Matrix(np, 1);
		Matrix B = new Matrix(ne, 1);
		Matrix D = new Matrix(nc, 1);
		Matrix P = new Matrix(ne, ne);

		Matrix MA = new Matrix(np+nc, np+nc);
		Matrix MB = new Matrix(np+nc, 1);
		Matrix MX = new Matrix(np+nc, 1);

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
			// Filling Jacobian matrix C with partial derivatives dci/dxj
			// -------------------------------------------------------------------
			for (int i=0; i<nc; i++){

				for (int j=0; j<np; j++){

					constraint = IMP_CONSTRAINTS.get(i);
					parameter = parameters.getParameterName(j);

					double partialDerivative = ExpressionComputer.numericalDerivation(constraint, parameter, h, order);

					C.set(i, j, partialDerivative);

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
			// Filling matrix D with observations minus Taylor constant term
			// -------------------------------------------------------------------
			for (int i=0; i<nc; i++){

				constraint = IMP_CONSTRAINTS.get(i);

				double obs = constraint.getRightPart();
				double cst = ExpressionComputer.eval(constraint);

				D.set(i, 0, obs-cst);

			}

			// -------------------------------------------------------------------
			// Weight matrix
			// -------------------------------------------------------------------
			for (int i=0; i<ne; i++){

				P.set(i, i, CONSTRAINTS.get(i).getWeight());

			}

			// -------------------------------------------------------------------
			// Right and left indicative members in normal equation
			// ------------------------------------------------------------------
			Matrix LEFT = A.transpose().times(P).times(A);
			Matrix RIGHT = A.transpose().times(P).times(B);
			
			// -------------------------------------------------------------------
			// Total equation
			// ------------------------------------------------------------------
			for (int i=0; i<np; i++){
				for (int j=0; j<np; j++){
					MA.set(i, j, LEFT.get(i, j));
				}
			}
			for (int i=np; i<np+nc; i++){
				for (int j=0; j<np; j++){
					MA.set(i, j, C.get(i-np, j));
				}
			}
			for (int i=0; i<np; i++){
				for (int j=np; j<np+nc; j++){
					MA.set(i, j, C.get(j-np, i));
				}
			}
			for (int i=0; i<np; i++){
				MB.set(i, 0, RIGHT.get(i, 0));
			}
			for (int i=np; i<np+nc; i++){
				MB.set(i, 0, D.get(i-np, 0));
			}
			
			

			// -------------------------------------------------------------------
			// Computing normal equation
			// -------------------------------------------------------------------
			N = MA.inverse();
			MX = N.times(MB);


			// -------------------------------------------------------------------
			// Computing incremental matrix X
			// -------------------------------------------------------------------
			for (int i=0; i<np; i++){
				X.set(i, 0, MX.get(i, 0));
			}



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

		// -------------------------------------------------------------------
		// Recovering residuals
		// -------------------------------------------------------------------

		Matrix V = new Matrix(ne, 1);

		for (int i=0; i<ne; i++){

			V.set(i, 0, RESIDUALS.get(i).get(RESIDUALS.get(i).size()-1));

		}

		// -------------------------------------------------------------------
		// Computing variance factor
		// -------------------------------------------------------------------
		sigma02 = (V.transpose().times(P).times(V).get(0, 0))/(ne-np+nc);


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
	public double getResidual(int index){

		return RESIDUALS.get(index).get(RESIDUALS.get(index).size()-1);

	}

	// -------------------------------------------------------------------
	// Method to get least squares final normalized (/std) residuals
	// Input : constraint index (integer)
	// Output : residual in constraint index after final iteration
	// -------------------------------------------------------------------
	public double getNormalizedResidual(int index){

		return getResidual(index)/CONSTRAINTS.get(index).getStddev();

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
	// Method to get variance factor sigma 0 square
	// Input : none
	// Output : s02
	// -------------------------------------------------------------------
	public double getS02(){

		return sigma02;

	}

	// -------------------------------------------------------------------
	// Method to get standard deviation of estimated parameter i
	// Input : parameter index
	// Output : estimation standard deviation
	// -------------------------------------------------------------------
	public double getEstimationStd(int index){

		return Math.sqrt(sigma02*N.get(index, index));

	}

	// -------------------------------------------------------------------
	// Method to get variance of estimated parameter i
	// Input : parameter index
	// Output : estimation variance
	// -------------------------------------------------------------------
	public double getEstimationVariance(int index){

		return sigma02*N.get(index, index);

	}


	// -------------------------------------------------------------------
	// Method to get covariance of estimated parameters i and j
	// Input : parameter index i and j
	// Output : estimation covariance
	// -------------------------------------------------------------------
	public double getEstimationCovariance(int i, int j){

		return sigma02*N.get(i, j);

	}

	// -------------------------------------------------------------------
	// Method to get correlation of estimated parameters i and j
	// Input : parameter index i and j
	// Output : estimation standard deviation
	// -------------------------------------------------------------------
	public double getEstimationCorrelation(int i, int j){

		double sigma_i = getEstimationStd(i);
		double sigma_j = getEstimationStd(j);

		return getEstimationCovariance(i, j)/(sigma_i*sigma_j);

	}

	// -------------------------------------------------------------------
	// Method to get standard deviation of an estimated parameter
	// Input : parameter name
	// Output : estimation standard deviation
	// -------------------------------------------------------------------
	public double getEstimationStd(String name){

		int index = parameters.getIndex(name);

		return getEstimationStd(index);

	}

	// -------------------------------------------------------------------
	// Method to get variance of an estimated parameter
	// Input : parameter name
	// Output : estimation variance
	// -------------------------------------------------------------------
	public double getEstimationVariance(String name){

		int index = parameters.getIndex(name);

		return getEstimationVariance(index);

	}


	// -------------------------------------------------------------------
	// Method to get covariance of two estimated parameters
	// Input : parameters name
	// Output : estimation covariance
	// -------------------------------------------------------------------
	public double getEstimationCovariance(String name1, String name2){

		int index1 = parameters.getIndex(name1);
		int index2 = parameters.getIndex(name2);

		return getEstimationCovariance(index1, index2);

	}

	// -------------------------------------------------------------------
	// Method to get correlation of two estimated parameters
	// Input : parameters name
	// Output : estimation standard deviation
	// -------------------------------------------------------------------
	public double getEstimationCorrelation(String name1, String name2){

		int index1 = parameters.getIndex(name1);
		int index2 = parameters.getIndex(name2);

		double sigma_1 = getEstimationStd(index1);
		double sigma_2 = getEstimationStd(index2);

		return getEstimationCovariance(index1, index2)/(sigma_1*sigma_2);

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

	// -------------------------------------------------------------------
	// Method to dispaly full results (with statistics)
	// -------------------------------------------------------------------
	public void displayFullResults(){


		System.out.println("");

		System.out.println("-----------------------------");
		System.out.println("Residuals");
		System.out.println("-----------------------------");

		for (int i=0; i<getIndicativeConstraintsNumber(); i++){

			System.out.println("Eq "+i+" : "+getResidual(i));

		}

		System.out.println("");

		System.out.println("-----------------------------");
		System.out.println("Normalized residuals");
		System.out.println("-----------------------------");

		for (int i=0; i<getIndicativeConstraintsNumber(); i++){

			System.out.println("Eq "+i+" : "+getNormalizedResidual(i));

		}

		System.out.println("");

		System.out.println("-----------------------------");
		System.out.println("Unit variance factor");
		System.out.println("-----------------------------");

		System.out.println("s0² = "+sigma02);
		System.out.println("s0 = "+Math.sqrt(sigma02));
		
		System.out.println("-----------------------------");
		System.out.println("After "+getEffectiveIterationsNumber()+" iterations");
		System.out.println("-----------------------------");

		for (int i=0; i<getParametersNumber(); i++){


			double r = parameters.getParameter(i);

			System.out.println(parameters.getParameterName(i)+" = "+r+" +/- "+getEstimationStd(i));

		}

	}

}