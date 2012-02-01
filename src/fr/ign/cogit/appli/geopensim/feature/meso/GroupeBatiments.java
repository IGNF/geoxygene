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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
 * Classe représentant les groupes de bâtiments à l'interieur d'une zone
 * élémentaire batie.
 * @author Julien Perret
 *
 */
@Entity
public class GroupeBatiments extends ZoneSurfaciqueUrbaine {

	List<Alignement> alignements = new ArrayList<Alignement>(0);
	/**
	 * @return la valeur de l'attribut alignements
	 */
	@OneToMany
	public List<Alignement> getAlignements() {return this.alignements;}
	/**
	 * @param i indice
	 * @return l'alignement d'indice i ddu groupe de bâtiments
	 */
	public Alignement getAlignement(int i) {return alignements.get(i);}
	/**
	 * Affecte la valeur de l'attribut alignements.
	 * @param alignements l'attribut alignements à affecter
	 */
	public void setAlignements(List<Alignement> alignements) {this.alignements = alignements;}
	/**
	 * @param e alignement du groupe de batiments
	 */
	public void addAlignement(Alignement e) {
		if ( e == null ) return;
		alignements.add(e);
		e.setGroupeBatiments(this);
	}

	ZoneElementaireUrbaine zoneElementaireUrbaine = null;
	/**
	 * @return
	 */
	@ManyToOne
	public ZoneElementaireUrbaine getZoneElementaireUrbaine() {return zoneElementaireUrbaine;}

	/**
	 * @param zoneElementaireUrbaine
	 */
	public void setZoneElementaireUrbaine(ZoneElementaireUrbaine zoneElementaireUrbaine) {this.zoneElementaireUrbaine = zoneElementaireUrbaine;}

	/**
	 *
	 */
	public GroupeBatiments() {
		super();
	}
	/**
	 * @param zoneElementaireUrbaine zone élémentaire à laquelle appartient le groupe
	 * @param batiments liste des bâtiments appartenant au groupe
	 */
	public GroupeBatiments(ZoneElementaireUrbaine zoneElementaireUrbaine, Collection<Batiment> batiments, IGeometry geom) {
		super();
		this.setZoneElementaireUrbaine(zoneElementaireUrbaine);
		this.setUniteUrbaine(zoneElementaireUrbaine.getUniteUrbaine());
		this.setBatiments(batiments);
		this.setGeom(geom);
	}

	/**
	 * Construit les alignements présents dans ce groupe de bâtiments.
	 */
	public void construireAlignements() {
		if (this.getNombreBatiments() < 3) return;
		if (logger.isDebugEnabled()) {logger.debug("Début Construction des alignements du groupe "+this.getGeom());}
		// Construction de la table de hachage entre les bâtiments et les centroïdes de bâtiments
		Map<DirectPosition,Batiment> correspondancePositionBatiment = new HashMap<DirectPosition,Batiment>();
		DirectPositionList centroidBatiment = new DirectPositionList();
		for (Batiment bati:this.getBatiments()){
			bati.qualifier();
			DirectPosition centroid = bati.getCentroid();
			centroidBatiment.add(centroid);
			correspondancePositionBatiment.put(centroid, bati);
		}

		// Construction des alignements pour ce groupe de bâtiment
		DetectionAlignement algoAlignement = new DetectionAlignement(centroidBatiment);
		List <IDirectPositionList> listePaquet = algoAlignement.getListePaquetsFinal();
		List<Double> listeAlpha = algoAlignement.getListeAlphaFinal();

		// transformation de la liste de centroïdes en liste de batiments
		List<List<Batiment>> listeAlignement = new ArrayList<List<Batiment>>();
		for (IDirectPositionList paquet:listePaquet) {
			List <Batiment> listeBatiments = new ArrayList<Batiment>();
			for (IDirectPosition centroid:paquet) {
				Batiment batiment = correspondancePositionBatiment.get(centroid);
				listeBatiments.add(batiment);
			}
			listeAlignement.add(listeBatiments);
		}

		// Qualification des alignements
		SelectionAlignement algoAlignement2 = new SelectionAlignement(listeAlignement,listeAlpha,this.getZoneElementaireUrbaine());
		algoAlignement2.qualification();
		// Affichage du nombre d'alignements Déterminés
		if (logger.isDebugEnabled()) logger.debug("Nombre d'alignements Détectés : "+listeAlignement.size());

		// création de l'objet Alignement
		for (List<Batiment> listeBatiments:listeAlignement){
			Alignement alignement = new Alignement(listeBatiments);
			alignement.setGroupeBatiments(this);
			alignement.setDateSourceSaisie(this.getDateSourceSaisie());
		}
		if (logger.isDebugEnabled()) {logger.debug("Fin Construction des alignements du groupe");}
	}

	@Override
	public void qualifier() {
		super.qualifier();
		for(Alignement alignement:this.getAlignements()) {alignement.qualifier();}
	}

	protected Collection<Batiment> batiments = new ArrayList<Batiment>();

	@Override
	@OneToMany
	public Collection<Batiment> getBatiments() {return batiments;}

	/**
	 * @param batiments bâtiments de la surface bâtie
	 */
	public void setBatiments(Collection<Batiment> batiments) {
		this.batiments = batiments;
		nombreBatiments=batiments.size();
		for (Batiment b : batiments) {
		  b.setGroupeBatiments(this);
		}
	}

	/**
	 * @param e bâtiment de la surface bâtie
	 */
	public void addBatiment(Batiment e) {
		if ( e == null ) return;
		batiments.add(e);
		nombreBatiments=batiments.size();
		e.setGroupeBatiments(this);
	}
//	@Override
//	public int sizeBatiments() {return batiments.size();}
}
