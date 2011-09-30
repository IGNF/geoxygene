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
package fr.ign.cogit.appli.geopensim.agent;

import java.util.List;

import fr.ign.cogit.appli.geopensim.contrainte.Contrainte;


/**
 * Agent Géographique
 * @author Julien Perret
 *
 */
public interface Agent {

	/**
	 * Affecte un identifiant (ne pas utiliser si l'objet est persistant car cela est automatique)
	 * @param id identifiant de l'agent
	 */
	public void setId(int id);
	/**
	 * Renvoie l'identifiant. NB: l'identifiant n'est rempli automatiquement que pour les objets persistants
	 * @return l'identifiant de l'agent
	 */
	public int getId();
	
	/**
	 * 
	 */
	public void activer();
	/**
	 * @return Contraintes
	 */
	public List<Contrainte> getContraintes();
	/**
	 * 
	 */
	public void cycleDeVie();

	/**
	 * 
	 */
	public void instancierContraintes();
	/**
	 * Renvoie la date de Début de la simulation.
	 * @return la date de Début de la simulation
	 */
	public int getDateDebutSimulation();
	/**
	 * Affecte la date de Début de la simulation
	 * et affecte les attributs de l'agent à l'aide des attributs de sa représentation
	 * à la date de Début de la simulation.
	 * @param dateDebutSimulation la date de Début de la simulation
	 */
	public void setDateDebutSimulation(int dateDebutSimulation);
	/**
	 * Renvoie la date simulée, i.e. la date de fin de la simulation
	 * @return la date simulée, i.e. la date de fin de la simulation
	 */
	public int getDateSimulee();
	/**
	 * Affecte la date simulée, i.e. la date de fin de la simulation 
	 * et affecte les valeurs objectifs aux agents.
	 * @param dateSimulee la date simulée, i.e. la date de fin de la simulation
	 */
	public void setDateSimulee(int dateSimulee);
	
	/**
	 * @return la satisfaction de l'agent.
	 */
	public double getSatisfaction();
	/**
	 * Affecte la satisfaction de l'agent.
	 * @param satisfaction la satisfaction de l'agent.
	 */
	public void setSatisfaction(double satisfaction);
}
