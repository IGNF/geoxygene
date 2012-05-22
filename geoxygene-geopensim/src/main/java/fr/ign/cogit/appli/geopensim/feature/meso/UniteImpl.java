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

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Implèmentation par Défaut de l'interface Unite
 * @author Julien Perret
 *
 */
public class UniteImpl<Elem extends ZoneElementaire, Agreg extends ZoneAgregee<Elem>> implements Unite<Elem, Agreg> {
	static Logger logger=Logger.getLogger(UniteImpl.class.getName());
	/**
	 * Zones agrégées appartenant à l'Unité
	 */
	List<Agreg> zonesAgregees = new ArrayList<Agreg>();
	/**
	 * Zones élémentaires appartenant à l'Unité
	 */
	List<Elem> zonesElementaires = new ArrayList<Elem>();
	@Override
	public void addZoneElementaire(Elem zone) {if (zone!=null) this.zonesElementaires.add(zone);}
	@Override
	public void emptyZonesElementaires() {zonesElementaires.clear();}
	@Override
	public List<Agreg> getZonesAgregees() {return this.zonesAgregees;}
	@Override
	public List<Elem> getZonesElementaires() {return this.zonesElementaires;}
	@Override
	public void removeZoneElementaire(Elem zone) {if (zone!=null) this.zonesElementaires.remove(zone);}
	@Override
	public void setZonesAgregees(List<Agreg> agreg) {this.zonesAgregees=agreg;}
	@Override
	public void setZonesElementaires(List<Elem> elem) {this.zonesElementaires=elem;}
	@Override
	public int getId() {return 0;}
	@Override
	public void setId(int Id) {}
	@Override
	public IGeometry getGeom() {return null;}
	@Override
	public void setGeom(IGeometry geom) {}
}
