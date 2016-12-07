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

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.filter.PropertyIsEqualTo;
import fr.ign.cogit.geoxygene.filter.expression.Literal;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;

/**
 * Utility class with static methods to manipulate a StyledLayerDescriptor
 * object.
 * @author GTouya
 *
 */
public class SLDUtil {

  /**
   * Removes from a SLD, for each layer, all the styles with a given group name.
   * @param sld
   * @param styleName
   */
  public static void removeGroupNamedStyles(StyledLayerDescriptor sld,
      String groupName) {
    for (Layer layer : sld.getLayers()) {
      Set<Style> layerStyles = new HashSet<Style>(layer.getStyles());
      for (Style style : layerStyles) {
        if (groupName.equals(style.getGroup()))
          layer.getStyles().remove(style);
      }
    }
  }

  /**
   * Removes from a SLD, for each layer, all the styles with a given name.
   * @param sld
   * @param styleName
   */
  public static void removeNamedStyles(StyledLayerDescriptor sld, String name) {
    for (Layer layer : sld.getLayers()) {
      Set<Style> layerStyles = new HashSet<Style>(layer.getStyles());
      for (Style style : layerStyles) {
        if (name.equals(style.getName())) {
          layer.getStyles().remove(style);
        }
      }
    }
  }

  /**
   * Add a rule in the SLD specific to the given feature, using the id of the
   * feature.
   * @param style
   * @param feature
   * @param ruleName
   * @param symbolizer the symbolizer to use in the new rule
   */
  public static void addFeatureRule(Style style, IFeature feature,
      String ruleName, Symbolizer symbolizer) {
    Rule rule = new Rule();
    // create the Filter for the feature id
    PropertyIsEqualTo filter = new PropertyIsEqualTo();
    filter.setPropertyName(new PropertyName("id"));
    Literal value = new Literal();
    value.setValue(String.valueOf(feature.getId()));
    filter.setLiteral(value);
    rule.setFilter(filter);
    rule.setName(ruleName);
    rule.getSymbolizers().add(symbolizer);
    FeatureTypeStyle ftStyle = new FeatureTypeStyle();
    ftStyle.getRules().add(rule);

    // now add the new rule to the layer styles
    style.getFeatureTypeStyles().add(ftStyle);
  }
}
