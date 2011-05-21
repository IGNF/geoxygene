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

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

/**
 * @author Julien Perret
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Stroke {
  static Logger logger = Logger.getLogger(Stroke.class.getName());

  @XmlElements( { @XmlElement(name = "GraphicFill", type = GraphicFill.class),
      @XmlElement(name = "GraphicStroke", type = GraphicStroke.class) })
  private GraphicType graphicType = null;

  /**
   * Renvoie la valeur de l'attribut graphicType.
   * @return la valeur de l'attribut graphicType
   */
  public GraphicType getGraphicType() {
    return this.graphicType;
  }

  /**
   * Affecte la valeur de l'attribut graphicType.
   * @param graphicType l'attribut graphicType à affecter
   */
  public void setGraphicType(GraphicType graphicType) {
    this.graphicType = graphicType;
  }

  @XmlElements( {
      @XmlElement(name = "SvgParameter", type = SvgParameter.class),
      @XmlElement(name = "CssParameter", type = SvgParameter.class) })
  private List<SvgParameter> svgParameters = new ArrayList<SvgParameter>(0);

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
  public void setSvgParameters(List<SvgParameter> svgParameters) {
    this.svgParameters = svgParameters;
    this.updateValues();
  }

  private synchronized void updateValues() {
//    this.setStrokeLineCap(BasicStroke.CAP_BUTT);
    synchronized (this.svgParameters) {
      for (SvgParameter parameter : this.svgParameters) {
        if (parameter.getName().equalsIgnoreCase("stroke")) { //$NON-NLS-1$
          this.stroke = Color.decode(parameter.getValue());
        } else if (parameter.getName().equalsIgnoreCase("color")) { //$NON-NLS-1$
          this.stroke = new Color(Integer.parseInt(parameter.getValue()));
        } else if (parameter.getName().equalsIgnoreCase("stroke-opacity")) { //$NON-NLS-1$
          this.strokeOpacity = Float.parseFloat(parameter.getValue());
        } else if (parameter.getName().equalsIgnoreCase("stroke-width")) { //$NON-NLS-1$
          this.setStrokeWidth(Float.parseFloat(parameter.getValue()));
        } else if (parameter.getName().equalsIgnoreCase("stroke-linejoin")) { //$NON-NLS-1$
          this.setStrokeLineJoin(parameter.getValue());
        } else if (parameter.getName().equalsIgnoreCase("stroke-linecap")) { //$NON-NLS-1$
          this.setStrokeLineCap(parameter.getValue());
        } else if (parameter.getName().equalsIgnoreCase("stroke-dasharray")) { //$NON-NLS-1$
          this.setStrokeDashArray(parameter.getValue());
        } else if (parameter.getName().equalsIgnoreCase("stroke-dashoffset")) { //$NON-NLS-1$
          this.setStrokeDashOffset(parameter.getValue());
        }
      }
    }
  }

  @XmlTransient
  private Color stroke = Color.black;

  /**
   * Renvoie la valeur de l'attribut stroke.
   * @return la valeur de l'attribut stroke
   */
  public Color getStroke() {
    return this.stroke;
  }

  /**
   * Affecte la valeur de l'attribut stroke.
   * <p>
   * Met à jout le parametre CSS correspondant
   * @param stroke l'attribut stroke à affecter
   */
  public synchronized void setStroke(Color stroke) {
    this.stroke = stroke;
    boolean found = false;
    synchronized (this.svgParameters) {
      for (SvgParameter parameter : this.svgParameters) {
        if (parameter.getName().equalsIgnoreCase("stroke")) { //$NON-NLS-1$
          String rgb = Integer.toHexString(stroke.getRGB());
          rgb = rgb.substring(2, rgb.length());
          parameter.setValue("#" + rgb); //$NON-NLS-1$
          found = true;
        } else if (parameter.getName().equalsIgnoreCase("stroke")) { //$NON-NLS-1$
          String sRGB = Integer.toString(stroke.getRGB());
          parameter.setValue(sRGB);
          found = true;
        }
      }
      if (!found) {
        SvgParameter parameter = new SvgParameter();
        parameter.setName("stroke"); //$NON-NLS-1$
        String rgb = Integer.toHexString(stroke.getRGB());
        rgb = rgb.substring(2, rgb.length());
        parameter.setValue("#" + rgb); //$NON-NLS-1$
        this.svgParameters.add(parameter);
      }
    }
    this.color = null;
  }

  @XmlTransient
  private float strokeOpacity = 1.0f;

  /**
   * Renvoie la valeur de l'attribut strokeOpacity.
   * @return la valeur de l'attribut strokeOpacity
   */
  public float getStrokeOpacity() {
    return this.strokeOpacity;
  }

  /**
   * Affecte la valeur de l'attribut strokeOpacity.
   * @param strokeOpacity l'attribut strokeOpacity à affecter
   */
  public synchronized void setStrokeOpacity(float strokeOpacity) {
    this.strokeOpacity = strokeOpacity;
    boolean found = false;
    synchronized (this.svgParameters) {
      for (SvgParameter parameter : this.svgParameters) {
        if (parameter.getName().equalsIgnoreCase("stroke-opacity")) { //$NON-NLS-1$
          parameter.setValue(Float.toString(strokeOpacity));
          found = true;
        }
      }
      if (!found) {
        SvgParameter parameter = new SvgParameter();
        parameter.setName("stroke-opacity"); //$NON-NLS-1$
        parameter.setValue(Float.toString(strokeOpacity));
        this.svgParameters.add(parameter);
      }
    }
  }

  @XmlTransient
  private float strokeWidth = 1.0f;

  /**
   * Renvoie la valeur de l'attribut strokeWidth.
   * @return la valeur de l'attribut strokeWidth
   */
  public float getStrokeWidth() {
    return this.strokeWidth;
  }

  /**
   * Affecte la valeur de l'attribut strokeWidth.
   * @param strokeWidth l'attribut strokeWidth à affecter
   */
  public synchronized void setStrokeWidth(float strokeWidth) {
    this.strokeWidth = strokeWidth;
    boolean found = false;
    synchronized (this.svgParameters) {
      for (SvgParameter parameter : this.svgParameters) {
        if (parameter.getName().equalsIgnoreCase("stroke-width")) { //$NON-NLS-1$
          parameter.setValue(Float.toString(strokeWidth));
          found = true;
        }
      }
      if (!found) {
        SvgParameter parameter = new SvgParameter();
        parameter.setName("stroke-width"); //$NON-NLS-1$
        parameter.setValue(Float.toString(strokeWidth));
        this.svgParameters.add(parameter);
      }
    }
  }

  @XmlTransient
  private int strokeLineJoin = BasicStroke.JOIN_ROUND;

  /**
   * Renvoie la valeur de l'attribut strokeLineJoin.
   * @return la valeur de l'attribut strokeLineJoin
   */
  public int getStrokeLineJoin() {
    return this.strokeLineJoin;
  }

  /**
   * Affecte la valeur de l'attribut strokeLineJoin.
   * @param strokeLineJoin l'attribut strokeLineJoin à affecter
   */
  public synchronized void setStrokeLineJoin(int strokeLineJoin) {
    this.strokeLineJoin = strokeLineJoin;
    synchronized (this.svgParameters) {
      for (SvgParameter parameter : this.svgParameters) {
        if (parameter.getName().equalsIgnoreCase("stroke-linejoin")) { //$NON-NLS-1$
          parameter.setValue(Integer.toString(strokeLineJoin));
        }
      }
    }
  }

  /**
   * Affecte la valeur de l'attribut strokeLineJoin.
   * @param strokeLineJoin l'attribut strokeLineJoin à affecter
   */
  private void setStrokeLineJoin(String strokeLineJoin) {
    if (strokeLineJoin.equalsIgnoreCase("mitre")) { //$NON-NLS-1$
      this.strokeLineJoin = BasicStroke.JOIN_MITER;
    } else if (strokeLineJoin.equalsIgnoreCase("bevel")) { //$NON-NLS-1$
      this.strokeLineJoin = BasicStroke.JOIN_BEVEL;
    }
    // sinon, c'est la valeur par defaut
    // otherwise, setting the default value.
  }

  @XmlTransient
  private int strokeLineCap = BasicStroke.CAP_ROUND;

  /**
   * Renvoie la valeur de l'attribut strokeLineCap.
   * @return la valeur de l'attribut strokeLineCap
   */
  public int getStrokeLineCap() {
    return this.strokeLineCap;
  }

  /**
   * Affecte la valeur de l'attribut strokeLineCap.
   * @param strokeLineCap l'attribut strokeLineCap à affecter
   */
  public synchronized void setStrokeLineCap(int strokeLineCap) {
    this.strokeLineCap = strokeLineCap;
    synchronized (this.svgParameters) {
      for (SvgParameter parameter : this.svgParameters) {
        if (parameter.getName().equalsIgnoreCase("stroke-linecap")) { //$NON-NLS-1$
          parameter.setValue(Integer.toString(strokeLineCap));
        }
      }
    }
  }

  /**
   * Affecte la valeur de l'attribut strokeLineCap.
   * @param strokeLineCap l'attribut strokeLineCap à affecter
   */
  private void setStrokeLineCap(String strokeLineCap) {
    if (strokeLineCap.equalsIgnoreCase("butt")) { //$NON-NLS-1$
      this.strokeLineCap = BasicStroke.CAP_BUTT; 
    } else if (strokeLineCap.equalsIgnoreCase("square")) { //$NON-NLS-1$
      this.strokeLineCap = BasicStroke.CAP_SQUARE; 
    }
    // sinon, c'est la valeur par défaut
    // otherwise, setting the default value.
  }

  @XmlTransient
  private float strokeDashOffset = 0.0f;

  /**
   * Renvoie la valeur de l'attribut strokeDashOffset.
   * @return la valeur de l'attribut strokeDashOffset
   */
  public float getStrokeDashOffset() {
    return this.strokeDashOffset;
  }

  /**
   * Affecte la valeur de l'attribut strokeDashOffset.
   * @param strokeDashOffset l'attribut strokeDashOffset à affecter
   */
  public synchronized void setStrokeDashOffset(float strokeDashOffset) {
    this.strokeDashOffset = strokeDashOffset;
    synchronized (this.svgParameters) {
      for (SvgParameter parameter : this.svgParameters) {
        if (parameter.getName().equalsIgnoreCase("strokeDashOffset")) { //$NON-NLS-1$
          parameter.setValue(Float.toString(strokeDashOffset));
        }
      }
    }
  }

  /**
   * @param value
   */
  private void setStrokeDashOffset(String value) {
    this.setStrokeDashOffset(Float.parseFloat(value));
  }

  @XmlTransient
  private float[] strokeDashArray = null;

  /**
   * Renvoie la valeur de l'attribut strokeDashArray.
   * @return la valeur de l'attribut strokeDashArray
   */
  public float[] getStrokeDashArray() {
    return this.strokeDashArray;
  }

  /**
   * Renvoie le strokeDashArray avec un facteur d'échelle.
   * @param scale facteur d'échelle
   * @return le strokeDashArray avec un facteur d'échelle
   */
  public float[] getStrokeDashArray(float scale) {
    if (this.strokeDashArray == null) {
      return null;
    }
    float[] scaledStrokeDashArray = new float[this.strokeDashArray.length];
    for (int i = 0; i < this.strokeDashArray.length; i++) {
      scaledStrokeDashArray[i] = this.strokeDashArray[i] * scale;
    }
    return scaledStrokeDashArray;
  }

  /**
   * Affecte la valeur de l'attribut strokeDashArray.
   * @param strokeDashArray l'attribut strokeDashArray à affecter
   */
  public synchronized void setStrokeDashArray(float[] strokeDashArray) {
    this.strokeDashArray = strokeDashArray;
    synchronized (this.svgParameters) {
      for (SvgParameter parameter : this.svgParameters) {
        if (parameter.getName().equalsIgnoreCase("strokeDashArray")) { //$NON-NLS-1$
          String dashArray = ""; //$NON-NLS-1$
          for (float element : strokeDashArray) {
            dashArray += element + " "; //$NON-NLS-1$
          }
          parameter.setValue(dashArray);
        }
      }
    }
  }

  /**
   * @param value
   */
  private void setStrokeDashArray(String value) {
    String[] values = value.split(" "); //$NON-NLS-1$
    this.strokeDashArray = new float[values.length];
    for (int index = 0; index < values.length; index++) {
      this.strokeDashArray[index] = Float.parseFloat(values[index]);
    }
  }

  @XmlTransient
  private Color color = null;

  public synchronized Color getColor() {
    if (this.color == null) {
      this.updateValues();
      if (this.strokeOpacity == 1.0f) {
        this.color = this.stroke;
      } else {
        this.color = new Color(this.stroke.getRed(), this.stroke.getGreen(),
            this.stroke.getBlue(), (int) (this.strokeOpacity * 255f));
      }
    }
    return this.color;
  }

  /**
   * @param newColor
   */
  public void setColor(Color newColor) {
    this.setStroke(newColor);
    if (this.strokeOpacity == 1.0f) {
      this.color = this.stroke;
    } else {
      this.color = new Color(this.stroke.getRed(), this.stroke.getGreen(),
          this.stroke.getBlue(), (int) (this.strokeOpacity * 255f));
    }
  }

  @XmlTransient
  private java.awt.Stroke awtStroke = null;

  /**
   * @return the AWT Stroke properties to be used for drawing
   */
  public java.awt.Stroke toAwtStroke() {
    return this.toAwtStroke(1.0f);
  }

  /**
   * @return the AWT Stroke properties to be used for drawing
   */
  public java.awt.Stroke toAwtStroke(float scale) {
    if (this.awtStroke == null) {
      this.updateValues();
    }
    this.awtStroke = new BasicStroke(this.getStrokeWidth() * scale, this
        .getStrokeLineCap(), this.getStrokeLineJoin(), 10.0f, this
        .getStrokeDashArray(scale), this.getStrokeDashOffset() * scale);
    return this.awtStroke;
  }
}
