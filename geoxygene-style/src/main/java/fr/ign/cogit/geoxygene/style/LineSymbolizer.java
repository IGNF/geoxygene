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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import fr.ign.cogit.geoxygene.style.proxy.ProxySymbol;

/**
 * @author Julien Perret
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class LineSymbolizer extends AbstractSymbolizer {

    @XmlElement(name = "ColorMap")
    ColorMap colorMap = null;

    @XmlElement(name = "CategorizedMap")
    CategorizedMap categorizedMap = null;

    @XmlElement(name = "PerpendicularOffset")
    double perpendicularOffset = 0;

    @XmlElement(name = "ProxySymbol")
    ProxySymbol proxySymbol = null;

    @Override
    public boolean isLineSymbolizer() {
        return true;
    }

    public ColorMap getColorMap() {
        return this.colorMap;
    }

    public void setColorMap(ColorMap colorMap) {
        this.colorMap = colorMap;
    }

    public CategorizedMap getCategorizedMap() {
        return this.categorizedMap;
    }

    public void setCategorizedMap(CategorizedMap categorizedMap) {
        this.categorizedMap = categorizedMap;
    }

    public double getPerpendicularOffset() {
        return this.perpendicularOffset;
    }

    public void setPerpendicularOffset(double perpendicularOffset) {
        this.perpendicularOffset = perpendicularOffset;
    }

    public ProxySymbol getProxySymbol() {
        return this.proxySymbol;
    }

    public void setProxySymbo(ProxySymbol proxySymbol) {
        this.proxySymbol = proxySymbol;
    }

}
