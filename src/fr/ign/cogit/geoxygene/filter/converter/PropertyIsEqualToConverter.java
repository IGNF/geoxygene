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

package fr.ign.cogit.geoxygene.filter.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import fr.ign.cogit.geoxygene.filter.PropertyIsEqualTo;
import fr.ign.cogit.geoxygene.filter.expression.Literal;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;

/**
 * @author Julien Perret
 *
 */
public class PropertyIsEqualToConverter implements Converter {

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		PropertyIsEqualTo property = (PropertyIsEqualTo) source;
		if (property.getPropertyName()!=null) {
			writer.startNode("PropertyName");
			context.convertAnother(property.getPropertyName());
			writer.endNode();
		}
		if (property.getLiteral()!=null) {
			writer.startNode("Literal");
			context.convertAnother(property.getLiteral());
			writer.endNode();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		PropertyIsEqualTo property = new PropertyIsEqualTo();
        while (reader.hasMoreChildren()) {
    		reader.moveDown();
            if (reader.getNodeName().equalsIgnoreCase("Literal")) {
            	Literal literal = new Literal();
            	literal.setValue(reader.getValue());
            	/*
            	try {
            		double value = Double.parseDouble(reader.getValue());
            		literal.setValue(new Double(value));
            	} catch (NumberFormatException e) {
            		try {
            			int value = Integer.parseInt(reader.getValue());
            			literal.setValue(new Integer(value));
            		} catch (NumberFormatException e2) {
                		try {
                			float value = Float.parseFloat(reader.getValue());
                			literal.setValue(new Float(value));
                		} catch (NumberFormatException e3) {
                			literal.setValue(reader.getValue());
                		}
            		}
            	}
            	*/
            	property.setLiteral(literal);
            } else if (reader.getNodeName().equalsIgnoreCase("PropertyName")) {
            	PropertyName propertyName = new PropertyName();
            	propertyName.setPropertyName(reader.getValue());
            	property.setPropertyName(propertyName);
            }
        	reader.moveUp();
        }
		return property;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class classe) {return classe.equals(PropertyIsEqualTo.class);}
}
