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
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Surface;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * Resultat de l'appariement : lien entre des objets homologues de deux bases de données.
 * Un lien a aussi une géométrie qui est sa représentation graphique.
 * 
 * @author Mustiere / IGN Laboratoire COGIT
 * @version 1.0
 */

public class Lien extends FT_Feature {

	/** Les objets d'une BD pointés par le lien */
	protected List<FT_Feature> objetsRef = new ArrayList<FT_Feature>();
	public List<FT_Feature> getObjetsRef() {return this.objetsRef;}
	public void setObjetsRef(List<FT_Feature> liste) { this.objetsRef=liste; }
	public void addObjetRef(FT_Feature objet) { this.objetsRef.add(objet); }
	public void addObjetsRef(List<FT_Feature> objets) {this.objetsRef.addAll(objets);}

	/** Les objets de l'autre BD pointés par le lien */
	protected List<FT_Feature> objetsComp = new ArrayList<FT_Feature>();
	public List<FT_Feature> getObjetsComp() {return this.objetsComp;}
	public void setObjetsComp(List<FT_Feature> liste) { this.objetsComp=liste; }
	public void addObjetComp(FT_Feature objet) { this.objetsComp.add(objet); }
	public void addObjetsComp(List<FT_Feature> objets) {this.objetsComp.addAll(objets);}

	/** Estimation de la qualité du lien d'appariement.
	 *  Entre 0 et 1 en général */
	private double evaluation ;
	public double getEvaluation() {return this.evaluation;}
	public void setEvaluation(double evaluation) {this.evaluation = evaluation;}

	/** Liste d'indicateurs utilisés pendant les calculs d'appariement */
	protected List<Object> indicateurs = new ArrayList<Object>();
	public List<Object> getIndicateurs() {return this.indicateurs;}
	public void setIndicateurs(List<Object> liste) { this.indicateurs=liste; }
	public void addIndicateur(Object objet) { this.indicateurs.add(objet); }

	/** Texte libre pour décrire le lien d'appariement */
	protected String commentaire = new String();
	public String getCommentaire() {return this.commentaire;}
	public void setCommentaire(String commentaire) { this.commentaire=commentaire; }

	/** Texte libre pour décrire le nom du procesus d'appariement. */
	protected String nom = new String();
	public String getNom() {return this.nom;}
	public void setNom(String nom) { this.nom=nom; }

	/** Texte libre pour décrire le type d'appariement (ex. "Noeud-Noeud").*/
	protected String type = new String();
	public String getType() {return this.type;}
	public void setType(String type) { this.type=type; }

	/** Texte libre pour décrire les objets de la BD1 pointés. */
	protected String reference = new String();
	public String getReference() {return this.reference;}
	public void setReference(String reference) { this.reference=reference; }

	/** Texte libre pour décrire les objets de la BD2 pointés.*/
	protected String comparaison = new String();
	public String getComparaison() {return this.comparaison;}
	public void setComparaison(String comparaison) { this.comparaison=comparaison; }

	//////////////////////////////////////////////////////
	// Methodes utiles à la manipulation des liens
	//////////////////////////////////////////////////////

	//////////////////////////////////////////////////////
	// POUR TOUS LES LIENS
	//////////////////////////////////////////////////////
	/** recopie les valeurs de lienACopier dans this
	 */
	public void copie(Lien lienACopier) {
		this.setObjetsComp(lienACopier.getObjetsComp());
		this.setObjetsRef(lienACopier.getObjetsRef());
		this.setEvaluation(lienACopier.getEvaluation());
		this.setGeom(lienACopier.getGeom());
		this.setIndicateurs(lienACopier.getIndicateurs());
		this.setCorrespondants(lienACopier.getCorrespondants());
		this.setCommentaire(lienACopier.getCommentaire());
		this.setNom(lienACopier.getNom());
		this.setType(lienACopier.getType());
		this.setReference(lienACopier.getReference());
		this.setComparaison(lienACopier.getComparaison());
	}

	///////////////////////////////////////////
	// Pour calcul de la géométrie des liens
	///////////////////////////////////////////

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

