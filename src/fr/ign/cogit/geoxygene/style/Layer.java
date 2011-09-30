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

import java.util.Collection;
import java.util.List;

import javax.swing.ImageIcon;
import javax.xml.bind.annotation.XmlTransient;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

/**
 * The Layer class.
 * @see NamedLayer
 * @see UserLayer
 * @author Julien Perret
 */
@XmlTransient
public interface Layer {
  /**
   * Renvoie la valeur de l'attribut name.
   * @return la valeur de l'attribut name
   */
  public String getName();

  /**
   * Affecte la valeur de l'attribut name.
   * @param name l'attribut name à affecter
   */
  public void setName(String name);

  public String getDescription();

  public void setDescription(String description);

  /**
   * Renvoie la valeur de l'attribut styles.
   * @return la valeur de l'attribut styles
   */
  public List<Style> getStyles();

  /**
   * Affecte la valeur de l'attribut styles.
   * @param styles l'attribut styles à affecter
   */
  public void setStyles(List<Style> styles);

  public List<Style> getActiveStyles();

  /**
   * @return les features de la couche
   */
  public IFeatureCollection<? extends IFeature> getFeatureCollection();

  /**
   * @return <code>true</code> if the layer is visible in the LayerViewPanel by
   *         the user; <code>false</code> otherwise.
   */
  public boolean isVisible();

  /**
   * @param visible
   */
  public void setVisible(boolean visible);

  /**
   * @return <code>true</code> if the layer is selectable by the user;
   *         <code>false</code> otherwise.
   */
  public boolean isSelectable();

  /**
   * @param selectable
   */
  public void setSelectable(boolean selectable);

  /**
   * @return <code>true</code> if the layer is symbolized; <code>false</code>
   *         otherwise.
   */
  public boolean isSymbolized();

  /**
   * @param symbolized
   */
  public void setSymbolized(boolean symbolized);

  public Symbolizer getSymbolizer();
  
  public String getActiveGroup();

  public void setActiveGroup(String activeGroup);

  public Collection<String> getGroups();

  public void setCRS(CoordinateReferenceSystem crs);

  public CoordinateReferenceSystem getCRS();

  void destroy();

  public ImageIcon getIcon();

  public void setIcon(ImageIcon image);
}
