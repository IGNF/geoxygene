/*
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at
 * the Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or any later
 * version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library (see file LICENSE if present); if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.feature.type;


import java.util.List;


/**
 * @author Balley
 *
 * GF_AttributeType proposé par le General Feature Model de la norme ISO19109.
 * Nous y ajoutons le lien avec FC_FeatureAttributeValue defini dans la norme ISO 19110
 * pour preciser les domaines de valeurs énumérés.
 */
public interface GF_AttributeType extends GF_PropertyType{

	/** Renvoie le type de l'attribut. */
	public String getValueType ();
	/** Affecte un type à l'attribut. */
	public void setValueType(String ValueType);

	/** Renvoie le domaine de valeur. */
	public String getDomainOfValues ();
	/** Affecte un domaine de valeur. */
	public void setDomainOfValues(String DomainOfValues);



	/** Renvoie le nombre de valeurs minimal de l'attribut. */
	public int getCardMin ();
	/** Affecte un nombre de valeurs minimal à l'attribut. */
	public void setCardMin (int CardMin);

	/** Renvoie le nombre de valeurs maximal de l'attribut. */
	public int getCardMax ();
	/** Affecte un nombre de valeurs maximal à l'attribut. */
	public void setCardMax (int CardMax);

	/** Renvoie l'attribut que self caractérise. */
	public GF_AttributeType getCharacterize();
	/** Affecte un attribut que self caractérise. */
	public void setCharacterize(GF_AttributeType Characterize);

	/** Renvoie le type de domaine de valeur de l'attribut.
	 * (FALSE pour non énuméré, TRUE pour énuméré
	 * C'est une extension du GFM proposée par la norme ISO 19110
	 **/
	public boolean getValueDomainType();
	/** Affecte un type à l'attribut. */
	public void setValueDomainType(boolean ValueDomainType);

	/** Renvoie les valeurs possibles d'un attribut enumere. */
	public List<FC_FeatureAttributeValue> getValuesDomain();

	/** Renvoie les valeurs possibles d'un attribut enumere sous forme de liste de
	 * chaines de caractères.
	 * Peut servir avantageusement à affecter une valeur à l'attribut
	 * domainOfValues proposé par le GFM
	 **/
	public List<String> getValuesDomainToString();
	/** Affecte une liste de valeurs possibles. */
	public void setValuesDomain(List<FC_FeatureAttributeValue> ValuesDomain);
	/** Ajoute une valeur à la liste des valeurs possibles de l'attribut. */
	public void addValuesDomain(FC_FeatureAttributeValue value);
	/** Enleve une valeur a la liste des valeurs possibles de l'attribut. */
	public void removeValuesDomain(FC_FeatureAttributeValue value);


	/**renvoie la définition semantique de l'AttributeType (sous la forme d'un String
	 * ou d'une classe d'ontologie)
	 * ou un pointeur vers cette définition (sous la forme d'une URI)
	 *
	 * Correspond à FC_DescriptionReference et FC_DescriptionSource proposés
	 * dans la norme ISO19110
	 * @return Object
	 */
	public Object getDefinitionReference();
	public void setDefinitionReference(Object ref);
}