	/** définit des petits tirets entre 2 lignes pour représenter un lien d'appariement */
	public static GM_MultiCurve<GM_OrientableCurve> tirets(GM_LineString LS1, GM_LineString LS2, double pas) {
		double long1, long2;
		int nb_tirets;
		GM_MultiCurve<GM_OrientableCurve> tirets = new GM_MultiCurve<GM_OrientableCurve>();
		GM_LineString tiret;
		DirectPosition pt1, pt2;
		int i;

		long1 = LS1.length();
		long2 = LS2.length();
		nb_tirets = (int) (long1/pas);
		for(i=0;i<=nb_tirets ;i++) {
			tiret = new GM_LineString();
			pt1 = Operateurs.pointEnAbscisseCurviligne(LS1,i*pas);
			pt2 = Operateurs.pointEnAbscisseCurviligne(LS2,i*pas*long2/long1);
			if (pt1 == null || pt2 == null ) continue;
			tiret.addControlPoint(pt1);
			tiret.addControlPoint(pt2);
			tirets.add(tiret);
		}
		return tirets;
	}

	/** définit des petits tirets entre 1 ligne et un point pour représenter un lien d'appariement */
	public static GM_MultiCurve<GM_OrientableCurve> tirets(GM_LineString LS1, GM_Point PT, double pas) {
		double long1;
		int nb_tirets;
		GM_MultiCurve<GM_OrientableCurve> tirets = new GM_MultiCurve<GM_OrientableCurve>();
		GM_LineString tiret;
		DirectPosition pt1, pt2;
		int i;

		long1 = LS1.length();
		nb_tirets = (int) (long1/pas);
		for(i=0;i<=nb_tirets ;i++) {
			tiret = new GM_LineString();
			pt1 = Operateurs.pointEnAbscisseCurviligne(LS1,i*pas);
			pt2 = PT.getPosition();
			if (pt1 == null || pt2 == null ) continue;
			tiret.addControlPoint(pt1);
			tiret.addControlPoint(pt2);
			tirets.add(tiret);
		}
		return tirets;
	}

	/** définit la géométrie d'un lien entre 2 lignes par un trait reliant les milieux des lignes */
	public static GM_LineString tiret(GM_LineString LS1, GM_LineString LS2) {
		GM_LineString tiret = new GM_LineString();
		tiret.addControlPoint(Operateurs.milieu(LS1));
		tiret.addControlPoint(Operateurs.milieu(LS2));
		return tiret;
	}

	/** définit la géométrie d'un lien entre 1 ligne et un point par un trait
	 * reliant le milieu de la ligne au point */
	public static GM_LineString tiret(GM_LineString LS1, GM_Point PT) {
		GM_LineString tiret = new GM_LineString();
		tiret.addControlPoint(Operateurs.milieu(LS1));
		tiret.addControlPoint(PT.getPosition());
		return tiret;
	}

	/** définit des petits tirets entre 2 lignes pour représenter un lien d'appariement.
	 * NB: projete les points sur l'arc LS2, plutot que de se baser sur l'abscisse curviligne */
	public static GM_MultiCurve<GM_OrientableCurve> tiretsProjetes(GM_LineString LS1, GM_LineString LS2, double pas) {
		double long1;
		int nb_tirets;
		GM_MultiCurve<GM_OrientableCurve> tirets = new GM_MultiCurve<GM_OrientableCurve>();
		GM_LineString tiret;
		DirectPosition pt1, pt2;
		int i;

		long1 = LS1.length();
		nb_tirets = (int) (long1/pas);
		for(i=0;i<=nb_tirets ;i++) {
			tiret = new GM_LineString();
			pt1 = Operateurs.pointEnAbscisseCurviligne(LS1,i*pas);
			pt2 = Operateurs.projection(pt1,LS2);
			if (pt1 == null || pt2 == null ) continue;
			tiret.addControlPoint(pt1);
			tiret.addControlPoint(pt2);
			tirets.add(tiret);
		}
		return tirets;
	}

	/** définit des petits tirets entre 1 ligne et un aggregat pour représenter un lien d'appariement.
	 * NB: projete les points sur l'aggregat, plutot que de se baser sur l'abscisse curviligne */
	public static GM_MultiCurve<GM_OrientableCurve> tiretsProjetes(GM_LineString LS1, GM_Aggregate<GM_Object> aggregat, double pas) {
		double long1;
		int nb_tirets;
		GM_MultiCurve<GM_OrientableCurve> tirets = new GM_MultiCurve<GM_OrientableCurve>();
		GM_LineString tiret;
		DirectPosition pt1, pt2;
		int i;

		long1 = LS1.length();
		nb_tirets = (int) (long1/pas);
		for(i=0;i<=nb_tirets ;i++) {
			tiret = new GM_LineString();
			pt1 = Operateurs.pointEnAbscisseCurviligne(LS1,i*pas);
			pt2 = Operateurs.projection(pt1,aggregat);
			if (pt1 == null || pt2 == null ) continue;
			tiret.addControlPoint(pt1);
			tiret.addControlPoint(pt2);
			tirets.add(tiret);
		}
		return tirets;
	}

