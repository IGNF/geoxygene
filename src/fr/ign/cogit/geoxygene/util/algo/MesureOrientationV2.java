/*
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
 * 
 */
package fr.ign.cogit.geoxygene.util.algo;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;




/**
 * Méthodes de calcul de l'orientation générale et de l'orientation des murs d'un polygone.
 * 
 * @author Julien Gaffuri et Florence Curie
 *
 */
public abstract class MesureOrientationV2 {

	/**
	 * Orientation d'une geometrie (en radian entre 0 et Pi, par rapport a l'axe Ox). c'est l'orientation du PPRE.
	 * @return l'orientation de la géométrie : 999.9 si le PPRE n'est pas defini, ou s'il est carré.
	 */
	public static double getOrientationGenerale(Geometry geom){

		//calcul du PPRE
		Polygon ppre=JtsUtil.PPRE(geom);

		if (ppre == null)
			return 999.9;

		//recupere le plus long cote
		Coordinate[] coords=ppre.getCoordinates();
		double lg1=coords[0].distance(coords[1]);
		double lg2=coords[1].distance(coords[2]);
		if (lg1==lg2) return 999.9;

		//l'orientation est suivant c1,c2
		Coordinate c1,c2;
		if (lg1>lg2) {c1=coords[0]; c2=coords[1]; }
		else {c1=coords[1]; c2=coords[2]; }

		//calcul de l'orientation du plus long cote
		double angle=Math.atan((c1.y-c2.y)/(c1.x-c2.x));
		if (angle<0) angle+=Math.PI;
		return angle;
	}

	/**
	 * Qualification de la biscornuité d'un polygone à partir des orientations mesurées.
	 */
	public static String getBiscornuite(Geometry geom) {

		// Constantes
		double seuilComparaisonOrientation=Math.PI/36; // (= 5°) angle en deça duquel l'orientation du batiment et l'orientation des cotes sont considérées identiques
		double seuilIndicateurConfiance=0.8; // seuil de l'indicateur de confiance à partir duquel on considére la mesure comme fiable 

		MesureOrientationFeuille mesureOrientation = new MesureOrientationFeuille(geom,Math.PI*0.5);
		boolean pertinence = mesureOrientation.getPertinence();

		String biscornuite="";
		// l'orientation des murs n'a pas de sens
		if (!pertinence) biscornuite = "aucune_orientation";
		else{
			int nbOrient = mesureOrientation.getNombreOrientation();
			if(nbOrient > 1){
				double contribSecondaire = mesureOrientation.getContributionSecondaire();
				double contribPrincipale = mesureOrientation.getContributionPrincipale();
				double partSecondaire = contribSecondaire/contribPrincipale*100;
				if(partSecondaire>40){// l'orientation secondaire est importante
					biscornuite = "plusieurs_orientations";
				}
				else{// l'orientation principale reste majoritaire
					biscornuite = "un_mur_different";
				}
			}
			else{
				double elongation = JtsUtil.elongation(geom);
				double orientationGenerale = getOrientationGenerale(geom);
				if (orientationGenerale>=0.5*Math.PI) orientationGenerale-=0.5*Math.PI;
				double orientationPrincipale = mesureOrientation.getOrientationPrincipale();
				double diffOrientation = (Math.abs(orientationGenerale-orientationPrincipale));
				if(diffOrientation>(Math.PI/4)) diffOrientation = (0.5*Math.PI)-diffOrientation;
				double indicateur = mesureOrientation.getIndicateurOrientationPrincipale();
				if(indicateur>=seuilIndicateurConfiance){ // les murs sont à peu près droits
					if((diffOrientation>=seuilComparaisonOrientation)&&(elongation<0.4)){
						// l'orientation du batiment est la même que celle des murs
						biscornuite = "escalier_mur_droit";
					}
					else{// l'orientation du batiment est différente de celle des murs et le batiment est alongé
						biscornuite = "rectangulaire_mur_droit";
					}
				}
				else{ // les murs ne sont pas très droits 
					if((diffOrientation>=seuilComparaisonOrientation)&&(elongation<0.4)){
						// l'orientation du batiment est la même que celle des murs
						biscornuite = "escalier_mur_peu_droit";
					}
					else{// l'orientation du batiment est différente de celle des murs et le batiment est alongé
						biscornuite = "rectangulaire_mur_peu_droit";
					}
				}
			}
		}
		return biscornuite;
	}
}
