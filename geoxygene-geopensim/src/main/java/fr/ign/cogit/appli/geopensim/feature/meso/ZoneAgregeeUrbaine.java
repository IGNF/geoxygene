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
package fr.ign.cogit.appli.geopensim.feature.meso;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;

/**
 * Zones agrégées urbaines : les quartiers.
 * @author Julien Perret
 */
@Entity
public class ZoneAgregeeUrbaine extends ZoneSurfaciqueUrbaine implements ZoneAgregee<ZoneElementaireUrbaine> {

	/**
	 * Implèmentation par Défaut de la zone agrégée
	 */
	protected ZoneAgregeeImpl<ZoneElementaireUrbaine> zoneAgregeeImpl = new ZoneAgregeeImpl<ZoneElementaireUrbaine>();

	@OneToMany(targetEntity=ZoneElementaireUrbaine.class)
	@Override
	public List<ZoneElementaireUrbaine> getZonesElementaires() {return zoneAgregeeImpl.getZonesElementaires();}

	@Override
	public void setZonesElementaires(List<ZoneElementaireUrbaine> zonesElementaires) {zoneAgregeeImpl.setZonesElementaires(zonesElementaires);}

	@Override
	public List<Batiment> getBatiments() {
	    List<Batiment> batiments = new ArrayList<Batiment>();
	    for(ZoneElementaireUrbaine zone:this.getZonesElementaires()) batiments.addAll(zone.getBatiments());
	    return batiments;
	}
//	@Override
//	public int sizeBatiments() {
//	    int nbBatiments = 0;
//	    for(ZoneElementaireUrbaine zone:this.getZonesElementaires()) nbBatiments+=zone.sizeBatiments();
//	    return nbBatiments;
//	}
}