	/** définit la géométrie d'un lien entre 2 lignes par un trait reliant les lignes */
	public static GM_LineString tiretProjete(GM_LineString LS1, GM_LineString LS2) {
		DirectPosition milieu = Operateurs.milieu(LS1);
		DirectPosition projete = Operateurs.projection(milieu, LS2);
		GM_LineString tiret = new GM_LineString();
		tiret.addControlPoint(milieu);
		tiret.addControlPoint(projete);
		return tiret;
	}

	/** définit la géométrie d'un lien entre 2 lignes par un trait reliant la ligne à l'aggregat */
	public static GM_LineString tiretProjete(GM_LineString LS1, GM_Aggregate<GM_Object> aggegat) {
		DirectPosition milieu = Operateurs.milieu(LS1);
		DirectPosition projete = Operateurs.projection(milieu, aggegat);
		GM_LineString tiret = new GM_LineString();
		tiret.addControlPoint(milieu);
		tiret.addControlPoint(projete);
		return tiret;
	}

	/** définit la géométrie d'un lien entre 1 point et son projeté sur la ligne  */
	public static GM_LineString tiretProjete(GM_Point PT, GM_LineString LS2) {
		DirectPosition pt = PT.getPosition();
		DirectPosition projete = Operateurs.projection(pt, LS2);
		GM_LineString tiret = new GM_LineString();
		tiret.addControlPoint(pt);
		tiret.addControlPoint(projete);
		return tiret;
	}

	/** définit la géométrie d'un lien entre 1 point et son projeté sur l'aggregat  */
	public static GM_LineString tiretProjete(GM_Point PT, GM_Aggregate<GM_Object> aggregat) {
		DirectPosition pt = PT.getPosition();
		DirectPosition projete = Operateurs.projection(pt, aggregat);
		GM_LineString tiret = new GM_LineString();
		tiret.addControlPoint(pt);
		tiret.addControlPoint(projete);
		return tiret;
	}

	/** Affecte une géométrie au lien.
	 * Cette géométrie est principalement adaptée au cas de l'appariement de réseaux.
	 * Attention: peut laisser une geometrie nullle si on ne pointe vers rien (cas des noeuds souvent).
	 * 
	 * @param tirets
	 * true: crée des petits traits régulièrement espacés pour relier les arcs;
	 * false: ne crée pour chaque arc qu'un seul trait reliant le milieu de l'arc.
	 *
	 * @param pas
	 * L'écart entre les tirets, le cas échéant
	 *	 */
	public void setGeometrieReseaux(boolean tirets, double pas) {
		Iterator<FT_Feature> itObjRef, itObjComp;
		GM_Object geomRef = null, geomComp = null;
		boolean refPoint;
		GM_Aggregate<GM_Object> geomLien, groupe;
		GM_Object buffer;
		GM_Point centroide;
		GM_LineString ligne;
		GM_MultiCurve<GM_OrientableCurve> lignes ;

		geomLien = new GM_Aggregate<GM_Object>();
		itObjRef = this.getObjetsRef().iterator();
		while (itObjRef.hasNext()) {
			// determination du côté ref
			geomRef = (itObjRef.next()).getGeom();
			if (geomRef instanceof GM_Point) refPoint = true;
			else {
				if (geomRef instanceof GM_LineString) refPoint = false;
				else {
					System.out.println("géométrie réseau: Type de géométrie non géré "+geomRef.getClass()); //$NON-NLS-1$
					continue;
				}
			}

			// cas "1 noeud ref --> d'autres choses": 1 tiret + 1 buffer
			if ( refPoint ) {
				groupe = new GM_Aggregate<GM_Object>();
				itObjComp = this.getObjetsComp().iterator();
				while (itObjComp.hasNext()) {
					// determination du côté comp
					geomComp = (itObjComp.next()).getGeom();
					groupe.add(geomComp);
				}
				buffer = groupe.buffer(20);
				centroide = new GM_Point(buffer.centroid());
				ligne = new GM_LineString();
				ligne.addControlPoint(centroide.getPosition());
				ligne.addControlPoint(((GM_Point)geomRef).getPosition());
				geomLien.add(buffer);
				geomLien.add(ligne);
				continue;
			}

			// cas "1 arc ref --> d'autres choses": des tirets
			GM_Aggregate<GM_Object> aggr = new GM_Aggregate<GM_Object>();
			itObjComp = this.getObjetsComp().iterator();
			while (itObjComp.hasNext()) {
				// determination du côté comp
				geomComp = (itObjComp.next()).getGeom();
				aggr.add(geomComp);
			}
			if (tirets) {
				lignes = tiretsProjetes((GM_LineString)geomRef, aggr, pas);
				geomLien.add(lignes);
			}
			else {
				ligne = tiretProjete((GM_LineString)geomRef, aggr);
				geomLien.add(ligne);
			}
		}
		if (geomLien.size() != 0) this.setGeom(geomLien);
	}

