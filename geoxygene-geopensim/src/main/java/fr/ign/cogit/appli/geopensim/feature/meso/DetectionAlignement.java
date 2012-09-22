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

package fr.ign.cogit.appli.geopensim.feature.meso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 *
 * Cette Détection des alignements est basée sur le travail de Sidonie Christophe.
 * La Méthode utilisée consiste à balayer la zone où l'on souhaite Détecter des
 * alignements avec des droites de différentes orientations puis à projeter les centroides
 * des bâtiments sur ces droites de manière à pouvoir repèrer des paquets de plus
 * forte densité de points (ici de 4, 5 et 6 bâtiments).
 *
 * @author Florence Curie
 *
 */
public class DetectionAlignement {
	static Logger logger=Logger.getLogger(DetectionAlignement.class.getName());

	IDirectPositionList listePointsTous = new DirectPositionList();
	/**
	 * Constructeur
	 */
	public DetectionAlignement(IDirectPositionList directPositionList){this.listePointsTous=directPositionList;}

	/**
	 * La liste des paquets de points retenus
	 */
	List<IDirectPositionList> listePaquetsFinal = null;
	/**
	 * La liste des paquets de points retenus
	 * @return listePaquetsFinal la liste des paquets de points retenus
	 */
	public List<IDirectPositionList> getListePaquetsFinal() {
		if (listePaquetsFinal==null) detecterAlignements();
		return this.listePaquetsFinal;
		}

	/**
	 * La liste des angles des droites projetées retenues
	 */
	List<Double> listeAlphaFinal = null;
	/**
	 * La liste des angles des droites projetées retenues
	 * @return listeAlphaFinal la liste des angles des droites projetées retenues
	 */
	public List<Double> getListeAlphaFinal() {
		if (listeAlphaFinal==null) detecterAlignements();
		return this.listeAlphaFinal;
		}

	// Les constantes
	//private static int SEUIL_DISTANCE = 40; // seuil en mètre au delà duquel un point n'appartient pas à l'alignement
	private static double SEUIL_BUFFER = 10; // 10 seuil pour la création du buffer
	private static int SEUIL_DMAX = 3; // 3 seuil au delà duquel la distance disatnce maxmum projetée n'est plus considérée

