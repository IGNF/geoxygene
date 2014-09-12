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
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Julien Perret
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Font {
    @XmlTransient
    private String fontFamily = "Default"; //$NON-NLS-1$

    /**
     * Renvoie la valeur de l'attribut fontFamily.
     * 
     * @return la valeur de l'attribut fontFamily
     */
    public String getFontFamily() {
        return this.fontFamily;
    }

    /**
     * Affecte la valeur de l'attribut fontFamily.
     * 
     * @param fontFamily
     *            l'attribut fontFamily à affecter
     */
    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
        boolean found = false;
        synchronized (this) {
            for (SvgParameter parameter : this.svgParameters) {
                if (parameter.getName().equalsIgnoreCase("font-family")) { //$NON-NLS-1$
                    parameter.setValue(fontFamily);
                    found = true;
                }
            }
            if (!found) {
                SvgParameter parameter = new SvgParameter();
                parameter.setName("font-family"); //$NON-NLS-1$
                parameter.setValue(fontFamily);
                this.svgParameters.add(parameter);
            }
        }
    }

    @XmlTransient
    private int fontStyle = java.awt.Font.PLAIN;

    /**
     * Renvoie la valeur de l'attribut fontStyle.
     * 
     * @return la valeur de l'attribut fontStyle
     */
    public int getFontStyle() {
        return this.fontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
        boolean found = false;
        synchronized (this) {
            for (SvgParameter parameter : this.svgParameters) {
                if (parameter.getName().equalsIgnoreCase("font-style")) { //$NON-NLS-1$
                    parameter.setValue(Float.toString(fontStyle));
                    switch (fontStyle) {
                    case java.awt.Font.PLAIN:
                        parameter.setValue("normal");//$NON-NLS-1$
                        break;
                    case java.awt.Font.ITALIC:
                        parameter.setValue("italic");//$NON-NLS-1$
                        break;
                    case java.awt.Font.BOLD:
                        parameter.setValue("oblique");//$NON-NLS-1$
                        break;
                    default:
                        parameter.setValue("normal");//$NON-NLS-1$
                        break;
                    }
                    found = true;
                }
            }
            if (!found) {
                SvgParameter parameter = new SvgParameter();
                parameter.setName("font-style"); //$NON-NLS-1$
                parameter.setValue(Float.toString(fontStyle));
                this.svgParameters.add(parameter);
            }
        }
    }

    /**
     * Affecte la valeur de l'attribut fontStyle.
     * 
     * @param fontStyle
     *            l'attribut fontStyle à affecter
     */
    public void setFontStyle(String fontStyle) {
        int style = java.awt.Font.PLAIN;
        if (fontStyle.equalsIgnoreCase("italic")) { //$NON-NLS-1$
            style = java.awt.Font.ITALIC;
        } else if (fontStyle.equalsIgnoreCase("oblique")//$NON-NLS-1$
                || fontStyle.equalsIgnoreCase("bold")) { //$NON-NLS-1$
            style = java.awt.Font.BOLD;
        }
        this.setFontStyle(style);
    }

    @XmlTransient
    private int fontWeight = java.awt.Font.PLAIN;

    /**
     * Renvoie la valeur de l'attribut fontWeight.
     * 
     * @return la valeur de l'attribut fontWeight
     */
    public int getFontWeight() {
        return this.fontWeight;
    }

    /**
     * Affecte la valeur de l'attribut fontWeight.
     * 
     * @param fontWeight
     *            l'attribut fontWeight à affecter
     */
    public synchronized void setFontWeight(int fontWeight) {
        this.fontWeight = fontWeight;
        boolean found = false;
        synchronized (this) {
            for (SvgParameter parameter : this.svgParameters) {
                if (parameter.getName().equalsIgnoreCase("font-weight")) { //$NON-NLS-1$
                    switch (fontWeight) {
                    case java.awt.Font.PLAIN:
                        parameter.setValue("normal");//$NON-NLS-1$
                        break;
                    case java.awt.Font.BOLD:
                        parameter.setValue("bold");//$NON-NLS-1$
                        break;
                    default:
                        parameter.setValue("normal");//$NON-NLS-1$
                        break;
                    }
                    found = true;
                }
            }
            if (!found) {
                SvgParameter parameter = new SvgParameter();
                parameter.setName("font-weight"); //$NON-NLS-1$
                switch (fontWeight) {
                case java.awt.Font.PLAIN:
                    parameter.setValue("normal");
                    break;
                case java.awt.Font.BOLD:
                    parameter.setValue("bold");
                    break;
                default:
                    parameter.setValue("normal");
                    break;
                }
                this.svgParameters.add(parameter);
            }
        }
    }

    public void setFontWeight(String fontWeight) {
        int weight = java.awt.Font.PLAIN;
        if (fontWeight.equalsIgnoreCase("bold")) { //$NON-NLS-1$
            weight = java.awt.Font.BOLD;
        }
        this.setFontWeight(weight);
    }

    @XmlTransient
    private int fontSize = 10;

    /**
     * Renvoie la valeur de l'attribut fontSize.
     * 
     * @return la valeur de l'attribut fontSize
     */
    public int getFontSize() {
        return this.fontSize;
    }

    /**
     * Affecte la valeur de l'attribut fontSize.
     * 
     * @param fontSize
     *            l'attribut fontSize à affecter
     */
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        boolean found = false;
        synchronized (this) {
            for (SvgParameter parameter : this.svgParameters) {
                if (parameter.getName().equalsIgnoreCase("font-size")) { //$NON-NLS-1$
                    parameter.setValue(Float.toString(fontSize));
                    found = true;
                }
            }
            if (!found) {
                SvgParameter parameter = new SvgParameter();
                parameter.setName("font-size"); //$NON-NLS-1$
                parameter.setValue(Float.toString(fontSize));
                this.svgParameters.add(parameter);
            }
        }
    }

    @XmlElements({
            @XmlElement(name = "SvgParameter", type = SvgParameter.class),
            @XmlElement(name = "CssParameter", type = SvgParameter.class) })
    private List<SvgParameter> svgParameters = new ArrayList<SvgParameter>();

    /**
     * Renvoie la valeur de l'attribut cssParameters.
     * 
     * @return la valeur de l'attribut cssParameters
     */
    public List<SvgParameter> getSvgParameters() {
        return this.svgParameters;
    }

    /**
     * Affecte la valeur de l'attribut cssParameters. Four types of CssParameter
     * are allowed, 'font-family', 'font-style', 'font-weight', and 'font-size'.
     * (cf. OGC 02-070 p.46)
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
                if (parameter.getName().equalsIgnoreCase("font-family")) { //$NON-NLS-1$
                    this.setFontFamily(parameter.getValue());
                } else if (parameter.getName().equalsIgnoreCase("font-style")) { //$NON-NLS-1$
                    this.setFontStyle(parameter.getValue());
                } else if (parameter.getName().equalsIgnoreCase("font-weight")) { //$NON-NLS-1$
                    this.setFontWeight(parameter.getValue());
                } else if (parameter.getName().equalsIgnoreCase("font-size")) { //$NON-NLS-1$
                    this.setFontSize((int) (Double.parseDouble(parameter
                            .getValue())));
                }
            }

        }
    }

    @XmlTransient
    private java.awt.Font font = null;

    /**
     * @return une police AWT équivalent à la police courante
     */
    public java.awt.Font toAwfFont(float scale) {
        if (this.font == null) {
            this.updateValues();
        }
        this.font = new java.awt.Font(this.getFontFamily(), this.getFontStyle()
                | this.getFontWeight(), (int) (this.getFontSize() * scale));
        return this.font;
    }

    public java.awt.Font toAwfFont() {
        if (this.font == null) {
            this.updateValues();
        }
        this.font = new java.awt.Font(this.getFontFamily(), this.getFontStyle()
                | this.getFontWeight(), this.getFontSize());
        return this.font;
    }

    public Font() {
    }

    public Font(java.awt.Font font) {
        this.setFontFamily(font.getFamily());
        switch (font.getStyle()) {
        case java.awt.Font.PLAIN:
            this.setFontStyle(java.awt.Font.PLAIN);
            this.setFontWeight(java.awt.Font.PLAIN);
            break;
        case java.awt.Font.ITALIC:
            this.setFontStyle(java.awt.Font.ITALIC);
            this.setFontWeight(java.awt.Font.PLAIN);
            break;
        case java.awt.Font.BOLD:
            this.setFontStyle(java.awt.Font.PLAIN);
            this.setFontWeight(java.awt.Font.BOLD);
            break;
        case java.awt.Font.BOLD | java.awt.Font.ITALIC:
            this.setFontStyle(java.awt.Font.ITALIC);
            this.setFontWeight(java.awt.Font.BOLD);
            break;
        }
        this.setFontSize(font.getSize());
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
                + ((this.font == null) ? 0 : this.font.hashCode());
        result = prime * result
                + ((this.fontFamily == null) ? 0 : this.fontFamily.hashCode());
        result = prime * result + this.fontSize;
        result = prime * result + this.fontStyle;
        result = prime * result + this.fontWeight;
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
        Font other = (Font) obj;
        if (this.font == null) {
            if (other.font != null) {
                return false;
            }
        } else if (!this.font.equals(other.font)) {
            return false;
        }
        if (this.fontFamily == null) {
            if (other.fontFamily != null) {
                return false;
            }
        } else if (!this.fontFamily.equals(other.fontFamily)) {
            return false;
        }
        if (this.fontSize != other.fontSize) {
            return false;
        }
        if (this.fontStyle != other.fontStyle) {
            return false;
        }
        if (this.fontWeight != other.fontWeight) {
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
