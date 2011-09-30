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

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import fr.ign.cogit.appli.geopensim.agent.AgentGeographique;
import fr.ign.cogit.appli.geopensim.feature.micro.Troncon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * @author Julien Perret
 *
 */
@Entity
@MappedSuperclass
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="ojbConcreteClass",discriminatorType=DiscriminatorType.STRING)
public interface ZoneElementaire {
	/**
	 * Renvoie l'identifiant. NB: l'identifiant n'est rempli automatiquement que
	 * pour les objets persistants
	 */
	@Id @GeneratedValue
	public abstract int getId();

	/**
	 * Affecte un identifiant (ne pas utiliser si l'objet est persistant car
	 * cela est automatique)
	 */
	public abstract void setId(int Id);
	
	public abstract IGeometry getGeom();
	public abstract void setGeom(IGeometry geom);
	
	/**
	 * @return la classe conCrête des objets java liés par OJB.
	 */
	public abstract String getOjbConcreteClass();
	/**
	 * @param ojbConcreteClass la classe conCrête des objets java liés par OJB
	 */
	public abstract void setOjbConcreteClass(String ojbConcreteClass);

	public abstract <Elem extends ZoneElementaire,Agreg extends ZoneAgregee<Elem>> Unite<Elem,Agreg> getUnite();
	public <Elem extends ZoneElementaire,Agreg extends ZoneAgregee<Elem>> void setUnite(Unite<Elem,Agreg> unite);
	
	/**
	 * @return liste des tronçons entourant la zone élémentaire
	 */
	@OneToMany
	public abstract Set<Troncon> getTroncons();

	/**
	 * @param Troncons tronçons entourant la zone élémentaire
	 */
	public abstract void setTroncons(Set<Troncon> Troncons);

	/**
	 * @param i indice du troncon à récupèrer
	 * @return tronçon d'indice i entourant la zone élémentaire
	 */
	//public abstract Troncon getTroncon(int i);

	/**
	 * Ajout d'un tronçon
	 * @param troncon tronçon entourant la zone élémentaire à ajouter
	 */
	public abstract void addTroncon(Troncon troncon);

	/**
	 * Suppression d'un tronçon
	 * @param troncon tronçon entourant la zone élémentaire à supprimer
	 */
	public abstract void removeTroncon(Troncon troncon);

	/**
	 * Vidage de la liste des tronçons entourant la zone élémentaire
	 */
	public abstract void emptyTroncons();

	/**
	 * @return nombre de troncons entourant la zone élémentaire
	 */
	public abstract int getNombreTroncons();

	/**
	 * @param nombreTroncons nombre de tronçons entourant la zone élémentaire
	 */
	public abstract void setNombreTroncons(int nombreTroncons);

	/**
	 * @param trous trous de la zone élémentaire, i.e. les zones élémentaires inclues dans celle-ci
	 */
	public abstract void setTrous(Set<ZoneElementaire> trous);

	/**
	 * @return trous de la zone élémentaire, i.e. les zones élémentaires inclues dans celle-ci
	 */
	@OneToMany(mappedBy="estTrouDe")
	public abstract Set<ZoneElementaire> getTrous();

	/**
	 * @param i indice du trou recherché
	 * @return trou numéro i de la zone élémentaire
	 */
	//public abstract ZoneElementaire getTrou(int i);

	/**
	 * Ajout d'un trou à la zone élémentaire
	 * @param zoneElementaire trou de la zone élémentaire, i.e. une zone élémentaire inclue dans celle-ci
	 */
	public abstract void addTrou(ZoneElementaire zoneElementaire);

	/**
	 * Suppression d'un trou de la zone élémentaire
	 * @param zoneElementaire trou à enlever
	 */
	public abstract void removeTrou(ZoneElementaire zoneElementaire);

	/**
	 * Vidage de la liste des trous
	 */
	public abstract void emptyTrous();

	/**
	 * @return estTrouDe zone élémentaire de laquelle cette zone élémentaire est un trou. NULL si la zone élémentaire n'est pas un trou.
	 */
    @ManyToOne
    public abstract ZoneElementaire getEstTrouDe();

	/**
	 * @param estTrouDe zone élémentaire de laquelle cette zone élémentaire est un trou
	 */
	public abstract void setEstTrouDe(ZoneElementaire estTrouDe);

	/**
	 * @return vrai si la zone élémentaire est un trou d'une autre zone élémentaire, faux sinon.
	 */
	public abstract boolean estTrou();

	/**
	 * @return zone agrégée à laquelle appartient la zone élémentaire
	 */
    @ManyToOne(targetEntity=ZoneAgregee.class)
	public abstract ZoneAgregee<ZoneElementaire> getZoneAgregee();

	/**
	 * @param zoneAgregee zone agrégée à laquelle appartient la zone élémentaire
	 */
	public abstract void setZoneAgregee(ZoneAgregee<ZoneElementaire> zoneAgregee);

	/**
	 * @return vrai si la zone élémentaire borde l'Unité à laquelle elle appartient
	 */
	public abstract boolean getBordeUniteUrbaine();

	/**
	 * @param bordeUnite vrai si la zone élémentaire borde l'Unité à laquelle elle appartient
	 */
	public abstract void setBordeUniteUrbaine(boolean bordeUnite);

	/**
	 * @return nombre de trous que possède la zone élémentaire
	 */
	public abstract int getNombreTrous();

	/**
	 * @param nombreTrous nombre de trous que possède la zone élémentaire
	 */
	public abstract void setNombreTrous(int nombreTrous);

	/**
	 * @return la liste des voisins de la zone élémentaire
	 */
	@ManyToMany
	public abstract Set<ZoneElementaire> getVoisins();

	/**
	 * @param voisins la liste des voisins de la zone élémentaire
	 */
	public abstract void setVoisins(Set<ZoneElementaire> voisins);

	/**
	 * Ajout d'un voisin à la zone élémentaire
	 * @param voisin voisin de la zone élémentaire
	 */
	public abstract void addVoisin(ZoneElementaire voisin);

	/**
	 * Suppression d'un voisin de la zone élémentaire
	 * @param voisin voisin de la zone élémentaire à enlever
	 */
	public abstract void removeVoisin(ZoneElementaire voisin);

	/**
	 * Vidage de la liste des voisins de la zone élémentaire
	 */
	public abstract void emptyVoisins();
	
	/**
	 * Qualifier la représentation
	 */
	public abstract void qualifier();
	
	/**
	 * @return the elementGeo
	 */
	@ManyToOne(optional=false, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	public AgentGeographique getAgentGeographique();
	/**
	 * @param agentGeographique the elementGeo to set
	 */
	public void setAgentGeographique(AgentGeographique agentGeographique);

}