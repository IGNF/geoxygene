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
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;
import fr.ign.cogit.appli.geopensim.util.NoteAlignement;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * Classe représentant les alignements de bâtiments à l'interieur
 * d'une zone élémentaire batie.
 * @author Julien Perret
 *
 */
@Entity
public class Alignement extends ZoneSurfaciqueUrbaine {

	GroupeBatiments groupeBatiments = null;
	/**
	 * Renvoie la valeur de l'attribut groupeBatiments.
	 * @return la valeur de l'attribut groupeBatiments
	 */
	@ManyToOne
	public GroupeBatiments getGroupeBatiments() {return this.groupeBatiments;}
	/**
	 * Affecte la valeur de l'attribut groupeBatiments.
	 * @param groupeBatiments l'attribut groupeBatiments à affecter
	 */
	public void setGroupeBatiments(GroupeBatiments groupeBatiments) {
		this.groupeBatiments = groupeBatiments;
		this.groupeBatiments.getAlignements().add(this);
		this.setZoneElementaireUrbaine(groupeBatiments.getZoneElementaireUrbaine());
		this.setUniteUrbaine(groupeBatiments.getUniteUrbaine());
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

	public Alignement() {
		super();
	}
	/**
	 * @param groupeBatiments
	 * @param batiments
	 * TODO nettoyer code entre alignements et groupes de batiments
	 */
	public Alignement(GroupeBatiments groupeBatiments, List<Batiment> batiments) {
		super();
		this.batiments=batiments;
		this.setGroupeBatiments(groupeBatiments);
		for (Batiment batiment : batiments) batiment.getAlignements().add(this);
		double distance = 100;
		List<Geometry> liste = new ArrayList<Geometry>();
		for (Batiment batiment : batiments) {
			try {
				Geometry geoBatiment = AdapterFactory.toGeometry(new GeometryFactory(), batiment.getGeom().buffer(distance));
				liste.add(geoBatiment);
			} catch(Exception e) {}
		}
		Geometry union = JtsAlgorithms.union(liste);
		try {
			IGeometry geometrie = AdapterFactory.toGM_Object(union.buffer(-distance));
			this.setGeom(geometrie);
		} catch(Exception e) {}
	}
	/**
	 * @param batiments
	 * TODO nettoyer code entre alignements et groupes de batiments
	 */
	public Alignement(List<Batiment> batiments) {
		super();
		this.batiments=batiments;
		for (Batiment batiment : batiments) batiment.getAlignements().add(this);
		double distance = 100;
		List<Geometry> liste = new ArrayList<Geometry>();
		for (Batiment batiment : batiments) {
			try {
				Geometry geoBatiment = AdapterFactory.toGeometry(new GeometryFactory(), batiment.getGeom().buffer(distance));
				liste.add(geoBatiment);
			} catch(Exception e) {}
		}
		Geometry union = JtsAlgorithms.union(liste);
		try {
		    IGeometry geometrie = AdapterFactory.toGM_Object(union.buffer(-distance));
			this.setGeom(geometrie);
		} catch(Exception e) {}
	}

	/**
	 * Note sur l'aire des bâtiments de l'alignement
	 * (plus la note est forte, moins bon est l'alignement).
	 */
	protected double noteAire = 0.0;
	/**
	 * @return la note sur l'aire des bâtiments de l'alignement.
	 */
	public double getNoteAire() {return this.noteAire;}
	/**
	 * @param noteAire la note sur l'aire des bâtiments de l'alignement.
	 */
	public void setNoteAire(double noteAire) {this.noteAire = noteAire;}
	
	/**
	 * Note sur la convexité des bâtiments de l'alignement
	 * (plus la note est forte, moins bon est l'alignement).
	 */
	protected double noteConvexite;
	/**
	 * @return la note sur la convexité des bâtiments de l'alignement.
	 */
	public double getNoteConvexite() {return this.noteConvexite;}
	/**
	 * @param noteConvexite la note sur la convexité des bâtiments de l'alignement.
	 */
	public void setNoteConvexite(double noteConvexite) {this.noteConvexite = noteConvexite;}
	
	/**
	 * Note sur la distance inter bâtiments de l'alignement
	 * (plus la note est forte, moins bon est l'alignement).
	 */
	protected double noteDistance;
	/**
	 * @return la note sur la distance inter bâtiments de l'alignement.
	 */
	public double getNoteDistance() {return this.noteDistance;}
	/**
	 * @param noteDistance la note sur la distance inter bâtiments de l'alignement.
	 */
	public void setNoteDistance(double noteDistance) {this.noteDistance = noteDistance;}
	
	/**
	 * Note sur l'étirement de l'alignement
	 * (plus la note est forte, moins bon est l'alignement).
	 */
	protected double noteEtirement;
	/**
	 * @return la note sur l'étirement de l'alignement.
	 */
	public double getNoteEtirement() {
		return this.noteEtirement;
	}
	/**
	 * @param noteEtirement la note sur l'étirement de l'alignement.
	 */
	public void setNoteEtirement(double noteEtirement) {
		this.noteEtirement = noteEtirement;
	}
	
	/**
	 * Note générale de qualité de l'alignement
	 * (plus la note est forte, moins bon est l'alignement).
	 */
	protected double noteGenerale;
	/**
	 * @return noteGenerale la note générale de qualité de l'alignement.
	 */
	public double getNoteGenerale() {return this.noteGenerale;}
	/**
	 * @param noteGenerale la note générale de qualité de l'alignement.
	 */
	public void setNoteGenerale(double noteGenerale) {this.noteGenerale = noteGenerale;}
	
	@Override
	public void qualifier() {
		super.qualifier();
		noteConvexite = NoteAlignement.calculNoteConvexite(this.getBatiments());
		noteAire = NoteAlignement.calculNoteAire(this.getBatiments());
		noteDistance = NoteAlignement.calculNoteDistance(this.getBatiments());
		noteEtirement = NoteAlignement.calculNoteEtirement(this.getBatiments());
		noteGenerale = NoteAlignement.calculNoteGenerale(noteAire, noteConvexite, noteDistance, noteEtirement);	
	}

	private Collection<Batiment> batiments = new ArrayList<Batiment>(0);
	@Override
	@OneToMany
	public Collection<Batiment> getBatiments() {return batiments;}

	/**
	 * @param batiments bâtiments de la surface bâtie
	 */
	public void setBatiments(Collection<Batiment> batiments) {
		this.batiments = batiments;
		nombreBatiments=batiments.size();
	}
	
	/**
	 * @param e bâtiment de l'alignement
	 */
	public void addBatiment(Batiment e) {
		if ( e == null ) return;
		batiments.add(e);
		nombreBatiments=batiments.size();
		//e.setAlignement(this);
	}
//	@Override
//	public int sizeBatiments() {return batiments.size();}
}
