/*******************************************************************************
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
 *******************************************************************************/

package fr.ign.cogit.geoxygene.style;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author JeT Blending Mode manages how layers are blended together (Screen,
 *         overlay, ...)
 */
@XmlType(name = "BlendingMode")
@XmlEnum(String.class)
public enum BlendingMode {
    Normal(1),
    HighTone(2),
    Multiply(3),
    Overlay(4);
    
    private final int value;
    
    private BlendingMode(final int _v){
        this.value = _v;
    }

    public int getValue() {
        return value;
    }
    
    public String toString(){
    	switch (this.value) {
		case 1:
			return "Normal";
		case 2:
			return "HighTone";
		case 3:
			return "Multiply";
		case 4:
			return "Overlay";
		default:
			return "";
		}
    }
    
}
