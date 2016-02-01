package fr.ign.cogit.geoxygene.contrib.randomforests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;


// ===============================================================
// CLASS FOR (RANDOMIZED) DECISION TREE
// ===============================================================
// A randomized decision tree is built from a training dataset :
// {Di = (Xi,Yi), i = [0, n-1]}
// It can be used for :
//  - Regression (if output variable Y is ordered type)
// 	- Classification (if output variable Y is categorical type)
// A randomized decision tree is built on all data (no bagging) 
// but with a subset of m features {Xj, j = 0,..m-1} to split each 
// node. Hyper-parameters to be set before running are :
// - Degree of randomness rho = 1-m/p  (0<=rho<=1)
//        -> default as 0
// - Maximal depth d (d > 0)  -> default n
// - Nodes impurity function :
//     - Entropy               (classification)
//     - Gini                  (classification, default)
//     - Mean Squared Errors   (regression, default)
// - Minimal difference impurity before prunning tree (M >= 0)
//         -> default is set at perfect homogeneity M = 0
// ===============================================================
// Randomized decision trees are used in two steps :
// (1) Learning (from the training data set)
// (2) Predicting (on an unseen input data X)
// ===============================================================
// Note that randomized decision trees sum up to classical trees 
// whenever the degree of randomness is set to zero (which amounts
// to state m = p, that is all variables {Xj, j = 0...m-1} are 
// selected for each node split. Besides, when there is no risk of
// over-fitting, one can get complete unprunned trees by setting :
// maximal depth d arbitrary big (above p should be enough) and 
// minimal difference impurity to 0. In this case, trees are 
// unpruned when, in each leaf, all data Y have same value.
// ===============================================================

public class DecisionTree {

	// ----------------------------------------------------------
	// Attributes
	// ----------------------------------------------------------

	// Static computation mode
	public static int MODE_REGRESSION = 1;
	public static int MODE_CLASSIFICATION = 2;

	// Static Impurity Functions
	public static int FUNCTION_MSE = 1;
	public static int FUNCTION_ENTROPY = 2;
	public static int FUNCTION_GINI = 3;

	// Hyper-parameters
	private int mode;         // Computation mode
	private double rho;       // Degree of randomness
	private int max_depth;    // Maximal depth
	private double min_imp;   // Minimal impurity difference
	private int impurity;     // Impurity function
	private int max_data;	  // Maximal data in leaf

	// Verbose mode
	private boolean verbose;

	// Tree architecture
	private ArrayList<Node> PROCESSED;
	private ArrayList<Node> TO_BE_PROCESSED;

	// Output classes
	private Hashtable<String, Double> OUTPUT_CLASSES;
	private Hashtable<String, Double> OUTPUT_CLASSES_LEFT;
	private Hashtable<String, Double> OUTPUT_CLASSES_RIGHT;
	private ArrayList<String> KEYS;

	// Variables list
	private ArrayList<String> INPUT_VARIABLES;

	// Categorical input modalities list
	private Hashtable<String, Hashtable<String, Integer>> CAT_VARIABLES;

	// Temporary variable to store categorical splits
	ArrayList<String> categorical_split;
	ArrayList<String> categorical_split_temp;

