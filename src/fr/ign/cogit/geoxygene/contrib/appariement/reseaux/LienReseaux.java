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

package fr.ign.cogit.geoxygene.contrib.appariement.reseaux;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ArcApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.GroupeApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.NoeudApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * Resultats de l'appariement, qui sont des liens entre objets de BDref et objets de BDcomp.
 * Un lien a aussi une géométrie qui est sa représentation graphique.
 * 
 * @author Mustiere - IGN / Laboratoire COGIT
 * @version 1.0
 * 
 */

public class LienReseaux extends Lien {


	/** Nom de l'appariement qui a créé le lien */
	private String nom;
	@Override
	public String getNom() {return nom;}
	@Override
	public void setNom(String nom){ this.nom = nom; }

	/** Les Arc1 pointés par le lien */
	private List<Arc> arcs1 = new ArrayList<Arc>();
	public List<Arc> getArcs1() {return arcs1;}
	public void setArcs1(List<Arc> arcs) { arcs1=arcs; }
	public void addArcs1(Arc arc) { arcs1.add(arc); }

	/** Les Noeud1 pointés par le lien */
	private List<Noeud> noeuds1 = new ArrayList<Noeud>();
	public List<Noeud> getNoeuds1() {return noeuds1;}
	public void setNoeuds1(List<Noeud> noeuds) { noeuds1=noeuds; }
	public void addNoeuds1(Noeud noeud) { noeuds1.add(noeud); }

	/** Les Groupe1 pointés par le lien */
	private List<Groupe> groupes1 = new ArrayList<Groupe>();
	public List<Groupe> getGroupes1() {return groupes1;}
	public void setGroupes1(List<Groupe> groupes) { groupes1=groupes; }
	public void addGroupes1(Groupe groupe) { groupes1.add(groupe); }

	/** Les Arc2 pointés par le lien */
	private List<Arc> arcs2 = new ArrayList<Arc>();
	public List<Arc> getArcs2() {return arcs2;}
	public void setArcs2(List<Arc> arcs) { arcs2=arcs; }
	public void addArcs2(Arc arc) { arcs2.add(arc); }

	/** Les Noeud2 pointés par le lien */
	private List<Noeud> noeuds2 = new ArrayList<Noeud>();
	public List<Noeud> getNoeuds2() {return noeuds2;}
	public void setNoeuds2(List<Noeud> noeuds) { noeuds2=noeuds; }
	public void addNoeuds2(Noeud noeud) { noeuds2.add(noeud); }

	/** Les Groupe2 pointés par le lien */
	private List<Groupe> groupes2 = new ArrayList<Groupe>();
	public List<Groupe> getGroupes2() {return groupes2;}
	public void setGroupes2(List<Groupe> groupes) { groupes2=groupes; }
	public void addGroupes2(Groupe groupe) { groupes2.add(groupe); }


	/** Methode qui affecte la valeur 'eval' comme évaluation du lien et
	 * le commentaire 'commentaire' à tous les objets liés par ce lien.
	 */
	public void affecteEvaluationAuxObjetsLies(double eval, String commentaire) {
		this.setEvaluation(eval);
		Iterator<?> itObj;

		itObj = this.getArcs2().iterator();
		while (itObj.hasNext()) {
			ArcApp arc = (ArcApp) itObj.next();
			arc.setResultatAppariement(commentaire);
		}
		itObj = this.getArcs1().iterator();
		while (itObj.hasNext()) {
			ArcApp arc = (ArcApp) itObj.next();
			arc.setResultatAppariement(commentaire);
		}
		itObj = this.getNoeuds2().iterator();
		while (itObj.hasNext()) {
			NoeudApp noeud = (NoeudApp) itObj.next();
			noeud.setResultatAppariement(commentaire);
		}
		itObj = this.getNoeuds1().iterator();
		while (itObj.hasNext()) {
			NoeudApp noeud = (NoeudApp) itObj.next();
			noeud.setResultatAppariement(commentaire);
		}
		itObj = this.getGroupes2().iterator();
		while (itObj.hasNext()) {
			GroupeApp groupe = (GroupeApp) itObj.next();
			groupe.setResultatAppariement(commentaire);
		}
		itObj = this.getGroupes1().iterator();
		while (itObj.hasNext()) {
			GroupeApp groupe = (GroupeApp) itObj.next();
			groupe.setResultatAppariement(commentaire);
		}
	}


