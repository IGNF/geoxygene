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
package fr.ign.cogit.appli.geopensim.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.util.algo.MathUtil;

/**
 * Notation d'un alignement : les notes calculées sont basées sur les travaux de Florence Holzapfel.
 * <ul>
 * <li> note basée sur la convexité des bâtiments
 * <li> note basée sur l'aire des bâtiments
 * <li> note basée sur la distance inter bâtiments
 * <li> note basée sur l'étirement de l'alignement (le rapport distance / taille)
 * <li> note basée sur l'orientation des bâtiments
 * <li> note basée sur l'écart des centroïdes de bâtiments à l'axe de l'alignement
 * </ul>
 * Plus les notes sont élevées et moins l'alignement est bon.
 * 
 * @author Florence Curie
 *
 */
public abstract class NoteAlignement {

	/**
	 * Calcul de la note sur la convexité des bâtiments d'un alignement.
	 * @return la note convexité.
	 * @param listeBati la liste des bâtiments composant l'alignement.
	 */
	public static double calculNoteConvexite(Collection<Batiment> listeBati){
		List<Double> listeConvexite = creationListeConvexite(listeBati);
		double ecartTypeConvexiteBatiments = MathUtil.ecartType(listeConvexite);
		double noteConvexite = (0.02 + ecartTypeConvexiteBatiments)/0.03;
		return noteConvexite;
	}

	/**
	 * Création de la liste des convexités des bâtiments de l'alignement.
	 * @return la liste des convexités.
	 * @param listeBati la liste des bâtiments composant l'alignement.
	 */
	private static List<Double> creationListeConvexite(Collection<Batiment> listeBati){
		List<Double> listeConvexite = new ArrayList<Double>(0);
		for (Batiment bat:listeBati) {
			bat.qualifier();
			listeConvexite.add(bat.getConvexite());
		}
		return listeConvexite;
	}

	/**
	 * Calcul de la moyenne des convexités des bâtiments de l'alignement.
	 * @return la convexité moyenne.
	 * @param listeBati la liste des bâtiments composant l'alignement.
	 */
	public static Double calculMoyenneConvexite(List<Batiment> listeBati){
		List<Double> listeConvexite = creationListeConvexite(listeBati);
		double moyenneConvexiteBatiments = MathUtil.moyenne(listeConvexite);
		return moyenneConvexiteBatiments;
	}

	/**
	 * Calcul de la note sur l'aire des bâtiments d'un alignement.
	 * @return la note aire.
	 * @param listeBati la liste des bâtiments composant l'alignement.
	 */
	public static double calculNoteAire(Collection<Batiment> listeBati){
		List<Double> listeAire = creationListeAire(listeBati);
		double ecartTypeAireBatiments=MathUtil.ecartType(listeAire);
		double noteAire = (ecartTypeAireBatiments+65)/64;
		return noteAire;
	}

	/**
	 * Création de la liste des aires des bâtiments de l'alignement.
	 * @return la liste des aires.
	 * @param listeBati la liste des bâtiments composant l'alignement.
	 */
	private static List<Double> creationListeAire(Collection<Batiment> listeBati){
		List<Double> listeAire = new ArrayList<Double>();
		for (Batiment bat:listeBati) {
			bat.qualifier();
			listeAire.add(bat.getAire());
		}
		return listeAire;
	}

	/**
	 * Calcul de la moyenne des aires des bâtiments de l'alignement.
	 * @return l'aire moyenne.
	 * @param listeBati la liste des bâtiments composant l'alignement.
	 */
	public static Double calculMoyenneAire(Collection<Batiment> listeBati){
		List<Double> listeAire = creationListeAire(listeBati);
		double moyenneAireBatiments = MathUtil.moyenne(listeAire);
		return moyenneAireBatiments;
	}

	/**
	 * Calcul de la note sur la distance entre les bâtiments d'un alignement.
	 * @return la note distance.
	 * @param listeBati la liste des bâtiments composant l'alignement.
	 */
	public static double calculNoteDistance(Collection<Batiment> listeBati){
		List<Double> listeDistReel = creationListeDistance(listeBati);
		double moyenneDistancesBatiments=MathUtil.moyenne(listeDistReel);
		double ecartTypeDistancesBatiments=MathUtil.ecartType(listeDistReel, moyenneDistancesBatiments);
		double noteDistance = (ecartTypeDistancesBatiments+2.9)/3.6;
		return noteDistance;
	}
	
