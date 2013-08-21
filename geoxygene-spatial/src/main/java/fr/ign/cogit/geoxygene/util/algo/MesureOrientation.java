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

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * Méthodes de calcul de l'orientation générale et de l'orientation des murs d'un polygone.
 * 
 * @author Julien Gaffuri
 *
 */
public class MesureOrientation {
	private static Logger logger=Logger.getLogger(MesureOrientation.class.getName());

	//nombre d'orientations testees dans l'intervalle [0, Pi/2[ pour le calcul de l'orientation moyenne des cotes
	private static int NB_ORIENTATIONS_TESTEES=90; 
	public static int getNB_ORIENTATIONS_TESTEES() {return NB_ORIENTATIONS_TESTEES;}

	// Constantes 
	private static double ANGLE_CONTRIBUTIF=Math.PI/18; // (= 10°) angle en deça duquel une arète contribue à l'orientation testée 
	private static double SEUIL_MICRO_FEUILLES=5; // longueur (en pourcentage du maximum théorique) en dessous de laquelle une contribution sera négligée
	private static double SEUIL_CONCAT_FEUILLES=5; // longueur (en pourcentage du maximum théorique) en dessous duquel un décrochement entre deux feuilles ne sera pas pris en compte (les deux feuilles seront concaténées) 
	private static double SEUIL_COMPARAISON_ORIENTATION=Math.PI/36; // (= 5°) angle en deça duquel l'orientation du batiment et l'orientation des cotes sont considérées identiques
	private static double SEUIL_INDICATEUR_CONFIANCE=0.8; // seuil de l'indicateur de confiance à partir duquel on considére la mesure comme fiable 
	
	private Geometry geom=null;

	/**
	 * Constructeur de mesure d'orientation à partir d'une géométrie JTS.
	 * @param geom géométrie sur laquelle on va calculer l'orientation.
	 */
	public MesureOrientation(Geometry geom){this.geom=geom;}

	/**
	 * Constructeur de mesure d'orientation à partir d'une géométrie geoxygene.
	 * @param geomGeox géométrie sur laquelle on va calculer l'orientation.
	 */
	public MesureOrientation(IGeometry geomGeox){
		try {this.geom=AdapterFactory.toGeometry(new GeometryFactory(), geomGeox);} catch (Exception e) {e.printStackTrace();}
	}

	private double[] contributionsCotesOrientation=null;
	/**
	 * @return tableau des contributions respectives des différents côtés à l'orientation des murs d'un polygone
	 */
	public double[] getContributionsCotesOrientation() {
		if (contributionsCotesOrientation==null) calculerContributionsCotesOrientation((Polygon)geom);
		return contributionsCotesOrientation;
	}

	private double orientationCotes=-999.9;
	private double contributionMax=-999.9;
	private int iMax=-1;
	private double contributionMaxTheorique = 0;

	/**
	 * @return orientation des murs d'un polygone
	 */
	public double getOrientationCotes() {
		if (orientationCotes==-999.9) calculerOrientationCote();
		return orientationCotes;
	}

	private double indicateurOrientationCote=-999.9;
	
	/**
	 * @return qualité de l'orientation des côtés d'un polygone.
	 */
	public double getIndicateurOrientationCote() {
		if (indicateurOrientationCote==-999.9) calculerIndicateurOrientationCote();
		return indicateurOrientationCote;
	}
	
	private boolean pertinence=false;
	/**
	 * @return Pertinence du calcul de l'orientation des côtés d'un polygone.
	 */
	public boolean getPertinence() {
		if (pertinence==false) determinerPertinence();
		return pertinence;
	}
	
	private String biscornuite="";
	/**
	 * @return biscornuite d'un polygone.
	 */
	public String getBiscornuite() {
		if (biscornuite.contentEquals("")) determinerBiscornuite();
		return biscornuite;
	}
	
	private double contributionSecondaire=-999.9;
	/**
	 * @return la valeur de l'orientation secondaire des côtés d'un polygone.
	 */
	public double getContributionSecondaire() {
		if (contributionSecondaire==-999.9) calculContributionSecondaire();
		return contributionSecondaire;
	}

	private int nombreOrientation=-1;
	private double[] maxiLocaux=null;
	/**
	 * @return True si il existe une orientation secondaire des côtés d'un polygone.
	 */
	public int getNombreOrientation() {
		if (nombreOrientation==-1) detecterOrientationSecondaire();
		return nombreOrientation;
	}

