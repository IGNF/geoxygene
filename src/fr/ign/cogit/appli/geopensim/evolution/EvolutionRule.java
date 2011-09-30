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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.agent.AgentGeographique;
import fr.ign.cogit.appli.geopensim.evolution.EvolutionRuleUnitaire.Possibilites;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;

/**
 * @author Julien Perret
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="EvolutionRule")
public class EvolutionRule {
	static Logger logger=Logger.getLogger(EvolutionRule.class.getName());

	@XmlAttribute
	String Nom;
	/**
	 * @return la valeur de l'attribut name
	 */
	public String getNom() {return this.Nom;}
	/**
	 * @param name l'attribut name à affecter
	 */
	public void setNom(String nom) {this.Nom = nom;}
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
	String propertyName = null;
	/**
	 * @return la valeur de l'attribut propertyName
	 */
	public String getPropertyName() {return this.propertyName;}
	/**
	 * @param propertyName l'attribut propertyName à affecter
	 */
	public void setPropertyName(String propertyName) {this.propertyName = propertyName;}
	@XmlElement(name = "subRules")
	List<EvolutionRuleUnitaire> subRules = new ArrayList<EvolutionRuleUnitaire>(0);
    /**
	 * @return la valeur de l'attribut end
	 */
	public List<EvolutionRuleUnitaire> getListeEvolutionRuleUnitaire() {return this.subRules;}
	/**
	 * @param end l'attribut end à affecter
	 */
	public void setListeEvolutionRuleUnitaire(List<EvolutionRuleUnitaire> subRules) {this.subRules = subRules;}
//	@XmlElements({
//        @XmlElement(name = "PropertyIsEqualTo", type = PropertyIsEqualTo.class),
//        @XmlElement(name = "PropertyIsGreaterThan", type = PropertyIsGreaterThan.class),
//        @XmlElement(name = "PropertyIsGreaterThanOrEqualTo", type = PropertyIsGreaterThanOrEqualTo.class),
//        @XmlElement(name = "PropertyIsLessThan", type = PropertyIsLessThan.class),
//        @XmlElement(name = "PropertyIsLessThanOrEqualTo", type = PropertyIsLessThanOrEqualTo.class),
//        @XmlElement(name = "PropertyIsNotEqualTo", type = PropertyIsNotEqualTo.class),
//        @XmlElement(name = "And", type = And.class),
//        @XmlElement(name = "Or", type = Or.class),
//        @XmlElement(name = "Not", type = Not.class)
//    })
//	@XmlElementWrapper(name="precondition")
//	Filter[] filter = new Filter[1];
//	/**
//	 * @return la valeur de l'attribut filter
//	 */
//	public Filter[] getFilter() {return this.filter;}
//	/**
//	 * @param filter l'attribut filter à affecter
//	 */
//	public void setFilter(Filter[] filter) {this.filter = filter;}
//	
//	float probability = 1f;
//	/**
//	 * @return la valeur de l'attribut probability
//	 */
//	public float getProbability() {return this.probability;}
//	/**
//	 * @param probability l'attribut probability à affecter
//	 */
//	public void setProbability(float probability) {this.probability = probability;}
//	
	
//	@XmlElementRef
//	Expression expression = null;
//	/**
//	 * @return la valeur de l'attribut expression
//	 */
//	public Expression getExpression() {return this.expression;}
//	/**
//	 * @param expression l'attribut expression à affecter
//	 */
//	public void setExpression(Expression expression) {this.expression = expression;}
	public boolean apply(FT_Feature feature) {
	    if (logger.isTraceEnabled()) {
	        logger.trace("apply rule "+this.getNom()+ " to feature "+feature.getClass().getSimpleName());
	    }
	    if (!(feature instanceof AgentGeographique)) {
	        return false;
	    }
	    AgentGeographique agent = (AgentGeographique) feature;
        if (logger.isTraceEnabled()) {
            logger.trace("apply rule "+this.getNom()+ " to agent type "+agent.getRepresentationClassString());
        }
	    if (!this.type.equalsIgnoreCase(agent.getRepresentationClass().getSimpleName())) {
	        return false;
	    }
	    if (logger.isTraceEnabled()) {
	        logger.trace("apply rule "+this.getNom()+ " to feature "+feature.getClass().getSimpleName());
	    }
	    int comp = 0;
	    for (EvolutionRuleUnitaire sr:this.getListeEvolutionRuleUnitaire()){
	    	logger.trace(++comp + " - " + sr.getPossib().size());
            boolean applies = (sr.filter == null) || sr.filter.length == 0
                    || (sr.filter[0] == null) ? true : sr.filter[0]
                    .evaluate(feature);
	    	if (applies) { 
	    		List<Possibilites> listePossib = sr.getPossib();
	    		Possibilites bestPossib = null;
	    		if (listePossib.size() == 1) {
	    		  bestPossib = listePossib.get(0);
	    		} else {
	    		  double random = Math.random();
	    		  logger.trace(random);
	    		  double frequenceCumulee = 0;
	    		  double frequenceCumuleePrecedente = 0;
	    		  for (Possibilites possib : listePossib) {
	    		    frequenceCumuleePrecedente = frequenceCumulee;
	    		    frequenceCumulee += possib.getProbability();
	    		    if ((random > frequenceCumuleePrecedente)
	    		        && (random < frequenceCumulee)) {
	    		      bestPossib = possib;
	    		      break;
	    		    }
	    		  }
	    		}
	    		if (bestPossib!=null) { 
	    			Object attribute = feature.getAttribute(bestPossib.getPropertyName());
	    			//logger.info(propertyName+" = "+attribute);
	    			AttributeType at = new AttributeType();
	    			at.setMemberName(bestPossib.getPropertyName());
	    			at.setNomField(bestPossib.getPropertyName());
	    			at.setValueType(attribute.getClass().getSimpleName());
	    			Object value = bestPossib.getExpression().evaluate(feature);
	    			//logger.info(propertyName+" final = "+value+" "+attribute.getClass());
	    			if (value instanceof Number) {
	    				if(attribute instanceof Double){
	    				  double targetValue = ((Number)value).doubleValue();
	    				  if (bestPossib.getPropertyName().equalsIgnoreCase("densiteBut")) {
	    				    // check if the target density value is higher thant the current one
	    				    double currentValue = ((Number)feature.getAttribute("densite")).doubleValue();
	    				    if (targetValue > currentValue) {
	    				      feature.setAttribute(at, targetValue);
	    				    } else {
	    				      logger.debug("Did not apply the target value " + targetValue + " smaller than the current " + currentValue);
	    				      return true;
	    				    }
	    				  } else {
	    					feature.setAttribute(at, targetValue);
	    				  }
	    				}else if(attribute instanceof Integer){
	    					feature.setAttribute(at, ((Number)value).intValue());
	    				}
	    			} else {
	    				feature.setAttribute(at, value);
	    			}
	    			attribute = feature.getAttribute(bestPossib.getPropertyName());
	    			//logger.info(propertyName+" really final = "+attribute);
	    			//return applies;
	    			return true;
	    		}
	    	}
	    }
	    return false;
	}
}