	// ----------------------------------------------------------
	// Standard constructor from computation mode :
	//   - regression
	//   - classification
	// Note that default tree max depth is set accordingly to the
	// training data set specificity.
	// ----------------------------------------------------------
	public DecisionTree(){

		mode = MODE_REGRESSION; // Default

		this.rho = 0;            
		this.max_depth = -1;       // Default to be set later
		this.min_imp = 0;
		this.max_data = 0;

		PROCESSED = new ArrayList<Node>();
		TO_BE_PROCESSED = new ArrayList<Node>();

		OUTPUT_CLASSES = new Hashtable<String, Double>();
		OUTPUT_CLASSES_LEFT = new Hashtable<String, Double>();
		OUTPUT_CLASSES_RIGHT = new Hashtable<String, Double>();
		KEYS = new ArrayList<String>();

		INPUT_VARIABLES = new ArrayList<String>();
		CAT_VARIABLES = new Hashtable<String, Hashtable<String, Integer>>();

		verbose = false;

	}
	// ----------------------------------------------------------
	public DecisionTree(int mode){

		this.mode = mode;
		this.rho = 0;            
		this.max_depth = -1;       // Default to be set later
		this.min_imp = 0;
		this.max_data = 0;

		if (mode == MODE_REGRESSION){this.impurity = FUNCTION_MSE;}
		if (mode == MODE_CLASSIFICATION){this.impurity = FUNCTION_GINI;}

		PROCESSED = new ArrayList<Node>();
		TO_BE_PROCESSED = new ArrayList<Node>();

		OUTPUT_CLASSES = new Hashtable<String, Double>();
		OUTPUT_CLASSES_LEFT = new Hashtable<String, Double>();
		OUTPUT_CLASSES_RIGHT = new Hashtable<String, Double>();
		KEYS = new ArrayList<String>();

		INPUT_VARIABLES = new ArrayList<String>();
		CAT_VARIABLES = new Hashtable<String, Hashtable<String, Integer>>();

		verbose = false;

	}

	// ----------------------------------------------------------
	// Getters
	// ----------------------------------------------------------
	public int getMode(){return mode;}
	public int getImpurityFunction(){return impurity;}
	public int getMaxDepth(){return max_depth;}
	public int getMaxData(){return max_data;}
	public double getMinImpurity(){return min_imp;}
	public double getRandomness(){return rho;}
	public boolean getVerbose(){return verbose;}


	// ----------------------------------------------------------
	// Setters
	// ----------------------------------------------------------
	public void setMode(int mode){this.mode = mode;}
	public void setImpurityFunction(int impurity){this.impurity = impurity;}
	public void setMaxDepth(int max_depth){this.max_depth = max_depth;}
	public void setMaxData(int max_data){this.max_data = max_data;}
	public void setMinImpurity(double min_imp){this.min_imp = min_imp;}
	public void setRandomness(double rho){this.rho = rho;}
	public void setVerbose(boolean verbose){this.verbose = verbose;}

	// ----------------------------------------------------------
	// Set input variables list (to avoid checking)
	// ----------------------------------------------------------
	public void setInputVariablesList(ArrayList<String> list){this.INPUT_VARIABLES = list;}