	/**
	 * Création de la liste des distances entre les bâtiments de l'alignement.
	 * @return la liste des distances.
	 * @param listeBati la liste des bâtiments composant l'alignement.
	 */	
	public static List<Double> creationListeDistance(Collection<Batiment> listeBati){
		List<Double> listeDistReel = new ArrayList<Double>(0);
		Iterator<Batiment> iterator = listeBati.iterator();
		Batiment previous = iterator.next();
		while (iterator.hasNext()) {
		    Batiment current = iterator.next();
		    IGeometry geomBati1 = previous.getGeom();
			IGeometry geomBati2 = current.getGeom();
			double dist = geomBati1.distance(geomBati2);
			listeDistReel.add(dist);
			previous = current;
		}
		return listeDistReel;
	}

	/**
	 * Calcul de la moyenne des distances entre les bâtiments de l'alignement.
	 * @return la distance moyenne.
	 * @param listeBati la liste des bâtiments composant l'alignement.
	 */
	private static Double calculMoyenneDistance(Collection<Batiment> listeBati){
		List<Double> listeDistance = creationListeDistance(listeBati);
		double moyenneDistanceBatiments = MathUtil.moyenne(listeDistance);
		return moyenneDistanceBatiments;
	}

	/**
	 * Calcul de la note sur l'étirement d'un alignement.
	 * @return la note étirement.
	 * @param listeBati la liste des bâtiments composant l'alignement.
	 */
	public static double calculNoteEtirement(Collection<Batiment> listeBati){
		double moyenneAireBatiments = calculMoyenneAire(listeBati);
		double moyenneDistancesBatiments = calculMoyenneDistance(listeBati);
		double rapportDistTaille = moyenneDistancesBatiments / Math.sqrt(moyenneAireBatiments);
		double noteEtirement;
		if ((rapportDistTaille>2.6)||(moyenneDistancesBatiments>2*moyenneAireBatiments)){
			noteEtirement = 5;
		}
		else{
			noteEtirement = (rapportDistTaille + 0.1)/0.4237;
		}
		return noteEtirement;
	}

	/**
	 * Calcul de la note sur l'orientation des bâtiments d'un alignement.
	 * @return la note orientation.
	 * @param listeBati la liste des bâtiments composant l'alignement.
	 * TODO à modifier : la note orientation n'a pas de sens 
	 */
	public static double calculNoteOrientation(List<Batiment> listeBati){
		List<Double> listeOrientation = creationListeOrientation(listeBati);
		double moyenneOrientationBatiments=MathUtil.moyenne(listeOrientation);
		double ecartTypeOrientationBatiments=MathUtil.ecartType(listeOrientation, moyenneOrientationBatiments);
		double noteOrientation;
		if (ecartTypeOrientationBatiments>0.35){
			noteOrientation = 5;
		}
		else{
			noteOrientation = (ecartTypeOrientationBatiments + 0.1)/0.1;
		}
		return noteOrientation;
	}

	/**
	 * Création de la liste des orientations des murs des bâtiments de l'alignement.
	 * @return la liste des orientatins des murs.
	 * @param listeBati la liste des bâtiments composant l'alignement.
	 */
	private static List<Double> creationListeOrientation(List<Batiment> listeBati){
		List<Double> listeOrientation = new ArrayList<Double>(0);
		for (Batiment bat:listeBati) {
			bat.qualifier();
			listeOrientation.add(bat.getOrientationCotes());
		}
		return listeOrientation;
	}	
	
	/**
	 * Création de la liste des centroïdes des bâtiments de l'alignement.
	 * @return la liste des centroïdes des bâtiments.
	 * @param listeBati la liste des bâtiments composant l'alignement.
	 */
	public static DirectPositionList creationListeCentroid(List<Batiment> listeBati){
		DirectPositionList listeCentroid = new DirectPositionList();
		for (Batiment bat:listeBati) {
			bat.qualifier();
			listeCentroid.add(bat.getCentroid());
		}
		return listeCentroid;
	}
	
