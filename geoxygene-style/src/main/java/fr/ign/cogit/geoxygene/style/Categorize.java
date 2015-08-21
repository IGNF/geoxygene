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
import java.awt.Color;
import java.lang.System;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * @author AMasse
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class Categorize {
    
    @XmlElement( name = "LookupValue")
    private String lookupValue;
    
    public String getLookupValue() {
        return lookupValue;
    }
    
    public void setLookupValue(String lookupValue) {
        this.lookupValue = lookupValue;
    }
    
    @XmlElement ( name = "Value")
    private List<String> value = new ArrayList<String>(0);
    
    public List<String> getValue() {
        return this.value;
    }
    
    public String getValue(int iPoint) {
        return this.value.get(iPoint);
    }

    public void setValue(List<String> value) {
        this.value = value;
    }
    
    
    @XmlElement ( name = "Threshold")
    private List<Double> threshold = new ArrayList<Double>(0);
    
    public List<Double> getThreshold() {
        return this.threshold;
    }

    public double getThreshold(int iPoint) {
        return this.threshold.get(iPoint);
    }
    
    public void setThreshold(List<Double> threshold) {
        this.threshold = threshold;
    }
    
    public int getColorFromValue(double requestedValue) {
        if (threshold.size() != value.size()) {
            System.err.println("Problem in SLD/RasterSymbolizer/ColorMap/Categorize : non-paired elements"); 
            return -1;
        } else {
            for(int i=0; i<threshold.size(); i++) {
                if(threshold.get(i) == requestedValue) {
                    return Color.decode(value.get(i)).getRGB();
                }
            }
        }
        return -1;
    }
    
    public Color getColor(int iPoint) {
        return Color.decode(this.value.get(iPoint));
    }
    
    public int getNbCategorizePoint() {
        if (threshold.size() != value.size()) {
            return -1;
        } else {
            return value.size();
        }
    }
    
}
