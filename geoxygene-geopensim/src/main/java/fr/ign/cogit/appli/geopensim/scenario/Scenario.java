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

package fr.ign.cogit.appli.geopensim.scenario;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.agent.AgentGeographique;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
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
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;

/**
 * @author Julien Perret
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="Scenario")
public class Scenario {
	static Logger logger=Logger.getLogger(Scenario.class.getName());

	@XmlAttribute
	String type;
	/**
	 * @return la valeur de l'attribut type
	 */
	public String getType() {return this.type;}
	/**
	 * @param type l'attribut type à affecter
	 */
	public void setType(String type) {this.type = type;}
	int start;
	/**
	 * @return la valeur de l'attribut start
	 */
	public int getStart() {return this.start;}
	/**
	 * @param start l'attribut start à affecter
	 */
	public void setStart(int start) {this.start = start;}
	int end;
    /**
	 * @return la valeur de l'attribut end
	 */
	public int getEnd() {return this.end;}
	/**
	 * @param end l'attribut end à affecter
	 */
	public void setEnd(int end) {this.end = end;}
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
	Filter[] filter = new Filter[1];
	/**
	 * @return la valeur de l'attribut filter
	 */
	public Filter[] getFilter() {return this.filter;}
	/**
	 * @param filter l'attribut filter à affecter
	 */
	public void setFilter(Filter[] filter) {this.filter = filter;}
	
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
	public boolean apply(FT_Feature feature) {
	    if (logger.isTraceEnabled()) {
	        logger.info("apply rule "+this.type+ " to feature "+feature.getClass().getSimpleName());
	    }
	    if (!(feature instanceof AgentGeographique)) {
	        return false;
	    }
	    AgentGeographique agent = (AgentGeographique) feature;
        if (logger.isTraceEnabled()) {
            logger.trace("apply rule "+this.type+ " to agent type "+agent.getRepresentationClassString());
        }
	    if (!this.type.equalsIgnoreCase(agent.getRepresentationClass().getSimpleName())) {
	        return false;
	    }
		boolean applies = (filter[0] == null) ? true : filter[0].
		        evaluate(feature);
		if (!applies) { return applies; }
		double random = Math.random();
		if (random > probability) { return false; }
		Object attribute = feature.getAttribute(propertyName);
		//logger.info(propertyName+" = "+attribute);
		AttributeType at = new AttributeType();
		at.setMemberName(propertyName);
		at.setNomField(propertyName);
		at.setValueType(attribute.getClass().getSimpleName());
		Object value = expression.evaluate(feature);
		//logger.info(propertyName+" final = "+value+" "+attribute.getClass());
		if (value instanceof Number) {
			if(attribute instanceof Double){
			    feature.setAttribute(at, ((Number)value).doubleValue());
			}else if(attribute instanceof Integer){
			    feature.setAttribute(at, ((Number)value).intValue());
			}
		} else {
		    feature.setAttribute(at, value);
		}
        attribute = feature.getAttribute(propertyName);
        //logger.info(propertyName+" really final = "+attribute);
		return applies;
	}
}
