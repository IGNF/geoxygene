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

package fr.ign.cogit.geoxygene.contrib.appariement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;


/**
 * Resultats de la réalisation d'un appariement : un ensemble de liens.
 * 
 * @author Mustiere / IGN Laboratoire COGIT
 * @version 1.0
 */

public class EnsembleDeLiens extends Population {

	public EnsembleDeLiens() {
		super(false, "Ensemble de liens", Lien.class, true);		
	}

	public EnsembleDeLiens(boolean persistant) {
		super(persistant, "Ensemble de liens", Lien.class, true);		
	}

	public EnsembleDeLiens(Class classeDesLiens) {
		super(false, "Ensemble de liens", classeDesLiens, true);		
	}

	/** Nom du l'ensemble des liens d'appariement  (ex: "Appariement des routes par la méthode XX")*/
	private String nom ;
	public String getNom() {return nom;}
	public void setNom(String nom) {this.nom = nom;}

	/** Description textuelle des paramètres utilisés pour l'appariement */
	private String parametrage ;
	public String getParametrage() {return parametrage;}
	public void setParametrage(String parametrage) {this.parametrage = parametrage;}

	/** Description textuelle du résultat de l'auto-évaluation des liens */
	private String evaluationInterne ;
	public String getEvaluationInterne() {return evaluationInterne;}
	public void setEvaluationInterne(String evaluation) {evaluationInterne = evaluation;}

	/** Description textuelle du résultat de l'évaluation globale des liens */
	private String evaluationGlobale ;
	public String getEvaluationGlobale() {return evaluationGlobale;}
	public void setEvaluationGlobale(String evaluation) {evaluationGlobale = evaluation;}


	//////////////////////////////////////////////////////////////
	// METHODES UTILES A LA MANIPULATION DES ENSEMBLES DE LIENS   
	//////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////
	// MANIPULATION DE BASE DES LISTES D'ELEMENTS   
	//////////////////////////////////////////////////////////////
	/** Copie d'un ensemble de liens */
	public EnsembleDeLiens copie() {
		EnsembleDeLiens copie = new EnsembleDeLiens();
		copie.setElements(this.getElements());
		copie.setNom(this.getNom());
		copie.setEvaluationGlobale(this.getEvaluationGlobale());			
		copie.setEvaluationInterne(this.getEvaluationInterne());
		return copie;			
	}

	/** Compilation de deux listes de liens */
	public static EnsembleDeLiens compile(EnsembleDeLiens liens1, EnsembleDeLiens liens2) {
		EnsembleDeLiens total = liens1.copie();
		total.compile(liens2);
		return total;
	}

	/** Ajout des liens à this. NB: modifie this. */
	public void compile(EnsembleDeLiens liensAAjouter) {
		this.getElements().addAll(liensAAjouter.getElements());
	}

	/** Regroupement de liens pointant vers les mêmes objets.
	 * Autrement dit : les liens en entrée forment un graphe entre les objets;
	 * la méthode crée un seul lien pour toute partie connexe du graphe.
	 * exemple : Ref = (A,B,C), Comp = (X,Y,Z)
	 * en entrée (this) on a 4 liens 1-1 (A-X) (B-X) (B-Y) (C-Z)
	 * en sortie on a un lien n-m (A,B)-(X-Y) et 1 lien 1-1 (C-Z)
	 * 
	 */ 
	public EnsembleDeLiens regroupeLiens(Population popRef, Population popComp) {
		EnsembleDeLiens liensGroupes ;
		Lien lienGroupe;
		CarteTopo grapheDesLiens ;
		Groupe groupeTotal, groupeConnexe;
		Iterator itGroupes, itNoeuds;
		Noeud noeud;
		FT_Feature feat;
		grapheDesLiens = this.transformeEnCarteTopo(popRef, popComp);
		groupeTotal = (Groupe)grapheDesLiens.getPopGroupes().nouvelElement();
		groupeTotal.setListeArcs(grapheDesLiens.getListeArcs());
		groupeTotal.setListeNoeuds(grapheDesLiens.getListeNoeuds());
		groupeTotal.decomposeConnexes();

		liensGroupes = new EnsembleDeLiens();
		liensGroupes.setNom("TMP : liens issus d'un regroupement");
		// on parcours tous les groupes connexes créés 
		itGroupes = grapheDesLiens.getListeArcs().iterator();
		while (itGroupes.hasNext()) {
			groupeConnexe = (Groupe)itGroupes.next();
			if ( groupeConnexe.getListeArcs().size() == 0 ) continue; // cas des noeuds isolés
			lienGroupe = (Lien)liensGroupes.nouvelElement();
			itNoeuds = groupeConnexe.getListeNoeuds().iterator();
			while (itNoeuds.hasNext()) {
				noeud = (Noeud)itNoeuds.next();
				feat = noeud.getCorrespondant(0);
				if ( popRef.getElements().contains(feat) ) 
					lienGroupe.addObjetRef(feat);
				if ( popComp.getElements().contains(feat) ) 
					lienGroupe.addObjetComp(feat);
				// nettoyage de la carteTopo créée
				noeud.setCorrespondants(new ArrayList());
			}
		}
		return liensGroupes;
	}