	/**
	 * Détection des alignements
	 */
	public void detecterAlignements(){

		// Cas où pas de bâtiments dans le groupe : normalement pas sensé arriver !
		if(listePointsTous.isEmpty()) return;

		// constantes
		int taillePaquets = 6;
		// Angle
		double angle = -Math.PI/2;
		double anglePrecision = Math.PI/180;

		// liste des alphas sélectionnés pour chacunes des tailles de paquets
		List<Double> listeAlpha6Select = new ArrayList<Double>(0);
		List<Double> listeAlpha5Select = new ArrayList<Double>(0);
		List<Double> listeAlpha4Select = new ArrayList<Double>(0);

		// distance maximum minimale pour chacunes des tailles de paquets
		List<Double> listeDistMax6Select = new ArrayList<Double>(0);
		List<Double> listeDistMax5Select = new ArrayList<Double>(0);
		List<Double> listeDistMax4Select = new ArrayList<Double>(0);

		// liste des paquets pour chacunes des tailles de paquets
		List<IDirectPositionList> listePaquets6Select = new ArrayList<IDirectPositionList>(0);
		List<IDirectPositionList> listePaquets5Select = new ArrayList<IDirectPositionList>(0);
		List<IDirectPositionList> listePaquets4Select = new ArrayList<IDirectPositionList>(0);

		double xMin = getXmin(listePointsTous);
		double yMin = getYmin(listePointsTous);
		double xMax = getXmax(listePointsTous);
		double yMax = getYmax(listePointsTous);

		// Point origine de la droite de projection
		DirectPosition pointOrigine;
		DirectPosition pointOrigine1 = new DirectPosition(xMin,yMin);
		DirectPosition pointOrigine2 = new DirectPosition(xMin,yMax);

		double longueur = getLongueur(pointOrigine1,xMax,yMax);

		while (angle<Math.PI/2){ // pour chaque angle entre -PI/2 et PI/2
			// si l'angle est compris entre -PI/2 et 0 le point origine est en haut à gauche
			if (angle<0){pointOrigine = pointOrigine2;}
			// si l'angle est compris entre 0 et PI/2 le point origine est en bas à gauche
			else {pointOrigine = pointOrigine1;}

			// Récupération d'un autre point sur la droite de projection
			IDirectPosition autrePoint = getAutrePoint(pointOrigine,angle,longueur);
			List<IDirectPosition> listePoints = new ArrayList<IDirectPosition>(0);
			List<Double> distances = new ArrayList<Double>(0);

			// Calcul de la distance entre chaque point projeté et le point origine
			for (int m=0;m<listePointsTous.size();m++){
				IDirectPosition directPosition = listePointsTous.get(m);
				// Calcul de la distance entre le point projeté et le point origine
				double distance = getDistance(directPosition, pointOrigine, autrePoint);
				// Classement des points en fonction d'une distance croissante avec le point origine
				int compt = 0;
				for (int i=0;i<distances.size();i++){
					if (distance>distances.get(i)){compt=i+1;}
				}
				listePoints.add(compt,directPosition);
				distances.add(compt,distance);
			}

			// Création des paquets et calcul des distances maximum projetées intra paquets
			creerPaquets(taillePaquets,listePoints,distances);
			List<Double> listeDistMax6 = listeDistMax;
			List <DirectPositionList> listePaquets6 = listePaquets;
			creerPaquets(taillePaquets-1,listePoints,distances);
			List<Double> listeDistMax5 = listeDistMax;
			List <DirectPositionList> listePaquets5 = listePaquets;
			creerPaquets(taillePaquets-2,listePoints,distances);
			List<Double> listeDistMax4 = listeDistMax;
			List <DirectPositionList> listePaquets4 = listePaquets;

			// Classement des points contenus dans chaque paquet
			classementPaquets(listePaquets6);
			classementPaquets(listePaquets5);
			classementPaquets(listePaquets4);

			// Calcul de la distance maximum réelle intra paquets
			//List<Double> listeDistMaxReelle6 = calculDistanceReelle(listePaquets6);
			//List<Double> listeDistMaxReelle5 = calculDistanceReelle(listePaquets5);
			//List<Double> listeDistMaxReelle4 = calculDistanceReelle(listePaquets4);

			// Recherche des paquets ayant une distance maximum intra paquets inférieur au SEUIL_DMAX
			// et dont la distance entre points non projetés est inférieure à SEUIL_DISTANCE
			for (int i=0;i<listeDistMax6.size();i++){
				if ((listeDistMax6.get(i)<SEUIL_DMAX)){//&&(listeDistMaxReelle6.get(i)<SEUIL_DISTANCE)){
					listeAlpha6Select.add(angle);
					listeDistMax6Select.add(listeDistMax6.get(i));
					listePaquets6Select.add(listePaquets6.get(i));
				}
			}

			for (int i=0;i<listeDistMax5.size();i++){
				if ((listeDistMax5.get(i)<SEUIL_DMAX)){//&&(listeDistMaxReelle5.get(i)<SEUIL_DISTANCE)){
					listeAlpha5Select.add(angle);
					listeDistMax5Select.add(listeDistMax5.get(i));
					listePaquets5Select.add(listePaquets5.get(i));
				}
			}

			for (int i=0;i<listeDistMax4.size();i++){
				if ((listeDistMax4.get(i)<SEUIL_DMAX)){//&&(listeDistMaxReelle4.get(i)<SEUIL_DISTANCE)){
					listeAlpha4Select.add(angle);
					listeDistMax4Select.add(listeDistMax4.get(i));
					listePaquets4Select.add(listePaquets4.get(i));
				}
			}

			// incrèmentation del'angle
			angle += anglePrecision;
		}
		// Suppression des structures redondantes (ayant au moins deux éléments en commun)
		nettoyageIntraListe(listePaquets6Select,listeDistMax6Select,listeAlpha6Select);
		nettoyageIntraListe(listePaquets5Select,listeDistMax5Select,listeAlpha5Select);
		nettoyageIntraListe(listePaquets4Select,listeDistMax4Select,listeAlpha4Select);

		// Suppression des structures redondantes (ayant au moins deux éléments en commun)
		nettoyageInterListe(listePaquets6Select,listeDistMax6Select,listeAlpha6Select,listePaquets5Select,listeDistMax5Select,listeAlpha5Select);
		nettoyageInterListe(listePaquets6Select,listeDistMax6Select,listeAlpha6Select,listePaquets4Select,listeDistMax4Select,listeAlpha4Select);
		nettoyageInterListe(listePaquets5Select,listeDistMax5Select,listeAlpha5Select,listePaquets4Select,listeDistMax4Select,listeAlpha4Select);

		// On combine toutes les listes ensemble
		listePaquetsFinal = new ArrayList<IDirectPositionList>(0);
		listeAlphaFinal = new ArrayList<Double>(0);
		listePaquetsFinal.addAll(listePaquets6Select);
		listePaquetsFinal.addAll(listePaquets5Select);
		listePaquetsFinal.addAll(listePaquets4Select);
		listeAlphaFinal.addAll(listeAlpha6Select);
		listeAlphaFinal.addAll(listeAlpha5Select);
		listeAlphaFinal.addAll(listeAlpha4Select);


		// On calcule une droite de régression pour chacun des alignements Déterminés
		//List<List<Double>> coefReg = calculDroiteRegression(listePaquetsFinal);

		// On calcule les coefficients a et b à partir de alpha
		List<List<Double>> coefReg2 = calculCoefficientsDroite(listePaquetsFinal,listeAlphaFinal);

		// On calcul les points extrèmes de la droite de l'alignement
		List <IDirectPositionList> pointsExtremite = calculExtremiteDroitePaquets(listePaquetsFinal, coefReg2);

		// On récupère les points oubliés lors de la création de l'alignement
		recuperationPointsOublies(listePaquetsFinal, pointsExtremite);

		// on prolonge l'alignement
		prolongementAlignement(listePaquetsFinal, coefReg2);

		// on fusionne les structures parallèles
		fusionAlignement(listePaquetsFinal, coefReg2, listeAlphaFinal);
	}