	/** Méthode qui renvoie en sortie des liens génériques (appariement.Lien, liens 1-1 uniquement)
	 * correspondant aux lienReseaux en entrée.
	 * Cette méthode crée une géoémtrie aux liens au passage
	 * @param liensReseaux
	 * @param ctRef
	 * @param param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static EnsembleDeLiens exportLiensAppariement(EnsembleDeLiens liensReseaux, ReseauApp ctRef, ParametresApp param) {
		Lien lienG;
		Iterator itObjets2,itObjets1;
		EnsembleDeLiens liensGeneriques;

		liensGeneriques = new EnsembleDeLiens();
		liensGeneriques.setNom(liensReseaux.getNom());

		// On compile toutes les populations du reseau 1 [resp. 2] dans une liste
		List pops1 = new ArrayList(param.populationsArcs1);
		pops1.addAll(param.populationsNoeuds1);
		List pops2 = new ArrayList(param.populationsArcs2);
		pops2.addAll(param.populationsNoeuds2);

		//boucle sur les liens entre cartes topo
		Iterator itLiensReseaux = liensReseaux.iterator();
		while (itLiensReseaux.hasNext()) {
			LienReseaux lienReseau = (LienReseaux) itLiensReseaux.next();
			// on récupère tous les objets des carte topo concernés
			Set objetsCT1PourUnLien = new HashSet(lienReseau.getArcs1());
			objetsCT1PourUnLien.addAll(lienReseau.getNoeuds1());
			Iterator itGroupes1 = lienReseau.getGroupes1().iterator();
			while (itGroupes1.hasNext()) {
				GroupeApp groupe1 = (GroupeApp) itGroupes1.next();
				objetsCT1PourUnLien.addAll(groupe1.getListeArcs());
				objetsCT1PourUnLien.addAll(groupe1.getListeNoeuds());
			}
			Set objetsCT2PourUnLien = new HashSet(lienReseau.getArcs2());
			objetsCT2PourUnLien.addAll(lienReseau.getNoeuds2());
			Iterator itGroupes2 = lienReseau.getGroupes2().iterator();
			while (itGroupes2.hasNext()) {
				GroupeApp groupe2 = (GroupeApp) itGroupes2.next();
				objetsCT2PourUnLien.addAll(groupe2.getListeArcs());
				objetsCT2PourUnLien.addAll(groupe2.getListeNoeuds());
			}

			// On parcours chaque couple d'objets de cartes topos appariés
			Iterator itObjetsCT1PourUnLien = objetsCT1PourUnLien.iterator();
			while (itObjetsCT1PourUnLien.hasNext()) {
				FT_Feature objetCT1 = (FT_Feature) itObjetsCT1PourUnLien.next();
				Iterator itObjetsCT2PourUnLien = objetsCT2PourUnLien.iterator();
				List objets1 = getCorrespondants(objetCT1, pops1);
				while (itObjetsCT2PourUnLien.hasNext()) {
					FT_Feature objetCT2 = (FT_Feature) itObjetsCT2PourUnLien.next();
					List objets2 = getCorrespondants(objetCT2, pops2);
					if (objets1.size() == 0 && objets2.size()==0 ) {
						//cas où il n'y a pas de correspondant dans les données de départ des 2 cotés
						lienG = liensGeneriques.nouvelElement();
						lienG.setEvaluation(lienReseau.getEvaluation());
						lienG.setCommentaire("Pas de correspondant géo dans les deux BDs");
						if (param.exportGeometrieLiens2vers1) lienG.setGeom(creeGeometrieLienSimple(objetCT1, objetCT2));
						else lienG.setGeom(creeGeometrieLienSimple(objetCT2, objetCT1));
						continue;
					}
					if (objets1.size() == 0 ) {
						//cas où il n'y a pas de correspondant dans les données de BD1
						itObjets2 = objets2.iterator();
						while (itObjets2.hasNext()) {
							FT_Feature objet2 = (FT_Feature) itObjets2.next();
							lienG = liensGeneriques.nouvelElement();
							lienG.setEvaluation(lienReseau.getEvaluation());
							lienG.setCommentaire("Pas de correspondant géo dans BD1");
							if (param.exportGeometrieLiens2vers1) lienG.setGeom(creeGeometrieLienSimple(objetCT1, objet2));
							else lienG.setGeom(creeGeometrieLienSimple(objet2, objetCT1));
							lienG.addObjetComp(objet2);
						}
						continue;
					}
					if (objets2.size() == 0 ) {
						//cas où il n'y a pas de correspondant dans les données de BD2
						itObjets1 = objets1.iterator();
						while (itObjets1.hasNext()) {
							FT_Feature objet1 = (FT_Feature) itObjets1.next();
							lienG = liensGeneriques.nouvelElement();
							lienG.setEvaluation(lienReseau.getEvaluation());
							lienG.setCommentaire("Pas de correspondant géo dans BD1");
							if (param.exportGeometrieLiens2vers1) lienG.setGeom(creeGeometrieLienSimple(objet1, objetCT2));
							else lienG.setGeom(creeGeometrieLienSimple(objetCT2, objet1));
							lienG.addObjetRef(objet1);
						}
						continue;
					}
					//cas où il y a des correspondants dans les deux BD
					itObjets1 = objets1.iterator();
					while (itObjets1.hasNext()) {
						FT_Feature objet1 = (FT_Feature) itObjets1.next();
						itObjets2 = objets2.iterator();
						while (itObjets2.hasNext()) {
							FT_Feature objet2 = (FT_Feature) itObjets2.next();
							lienG = liensGeneriques.nouvelElement();
							lienG.setEvaluation(lienReseau.getEvaluation());
							lienG.setCommentaire("");
							if (param.exportGeometrieLiens2vers1)lienG.setGeom(creeGeometrieLienSimple(objet1, objet2));
							else lienG.setGeom(creeGeometrieLienSimple(objet2, objet1));
							lienG.addObjetRef(objet1);
							lienG.addObjetComp(objet2);
						}
					}
				}
			}
		}
		if ( param.debugAffichageCommentaires > 1 ) System.out.println("  "+liensGeneriques.size()+" liens 1-1 ont été exportés");
		System.out.println("liens réseaux"+liensReseaux.size());

		return liensGeneriques;
	}


	/** Renvoie les correspondants appartenant à une des FT_FeatureCollection de la liste passée en parametre. */
	private static List<FT_Feature> getCorrespondants(FT_Feature ft, List<FT_FeatureCollection<FT_Feature>> populations) {
		List<FT_Feature> resultats = new ArrayList<FT_Feature>();
		Iterator<FT_FeatureCollection<FT_Feature>> itPop = populations.iterator();
		while (itPop.hasNext()) {
			FT_FeatureCollection<FT_Feature> pop = itPop.next();
			resultats.addAll(ft.getCorrespondants(pop));
		}
		return resultats;
	}