	/** Regroupement de liens pointant vers les mêmes objets.
	 * Autrement dit : les liens en entrée forment un graphe entre les objets;
	 * la méthode crée un seul lien pour toute partie connexe du graphe.
	 * exemple : Ref = (A,B,C), Comp = (X,Y,Z)
	 * en entrée (this) on a 4 liens 1-1 (A-X) (B-X) (B-Y) (C-Z)
	 * en sortie on a un lien n-m (A,B)-(X-Y) et 1 lien 1-1 (C-Z)
	 */ 
	public EnsembleDeLiens regroupeLiens(FT_FeatureCollection popRef, FT_FeatureCollection popComp) {
		EnsembleDeLiens liensGroupes ;
		Lien lienGroupe;
		CarteTopo grapheDesLiens ;
		Groupe groupeTotal, groupeConnexe;
		Iterator itGroupes, itNoeuds;
		Noeud noeud;
		FT_Feature feat;
		grapheDesLiens = this.transformeEnCarteTopo(popRef, popComp);
		groupeTotal = (Groupe)grapheDesLiens.getPopGroupes().nouvelElement();
		groupeTotal.setListeArcs(grapheDesLiens.getListeArcs());
		groupeTotal.setListeNoeuds(grapheDesLiens.getListeNoeuds());
		groupeTotal.decomposeConnexes();

		liensGroupes = new EnsembleDeLiens();
		liensGroupes.setNom("TMP : liens issus d'un regroupement");
		// on parcours tous les groupes connexes créés 
		itGroupes = grapheDesLiens.getListeArcs().iterator();
		while (itGroupes.hasNext()) {
			groupeConnexe = (Groupe)itGroupes.next();
			if ( groupeConnexe.getListeArcs().size() == 0 ) continue; // cas des noeuds isolés
			lienGroupe = (Lien)liensGroupes.nouvelElement();
			itNoeuds = groupeConnexe.getListeNoeuds().iterator();
			while (itNoeuds.hasNext()) {
				noeud = (Noeud)itNoeuds.next();
				feat = noeud.getCorrespondant(0);
				if ( popRef.getElements().contains(feat) ) 
					lienGroupe.addObjetRef(feat);
				if ( popComp.getElements().contains(feat) ) 
					lienGroupe.addObjetComp(feat);
				// nettoyage de la carteTopo créée
				noeud.setCorrespondants(new ArrayList());
			}
		}
		return liensGroupes;
	}