	/**
	 * Abscisse minimum de la liste des points
	 * @return xMin l'abscisse minimum de la liste des points
	 * @param listeDP une liste de DirectPosition
	 */
	private double getXmin(IDirectPositionList listeDP){

		double xMin = listeDP.get(0).getX();
		for (int i=1;i<listeDP.size();i++){
			double x = listeDP.get(i).getX();
			if (x<xMin)xMin = x;
		}
		return xMin;
	}

	/**
	 * Ordonnée minimum de la liste des points
	 * @return yMin l'ordonnée minimum de la liste des points
	 * @param listeDP une liste de DirectPosition
	 */
	private double getYmin(IDirectPositionList listeDP){

		double yMin = listeDP.get(0).getY();
		for (int i=1;i<listeDP.size();i++){
			double y = listeDP.get(i).getY();
			if (y<yMin)yMin = y;
		}
		return yMin;
	}

	/**
	 * Abscisse maximum de la liste des points
	 * @return xMax l'abscisse maximum de la liste des points
	 * @param listeDP une liste de DirectPosition
	 */
	private double getXmax(IDirectPositionList listeDP){

		double xMax = listeDP.get(0).getX();
		for (int i=1;i<listeDP.size();i++){
			double x = listeDP.get(i).getX();
			if (x>xMax)xMax = x;
		}
		return xMax;
	}

	/**
	 * Ordonnée maximum de la liste des points
	 * @return yMax l'ordonnée maximum de la liste des points
	 * @param listeDP une liste de DirectPosition
	 */
	private double getYmax(IDirectPositionList listeDP){

		double yMax = listeDP.get(0).getY();
		for (int i=1;i<listeDP.size();i++){
			double y = listeDP.get(i).getY();
			if (y>yMax)yMax = y;
		}
		return yMax;
	}

	/**
	 * Longueur de la droite de projection
	 * @return longueur de la droite de projection
	 * @param pointOrigine le point origine de la droite de projection
	 * @param xMax l'abscisse maximum
	 * @param yMax l'ordonnée maximum
	 */
	private double getLongueur(DirectPosition pointOrigine, double xMax, double yMax){

		double longueur = Math.sqrt(Math.pow(xMax-pointOrigine.getX(),2)+Math.pow(yMax-pointOrigine.getY(),2));
		return longueur;
	}

	/**
	 * Autre Point de la droite de projection
	 * @return un autre point de la droite de projection
	 * @param pointOrigine le point origine de la droite
	 * @param angle l'angle de la droite
	 * @param longueur la longueur de la droite de projection
	 */
	private IDirectPosition getAutrePoint(IDirectPosition pointOrigine, double angle, double longueur){

		double xAutre = pointOrigine.getX() + longueur * Math.cos(angle);
		double yAutre = pointOrigine.getY() + longueur * Math.sin(angle);
		IDirectPosition autrePointDroite = new DirectPosition(xAutre,yAutre);

		return autrePointDroite;
	}

	/**
	 * Distance entre le point origine et le point projeté sur la droite de projection
	 * @return dist la distance entre le point origine et le point projeté sur la droite de projection
	 * @param directPosition le point à projeter
	 * @param pointOrigine le point origine de la droite de projection
	 * @param autrePointDroite un autre point sur la droite de projection
	 */
	private double getDistance(IDirectPosition directPosition, IDirectPosition pointOrigine, IDirectPosition autrePointDroite){

		// Projection d'un point sur la droite de projection
		IDirectPosition pointProjete = Operateurs.projection(directPosition, pointOrigine, autrePointDroite);
		// Calcul de la distance entre le point projeté et le point origine
		double dist = pointOrigine.distance(pointProjete);

		return dist;
	}

	// Création des structures de stockage
	private List<Double> listeDistMax = null;
	private List<DirectPositionList> listePaquets = null;

	/**
	 * Création de paquets de points
	 * @param taillePaquets la taille des paquets de points que l'on va créer
	 * @param listePoint la liste des points classée par distance croissante au point origine
	 * @param distances la liste des distances correspondant à la listePoint
	 */
	private void creerPaquets(int taillePaquets, List<IDirectPosition> listePoint, List<Double> distances){

		// Création des structures de stockage
		listeDistMax = new ArrayList<Double>(0);
		listePaquets = new ArrayList<DirectPositionList>(0);
		// Création des paquets et calcul de la distance maximum pour chacun des paquets
		for (int j=0;j<(listePoint.size()-(taillePaquets-1));j++){
			double distMax = 0;
			DirectPositionList listePaquet = new DirectPositionList();
			listePaquet.add(listePoint.get(j));
			for (int i=j;i<j+(taillePaquets-1);i++){
				double distance = distances.get(i+1)-distances.get(i);
				listePaquet.add(listePoint.get(i+1));
				if (distance>distMax){
					distMax = distance;
				}
			}
			// Stockage des résultats en fonction de la distance maximum croissante
			int compt = 0;
			for (int i=0;i<listeDistMax.size();i++){
				if (distMax>listeDistMax.get(i)){compt=i+1;}
			}
			listeDistMax.add(compt,distMax);
			listePaquets.add(compt,listePaquet);
		}
	}