	// ----------------------------------------------------------
	// Learning a tree from a training data set
	// ----------------------------------------------------------
	public void build(TrainingDataSet dataset){

		// If not already specified prepare a list of input fields
		if (INPUT_VARIABLES.size() == 0){listInputVariables(dataset);}

		// Number of variables
		int p = INPUT_VARIABLES.size();

		// Prepare list of output modalities
		listInputModalities(dataset);

		// Define default value for tree max depth
		if (this.max_depth == -1){

			this.max_depth = dataset.getTrainingDataNumber();

		}

		// Data number
		int n = dataset.getTrainingDataNumber();

		// ------------------------------------------------------
		// Building the tree by learning
		// ------------------------------------------------------

		if (mode == MODE_CLASSIFICATION){

			// Listing output classes
			for (int i=0; i<n; i++){

				String output_class = dataset.getData(i).getOutputData().getCategoricalValue();

				if (!OUTPUT_CLASSES.containsKey(output_class)){

					OUTPUT_CLASSES.put(output_class, 0.0);

					// Listing of keys
					KEYS.add(output_class);

				}

			}

		}

		// Initiating first node
		Node root = new Node();
		root.setDegree(0);
		root.setDataSet(dataset.copy());

		TO_BE_PROCESSED.add(root);

		// Subset of randomly chosen variables
		int m = (int) (p*(1-rho));

		// Loops until pruning conditions are met
		while(TO_BE_PROCESSED.size() != 0){

			// Get first node in the queue
			Node node = TO_BE_PROCESSED.get(0);

			// Draw at random m variables
			ArrayList<Integer> VARIABLES = randomize(m, p);

			// Search for optimal cut...
			double s_optimal = Double.MIN_VALUE;
			double delta_optimal = Double.MAX_VALUE;
			String j_optimal = INPUT_VARIABLES.get(VARIABLES.get(0));

			// ... for each variable
			for (int i=0; i<VARIABLES.size(); i++){

				String j = INPUT_VARIABLES.get(VARIABLES.get(i));
				
				// default category
				categorical_split_temp = new ArrayList<String>();

				// Search for best split on variable j
				double[] bestSplit  = computeBestSplitImpurity(node, j);

				// Update
				if (bestSplit[1] < delta_optimal){

					s_optimal = bestSplit[0];
					delta_optimal = bestSplit[1];
					j_optimal = j;
					categorical_split = categorical_split_temp;

				}

			}


			// If pruning conditions are not met
			if ((node.getDegree() < max_depth) && (s_optimal > -Double.MAX_VALUE)){

				if (node.getDataSet().getTrainingDataNumber() > max_data){

					// Complete the node splits information
					node.setSplitIndex(j_optimal);
					node.setThreshold(s_optimal);
					node.setCategories(categorical_split);
					

					// Create successors
					Node node_left = new Node();
					Node node_right = new Node();

					// Set node degrees
					node_left.setDegree(node.getDegree()+1);
					node_right.setDegree(node.getDegree()+1);

					// Add data in successors
					operateSplit(node, node_left, node_right);

					
					// Test sizes
					if (node_left.getDataSet().getTrainingDataNumber() != 0){

						if (node_right.getDataSet().getTrainingDataNumber() != 0){

							// Add successors to the nodes
							node.setLeftSuccessor(node_left);
							node.setRightSuccessor(node_right);
							
							// Add successors to the tree
							TO_BE_PROCESSED.add(node_left);
							TO_BE_PROCESSED.add(node_right);

						}

					}

				}

			}

			// Compute a posteriori probabilities
			if (node.isTerminal()){

				if (mode == MODE_CLASSIFICATION){
					node.computeAPosterioriProbabilities();
				}
				if (mode == MODE_REGRESSION){
					node.computeAPosterioriEstimation();
				}
			}
			
			// End with this node
			PROCESSED.add(TO_BE_PROCESSED.remove(0));


			// Optional message display
			if (verbose){

				System.out.println(PROCESSED.get(PROCESSED.size()-1));

			}

		}

		if (verbose){

			System.out.println("Tree computed with success");

		}

	}


	// ----------------------------------------------------------
	// Compute best impurity difference split variable j 
	// -> call classification or regression function
	// Input : node of value according to j
	// output : best combination [split,optimal gain]
	// ----------------------------------------------------------
	private double[] computeBestSplitImpurity(Node node, String j){

		// Categorical input variable
		if (CAT_VARIABLES.containsKey(j)){

			return computeBestCategoricalSplitImpurity(node, j);

		}

		return computeBestOrderedSplitImpurity(node, j);

	}

	// ----------------------------------------------------------
	// Compute best impurity difference split variable j in the
	// ordered case -> call classification or regression function
	// Input : node of value according to j
	// output : best combination [split,optimal gain]
	// ----------------------------------------------------------
	@SuppressWarnings("unchecked")
	private double[] computeBestOrderedSplitImpurity(Node node, String j){

		// Sort data accordingly to Xj
		DataComparator comparator = new DataComparator(j);
		Collections.sort(node.getDataSet().toList(), comparator);

		if (mode == MODE_CLASSIFICATION){

			return computeBestOrderedSplitClassificationGain(node, j);

		}

		if (mode == MODE_REGRESSION){

			return computeBestOrderedSplitRegressionGain(node, j);

		}

		return null;

	}

