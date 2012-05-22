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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * Méthodes de calcul d'orientation par pondération des différentes contributions :
 * <ul>
 * <li> orientation des côtés d'un polygone (entre 0 et Pi/2)
 * <li> orientations principales d'une liste d'orientation de polygones (entre 0 et Pi)
 * <li> orientations principales d'une liste d'orientation des côtés de polygones (entre 0 et Pi/2)
 * </ul>
 *
 * @author Julien Gaffuri et Florence Curie
 *
 */
public class MesureOrientationFeuille {
	private static Logger logger=Logger.getLogger(MesureOrientationFeuille.class.getName());

	// précision de l'angle testé
	private static double ANGLE_PRECISION = Math.PI/180; //(=1°)
	public static double getANGLE_PRECISION() {return ANGLE_PRECISION;}

	// Constantes
	private static double ANGLE_CONTRIBUTIF=Math.PI/18; // (= 10°) angle en deça duquel une arète contribue à l'orientation testée
	private static double SEUIL_MICRO_FEUILLES=5; // longueur (en pourcentage du maximum théorique) en dessous de laquelle une contribution sera négligée
	private static double SEUIL_CONCAT_FEUILLES=5; // longueur (en pourcentage du maximum théorique) en dessous duquel un décrochement entre deux feuilles ne sera pas pris en compte (les deux feuilles seront concaténées)

	private List<Double> listeOrientations=null;
	private List<Double> listePoids=null;
	private double angleMaximum = 0;
	private int nbOrientationsTestees;

	/**
	 * Constructeur de mesure d'orientation à partir d'une liste de double contenant des orientations.
	 * Dans ce cas, tous les poids des orientations sont égalisés à 1.
	 * @param listeOrientations liste des orientations dont on va extraire les principales orientations et leurs dispersions.
	 * @param angleMaximum angle correspondant à la borne supérieure de l'intervalle
	 * dans lequel doit être réalisé le calcul (valeur attendue : pi pour les polygones
	 * et pi/2 pour les côtés des polygones).
	 */
	public MesureOrientationFeuille(List<Double> listeOrientations,double angleMaximum){
		this.listeOrientations=listeOrientations;
		listePoids = new ArrayList<Double> ();
		for (int i=0;i<listeOrientations.size();i++){
			listePoids.add(1.0);
		}
		this.angleMaximum = angleMaximum;
		nbOrientationsTestees = (int) (angleMaximum / ANGLE_PRECISION);
	}

	/**
	 * Constructeur de mesure d'orientation à partir d'une liste de double contenant des orientations et d'une liste de double contenant les poids.
	 * @param listeOrientations liste des orientations dont on va extraire les principales orientations.
	 * @param listePoids liste des poids correspondant à chaque orientation.
	 * @param angleMaximum angle correspondant à la borne supérieure de l'intervalle
	 * dans lequel doit être réalisé le calcul (valeur attendue : pi pour les polygones
	 * et pi/2 pour les côtés des polygones).
	 */
	public MesureOrientationFeuille(List<Double> listeOrientations,List<Double> listePoids,double angleMaximum){
		this.listeOrientations=listeOrientations;
		this.listePoids=listePoids;
		this.angleMaximum = angleMaximum;
		nbOrientationsTestees = (int) (angleMaximum / ANGLE_PRECISION);
	}

	/**
	 * Constructeur de mesure d'orientation à partir d'une géométrie JTS.
	 * @param geom géométrie sur laquelle on va calculer l'orientation.
	 * @param angleMaximum angle correspondant à la borne supérieure de l'intervalle
	 * dans lequel doit être réalisé le calcul (valeur attendue : pi pour les polygones
	 * et pi/2 pour les côtés des polygones).
	 */
	public MesureOrientationFeuille(Geometry geom,double angleMaximum){
		listeOrientations = new ArrayList<Double> ();
		listePoids = new ArrayList<Double> ();
		if (geom instanceof Polygon) {
			Polygon poly = (Polygon)geom;
			LineString contourExterieur = poly.getExteriorRing();
			ajouterOrientation(contourExterieur);
			for (int i=0;i<poly.getNumInteriorRing();i++){
				LineString contourInterieur = poly.getInteriorRingN(i);
				ajouterOrientation(contourInterieur);
			}
		}
		else {
			logger.error("La géométrie entrée n'est pas un polygone");
			return;
		}
		this.angleMaximum = angleMaximum;
		nbOrientationsTestees = (int) (angleMaximum / ANGLE_PRECISION);
	}

