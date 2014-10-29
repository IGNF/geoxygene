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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;

import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import fr.ign.cogit.geoxygene.style.filter.LayerFilter;
import fr.ign.cogit.geoxygene.style.filter.LayerFilterContrast;
import fr.ign.cogit.geoxygene.style.filter.LayerFilterIdentity;

/**
 * @author Julien Perret
 */
@XmlAccessorType(XmlAccessType.NONE)
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

    @XmlElement(name = "Description", required = false)
    private String description;

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String d) {
        this.description = d;
    }

    // @XmlElement(name = "Description")
    // protected Description description;
    // @XmlElement(name = "LayerFeatureConstraints")
    // protected LayerFeatureConstraints layerFeatureConstraints;

    @XmlElements({ @XmlElement(name = "UserStyle", type = UserStyle.class),
            @XmlElement(name = "NamedStyle", type = NamedStyle.class) })
    List<Style> styles = new ArrayList<Style>(0);

    @Override
    public List<Style> getStyles() {
        return this.styles;
    }

    @Override
    public void setStyles(List<Style> styles) {
        this.styles = styles;
    }

    @XmlElements({
            @XmlElement(name = "ContrastFilter", type = LayerFilterContrast.class),
            @XmlElement(name = "NoFilter", type = LayerFilterIdentity.class) })
    LayerFilter filter = null;

    /**
     * @return the filters
     */
    @Override
    public LayerFilter getFilter() {
        return this.filter;
    }

    /**
     * @param filters
     *            the filters to set
     */
    @Override
    public void setFilter(LayerFilter filter) {
        this.filter = filter;
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
    public void setSelectable(boolean newSelectable) {
        this.selectable = newSelectable;
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
    private String activeGroup = null;

    // XXX Maybe move the CRS in FeatureTypeStyle.
    @XmlTransient
    private CoordinateReferenceSystem ftscrs;

    @Override
    public String getActiveGroup() {
        return this.activeGroup;
    }

    @Override
    public void setActiveGroup(String activeGroup) {
        this.activeGroup = activeGroup;
    }

    @Override
    public List<Style> getActiveStyles() {
        Collection<String> groups = this.getGroups();
        if (groups.isEmpty()) {
            return this.getStyles();
        }
        String group = this.getActiveGroup();
        if (group == null || group.isEmpty()) {
            group = groups.iterator().next();
        }
        return this.getStyles(group);
    }

    public List<Style> getStyles(String group) {
        if (group == null || group.isEmpty()) {
            return this.getStyles();
        }
        List<Style> groupStyles = new ArrayList<Style>(0);
        for (Style style : this.getStyles()) {
            if (style.getGroup().equalsIgnoreCase(group)) {
                groupStyles.add(style);
            }
        }
        return groupStyles;
    }

    @Override
    public Collection<String> getGroups() {
        Set<String> groups = new HashSet<String>(0);
        for (Style style : this.getStyles()) {
            if (style.getGroup() != null) {
                groups.add(style.getGroup());
            }
        }
        return groups;
    }

    /**
     * Affecte la valeur de l'attribut CRS
     */
    @Override
    public void setCRS(CoordinateReferenceSystem crs) {
        this.ftscrs = crs;
    }

    /**
     * Crée un CRS a partir d'un crs sous forme de chaine de caractère WKT.
     * 
     * @param scrs
     */
    public void setCRSFromWKTString(String scrs) {
        try {
            this.ftscrs = CRS.parseWKT(scrs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public CoordinateReferenceSystem getCRS() {
        return this.ftscrs;
    }

    @Override
    public void destroy() {
        this.styles.clear();
        this.ftscrs = null;

    }

    // TODO Cette solution doit être provisoire, l'icon devrait être rattaché
    // aux styles du layer et non à lui même.
    @XmlTransient
    protected ImageIcon icon;

    @XmlTransient
    private double opacity = 1.0d;

    @Override
    public ImageIcon getIcon() {
        return this.icon;
    }

    @Override
    public void setIcon(ImageIcon _icon) {
        this.icon = _icon;
    }

    @Override
    public double getOpacity() {
        return this.opacity;
    }

    @Override
    public void setOpacity(double opacity) {
        if (opacity < 0.0d) {
            this.opacity = 0.0d;
        } else if (opacity > 1.0d) {
            this.opacity = 1.0d;
        }
        this.opacity = opacity;
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
                + ((this.filter == null) ? 0 : this.filter.hashCode());
        result = prime * result
                + ((this.name == null) ? 0 : this.name.hashCode());
        long temp;
        temp = Double.doubleToLongBits(this.opacity);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result
                + ((this.styles == null) ? 0 : this.styles.hashCode());
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
        AbstractLayer other = (AbstractLayer) obj;
        if (this.filter == null) {
            if (other.filter != null) {
                return false;
            }
        } else if (!this.filter.equals(other.filter)) {
            return false;
        }
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        if (Double.doubleToLongBits(this.opacity) != Double
                .doubleToLongBits(other.opacity)) {
            return false;
        }
        if (this.styles == null) {
            if (other.styles != null) {
                return false;
            }
        } else if (!this.styles.equals(other.styles)) {
            return false;
        }
        return true;
    }

}
