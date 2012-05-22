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

package fr.ign.cogit.appli.geopensim.agent.micro;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneElementaireBatie;
import fr.ign.cogit.appli.geopensim.feature.micro.Troncon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * @author Julien Perret
 *
 */
public abstract class AgentTronconRoutier extends AgentTroncon {
	static Logger logger=Logger.getLogger(AgentTronconRoutier.class.getName());

	/**
	 * 
	 */
	public AgentTronconRoutier() {super();}
	/**
	 * @param idGeo
	 */
	public AgentTronconRoutier(int idGeo, Class<? extends Troncon> classe) {super(idGeo,classe);}
	
	boolean impasse = false;
	public boolean isImpasse() {return this.impasse;}
	public void setImpasse(boolean impasse) {this.impasse = impasse;}

	public double getAccessibilite() {
		if (getZonesElementaires().size()>1) return 100;
		/*
		double distance = ((AgentZoneElementaireBatie)getZonesElementaires().iterator().next()).getDistanceALaRoute();
		if (distance<100) return 100;
		return 150-distance/2;
		*/
		AgentZoneElementaireBatie agentZone = (AgentZoneElementaireBatie)getZonesElementaires().iterator().next();
		IGeometry zoneInaccessible = agentZone.getZoneInaccessible();
		if (zoneInaccessible==null) return 100;
		double inaccessibleArea = zoneInaccessible.area();
		if (logger.isDebugEnabled()) logger.debug("zone inaccessible = "+inaccessibleArea);
		if (logger.isDebugEnabled()) logger.debug("zone = "+agentZone.getGeom().area());
		if (logger.isDebugEnabled()) logger.debug("accessiblite = "+100*(1-inaccessibleArea/agentZone.getGeom().area()));
		return 100*(1-inaccessibleArea/agentZone.getGeom().area());
	}

}
