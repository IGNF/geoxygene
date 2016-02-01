package fr.ign.cogit.geoxygene.contrib.randomforests;



// ===============================================================
// CLASS FOR OUTPUT DATA Y
// ===============================================================
// Each data is an output features, with a type
//   - Ordered (whether continuous or discrete)
//   - Categorical
// Output data Y doesn't contain explaining input for training
//===============================================================

public class OutputData {

	// ----------------------------------------------------------
	// Attributes
	// ----------------------------------------------------------
	private String name;
	private double value_numeric = Double.NaN;
	private String value_categorical = "Undefined";
	private boolean isnumeric;


	// ----------------------------------------------------------
	// Standard constructor (default ordered)
	// ----------------------------------------------------------
	public OutputData(String name, double  value){

		this.name = name;
		value_numeric = value;
		isnumeric = true; 

	}

	// ----------------------------------------------------------
	// Alterative constructor (default categorical)
	// ----------------------------------------------------------
	public OutputData(String name, String category){

		this.name = name;
		value_categorical = category;
		isnumeric = false; 

	}

	// ----------------------------------------------------------
	// Getters
	// ----------------------------------------------------------
	public String getName(){return name;}
	public double getOrderedValue(){return value_numeric;}
	public String getCategoricalValue(){return value_categorical;}
	public boolean isOrdered(){return isnumeric;}
	public boolean  isCategorical(){return !isnumeric;}

	// ----------------------------------------------------------
	// Setters
	// ----------------------------------------------------------
	public void setName(String name){this.name = name;}

	public void setValue(double value){

		this.value_numeric = value;
		isnumeric = true;

	}

	public void setValue(String category){

		this.value_categorical = category;
		isnumeric = false;

	}

	// ----------------------------------------------------------
	// String transformation for print
	// ----------------------------------------------------------
	public String toString(){
		
		String chaine = "OUTPUT VALUE -> ";
		
		if (isOrdered()){
			
			chaine += " "+getName()+" = "+getOrderedValue();
			
		}
		else{
			
			chaine += " "+getName()+" = "+getCategoricalValue();
			
		}
		
		return chaine;
		
	}

}
