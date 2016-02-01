package fr.ign.cogit.geoxygene.contrib.randomforests;

import java.util.ArrayList;
import java.util.Hashtable;

// ===============================================================
// CLASS FOR INPUT DATA X = (X1, X2, X3..., Xn)
// ===============================================================
// Each data is a set of input features, each features being Xi
// being identified by a string name "Xi" and an index from 0 to 
// (n-1) where n is the number of features included in X.
// Each input variable is also associated with a type :
//   - Ordered (whether continuous or discrete)
//   - Categorical
// Input data X doesn't contain expected output for training
// ===============================================================

public class InputData {

	// ----------------------------------------------------------
	// Attributes
	// ----------------------------------------------------------
	private ArrayList<String> TABLE_INDEX;
	private Hashtable<String, Double> TABLE_ORDERED;     
	private Hashtable<String, String> TABLE_CATEGORICAL;  

	// ----------------------------------------------------------
	// Getters
	// ----------------------------------------------------------
	public int getFeaturesNumber(){return TABLE_INDEX.size();}
	public int getOrderedFeaturesNumber(){return TABLE_ORDERED.size();}
	public int getCategoricalFeaturesNumber(){return TABLE_CATEGORICAL.size();}

	// ----------------------------------------------------------
	// Standard constructor
	// ----------------------------------------------------------
	public InputData(){

		TABLE_ORDERED = new Hashtable<String, Double>();
		TABLE_CATEGORICAL = new Hashtable<String, String>();

		TABLE_INDEX = new ArrayList<>();

	}

	// ----------------------------------------------------------
	// Adding a feature by its name and its value
	// Default type : ordered
	// ----------------------------------------------------------
	public void addFeature(String name, double value){

		// Tests
		test_no_exists(name);

		TABLE_ORDERED.put(name, value);
		TABLE_INDEX.add(name);

	}

	// ----------------------------------------------------------
	// Adding a feature by its name and its category
	// Default type : categorical
	// ----------------------------------------------------------
	public void addFeature(String name, String category){

		// Tests
		test_no_exists(name);

		TABLE_CATEGORICAL.put(name, category);
		TABLE_INDEX.add(name);

	}

	// ----------------------------------------------------------
	// Modifying a feature by its name and its value
	// Default type : ordered
	// ----------------------------------------------------------
	public void setFeature(String name, double value){

		// Tests
		test_exists(name);

		if (isCategorical(name)){

			halfDelete(name);

		}

		TABLE_ORDERED.put(name, value);

	}

	// ----------------------------------------------------------
	// Modifying a feature by its name and its category
	// Default type : categorical
	// ----------------------------------------------------------
	public void setFeature(String name, String category){

		// Tests
		test_exists(name);

		if (isOrdered(name)){

			halfDelete(name);

		}

		TABLE_CATEGORICAL.put(name, category);

	}

	// ----------------------------------------------------------
	// Modifying a feature by its index and its value
	// Default type : ordered
	// ----------------------------------------------------------
	public void setFeature(int index, double value){

		String name = getFeatureName(index);

		// Tests
		test_exists(name);

		if (isCategorical(name)){

			halfDelete(name);

		}

		TABLE_ORDERED.put(name, value);

	}

	// ----------------------------------------------------------
	// Modifying a feature by its name and its category
	// Default type : categorical
	// ----------------------------------------------------------
	public void setFeature(int index, String category){

		String name = getFeatureName(index);

		// Tests
		test_exists(name);

		if (isOrdered(name)){

			halfDelete(name);

		}

		TABLE_CATEGORICAL.put(name, category);

	}

	// ----------------------------------------------------------
	// Getting the value of an ordered feature from its name
	// ----------------------------------------------------------
	public double getOrderedFeature(String name){

		// Recherhe dans les deux types

		if (TABLE_ORDERED.containsKey(name)){

			return TABLE_ORDERED.get(name);

		}
		else{

			System.out.println("Error : no ordered input feature named "+name);
			System.exit(1);

		}

		return 0;


	}


	// ----------------------------------------------------------
	// Getting the value of a categorical feature from its name
	// ----------------------------------------------------------
	public String getCategoricalFeature(String name){

		// Recherhe dans les deux types

		if (TABLE_CATEGORICAL.containsKey(name)){

			return TABLE_CATEGORICAL.get(name);

		}
		else{

			System.out.println("Error : no categorical input feature named "+name);
			System.exit(1);

		}

		return "0";


	}

	// ----------------------------------------------------------
	// Getting the value of an ordered feature from its index
	// ----------------------------------------------------------
	public double getOrderedFeature(int index){

		String name = getFeatureName(index);

		if (isOrdered(name)){

			return TABLE_ORDERED.get(name);

		}

		System.out.println("Error : feature "+name+" is not ordered");
		System.exit(1);

		return 0;


	}