	/**
	 * Classement des paquets selon l'ordre des points dans l'alignement
	 * @param newListePaquets la liste des paquets à classer
	 */
	private void classementPaquets(List<DirectPositionList> newListePaquets){

		for (int i=0;i<newListePaquets.size();i++){
			DirectPositionList listeDP = newListePaquets.get(i);
			DirectPositionList listeDPTri = triPaquet(listeDP);
			newListePaquets.set(i, listeDPTri);
		}
	}

	/**
	 * Classement d'un paquet selon l'ordre des points dans l'alignement
	 * @param paquet paquet à classer
	 */
	private DirectPositionList triPaquet(DirectPositionList listeDP){

		DirectPositionList listeDPTri = new DirectPositionList();
		// Première étape : on calcule les interdistances entre tous les points : pour trouver un bout de la chaine
		double dMax = 0;
		IDirectPosition bout = new DirectPosition();
		for (int j=0;j<listeDP.size();j++){
			IDirectPosition point1 = listeDP.get(j);
			for (int k=j+1;k<listeDP.size();k++){
				IDirectPosition point2 = listeDP.get(k);
				double dist = point1.distance(point2);
				if(dist>dMax){
					dMax = dist;
					bout = point1;
				}
			}
		}
		// deuxième étape : on recalcule les distances à partir de ce bout pour classer les points par distance croissante
		listeDPTri.add(bout);
		for (int j=0;j<listeDP.size();j++){
			IDirectPosition point2 = listeDP.get(j);
			if (!point2.equals(bout)){
				insertionPoint(listeDPTri, point2);
			}
		}
		return listeDPTri;
	}

	/**
	 * Insertion d'un point dans un alignement
	 * @param listeDPTri la liste des points de l'alignement
	 * @param point le point à intégrer dans l'alignement
	 */
	private void insertionPoint(IDirectPositionList listeDPTri, IDirectPosition point){

		int compt = 1;
		IDirectPosition bout = listeDPTri.get(0);
		double dist = bout.distance(point);
		for (int k=1;k<listeDPTri.size();k++){
			double distComp = bout.distance(listeDPTri.get(k));
			if (dist>distComp){compt=k+1;}
		}
		listeDPTri.add(compt,point);
	}

	/**
	 * Elimination des paquets redondants au sein des listes de paquets de même taille
	 * (deux paquets sont redondants quand ils ont au moins deux éléments en commun).
	 * On supprime celui ayant la dmax la plus grande.
	 * @param ListePaquets la liste des paquets
	 * @param listeDistanceMax la liste des distance maximum au sein de chacun des paquets
	 * @param listeAlpha la liste des angles des droites de projection correspondant aux paquets
	 */
	private void nettoyageIntraListe(List<IDirectPositionList> ListePaquets, List<Double> listeDistanceMax, List<Double> listeAlpha){

		int nbrElement = listeAlpha.size();
		int i=0;
		while (i<nbrElement){
			IDirectPositionList liste1 = ListePaquets.get(i);
			int j=i+1;
			while (j<nbrElement){
				IDirectPositionList liste2 = ListePaquets.get(j);
				int compt =0;
				for (int k=0;k<liste1.size();k++){
					if (liste2.contains(liste1.get(k))) compt++;
				}
				if (compt>=2){// Si il y a plus de deux éléments en commun il faut supprimer une des deux listes
					double dmax1 = listeDistanceMax.get(i);
					double dmax2 = listeDistanceMax.get(j);
					// On supprime celle qui a le dmax le plus grand
					if (dmax1>dmax2){// on vire la liste 1
						listeAlpha.remove(i);
						listeDistanceMax.remove(i);
						ListePaquets.remove(i);
						i--;
						nbrElement--;
						break;
					}
					else{ // on vire la liste 2
						listeAlpha.remove(j);
						listeDistanceMax.remove(j);
						ListePaquets.remove(j);
						nbrElement--;
					}
				}
				else{
					j++; // la liste 2 suivante
				}
			}
			i++; // la liste 1 suivante
		}
	}

