package fr.ign.cogit.geoxygene.contrib.randomforests;

// ===============================================================
// CLASS FOR TRAINING DATA (X,Y)
// ===============================================================
// Training data (X,Y) with :
//    - X a InputData
//    - Y an OutputData
// X may contain ordered (numeric) and/or categorical variables
// Y is whether ordered or categorical
// To build a training data sets, all output types Y of training 
// data should be the same (regression or classification problem)
//===============================================================

public class TrainingData {

	// ----------------------------------------------------------
	// Attributes
	// ----------------------------------------------------------
	private InputData X;
	private OutputData Y;


	// ----------------------------------------------------------
	// Standard constructor
	// ----------------------------------------------------------
	public TrainingData(InputData X, OutputData Y){

		this.X = X;
		this.Y = Y;

	}

	// ----------------------------------------------------------
	// Getters
	// ----------------------------------------------------------
	public InputData getInputData(){return X;}
	public OutputData getOutputData(){return Y;}

	// ----------------------------------------------------------
	// Setters
	// ----------------------------------------------------------
	public void setInputData(InputData X){this.X = X;}
	public void setOutputData(OutputData Y){this.Y = Y;}


	// ----------------------------------------------------------
	// String transformation for print
	// ----------------------------------------------------------
	public String toString(){

		String chaine = "";

		for (int i=0; i<X.getFeaturesNumber(); i++){

			if (X.isOrdered(i)){

				chaine += X.getFeatureName(i)+"="+X.getOrderedFeature(i)+" ";

			}
			else{

				chaine += X.getFeatureName(i)+"="+X.getCategoricalFeature(i)+" ";

			}

		}


		if (Y.isOrdered()){

			chaine += " "+Y.getName()+"="+Y.getOrderedValue();

		}
		else{

			chaine += " "+Y.getName()+"="+Y.getCategoricalValue();

		}
		
		return chaine;

	}

}