	// ----------------------------------------------------------
	// Compute best impurity difference split variable j in the
	// categorical case -> call classification or regression 
	// function. Output : best combination [split,optimal gain]
	// ----------------------------------------------------------
	private double[] computeBestCategoricalSplitImpurity(Node node, String j){

		if (mode == MODE_CLASSIFICATION){

			return computeBestCategoricalSplitClassificationGain(node, j);

		}

		if (mode == MODE_REGRESSION){

			return computeBestCategoricalSplitRegressionGain(node, j);

		}

		return null;

	}

	// ----------------------------------------------------------
	// Compute best impurity difference split variable j for the 
	// Gini function or the entropy function for categorical
	// variable. Output : best combination [split,optimal gain]
	// ----------------------------------------------------------
	private double[] computeBestCategoricalSplitClassificationGain(Node node, String j){

		double[] result = new double[2];
		result[0] = 0;
		result[1] = 0;

		// Get dataset
		TrainingDataSet dataset = node.getDataSet();
		int N = dataset.getTrainingDataNumber();

		// Test all categorical possible splits
		ArrayList<ArrayList<String>> possible = subPartition(CAT_VARIABLES.get(j));

		// Optimal values
		double s_opt = Double.MAX_VALUE;
		int k_opt = 1;

		// List on sub partitions
		for (int k=0; k<possible.size(); k++){

			// Excluding empty set
			if (possible.get(k).size() == 0){continue;}
			if (possible.get(k).size() == CAT_VARIABLES.get(j).size()){continue;}

			// Define left and right successors datasets
			ArrayList<TrainingData> DATASET_LEFT = new ArrayList<TrainingData>();
			ArrayList<TrainingData> DATASET_RIGHT = new ArrayList<TrainingData>();

			// Compute both datasets
			for (int i=0; i<N; i++){

				// Get input modality
				String classe = dataset.getData(i).getInputData().getCategoricalFeature(j);

				// Assignement 
				if (possible.get(k).contains(classe)){DATASET_LEFT.add(dataset.getData(i));}
				else{DATASET_RIGHT.add(dataset.getData(i));}

			}

			// Class initializing
			for (int i=0; i<KEYS.size(); i++){

				OUTPUT_CLASSES_LEFT.put(KEYS.get(i), 0.0);
				OUTPUT_CLASSES_RIGHT.put(KEYS.get(i), 0.0);

			}

			// Set partitions cardinalities
			int Ntl = DATASET_LEFT.size();
			int Ntr = DATASET_RIGHT.size();

			// Left class initializing
			for (int l=0; l<Ntl; l++){

				String classe = DATASET_LEFT.get(l).getOutputData().getCategoricalValue();

				OUTPUT_CLASSES_LEFT.put(classe, OUTPUT_CLASSES_LEFT.get(classe)+1);

			}

			// Right class initializing
			for (int l=0; l<Ntr; l++){

				String classe = DATASET_RIGHT.get(l).getOutputData().getCategoricalValue();

				OUTPUT_CLASSES_RIGHT.put(classe, OUTPUT_CLASSES_RIGHT.get(classe)+1);

			}

			double itl = 0;
			double itr = 0;

			// Compute indices	
			for (int i=0; i<KEYS.size(); i++){

				String c = KEYS.get(i);
				double pcl = OUTPUT_CLASSES_LEFT.get(c)/Ntl;
				double pcr = OUTPUT_CLASSES_RIGHT.get(c)/Ntr;

				if (impurity == FUNCTION_GINI){
					itl += Math.pow(pcl, 2);
					itr += Math.pow(pcr, 2);
				}

				if (impurity == FUNCTION_ENTROPY){
					itl += pcl*Math.log(pcl)/Math.log(2);
					itr += pcr*Math.log(pcr)/Math.log(2);
				}

			}

			if (impurity == FUNCTION_GINI){
				itl = 1-itl;
				itr = 1-itr;
			}


			if (impurity == FUNCTION_ENTROPY){
				itl = -itl;
				itr = -itr;
			}

			// Optimal split update
			double sum = (((double)(Ntl)/N)*itl+((double)(Ntr)/N)*itr);


			// Update
			if (sum < s_opt){

				s_opt = sum;
				k_opt = k;

			}

		}

		result[1] = s_opt;
		categorical_split_temp = possible.get(k_opt);

		return result;

	}