	/**
	 * Elimination des paquets redondants au sein des listes de paquets de tailles différentes
	 * (deux paquets sont redondants quand ils ont au moins deux éléments en commun).
	 * On supprime le plus petit des deux paquets.
	 * @param ListePaquetsGrand la liste des paquets de plus grande taille
	 * @param listeDistanceMaxGrand la liste des distances maximum au sein de chacun des paquets de plus grande taille
	 * @param listeAlphaGrand la liste des angles des droites de projection correspondant aux paquets de plus grande taille
	 * @param ListePaquetsPetit la liste des paquets de plus petite taille
	 * @param listeDistanceMaxPetit la liste des distances maximum au sein de chacun des paquets de plus petite taille
	 * @param listeAlphaPetit la liste des angles des droites de projection correspondant aux paquets de plus petite taille
	 */
	private void nettoyageInterListe(List<IDirectPositionList> ListePaquetsGrand, List<Double> listeDistanceMaxGrand, List<Double> listAlphaGrand,
			List<IDirectPositionList> ListePaquetsPetit, List<Double> listeDistanceMaxPetit, List<Double> listAlphaPetit){

		int nbrElementGrand = listAlphaGrand.size();
		int i=0;
		while (i<nbrElementGrand){
			IDirectPositionList liste1 = ListePaquetsGrand.get(i);
			int j=0;
			int nbrElementPetit = listAlphaPetit.size();
			while (j<nbrElementPetit){
				IDirectPositionList liste2 = ListePaquetsPetit.get(j);
				int compt =0;
				for (int k=0;k<liste1.size();k++){
					if (liste2.contains(liste1.get(k))) compt++;
				}
				if (compt>=2){// il faut supprimer une des deux listes
					// On supprime celle la structure de la plus petite des listes
					listAlphaPetit.remove(j);
					listeDistanceMaxPetit.remove(j);
					ListePaquetsPetit.remove(j);
					nbrElementPetit--;
				}
				else{
					j++; // la liste 2 suivante
				}
			}
			i++; // la liste 1 suivante
		}
	}

	/**
	 * Calcul de la distance maximum réelle (entre deux points successifs de l'alignement)
	 * @param newListePaquets la liste des paquets
	 * @return listeDistReel la liste des distances maximums réelles (entre deux points successifs de l'alignement)
	 */
	@SuppressWarnings("unused")
	private List<Double> calculDistanceReelle(List<DirectPositionList> newListePaquets){

		List<Double> listeDistReel = new ArrayList<Double>(0);
		for (int i=0;i<newListePaquets.size();i++){
			double distMax = 0;
			DirectPositionList sousListe = newListePaquets.get(i);
			for (int j=0;j<sousListe.size()-1;j++){
				IDirectPosition point1 = sousListe.get(j);
				IDirectPosition point2 = sousListe.get(j+1);
				double dist = point1.distance(point2);
				if (dist>distMax){
					distMax = dist;
				}
			}
			listeDistReel.add(i, distMax);
		}
		return listeDistReel;
	}

	/**
	 * Calcul de la droite de régression par la Méthode des moindres aux carrés
	 * (résultats très mauvais dans quelques cas...).
	 * @param newListePaquets la liste des paquets
	 * @return coefficient la liste des coefficients (a et b) de la droite de régression
	 */
	public List<List<Double>> calculDroiteRegression(List<DirectPositionList> newListePaquets){

		List<List<Double>> coefficient = new ArrayList<List<Double>>(0);
		for (int i=0;i<newListePaquets.size();i++){
			DirectPositionList paquet = newListePaquets.get(i);
			double somX = 0;
			double somY = 0;
			double somXX = 0;
			double somXY = 0;
			double nbr = paquet.size();
			for (int j=0;j<paquet.size();j++){
				double x = paquet.get(j).getX();
				double y = paquet.get(j).getY();
				somX += x;
				somY += y;
				somXX += x*x;
				somXY += x*y;
			}
			double somX2 = somX * somX;
			double a = ((nbr * somXY)-(somX * somY)) / ((nbr * somXX)-somX2);
			double b = ((somY * somXX)-(somX * somXY)) / ((nbr * somXX)-somX2);
			List<Double> coeff = new ArrayList<Double>();
			coeff.add(a);
			coeff.add(b);
			coefficient.add(i,coeff);
		}
		return coefficient;
	}

	/**
	 * Calcul de la droite d'approximation à partir des valeurs de alpha pour tous les paquets
	 * (résultats plus robustes qu'avec la régression linéaire)
	 * @param newListePaquets la liste des paquets
	 * @param listeAlpha la liste des angles des droites de projection
	 * @return coefficient la liste des coefficients (a et b) de la droite de régression
	 */
	public List<List<Double>> calculCoefficientsDroite(List <IDirectPositionList> newListePaquets, List<Double> listeAlpha){

		List<List<Double>> coefficient = new ArrayList<List<Double>>(0);
		for (int i=0;i<newListePaquets.size();i++){
			IDirectPositionList paquet = newListePaquets.get(i);
			double alpha = listeAlpha.get(i);
			List<Double> coeff = calculCoefficient(paquet, alpha);
			coefficient.add(i,coeff);
		}
		return coefficient;
	}

