package fr.ign.cogit.geoxygene.contrib.randomforests;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

// ===============================================================
// CLASS FOR RANDOM FORESTS  (BREIMAN 2001)
// ===============================================================
// Random forest model containing N randomized decision trees.
// It can be used for :
//  - Regression (if output variable Y is ordered type)
// 	- Classification (if output variable Y is categorical type)
// Each bagging is done on one of these 2 modes :
//    - Complete bagging (sample size = original dataset size)
//    - Sub-sampling, taking each time a number n of data
// Randomized on variables {Xj, j=1,...p} is done by selecting a 
// number m of variables at each split. By default, m is equal to 
// the square root of p for classifications and to p out of 3 for 
// regressions. It is possible to set individual trees max depth 
// and to prune theme according to a minimal impurity gain 
// criteria. Error estimate is naturally computed with OOB which 
// has been proven to be biased upwards in comparison to the 
// actual error expectancy. In complete bagging, OOB is computed 
// on about one third of trees (1/e ~ 0.37), while on sub-sampling
// it is computed on a number of trees depending on the sampling 
// ratio. As with decision trees, results can be returned thanks 
// to the functions estimate, classify and posterior.
//===============================================================
// Nodes impurity function :
//  - Entropy               (classification)
//  - Gini                  (classification, default)
//  - Mean Squared Errors   (regression, default)
// ===============================================================
// Aggregations of results are done according to the average for 
// the regressions problems and according to the argmax of the 
// posterior probabilities for the classification problems. 
// ===============================================================

public class RandomForest {

	// Static computation mode
	public static int MODE_REGRESSION = 1;
	public static int MODE_CLASSIFICATION = 2;

	// Static Impurity Functions
	public static int FUNCTION_MSE = 1;
	public static int FUNCTION_ENTROPY = 2;
	public static int FUNCTION_GINI = 3;


	// Hyper-parameters
	private int N;            // Number of trees
	private int mode;         // Computation mode
	private int impurity;     // Impurity function
	private int max_depth;    // Maximal depth
	private int max_data;	  // Maximal data in leaf
	private double min_imp;   // Minimal impurity difference
	private double bagging;	  // Sub sample size
	private double rho;       // Degree of randomness


	// Verbode mode
	private boolean verbose;

	// Compute OOB
	private boolean oob_mode;

	// OOB error
	private double oob;

	// Random forest model
	private ArrayList<DecisionTree> FOREST;

	// ----------------------------------------------------------
	// Getters
	// ----------------------------------------------------------
	public int getMode(){return mode;}
	public int getImpurityFunction(){return impurity;}
	public int getMaxDepth(){return max_depth;}
	public int getMaxData(){return max_data;}
	public int getTreesNumber(){return N;}
	public double getBagging(){return bagging;}
	public double getMinImpurity(){return min_imp;}
	public double getRandomness(){return rho;}
	public boolean getVerbose(){return verbose;}

	public DecisionTree getTree(int i){return FOREST.get(i);}

	// ----------------------------------------------------------
	// Setters
	// ----------------------------------------------------------
	public void setMode(int mode){this.mode = mode;}
	public void setImpurityFunction(int impurity){this.impurity = impurity;}
	public void setMaxDepth(int max_depth){this.max_depth = max_depth;}
	public void setBagging(int bagging){this.bagging = bagging;}
	public void setMaxData(int max_data){this.max_data = max_data;}
	public void setMinImpurity(double min_imp){this.min_imp = min_imp;}
	public void setRandomness(double rho){this.rho = rho;}
	public void setVerbose(boolean verbose){this.verbose = verbose;}
	public void computeOOB(boolean oob_mode){this.oob_mode = oob_mode;}

	// ----------------------------------------------------------
	// Constructor from trees number N and computation mode :
	//   - regression
	//   - classification
	// Note that default degree of randomness and tree max depth 
	// are set accordingly to the training data set specificity.
	// Default : no out of bag error computation.
	// ----------------------------------------------------------
	public RandomForest(int mode, int N){

		this.mode = mode;
		this.rho = -1;             // Default to be set later
		this.max_depth = -1;       // Default to be set later
		this.max_data = 0;
		this.min_imp = 0;
		this.bagging = -1;         // Default to be set later
		this.N = N;

		verbose = false;
		oob_mode = false;
		oob = 0.0;

		if (mode == MODE_REGRESSION){this.impurity = FUNCTION_MSE;}
		if (mode == MODE_CLASSIFICATION){this.impurity = FUNCTION_GINI;}

		FOREST = new ArrayList<DecisionTree>();

	}

