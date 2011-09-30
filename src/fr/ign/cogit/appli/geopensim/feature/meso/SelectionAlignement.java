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
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;
import fr.ign.cogit.appli.geopensim.feature.micro.Troncon;
import fr.ign.cogit.appli.geopensim.util.NoteAlignement;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;



/**
 * 
 * Sélection parmi les alignements Détectés (via la Méthode développée par S. Christophe) de
 * ceux qui sont les plus évidents et notation de ces alignements (Méthode développée par
 * F. Holzapfel) en fonction de :
 * <ul> 
 * <li> l'aire et de la convexité des bâtiments,
 * <li> la distance entre bâtiments,
 * <li> l'étirement de l'alignement,
 * <li> l'orientation des murs des bâtiments et
 * <li> la qualité de l'alignement des bâtiments par rapport à l'axe de l'alignement.
 * </ul>
 * @author Florence Curie
 *
 */

public class SelectionAlignement {
	static Logger logger=Logger.getLogger(SelectionAlignement.class.getName());

	List<List<Batiment>> listeAlign = new ArrayList<List<Batiment>>();
	List<Double> listeAlpha = new ArrayList<Double>();
	ZoneElementaireUrbaine zoneElem; 
	/**
	 * Constructeur
	 */
	public SelectionAlignement(List<List<Batiment>> listeAlign, List<Double> listeAlpha,ZoneElementaireUrbaine zoneElem){
		this.listeAlpha = listeAlpha;
		this.listeAlign = listeAlign;
		this.zoneElem = zoneElem;
	}