	/**
	 * Orientation d'une geometrie (en radian entre 0 et Pi, par rapport a l'axe Ox). c'est l'orientation du PPRE.
	 * @return l'orientation de la géométrie : 999.9 si le PPRE n'est pas defini, ou s'il est carré
	 */
	public double getOrientationGenerale(){

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
	 * Orientation des cotes d'un polygone (en radian, dans l'intervalle [0, Pi/2[, par rapport a l'axe Ox)
	 */
	public void calculerOrientationCote(){

		//calcul des contributions des cotes
		if (contributionsCotesOrientation==null) calculerContributionsCotesOrientation();

		if (logger.isDebugEnabled()) {
			logger.debug("contributions:");
			String st="";
			for(int i=1; i<contributionsCotesOrientation.length; i++) st+= ((int)contributionsCotesOrientation[i])+"  ";
			logger.debug(st);
		}

		//recupere l'index de la contribution maximale
		iMax = 0;
		contributionMax = contributionsCotesOrientation[0];
		for(int i=1; i<contributionsCotesOrientation.length; i++) {
			if (contributionsCotesOrientation[i] > contributionMax) {
				contributionMax = contributionsCotesOrientation[i];
				iMax=i;
			}
		}
		
		//renvoie l'angle correspondant a l'index max
		orientationCotes = 0.5*Math.PI*iMax/NB_ORIENTATIONS_TESTEES;
		
		// Calcul de la contribution maximal théorique
		contributionMaxTheorique = ((Polygon)geom).getExteriorRing().getLength();
		for(int i=0; i<((Polygon)geom).getNumInteriorRing(); i++) {//ajout des contributions des cotes des trous
			contributionMaxTheorique += ((Polygon)geom).getInteriorRingN(i).getLength();
		}
	}


	/**
	 * Calcul des contributions des côtés d'un polygone à son orientation.
	 */
	public void calculerContributionsCotesOrientation(){
		if (geom instanceof Polygon) calculerContributionsCotesOrientation( (Polygon)geom );
		else if (geom instanceof LineString) calculerContributionsCotesOrientation( (LineString)geom );
		else {
			logger.warn("attention: calcul de l'orientation de cotes non permise pour geometrie "+geom);
			orientationCotes=-999.9;
			return;
		}
	}

	/**
	 * Calcul des contributions des côtés d'un polygone à son orientation.
	 * @param poly polygone évalué
	 */
	public void calculerContributionsCotesOrientation(Polygon poly){
		if (logger.isDebugEnabled()) logger.debug("calcul des contributions des cotes a l'orientation moyenne des cotes de "+poly);

		//initialise la table des contributions
		contributionsCotesOrientation=new double[NB_ORIENTATIONS_TESTEES];
		for(int i=0; i<NB_ORIENTATIONS_TESTEES; i++) contributionsCotesOrientation[i]=0.0;

		//ajout des contributions des cotes de l'enveloppe exterieure
		ajouterContribution(poly.getExteriorRing());

		//ajout des contributions des cotes des trous
		for(int i=0; i<poly.getNumInteriorRing(); i++) ajouterContribution(poly.getInteriorRingN(i));
	}

	/**
	 * Calcul des contributions des côtés d'une ligne à son orientation.
	 * @param ls ligne évaluée
	 */
	public void calculerContributionsCotesOrientation(LineString ls){
		if (logger.isDebugEnabled()) logger.debug("calcul des contributions des cotes a l'orientation moyenne des cotes de "+ls);

		//initialise la table des contributions
		contributionsCotesOrientation=new double[NB_ORIENTATIONS_TESTEES];
		for(int i=0; i<NB_ORIENTATIONS_TESTEES; i++) contributionsCotesOrientation[i]=0.0;

		//ajout des contributions des cotes de l'enveloppe exterieure
		ajouterContribution(ls);		
	}

	/**
	 * Calcule les contributions de chaque mur a chaque orientation testee.
	 * Chaque côté contribue proportionellement à sa longueur et à son écart à l'orientation testés
	 * @param ls ligne évaluée
	 */
	private void ajouterContribution(LineString ls){
		double orientation, lg;
		
		//parcours des cotes pour calculer la contribution de chacun
		Coordinate[] coord=ls.getCoordinates();
		Coordinate c1=coord[0], c2;
		double pasOrientation = Math.PI*0.5/NB_ORIENTATIONS_TESTEES;
		double orientationTestee = 0;
		int index = 0;
		while(orientationTestee< 0.5*Math.PI){
			for(int i=1; i<coord.length; i++) {
				c2=coord[i];

				if (logger.isDebugEnabled()) logger.debug("contribution de cote ("+c1+", "+c2+")");

				//calcul de l'orientation à PI/2 pres entre c1 et c2
				if (c1.x==c2.x) orientation=0.0; else orientation=Math.atan( ((c1.y-c2.y)/(c1.x-c2.x)) );
				if (orientation<0) orientation+=0.5*Math.PI;
				if (logger.isDebugEnabled()) logger.debug("   orientation (en deg): "+orientation*180/Math.PI);

				// Calcul de l'angle entre l'arète courante et l'angle auquel on est en train d'associer un poids
				double alpha = Math.abs(orientationTestee-orientation);
				if(alpha>(Math.PI/4)) alpha = (0.5*Math.PI)-alpha;

				// Calcul de la longueur de l'arète
				lg=c1.distance(c2);
				
				// Si alpha est plus petit que l'angleContibutif l'arète est prise en compte pour l'orientation testée 
				if(alpha < ANGLE_CONTRIBUTIF){
					contributionsCotesOrientation[index] += ((ANGLE_CONTRIBUTIF-alpha)/ANGLE_CONTRIBUTIF)* lg;
				}
				//et au suivant
				c1=c2;
			}
			index++;
			orientationTestee += pasOrientation;
		}
	}

	/**
	 * Calcule la qualité de l'orientation des côtés d'un polygone.
	 */
	private void calculerIndicateurOrientationCote() {
		if (orientationCotes==-999.9) calculerOrientationCote();

		determinerPertinence();
		if (pertinence){
			indicateurOrientationCote=contributionMax/contributionMaxTheorique;
		}
	}


	/**
	 * Calcule la pertinence de la valeur d'orientation des côtés d'un polygone.
	 */
	private void determinerPertinence() {
		if (orientationCotes==-999.9) calculerOrientationCote();

		// Calcul du nombre de contributions égales à zéro
		int nbContributionEgalZero=0;
		for(int i=1; i<contributionsCotesOrientation.length; i++) {
			if (contributionsCotesOrientation[i] == 0.0 ) {nbContributionEgalZero++;}
		}
		if(nbContributionEgalZero==0){
			pertinence = false;
			return;
		}

		//récupère la contribution minimale
		double contributionMin=contributionsCotesOrientation[0];
		for(int i=1; i<contributionsCotesOrientation.length; i++) {
			if (contributionsCotesOrientation[i] < contributionMin) {
				contributionMin = contributionsCotesOrientation[i];
			}
		}

		// Calcul difference indices maximum et minimum
		double difference = (contributionMax - contributionMin)/contributionMaxTheorique*100;
		// Est ce que la notion d'orientation a un sens? 
		if(difference < 35.0){pertinence = false;}
		else{pertinence = true;}

	}

	/**
	 * Détecte la présence d'orientations secondaires parmi les orientations des côtés d'un polygone.
	 */
	private void detecterOrientationSecondaire() {
		if (orientationCotes==-999.9) calculerOrientationCote();

		//initialise la table des maximums locaux (= feuilles)
		maxiLocaux=new double[NB_ORIENTATIONS_TESTEES];
		for(int i=0; i<NB_ORIENTATIONS_TESTEES; i++) maxiLocaux[i]=0.0;
		//recherche des maximums locaux (= feuilles)
		int up = 1;
		int down = 0;
		int ind1,ind2;
		nombreOrientation = 0;
		for (int i=iMax;i<contributionsCotesOrientation.length + iMax;i++){
			ind1 = i;
			ind2 = i+1;
			if (ind1>=contributionsCotesOrientation.length) ind1 = ind1 - contributionsCotesOrientation.length;
			if (ind2>=contributionsCotesOrientation.length) ind2 = ind2 - contributionsCotesOrientation.length;
			double delta = contributionsCotesOrientation[ind1]-contributionsCotesOrientation[ind2];
			if ((delta>0)&&(up==1)){//on est en présence d'un maximum local
				maxiLocaux[ind1]=1.0;
				up = 0;
				down = 1;
				nombreOrientation++;
			}
			else if((delta<0)&&(down ==1)){//on est en présence d'un minimum local
				maxiLocaux[ind1]=-1.0;
				up = 1;
				down = 0;	
			}
		}

		// Suppression des feuilles trop petites
		int indexApres, indexAvant;
		for (int i=0;i<maxiLocaux.length;i++){
			if (maxiLocaux[i]==1){
				double contributionNormalisee = contributionsCotesOrientation[i]/contributionMaxTheorique*100;
				if (contributionNormalisee<SEUIL_MICRO_FEUILLES){ // la feuille est trop petite
					maxiLocaux[i] = 0; // on la supprime 
					nombreOrientation--;
					indexApres = -1;
					indexAvant = -1;
					// on recherche les deux minimums entourant ce maximum trop petit
					for (int j=i;j<contributionsCotesOrientation.length + i;j++){
						ind1 = j;
						if (ind1>=contributionsCotesOrientation.length) ind1 = ind1 - contributionsCotesOrientation.length;
						if(maxiLocaux[ind1]==-1){
							if (indexApres==-1){
								indexApres = ind1;
							}
							else{
								indexAvant = ind1;
							}
						}
					}
					// on supprime celui qui a le plus grand poids
					if(contributionsCotesOrientation[indexApres]>contributionsCotesOrientation[indexAvant]){
						maxiLocaux[indexApres]=0;
					}
					else{maxiLocaux[indexAvant]=0;}
				}
			}
		}

		// Concaténation des feuilles avec un décrochement trop court
		for (int i=0;i<maxiLocaux.length;i++){
			if (nombreOrientation>1){ // Si il y a plusieurs feuilles
				if (maxiLocaux[i]==-1){ // c'est un minimum
					indexApres = -1;
					indexAvant = -1;
					// on recherche les deux maximums entourant ce minimum
					for (int j=i;j<contributionsCotesOrientation.length + i;j++){
						ind1 = j;
						if (ind1>=contributionsCotesOrientation.length) ind1 = ind1 - contributionsCotesOrientation.length;
						if(maxiLocaux[ind1]==1){ // c'est un maximum
							if (indexApres==-1){
								indexApres = ind1;
							}
							else{
								indexAvant = ind1;
							}
						}
					}
					double poidsMinNorm = contributionsCotesOrientation[i]/contributionMaxTheorique*100;
					double poidsAvantNorm = contributionsCotesOrientation[indexAvant]/contributionMaxTheorique*100;
					double poidsApresNorm = contributionsCotesOrientation[indexApres]/contributionMaxTheorique*100;
					if (Math.min(poidsAvantNorm-poidsMinNorm, poidsApresNorm-poidsMinNorm)<=SEUIL_CONCAT_FEUILLES){
						// il faut Concaténer les deux feuilles : on supprime le minimum
						maxiLocaux[i]=0;
						// on supprime le plus petit des maximums
						if(poidsAvantNorm<=poidsApresNorm){maxiLocaux[indexAvant]=0;}
						else{maxiLocaux[indexApres]=0;}
						nombreOrientation--;
					}
				}
			}
		}
	}
	/**
	 * Calcul de la principale orientation secondaire
	 */
	private void calculContributionSecondaire() {
		if (maxiLocaux==null) detecterOrientationSecondaire();
		
		// Calcul de la contribution de l'orientation secondaire
		contributionSecondaire = 0;
		if(nombreOrientation>1){
			for (int i=0;i<maxiLocaux.length;i++){
				if ((maxiLocaux[i]>0)&&(i!=iMax)){
					if(contributionsCotesOrientation[i]>contributionSecondaire){
						contributionSecondaire = contributionsCotesOrientation[i];
					}
				}
			}				
		}
	}
	
	
	/**
	 * Qualification de la biscornuité à partir des orientations mesurées
	 */
	private void determinerBiscornuite() {

		determinerPertinence();
		// l'orientation des murs n'a pas de sens
		if (!pertinence) biscornuite = "aucune_orientation";
		else{
			int nbOrient = getNombreOrientation();
			if(nbOrient > 1){
				double contribSec = getContributionSecondaire();
				double partSecondaire = contribSec/contributionMax*100;
				if(partSecondaire>40){// l'orientation secondaire est importante
					biscornuite = "plusieurs_orientations";
				}
				else{// l'orientation principale reste majoritaire
					biscornuite = "un_mur_different";
				}
			}
			else{
				double elongation = JtsUtil.elongation(geom);
				double orientationGenerale = getOrientationGenerale();
				if (orientationGenerale>=0.5*Math.PI) orientationGenerale-=0.5*Math.PI;
				double diffOrientation = (Math.abs(orientationGenerale-orientationCotes));
				if(diffOrientation>(Math.PI/4)) diffOrientation = (0.5*Math.PI)-diffOrientation;
				double indicateur = getIndicateurOrientationCote();
				if(indicateur>=SEUIL_INDICATEUR_CONFIANCE){ // les murs sont à peu près droits
					if((diffOrientation>=SEUIL_COMPARAISON_ORIENTATION)&&(elongation<0.4)){
						// l'orientation du batiment est la même que celle des murs
						biscornuite = "escalier_mur_droit";
					}
					else{// l'orientation du batiment est différente de celle des murs et le batiment est alongé
						biscornuite = "rectangulaire_mur_droit";
					}
				}
				else{ // les murs ne sont pas très droits 
					if((diffOrientation>=SEUIL_COMPARAISON_ORIENTATION)&&(elongation<0.4)){
						// l'orientation du batiment est la même que celle des murs
						biscornuite = "escalier_mur_peu_droit";
					}
					else{// l'orientation du batiment est différente de celle des murs et le batiment est alongé
						biscornuite = "rectangulaire_mur_peu_droit";
					}
				}
			}
		}
	}
}