	/** Methode créant une géométrie au lien 1-1 en reliant les
	 * deux objets concerné par un simple trait
	 */
	private static GM_Object creeGeometrieLienSimple(FT_Feature obj1, FT_Feature obj2) {
		GM_LineString ligne = new GM_LineString();
		DirectPosition DP2 = null;
		if ( obj2.getGeom() instanceof GM_Point) {
			GM_Point point2 = (GM_Point)obj2.getGeom()  ;
			DP2 = point2.getPosition();
			ligne.addControlPoint(DP2);
		}
		if ( obj2.getGeom() instanceof GM_LineString) {
			GM_LineString ligne2 = (GM_LineString)obj2.getGeom()  ;
			DP2 = Operateurs.milieu(ligne2);
			ligne.addControlPoint(DP2);
		}

		if ( obj1.getGeom() instanceof GM_Point) {
			GM_Point point1 = (GM_Point)obj1.getGeom()  ;
			ligne.addControlPoint(point1.getPosition());
		}
		if ( obj1.getGeom() instanceof GM_LineString) {
			GM_LineString ligne1 = (GM_LineString)obj1.getGeom()  ;
			ligne.addControlPoint(Operateurs.projection(DP2,ligne1));
		}
		return ligne;
	}

	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
	//                         ATTENTION
	//
	// LES CODES CI-DESSOUS PERMETTENT DE CREER DES GEOMETRIES
	// COMPLEXES QUI...
	// 1/ SONT UTILES POUR AVOIR UNE REPRESENTATION FINE
	// 2/ MAIS NE SONT PAS TRES BLINDEES (code en cours d'affinage)
	//
	// A UTILSER AVEC PRECAUTION DONC
	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////

