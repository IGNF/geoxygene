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
import java.util.HashMap;

//=======================================================================
// Class to store parameters
// Date : 29/03/2015
// Contact : yann.meneroux@ign.fr
//=======================================================================

public class Parameters {

	// Table de variables
	private HashMap<String, Double> PARAMETERS;

	// Table d'index sur les valeurs numériques
	private ArrayList<String> INDEX;

	// Number of parameters
	public int getParametersNumber(){return PARAMETERS.size();}

	// -------------------------------------------------------------------
	// General method to build parameters table
	// -------------------------------------------------------------------
	public Parameters(){

		PARAMETERS = new HashMap<String, Double>();
		INDEX = new ArrayList<String>();

	}

	// -------------------------------------------------------------------
	// Method to implement a parameter with a defined value
	// Input : parameter name (string) and initial value (double)
	// -------------------------------------------------------------------
	public void setParameter(String name, double value){

		if (!PARAMETERS.containsKey(name)){

			INDEX.add(name);

		}

		PARAMETERS.put(name, value);

	}


	// -------------------------------------------------------------------
	// Method to implement a parameter with a defined value
	// Input : parameter name (string) and initial value (double)
	// -------------------------------------------------------------------
	public void setParameter(int index, double value){

		if (index >= INDEX.size()){

			System.out.println("Error : parameter with index "+index+" doesn't exist");
			System.exit(0);

		}

		PARAMETERS.put(INDEX.get(index), value);

	}


	// -------------------------------------------------------------------
	// Method to get the value of a parameter
	// Input : parameter name (string)
	// Output : parameter value
	// -------------------------------------------------------------------
	public double getParameter(String name){

		if (!PARAMETERS.containsKey(name)){

			System.out.println("Error : parameter "+name+" is not defined");
			System.exit(0);

		}

		return PARAMETERS.get(name);

	}

	// -------------------------------------------------------------------
	// Method to get the name of a parameter from its index
	// Input : parameter index (integer)
	// Output : parameter name (string)
	// -------------------------------------------------------------------
	public String getParameterName(int index){

		return INDEX.get(index);

	}

	// -------------------------------------------------------------------
	// Method to get the value of a parameter from its index
	// Input : parameter index (integer)
	// Output : parameter value (double)
	// -------------------------------------------------------------------
	public double getParameter(int index){

		return getParameter(getParameterName(index));

	}

	// -------------------------------------------------------------------
	// Method to get index of a parameters from its name
	// Input : parameter name (string)
	// Output : parameter index (if it exists)
	// -------------------------------------------------------------------
	public int getIndex(String name){


		for (int i=0; i<INDEX.size(); i++){

			if (INDEX.get(i).equals(name)){

				return i;

			}

		}

		System.out.println("Error : parameter "+name+" is not defined");
		System.exit(0);
		return 0;

	}


	// -------------------------------------------------------------------
	// Method to increment a parameter with a defined value
	// Input : parameter name (string) and increment value (double)
	// -------------------------------------------------------------------
	public void incrementParameter(String name, double h){

		setParameter(name, getParameter(name)+h);

	}

	// -------------------------------------------------------------------
	// Method to increment a parameter from index with a defined value
	// Input : parameter index (integer) and increment value (double)
	// -------------------------------------------------------------------
	public void incrementParameter(int index, double h){

		setParameter(index, getParameter(index)+h);

	}


	// -------------------------------------------------------------------
	// Method to test if a parameters has been defined
	// Input : parameter name (string)
	// Output : boolean true if parameter has been defined
	// -------------------------------------------------------------------
	public boolean contains(String name){

		return PARAMETERS.containsKey(name);

	}

	// -------------------------------------------------------------------
	// Method to remove a parameter from its name
	// Input : parameter name (string)
	// Output : void
	// -------------------------------------------------------------------
	public void remove(String name){

		int index = getIndex(name);
		
		PARAMETERS.remove(name);
		INDEX.remove(index);

	}


}
