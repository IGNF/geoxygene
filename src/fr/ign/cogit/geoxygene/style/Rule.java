/**
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
 * 
 */

package fr.ign.cogit.geoxygene.style;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import fr.ign.cogit.geoxygene.filter.And;
import fr.ign.cogit.geoxygene.filter.ElseFilter;
import fr.ign.cogit.geoxygene.filter.ElseFilterImpl;
import fr.ign.cogit.geoxygene.filter.Filter;
import fr.ign.cogit.geoxygene.filter.Not;
import fr.ign.cogit.geoxygene.filter.Or;
import fr.ign.cogit.geoxygene.filter.PropertyIsEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsGreaterThan;
import fr.ign.cogit.geoxygene.filter.PropertyIsGreaterThanOrEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsLessThan;
import fr.ign.cogit.geoxygene.filter.PropertyIsLessThanOrEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsNotEqualTo;
import fr.ign.cogit.geoxygene.style.thematic.ThematicSymbolizer;

/**
 * @author Julien Perret
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "name", "title", "description",
    "legendGraphic",
    "filter", "elseFilter", "minScaleDenominator", "maxScaleDenominator",
    "symbolizers" })
@XmlRootElement(name = "Rule")
public class Rule {

  @XmlElement(name = "Name")
  protected String name;
  @XmlElement(name = "Description")
  protected String description;
  @XmlElement(name = "LegendGraphic")
  protected LegendGraphic legendGraphic = null;
  public LegendGraphic getLegendGraphic() {
    return this.legendGraphic;
  }
  public void setLegendGraphic(LegendGraphic legendGraphic) {
    this.legendGraphic = legendGraphic;
  }

  // protected FilterType filter;
  @XmlElement(name = "MinScaleDenominator")
  protected Double minScaleDenominator;
  @XmlElement(name = "MaxScaleDenominator")
  protected Double maxScaleDenominator;

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

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String d) {
    this.description = d;
  }

  @XmlElement(name = "Title")
  private String title;
  public String getTitle() {
    return this.title;
  }
  public void setTitle(String title) {
    this.title = title;
  }

  @XmlElements( {
      @XmlElement(name = "PropertyIsEqualTo", type = PropertyIsEqualTo.class),
      @XmlElement(name = "PropertyIsGreaterThan", type = PropertyIsGreaterThan.class),
      @XmlElement(name = "PropertyIsGreaterThanOrEqualTo", type = PropertyIsGreaterThanOrEqualTo.class),
      @XmlElement(name = "PropertyIsLessThan", type = PropertyIsLessThan.class),
      @XmlElement(name = "PropertyIsLessThanOrEqualTo", type = PropertyIsLessThanOrEqualTo.class),
      @XmlElement(name = "PropertyIsNotEqualTo", type = PropertyIsNotEqualTo.class),
      @XmlElement(name = "And", type = And.class),
      @XmlElement(name = "Or", type = Or.class),
      @XmlElement(name = "Not", type = Not.class) })
  @XmlElementWrapper(name = "Filter")
  // @XmlElement(name = "Filter", namespace = "http://www.opengis.net/ogc")
  private Filter[] filter = null;

  /**
   * Renvoie la valeur de l'attribut filter.
   * @return la valeur de l'attribut filter
   */
  public Filter getFilter() {
    return (this.filter == null) ? null : this.filter[0];
  }

  /**
   * Affecte la valeur de l'attribut filter.
   * @param filter l'attribut filter à affecter
   */
  public void setFilter(Filter filter) {
    if (this.filter == null) {
      this.filter = new Filter[1];
    }
    this.filter[0] = filter;
  }

  @XmlElement(name = "ElseFilter", type = ElseFilterImpl.class)
  protected ElseFilter elseFilter = null;

  // public void setElseFilter(ElseFilter elseFilter) {this.elseFilter =
  // elseFilter;}
  // public ElseFilter getElseFilter() {return elseFilter;}

  // @XmlElementRef(name = "symbolizers", namespace =
  // "http://www.opengis.net/sld", type = Symbolizer.class)
  // protected List<JAXBElement<? extends SymbolType>> symbol;
  @XmlElements( {
      @XmlElement(name = "LineSymbolizer", type = LineSymbolizer.class),
      @XmlElement(name = "PointSymbolizer", type = PointSymbolizer.class),
      @XmlElement(name = "PolygonSymbolizer", type = PolygonSymbolizer.class),
      @XmlElement(name = "RasterSymbolizer", type = RasterSymbolizer.class),
      @XmlElement(name = "TextSymbolizer", type = TextSymbolizer.class),
      @XmlElement(name = "ThematicSymbolizer", type = ThematicSymbolizer.class)})
  private List<Symbolizer> symbolizers = new ArrayList<Symbolizer>(0);
  /**
   * Renvoie la valeur de l'attribut symbolizers.
   * @return la valeur de l'attribut symbolizers
   */
  public List<Symbolizer> getSymbolizers() {
    return this.symbolizers;
  }

  /**
   * Affecte la valeur de l'attribut symbolizers.
   * @param symbolizers l'attribut symbolizers à affecter
   */
  public void setSymbolizers(List<Symbolizer> symbolizers) {
    this.symbolizers = symbolizers;
  }

  @Override
  public String toString() {
    String result = "Rule " + this.getName() //$NON-NLS-1$
        + "\n"; //$NON-NLS-1$
    result += "\tFilter " + this.getFilter() //$NON-NLS-1$
        + "\n"; //$NON-NLS-1$
    for (Symbolizer symbolizer : this.getSymbolizers()) {
      result += "\t" + symbolizer + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    return result;
  }
}