	/**
	 * Constructeur de mesure d'orientation à partir d'une géométrie geoxygene.
	 * @param geomGeox géométrie sur laquelle on va calculer l'orientation.
	 * @param angleMaximum angle correspondant à la borne supérieure de l'intervalle
	 * dans lequel doit être réalisé le calcul (valeur attendue : pi pour les polygones
	 * et pi/2 pour les côtés des polygones).
	 */
	public MesureOrientationFeuille(IGeometry geomGeox,double angleMaximum){
		listeOrientations = new ArrayList<Double> ();
		listePoids = new ArrayList<Double> ();
		try {
			Geometry geom=AdapterFactory.toGeometry(new GeometryFactory(), geomGeox);
			if (geom instanceof Polygon) {
				Polygon poly = (Polygon)geom;
				LineString contourExterieur = poly.getExteriorRing();
				ajouterOrientation(contourExterieur);
				for (int i=0;i<poly.getNumInteriorRing();i++){
					LineString contourInterieur = poly.getInteriorRingN(i);
					ajouterOrientation(contourInterieur);
				}
			}
			else {
				logger.error("La géométrie entrée n'est pas un polygone");
				return;
			}
		}
		catch (Exception e) {e.printStackTrace();}
		this.angleMaximum = angleMaximum;
		nbOrientationsTestees = (int) (angleMaximum / ANGLE_PRECISION);
	}

	/**
	 * Ajout des orientations et des longueurs à la liste des orientations et des poids.
	 * @param contour LineString correspond aux contours interieur ou extérieur du polygone.
	 */
	private void ajouterOrientation(LineString contour){
		double orientation,lg;
		Coordinate[] coord = contour.getCoordinates();
		Coordinate c1=coord[0], c2;
		for(int i=1; i<coord.length; i++) {
			c2=coord[i];
			//calcul de l'orientation à PI/2 pres entre c1 et c2
			if (c1.x==c2.x) orientation=0.0; else orientation=Math.atan( ((c1.y-c2.y)/(c1.x-c2.x)) );
			if (orientation<0) orientation+=0.5*Math.PI;
			if (logger.isTraceEnabled()) logger.trace("   orientation (en deg): "+orientation*180/Math.PI);
			listeOrientations.add(orientation);
			// Calcul de la longueur de l'arète
			lg=c1.distance(c2);
			listePoids.add(lg);
			//et au suivant
			c1=c2;
		}
	}

	private List<Double> listeOrientationsPrincipales=null;
	/**
	 * @return liste des orientations principales.
	 */
	public List<Double> getOrientationsPrincipales() {
		if (listeOrientationsPrincipales==null) calculerOrientationsPrincipales();
		return listeOrientationsPrincipales;
	}

	private List<Double> listeDispersionOrientationsPrincipales=null;
	/**
	 * @return liste des dispersion correspondant aux orientations principales.
	 */
	public List<Double> getDispersionOrientationsPrincipales() {
		if (listeDispersionOrientationsPrincipales==null) calculerOrientationsPrincipales();
		return listeDispersionOrientationsPrincipales;
	}

	private double[] contributionsOrientation=null;
	/**
	 * Calcul des contributions de chaque orientation
	 * @return tableau des contributions respectives des différents orientations testées.
	 */
	public double[] getContributionsOrientation() {
		if (contributionsOrientation==null) calculerContributionsOrientation();
		return contributionsOrientation;
	}

	private double contributionPrincipale=-999.9;
	/**
	 * @return la valeur de la contribution principale.
	 */
	public double getContributionPrincipale() {
		if (contributionPrincipale==-999.9) calculerOrientationPrincipale();
		return contributionPrincipale;
	}

	private double orientationPrincipale=-999.9;
	/**
	 * @return l'orientation principale parmi les orientations testées.
	 */
	public double getOrientationPrincipale() {
		if (orientationPrincipale==-999.9) calculerOrientationPrincipale();
		return orientationPrincipale;
	}

	private double indicateurOrientationPrincipale=-999.9;
	/**
	 * @return qualité de l'orientation.
	 */
	public double getIndicateurOrientationPrincipale() {
		if (indicateurOrientationPrincipale==-999.9) calculerIndicateurOrientationPrincipale();
		return indicateurOrientationPrincipale;
	}

	private boolean pertinence=false;
	/**
	 * @return Pertinence du calcul d'orientation.
	 */
	public boolean getPertinence() {
		if (pertinence==false) determinerPertinence();
		return pertinence;
	}

