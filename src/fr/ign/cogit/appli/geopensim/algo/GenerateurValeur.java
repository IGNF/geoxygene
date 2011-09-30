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

package fr.ign.cogit.appli.geopensim.algo;


import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.Distribution;

/**
 * @author Florence Curie
 *
 */
public class GenerateurValeur {

	private static Logger logger=Logger.getLogger(GenerateurValeur.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Distribution distrib = new Distribution(TypeDistribution.Aleatoire, 2, 8, 5, -1);
		double valeur = genererValeur(distrib);
		logger.info(valeur);
	}
	
	
	public static double genererValeur(Distribution distrib){
		
		double valeur = -1.0;
		if(distrib!=null){
			if (distrib.getTypeDistribution()==TypeDistribution.Aleatoire){
				if ((distrib.getMinimum()!=-1)&&(distrib.getMaximum()!=-1)){
					valeur = genererValeurAleatoire(distrib.getMinimum(), distrib.getMaximum());
				}
			}else if(distrib.getTypeDistribution() == TypeDistribution.Normale){
				if ((distrib.getMinimum()!=-1)&&(distrib.getMaximum()!=-1)){
					valeur = genererValeurLoiNormale(distrib.getMinimum(), distrib.getMaximum(), 2);
				}else if((distrib.getMoyenne()!=-1)){
					double ecartT = distrib.getEcartType();
					if (ecartT == -1){
						ecartT = 10*distrib.getMoyenne()/100;
					}
					valeur = genererValeurLoiNormale(distrib.getMoyenne(), ecartT);
				}
			}
		}
		return valeur;
	}
	
	
	public static double genererValeurLoiNormaleBM(double moy,double dev){
        
		// méthode de Box-Muller
		double w,x1,x2,y1;
		
        do {
                x1 = 2.0 * Math.random() - 1.0;
                x2 = 2.0 * Math.random() - 1.0;
                w = x1*x1+x2*x2;
        } while (w >= 1.0);

        w = Math.sqrt((-2.0*Math.log(w))/w);
        y1 = (x1*w)*dev+moy;
//        y2 = (x2*w)*dev+moy;
		
		return y1;
	}
	
	public static double genererValeurLoiNormale(double moy,double dev){
        
		double x1,x2,y1,y2;
		
		x1 = Math.random();
		x2 = Math.random();
		
		y1 = Math.sqrt(-2*Math.log(x1))*Math.cos(2*Math.PI*x2);
		y2 = Math.sqrt(-2*Math.log(x1))*Math.sin(2*Math.PI*x2);
		
		y1 = y1*dev+moy;
		y2 = y2*dev+moy;
		
		return y1;
	}
	
	public static double genererValeurLoiNormale(double min,double max,double var){
        
		double moy = (min+max)/2;
		double dev = (max-moy)/var;
		return genererValeurLoiNormale(moy,dev);
	}
	
	/**
	 * @return une valeur aléatoire comprise entre a et b
	 */
	public static double genererValeurAleatoire(double min, double max){
		double valAlea = min+Math.random()*(max-min);
		return valAlea;
	}

}