	// ----------------------------------------------------------
	// Getting the value of a categorical feature from its index
	// ----------------------------------------------------------
	public String getCategoricalFeature(int index){

		String name = getFeatureName(index);

		if (isCategorical(name)){

			return TABLE_CATEGORICAL.get(name);

		}

		System.out.println("Error : feature "+name+" is not categorical");
		System.exit(1);

		return "0";

	}

	// ----------------------------------------------------------
	// Getting variable name
	// ----------------------------------------------------------
	public String getFeatureName(int index){

		if (index >= TABLE_INDEX.size()){

			System.out.println("Error : index greater or equal to the number of input features");
			System.exit(1);

		}

		return TABLE_INDEX.get(index);

	}


	// ----------------------------------------------------------
	// Testing type of a variable from its name
	// ----------------------------------------------------------
	public boolean isOrdered(String name){

		return TABLE_ORDERED.containsKey(name);

	}

	// ----------------------------------------------------------
	// Testing type of a variable from its index
	// ----------------------------------------------------------
	public boolean isOrdered(int index){

		return TABLE_ORDERED.containsKey(getFeatureName(index));

	}

	// ----------------------------------------------------------
	// Testing type of a variable from its name
	// ----------------------------------------------------------
	public boolean isCategorical(String name){

		return TABLE_CATEGORICAL.containsKey(name);

	}

	// ----------------------------------------------------------
	// Testing type of a variable from its index
	// ----------------------------------------------------------
	public boolean isCategorical(int index){

		return TABLE_CATEGORICAL.containsKey(getFeatureName(index));

	}

	// ----------------------------------------------------------
	// Testing type of a variable exists
	// ----------------------------------------------------------
	public boolean isFeature(String name){

		return (isOrdered(name) || isCategorical(name));

	}

	// ----------------------------------------------------------
	// Deleting a feature from its name
	// ----------------------------------------------------------
	public void delete(String name){

		test_exists(name);

		if (isOrdered(name)){TABLE_ORDERED.remove(name);}
		if (isCategorical(name)){TABLE_CATEGORICAL.remove(name);}

		for (int i=0; i<TABLE_INDEX.size(); i++){

			if (TABLE_INDEX.get(i).equals(name)){

				TABLE_INDEX.remove(i);
				break;

			}

		}

	}

	// ----------------------------------------------------------
	// Deleting a feature from its index
	// ----------------------------------------------------------
	public void delete(int index){

		delete(getFeatureName(index));

	}

	// ----------------------------------------------------------
	// Half deleting a feature from its name (remembering index)
	// ----------------------------------------------------------
	private void halfDelete(String name){

		test_exists(name);

		if (isOrdered(name)){TABLE_ORDERED.remove(name);}
		if (isCategorical(name)){TABLE_CATEGORICAL.remove(name);}

	}


	// ----------------------------------------------------------
	// Conversion string pour affichage console
	// ----------------------------------------------------------
	public String toString(){

		String chaine = "";
		String val = "";
		String name = "";
		String type = "";

		chaine += "---------------------------------------------------- \r\n";
		chaine += "NUMBER OF FEATURES : "+getFeaturesNumber();
		chaine += "  (ORD : "+getOrderedFeaturesNumber()+" ";
		chaine += " CAT : "+getCategoricalFeaturesNumber()+") \r\n";
		chaine += "---------------------------------------------------- \r\n";

		for (int i=0; i<TABLE_INDEX.size(); i++){

			name = TABLE_INDEX.get(i);

			if (TABLE_ORDERED.containsKey(name)){

				val = "" + TABLE_ORDERED.get(name);
				type = "ORDERED";

			}

			if (TABLE_CATEGORICAL.containsKey(name)){

				val = TABLE_CATEGORICAL.get(name);
				type = "CATEGORICAL";

			}


			chaine += "X"+i+" -> VAR_NAME = "+name+"  ";
			chaine += "VAL = "+val+"  ";
			chaine += "TYPE = "+type+"  ";
			chaine += "\r\n";

		}

		return chaine;

	}


	// ----------------------------------------------------------
	// Testing of the non-existence of pre-defined input features
	// ----------------------------------------------------------
	private void test_no_exists(String name){

		if (TABLE_ORDERED.containsKey(name)){

			System.out.println("Error : ordered input feature "+name+" has already been defined");
			System.exit(1);


		}

		// Tests
		if (TABLE_CATEGORICAL.containsKey(name)){

			System.out.println("Error : categorical input feature "+name+" has already been defined");
			System.exit(1);


		}

	}

	// ----------------------------------------------------------
	// Testing of the existence of pre-defined input features
	// ----------------------------------------------------------
	private void test_exists(String name){

		if ((!TABLE_ORDERED.containsKey(name)) && (!TABLE_CATEGORICAL.containsKey(name))){

			System.out.println("Error : ordered input feature "+name+" hasn't been defined");
			System.exit(1);


		}

	}

}