	// ----------------------------------------------------------
	// Building a random forest from a training data set
	// ----------------------------------------------------------
	public void build(TrainingDataSet dataset){

		ArrayList<Hashtable<Integer, Integer>> UNCALLED = null;

		if (oob_mode){UNCALLED = new ArrayList<Hashtable<Integer, Integer>>();}

		if (verbose){System.out.print("Checking dataset consistency : ");}
		//Prepare a list of input fields
		ArrayList<String> INPUT_VARIABLES = listInputVariables(dataset);

		// Number of variables
		int p = INPUT_VARIABLES.size();

		if (verbose){System.out.println("ok");}

		// Setting rho if needed
		if (this.rho == -1){

			if (this.mode == MODE_REGRESSION){

				this.rho = 2/3;

			}

			if (this.mode == MODE_CLASSIFICATION){

				this.rho = 1-Math.sqrt(p)/p;



			}

			if (verbose){System.out.println("Set randomness default value : pho = "+this.rho);}

		}

		// Setting max_depth if needed
		if (this.max_depth == -1){

			this.max_depth = dataset.getTrainingDataNumber();

			if (verbose){System.out.println("Set maximal depth default value : max_depth = "+this.max_depth);}

		}

		// Setting bagging if needed
		if (this.bagging == -1){

			this.bagging = dataset.getTrainingDataNumber();

			if (verbose){System.out.println("Set default bagging sub sample size  : bagging = "+this.bagging);}

		}

		// Number of original data
		int n = dataset.getTrainingDataNumber();

		if (verbose){System.out.println("Training dataset size = "+n);}
		if (verbose){System.out.println("Number of trees to build = "+N);}

		// Growing N randomized decision trees
		for (int i=0; i<N; i++){

			// OOB computation
			Hashtable<Integer, Integer> uncalled = null;
			if (oob_mode){

				uncalled = new Hashtable<Integer, Integer>();

				for (int k=0; k<n; k++){

					uncalled.put(k, 0);

				}

			}

			// Initializing tree
			DecisionTree tree = new DecisionTree(mode);

			// Setting its parameters
			tree.setImpurityFunction(impurity);
			tree.setMaxDepth(max_depth);
			tree.setMaxData(max_data);
			tree.setMinImpurity(min_imp);
			tree.setRandomness(rho);
			
			// Set its list of variables
			tree.setInputVariablesList(INPUT_VARIABLES);

			// Making dataset for tree i
			TrainingDataSet baggeddata = new TrainingDataSet();

			// Bagging data
			for (int k=0; k<this.bagging; k++){

				// Random index
				int index = (int)(Math.random()*n);

				// Getting random number
				baggeddata.addData(dataset.getData(index));			


				if (oob_mode){

					if(uncalled.containsKey(index)){

						uncalled.remove(index);

					}

				}

			}

			// Growing tree i
			tree.build(baggeddata);

			// Saving tree
			FOREST.add(tree);

			// Saving oob sample
			if (oob_mode){UNCALLED.add(uncalled);}

			// Progression
			double pc = Math.floor(1000*(double)(i)/N)/10;
			System.out.println("Tree number "+(i+1)+" built ("+pc+"%)");

		}

		// Compute out of bag error
		if (oob_mode){

			System.out.print("Computing out of bag error : ");

			oob = estimateOOB(dataset, UNCALLED);

			System.out.println(oob);

		}

		System.out.print("Random forest built with succes");

	}

	// ----------------------------------------------------------
	// AGGREGATING INDIVIDUAL TREES RESULTS
	// ----------------------------------------------------------
	// Predict a data (InputData X)
	// Output :
	//  - Regression : estimated value yhat
	//  - Classification : a posteriori conditionnal  : P(Y=c|X)
	// ----------------------------------------------------------
	public double estimate(InputData X){

		double predicted_value = 0;

		// Test
		if (FOREST.size() == 0){

			System.out.println("Error : random forest should be built with training dataset before prediction");
			System.exit(0);

		}

		// Prediction
		for (int i=0; i<N; i++){

			predicted_value += FOREST.get(i).estimate(X);

		}

		// Normalization
		predicted_value /= N;

		return predicted_value;

	}

	// ----------------------------------------------------------
	// Classification
	// ----------------------------------------------------------
	public String classify(InputData X){

		Hashtable<String, Integer> CLASSES = new Hashtable<String, Integer>();

		String predicted_value = "";

		int max = 0;

		// Test
		if (FOREST.size() == 0){

			System.out.println("Error : random forest should be built with training dataset before prediction");
			System.exit(0);

		}


		// Prediction
		for (int i=0; i<N; i++){

			String classe = FOREST.get(i).classify(X);

			if (!CLASSES.containsKey(classe)){

				CLASSES.put(classe, 1);

			}
			else{

				CLASSES.put(classe, CLASSES.get(classe)+1);

			}

			if (CLASSES.get(classe) > max){

				predicted_value = classe;
				max = CLASSES.get(classe);

			}

		}

		return predicted_value;

	}