	/** Méthode qui affecte une geometrie aux liens de réseau et remplit les commentaires des liens.
	 * UTILE POUR CODE / DEBUG UNIQUEMENT
	 */
	public static void exportAppCarteTopo(EnsembleDeLiens liensReseaux,ParametresApp param) {
//		EnsembleDeLiens liensGeneriques ;
//		LienReseaux lienR;
//		Lien lienG;
//		List<FT_Feature> tousobjetsRef,tousobjetsComp ;
//		Iterator<Groupe> itGroupe;
//		Groupe groupe;
		LienReseaux lienR;
		Iterator<Lien> itLiens;

		/////////////////////////////////////////
		// Création de la géométrie des liens
		itLiens = liensReseaux.getElements().iterator();
		while (itLiens.hasNext()) {
			lienR = (LienReseaux) itLiens.next();
			lienR.setGeom(lienR.creeGeometrieLien(param.debugTirets, param.debugPasTirets, param.debugBuffer, param.debugTailleBuffer));
		}

//		if ( param.debugAffichageCommentaires > 1 ) System.out.println("BILAN de l'appariement sur le réseau 1");
//		if ( param.debugAffichageCommentaires > 1 ) System.out.println("NB : bilan sur les objets des réseaux créés, et non sur les objets initiaux, il peut y avoir quelques nuances");
//
//		liensGeneriques = new EnsembleDeLiens();
//		liensGeneriques.setNom(liensReseaux.getNom());
//
//		itLiens = liensReseaux.getElements().iterator();
//		while (itLiens.hasNext()) {
//			lienR = (LienReseaux) itLiens.next();
//			lienG = (Lien)liensGeneriques.nouvelElement();
//			lienG.setEvaluation(lienR.getEvaluation());
//
//			/////////////////////////////////////////
//			// Récupération des objets pointés par le lien
//
//			// Liens vers les objets de référence
//			tousobjetsRef = new ArrayList<FT_Feature>();
//			tousobjetsRef.addAll(lienR.getArcs1());
//			tousobjetsRef.addAll(lienR.getNoeuds1());
//			itGroupe = lienR.getGroupes1().iterator();
//			while (itGroupe.hasNext()) {
//				groupe = itGroupe.next();
//				tousobjetsRef.addAll(groupe.getListeArcs());
//				tousobjetsRef.addAll(groupe.getListeNoeuds());
//			}
//			lienG.setObjetsRef(tousobjetsRef);
//
//			// Liens vers les objets de comparaison
//			tousobjetsComp = new ArrayList<FT_Feature>();
//			tousobjetsComp.addAll(lienR.getArcs2());
//			tousobjetsComp.addAll(lienR.getNoeuds2());
//			itGroupe = lienR.getGroupes2().iterator();
//			while (itGroupe.hasNext()) {
//				groupe = itGroupe.next();
//				tousobjetsComp.addAll(groupe.getListeArcs());
//				tousobjetsComp.addAll(groupe.getListeNoeuds());
//			}
//			lienG.setObjetsComp(tousobjetsComp);
//
//			/////////////////////////////////////////
//			// Détermination du type du lien
//			if (lienR.getNoeuds1().size() != 0 ) {
//				lienG.setType("Lien de noeud ref");
//				//				liensGeneriques.enleveElement(lienG);
//				//				continue;
//			}
//			else if (lienR.getArcs1().size() != 0 ) lienG.setType("Lien d'arc ref");
//
//			/////////////////////////////////////////
//			// Remplissage des chaines de caractères pour l'export
//			Iterator<FT_Feature> itRef = lienG.getObjetsRef().iterator();
//			String txt = new String("COGITID:");
//			while (itRef.hasNext()) {
//				FT_Feature objet = itRef.next();
//				Iterator<FT_Feature> itObjGeo = objet.getCorrespondants().iterator();
//				while (itObjGeo.hasNext()) {
//					FT_Feature objetGeo = itObjGeo.next();
//					txt = txt.concat(objetGeo.getId()+" ");
//				}
//			}
//			lienG.setReference(txt);
//
//			txt = new String("COGITID:");
//			Iterator<FT_Feature> itComp= lienG.getObjetsComp().iterator();
//			while (itComp.hasNext()) {
//				FT_Feature objet = itComp.next();
//				Iterator<FT_Feature> itObjGeo = objet.getCorrespondants().iterator();
//				while (itObjGeo.hasNext()) {
//					FT_Feature objetGeo = itObjGeo.next();
//					txt = txt.concat(objetGeo.getId()+" ");
//				}
//			}
//			lienG.setComparaison(txt);
//
//			/////////////////////////////////////////
//			// Création de la géométrie des liens
//			lienG.setGeom(lienR.creeGeometrieLien(param.debugTirets, param.debugPasTirets, param.debugBuffer, param.debugTailleBuffer));
//		}
//		return liensGeneriques;
	}