	// ----------------------------------------------------------
	// Compute best impurity difference split on variable Xj for 
	// regression impurity function for an categorical variable
	// Input : node 
	// output : best combination [split,optimal gain]
	// ----------------------------------------------------------
	private double[] computeBestCategoricalSplitRegressionGain(Node node, String j){

		double[] result = new double[2];
		result[0] = 0;
		result[0] = 1;

		// Get dataset
		TrainingDataSet dataset = node.getDataSet();
		int N = dataset.getTrainingDataNumber();

		// Test all categorical possible splits
		ArrayList<ArrayList<String>> possible = subPartition(CAT_VARIABLES.get(j));

		// Optimal values
		double s_opt = Double.MAX_VALUE;
		int k_opt = 0;

		// List on sub partitions
		for (int k=0; k<possible.size(); k++){

			// Excluding empty set
			if (possible.get(k).size() == 0){continue;}
			if (possible.get(k).size() == CAT_VARIABLES.get(j).size()){continue;}

			// Define left and right successors datasets
			ArrayList<TrainingData> DATASET_LEFT = new ArrayList<TrainingData>();
			ArrayList<TrainingData> DATASET_RIGHT = new ArrayList<TrainingData>();

			// Compute both datasets
			for (int i=0; i<N; i++){

				// Get input modality
				String classe = dataset.getData(i).getInputData().getCategoricalFeature(j);

				// Assignement 
				if (possible.get(k).contains(classe)){DATASET_LEFT.add(dataset.getData(i));}
				else{DATASET_RIGHT.add(dataset.getData(i));}

			}

			// Set partitions cardinalities
			int Ntl = DATASET_LEFT.size();
			int Ntr = DATASET_RIGHT.size();

			// Compute regression impurity indices
			double my = 0;
			double my2 = 0;

			// Left partition

			for (int i=0; i<DATASET_LEFT.size(); i++){

				double y = DATASET_LEFT.get(i).getOutputData().getOrderedValue();

				my += y;
				my2 += y*y;

			}

			double itl= my2/Ntl-Math.pow(my/Ntl,2);

			// right partition

			for (int i=0; i<DATASET_RIGHT.size(); i++){

				double y = DATASET_RIGHT.get(i).getOutputData().getOrderedValue();

				my += y;
				my2 += y*y;

			}

			// Total

			double itr = my2/Ntr-Math.pow(my/Ntr,2);

			double sum = ((Ntl/N)*itl+(Ntr/N)*itr);


			// Update
			if (sum < s_opt){

				s_opt = sum;
				k_opt = k;

			}

		}

		// Best values
		result[1] = s_opt;
		categorical_split_temp = possible.get(k_opt);

		return result;

	}