	/** Transforme les liens, qui relient des objets de popRef et popComp,
	 * en une carte topo (graphe sans géométrie) où :
	 * - les objets de popRef et popComp sont des noeuds (sans géométrie)
	 * - les liens sont des arcs entre ces noeuds (sans géométrie)
	 */ 
	public CarteTopo transformeEnCarteTopo(FT_FeatureCollection popRef, FT_FeatureCollection popComp) {
		Iterator itObjetsRef, itObjetsComp, itNoeudsRef, itNoeudsComp; 
		List noeudsRef = new ArrayList(), noeudsComp = new ArrayList(); // listes de Noeud

		Lien lien;
		CarteTopo grapheDesLiens = new CarteTopo("Carte de liens");
		FT_Feature objetRef, objetComp  ;
		Noeud noeudRef, noeudComp;
		Arc arc;

		// création de noeuds du graphe = les objets ref et comp
		itObjetsRef = popRef.getElements().iterator();
		while (itObjetsRef.hasNext()){
			objetRef = (FT_Feature)itObjetsRef.next();
			noeudRef = (Noeud)grapheDesLiens.getPopNoeuds().nouvelElement();
			noeudRef.addCorrespondant(objetRef);
		}
		itObjetsComp = popComp.getElements().iterator();
		while (itObjetsComp.hasNext()){
			objetComp = (FT_Feature)itObjetsComp.next();
			noeudComp = (Noeud)grapheDesLiens.getPopNoeuds().nouvelElement();
			noeudComp.addCorrespondant(objetComp);
		}

		// création des arcs du graphe = les liens d'appariement 
		Iterator itLiens = this.getElements().iterator();
		while (itLiens.hasNext()) {
			lien = (Lien)itLiens.next();
			itObjetsRef = lien.getObjetsRef().iterator();
			// création des listes de noeuds concernés par le lien
			noeudsRef.clear();
			while (itObjetsRef.hasNext()) {
				objetRef = (FT_Feature)itObjetsRef.next();
				noeudsRef.add(objetRef.getCorrespondants(grapheDesLiens.getPopNoeuds()).get(0));
			}
			itObjetsComp = lien.getObjetsComp().iterator();
			noeudsComp.clear();
			while (itObjetsComp.hasNext()) {
				objetComp = (FT_Feature)itObjetsComp.next();
				noeudsComp.add(objetComp.getCorrespondants(grapheDesLiens.getPopNoeuds()).get(0));
			}

			// instanciation des arcs
			itNoeudsRef = noeudsRef.iterator();
			while(itNoeudsRef.hasNext()){
				noeudRef = (Noeud)itNoeudsRef.next();
				itNoeudsComp = noeudsComp.iterator();
				while(itNoeudsComp.hasNext()){
					noeudComp = (Noeud)itNoeudsComp.next();
					arc = (Arc)grapheDesLiens.getPopArcs().nouvelElement();
					arc.addCorrespondant(lien);
					arc.setNoeudIni(noeudRef);
					arc.setNoeudFin(noeudComp);
				}
			}
		}
		return grapheDesLiens;
	}

	/** Methode de regroupement des liens 1-1 carto qui pointent sur le meme
	 * objet topo; non optimisee du tout!!!
	 * @return
	 */
	public EnsembleDeLiens regroupeLiensCartoQuiPointentSurMemeTopo() {
		List liens = this.getElements();
		List remove = new ArrayList(), objetsRef = new ArrayList(),objetsComp = new ArrayList();
		Object objetTest;
		int i,j,k;

		for(i=0;i<liens.size();i++){
			objetsComp.add(((Lien)liens.get(i)).getObjetsComp().get(0));
			objetsRef.add(((Lien)liens.get(i)).getObjetsRef().get(0));
		}

		//regroupement des liens
		for(j=0;j<liens.size();j++){
			objetTest = objetsComp.get(j);
			for (k=j;k<liens.size();k++){
				if (objetTest.equals(objetsComp.get(k))&&j!=k){
					((Lien)liens.get(j)).addObjetRef((FT_Feature)objetsRef.get(k));
					if (!remove.contains(new Integer(k))){
						remove.add(new Integer(k));	
					}
				}
			}
		}

		//destruction des liens superflus
		SortedSet s = Collections.synchronizedSortedSet(new TreeSet());
		s.addAll(remove);
		remove = new ArrayList(s);
		for (i=s.size()-1;i>=0;i--){
			liens.remove(((Integer)remove.get(i)).intValue());
		}
		return this;
	}



	//////////////////////////////////////////////////////////////
	// AUTOUR DE L'EVALUATION   
	//////////////////////////////////////////////////////////////

	/** Filtrage des liens, on ne retient que ceux dont l'évaluation
	 * est supérieure ou égale au seuil passé en paramètre.
	 */
	public void filtreLiens(float seuilEvaluation) {
		Lien lien;
		Iterator itLiens =this.getElements().iterator(); 
		while(itLiens.hasNext()){
			lien = (Lien)itLiens.next();
			if ( lien.getEvaluation() < seuilEvaluation ) this.enleveElement(lien);
		}
	}


	/** Change l'évaluation d'un lien si celui-ci a un nombre d'objets associés
	 * à la base de comparaison (flag=true) ou à la base de référence (flag=false)
	 * supérieur au seuilCardinalite, en lui donnant la valeur nulle.
	 * Si le seuil n'est pas dépassé, l'évaluation du lien reste ce qu'elle est.
	 * @param flag : true si l'on s'intéresse aux objets de la base de comparaison des liens,
	 * false s'il s'agit de la base de référence
	 * @param seuilCardinalite : seuil au dessus duquel la méthode affecte une évaluation nulle
	 * au lien
	 */
	public void evaluationLiensParCardinalite(boolean flag,double seuilCardinalite){
		Lien lien;
		Iterator itLiens =this.getElements().iterator(); 
		if (flag) {
			while(itLiens.hasNext()){
				lien = (Lien)itLiens.next();
				if (lien.getObjetsComp().size()>seuilCardinalite) lien.setEvaluation(0);
			}
		}
		else{
			while(itLiens.hasNext()){
				lien = (Lien)itLiens.next();
				if (lien.getObjetsRef().size()>seuilCardinalite) lien.setEvaluation(0);
			}			
		}	
	}

