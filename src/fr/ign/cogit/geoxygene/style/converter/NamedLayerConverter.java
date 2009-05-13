/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.style.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import fr.ign.cogit.geoxygene.style.NamedLayer;
import fr.ign.cogit.geoxygene.style.NamedStyle;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.UserStyle;

/**
 * @author Julien Perret
 *
 */
public class NamedLayerConverter implements Converter {

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,MarshallingContext context) {
		NamedLayer layer = (NamedLayer) source;
		writer.startNode("Name");
		writer.setValue(layer.getName());
		writer.endNode();
		for(Style style:layer.getStyles()) {
			writer.startNode(style.getClass().getSimpleName());
			context.convertAnother(style);
			/*
			if (style.getName().length()>0) {
				writer.startNode("Name");
				writer.setValue(style.getName());
				writer.endNode();				
			}
			if (style.isUserStyle()) {
				UserStyle userStyle = (UserStyle) style;
				for(FeatureTypeStyle fts:userStyle.getFeatureTypeStyles()) {
					
				}
			} else {
				NamedStyle namedStyle = (NamedStyle) style;
			}
			*/
			writer.endNode();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		NamedLayer layer = new NamedLayer();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if (reader.getNodeName().equalsIgnoreCase("name")) {
            	layer.setName(reader.getValue());
            } else {
            	Style style;
            	if (reader.getNodeName().equalsIgnoreCase("UserStyle")) {
            		style = (Style) context.convertAnother(layer,UserStyle.class);
            	} else {
            		style = (Style) context.convertAnother(layer,NamedStyle.class);
            	}
            	layer.getStyles().add(style);
            }
        	reader.moveUp();
        }
		return layer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class classe) {return classe.equals(NamedLayer.class);}

}