	public void qualification ()   {

		for (int k=0;k<listeAlign.size();k++){

			// On récupère la liste de batiment à traiter
			List<Batiment> listeBati = listeAlign.get(k);
			double alpha = listeAlpha.get(k);
			Set<Troncon> troncons = zoneElem.getTroncons();
			int nombreBatiments = listeBati.size();

			// CRITERE 1 : la forme (la convexité des bâtiments)
			double noteConvexite = NoteAlignement.calculNoteConvexite(listeBati);
			while ((noteConvexite > 4)&&(listeBati.size()>3)){// la note sur la convexité est mauvaise : on vire le bâtiment le plus extrème de l'alignement
				double moyenneConvexiteBatiments = NoteAlignement.calculMoyenneConvexite(listeBati);
				double diffConvexiteMax = 0;
				Batiment batiAVirer = listeBati.get(0);
				for (Batiment bati:listeBati){
					double diffConvexiteComp = Math.abs(moyenneConvexiteBatiments-bati.getConvexite());
					if (diffConvexiteComp>diffConvexiteMax){
						diffConvexiteMax = diffConvexiteComp;
						batiAVirer = bati;
					}
				}
				listeBati.remove(batiAVirer); 
				noteConvexite = NoteAlignement.calculNoteConvexite(listeBati);
			}

			// Si le nombre de bâtiment a été modifié on vérifie que l'alignement 
			// possède plus de 4 bâtiments et on relance le calcul depuis le Début
			if (nombreBatiments>listeBati.size()){
				if (listeBati.size()<4){
					listeAlign.remove(listeBati);
					listeAlpha.remove(k);
				}
				k--;
				continue;
			}

			// CRITERE 2 : La taille (l'aire des bâtiments)
			double noteAire = NoteAlignement.calculNoteAire(listeBati);
			while ((noteAire > 3)&&(listeBati.size()>3)){// la note sur l'aire est mauvaise : on vire le bâtiment le plus extrème de l'alignement
				double moyenneAireBatiments = NoteAlignement.calculMoyenneAire(listeBati);
				double diffAireMax = 0;
				Batiment batiAVirer = listeBati.get(0);
				for (Batiment bati:listeBati){
					double diffAireComp = Math.abs(moyenneAireBatiments-bati.getAire());
					if (diffAireComp>diffAireMax){
						diffAireMax = diffAireComp;
						batiAVirer = bati;
					}
				}
				listeBati.remove(batiAVirer);
				noteAire =  NoteAlignement.calculNoteAire(listeBati);
			}

			// Si le nombre de bâtiment a été modifié on vérifie que l'alignement 
			// possède plus de 4 bâtiments et on relance le calcul depuis le Début
			if (nombreBatiments>listeBati.size()){
				if (listeBati.size()<4){
					listeAlign.remove(listeBati);
					listeAlpha.remove(k);
				}
				k--;
				continue;
			}

			// CRITERE 3 : l'alignement (Distance entre chaque centre et la droite de régression)
			List<Double> listeDistance = NoteAlignement.distanceCentroidDroite(listeBati, alpha);
			double noteAligne = NoteAlignement.calculNoteAlign(listeDistance);

			while ((noteAligne>4)&&(listeBati.size()>3)){// il faut nettoyer l'alignement on vire les points les plus distants
				double distMax = 0;
				int indBatiAVirer = 0;
				for (int i=0;i<listeDistance.size();i++){
					if (listeDistance.get(i)>distMax){
						distMax = listeDistance.get(i);
						indBatiAVirer = i;
					}
				}
				listeBati.remove(indBatiAVirer);
				listeDistance.remove(indBatiAVirer);
				noteAligne = NoteAlignement.calculNoteAlign(listeDistance);
			}

			// Si le nombre de bâtiment a été modifié on vérifie que l'alignement 
			// possède plus de 4 bâtiments et on relance le calcul depuis le Début
			if (nombreBatiments>listeBati.size()){
				if (listeBati.size()<4){
					listeAlign.remove(listeBati);
					listeAlpha.remove(k);
				}
				k--;
				continue;
			}

			// CRITERE 4 : la distance (Distance entre deux bâtiments consécutifs)
			List<Double> listeDistReel = NoteAlignement.creationListeDistance(listeBati);
			double noteDistance =  NoteAlignement.calculNoteDistance(listeBati);
			if (noteDistance > 3){// la note sur l'interdistance est mauvaise, il faut couper l'alignement
				// On recherche le lien le plus lache
				int indMax = 0;
				double distMax = 0;
				for (int i=0;i<listeDistReel.size();i++){
					if (listeDistReel.get(i)> distMax){
						indMax = i;
						distMax = listeDistReel.get(i);
					}
				}
				// On le coupe et on crée deux nouveaux alignements
				List<Batiment> listeBati1 = new ArrayList<Batiment>();
				for (int i=0;i<indMax+1;i++){
					listeBati1.add(listeBati.get(i));
				}
				List<Batiment> listeBati2 = new ArrayList<Batiment>();
				for (int i=indMax+1;i<listeBati.size();i++){
					listeBati2.add(listeBati.get(i));
				}
				listeAlign.remove(listeBati);
				listeAlpha.remove(k);
				if (listeBati1.size()>=4) {
					listeAlign.add(listeBati1);
					listeAlpha.add(alpha);
				}
				if (listeBati2.size()>=4) {
					listeAlign.add(listeBati2);
					listeAlpha.add(alpha);
				}
				k--;
				continue;
			}

			// CRITERE 5 : l'étirement (le rapport distance / taille)
			double noteEtirement = NoteAlignement.calculNoteEtirement(listeBati);
			if (noteEtirement>4){
				listeAlign.remove(listeBati);
				listeAlpha.remove(k);
				k--;
				continue;
			}

			// CRITERE 6 : l'orientation des murs (Orientation principale des murs des bâtiments)
			@SuppressWarnings("unused")
			double noteOrientation = NoteAlignement.calculNoteOrientation(listeBati);

			// REGLE 1 : deux bâtiments ne peuvent pas être côte à côte 
			// on calcule le rapport entre la distance euclidienne entre deux centroïdes consécutifs et 
			// la distance entre les points correspondant aux projections sur l'axe de l'alignement 
			DirectPositionList listeCentroid = NoteAlignement.creationListeCentroid(listeBati);
			List<Double> coefficient = NoteAlignement.calculCoefficient(listeCentroid, alpha);
			IDirectPositionList pointsExtrem = NoteAlignement.calculExtremiteDroite(listeCentroid, coefficient);
			IDirectPositionList listePointsProjetes = NoteAlignement.projectionCentroid(listeCentroid, pointsExtrem);
			List<Double> distanceCentroid = distancePoints(listeCentroid);
			List<Double> distancePointsProjetes = distancePoints(listePointsProjetes);
			List<Double> distanceCentroidPointProjetes = NoteAlignement.distanceCentroidDroite(listeCentroid, listePointsProjetes);
			for (int i=0;i<distanceCentroid.size();i++){
				double rapportDistance = distancePointsProjetes.get(i)/distanceCentroid.get(i);
				if (rapportDistance<0.5){// deux bâtiments sont côte à côte 
					// on supprime le bâtiment le plus éloigné de l'axe
					if (distanceCentroidPointProjetes.get(i)>distanceCentroidPointProjetes.get(i+1)){
						// on vire le premier bati
						listeBati.remove(i);
						listeCentroid.remove(i);
						listePointsProjetes.remove(i);
						// on recalcule les distances 
						distanceCentroid = distancePoints(listeCentroid);
						distancePointsProjetes = distancePoints(listePointsProjetes);
						distanceCentroidPointProjetes = NoteAlignement.distanceCentroidDroite(listeCentroid, listePointsProjetes);
						i--;
					}
					else{// on vire le second bati
						listeBati.remove(i+1);
						listeCentroid.remove(i+1);
						listePointsProjetes.remove(i+1);
						// on recalcule les distances 
						distanceCentroid = distancePoints(listeCentroid);
						distancePointsProjetes = distancePoints(listePointsProjetes);
						distanceCentroidPointProjetes = NoteAlignement.distanceCentroidDroite(listeCentroid, listePointsProjetes);
						i--;
					}
				}
			}

			// Si le nombre de bâtiment a été modifié on vérifie que l'alignement 
			// possède plus de 4 bâtiments et on relance le calcul depuis le Début
			if (nombreBatiments>listeBati.size()){
				if (listeBati.size()<4){
					listeAlign.remove(listeBati);
					listeAlpha.remove(k);
				}
				k--;
				continue;
			}

			// REGLE 2 : Un tronçon interrompt forcèment un alignement
			// Création de polylignes à partir des listes de DirectPosition	
			DirectPositionList listeCentroid3 = NoteAlignement.creationListeCentroid(listeBati);
			GM_LineString ligneAligne = new GM_LineString(listeCentroid3);
			boolean intersection = false;
			int indiceCoupure = 0;
			for (Troncon tronc : troncons){
				if (ligneAligne.intersects(tronc.getGeom())){// il faut trouver où et couper l'alignement
					intersection = true;
					for (int pp=0;pp<listeCentroid3.size()-1;pp++){
						DirectPositionList listeCentroidMini = new DirectPositionList();
						listeCentroidMini.add(listeCentroid3.get(pp));
						listeCentroidMini.add(listeCentroid3.get(pp+1));
						GM_LineString ligneAligneMini = new GM_LineString(listeCentroidMini);
						if (ligneAligneMini.intersects(tronc.getGeom())){
							indiceCoupure = pp;
						}
					}
				}
			}
			if (intersection){
				// On le coupe et on crée deux nouveaux alignements
				List<Batiment> listeBati1 = new ArrayList<Batiment>();
				for (int i=0;i<indiceCoupure+1;i++){
					listeBati1.add(listeBati.get(i));
				}
				List<Batiment> listeBati2 = new ArrayList<Batiment>();
				for (int i=indiceCoupure+1;i<listeBati.size();i++){
					listeBati2.add(listeBati.get(i));
				}
				listeAlign.remove(listeBati);
				listeAlpha.remove(k);
				if (listeBati1.size()>=4) {
					listeAlign.add(listeBati1);
					listeAlpha.add(alpha);
				}
				if (listeBati2.size()>=4) {
					listeAlign.add(listeBati2);
					listeAlpha.add(alpha);
				}
				k--;
				continue;
			}

			// Calcul note générale
			double noteGenerale = NoteAlignement.calculNoteGenerale(noteAire, noteConvexite, noteDistance, noteEtirement);

			//Affichage des caractéristiques des restants :
			if (logger.isDebugEnabled()) {
				//logger.debug("note aire = "+noteAire);
				//logger.debug("note étirement = "+noteEtirement);
				//logger.debug("note alignement = "+noteAligne);
				//logger.debug("note convexité = "+noteConvexite);
				//logger.debug("note distance = "+noteDistance);
				//logger.debug("note orientation = "+noteOrientation);
				logger.debug("note générale de l'alignement = "+noteGenerale);
			}
		}

		// On supprime les alignements redondants
		nettoyageListe(listeAlign,listeAlpha);
	}

