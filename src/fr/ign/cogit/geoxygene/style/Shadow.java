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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Julien Perret
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Shadow {

  private GraphicFill graphicFill = null;

  /**
   * Renvoie la valeur de l'attribut graphicFill.
   * @return la valeur de l'attribut graphicFill
   */
  public GraphicFill getGraphicFill() {
    return this.graphicFill;
  }

  /**
   * Affecte la valeur de l'attribut graphicFill.
   * @param graphicFill l'attribut graphicFill à affecter
   */
  public void setGraphicFill(GraphicFill graphicFill) {
    this.graphicFill = graphicFill;
  }

  private Displacement displacement;

  /**
   * Renvoie la valeur de l'attribut displacement.
   * @return la valeur de l'attribut displacement
   */
  public Displacement getDisplacement() {
    return this.displacement;
  }

  /**
   * Affecte la valeur de l'attribut displacement.
   * @param displacement l'attribut displacement à affecter
   */
  public void setDisplacement(Displacement displacement) {
    this.displacement = displacement;
  }

  @XmlElements({ @XmlElement(name = "SvgParameter", type = SvgParameter.class),
      @XmlElement(name = "CssParameter", type = SvgParameter.class) })
  private List<SvgParameter> svgParameters = new ArrayList<SvgParameter>();

  /**
   * Renvoie la valeur de l'attribut cssParameters.
   * @return la valeur de l'attribut cssParameters
   */
  public List<SvgParameter> getSvgParameters() {
    return this.svgParameters;
  }

  /**
   * Affecte la valeur de l'attribut cssParameters.
   * @param svgParameters l'attribut cssParameters à affecter
   */
  public synchronized void setSvgParameters(List<SvgParameter> svgParameters) {
    this.svgParameters = svgParameters;
    this.updateValues();
  }

  private synchronized void updateValues() {
    synchronized (this) {
      for (SvgParameter parameter : this.svgParameters) {
        if (parameter.getName().equalsIgnoreCase("fill")) { //$NON-NLS-1$
          this.setFill(Color.decode(parameter.getValue()));
        } else if (parameter.getName().equalsIgnoreCase("fill-opacity")) { //$NON-NLS-1$
          this.setFillOpacity(Float.parseFloat(parameter.getValue()));
        } else if (parameter.getName().equalsIgnoreCase("color")) { //$NON-NLS-1$
          this.setFill(new Color(Integer.parseInt(parameter.getValue())));
        }
      }
    }
  }

  @XmlTransient
  private Color fill = Color.gray;

  /**
   * Renvoie la valeur de l'attribut fill.
   * @return la valeur de l'attribut fill
   */
  public Color getFill() {
    return this.fill;
  }

  /**
   * Affecte la valeur de l'attribut fill.
   * @param fill l'attribut fill à affecter
   */
  public synchronized void setFill(Color fill) {
    this.fill = fill;
    boolean found = false;
    synchronized (this) {
      for (SvgParameter parameter : this.svgParameters) {
        if (parameter.getName().equalsIgnoreCase("fill")) { //$NON-NLS-1$
          String rgb = Integer.toHexString(fill.getRGB());
          rgb = rgb.substring(2, rgb.length());
          parameter.setValue("#" + rgb); //$NON-NLS-1$
          found = true;
        } else if (parameter.getName().equalsIgnoreCase("color")) { //$NON-NLS-1$
          String sRGB = Integer.toString(fill.getRGB());
          parameter.setValue(sRGB);
          found = true;
        }
      }
      if (!found) {
        SvgParameter parameter = new SvgParameter();
        parameter.setName("fill"); //$NON-NLS-1$
        String rgb = Integer.toHexString(fill.getRGB());
        rgb = rgb.substring(2, rgb.length());
        parameter.setValue("#" + rgb); //$NON-NLS-1$
        this.svgParameters.add(parameter);
      }
    }
  }

  @XmlTransient
  private float fillOpacity = 1.0f;

  /**
   * Renvoie la valeur de l'attribut fillOpacity.
   * @return la valeur de l'attribut fillOpacity
   */
  public float getFillOpacity() {
    return this.fillOpacity;
  }

  /**
   * Affecte la valeur de l'attribut fillOpacity.
   * @param fillOpacity l'attribut fillOpacity à affecter
   */
  public synchronized void setFillOpacity(float fillOpacity) {
    this.fillOpacity = fillOpacity;
    boolean found = false;
    synchronized (this) {
      for (SvgParameter parameter : this.svgParameters) {
        if (parameter.getName().equalsIgnoreCase("fill-opacity")) { //$NON-NLS-1$
          parameter.setValue(Float.toString(fillOpacity));
          found = true;
        }
      }
      if (!found) {
        SvgParameter parameter = new SvgParameter();
        parameter.setName("fill-opacity"); //$NON-NLS-1$
        parameter.setValue(Float.toString(fillOpacity));
        this.svgParameters.add(parameter);
      }
    }
  }

  @XmlTransient
  private Color color = null;

  public synchronized Color getColor() {
    if (this.color == null) {
      this.updateValues();
      if (this.fillOpacity == 1.0f) {
        this.color = this.fill;
      } else {
        this.color = new Color(this.fill.getRed(), this.fill.getGreen(),
            this.fill.getBlue(), (int) (this.fillOpacity * 255f));
      }
    }
    return this.color;
  }

  /**
   * @param newColor
   */
  public void setColor(Color newColor) {
    this.setFill(newColor);
    if (this.fillOpacity == 1.0f) {
      this.color = this.fill;
    } else {
      this.color = new Color(this.fill.getRed(), this.fill.getGreen(),
          this.fill.getBlue(), (int) (this.fillOpacity * 255f));
    }
  }
}
