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

import fr.ign.cogit.geoxygene.filter.Filter;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.RasterSymbolizer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.TextSymbolizer;

/**
 * @author Julien Perret
 *
 */
public class RuleConverter implements Converter {

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		Rule rule = (Rule) source;
		if ((rule.getName()!=null)&&(rule.getName().length()>0)) {
			writer.startNode("Name");
			writer.setValue(rule.getName());
			writer.endNode();
		}
		if ((rule.getTitle()!=null)&&(rule.getTitle().length()>0)) {
			writer.startNode("Title");
			writer.setValue(rule.getTitle());
			writer.endNode();
		}
		if (rule.getFilter()!=null) {
			writer.startNode("Filter");
			context.convertAnother(rule.getFilter());
			writer.endNode();
		}
		for (Symbolizer symbolizer:rule.getSymbolizers()) {
			writer.startNode(symbolizer.getClass().getSimpleName());
			context.convertAnother(symbolizer);
			writer.endNode();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Rule rule = new Rule();
        while (reader.hasMoreChildren()) {
    		reader.moveDown();
            if (reader.getNodeName().equalsIgnoreCase("Name")) {
            	rule.setName(reader.getValue());
            } else if (reader.getNodeName().equalsIgnoreCase("Filter")) {
        		Filter filter = (Filter) context.convertAnother(rule,Filter.class);
        		rule.setFilter(filter);
        	} else {
            	Symbolizer symbolizer = null;
            	if (reader.getNodeName().equalsIgnoreCase("LineSymbolizer")) {
            		symbolizer = (Symbolizer) context.convertAnother(rule,LineSymbolizer.class,new LineSymbolizerConverter());
            	} else if (reader.getNodeName().equalsIgnoreCase("PointSymbolizer")) {
            		symbolizer = (Symbolizer) context.convertAnother(rule,PointSymbolizer.class);
            	} else if (reader.getNodeName().equalsIgnoreCase("PolygonSymbolizer")) {
            		symbolizer = (Symbolizer) context.convertAnother(rule,PolygonSymbolizer.class, new PolygonSymbolizerConverter());
            	} else if (reader.getNodeName().equalsIgnoreCase("TextSymbolizer")) {
            		symbolizer = (Symbolizer) context.convertAnother(rule,TextSymbolizer.class);
            	} else if (reader.getNodeName().equalsIgnoreCase("RasterSymbolizer")) {
            		symbolizer = (Symbolizer) context.convertAnother(rule,RasterSymbolizer.class);
            	} else {
            		System.out.println("ERROR dans le noeud "+reader.getNodeName()+" avec la valeur "+reader.getValue());
            	}
            	//if (symbolizer!=null) 
            	rule.getSymbolizers().add(symbolizer);
            }
        	reader.moveUp();
        }
		return rule;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class classe) {return classe.equals(Rule.class);}

}
