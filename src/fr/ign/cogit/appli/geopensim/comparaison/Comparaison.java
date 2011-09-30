/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.appli.geopensim.comparaison;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;

/**
 * @author Florence Curie
 *
 */
public abstract class Comparaison {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(Comparaison.class.getName());
	
	/**
	 * Calcul de la valeur agrégée
	 * @param 
	 */
	public static double CalculValeurAgregee(Method methode, String operation, List<ZoneElementaireUrbaine> listeZE){
		List<Object> listeValeurs = new ArrayList<Object>();
		for (ZoneElementaireUrbaine zoneElementaire : listeZE){
			Object valeur = null;
			try {
				valeur = methode.invoke(zoneElementaire, (Object[]) null);
				listeValeurs.add(valeur);
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		double valeurAgregee = 0;
		double compt=0;
		for (Object val : listeValeurs){
			compt++;
			if(val.getClass().equals(Double.class)){
				valeurAgregee += (Double)val;
			}else if(val.getClass().equals(Integer.class)){
				valeurAgregee += (Integer)val;
			}
		}
		if(operation.equals("moyenne")){
			valeurAgregee = valeurAgregee/compt;
		}
		return valeurAgregee;
	}
	
	/**
	 * Calcul de la distance entre deux états
	 * @param 
	 */
	public static double CalculDistance(List<Double> listeValAgregeeEtat1,List<Double> listeValAgregeeEtat2,List<Double> listePonderations){
		
		double distance = 0;
		double sommePonderation = 0;
		for (int i=0;i<listePonderations.size();i++){
			distance += listePonderations.get(i) * Math.pow(listeValAgregeeEtat1.get(i)-listeValAgregeeEtat2.get(i),2);
			sommePonderation += listePonderations.get(i);
		}
		distance = distance/sommePonderation;
		distance = Math.sqrt(distance);
		return distance;
	}
	
	/**
	 * Calcul de la distance entre deux états
	 * @param 
	 */
	public static double CalculDistance(List<Double> listeDifference,List<Double> listePonderations){
		
		double distance = 0;
		double sommePonderation = 0;
		for (int i=0;i<listePonderations.size();i++){
			distance += listePonderations.get(i) * Math.pow(listeDifference.get(i),2);
			sommePonderation += listePonderations.get(i);
		}
		distance = distance/sommePonderation;
		distance = Math.sqrt(distance);
		return distance;
	}
}
