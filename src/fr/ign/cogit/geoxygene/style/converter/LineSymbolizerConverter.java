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

import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.Stroke;

/**
 * @author Julien Perret
 *
 */
public class LineSymbolizerConverter implements Converter {

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		LineSymbolizer symbolizer = (LineSymbolizer) source;
		if (symbolizer.getGeometryPropertyName().length()>0) {
			writer.startNode("Geometry");
			writer.startNode("ogc:PropertyName");
			writer.setValue(symbolizer.getGeometryPropertyName());
			writer.endNode();
			writer.endNode();
		}
		if (symbolizer.getStroke()!=null) {
			writer.startNode("Stroke");
			context.convertAnother(symbolizer.getStroke());
			writer.endNode();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		LineSymbolizer symbolizer = new LineSymbolizer();
		while (reader.hasMoreChildren()) {
			reader.moveDown();
	        if (reader.getNodeName().equalsIgnoreCase("geometry")) {
	    		reader.moveDown();
	    		if (reader.getNodeName().equalsIgnoreCase("ogc:PropertyName")) {
	    			symbolizer.setGeometryPropertyName(reader.getValue());
	    		}
	            reader.moveUp();
	        } else if (reader.getNodeName().equalsIgnoreCase("stroke")) {
	        	Stroke stroke = (Stroke) context.convertAnother(symbolizer,Stroke.class,new StrokeConverter());
	        	symbolizer.setStroke(stroke);
	        }
	        reader.moveUp();			
		}
		return symbolizer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class classe) {return classe.equals(LineSymbolizer.class);}

}
