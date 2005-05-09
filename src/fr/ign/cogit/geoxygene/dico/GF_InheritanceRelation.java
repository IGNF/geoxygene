/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework compliant with OGC/ISO specifications for 
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
 *  
 */

package fr.ign.cogit.geoxygene.dico;

import java.util.ArrayList;
import java.util.List;


/**
  * Classe générique pour traduire les relations d'héritage (généralisation / spécialisation).
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */

public class GF_InheritanceRelation {
    
    
    /** Identifiant. */
    protected int id;
    /** Renvoie l'identifiant. */
    public int getId () {return this.id;}
    /** Affecte un identifiant. */
    public void setId (int Id) {this.id = Id;}
    
    
    /**Nom de la généralisation ou de la spécialisation. */
    protected String name;
    /** Renvoie le nom. */
    public String getName () {return this.name;}
    /** Affecte un nom. */
    public void setName(String Name) {this.name = Name;}

    
    /** Description.*/
    protected String description;  
    /** Renvoie la description. */
    public String getDescription () {return this.description;}
    /** Affecte une description. */
    public void setDescription (String Description) {this.description = Description;}
  
    
    /** TRUE si une instance de l'hyperclasse doit être au plus dans une sous-classe, FALSE sinon. */
    protected boolean uniqueInstance;
    /** Renvoie l'attribut uniqueInstance. */
    public boolean getUniqueInstance () {return this.uniqueInstance;}
    /** Affecte l'attribut uniqueInstance.. */
    public void setUniqueInstance (boolean UniqueInstance) {this.uniqueInstance = UniqueInstance;}

    
    /** Les classes meres de la relation d'héritage. */
    protected List superType = new ArrayList();
	/** Renvoie les classes mere de la relation d'héritage. */
    public List getSuperType() { return superType; }
	/** Affecte une liste de classes meres    */
	public void setSuperType(List L) {this.superType = L;}
	/** Nombre de classes meres de la relation d'héritage. */
	public int sizeSuperType() { return this.superType.size(); }
	/** Ajoute une classe mere à la relation d'héritage. */
	public void addSuperType (GF_FeatureType featureType) {
		superType.add(featureType);
		if (!featureType.getSpecialization().contains(this))
			featureType.addSpecialization(this);
	}
			
		
    /** Classe fille de la relation d'heritage. */    
	protected GF_FeatureType subType;
	/** Renvoie la classe fille de la relation d'héritage. */
    public GF_FeatureType getSubType() { return subType; }
	/** Affecte une classe fille à la relation d'héritage. */
    public void setSubType(GF_FeatureType SubType) {
		this.subType = SubType;
		if (!SubType.getGeneralization().contains(this))
			SubType.addGeneralization(this);
    }
   
}
