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
	protected List objetsRef = new ArrayList();
	public List getObjetsRef() {return objetsRef;}
	public void setObjetsRef(List liste) { objetsRef=liste; }
	public void addObjetRef(FT_Feature objet) { objetsRef.add(objet); }
	public void addObjetsRef(List objets) {objetsRef.addAll(objets);}

	/** Les objets de l'autre BD pointés par le lien */
	protected List objetsComp = new ArrayList();
	public List getObjetsComp() {return objetsComp;}
	public void setObjetsComp(List liste) { objetsComp=liste; }
	public void addObjetComp(FT_Feature objet) { objetsComp.add(objet); }
	public void addObjetsComp(List objets) {objetsComp.addAll(objets);}

	/** Estimation de la qualité du lien d'appariement.
	 *  Entre 0 et 1 en général */
	private double evaluation ;
	public double getEvaluation() {return evaluation;}
	public void setEvaluation(double evaluation) {this.evaluation = evaluation;}

	/** Liste d'indicateurs utilisés pendant les calculs d'appariement */
	protected List indicateurs = new ArrayList();
	public List getIndicateurs() {return indicateurs;}
	public void setIndicateurs(List liste) { indicateurs=liste; }
	public void addIndicateur(Object objet) { indicateurs.add(objet); }

	/** Texte libre pour décrire le lien d'appariement */
	protected String commentaire = new String();
	public String getCommentaire() {return commentaire;}
	public void setCommentaire(String commentaire) { this.commentaire=commentaire; }

	/** Texte libre pour décrire le nom du procesus d'appariement. */
	protected String nom = new String();
	public String getNom() {return nom;}
	public void setNom(String nom) { this.nom=nom; }

	/** Texte libre pour décrire le type d'appariement (ex. "Noeud-Noeud").*/
	protected String type = new String();
	public String getType() {return type;}
	public void setType(String type) { this.type=type; }

	/** Texte libre pour décrire les objets de la BD1 pointés. */
	protected String reference = new String();
	public String getReference() {return reference;}
	public void setReference(String reference) { this.reference=reference; }

	/** Texte libre pour décrire les objets de la BD2 pointés.*/
	protected String comparaison = new String();
	public String getComparaison() {return comparaison;}
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

	/** Définit des petits tirets entre 2 lignes pour représenter un lien d'appariement */        
	public static GM_MultiCurve tirets(GM_LineString LS1, GM_LineString LS2, double pas) {
		double long1, long2;
		int nb_tirets;
		GM_MultiCurve tirets = new GM_MultiCurve();
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

	/** Définit des petits tirets entre 1 ligne et un point pour représenter un lien d'appariement */        
	public static GM_MultiCurve tirets(GM_LineString LS1, GM_Point PT, double pas) {
		double long1;
		int nb_tirets;
		GM_MultiCurve tirets = new GM_MultiCurve();
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

	/** Définit la géométrie d'un lien entre 2 lignes par un trait reliant les milieux des lignes */        
	public static GM_LineString tiret(GM_LineString LS1, GM_LineString LS2) {
		GM_LineString tiret = new GM_LineString();
		tiret.addControlPoint(Operateurs.milieu(LS1));
		tiret.addControlPoint(Operateurs.milieu(LS2));
		return tiret;
	}

	/** Définit la géométrie d'un lien entre 1 ligne et un point par un trait 
	 * reliant le milieu de la ligne au point */        
	public static GM_LineString tiret(GM_LineString LS1, GM_Point PT) {
		GM_LineString tiret = new GM_LineString();
		tiret.addControlPoint(Operateurs.milieu(LS1));
		tiret.addControlPoint(PT.getPosition());
		return tiret;
	}


	/** Définit des petits tirets entre 2 lignes pour représenter un lien d'appariement.
	 * NB: projete les points sur l'arc LS2, plutot que de se baser sur l'abscisse curviligne */        
	public static GM_MultiCurve tiretsProjetes(GM_LineString LS1, GM_LineString LS2, double pas) {
		double long1;
		int nb_tirets;
		GM_MultiCurve tirets = new GM_MultiCurve();
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

	/** Définit des petits tirets entre 1 ligne et un aggregat pour représenter un lien d'appariement.
	 * NB: projete les points sur l'aggregat, plutot que de se baser sur l'abscisse curviligne */        
	public static GM_MultiCurve tiretsProjetes(GM_LineString LS1, GM_Aggregate aggregat, double pas) {
		double long1;
		int nb_tirets;
		GM_MultiCurve tirets = new GM_MultiCurve();
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


	/** Définit la géométrie d'un lien entre 2 lignes par un trait reliant les lignes */        
	public static GM_LineString tiretProjete(GM_LineString LS1, GM_LineString LS2) {
		DirectPosition milieu = Operateurs.milieu(LS1);
		DirectPosition projete = Operateurs.projection(milieu, LS2);
		GM_LineString tiret = new GM_LineString();
		tiret.addControlPoint(milieu);
		tiret.addControlPoint(projete);
		return tiret;
	}

	/** Définit la géométrie d'un lien entre 2 lignes par un trait reliant la ligne à l'aggregat */        
	public static GM_LineString tiretProjete(GM_LineString LS1, GM_Aggregate aggegat) {
		DirectPosition milieu = Operateurs.milieu(LS1);
		DirectPosition projete = Operateurs.projection(milieu, aggegat);
		GM_LineString tiret = new GM_LineString();
		tiret.addControlPoint(milieu);
		tiret.addControlPoint(projete);
		return tiret;
	}

	/** Définit la géométrie d'un lien entre 1 point et son projeté sur la ligne  */        
	public static GM_LineString tiretProjete(GM_Point PT, GM_LineString LS2) {
		DirectPosition pt = PT.getPosition();
		DirectPosition projete = Operateurs.projection(pt, LS2);
		GM_LineString tiret = new GM_LineString();
		tiret.addControlPoint(pt);
		tiret.addControlPoint(projete);
		return tiret;
	}

	/** Définit la géométrie d'un lien entre 1 point et son projeté sur l'aggregat  */        
	public static GM_LineString tiretProjete(GM_Point PT, GM_Aggregate aggregat) {
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
		Iterator itObjRef, itObjComp;
		GM_Object geomRef = null, geomComp = null;
		boolean refPoint;
		GM_Aggregate geomLien, groupe;
		GM_Object buffer; 
		GM_Point centroide;
		GM_LineString ligne;
		GM_MultiCurve lignes ;

		geomLien = new GM_Aggregate();
		itObjRef = this.getObjetsRef().iterator();
		while (itObjRef.hasNext()) {
			// determination du coté ref
			geomRef = ((FT_Feature) itObjRef.next()).getGeom();
			if (geomRef instanceof GM_Point) refPoint = true;
			else {
				if (geomRef instanceof GM_LineString) refPoint = false;
				else {
					System.out.println("Géométrie réseau: Type de géométrie non géré "+geomRef.getClass());
					continue;
				}
			}

			// cas "1 noeud ref --> d'autres choses": 1 tiret + 1 buffer
			if ( refPoint ) {
				groupe = new GM_Aggregate();
				itObjComp = this.getObjetsComp().iterator();
				while (itObjComp.hasNext()) {
					// determination du coté comp
					geomComp = ((FT_Feature) itObjComp.next()).getGeom();
					groupe.add(geomComp);
				}
				buffer = groupe.buffer(20);					
				centroide = (GM_Point)buffer.centroid();
				ligne = new GM_LineString();
				ligne.addControlPoint(centroide.getPosition());			
				ligne.addControlPoint(((GM_Point)geomRef).getPosition());			
				geomLien.add(buffer);	
				geomLien.add(ligne);
				continue;	
			}

			// cas "1 arc ref --> d'autres choses": des tirets
			GM_Aggregate aggr = new GM_Aggregate();
			itObjComp = this.getObjetsComp().iterator();
			while (itObjComp.hasNext()) {
				// determination du coté comp
				geomComp = ((FT_Feature) itObjComp.next()).getGeom();
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
		GM_MultiSurface geomRef = new GM_MultiSurface();
		GM_MultiSurface geomComp = new GM_MultiSurface();
		FT_Feature obj;
		GM_Object geometrie;
		Iterator it ;

		it = this.getObjetsRef().iterator();
		while ( it .hasNext() ) {
			obj = (FT_Feature)it.next();
			geometrie = (GM_Object)obj.getGeom();
			if ( !(geometrie instanceof GM_Surface)) return 2;
			geomRef.add(geometrie);
		}
		it = this.getObjetsComp().iterator();
		while ( it .hasNext() ) {
			obj = (FT_Feature)it.next();
			geometrie = (GM_Object)obj.getGeom();
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
		GM_MultiSurface geomRef = new GM_MultiSurface();
		GM_MultiSurface geomComp = new GM_MultiSurface();
		FT_Feature obj;
		GM_Object geometrie;
		Iterator it ;

		it = this.getObjetsRef().iterator();
		while ( it .hasNext() ) {
			obj = (FT_Feature)it.next();
			geometrie = (GM_Object)obj.getGeom();
			if ( !(geometrie instanceof GM_Surface)) return 2;
			geomRef.add(geometrie);
		}
		it = this.getObjetsComp().iterator();
		while ( it .hasNext() ) {
			obj = (FT_Feature)it.next();
			geometrie = (GM_Object)obj.getGeom();
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
		GM_MultiSurface geomRef = new GM_MultiSurface();
		GM_MultiSurface geomComp = new GM_MultiSurface();
		FT_Feature obj;
		GM_Object geometrie;
		Iterator it ;

		it = this.getObjetsRef().iterator();
		while ( it .hasNext() ) {
			obj = (FT_Feature)it.next();
			geometrie = (GM_Object)obj.getGeom();
			if ( !(geometrie instanceof GM_Surface)) return 2;
			geomRef.add(geometrie);
		}
		it = this.getObjetsComp().iterator();
		while ( it .hasNext() ) {
			obj = (FT_Feature)it.next();
			geometrie = (GM_Object)obj.getGeom();
			if ( !(geometrie instanceof GM_Surface)) return 2;
			geomComp.add(geometrie);
		}

		return Distances.exactitude(geomRef,geomComp);
	}	

}