	/** Methode créant une géométrie pour les liens de réseau.
	 * 
	 * 1/ Pour chaque noeud du réseau 1 apparié, cette géoémtrie est constituée...
	 *      - d'un buffer entourant les objets homolgues dans le réseau ,
	 *      - d'un trait reliant le noeud à ce buffer.
	 * 
	 * 2/ Pour chaque arc du réseau 1 apparié, cette géoémtrie est constituée...
	 *      - d'un ensemble de tirets reliant les arcs homologues de manière régulière
	 *        (intervalle entre les tirets en paramètre),
	 *      - ou alors d'un ensemble de traits reliant le milieu des arcs appariés.
	 * 
	 *  @param tirets
	 *  Spécifie si on veut une géométrie faite de tirets (true),
	 *  ou plutôt d'un unique trait pour chaque couple d'arcs (false)
	 * 
	 *  @param pasTirets
	 *  Si on veut des tirets réguliers, distance entre ces tirets.
	 * 
	 *  @param tailleBuffer
	 *  Taille du buffer autour des objets appariés à un noeud.
	 * 
	 */
	private GM_Object creeGeometrieLien(boolean tirets, double pasTirets, boolean buffer, double tailleBuffer) {

		Iterator<Groupe> itGroupes;
		Iterator<Noeud> itNoeuds;
		Iterator<Noeud> itNoeudsComp;
		Iterator<Arc> itArcs ;
		Iterator<Arc> itArcsComp ;
		NoeudApp noeudComp, noeudRef;
		ArcApp arcComp, arcRef;
		GroupeApp groupeComp;
		GM_LineString ligne, chemin;
		GM_Aggregate<GM_Object> geomLien;

		// LIEN D'UN NOEUD REF VERS DES NOEUDS COMP ET/OU DES GROUPES COMP
		if ( this.getNoeuds1().size() == 1 ) {
			noeudRef = (NoeudApp)this.getNoeuds1().get(0);
			geomLien = new GM_Aggregate<GM_Object>();

			// 1 noeud ref - n noeuds comp isolés --> 1 aggrégat de traits et de surfaces
			itNoeuds = this.getNoeuds2().iterator();
			while ( itNoeuds.hasNext() ) {
				noeudComp = (NoeudApp)itNoeuds.next();
				ligne = new GM_LineString();
				ligne.addControlPoint(noeudRef.getCoord());
				ligne.addControlPoint(noeudComp.getCoord());
				geomLien.add(ligne);
				if (buffer) geomLien.add(noeudComp.getGeometrie().buffer(tailleBuffer));
			}

			// 1 noeud ref - n groupes --> aggrégat de traits et de surface autour du groupe
			itGroupes = this.getGroupes2().iterator();
			while ( itGroupes.hasNext() ) {
				groupeComp = (GroupeApp)itGroupes.next();
				itArcsComp = groupeComp.getListeArcs().iterator();
				while ( itArcsComp.hasNext() ) {
					arcComp = (ArcApp)itArcsComp.next();
					geomLien.add( arcComp.getGeometrie().buffer(tailleBuffer) );
				}
				itNoeudsComp = groupeComp.getListeNoeuds().iterator();
				while ( itNoeudsComp.hasNext() ) {
					noeudComp = (NoeudApp)itNoeudsComp.next();
					geomLien.add( noeudComp.getGeometrie().buffer(tailleBuffer) );
				}

				// on fait le trait entre le noeud ref et le groupe comp
				ligne = new GM_LineString();
				ligne.addControlPoint( noeudRef.getCoord() );
				ligne.addControlPoint( noeudRef.noeudLePlusProche(groupeComp).getCoord() );
				geomLien.add(ligne);
			}

			if (geomLien.coord().size() > 1 ) return geomLien;
			System.out.println("Lien pour un noeud non créé : pas assez de coordonnées");
			return null;
		}

		// LIEN D'ARCS REF VERS DES ARCS OU DES GROUPES COMP
		Iterator<Arc> itArcsRef = this.getArcs1().iterator();
		geomLien = new GM_Aggregate<GM_Object>();
		while (itArcsRef.hasNext()) {
			arcRef = (ArcApp)itArcsRef.next();

			// 1 arc ref directement vers des noeuds
			itNoeuds = this.getNoeuds2().iterator();
			while (itNoeuds.hasNext()) {
				noeudComp = (NoeudApp) itNoeuds.next();
				if ( tirets) geomLien.add(Lien.tirets(arcRef.getGeometrie(), noeudComp.getGeometrie(), pasTirets));
				else geomLien.add(Lien.tiret(arcRef.getGeometrie(), noeudComp.getGeometrie()));
			}

			// 1 arc ref vers des groupes comp (groupes en parrallèle) --> plusieurs séries de tirets
			itGroupes = this.getGroupes2().iterator();
			while ( itGroupes.hasNext() ) {
				// 1 arc ref vers un groupe comp (des arcs en série) --> des tirets
				groupeComp = (GroupeApp)itGroupes.next();
				chemin = groupeComp.compileArcs(arcRef);
				if ( chemin != null ) {
					if ( tirets) geomLien.add(Lien.tirets(arcRef.getGeometrie(), chemin, pasTirets));
					else geomLien.add(Lien.tiret(arcRef.getGeometrie(), chemin));
				}
			}

			// 1 arc ref vers des arcs comp en série --> des tirets (utile pour le pre-appariement uniquement)
			itArcs = this.getArcs2().iterator();
			while ( itArcs.hasNext() ) {
				arcComp = (ArcApp)itArcs.next();
				if (tirets) geomLien.add(Lien.tirets(arcRef.getGeometrie(), arcComp.getGeometrie(), 25));
				else geomLien.add(Lien.tiret(arcRef.getGeometrie(), arcComp.getGeometrie()));
			}

		}
		if (geomLien.coord().size() > 1 ) return geomLien;
		System.out.println("Lien pour un arc non créé : pas assez de coordonnées");
		return null;
	}

}