	/**
	 * Elimination des alignements redondants au sein de la liste des alignements
	 * @param newListeAlign la liste des alignements
	 * @param newListeAlpha la liste des angles des droites de projection correspondant aux paquets
	 */
	private void nettoyageListe(List<List<Batiment>> newListeAlign, List<Double> newListeAlpha){

		int nbrElement = listeAlign.size();
		int i=0;
		while (i<nbrElement){
			List<Batiment> liste1 = newListeAlign.get(i);
			int j=i+1;
			while (j<nbrElement){
				List<Batiment> liste2 = newListeAlign.get(j);
				if (liste1.containsAll(liste2)){// on vire liste2
					listeAlign.remove(j);
					newListeAlpha.remove(j);
					nbrElement--;
				}
				else if(liste2.containsAll(liste1)){// on vire liste1
					listeAlign.remove(i);
					newListeAlpha.remove(i);
					i--;
					nbrElement--;
					break;
				}
				else{
					j++; // la liste 2 suivante
				}
			}
			i++; // la liste 1 suivante
		}
	}

	/**
	 * calcul des distances entre deux points consécutifs d'une liste de points.
	 * @return la liste des distances entre deux points consécutifs d'une liste de points.
	 * @param listePoints une liste de DirectPosition.
	 */
	public List<Double> distancePoints(IDirectPositionList listePoints){
		List<Double> listeDistance = new ArrayList<Double>();
		for (int i=0;i<listePoints.size()-1;i++){
			IDirectPosition point1 = listePoints.get(i);
			IDirectPosition point2 = listePoints.get(i+1);
			double distance = point1.distance(point2);
			listeDistance.add(distance);
		}
		return listeDistance;
	}

}
