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
public class UserStyle extends AbstractStyle {

  @XmlElement(name = "FeatureTypeStyle")
  private List<FeatureTypeStyle> featureTypeStyles = new ArrayList<FeatureTypeStyle>();

  /**
   * Renvoie la valeur de l'attribut featureTypeStyles.
   * @return la valeur de l'attribut featureTypeStyles
   */
  @Override
  public List<FeatureTypeStyle> getFeatureTypeStyles() {
    return this.featureTypeStyles;
  }

  /**
   * Affecte la valeur de l'attribut featureTypeStyles.
   * @param featureTypeStyles l'attribut featureTypeStyles à affecter
   */
  public void setFeatureTypeStyles(List<FeatureTypeStyle> featureTypeStyles) {
    this.featureTypeStyles = featureTypeStyles;
  }

  /**
   * Title of the style.
   */
  @XmlElement(name = "Title", required = false)
  private String title;

  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  /**
   * Abstract description of the style.
   */
  @XmlElement(name = "Abstract", required = false)
  private String abstractDescription;

  public String getAbstractDescription() {
    return abstractDescription;
  }

  public void setAbstractDescription(String abstractDescription) {
    this.abstractDescription = abstractDescription;
  }

  /**
   * True if the style is the default style of the layer.
   */
  @XmlElement(name = "IsDefault", required = false)
  private boolean defaultStyle = false;
  public boolean isDefaultStyle() {
    return defaultStyle;
  }

  public void setDefaultStyle(boolean defaultStyle) {
    this.defaultStyle = defaultStyle;
  }

  @Override
  public boolean isUserStyle() {
    return true;
  }

  @Override
  public String toString() {
    String result = "UserStyle " + this.getName() + "\n"; //$NON-NLS-1$//$NON-NLS-2$
    for (FeatureTypeStyle fts : this.getFeatureTypeStyles()) {
      result += "\t" + fts + "\n"; //$NON-NLS-1$//$NON-NLS-2$
    }
    return result;
  }

  @Override
  public Symbolizer getSymbolizer() {
    return this.featureTypeStyles.get(0).getSymbolizer();
  }
}
