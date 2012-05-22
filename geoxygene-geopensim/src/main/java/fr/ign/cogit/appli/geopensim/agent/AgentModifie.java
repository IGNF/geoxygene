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

package fr.ign.cogit.appli.geopensim.agent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.feature.event.FeatureCollectionEvent.Type;

/**
 * @author Florence Curie
 */
public class AgentModifie {

	private AgentGeographique agent;
	private Type statut;
	private Map<String,Valeurs> mapAttributValeur = new HashMap<String,Valeurs>();
	
	// Constructeur 1 à partir d'un agent et de son statut
	public AgentModifie(AgentGeographique ag,Type st){
		agent = ag;
		statut = st;
	}
	
	// Constructeur 1 à partir d'un attribut et de 2 valeurs
	public AgentModifie(AgentGeographique ag,Type st,String attrib, Object val1, Object val2){
		agent = ag;
		statut = st;
		Valeurs vals = new Valeurs(val1, val2);
		mapAttributValeur.put(attrib, vals);
	}
	
	// Constructeur 2 à partir d'une liste d'attributs et d'une liste de valeurs
	public AgentModifie(AgentGeographique ag,Type st,List<String> attribs, List<Valeurs> vals){
		agent = ag;
		statut = st;
		for (int i=0;i<attribs.size();i++){
			String key = attribs.get(i);
			Valeurs value = vals.get(i);
			mapAttributValeur.put(key, value);
		}
	}
	
	// Constructeur 3 à partir d'une map
	public AgentModifie(AgentGeographique ag,Type st,Map<String,Valeurs> map){
		agent = ag;
		statut = st;
		mapAttributValeur.putAll(map);
	}
	
	public class Valeurs {
		private Object valeurInitiale;
		private Object valeurFinale;
		
		public Valeurs(Object valeurInit,Object valeurFin){
			setValeurInitiale(valeurInit);
			setValeurFinale(valeurFin);
		}

		/**
		 * @param valeurInitiale la valeur initiale de l'objet avant changement
		 */
		public void setValeurInitiale(Object valeurInitiale) {this.valeurInitiale = valeurInitiale;}

		/**
		 * @return valeurInitiale la valeur initiale de l'objet avant changement
		 */
		public Object getValeurInitiale() {return valeurInitiale;}

		/**
		 * @param valeurFinale la valeur finale de l'objet après changement
		 */
		public void setValeurFinale(Object valeurFinale) {this.valeurFinale = valeurFinale;}

		/**
		 * @return valeurFinale la valeur finale de l'objet après changement
		 */
		public Object getValeurFinale() {return valeurFinale;}
		
	}

	/**
	 * Ajout d'un attribut et de sa valeur
	 */
	public void addAttributValeurs(String attrib,Object val1,Object val2){
		Valeurs vals = new Valeurs(val1, val2);
		addAttributValeurs(attrib,vals);
	}
	
	/**
	 * Ajout d'un attribut et de sa valeur
	 */
	public void addAttributValeurs(String attrib,Valeurs vals){
		this.mapAttributValeur.put(attrib, vals);
		}
	/**
	 * Suppression d'un attribut et de sa valeur
	 */
	public void removeAttributValeurs(String attrib){
		this.mapAttributValeur.remove(attrib);
		}
	
	/**
	 * @return la valeur de l'attribut mapAttributValeur
	 */
	public Map<String, Valeurs> getMapAttributValeur() {return this.mapAttributValeur;}

	/**
	 * @param mapAttributValeur l'attribut mapAttributValeur à affecter
	 */
	public void setMapAttributValeur(Map<String, Valeurs> mapAttributValeur) {this.mapAttributValeur = mapAttributValeur;}

	/**
	 * @return la valeur de l'attribut agent
	 */
	public AgentGeographique getAgent() {return this.agent;}

	/**
	 * @param agent l'attribut agent à affecter
	 */
	public void setAgent(AgentGeographique agent) {this.agent = agent;}

	/**
	 * @return la valeur de l'attribut statut
	 */
	public Type getStatut() {return this.statut;}

	/**
	 * @param statut l'attribut statut à affecter
	 */
	public void setStatut(Type statut) {this.statut = statut;}
	
}