	/**
	 * Calcul de la droite d'approximation à partir de la valeur de alpha pour un paquet
	 * (résultats plus robustes qu'avec la régression linéaire)
	 * @param paquet un paquet de points
	 * @param alpha l'angle de la droite de projection
	 * @return coeff lles coefficients (a et b) de la droite de régression
	 */
	public List<Double> calculCoefficient(IDirectPositionList paquet, double alpha){

		alpha += Math.PI*0.5;
		double moyenneb = 0;
		for (int j=0;j<paquet.size();j++){
			double x = paquet.get(j).getX();
			double y = paquet.get(j).getY();
			double b = y - Math.tan(alpha) * x;
			moyenneb += b;
		}
		moyenneb = moyenneb / paquet.size();
		List<Double> coeff = new ArrayList<Double>();
		coeff.add(Math.tan(alpha));
		coeff.add(moyenneb);

		return coeff;
	}

	/**
	 * Calcul des extrémités de la droite d'approximation pour tous les paquets
	 * @param newListePaquets la liste des paquets
	 * @param coeff la liste des coefficients a et b
	 * @return pointsExtremiteDroite la liste des deux points extrèmes de chaque droite
	 */
	public List<IDirectPositionList> calculExtremiteDroitePaquets(List<IDirectPositionList> newListePaquets, List<List<Double>> coeff){

		List<IDirectPositionList> pointsExtremiteDroite = new ArrayList<IDirectPositionList>(0);
		for (int i=0;i<newListePaquets.size();i++){
			IDirectPositionList paquet = newListePaquets.get(i);
			List<Double> coefficients = coeff.get(i);
			IDirectPositionList extremDroite = calculExtremiteDroite(paquet, coefficients);
			pointsExtremiteDroite.add(extremDroite);
		}
		return pointsExtremiteDroite;
	}

	/**
	 * Calcul des extrémités de la droite de régression pour un paquet
	 * @param paquet un paquet (un alignemets de points)
	 * @param coefficients les coefficients a et b de la droite d'approximation du paquet
	 * @return extremDroite les deux points extrèmes d'une droite
	 */
	public IDirectPositionList calculExtremiteDroite(IDirectPositionList paquet, List<Double> coefficients){

		IDirectPositionList extremDroite = new DirectPositionList();
		double x1,y1,x2,y2;
		double a = coefficients.get(0);
		double b = coefficients.get(1);
		double alpha = Math.atan(a);
		// si la droite est mieux exprimée en X
		if (((alpha>-Math.PI/4)&&(alpha<=0.0))||((alpha>0.0)&&(alpha<=Math.PI/4))||
				((alpha>0.75*Math.PI)&&(alpha<=Math.PI))||((alpha>-Math.PI)&&(alpha<=-0.75*Math.PI))){
			// On récupère les X
			x1 = paquet.get(0).getX();
			x2 = paquet.get(paquet.size()-1).getX();
			// On recalcule les Y
			y1 = a * x1 + b;
			y2 = a * x2 + b;
		}
		else{// si la droite est mieux exprimée en Y
			// On récupère les Y
			y1 = paquet.get(0).getY();
			y2 = paquet.get(paquet.size()-1).getY();
			// on recalcule les X
			x1 = (y1 - b) / a;
			x2 =  (y2 - b) / a;
		}
		if (logger.isTraceEnabled()) {
		    logger.trace(a + " " + b + " " + alpha + " " + x1 + " " + x2);
		}
		// On crée les deux extrémités et on les met dans une liste de points
		DirectPosition point1 = new DirectPosition(x1,y1);
		DirectPosition point2 = new DirectPosition(x2,y2);
		extremDroite.add(point1);
		extremDroite.add(point2);
		return extremDroite;
	}

	/**
	 * Récupération des points situés à moins de 10 mètres de l'alignement pour chaque alignement
	 * @param newListePaquets la liste des paquets de points
	 * @param listePointsExtremites la liste des extrémités de la droite d'approximation
	 */
    private void recuperationPointsOublies(
            List<IDirectPositionList> newListePaquets,
            List<IDirectPositionList> listePointsExtremites) {

		for (int i=0;i<newListePaquets.size();i++){
			IDirectPositionList paquet = newListePaquets.get(i);
			IDirectPositionList extremites = listePointsExtremites.get(i);
			GM_LineString droite = new GM_LineString(extremites);
			IGeometry bufferAlignement = droite.buffer(SEUIL_BUFFER);
			ajoutPointsOublies(bufferAlignement, paquet);
		}
	}