	// ----------------------------------------------------------
	// Compute best impurity difference split variable j for the 
	// Gini function or the entropy function for ordered variable
	// Input : node of sorted value according to j
	// output : best combination [split,optimal gain]
	// ----------------------------------------------------------
	private double[] computeBestOrderedSplitClassificationGain(Node node, String j){

		// Get dataset
		TrainingDataSet dataset = node.getDataSet();
		int N = dataset.getTrainingDataNumber();


		// Initialization
		double xj1 = 0;
		double xj2 = 0;
		double sum = 0;
		double s = 0;

		// Inital min storage
		double opt = Double.MAX_VALUE;

		// Class cardinalities
		double Ntl = 0;
		double Ntr = N;

		// Left class initializing
		for (int i=0; i<KEYS.size(); i++){

			OUTPUT_CLASSES_LEFT.put(KEYS.get(i), 0.0);
			OUTPUT_CLASSES_RIGHT.put(KEYS.get(i), 0.0);

		}

		// Right class initializing
		for (int l=0; l<N; l++){

			String classe = dataset.getData(l).getOutputData().getCategoricalValue();

			OUTPUT_CLASSES_RIGHT.put(classe, OUTPUT_CLASSES_RIGHT.get(classe)+1);

		}


		// Generate threshold values
		for (int k=0; k<N-1; k++){

			// Left and right impurities
			double itl = 0;
			double itr = 0;

			// Increment occurrences number
			String classe_add = dataset.getData(k).getOutputData().getCategoricalValue();
			OUTPUT_CLASSES_LEFT.put(classe_add, OUTPUT_CLASSES_LEFT.get(classe_add)+1);
			OUTPUT_CLASSES_RIGHT.put(classe_add, OUTPUT_CLASSES_RIGHT.get(classe_add)-1);

			// Modify class cardinalities
			Ntl = k+1;
			Ntr = N-(k+1);

			// Compute indices	
			for (int i=0; i<KEYS.size(); i++){

				String c = KEYS.get(i);
				double pcl = OUTPUT_CLASSES_LEFT.get(c)/Ntl;
				double pcr = OUTPUT_CLASSES_RIGHT.get(c)/Ntr;

				if (impurity == FUNCTION_GINI){
					itl += Math.pow(pcl, 2);
					itr += Math.pow(pcr, 2);
				}

				if (impurity == FUNCTION_ENTROPY){
					itl += pcl*Math.log(pcl)/Math.log(2);
					itr += pcr*Math.log(pcr)/Math.log(2);
				}

			}

			if (impurity == FUNCTION_GINI){
				itl = 1-itl;
				itr = 1-itr;
			}

			if (impurity == FUNCTION_ENTROPY){
				itl = -itl;
				itr = -itr;
			}


			// Optimal split update
			sum = ((Ntl/N)*itl+(Ntr/N)*itr);

			if (sum < opt){

				opt = sum;

				xj1 = dataset.getData(k).getInputData().getOrderedFeature(j);
				xj2 = dataset.getData(k+1).getInputData().getOrderedFeature(j);	

				s = (xj1+xj2)/2;

			}

		}

		double[] result = new double[2];

		result[0] = s;
		result[1] = opt;

		return result;

	}

	// ----------------------------------------------------------
	// Compute best impurity difference split on variable Xj for 
	// the regression impurity function for an ordered variable
	// Input : node of sorted value according to Xj
	// output : best combination [split,optimal gain]
	// ----------------------------------------------------------
	private double[] computeBestOrderedSplitRegressionGain(Node node, String j){

		// Get dataset
		TrainingDataSet dataset = node.getDataSet();
		int N = dataset.getTrainingDataNumber();

		// Initialization
		double xj1 = 0;
		double xj2 = 0;
		double sum = 0;
		double s = 0;

		// Inital min storage
		double opt = Double.MAX_VALUE;

		// Class cardinalities
		double Ntl = 0;
		double Ntr = N;

		// Left and right sums
		double yl = 0;
		double yr = 0;
		double y2l = 0;
		double y2r = 0;

		// Right initialization
		for (int i=0; i<N; i++){

			double y = dataset.getData(i).getOutputData().getOrderedValue();
			yr += y;
			y2r += y*y;

		}


		// Generate threshold values
		for (int k=0; k<N-1; k++){

			// Left and right impurities
			double itl = 0;
			double itr = 0;

			// Get y change value
			double y = dataset.getData(k).getOutputData().getOrderedValue();

			// Modify sum values
			yl += y;
			yr -= y;
			y2l += y*y;
			y2r -= y*y;

			// Modify class cardinalities
			Ntl = k+1;
			Ntr = N-(k+1);

			// Compute regression indices
			itl = y2l/Ntl-Math.pow(yl/Ntl, 2);
			itr = y2r/Ntr-Math.pow(yr/Ntr, 2);

			// Optimal split update
			sum = ((Ntl/N)*itl+(Ntr/N)*itr);

			if (sum < opt){

				opt = sum;

				xj1 = dataset.getData(k).getInputData().getOrderedFeature(j);
				xj2 = dataset.getData(k+1).getInputData().getOrderedFeature(j);	

				s = (xj1+xj2)/2;

			}

		}

		double[] result = new double[2];

		result[0] = s;
		result[1] = opt;

		return result;

	}