	private double contributionSecondaire=-999.9;
	/**
	 * @return la valeur de la principale orientation secondaire.
	 */
	public double getContributionSecondaire() {
		if (contributionSecondaire==-999.9) calculContributionSecondaire();
		return contributionSecondaire;
	}

	private int nombreOrientation=-1;
	private double[] maxiLocaux=null;
	/**
	 * @return le nombre d'orientations principales.
	 */
	public int getNombreOrientation() {
		if (nombreOrientation==-1) detecterOrientationSecondaire();
		return nombreOrientation;
	}

	private int indiceContributionPrincipale=-1;
	private double contributionMaxTheorique = -1;
	/**
	 * Calcul de l'orientation principale (en radian, dans l'intervalle [0, Pi/2[, par rapport a l'axe Ox)
	 */
	private void calculerOrientationPrincipale(){

		//calcul des contributions des cotes
		if (contributionsOrientation==null) calculerContributionsOrientation();

		/*
		if (logger.isTraceEnabled()) {
			logger.trace("contributions:");
			String st="";
			for(int i=1; i<contributionsOrientation.length; i++) st+= ((int)contributionsOrientation[i])+"  ";
			logger.trace(st);
		}
		*/

		//recupere l'index de la contribution principale
		indiceContributionPrincipale = 0;
		contributionPrincipale = contributionsOrientation[0];
		for(int i=1; i<contributionsOrientation.length; i++) {
			if (contributionsOrientation[i] > contributionPrincipale) {
				contributionPrincipale = contributionsOrientation[i];
				indiceContributionPrincipale=i;
			}
		}

		//renvoie l'angle correspondant a la contribution principale
		orientationPrincipale = ANGLE_PRECISION*indiceContributionPrincipale;

		// Calcul de la contribution maximale théorique
		contributionMaxTheorique = 0;
		for (Double contrib:listePoids) {
			contributionMaxTheorique += contrib;
		}
	}

	/**
	 * Calcul des contributions des orientations.
	 */
	private void calculerContributionsOrientation(){

		//initialise la table des contributions
		contributionsOrientation=new double[nbOrientationsTestees];
		for(int i=0; i<nbOrientationsTestees; i++) contributionsOrientation[i]=0.0;

		//ajout des contributions des différentes orientations
		double orientation, lg;

		double orientationTestee = 0;
		int index = 0;
		while(index<nbOrientationsTestees){
			for(int i=0; i<listeOrientations.size(); i++) {

				//Récupération de l'orientation
				orientation=listeOrientations.get(i);
				// if (logger.isTraceEnabled()) logger.trace("   orientation (en deg): "+orientation*180/Math.PI);

				// Calcul de l'angle entre l'arète courante et l'angle auquel on est en train d'associer un poids
				double alpha = Math.abs(orientationTestee-orientation);
				if(alpha>(angleMaximum*0.5)) alpha = (angleMaximum)-alpha;

				// Récupération du poids de cette orientation
				lg=listePoids.get(i);

				// Si alpha est plus petit que l'angleContibutif l'arète est prise en compte pour l'orientation testée
				if(alpha < ANGLE_CONTRIBUTIF){
					contributionsOrientation[index] += ((ANGLE_CONTRIBUTIF-alpha)/ANGLE_CONTRIBUTIF)* lg;
				}
			}
			index++;
			orientationTestee += ANGLE_PRECISION;
		}
	}

	/**
	 * Calcul de la qualité de l'orientation principale.
	 */
	private void calculerIndicateurOrientationPrincipale() {
		if (orientationPrincipale==-999.9) calculerOrientationPrincipale();

		determinerPertinence();
		if (pertinence){
			indicateurOrientationPrincipale=contributionPrincipale/contributionMaxTheorique;
		}
	}

	/**
	 * Détermination de la pertinence de la valeur d'orientation.
	 */
	private void determinerPertinence() {
		if (orientationPrincipale==-999.9) calculerOrientationPrincipale();

		// Calcul du nombre de contributions égales à zéro
		int nbContributionEgalZero=0;
		for(int i=1; i<contributionsOrientation.length; i++) {
			if (contributionsOrientation[i] == 0.0 ) {nbContributionEgalZero++;}
		}
		if(nbContributionEgalZero==0){
			pertinence = false;
			return;
		}

		//récupère la contribution minimale
		double contributionMin=contributionsOrientation[0];
		for(int i=1; i<contributionsOrientation.length; i++) {
			if (contributionsOrientation[i] < contributionMin) {
				contributionMin = contributionsOrientation[i];
			}
		}

		// Calcul difference indices maximum et minimum
		double difference = (contributionPrincipale - contributionMin)/contributionMaxTheorique*100;
		// Est ce que la notion d'orientation a un sens?
		if(difference < 35.0){pertinence = false;}
		else{pertinence = true;}

	}

