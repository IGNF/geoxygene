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

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.appli.geopensim.agent.AgentGeographique;
import fr.ign.cogit.appli.geopensim.feature.micro.Troncon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Implèmentation par Défaut d'une zone élémentaire
 * @author Julien Perret
 *
 */
public class ZoneElementaireImpl implements ZoneElementaire {
	protected int id;
	@Override
	public int getId() {return id;}
	@Override
	public void setId(int Id) {id = Id;}
	/**
	 * La classe conCrête des objets java liés par OJB.
	 */
	protected String ojbConcreteClass = ZoneElementaireImpl.class.getName();
	@Override
	public String getOjbConcreteClass() {return ojbConcreteClass;}
	@Override
	public void setOjbConcreteClass(String ojbConcreteClass) {this.ojbConcreteClass = ojbConcreteClass;}

	protected ZoneElementaire estTrouDe = null;
	protected int nombreTroncons;
	protected int nombreTrous;
	//Troncons
	protected Set<Troncon> troncons = new HashSet<Troncon>(0); //contour
	//Trous
	protected Set<ZoneElementaire> trous = new HashSet<ZoneElementaire>(0);
	//zones élémentaires voisines
	protected Set<ZoneElementaire> voisins = new HashSet<ZoneElementaire>(0);
	//zone agrégée
	protected ZoneAgregee<ZoneElementaire> zoneAgregee = null;
	//borde Unité urbaine
	protected boolean bordeUniteUrbaine =false;
	@Override
	public void addTroncon(Troncon O) {
		if ( O == null ) return;
		this.troncons.add(O);
		this.nombreTroncons=troncons.size();
	}
	@Override
	public void addTrou(ZoneElementaire zoneElementaire) {
		if (zoneElementaire == null) return;
		this.trous.add(zoneElementaire);
		this.nombreTrous = trous.size();
	}
	@Override
	public void addVoisin(ZoneElementaire voisin) {
		if (voisin==null) return;
		if (!this.voisins.contains(voisin)) this.voisins.add(voisin);
	}
	@Override
	public void emptyTroncons() {
		troncons.clear();
		this.nombreTroncons=troncons.size();
	}
	@Override
	public void emptyTrous() {
		for (ZoneElementaire trou:trous) trou.setEstTrouDe(null);
		this.trous.clear();
		nombreTrous = trous.size();
	}
	@Override
	public void emptyVoisins() {
		for(ZoneElementaire voisin:voisins) voisin.removeVoisin(this);
		voisins.clear();
	}
	@Override
	public boolean estTrou() {return (estTrouDe != null);}
	@Override
	public boolean getBordeUniteUrbaine() {return this.bordeUniteUrbaine;}
	@Override
	public ZoneElementaire getEstTrouDe() {return this.estTrouDe;}
	@Override
	public int getNombreTroncons() {return this.nombreTroncons;}
	@Override
	public int getNombreTrous() {return this.nombreTrous;}
	@Override
	public Set<Troncon> getTroncons() {return this.troncons;}
	@Override
	public Set<ZoneElementaire> getTrous() {return this.trous;}
	@Override
	public Set<ZoneElementaire> getVoisins() {return this.voisins;}
	@Override
	public ZoneAgregee<ZoneElementaire> getZoneAgregee() {return this.zoneAgregee;}
	@Override
	public void removeTroncon(Troncon O) {
		if ( O == null ) return;
		this.troncons.remove(O);
		this.nombreTroncons=this.troncons.size();
	}
	@Override
	public void removeTrou(ZoneElementaire zoneElementaire) {
		if (zoneElementaire == null) return;
		this.trous.remove(zoneElementaire);
		zoneElementaire.setEstTrouDe(null);
		this.nombreTrous = this.trous.size();
	}
	@Override
	public void removeVoisin(ZoneElementaire voisin) {
		if (voisin==null) return;
		if (this.voisins.contains(voisin)) this.voisins.remove(voisin);
	}
	@Override
	public void setBordeUniteUrbaine(boolean bordeUniteUrbaine) {this.bordeUniteUrbaine=bordeUniteUrbaine;}
	@Override
	public void setEstTrouDe(ZoneElementaire estTrouDe) {if (estTrouDe==null) return;this.estTrouDe = estTrouDe;/*if (!estTrouDe.getTrous().contains(this)) estTrouDe.addTrou(this);*/}
	@Override
	public void setNombreTroncons(int nombreTroncons) {this.nombreTroncons=nombreTroncons;}
	@Override
	public void setNombreTrous(int nombreTrous) {this.nombreTrous = nombreTrous;}
	@Override
	public void setTroncons(Set<Troncon> troncons) {
		this.emptyTroncons();
		this.troncons = troncons;
		this.nombreTroncons = this.troncons.size();
	}
	@Override
	public void setTrous(Set<ZoneElementaire> trous) {
		this.emptyTrous();
		this.trous = trous;
		if (trous==null) return;
		this.nombreTrous = this.trous.size();
	}
	@Override
	public void setVoisins(Set<ZoneElementaire> voisins) {
		this.emptyVoisins();
		this.voisins = voisins;
	}
	@Override
	public void setZoneAgregee(ZoneAgregee<ZoneElementaire> zoneAgregee) {this.zoneAgregee=zoneAgregee;}
	@Override
	public void qualifier() {}
	@Override
	public <Elem extends ZoneElementaire, Agreg extends ZoneAgregee<Elem>> Unite<Elem, Agreg> getUnite() {return null;}
	@Override
	public <Elem extends ZoneElementaire, Agreg extends ZoneAgregee<Elem>> void setUnite(Unite<Elem, Agreg> unite) {}
	@Override
	public IGeometry getGeom() {return null;}
	@Override
	public void setGeom(IGeometry geom) {}
	@Override
	public AgentGeographique getAgentGeographique() {return null;}
	@Override
	public void setAgentGeographique(AgentGeographique agentGeographique) {}
}
