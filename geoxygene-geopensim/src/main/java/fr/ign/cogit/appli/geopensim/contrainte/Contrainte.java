/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.appli.geopensim.contrainte;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.agent.AgentGeographique;
import fr.ign.cogit.appli.geopensim.comportement.Comportement;
import fr.ign.cogit.geoxygene.filter.expression.Expression;

/**
 * @author Julien Perret
 * 
 */
@Entity
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Contrainte")
public class Contrainte {
  private static Logger logger = Logger.getLogger(Contrainte.class.getName());
  protected int id;

  /**
   * @return
   */
  @Id
  @GeneratedValue
  public int getId() {
    return id;
  }

  /**
   * @param id
   */
  public void setId(int id) {
    this.id = id;
  }

  @XmlElementRefs( { @XmlElementRef })
  @XmlElementWrapper(name = "Expression")
  private Expression[] expression = new Expression[1];

  /**
   * Renvoie la valeur de l'attribut expression.
   * @return la valeur de l'attribut expression
   */
  public Expression getExpression() {
    return (this.expression == null) ? null : this.expression[0];
  }

  /**
   * Affecte la valeur de l'attribut expression.
   * @param expression l'attribut expression à affecter
   */
  public void setExpression(Expression expression) {
    this.expression[0] = expression;
  }

  /**
   * @return la satisfaction de la contrainte
   */
  @Transient
  public double getSatisfaction(AgentGeographique agent) {
    if (logger.isDebugEnabled()) {
      logger.debug(this.getId() + " " + this.getExpression());
    }
    return Math.min(((BigDecimal) this.getExpression().evaluate(agent))
        .doubleValue(), 100);
  }
  protected double importance;
  /**
   * @return l'importance de la contrainte.
   */
  public double getImportance() {
    return this.importance;
  }
  /**
   * Affecte l'importance de la contrainte.
   * @param importance l'importance de la contrainte.
   */
  public void setImportance(double importance) {
    this.importance = importance;
  }
  protected int priorite = 1;
  /**
   * Renvoie la priorité de la contrainte.
   * @return la priorité de la contrainte.
   */
  public int getPriorite() {
    return this.priorite;
  }
  /**
   * Affecte la priorité de la contrainte.
   * @param priorite la priorité de la contrainte.
   */
  public void setPriorite(int priorite) {
    this.priorite = priorite;
  }
  public Contrainte() {
  }
  /**
   * Contructeur à partir de l'importance et de la priorité de la contrainte
   * pour l'agent.
   * @param importance importance de la contrainte pour l'agent
   * @param priorite priorité de la contrainte pour l'agent
   */
  public Contrainte(double importance, int priorite) {
    this.importance = importance;
    this.priorite = priorite;
  }
  // @XmlElement(name="Comportement")
  @XmlJavaTypeAdapter(ComportementListAdapter.class)
  ArrayList<Comportement> comportements = new ArrayList<Comportement>();
  /**
   * Propose des comportements à l'agent portant la contrainte.
   */
  public ArrayList<Comportement> getComportements() {
    return this.comportements;
  }
  /**
   * Affecte la valeur de l'attribut comportements.
   * @param comportements l'attribut comportements à affecter
   */
  public void setComportements(ArrayList<Comportement> comportements) {
    this.comportements = comportements;
  }
  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " - importance = "
    + this.getImportance() + " - priorité = " + this.getPriorite()
    + " - expression = " + this.getExpression() + " - "
    + this.getComportements().size() + " contraintes";
  }
}
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
class ClassArrayList {
  @SuppressWarnings("unchecked")
  @XmlElement(name = "Comportement")
  public ArrayList<Class> classes = new ArrayList<Class>();
}

@SuppressWarnings("unchecked")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
class ComportementListAdapter extends XmlAdapter<ClassArrayList, ArrayList> {
  @Override
  public ClassArrayList marshal(ArrayList v) throws Exception {
    ClassArrayList list = new ClassArrayList();
    for (Object o : v) {
      list.classes.add(o.getClass());
      // System.out.println("o = "+o.getClass());
    }
    return list;
  }
  @Override
  public ArrayList unmarshal(ClassArrayList v) throws Exception {
    ArrayList list = new ArrayList();
    for (Class c : v.classes) {
      // System.out.println("c = "+c);
      list.add(c.newInstance());
    }
    return list;
  }
}
