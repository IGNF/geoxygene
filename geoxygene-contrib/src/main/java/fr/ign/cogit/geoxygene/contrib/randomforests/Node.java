package fr.ign.cogit.geoxygene.contrib.randomforests;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

// ===============================================================
// CLASS FOR TREE NODES
// ===============================================================
// Node in the classical and randomized trees
// Each node contains an upper parameter, a list of training data 
// and a variable index j to not that split is done on Xj. 
// In a tree structure, it also has a right successor and and left 
// successor plus the associated navigation functionnalities.
// ===============================================================

public class Node {

	// ----------------------------------------------------------
	// Attributes
	// ----------------------------------------------------------

	private String j;	 // Split variable Xj index			        

	// Split threshold for ordered variables 
	private double threshold = Double.MIN_VALUE;            

	// Split for categorical variables
	private ArrayList<String> categories;   

	private TrainingDataSet dataset;

	private Node right_successor = null;
	private Node left_successor = null;

	private int degree;

	// A posteriori probabilities (classif)
	private Hashtable<String, Double> A_POSTERIORI;

	// A posteriori y estimation (regression)
	private double yhat;

	// Mode estimation
	private String mode = "";

	// ----------------------------------------------------------
	// Standard constructor 
	// ----------------------------------------------------------
	public Node(){

		A_POSTERIORI = new Hashtable<String, Double>();

		categories = new ArrayList<String>();

	}

	// ----------------------------------------------------------
	// Alternative constructo 
	// ----------------------------------------------------------
	public Node(String j, double threshold){

		this.j = j;
		this.threshold = threshold;

		A_POSTERIORI = new Hashtable<String, Double>();

		categories = new ArrayList<String>();

	}

	// ----------------------------------------------------------
	// Alternative constructor 
	// ----------------------------------------------------------
	public Node(String j, double threshold, TrainingDataSet dataset){

		this.j = j;
		this.threshold = threshold;
		this.dataset = dataset;

		A_POSTERIORI = new Hashtable<String, Double>();

		categories = new ArrayList<String>();

	}


	// ----------------------------------------------------------
	// Getters
	// ----------------------------------------------------------
	public String getSplitVariable(){return j;}
	public double getThreshold(){return threshold;}
	public TrainingDataSet getDataSet(){return dataset;}
	public Node getRightSuccessor(){return right_successor;}
	public Node getLeftSuccessor(){return left_successor;}
	public int getDegree(){return degree;}

	// Get split categories
	public ArrayList<String> getCategories(){return categories;}

	// ----------------------------------------------------------
	// Setters
	// ----------------------------------------------------------
	public void setSplitIndex(String j){this.j = j;}
	public void setThreshold(double threshold){this.threshold = threshold;}
	public void setDataSet(TrainingDataSet dataset){this.dataset = dataset;}
	public void setRightSuccessor(Node right_successor){this.right_successor = right_successor;}
	public void setLeftSuccessor(Node left_successor){this.left_successor = left_successor;}
	public void setDegree(int degree){this.degree = degree;}

	// Set split categories
	public void setCategories(ArrayList<String> categories){this.categories = categories;}


	// ----------------------------------------------------------
	// Terminal node
	// ----------------------------------------------------------
	public boolean isTerminal(){

		return (left_successor == null);

	}

	// ----------------------------------------------------------
	// Compute a posteriori estimation
	// ----------------------------------------------------------
	public void computeAPosterioriEstimation(){

		int N = dataset.getTrainingDataNumber();

		yhat = 0;

		for (int i=0; i<N; i++){

			double y = dataset.getData(i).getOutputData().getOrderedValue();

			yhat += y;

		}

		yhat /= N;

	}

	// ----------------------------------------------------------
	// Compute a posteriori probabilities
	// ----------------------------------------------------------
	public void computeAPosterioriProbabilities(){

		int N = dataset.getTrainingDataNumber();

		double max = 0;

		for (int i=0; i<N; i++){

			String classe = dataset.getData(i).getOutputData().getCategoricalValue();

			if (!A_POSTERIORI.containsKey(classe)){

				A_POSTERIORI.put(classe, 1.0/N);

			}
			else{

				A_POSTERIORI.put(classe, A_POSTERIORI.get(classe)+1.0/N);

			}

			// Updating mode
			if (A_POSTERIORI.get(classe) > max){

				mode = classe;
				max = A_POSTERIORI.get(classe);

			}

		}

	}

	// ----------------------------------------------------------
	// Method to know direction after node
	// ----------------------------------------------------------
	public boolean question(InputData X){

		// Ordered input variable
		if (X.isOrdered(j)){

			return (X.getOrderedFeature(j) <= threshold);

		}

		// Ordered input variable
		if (X.isCategorical(j)){

			return (categories.contains(X.getCategoricalFeature(j)));

		}

		return false;

	}


	// ----------------------------------------------------------
	// String converter
	// ----------------------------------------------------------
	public String toString(){

		if (!isTerminal()){

			if (categories.size() == 0){
				return "Node (degree "+degree+"), split condition "+j+" < "+threshold;
			}
			else{
				return "Node (degree "+degree+"), split condition "+j+" in "+categories;
			}

		}

		String retour = "Node (degree "+degree+"), terminal : ";

		Enumeration<String> KEYS = A_POSTERIORI.keys();

		while(KEYS.hasMoreElements()){

			String key = KEYS.nextElement();
			retour += "P(Y="+key+"|t) = "+Math.floor(A_POSTERIORI.get(key)*1000)/1000+"  ";

		}

		retour += " yhat = "+ yhat;

		return retour;


	}

	// ----------------------------------------------------------
	// Results getters
	// ----------------------------------------------------------
	public double getYhat(){return yhat;}

	public double getAPosteriori(String category){

		if (A_POSTERIORI.containsKey(category)){

			return A_POSTERIORI.get(category);

		}
		else{

			return 0.0;

		}

	}

	public String getMode(){return mode;}

}
