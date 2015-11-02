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

package fr.ign.cogit.geoxygene.style.expressive;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import fr.ign.cogit.geoxygene.style.StylingParameter;
import fr.ign.cogit.geoxygene.style.texture.Texture;

@XmlJavaTypeAdapter(ExpressiveParameterAdapter.class)
public class ExpressiveParameter implements StylingParameter{

    String name;

    Object value;

    // The "type" attribute is defined by expressive method this parameter
    // belongs to.
    Class<?> type = null;

    public String getName() {
        return this.name;
    }

    public Object getValue() {

        return this.type == null ? this.value : this.type.cast(value);
    }

    public static ExpressiveParameter getInstance(AdaptedExpressiveParameter v) {
        if (v.content == null || v.content.isEmpty()) {
            return null;
        }
        ExpressiveParameter sep = new ExpressiveParameter();
        sep.name = v.name;
        // If the content is of Xml type "mixed", the actual value is surrounded
        // by nodes
        for(Object o : v.content){
            if (!(o instanceof String)){
                sep.value =o;
            }
        }
        if(sep.value == null)
            sep.value = v.content.get(0);
        // Basic type detection
        if (sep.value instanceof String) {
            Pattern pdouble = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");
            Pattern pint = Pattern.compile("[-+]?[0-9]*");
            Matcher m = pdouble.matcher((String) sep.value);
            if (m.find()) {
                sep.value = Double.parseDouble((String) sep.value);
            } else {
                m = pint.matcher((String) sep.value);
                if (m.find()) {
                    sep.value = Integer.parseInt((String) sep.value);
                }
            }
        }

        return sep;
    }

    public String toString() {
        return "ExpressiveParameter " + this.name + " = " + this.value;
    }

    public boolean isSimpleParameter() {
        return value instanceof String || value instanceof Double || value instanceof Integer || value instanceof Float || value instanceof Byte || value instanceof Short || value instanceof Long
                || value instanceof Boolean || value instanceof Character;
    }

    public void setType(Class<?> c) {
        this.type = c;
    }

    public void setValue(Object _value) {
        this.value =_value;
    }

    public boolean isTextureParameter() {
        return value instanceof Texture;
    }

}