	// ----------------------------------------------------------
	// Add data to successors according to the optimal split
	// ----------------------------------------------------------
	private void operateSplit(Node node, Node node_left, Node node_right){

		TrainingDataSet dataset = node.getDataSet();

		TrainingDataSet dataset_left = new TrainingDataSet();
		TrainingDataSet dataset_right = new TrainingDataSet();

		for (int i=0; i<dataset.getTrainingDataNumber(); i++){

			if (node.question(dataset.getData(i).getInputData())){

				dataset_left.addData(dataset.getData(i));

			}
			else{

				dataset_right.addData(dataset.getData(i));

			}

		}

		node_left.setDataSet(dataset_left);
		node_right.setDataSet(dataset_right);

	}


	// ----------------------------------------------------------
	// Predict a data (InputData X)
	// Output :
	//  - Regression : estimated value yhat
	//  - Classification : a posteriori conditionnal  : P(Y=c|X)
	// ----------------------------------------------------------
	public double estimate(InputData X){

		double predicted_value = 0;

		// Test
		if (PROCESSED.size() == 0){

			System.out.println("Error : randomized decision tree should be built with training dataset before prediction");
			System.exit(0);

		}

		// Getting leaf
		Node leaf = getLeaf(X);

		// Estimate value
		predicted_value = leaf.getYhat();

		return predicted_value;

	}

	// ----------------------------------------------------------
	// Classification
	// ----------------------------------------------------------
	public String classify(InputData X){

		String predicted_value = "";

		// Test
		if (PROCESSED.size() == 0){

			System.out.println("Error : randomized decision tree should be built with training dataset before prediction");
			System.exit(0);

		}

		// Getting leaf
		Node leaf = getLeaf(X);

		// Estimate value
		predicted_value = leaf.getMode();

		return predicted_value;

	}