	/**
	 * Ajout d'une liste de points à un alignement
	 * @param bufferAlignement le buffer autour de la droite d'approximation
	 * @param paquet le paquet de points (l'alignement)
	 */
	private void ajoutPointsOublies(IGeometry bufferAlignement,IDirectPositionList paquet){

		for (int j=0;j<listePointsTous.size();j++){
			IDirectPosition pointTeste = listePointsTous.get(j);
			if((bufferAlignement.contains(pointTeste.toGM_Point()))&&(!paquet.contains(pointTeste))){
				// Il faut ajouter ce point à l'alignement
				IDirectPosition pointBout1 = paquet.get(0);
				IDirectPosition pointBout2 = paquet.get(paquet.size()-1);
				double distanceExtrem = pointBout1.distance(pointBout2);
				double distanceBout1 = pointBout1.distance(pointTeste);
				double distanceBout2 = pointBout2.distance(pointTeste);
				// On vérifie que ce nouveau point n'est pas un nouveau bout
				if (distanceBout1>distanceExtrem){
					// Si il est plus loin du bout 2 que le bout 1 : Il faut l'ajouter au bout 2
					paquet.add(paquet.size(), pointTeste);
				}
				else if(distanceBout2>distanceExtrem){
					// Si il est plus loin du bout 1 que le bout 2 : Il faut l'ajouter au bout 1
					paquet.add(0, pointTeste);
				}
				else{ // Sinon on l'ajoute au milieu
					insertionPoint(paquet, pointTeste);
				}
			}
		}
	}

	/**
	 * Récupération des points situés dans le prolongement de l'alignement à moins de 10 mètres de la droite d'approximation
	 * @param newListePaquets la liste des paquets de points
	 * @param listeCoefDroite la liste des coefficients des droites d'approximation
	 */
	private void prolongementAlignement(List<IDirectPositionList> newListePaquets, List<List<Double>> listeCoefDroite){

		for (int i=0;i<newListePaquets.size();i++){
			IDirectPositionList paquet = newListePaquets.get(i);
			List<Double> coefficients = listeCoefDroite.get(i);
			double a = coefficients.get(0);
			double alpha = Math.atan(a);
			double longEnPlus = 200;

			// si la droite est mieux exprimée en X
			if (((alpha>-Math.PI/4)&&(alpha<=0.0))||((alpha>0.0)&&(alpha<=Math.PI/4))||
					((alpha>0.75*Math.PI)&&(alpha<=Math.PI))||((alpha>-Math.PI)&&(alpha<=-0.75*Math.PI))){
				IDirectPosition pointExtremMax, pointExtremMin;
				IDirectPositionList pointsExtrem = calculExtremiteDroite(paquet, coefficients);

				if (pointsExtrem.get(0).getX()>pointsExtrem.get(1).getX()){
					pointExtremMax = pointsExtrem.get(0);
					pointExtremMin = pointsExtrem.get(1);
				}
				else{
					pointExtremMax = pointsExtrem.get(1);
					pointExtremMin = pointsExtrem.get(0);
				}

				IDirectPosition pointMax = getAutrePoint(pointExtremMax, alpha, longEnPlus);
				IDirectPosition pointMin = getAutrePoint(pointExtremMin, alpha, -longEnPlus);
				IDirectPositionList pointsSegmentSupplementaire = new DirectPositionList();
				pointsSegmentSupplementaire.add(pointMax);
				pointsSegmentSupplementaire.add(pointMin);
				GM_LineString droite = new GM_LineString(pointsSegmentSupplementaire);
				IGeometry bufferAlignement = droite.buffer(SEUIL_BUFFER);
				ajoutPointsOublies(bufferAlignement,paquet);
			}
			else{// si la droite est mieux exprimée en Y
				if (alpha<0){alpha += Math.PI;}
				IDirectPosition pointExtremMax, pointExtremMin;
				IDirectPositionList pointsExtrem = calculExtremiteDroite(paquet, coefficients);

				if (pointsExtrem.get(0).getY()>pointsExtrem.get(1).getY()){
					pointExtremMax = pointsExtrem.get(0);
					pointExtremMin = pointsExtrem.get(1);
				}
				else{
					pointExtremMax = pointsExtrem.get(1);
					pointExtremMin = pointsExtrem.get(0);
				}

				IDirectPosition pointMax = getAutrePoint(pointExtremMax, alpha, longEnPlus);
				IDirectPosition pointMin = getAutrePoint(pointExtremMin, alpha, -longEnPlus);
				DirectPositionList pointsSegmentSupplementaire = new DirectPositionList();
				pointsSegmentSupplementaire.add(pointMax);
				pointsSegmentSupplementaire.add(pointMin);
				GM_LineString droite = new GM_LineString(pointsSegmentSupplementaire);
				IGeometry bufferAlignement = droite.buffer(SEUIL_BUFFER);
				ajoutPointsOublies(bufferAlignement,paquet);
			}
		}
	}

