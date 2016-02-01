package fr.ign.cogit.geoxygene.contrib.randomforests;

import java.util.ArrayList;
import java.util.Hashtable;

// ===============================================================
// CLASS FOR TRAINING DATA SET {Di = (Xi,Yi), i = [0, n-1]}
// ===============================================================
// A training data set contains n training data (Xi,Yi) with :
//    - Xi a InputData
//    - Yi an OutputData
// A training data set can contain an arbitrary great number of
// data. In order to be passed in a decision tree or a random 
// forest, all outputs Yi must have the same type (ordered -> 
// regression, categorical -> classification). 
//===============================================================

public class TrainingDataSet {

	// ----------------------------------------------------------
	// Attributes
	// ----------------------------------------------------------
	private ArrayList<TrainingData> DATALIST;
	private String name = "undefined";

	// ----------------------------------------------------------
	// Standard constructor Attributes
	public TrainingDataSet(){DATALIST = new ArrayList<TrainingData>();}

	// ----------------------------------------------------------
	// Getters
	// ----------------------------------------------------------
	public TrainingData getData(int i){return DATALIST.get(i);}
	public int getTrainingDataNumber(){return DATALIST.size();}
	public ArrayList<TrainingData> toList(){return DATALIST;}
	public String getName(){return name;}

	// ----------------------------------------------------------
	// Setters
	// ----------------------------------------------------------
	public void setTrainingData(int i, TrainingData D){DATALIST.set(i, D);}
	public void setName(String name){this.name = name;}

	// ----------------------------------------------------------
	// Add a new training data
	// ----------------------------------------------------------
	public void addData(TrainingData data){

		DATALIST.add(data);

	}

	// ----------------------------------------------------------
	// Remove a data from its index
	// ----------------------------------------------------------
	public void removeData(int index){

		DATALIST.remove(index);

	}

	// ----------------------------------------------------------
	// Clear Training Data Set
	// ----------------------------------------------------------
	public void clear(){DATALIST = new ArrayList<TrainingData>();}

	// ----------------------------------------------------------
	// Copying dataset
	// ----------------------------------------------------------
	public TrainingDataSet copy(){

		TrainingDataSet TDS = new TrainingDataSet();

		TDS.name = this.name;
		TDS.DATALIST = this.DATALIST;

		return TDS;

	}

	// ----------------------------------------------------------
	// String transformation for print
	// ----------------------------------------------------------
	public String toString(){

		String chaine = "";

		chaine += "---------------------------------------------\r\n";
		chaine += "TRAINING DATASET : "+this.name;
		chaine += " ("+DATALIST.size()+" DATA)  \r\n";
		chaine += "---------------------------------------------\r\n";

		for (int i=0; i<DATALIST.size(); i++){

			String sous_chaine = "TRAINING DATA "+i+" -> "+DATALIST.get(i);


			chaine += sous_chaine+="\r\n";

		}

		return chaine;

	}


	// ----------------------------------------------------------
	// Method to extract a part of a training dataset
	// All data before (true) or after (false) the index would be
	// extracted
	// ----------------------------------------------------------
	public TrainingDataSet extract(int index, boolean before){

		TrainingDataSet extracted = new TrainingDataSet();

		for (int i=0; i<getTrainingDataNumber(); i++){

			if ((before)&&(i <= index)){extracted.addData(getData(i));}

			if ((!before)&&(i >= index)){extracted.addData(getData(i));}

		}

		return extracted;

	}

	// ----------------------------------------------------------
	// Method to subpartition a training data set according to 
	// an indices table
	// ----------------------------------------------------------
	public ArrayList<TrainingDataSet> extract(ArrayList<Integer> indices){

		ArrayList<TrainingDataSet> EXTRACTED = new ArrayList<TrainingDataSet>();
		
		TrainingDataSet dataset = copy();
		
		for (int i=0; i<indices.size(); i++){
			
			int index = indices.get(i);
			
			EXTRACTED.add(dataset.extract(index, true));
			
			for (int j=0; j<index; j++){
				
				dataset.removeData(j);
				
			}
			
		}

		return EXTRACTED;

	}

	// ----------------------------------------------------------
	// Gnu plot debugging functionnality
	// ----------------------------------------------------------
	public void gnuPlot(){

		Hashtable<String, Double> OUTPUT_CLASSES = new Hashtable<String, Double>();
		ArrayList<String> KEYS = new ArrayList<String>();

		// Listing output classes
		for (int i=0; i<getTrainingDataNumber(); i++){

			String output_class = getData(i).getOutputData().getCategoricalValue();

			if (!OUTPUT_CLASSES.containsKey(output_class)){

				OUTPUT_CLASSES.put(output_class, 0.0);

				// Listing of keys
				KEYS.add(output_class);

			}

		}

		System.out.println("clf");
		System.out.println("hold on");

		System.out.println("M1 = [");

		for (int i=0; i<getTrainingDataNumber(); i++){

			if (!getData(i).getOutputData().getCategoricalValue().equals(KEYS.get(0))){

				continue;

			}

			System.out.print(getData(i).getInputData().getOrderedFeature(0)+", ");
			System.out.println(getData(i).getInputData().getOrderedFeature(1)+";");

		}

		System.out.println("];");

		System.out.println("M2 = [");

		for (int i=0; i<getTrainingDataNumber(); i++){

			if (!getData(i).getOutputData().getCategoricalValue().equals(KEYS.get(1))){

				continue;

			}

			System.out.print(getData(i).getInputData().getOrderedFeature(0)+", ");
			System.out.println(getData(i).getInputData().getOrderedFeature(1)+";");

		}

		System.out.println("];");

		System.out.println("plot(M1(:,1),M1(:,2),'ro');");
		System.out.println("plot(M2(:,1),M2(:,2),'go');");

	}

}
