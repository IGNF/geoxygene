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

package fr.ign.cogit.appli.geopensim.agent.macro;

import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.agent.AgentGeographique;


/**
 * @author Florence Curie
 *
 */
public class EtatGlobal {

	static Logger logger=Logger.getLogger(EtatGlobal.class.getName());
	static int staticId = 1;
	
	/**
	 * Constructeur d'un état global.
	 */
	public EtatGlobal() {
		super();
		this.setId(staticId++);
	}

	protected int id;
	/**
	 * @return id identifiant de l'état global
	 */
	public int getId() {return id;}
	/**
	 * @param Id identifiant de l'état global
	 */
	public void setId(int Id) {id = Id;}
	
	protected String nom;
	/**
	 * @return nom le nom de l'état global
	 */
	public String getNom() {return this.nom;}
	/**
	 * @param nom e nom de l'état global
	 */
	public void setNom(String nom) {this.nom = nom;}

	protected boolean simule;
	/**
	 * @return simule (true : si l'état est simulé et false : si l'état existe)  
	 */
	public boolean isSimule() {return this.simule;}
	/**
	 * @param simule (true : si l'état est simulé et false : si l'état existe)
	 */
	public void setSimule(boolean simule) {this.simule = simule;}
	
	protected int date;
	/**
	 * @return date la date de cet état global
	 */
	public int getDate() {return this.date;}
	/**
	 * @param date la date de cet état global
	 */
	public void setDate(int date) {this.date = date;}
	
	protected EtatGlobal etatPrecedent;
	/**
	 * @return etatPrecedent l'état global précédent cet état global
	 */
	public EtatGlobal getEtatPrecedent() {return this.etatPrecedent;}
	/**
	 * @param etatPrecedent l'état global précédent cet état global
	 */
	public void setEtatPrecedent(EtatGlobal etatPrecedent) {this.etatPrecedent = etatPrecedent;}
	
	protected List<AgentGeographique> collection;
	/**
	 * @return populations les populations liées à cet état global
	 */
	public List<AgentGeographique> getCollection() {return this.collection;}
	/**
	 * @param populations les populations liées à cet état global
	 */
	public void setCollection (List<AgentGeographique> collection) {this.collection = collection;}
	
}