	// ----------------------------------------------------------
	// Classification with posterior probability P(c|X)
	// ----------------------------------------------------------
	public double posterior(String category, InputData X){

		double predicted_value = 0;

		// Test
		if (PROCESSED.size() == 0){

			System.out.println("Error : randomized decision tree should be built with training dataset before prediction");
			System.exit(0);

		}

		// Getting leaf
		Node leaf = getLeaf(X);

		// Estimate value
		predicted_value = leaf.getAPosteriori(category);

		return predicted_value;

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
	// Draw at random m variable from a set of p variables
	// ----------------------------------------------------------
	private static ArrayList<Integer> randomize(int m, int p){

		// Extracted number
		ArrayList<Integer> OUTPUT = new ArrayList<Integer>();

		// Extraction source numbers
		ArrayList<Integer> SOURCE = new ArrayList<Integer>();
		for (int i=0; i<p; i++){SOURCE.add(i);}

		// Exctraction
		while(OUTPUT.size() != m){

			int index = (int)(Math.random()*SOURCE.size());

			OUTPUT.add(SOURCE.remove(index));

		}

		return OUTPUT;

	}

	// ----------------------------------------------------------
	// String converter
	// ----------------------------------------------------------
	public String toString(){

		if (PROCESSED.size() == 0){

			return "Decision tree hasn't been built yet";

		}

		String chaine = "";

		for (int i=0; i<PROCESSED.size(); i++){

			chaine += PROCESSED.get(i)+"\r\n";

		}

		return chaine;

	}

	// ----------------------------------------------------------
	// Gnu plot debugging functionnality
	// ----------------------------------------------------------
	public void gnuPlot(){

		// Data representation
		PROCESSED.get(0).getDataSet().gnuPlot();

		// Split nodes representations
		for (int i=0; i<PROCESSED.size(); i++){

			plotSeparation(PROCESSED.get(i));

		}

	}

	// ----------------------------------------------------------
	// Gnu plot of a separation
	// ----------------------------------------------------------
	private void plotSeparation (Node node){

		if (node.isTerminal()){return;}

		double x0 = Double.MAX_VALUE;
		double y0 = Double.MAX_VALUE;
		double x1 = Double.MIN_VALUE;
		double y1 = Double.MIN_VALUE;

		if (node.getSplitVariable().equals(INPUT_VARIABLES.get(0))){

			x0 = node.getThreshold();
			x1 = node.getThreshold();

			for (int i=0; i<node.getDataSet().getTrainingDataNumber(); i++){

				double y = node.getDataSet().getData(i).getInputData().getOrderedFeature(1);

				if (y < y0){y0 = y;}
				if (y > y1){y1 = y;}

			}

		}

		if (node.getSplitVariable().equals(INPUT_VARIABLES.get(1))){

			y0 = node.getThreshold();
			y1 = node.getThreshold();

			for (int i=0; i<node.getDataSet().getTrainingDataNumber(); i++){

				double x = node.getDataSet().getData(i).getInputData().getOrderedFeature(0);

				if (x < x0){x0 = x;}
				if (x > x1){x1 = x;}

			}

		}

		System.out.println("plot(["+x0+","+x1+"],["+y0+","+y1+"],'k-','linewidth',1.5)");


	}

	// ----------------------------------------------------------
	// Method to get leaf node t associated with an input X
	// ----------------------------------------------------------
	private Node getLeaf(InputData X){

		// Reaching a leave
		Node node = PROCESSED.get(0);

		// While not terminal
		while (node.getLeftSuccessor() != null){

			if (node.question(X)){

				node = node.getLeftSuccessor();

			}
			else{

				node = node.getRightSuccessor();

			}

		}

		return node;

	}

	// ----------------------------------------------------------
	// Build a variables list
	// ----------------------------------------------------------
	private void listInputVariables(TrainingDataSet dataset){

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

	}

	// ----------------------------------------------------------
	// Method to list modalities of categorical input variables
	// ----------------------------------------------------------
	private void listInputModalities(TrainingDataSet dataset){

		int n = dataset.getTrainingDataNumber();

		for (int i=0; i<n; i++){

			InputData X = dataset.getData(i).getInputData();

			for (int j=0; j<X.getFeaturesNumber(); j++){

				if (!X.isCategorical(j)){continue;}

				String name = X.getFeatureName(j);

				if (!CAT_VARIABLES.containsKey(name)){

					Hashtable<String, Integer> MODALITIES = new Hashtable<String, Integer>();

					CAT_VARIABLES.put(name, MODALITIES);

				}

				CAT_VARIABLES.get(name).put(X.getCategoricalFeature(j), 0);

			}

		}

	}

	// ----------------------------------------------------------
	// Method to all sub-partitions of a set of n elements
	// ----------------------------------------------------------
	public static ArrayList<ArrayList<String>> subPartition(Hashtable<String, Integer> cat){

		Enumeration<String> keys = cat.keys();
		ArrayList<ArrayList<String>> SUBS = new ArrayList<ArrayList<String>>();

		// Ensemble vide
		SUBS.add(new ArrayList<String>());  

		while (keys.hasMoreElements()){

			int np = SUBS.size();

			String classe = keys.nextElement();

			for (int k=0; k<np; k++){

				@SuppressWarnings("unchecked")
				ArrayList<String> newPartition = (ArrayList<String>) SUBS.get(k).clone();

				newPartition.add(classe);

				SUBS.add(newPartition);

			}

		}

		return SUBS;

	}	

}
