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

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.filter.expression.PropertyName;
import fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRendering;
import fr.ign.cogit.geoxygene.style.expressive.ExpressiveRendering;
import fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRendering;
import fr.ign.cogit.geoxygene.style.gradient.GradientStroke;

/**
 * @author Julien Perret
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Stroke {

    static private Logger logger = Logger.getLogger(Stroke.class.getName());

    @XmlElements({ @XmlElement(name = "GraphicFill", type = GraphicFill.class),
            @XmlElement(name = "GraphicStroke", type = GraphicStroke.class),
            @XmlElement(name = "GradientStroke", type = GradientStroke.class) })
    private GraphicType graphicType = null;

    @XmlElements({
            @XmlElement(name = "SvgParameter", type = SvgParameter.class),
            @XmlElement(name = "CssParameter", type = SvgParameter.class) })
    private List<SvgParameter> svgParameters = new ArrayList<SvgParameter>(0);

    @XmlElements({
            @XmlElement(name = "StrokeTextureExpressiveRendering", type = StrokeTextureExpressiveRendering.class),
            @XmlElement(name = "BasicTextureExpressiveRendering", type = BasicTextureExpressiveRendering.class) })
    private ExpressiveRendering expressiveRendering = null;

    /**
     * The raw color of the stroke, without opacity information.
     */
    @XmlTransient
    private Color stroke = Color.black;

    @XmlTransient
    private float strokeOpacity = 1.0f;

    @XmlTransient
    private float strokeWidth = 1.0f;

    @XmlTransient
    private int strokeLineJoin = BasicStroke.JOIN_ROUND;

    @XmlTransient
    private int strokeLineCap = BasicStroke.CAP_ROUND;

    @XmlTransient
    private float strokeDashOffset = 0.0f;

    @XmlTransient
    private float[] strokeDashArray = null;

    /**
     * The color of the stroke, considering the opacity attribute.
     */
    @XmlTransient
    private Color color = null;

    @XmlTransient
    private java.awt.Stroke awtStroke = null;

    @XmlTransient
    private PropertyName colorPropertyName;

    /**
     * Renvoie la valeur de l'attribut graphicType.
     * 
     * @return la valeur de l'attribut graphicType
     */
    public GraphicType getGraphicType() {
        return this.graphicType;
    }

    /**
     * Affecte la valeur de l'attribut graphicType.
     * 
     * @param graphicType
     *            l'attribut graphicType à affecter
     */
    public void setGraphicType(GraphicType graphicType) {
        this.graphicType = graphicType;
    }

    /**
     * Renvoie la valeur de l'attribut cssParameters.
     * 
     * @return la valeur de l'attribut cssParameters
     */
    public List<SvgParameter> getSvgParameters() {
        return this.svgParameters;
    }

    /**
     * Affecte la valeur de l'attribut cssParameters.
     * 
     * @param svgParameters
     *            l'attribut cssParameters à affecter
     */
    public void setSvgParameters(List<SvgParameter> svgParameters) {
        this.svgParameters = svgParameters;
        this.updateValues();
    }

    private synchronized void updateValues() {
        synchronized (this.svgParameters) {
            for (SvgParameter parameter : this.svgParameters) {
                if (parameter.getName().equalsIgnoreCase("stroke")) { //$NON-NLS-1$
                    if (parameter.getPropertyName() != null) {
                        this.colorPropertyName = parameter.getPropertyName();
                    } else if (parameter.getValue() != null) {
                        this.stroke = Color.decode(parameter.getValue().trim());
                    }
                } else if (parameter.getName().equalsIgnoreCase("color")) { //$NON-NLS-1$
                    if (parameter.getPropertyName() != null) {
                        this.colorPropertyName = parameter.getPropertyName();
                    } else if (parameter.getValue() != null) {
                        this.stroke = Color.decode(parameter.getValue().trim());
                    }
                } else if (parameter.getName().equalsIgnoreCase(
                        "stroke-opacity")) { //$NON-NLS-1$
                    this.strokeOpacity = Float.parseFloat(parameter.getValue());
                } else if (parameter.getName().equalsIgnoreCase("stroke-width")) { //$NON-NLS-1$
                    this.setStrokeWidth(Float.parseFloat(parameter.getValue()));
                } else if (parameter.getName().equalsIgnoreCase(
                        "stroke-linejoin")) { //$NON-NLS-1$
                    this.setStrokeLineJoin(parameter.getValue());
                } else if (parameter.getName().equalsIgnoreCase(
                        "stroke-linecap")) { //$NON-NLS-1$
                    this.setStrokeLineCap(parameter.getValue());
                } else if (parameter.getName().equalsIgnoreCase(
                        "stroke-dasharray")) { //$NON-NLS-1$
                    this.setStrokeDashArray(parameter.getValue());
                } else if (parameter.getName().equalsIgnoreCase(
                        "stroke-dashoffset")) { //$NON-NLS-1$
                    this.setStrokeDashOffset(parameter.getValue());
                }
            }
        }
    }

    /**
     * Returns the raw color of the stroke, without opacity information. Renvoie
     * la valeur de l'attribut stroke, sans considération de l'opacité.
     * 
     * @return The raw color of the stroke, without opacity information.
     */
    public Color getStroke() {
        return this.stroke;
    }

    /**
     * Affecte la valeur de l'attribut stroke.
     * <p>
     * Met à jout le parametre CSS correspondant
     * 
     * @param stroke
     *            l'attribut stroke à affecter
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

    /**
     * Renvoie la valeur de l'attribut strokeOpacity.
     * 
     * @return la valeur de l'attribut strokeOpacity
     */
    public float getStrokeOpacity() {
        return this.strokeOpacity;
    }

    /**
     * Renvoie la valeur de l'attribut strokeOpacity.
     * 
     * @return la valeur de l'attribut strokeOpacity
     */
    public float getStrokeOpacity(Object object) {
        return this.getStrokeOpacity();
    }

    /**
     * Affecte la valeur de l'attribut strokeOpacity.
     * 
     * @param strokeOpacity
     *            l'attribut strokeOpacity à affecter
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

    /**
     * Renvoie la valeur de l'attribut strokeWidth.
     * 
     * @return la valeur de l'attribut strokeWidth
     */
    public float getStrokeWidth() {
        return this.strokeWidth;
    }

    /**
     * Affecte la valeur de l'attribut strokeWidth.
     * 
     * @param strokeWidth
     *            l'attribut strokeWidth à affecter
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

    /**
     * Renvoie la valeur de l'attribut strokeLineJoin.
     * 
     * @return la valeur de l'attribut strokeLineJoin
     */
    public int getStrokeLineJoin() {
        return this.strokeLineJoin;
    }

    /**
     * Affecte la valeur de l'attribut strokeLineJoin.
     * 
     * @param strokeLineJoin
     *            l'attribut strokeLineJoin à affecter
     */
    public synchronized void setStrokeLineJoin(int strokeLineJoin) {
        this.strokeLineJoin = strokeLineJoin;
        boolean found = false;
        synchronized (this.svgParameters) {
            for (SvgParameter parameter : this.svgParameters) {
                if (parameter.getName().equalsIgnoreCase("stroke-linejoin")) { //$NON-NLS-1$
                    switch (strokeLineJoin) {
                    case BasicStroke.JOIN_MITER:
                        parameter.setValue("miter"); //$NON-NLS-1$
                        break;
                    case BasicStroke.JOIN_ROUND:
                        parameter.setValue("round"); //$NON-NLS-1$
                        break;
                    case BasicStroke.JOIN_BEVEL:
                        parameter.setValue("bevel"); //$NON-NLS-1$
                        break;
                    default:
                        break;
                    }

                    found = true;
                }
            }
            if (!found) {
                SvgParameter parameter = new SvgParameter();
                parameter.setName("stroke-linejoin"); //$NON-NLS-1$
                switch (strokeLineJoin) {
                case BasicStroke.JOIN_MITER:
                    parameter.setValue("miter"); //$NON-NLS-1$
                    break;
                case BasicStroke.JOIN_ROUND:
                    parameter.setValue("round"); //$NON-NLS-1$
                    break;
                case BasicStroke.JOIN_BEVEL:
                    parameter.setValue("bevel"); //$NON-NLS-1$
                    break;
                default:
                    break;
                }
                this.svgParameters.add(parameter);
            }
        }
    }

    /**
     * Affecte la valeur de l'attribut strokeLineJoin.
     * 
     * @param strokeLineJoin
     *            l'attribut strokeLineJoin à affecter
     */
    private void setStrokeLineJoin(String strokeLineJoin) {
        if (strokeLineJoin.equalsIgnoreCase("miter")) { //$NON-NLS-1$
            this.strokeLineJoin = BasicStroke.JOIN_MITER;
        } else if (strokeLineJoin.equalsIgnoreCase("bevel")) { //$NON-NLS-1$
            this.strokeLineJoin = BasicStroke.JOIN_BEVEL;
        } else if (strokeLineJoin.equalsIgnoreCase("round")) { //$NON-NLS-1$
            this.strokeLineJoin = BasicStroke.JOIN_ROUND;
        }
        // sinon, c'est la valeur par defaut
        // otherwise, setting the default value.
    }

    /**
     * Renvoie la valeur de l'attribut strokeLineCap.
     * 
     * @return la valeur de l'attribut strokeLineCap
     */
    public int getStrokeLineCap() {
        return this.strokeLineCap;
    }

    /**
     * Affecte la valeur de l'attribut strokeLineCap.
     * 
     * @param strokeLineCap
     *            l'attribut strokeLineCap à affecter
     */
    public synchronized void setStrokeLineCap(int strokeLineCap) {
        this.strokeLineCap = strokeLineCap;
        boolean found = false;
        synchronized (this.svgParameters) {
            for (SvgParameter parameter : this.svgParameters) {
                if (parameter.getName().equalsIgnoreCase("stroke-linecap")) { //$NON-NLS-1$
                    parameter.setValue(Integer.toString(strokeLineCap));
                    switch (strokeLineCap) {
                    case BasicStroke.CAP_BUTT:
                        parameter.setValue("butt"); //$NON-NLS-1$
                        break;
                    case BasicStroke.CAP_SQUARE:
                        parameter.setValue("square"); //$NON-NLS-1$
                        break;
                    case BasicStroke.CAP_ROUND:
                        parameter.setValue("round"); //$NON-NLS-1$
                        break;
                    default:
                        break;
                    }

                    found = true;
                }

            }
            if (!found) {
                SvgParameter parameter = new SvgParameter();
                parameter.setName("stroke-linecap"); //$NON-NLS-1$
                switch (strokeLineCap) {
                case BasicStroke.CAP_BUTT:
                    parameter.setValue("butt"); //$NON-NLS-1$
                    break;
                case BasicStroke.CAP_SQUARE:
                    parameter.setValue("square"); //$NON-NLS-1$
                    break;
                case BasicStroke.CAP_ROUND:
                    parameter.setValue("round"); //$NON-NLS-1$
                    break;
                default:
                    break;
                }
                this.svgParameters.add(parameter);
            }
        }
    }

    /**
     * Affecte la valeur de l'attribut strokeLineCap.
     * 
     * @param strokeLineCap
     *            l'attribut strokeLineCap à affecter
     */
    private void setStrokeLineCap(String strokeLineCap) {
        if (strokeLineCap.equalsIgnoreCase("butt")) { //$NON-NLS-1$
            this.strokeLineCap = BasicStroke.CAP_BUTT;
        } else if (strokeLineCap.equalsIgnoreCase("square")) { //$NON-NLS-1$
            this.strokeLineCap = BasicStroke.CAP_SQUARE;
        } else {
            this.strokeLineCap = BasicStroke.CAP_ROUND;
        }
        // sinon, c'est la valeur par défaut
        // otherwise, setting the default value.
    }

    /**
     * Renvoie la valeur de l'attribut strokeDashOffset.
     * 
     * @return la valeur de l'attribut strokeDashOffset
     */
    public float getStrokeDashOffset() {
        return this.strokeDashOffset;
    }

    public ExpressiveRendering getExpressiveRendering() {
        return this.expressiveRendering;
    }

    public void setExpressiveRendering(
            StrokeTextureExpressiveRendering expressiveRendering) {
        this.expressiveRendering = expressiveRendering;
    }

    /**
     * Affecte la valeur de l'attribut strokeDashOffset.
     * 
     * @param strokeDashOffset
     *            l'attribut strokeDashOffset à affecter
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

    /**
     * Renvoie la valeur de l'attribut strokeDashArray.
     * 
     * @return la valeur de l'attribut strokeDashArray
     */
    public float[] getStrokeDashArray() {
        return this.strokeDashArray;
    }

    /**
     * Renvoie le strokeDashArray avec un facteur d'échelle.
     * 
     * @param scale
     *            facteur d'échelle
     * @return le strokeDashArray avec un facteur d'échelle
     */
    public synchronized float[] getStrokeDashArray(float scale) {
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
     * 
     * @param strokeDashArray
     *            l'attribut strokeDashArray à affecter
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

    /**
     * Returns the color of the stroke, considering the opacity attribute.
     * 
     * @return The color of the stroke, considering the opacity attribute.
     */
    public synchronized Color getColor() {
        if (this.color == null) {
            this.updateValues();
            if (this.strokeOpacity == 1.0f) {
                this.color = this.stroke;
            } else {
                this.color = new Color(this.stroke.getRed(),
                        this.stroke.getGreen(), this.stroke.getBlue(),
                        (int) (this.strokeOpacity * 255f));
            }
        }
        return this.color;
    }

    /**
     * Returns the color of the stroke, considering the opacity attribute.
     * 
     * @return The color of the stroke, considering the opacity attribute.
     */
    public synchronized Color getColor(Object object) {

        if (object == null) {
            return this.getColor();
        }
        if (this.colorPropertyName == null) {
            this.updateValues();
        }
        if (this.colorPropertyName == null) {
            return this.getColor();
        } else {
            Color compColor = (Color) this.colorPropertyName.evaluate(object);
            if (this.getStrokeOpacity(object) != 1.0f) {
                compColor = new Color(compColor.getRed(), compColor.getGreen(),
                        compColor.getBlue(),
                        (int) (this.getStrokeOpacity(object) * 255f));
            }
            return compColor;
        }
    }

    /**
     * @param newColor
     */
    public void setColor(Color newColor) {
        this.setStroke(newColor);
        if (this.strokeOpacity == 1.0f) {
            this.color = this.stroke;
        } else {
            this.color = new Color(this.stroke.getRed(),
                    this.stroke.getGreen(), this.stroke.getBlue(),
                    (int) (this.strokeOpacity * 255f));
        }
    }

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
        this.awtStroke = new BasicStroke(this.getStrokeWidth() * scale,
                this.getStrokeLineCap(), this.getStrokeLineJoin(), 10.0f,
                this.getStrokeDashArray(scale), this.getStrokeDashOffset()
                        * scale);
        return this.awtStroke;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.awtStroke == null) ? 0 : this.awtStroke.hashCode());
        result = prime * result
                + ((this.color == null) ? 0 : this.color.hashCode());
        result = prime
                * result
                + ((this.colorPropertyName == null) ? 0
                        : this.colorPropertyName.hashCode());
        result = prime
                * result
                + ((this.expressiveRendering == null) ? 0
                        : this.expressiveRendering.hashCode());
        result = prime
                * result
                + ((this.graphicType == null) ? 0 : this.graphicType.hashCode());
        result = prime * result
                + ((this.stroke == null) ? 0 : this.stroke.hashCode());
        result = prime * result + Arrays.hashCode(this.strokeDashArray);
        result = prime * result + Float.floatToIntBits(this.strokeDashOffset);
        result = prime * result + this.strokeLineCap;
        result = prime * result + this.strokeLineJoin;
        result = prime * result + Float.floatToIntBits(this.strokeOpacity);
        result = prime * result + Float.floatToIntBits(this.strokeWidth);
        result = prime
                * result
                + ((this.svgParameters == null) ? 0 : this.svgParameters
                        .hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Stroke other = (Stroke) obj;
        if (this.awtStroke == null) {
            if (other.awtStroke != null) {
                return false;
            }
        } else if (!this.awtStroke.equals(other.awtStroke)) {
            return false;
        }
        if (this.color == null) {
            if (other.color != null) {
                return false;
            }
        } else if (!this.color.equals(other.color)) {
            return false;
        }
        if (this.colorPropertyName == null) {
            if (other.colorPropertyName != null) {
                return false;
            }
        } else if (!this.colorPropertyName.equals(other.colorPropertyName)) {
            return false;
        }
        if (this.expressiveRendering == null) {
            if (other.expressiveRendering != null) {
                return false;
            }
        } else if (!this.expressiveRendering.equals(other.expressiveRendering)) {
            return false;
        }
        if (this.graphicType == null) {
            if (other.graphicType != null) {
                return false;
            }
        } else if (!this.graphicType.equals(other.graphicType)) {
            return false;
        }
        if (this.stroke == null) {
            if (other.stroke != null) {
                return false;
            }
        } else if (!this.stroke.equals(other.stroke)) {
            return false;
        }
        if (!Arrays.equals(this.strokeDashArray, other.strokeDashArray)) {
            return false;
        }
        if (Float.floatToIntBits(this.strokeDashOffset) != Float
                .floatToIntBits(other.strokeDashOffset)) {
            return false;
        }
        if (this.strokeLineCap != other.strokeLineCap) {
            return false;
        }
        if (this.strokeLineJoin != other.strokeLineJoin) {
            return false;
        }
        if (Float.floatToIntBits(this.strokeOpacity) != Float
                .floatToIntBits(other.strokeOpacity)) {
            return false;
        }
        if (Float.floatToIntBits(this.strokeWidth) != Float
                .floatToIntBits(other.strokeWidth)) {
            return false;
        }
        if (this.svgParameters == null) {
            if (other.svgParameters != null) {
                return false;
            }
        } else if (!this.svgParameters.equals(other.svgParameters)) {
            return false;
        }
        return true;
    }

}