	/**
	 * Détection de la présence d'orientations secondaires.
	 */
	private void detecterOrientationSecondaire() {
		if (orientationPrincipale==-999.9) calculerOrientationPrincipale();

		//initialise la table des maximums locaux (= feuilles)
		maxiLocaux=new double[nbOrientationsTestees];
		for(int i=0; i<nbOrientationsTestees; i++) maxiLocaux[i]=0.0;
		//recherche des maximums locaux (= feuilles)
		int up = 1;
		int down = 0;
		int ind1,ind2;
		nombreOrientation = 0;
		for (int i=indiceContributionPrincipale;i<contributionsOrientation.length + indiceContributionPrincipale;i++){
			ind1 = i;
			ind2 = i+1;
			if (ind1>=contributionsOrientation.length) ind1 = ind1 - contributionsOrientation.length;
			if (ind2>=contributionsOrientation.length) ind2 = ind2 - contributionsOrientation.length;
			double delta = contributionsOrientation[ind1]-contributionsOrientation[ind2];
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
				double contributionNormalisee = contributionsOrientation[i]/contributionMaxTheorique*100;
				if (contributionNormalisee<SEUIL_MICRO_FEUILLES){ // la feuille est trop petite
					maxiLocaux[i] = 0; // on la supprime
					nombreOrientation--;
					indexApres = -1;
					indexAvant = -1;
					// on recherche les deux minimums entourant ce maximum trop petit
					for (int j=i;j<contributionsOrientation.length + i;j++){
						ind1 = j;
						if (ind1>=contributionsOrientation.length) ind1 = ind1 - contributionsOrientation.length;
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
					if(contributionsOrientation[indexApres]>contributionsOrientation[indexAvant]){
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
					for (int j=i;j<contributionsOrientation.length + i;j++){
						ind1 = j;
						if (ind1>=contributionsOrientation.length) ind1 = ind1 - contributionsOrientation.length;
						if(maxiLocaux[ind1]==1){ // c'est un maximum
							if (indexApres==-1){
								indexApres = ind1;
							}
							else{
								indexAvant = ind1;
							}
						}
					}
					double poidsMinNorm = contributionsOrientation[i]/contributionMaxTheorique*100;
					double poidsAvantNorm = contributionsOrientation[indexAvant]/contributionMaxTheorique*100;
					double poidsApresNorm = contributionsOrientation[indexApres]/contributionMaxTheorique*100;
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
	 * Calcul de la principale orientation secondaire.
	 */
	private void calculContributionSecondaire() {
		if (maxiLocaux==null) detecterOrientationSecondaire();

		// Calcul de la contribution de l'orientation secondaire
		contributionSecondaire = 0;
		if(nombreOrientation>1){
			for (int i=0;i<maxiLocaux.length;i++){
				if ((maxiLocaux[i]>0)&&(i!=indiceContributionPrincipale)){
					if(contributionsOrientation[i]>contributionSecondaire){
						contributionSecondaire = contributionsOrientation[i];
					}
				}
			}
		}
	}

	/**
	 * Calcul des principales orientations et de leurs dispersions.
	 */
	private void calculerOrientationsPrincipales(){
		if (maxiLocaux==null) detecterOrientationSecondaire();
		listeDispersionOrientationsPrincipales = new ArrayList<Double>();
		listeOrientationsPrincipales = new ArrayList<Double>();
		if(nombreOrientation>0){
			listeDispersionOrientationsPrincipales.add(contributionPrincipale/contributionMaxTheorique);
			listeOrientationsPrincipales.add(orientationPrincipale);
			int compt = 0;
			for (int i=0;i<maxiLocaux.length;i++){
				if ((maxiLocaux[i]>0)&&(i!=indiceContributionPrincipale)){
					for (int k=0;k<listeOrientationsPrincipales.size();k++){
						double orientComp = listeDispersionOrientationsPrincipales.get(k);
						if((contributionsOrientation[i]/contributionMaxTheorique)<orientComp){
							compt=k+1;
						}
					}
					listeDispersionOrientationsPrincipales.add(compt,(contributionsOrientation[i]/contributionMaxTheorique));
					listeOrientationsPrincipales.add(compt,i * ANGLE_PRECISION);
				}
			}
		}
	}
}