	/**
	 * Calcul de la droite d'approximation à partir de la valeur de alpha pour une liste de points
	 * (résultats plus robustes qu'avec la régression linéaire).
	 * @param listePoints une liste de DirectPosition.
	 * @param alpha l'angle de la droite de projection.
	 * @return coeff les coefficients (a et b) de la droite de régression.
	 */
	public static List<Double> calculCoefficient(IDirectPositionList listePoints, double alpha){
		alpha += Math.PI*0.5;
		double moyenneb = 0;
		for (int j=0;j<listePoints.size();j++){
			double x = listePoints.get(j).getX();
			double y = listePoints.get(j).getY();
			double b = y - Math.tan(alpha) * x;
			moyenneb += b; 
		}
		moyenneb = moyenneb / listePoints.size();
		List<Double> coeff = new ArrayList<Double>(0);
		coeff.add(Math.tan(alpha));
		coeff.add(moyenneb);
		return coeff;
	}
	
	/**
	 * Calcul des extrémités de la droite de régression pour une liste de points.
	 * @param listePoints une liste de DirectPosition.
	 * @param coefficients les coefficients a et b de la droite d'approximation de la liste de points.
	 * @return extremDroite les deux points extrèmes d'une droite.
	 */
	public static IDirectPositionList calculExtremiteDroite(IDirectPositionList listePoints, List<Double> coefficients){
		IDirectPositionList extremDroite = new DirectPositionList();
		double x1,y1,x2,y2;
		double a = coefficients.get(0);
		double b = coefficients.get(1);
		double alpha = Math.atan(a);
		// si la droite est mieux exprimée en X
		if (((alpha>-Math.PI/4)&&(alpha<=0.0))||((alpha>0.0)&&(alpha<=Math.PI/4))||
				((alpha>0.75*Math.PI)&&(alpha<=Math.PI))||((alpha>-Math.PI)&&(alpha<=-0.75*Math.PI))){
			// On récupère les X
			x1 = listePoints.get(0).getX();
			x2 = listePoints.get(listePoints.size()-1).getX();
			// On recalcule les Y
			y1 = a * x1 + b;
			y2 = a * x2 + b;
		}
		else{// si la droite est mieux exprimée en Y
			// On récupère les Y
			y1 = listePoints.get(0).getY();
			y2 = listePoints.get(listePoints.size()-1).getY();
			// on recalcule les X
			x1 = (y1 - b) / a;
			x2 =  (y2 - b) / a;
		}
		// On crée les deux extrémités et on les met dans une liste de points
		DirectPosition point1 = new DirectPosition(x1,y1); 
		DirectPosition point2 = new DirectPosition(x2,y2); 
		extremDroite.add(point1);
		extremDroite.add(point2);
		return extremDroite;
	}

	/**
	 * Projection de la liste des centroïdes de bâtiments sur l'axe de l'alignement.
	 * @return la liste des centroïdes des bâtiments projetés sur l'axe de l'alignement.
	 * @param listeCentroid la liste des centroïdes des bâtiments composant l'alignement.
	 * @param extremitesDroite les deux extrémités de l'axe de l'alignement.
	 */
	public static IDirectPositionList projectionCentroid(IDirectPositionList listeCentroid, IDirectPositionList extremitesDroite){
		IDirectPosition pointA = extremitesDroite.get(0);
		IDirectPosition pointB = extremitesDroite.get(1);
		IDirectPositionList listePointsProj = new DirectPositionList();
		for (IDirectPosition centroid:listeCentroid){
			IDirectPosition pointProj = Operateurs.projection(centroid, pointA, pointB);
			listePointsProj.add(pointProj);
		}
		return listePointsProj;
	}
	
	/**
	 * calcul de la distance entre les centroïdes de bâtiments et l'axe de l'alignement.
	 * @return la liste des distances entre les centroïdes des bâtiments et l'axe de l'alignement.
	 * @param listeCentroid la liste des centroïdes des bâtiments composant l'alignement.
	 * @param listePointsProjetes la liste des centroïdes des bâtiments projetés sur l'axe de l'alignement.
	 */
	public static List<Double> distanceCentroidDroite(IDirectPositionList listeCentroid, IDirectPositionList listePointsProjetes){
		List<Double> listeDistance = new ArrayList<Double>(0);
		for (int i=0;i<listeCentroid.size();i++){
			IDirectPosition centroid = listeCentroid.get(i);
			IDirectPosition pointProj = listePointsProjetes.get(i);
			double distance = centroid.distance(pointProj);
			listeDistance.add(distance);
		}
		return listeDistance;
	}
	