	/** Crée une liste de population en fonction des seuils sur l'evaluation
	 *  passés en paramètre.
	 * Exemple: si la liste en entree contient 2 "Double" (0.5, 1), 
	 * alors renvoie 3 populations avec les liens ayant respectivement leur évaluation...
	 * 0: inférieur à 0.5 (strictement)
	 * 1: entre 0.5 et 1 (strictement sur 1)
	 * 2: supérieur ou égal à 1
	 */
	public List classeSelonSeuilEvaluation(List valeursClassement) {
		List liensClasses = new ArrayList();
		Iterator itLiens = this.getElements().iterator();
		Lien lien, lienClasse;
		double seuil;
		int i;
		boolean trouve;

		for(i=0;i<=valeursClassement.size();i++) {
			liensClasses.add(new EnsembleDeLiens());
		}
		while (itLiens.hasNext()) {
			lien= (Lien) itLiens.next();
			trouve = false;
			for(i=0;i<valeursClassement.size();i++) {
				seuil = ((Double)valeursClassement.get(i)).doubleValue();
				if ( lien.getEvaluation()< seuil ) {
					lienClasse = (Lien)((EnsembleDeLiens)liensClasses.get(i)).nouvelElement();
					lienClasse.setEvaluation(lien.getEvaluation());
					lienClasse.setGeom(lien.getGeom());
					lienClasse.setCommentaire(lien.getCommentaire());
					lienClasse.setCorrespondants(lien.getCorrespondants());
					lienClasse.setObjetsComp(lien.getObjetsComp());
					lienClasse.setObjetsRef(lien.getObjetsRef());
					lienClasse.setIndicateurs(lien.getIndicateurs());
					trouve = true;
					break;
				}
			}
			if (trouve) continue;
			lienClasse = (Lien)((EnsembleDeLiens)liensClasses.get(valeursClassement.size())).nouvelElement();
			lienClasse.setEvaluation(lien.getEvaluation());
			lienClasse.setGeom(lien.getGeom());
			lienClasse.setCommentaire(lien.getCommentaire());
			lienClasse.setCorrespondants(lien.getCorrespondants());
			lienClasse.setObjetsComp(lien.getObjetsComp());
			lienClasse.setObjetsRef(lien.getObjetsRef());
			lienClasse.setIndicateurs(lien.getIndicateurs());
		}
		return liensClasses;
	}


	//////////////////////////////////////////////////////////////
	// AUTOUR DE LA GEOMETRIE ET DE LA VISUALISATION   
	//////////////////////////////////////////////////////////////

	/** Affecte une géométrie à l'ensemble des liens, cette géométrie
	 * relie les centroïdes des objets concernés entre eux */
	public void creeGeometrieDesLiens() {
		Iterator itLiens = this.getElements().iterator();
		Iterator itRef, itComp;
		Lien lien;
		FT_Feature ref, comp;

		while (itLiens.hasNext()) {
			lien = (Lien)itLiens.next();
			itRef = lien.getObjetsRef().iterator();
			GM_Aggregate geom = new GM_Aggregate();
			while (itRef.hasNext()) {
				ref = (FT_Feature) itRef.next();
				itComp = lien.getObjetsComp().iterator();
				while (itComp.hasNext()) {
					comp = (FT_Feature) itComp.next();
					GM_LineString ligne = new GM_LineString();
					ligne.addControlPoint(((GM_Point)ref.getGeom().centroid()).getPosition());								
					ligne.addControlPoint(((GM_Point)comp.getGeom().centroid()).getPosition());
					geom.add(ligne);
				}
			}
			lien.setGeom(geom);								
		}
	}


