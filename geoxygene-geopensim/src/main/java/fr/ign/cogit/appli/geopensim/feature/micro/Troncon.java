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
/**
 * 
 */
package fr.ign.cogit.appli.geopensim.feature.micro;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaire;

/**
 * @author Julien Perret
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Troncon extends MicroRepresentation {

	/**
	 * Liste des îlots bordés par le tronçon.
	 */
	protected Set<ZoneElementaire> zonesElementaires = new HashSet<ZoneElementaire>();
	/**
	 * @return zonesElementaires bordées par le tronçon
	 */
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	    @JoinTable(
            name="troncons_zoneselementaires",
            joinColumns = @JoinColumn( name="troncon_id"),
            inverseJoinColumns = @JoinColumn( name="zone_id"))
	public Set<ZoneElementaire> getZonesElementaires() {return zonesElementaires;}
	/**
	 * @param zonesElementaires zonesElementaires bordées par le tronçon
	 */
	public void setZonesElementaires(Set<ZoneElementaire> zonesElementaires) {this.zonesElementaires = zonesElementaires;}
	/**
	 * Ajout d'une zoneElementaire bordée par le tronçon.
	 * @param zoneElementaire zoneElementaire bordée par le tronçon
	 */
	public void addZoneElementaire(ZoneElementaire zoneElementaire) {
		if (zoneElementaire == null) return;
		zonesElementaires.add(zoneElementaire);
	}
	/**
	 * Suppression d'une zoneElementaire bordée par le tronçon
	 * @param zoneElementaire zoneElementaire bordée par le tronçon
	 */
	public void removeZoneElementaire(ZoneElementaire zoneElementaire) {
		if (zoneElementaire == null) return;
		this.zonesElementaires.remove(zoneElementaire);
	}
	/**
	 * Vidage de la liste des îlots bordés par le tronçon
	 */
	public void emptyZonesElementaires() {this.zonesElementaires.clear();}

	/* (non-Javadoc)
	 * @see geoxygene.geodata.feature.MicroRepresentation#qualifier()
	 */
	@Override
	public void qualifier() {
	}
}
