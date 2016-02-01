package fr.ign.cogit.geoxygene.contrib.randomforests;

import java.util.Comparator;


@SuppressWarnings("rawtypes")
public class DataComparator implements Comparator {


	private String j = "";
	
	public DataComparator(String j){
		
		this.j = j;
		
	}

	public void setj(String j){this.j = j;}

	@Override
	public int compare(Object o1, Object o2) {

		TrainingData data1 = (TrainingData) o1;
		TrainingData data2 = (TrainingData) o2;

		if (data1.getInputData().getOrderedFeature(j) > data2.getInputData().getOrderedFeature(j)){

			return 1;

		}

		if (data1.getInputData().getOrderedFeature(j) == data2.getInputData().getOrderedFeature(j)){

			return 0;

		}

		return -1;
	}

}
