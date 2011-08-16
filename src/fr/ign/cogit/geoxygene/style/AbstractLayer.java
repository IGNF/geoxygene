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

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;

import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author Julien Perret
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractLayer implements Layer {

  @XmlElement(name = "Name", required = true)
  private String name;

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @XmlElement(name = "Description")
  protected String description;
  
  // @XmlElement(name = "LayerFeatureConstraints")
  // protected LayerFeatureConstraints layerFeatureConstraints;

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @XmlElements( { @XmlElement(name = "UserStyle", type = UserStyle.class),
      @XmlElement(name = "NamedStyle", type = NamedStyle.class) })
  List<Style> styles = new ArrayList<Style>();

  @Override
  public List<Style> getStyles() {
    return this.styles;
  }

  @Override
  public void setStyles(List<Style> styles) {
    this.styles = styles;
  }

  @XmlTransient
  private boolean visible = true;

  @Override
  public boolean isVisible() {
    return this.visible;
  }

  @Override
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  @XmlTransient
  private boolean selectable = true;

  @Override
  public boolean isSelectable() {
    return this.selectable;
  }

  @Override
  public void setSelectable(boolean selectable) {
    this.selectable = selectable;
  }
  
  @XmlTransient
  private boolean symbolized = true;

  @Override
  public boolean isSymbolized() {
    return this.symbolized;
  }

  @Override
  public void setSymbolized(boolean newSymbolized) {
    this.symbolized = newSymbolized;
  }

  @Override
  public Symbolizer getSymbolizer() {
    return this.getStyles().get(0).getSymbolizer();
  }

  @XmlTransient
  private Map<RasterSymbolizer, BufferedImage> rasterImage = new HashMap<RasterSymbolizer, BufferedImage>();

  @Override
  public void setImage(RasterSymbolizer symbolizer, BufferedImage image) {
    this.rasterImage.put(symbolizer, image);
  }

  @Override
  public BufferedImage getImage(RasterSymbolizer symbolizer) {
    return this.rasterImage.get(symbolizer);
  }
  
  @XmlTransient
  private String activeGroup;

  //XXX Maybe move the CRS in FeatureTypeStyle.
  @XmlTransient
  private CoordinateReferenceSystem ftscrs;
 
  @Override
  public String getActiveGroup() {
    return activeGroup;
  }

  @Override
  public void setActiveGroup(String activeGroup) {
    this.activeGroup = activeGroup;
  }
  @Override
  public Collection<String> getGroups() {
    Set<String> groups = new HashSet<String>(0);
    for(Style style : this.getStyles()) {
      if (((AbstractStyle) style).getGroup() != null) {
        groups.add(((AbstractStyle) style).getGroup());
      }
    }
    return groups;
  }

  /**
   * Affecte la valeur de l'attribut CRS
   */
  @Override
public void setCRS(CoordinateReferenceSystem crs){
	  this.ftscrs = crs;
  }
  
  /**
   * Crée un CRS a partir d'un crs sous forme de chaine de caractère WKT.
   * @param scrs
   */
  public void setCRSFromWKTString(String scrs){
	  try{
		  this.ftscrs = CRS.parseWKT(scrs);
	  }catch (Exception e) {
		  e.printStackTrace();
	  }
  }
  
  @Override
  public CoordinateReferenceSystem getCRS(){
	  return this.ftscrs;
  }
}