	/**
	 * calcul de la distance entre les centroïdes de bâtiments et l'axe de l'alignement.
	 * @return la liste des distances entre les centroïdes des bâtiments et l'axe de l'alignement.
	 * @param listeBati a liste des bâtiments composant l'alignement.
	 * @param alpha l'angle de la droite de projection.
	 */
	public static List<Double> distanceCentroidDroite(List<Batiment> listeBati, double alpha){
		IDirectPositionList listeCentroid = NoteAlignement.creationListeCentroid(listeBati);
		List<Double> coefficient = NoteAlignement.calculCoefficient(listeCentroid, alpha);
		IDirectPositionList pointsExtrem = NoteAlignement.calculExtremiteDroite(listeCentroid, coefficient);
		IDirectPositionList listePointsProjetes = NoteAlignement.projectionCentroid(listeCentroid, pointsExtrem);
		
		List<Double> listeDistance = new ArrayList<Double>(0);
		for (int i=0;i<listeCentroid.size();i++){
			IDirectPosition centroid = listeCentroid.get(i);
			IDirectPosition pointProj = listePointsProjetes.get(i);
			double distance = centroid.distance(pointProj);
			listeDistance.add(distance);
		}
		return listeDistance;
	}
	
	/**
	 * Calcul de la note sur l'alignement des bâtiments par rapport à la droite de projection.
	 * @return la note aligne.
	 * @param listeDistance la liste des distances entre bâtiments et axe de l'alignement.
	 * TODO à modifier : la note alignement n'est pas assez complète. 
	 */
	public static double calculNoteAlign(List<Double> listeDistance){
		double moyenneDistancesDroite = MathUtil.moyenne(listeDistance);
		double noteAlign = 0;
		if (moyenneDistancesDroite<=2){
			noteAlign = 1.5;
		}
		else if (moyenneDistancesDroite<=7){
			noteAlign = 3;
		}
		else{
			noteAlign = 5;
		}
		return noteAlign;
	}
	
	/**
	 * Calcul de la note générale de l'alignement.
	 * @return la note générale.
	 * @param noteAire la note de l'alignement sur l'aire des bâtiments.
	 * @param noteConvexite la note de l'alignement sur la convexité des bâtiments.
	 * @param noteDistance la note de l'alignement sur la distance inter bâtiments.
	 * @param noteEtirement la note de l'alignement sur son étirement.
	 */
	public static double calculNoteGenerale(double noteAire, double noteConvexite, double noteDistance, double noteEtirement){
		double lambdaAire = 1;
		double lambdaEtirement = 1;
		double lambdaConvexite = 1;
		double lambdaDistance = 1;
		//double lambdaOrientation = 1;
		//double lambdaAligne = 1;

		double numerateur = lambdaAire * noteAire + lambdaEtirement * noteEtirement +
		 lambdaConvexite * noteConvexite + lambdaDistance *	noteDistance ;
		// + lambdaAligne * noteAligne + lambdaOrientation * noteOrientation;
		double denominateur = lambdaAire + lambdaEtirement + lambdaDistance +
		lambdaConvexite;  //+ lambdaAligne + lambdaOrientation;
		double noteFinale = numerateur /denominateur;
		return noteFinale;
	}
	
	/**
	 * Calcul de la note générale de l'alignement.
	 * @return la note générale.
	 * @param listeBati une liste de bâtiments
	 */
	public static double calculNoteGenerale(List<Batiment> listeBati){
		double noteAire = calculNoteAire(listeBati);
		double noteConvexite = calculNoteConvexite(listeBati);
		double noteDistance = calculNoteDistance(listeBati);
		double noteEtirement = calculNoteEtirement(listeBati);
		
		double noteFinale = calculNoteGenerale(noteAire, noteConvexite, noteDistance, noteEtirement);
		return noteFinale;
	}
}
