/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.style;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Julien Perret
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class FeatureTypeStyle {

  @XmlElement(name = "Rule")
  private List<Rule> rules = new ArrayList<Rule>();

  /**
   * Renvoie la valeur de l'attribut rules.
   * @return la valeur de l'attribut rules
   */
  public List<Rule> getRules() {
    return this.rules;
  }

  /**
   * Affecte la valeur de l'attribut rules.
   * @param rules l'attribut rules à affecter
   */
  public void setRules(List<Rule> rules) {
    this.rules = rules;
  }

  private String name;

  /**
   * Renvoie la valeur de l'attribut name.
   * @return la valeur de l'attribut name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Affecte la valeur de l'attribut name.
   * @param name l'attribut name à affecter
   */
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    String result = "FeatureTypeStyle " + this.getName() + "\n"; //$NON-NLS-1$//$NON-NLS-2$
    for (Rule rule : this.getRules()) {
      result += "\t" + rule + "\n"; //$NON-NLS-1$//$NON-NLS-2$
    }
    return result;
  }

  public Symbolizer getSymbolizer() {
    return this.getRules().get(0).getSymbolizers().get(0);
  }
}
