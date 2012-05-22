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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import fr.ign.cogit.appli.geopensim.comportement.Comportement;
import fr.ign.cogit.appli.geopensim.feature.ElementRepresentation;

/**
 * @author Florence Curie
 */

public class Etat {
	static Logger logger=Logger.getLogger(Etat.class.getName());
	static int staticId = 1;
	
    /**
	 * Constructeur d'un état.
	 */
	public Etat() {
		super();
		this.setId(staticId++);
	}

	protected int id;
	/**
	 * @return id identifiant de l'état
	 */
	public int getId() {return id;}
	/**
	 * @param Id identifiant de l'état
	 */
	public void setId(int Id) {id = Id;}
	
	double satisfaction;
	/**
	 * @param satisfaction la satisfaction de l'état
	 */
	public void setSatisfaction(double satisfaction) {this.satisfaction = satisfaction;}
	/**
	 * @return satisfaction la satisfaction de l'état
	 */
	public double getSatisfaction() {return satisfaction;}
	
	private Etat precedent=null;
	/**
	 * @param precedent la valeur de l'état précédent
	 */
	public void setPrecedent(Etat precedent) {this.precedent = precedent;}
	/**
	 * @return precedent la valeur de l'état précédent
	 */
	public Etat getPrecedent() {return this.precedent;}

	private List<Etat> successeurs = new ArrayList<Etat>();
	/**
	 * @param successeurs l'attribut successeurs à affecter
	 */
	public void setSuccesseurs(List<Etat> successeurs) {this.successeurs = successeurs;}
	/**
	 * @return successeurs l'attribut successeurs à affecter
	 */
	public List<Etat> getSuccesseurs() {return successeurs;}
		
	private Comportement comportement=null;
	/**
	 * @param comportementAEssayer
	 */
	public void setComportement(Comportement comportement) {this.comportement=comportement;}
	/**
	 * @return comportementAEssayer
	 */
	public Comportement getComportement() {return this.comportement;}
	
	private List<AgentModifie> listeModifications = new ArrayList<AgentModifie>();
	/**
	 * @param listeModifications liste des modifications effectuées pour arriver depuis l'état précédent à cet état
	 */
	public void setListeModifications(List<AgentModifie> listeModifications) {this.listeModifications = listeModifications;}
	/**
	 * @return listeModifications liste des modifications effectuées pour arriver depuis l'état précédent à cet état
	 */
	public List<AgentModifie> getListeModifications() {return listeModifications;}

	private ElementRepresentation representationAssociee = null;
	/**
	 * @param representationAssociee la représentation associée à cet état
	 */
	public void setRepresentationAssociee(ElementRepresentation representationAssociee) {this.representationAssociee = representationAssociee;}
	/**
	 * @return la valeur de la représentation associée à cet état
	 */
	public ElementRepresentation getRepresentationAssociee() {return representationAssociee;}
	
	private int nbEtatsSuccesseurs = 0;
	/**
	 * @param nbEtatsSuccesseurs l'attribut nbEtatsSuccesseurs à affecter
	 */
	public void setNbEtatsSuccesseurs(int nbEtatsSuccesseurs) {this.nbEtatsSuccesseurs = nbEtatsSuccesseurs;}
	/**
	 * @return la valeur de l'attribut nbEtatsSuccesseurs
	 */
	public int getNbEtatsSuccesseurs() {return nbEtatsSuccesseurs;}

}
