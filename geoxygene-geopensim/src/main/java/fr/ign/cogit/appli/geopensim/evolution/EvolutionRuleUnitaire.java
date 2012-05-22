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

package fr.ign.cogit.appli.geopensim.evolution;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.filter.And;
import fr.ign.cogit.geoxygene.filter.Filter;
import fr.ign.cogit.geoxygene.filter.Not;
import fr.ign.cogit.geoxygene.filter.Or;
import fr.ign.cogit.geoxygene.filter.PropertyIsEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsGreaterThan;
import fr.ign.cogit.geoxygene.filter.PropertyIsGreaterThanOrEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsLessThan;
import fr.ign.cogit.geoxygene.filter.PropertyIsLessThanOrEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsNotEqualTo;
import fr.ign.cogit.geoxygene.filter.expression.Expression;

/**
 * @author Julien Perret
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="SubRule")
public class EvolutionRuleUnitaire {
	static Logger logger=Logger.getLogger(EvolutionRuleUnitaire.class.getName());

	@XmlElements({
		@XmlElement(name = "PropertyIsEqualTo", type = PropertyIsEqualTo.class),
		@XmlElement(name = "PropertyIsGreaterThan", type = PropertyIsGreaterThan.class),
		@XmlElement(name = "PropertyIsGreaterThanOrEqualTo", type = PropertyIsGreaterThanOrEqualTo.class),
		@XmlElement(name = "PropertyIsLessThan", type = PropertyIsLessThan.class),
		@XmlElement(name = "PropertyIsLessThanOrEqualTo", type = PropertyIsLessThanOrEqualTo.class),
		@XmlElement(name = "PropertyIsNotEqualTo", type = PropertyIsNotEqualTo.class),
		@XmlElement(name = "And", type = And.class),
		@XmlElement(name = "Or", type = Or.class),
		@XmlElement(name = "Not", type = Not.class)
	})
	@XmlElementWrapper(name="precondition")
	Filter[] filter = null;//new Filter[1];
	/**
	 * @return la valeur de l'attribut filter
	 */
	public Filter[] getFilter() {return this.filter;}
	/**
	 * @param filter l'attribut filter à affecter
	 */
	public void setFilter(Filter[] filter) {this.filter = filter;}

	List<Possibilites> possib = new ArrayList<Possibilites>();
	/**
	 * @return la valeur de l'attribut filter
	 */
	public List<Possibilites> getPossib() {return this.possib;}
	/**
	 * @param filter l'attribut filter à affecter
	 */
	public void setPossib(List<Possibilites> possib) {this.possib = possib;}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Possibilites  {
		float probability = 1f;
		/**
		 * @return la valeur de l'attribut probability
		 */
		public float getProbability() {return this.probability;}
		/**
		 * @param probability l'attribut probability à affecter
		 */
		public void setProbability(float probability) {this.probability = probability;}

		String propertyName = null;
		/**
		 * @return la valeur de l'attribut propertyName
		 */
		public String getPropertyName() {return this.propertyName;}
		/**
		 * @param propertyName l'attribut propertyName à affecter
		 */
		public void setPropertyName(String propertyName) {this.propertyName = propertyName;}
		@XmlElementRef
		Expression expression = null;
		/**
		 * @return la valeur de l'attribut expression
		 */
		public Expression getExpression() {return this.expression;}
		/**
		 * @param expression l'attribut expression à affecter
		 */
		public void setExpression(Expression expression) {this.expression = expression;}

	}

}