	/**
	 * Fusion des alignements présentant des pentes de droites d'approximation très proches
	 * @param newListePaquets la liste des paquets de points
	 * @param listeCoefDroite la liste des coefficients des droites d'approximation
	 */
	private void fusionAlignement(List<IDirectPositionList> newListePaquets, List<List<Double>> listeCoefDroite, List<Double> listeAlpha){

		int i=0;
		while (i<newListePaquets.size()-1){
			IDirectPositionList paquet1 = newListePaquets.get(i);
			List<Double> coefficients1 = listeCoefDroite.get(i);
			double pente1 = coefficients1.get(0);
			double angledeg1 = Math.atan(pente1)*180/Math.PI;
			int j=i+1;
			while(j<newListePaquets.size()){
				IDirectPositionList paquet2 = newListePaquets.get(j);
				List<Double> coefficients2 = listeCoefDroite.get(j);
				double pente2 = coefficients2.get(0);
				double angledeg2 = Math.atan(pente2)*180/Math.PI;
				double diff = Math.abs(angledeg1-angledeg2);
				if (diff>90){diff = 180-diff;}
				if (diff<10){// les deux alignements ont la même pente
					// Détermination du nombre d'éléments en commun
					int compt=0;
					DirectPositionList paquetFusion = new DirectPositionList();
					paquetFusion.addAll(paquet1);
					for (int k=0;k<paquet2.size();k++){
						if (paquet1.contains(paquet2.get(k))){
							compt++;
						}
						else{paquetFusion.add(paquet2.get(k));}
					}


					if(compt>0){// Si il y a au moins un élément commun aux deux alignements
						// Détermination de la distance moyenne séparant les deux alignements
						IDirectPositionList pointsExtremite1 = calculExtremiteDroite(paquet1, coefficients1);
						IDirectPositionList pointsExtremite2 = calculExtremiteDroite(paquet2, coefficients2);
						GM_LineString gmLigne1 = new GM_LineString(pointsExtremite1);
						GM_LineString gmLigne2 = new GM_LineString(pointsExtremite2);
						double distance = distanceMoyenne(gmLigne1, gmLigne2);
						if(distance<10){ // si la distance moyenne séparant les deux alignements est inférieure à 10 m
							// on remet le paquet fusion en ordre
							DirectPositionList paquetFusionTri = triPaquet(paquetFusion);
							// on recalcule alpha
							double differenceAlpha = listeAlpha.get(i)-listeAlpha.get(j);
							if (differenceAlpha>Math.PI*0.5){differenceAlpha = differenceAlpha-Math.PI;}
							else if (differenceAlpha<-Math.PI*0.5){differenceAlpha = differenceAlpha+Math.PI;}
							double alphaFusion = listeAlpha.get(i) - (differenceAlpha / 2);
							if(alphaFusion>Math.PI*0.5){alphaFusion = alphaFusion-Math.PI;}
							else if(alphaFusion<-Math.PI*0.5){alphaFusion = alphaFusion+Math.PI;}
							// on recalcule les coefficients de la droite d'approximation
							List<Double> coeffFusion = calculCoefficient(paquetFusionTri, alphaFusion);
							// on vire les deux paquets
							newListePaquets.remove(j);
							newListePaquets.remove(i);
							// on vire les alphas correspondant
							listeAlpha.remove(j);
							listeAlpha.remove(i);
							// on vire les coefficients de régression correspondant
							listeCoefDroite.remove(j);
							listeCoefDroite.remove(i);
							// on ajoute le paquet, alpha et les coefficients
							newListePaquets.add(i,paquetFusionTri);
							listeAlpha.add(i,alphaFusion);
							listeCoefDroite.add(i,coeffFusion);
							i--;
							break;
						}
					}
				}
				j++; // on passe au paquet 2 suivant
			}
			i++; // on passe au paquet 1 suivant
		}
	}

	/**
	 * Calcul de la distance moyenne entre deux polylignes
	 */
	private double distanceMoyenne(ILineString gmLigne1, ILineString gmLigne2){

		GM_Polygon poly;
		Iterator<IDirectPosition> itPts;

		//fabrication de la surface délimitée par les lignes
		List<IDirectPosition> perimetre = new ArrayList<IDirectPosition>();

    double somDist1 = gmLigne1.startPoint().distance(gmLigne2.startPoint())
        + gmLigne1.endPoint().distance(gmLigne2.endPoint());

    double somDist2 = gmLigne1.startPoint().distance(gmLigne2.endPoint())
        + gmLigne1.endPoint().distance(gmLigne2.startPoint());

		itPts=gmLigne1.coord().getList().iterator();
		while (itPts.hasNext()) {
			IDirectPosition pt = itPts.next();
			perimetre.add(pt);
		}

		itPts=gmLigne2.coord().getList().iterator();
		while (itPts.hasNext()) {
			IDirectPosition pt = itPts.next();
			if (somDist1<somDist2){
				perimetre.add(0,pt);
			}
			else{
				perimetre.add(pt);
			}
		}
		perimetre.add(perimetre.get(0));
		if (perimetre.size() < 4) {
		    logger.error("Not enough control points");
		    return 0;
		}
		poly = new GM_Polygon(new GM_LineString(perimetre));

		double area = poly.area();
		if (area == 0) {
		    logger.error("Error in the computation of the polygon");
	        if (logger.isTraceEnabled()) {
		        logger.trace("line 1 = " + gmLigne1);
		        logger.trace("line 2 = " + gmLigne2);
                logger.trace("poly = " + poly);
		    }
		    return 0;
		}
		double distance = 2*area/(gmLigne1.length()+gmLigne2.length()) ;
		return distance;
	}
}
