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
  * Classe mère pour les métaclasses de propriétés définissant les caractéristiques 
  * des classes géographiques Feature Types ou des associations Association Types.
  * (opérations, attributs, rôles dans une associations).
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */

abstract public class GF_PropertyType {


    /** Identifiant. */
    protected int id;  
    /** Renvoie l'identifiant. */
    public int getId() {return this.id;}    
    /** Affecte un identifiant. */
    public void setId(int Id) {this.id = Id;}


    /** Feature type auquel est rattaché la propriété. */
    protected GF_FeatureType featureType;
    /** Renvoie le feature type auquel est rattaché la propriété. */
    public GF_FeatureType getFeatureType() {return this.featureType;}
    /** Affecte un feature type à la propriété. */
    public void setFeatureType(GF_FeatureType FeatureType) {
		this.featureType = FeatureType;
        if (!FeatureType.getProperties().contains(this))
        	FeatureType.addProperty(this);  // gestion de la bi-direction
    }
    
    
    /** Nom de la propriété. */
    protected String memberName;
    /** Renvoie le nom de la propriété. */
    public String getMemberName () {return this.memberName;}
    /** Affecte un nom de propriété. */
    public void setMemberName (String MemberName) {this.memberName = MemberName;}
    
    
    /** Définition. */
    protected String definition;
    /** Renvoie la définition. */
    public String getDefinition () {return this.definition;}
    /** Affecte une définition. */
    public void setDefinition (String Definition) {this.definition = Definition;} 
    
    
    /** Les contraintes. */
    protected List constraint = new ArrayList();    
    /** Renvoie la liste des contraintes. */
    public List getConstraint() { return this.constraint; }
	/** Affecte une liste de contraintes */
	public void setConstraint (List L) {this.constraint = L;}        
    /** Ajoute une contrainte.*/
    public void addConstraint (GF_Constraint value) {this.constraint.add(value); }
    /** Renvoie le nombre de contraintes. */
    public int sizeConstraint () { return this.constraint.size(); }
    
}