	//////////////////////////////////////////////////////
	// POUR LES LIENS VERS DES SURFACES
	//////////////////////////////////////////////////////
	/** Distance surfacique entre les surfaces du lien ;
	 * Methode UNIQUEMENT valable pour des liens pointant vers 1 ou n
	 * objets ref et com avec une géométrie SURFACIQUE.
	 */
	public double distanceSurfaciqueRobuste() {
		GM_MultiSurface<GM_OrientableSurface> geomRef = new GM_MultiSurface<GM_OrientableSurface>();
		GM_MultiSurface<GM_OrientableSurface> geomComp = new GM_MultiSurface<GM_OrientableSurface>();
		FT_Feature obj;
		GM_OrientableSurface geometrie;
		Iterator<FT_Feature> it ;

		it = this.getObjetsRef().iterator();
		while ( it .hasNext() ) {
			obj = it.next();
			geometrie = (GM_OrientableSurface) obj.getGeom();
			if ( !(geometrie instanceof GM_Surface)) return 2;
			geomRef.add(geometrie);
		}
		it = this.getObjetsComp().iterator();
		while ( it .hasNext() ) {
			obj = it.next();
			geometrie = (GM_OrientableSurface) obj.getGeom();
			if ( !(geometrie instanceof GM_Surface)) return 2;
			geomComp.add(geometrie);
		}

		return Distances.distanceSurfaciqueRobuste(geomRef,geomComp);
	}

	/** Exactitude (définie par Atef) entre les surfaces du lien ;
	 * Methode UNIQUEMENT valable pour des liens pointant vers 1 ou n
	 * objets ref et com avec une géométrie SURFACIQUE.
	 */
	public double exactitude() {
		GM_MultiSurface<GM_OrientableSurface> geomRef = new GM_MultiSurface<GM_OrientableSurface>();
		GM_MultiSurface<GM_OrientableSurface> geomComp = new GM_MultiSurface<GM_OrientableSurface>();
		FT_Feature obj;
		GM_OrientableSurface geometrie;
		Iterator<FT_Feature> it ;

		it = this.getObjetsRef().iterator();
		while ( it .hasNext() ) {
			obj = it.next();
			geometrie = (GM_OrientableSurface) obj.getGeom();
			if ( !(geometrie instanceof GM_Surface)) return 2;
			geomRef.add(geometrie);
		}
		it = this.getObjetsComp().iterator();
		while ( it .hasNext() ) {
			obj = it.next();
			geometrie = (GM_OrientableSurface) obj.getGeom();
			if ( !(geometrie instanceof GM_Surface)) return 2;
			geomComp.add(geometrie);
		}

		return Distances.exactitude(geomRef,geomComp);
	}

	/** Exactitude (définie par Atef) entre les surfaces du lien ;
	 * Methode UNIQUEMENT valable pour des liens pointant vers 1 ou n
	 * objets ref et com avec une géométrie SURFACIQUE.
	 */
	public double completude() {
		GM_MultiSurface<GM_OrientableSurface> geomRef = new GM_MultiSurface<GM_OrientableSurface>();
		GM_MultiSurface<GM_OrientableSurface> geomComp = new GM_MultiSurface<GM_OrientableSurface>();
		FT_Feature obj;
		GM_OrientableSurface geometrie;
		Iterator<FT_Feature> it ;

		it = this.getObjetsRef().iterator();
		while ( it .hasNext() ) {
			obj = it.next();
			geometrie = (GM_OrientableSurface) obj.getGeom();
			if ( !(geometrie instanceof GM_Surface)) return 2;
			geomRef.add(geometrie);
		}
		it = this.getObjetsComp().iterator();
		while ( it .hasNext() ) {
			obj = it.next();
			geometrie = (GM_OrientableSurface) obj.getGeom();
			if ( !(geometrie instanceof GM_Surface)) return 2;
			geomComp.add(geometrie);
		}

		return Distances.exactitude(geomRef,geomComp);
	}

}