	/** Affecte une géométrie à l'ensemble des liens, cette géométrie
	 * relie le milieu d'une ligne au milieu d'une ligne correspondant
	 * des objets 
	 */
	public void creeGeometrieDesLiensEntreLignesEtLignes() {
		Iterator itLiens = this.getElements().iterator();
		Iterator itRef, itComp;
		Lien lien;
		FT_Feature ref, comp;
		GM_LineString lineStringLien;
		GM_Aggregate geom;
		DirectPosition dpRef;

		while (itLiens.hasNext()) {
			lien = (Lien)itLiens.next();
			geom = new GM_Aggregate();
			itRef = lien.getObjetsRef().iterator();
			while (itRef.hasNext()) {
				ref = (FT_Feature) itRef.next();
				dpRef = Operateurs.milieu((GM_LineString)ref.getGeom());
				itComp = lien.getObjetsComp().iterator();
				while (itComp.hasNext()) {
					comp = (FT_Feature) itComp.next();
					lineStringLien = new GM_LineString();
					lineStringLien.addControlPoint(dpRef);
					lineStringLien.addControlPoint(Operateurs.milieu((GM_LineString)comp.getGeom()));
					geom.add(lineStringLien);
				}
			}
			lien.setGeom(geom);								
		}
	}

	/** Affecte une géométrie à l'ensemble des liens, cette géométrie
	 * relie le centroide d'une surface au milieu du segment correspondant
	 * des objets 
	 * @param comparaison : true si les objets de la BD de comparaison sont
	 * des lignes; false s'il s'agit des objets de la BD de référence
	 */
	public void creeGeometrieDesLiensEntreSurfacesEtLignes(boolean comparaison) {
		Iterator itLiens = this.getElements().iterator();
		Iterator itRef, itComp;
		Lien lien;
		FT_Feature ref, comp;

		while (itLiens.hasNext()) {
			lien = (Lien)itLiens.next();
			itRef = lien.getObjetsRef().iterator();
			GM_Aggregate geom = new GM_Aggregate();
			while (itRef.hasNext()) {
				ref = (FT_Feature) itRef.next();
				itComp = lien.getObjetsComp().iterator();
				while (itComp.hasNext()) {
					comp = (FT_Feature) itComp.next();
					GM_LineString ligne = new GM_LineString();
					if (comparaison){
						GM_Point centroideSurface = (GM_Point)ref.getGeom().centroid();
						double pointCentral = Math.floor(((GM_LineString)comp.getGeom()).numPoints()/2)-1;
						GM_Point pointMilieu = new GM_Point(((GM_LineString)comp.getGeom()).getControlPoint((int)pointCentral));
						ligne.addControlPoint(centroideSurface.getPosition());								
						ligne.addControlPoint(pointMilieu.getPosition());
						geom.add(ligne);
					}
					else {
						GM_Point centroideSurface = (GM_Point)comp.getGeom().centroid();
						double pointCentral = Math.floor(((GM_LineString)ref.getGeom()).numPoints()/2)-1;
						GM_Point pointMilieu = new GM_Point(((GM_LineString)ref.getGeom()).getControlPoint((int)pointCentral));
						ligne.addControlPoint(centroideSurface.getPosition());								
						ligne.addControlPoint(pointMilieu.getPosition());
						geom.add(ligne);
					}

				}
			}
			lien.setGeom(geom);								
		}
	}


	/** Affecte une géométrie à l'ensemble des liens, cette géométrie
	 * relie le centroïde d'une surface à un point
	 * @param comparaison : true si les objets de la BD de comparaison sont
	 * des points; false s'il s'agit des objets de la BD de référence
	 */
	public void creeGeometrieDesLiensEntreSurfacesEtPoints(boolean comparaison) {
		Iterator itLiens = this.getElements().iterator();
		Iterator itRef, itComp;
		Lien lien;
		FT_Feature ref, comp;

		while (itLiens.hasNext()) {
			lien = (Lien)itLiens.next();
			itRef = lien.getObjetsRef().iterator();
			GM_Aggregate geom = new GM_Aggregate();
			while (itRef.hasNext()) {
				ref = (FT_Feature) itRef.next();
				itComp = lien.getObjetsComp().iterator();
				while (itComp.hasNext()) {
					comp = (FT_Feature) itComp.next();
					GM_LineString ligne = new GM_LineString();
					if (comparaison){
						GM_Point centroideSurface = (GM_Point)ref.getGeom().centroid();
						ligne.addControlPoint(centroideSurface.getPosition());								
						ligne.addControlPoint(((GM_Point)comp.getGeom()).getPosition());
						geom.add(ligne);
					}
					else {
						GM_Point centroideSurface = (GM_Point)comp.getGeom().centroid();
						ligne.addControlPoint(centroideSurface.getPosition());								
						ligne.addControlPoint(((GM_Point)ref.getGeom()).getPosition());
						geom.add(ligne);
					}

				}
			}
			lien.setGeom(geom);								
		}
	}