	// ----------------------------------------------------------
	// Classification posterior probability P(c|X)
	// ----------------------------------------------------------
	public double posterior(String category, InputData X){

		double predicted_value = 0;

		// Test
		// Test
		if (FOREST.size() == 0){

			System.out.println("Error : random forest should be built with training dataset before prediction");
			System.exit(0);

		}


		// Prediction
		for (int i=0; i<N; i++){

			predicted_value += FOREST.get(i).posterior(category, X);

		}

		// Normalization
		predicted_value /= N;

		return predicted_value;

	}


	// ----------------------------------------------------------
	// Computing out of bag from a training data set and its 
	// associated UNCALLED table
	// ----------------------------------------------------------
	private double estimateOOB(TrainingDataSet dataset, ArrayList<Hashtable<Integer, Integer>> UNCALLED){

		double oob_error = 0;

		// Running on observations
		for (int j=0; j<dataset.getTrainingDataNumber(); j++){

			RandomForest RF = new RandomForest(mode, 0);

			// Get input and output
			InputData X = dataset.getData(j).getInputData();
			OutputData Y = dataset.getData(j).getOutputData();

			// Running on trees
			for (int i=0; i<FOREST.size(); i++){


				// Get list of uncalled data
				Hashtable<Integer, Integer> uncalled = UNCALLED.get(i);

				if (uncalled.containsKey(j)){

					// Get estimated output
					RF.FOREST.add(this.FOREST.get(i));


				}


			}

			// Number of trees
			RF.N = RF.FOREST.size();

			// Estimated output
			String classe = RF.classify(X);

			// Add error
			if (!classe.equals(Y.getCategoricalValue())){

				oob_error += 1.0;

			}

		}

		// Renormalization
		oob_error /= dataset.getTrainingDataNumber();

		return oob_error;

	}

	// ----------------------------------------------------------
	// Regression validation with a training dataset 
	// ----------------------------------------------------------
	public double regressionCrossValidation(TrainingDataSet validation){

		double mse = 0;

		System.out.println("-----------------------------------------------------------------");
		System.out.println("Number        Regression         Validation           Error      ");
		System.out.println("-----------------------------------------------------------------");

		for (int i=0; i<validation.getTrainingDataNumber(); i++){

			double y = validation.getData(i).getOutputData().getOrderedValue(); 
			double y_hat = estimate(validation.getData(i).getInputData());

			System.out.println(i+"    "+y_hat+"     "+y+"   "+(y-y_hat));

			mse += Math.pow(y-y_hat, 2);

		}

		mse /= validation.getTrainingDataNumber();

		System.out.println("-----------------------------------------------------------------");
		System.out.println("Mean Squared Error : "+mse+" ("+Math.sqrt(mse)+"�)");
		System.out.println("-----------------------------------------------------------------");
		
		return mse;

	}


	// ----------------------------------------------------------
	// Classification validation with a training dataset 
	// ----------------------------------------------------------
	public double classificationCrossValidation(TrainingDataSet validation){

		double er = 0;

		System.out.println("-----------------------------------------------------------------");
		System.out.println("Number        Classification         Validation           Error      ");
		System.out.println("-----------------------------------------------------------------");

		for (int i=0; i<validation.getTrainingDataNumber(); i++){

			String y = validation.getData(i).getOutputData().getCategoricalValue();
			String y_hat = classify(validation.getData(i).getInputData());

			double diff = 0;

			if (!y.equals(y_hat)){diff = 1;}

			System.out.println(i+"    "+y_hat+"     "+y+"   "+(y == y_hat));

			er += diff;

		}

		er /= validation.getTrainingDataNumber();

		System.out.println("--------------------------");
		System.out.println("Error rate : "+er);
		System.out.println("--------------------------");
		
		return er;

	}

	// ----------------------------------------------------------
	// Build a variables list
	// ----------------------------------------------------------
	private ArrayList<String> listInputVariables(TrainingDataSet dataset){
		
		ArrayList<String> INPUT_VARIABLES = new  ArrayList<String>();

		Hashtable<String, Integer> TEMP = new Hashtable<String, Integer>();

		int n = dataset.getTrainingDataNumber();

		for (int i=0; i<n; i++){

			InputData X = dataset.getData(i).getInputData();

			for (int j=0; j<X.getFeaturesNumber(); j++){

				TEMP.put(X.getFeatureName(j), 0);

			}

		}

		Enumeration<String> keys = TEMP.keys();

		while (keys.hasMoreElements()){

			INPUT_VARIABLES.add(keys.nextElement());

		}
		
		return INPUT_VARIABLES;

	}

}