	/** Détruit la géométrie des liens
	 */
	public void detruitGeometrieDesLiens() {
		Iterator itLiens = this.getElements().iterator();
		Lien lien;

		while (itLiens.hasNext()) {
			lien = (Lien)itLiens.next();
			GM_Aggregate geom = new GM_Aggregate();
			lien.setGeom(geom);								
		}
	}


	//////////////////////////////////////////////////////////////
	// AUTOUR DE LA VISUALISATION OBJETS APPARIES / NON APPARIES   
	//////////////////////////////////////////////////////////////

	/** Méthode qui renvoie à partir d'un ensemble de liens une liste de dimension 4,
	 * avec 
	 * en 1. la population issue de la population de référence qui a été appariée,
	 * en 2. la population issue de la population de comparaison qui a été appariée, 
	 * en 3. la population issue de la population de référence qui n'a pas été appariée,
	 * en 4. la population issue de la population de comparaison qui n'a pas été appariée.
	 * @param ensemble: ensemble de liens issu d'un appariement
	 * @param popRef
	 * @param popComp
	 * @return liste des populations appariées et non appariées
	 */
	public static List<Population> objetsApparies(EnsembleDeLiens ensemble, FT_FeatureCollection popRef, FT_FeatureCollection popComp){
		List<Population> listPopulation = new ArrayList<Population>();
		Population popCompAppariee = new Population(), popCompNonAppariee = new Population(),
				popRefAppariee = new Population(), popRefNonAppariee = new Population();

		Lien lien;
		FT_Feature elementPopComp, elementPopRef;

		List liens = ensemble.getElements();
		Iterator itLiens = liens.iterator();
		//ajout des éléments appariés dans les populations concernées
		while (itLiens.hasNext()){
			lien = (Lien)itLiens.next();
			List elementsComp = lien.getObjetsComp();
			List elementsRef = lien.getObjetsRef();

			Iterator itComp = elementsComp.iterator();
			Iterator itRef = elementsRef.iterator();

			while (itComp.hasNext()){
				elementPopComp = (FT_Feature)itComp.next();
				popCompAppariee.add(elementPopComp);
			}

			while (itRef.hasNext()){
				elementPopRef = (FT_Feature)itRef.next();
				popRefAppariee.add(elementPopRef);
			}
		}

		//copie des populations comp et ref dans les populations non appariées
		//popCompNonAppariee.copiePopulation(popComp);
		//popRefNonAppariee.copiePopulation(popRef);
		popCompNonAppariee.setElements(popComp.getElements());
		popRefNonAppariee.setElements(popRef.getElements());

		Iterator itPopCompApp = popCompAppariee.getElements().iterator();
		Iterator itPopRefApp = popRefAppariee.getElements().iterator();

		//élimination dans les populations non appariées des éléments appariés contenus
		//dans les populations appariés afin d'obtenir les complémentaires
		while (itPopCompApp.hasNext()){
			FT_Feature elementAOter = (FT_Feature)itPopCompApp.next();
			popCompNonAppariee.remove(elementAOter);
		}

		while (itPopRefApp.hasNext()){
			FT_Feature elementAOter = (FT_Feature)itPopRefApp.next();
			popRefNonAppariee.remove(elementAOter);
		}

		listPopulation.add(popRefAppariee);
		listPopulation.add(popCompAppariee);
		listPopulation.add(popRefNonAppariee);
		listPopulation.add(popCompNonAppariee);

		return listPopulation;
	}

	/**  Methode utile principalement pour analyser les résultats d'un appariement, 
	 * qui découpe un réseau en plusieurs réseaux selon les valeurs de l'attribut 
	 * "resultatAppariement" des arcs et noeuds du réseau apparié.  
	 */
	public List scindeSelonValeursCommentaires(List valeursClassement) {
		List liensClasses = new ArrayList();
		int i;

		for(i=0;i<valeursClassement.size();i++) {
			liensClasses.add(new EnsembleDeLiens());
		}
		Iterator itLiens = this.getElements().iterator();
		while (itLiens.hasNext()) {
			Lien lien= (Lien) itLiens.next();
			for(i=0;i<valeursClassement.size();i++) {
				if ( lien.getCommentaire() == null ) {
					continue;
				}
				if (lien.getCommentaire().startsWith((String)valeursClassement.get(i))) {
					((EnsembleDeLiens)liensClasses.get(i)).add(lien);
				}
			}
		}
		return liensClasses;
	}

}    