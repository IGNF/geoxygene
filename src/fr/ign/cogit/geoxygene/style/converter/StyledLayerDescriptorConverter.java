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

import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.NamedLayer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.UserLayer;

/**
 * @author Julien Perret
 *
 */
public class StyledLayerDescriptorConverter implements Converter {

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,MarshallingContext context) {
		StyledLayerDescriptor sld = (StyledLayerDescriptor) source;
		for (Layer layer:sld.getLayers()) {
			if (layer instanceof NamedLayer) {
				writer.startNode("NamedLayer");
				context.convertAnother(layer);
				writer.endNode();
			} else if (layer instanceof UserLayer) {
				writer.startNode("UserLayer");
				context.convertAnother(layer);
				writer.endNode();
			}
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		StyledLayerDescriptor sld = new StyledLayerDescriptor();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            Layer layer;
            if (reader.getNodeName().equalsIgnoreCase("NamedLayer")) {
            	layer = (Layer) context.convertAnother(sld,NamedLayer.class);
            } else /*if (reader.getNodeName().equalsIgnoreCase("UserLayer"))*/ {
            	layer = (Layer) context.convertAnother(sld,UserLayer.class);
            }
            sld.getLayers().add(layer);
        	reader.moveUp();
        }
		return sld;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class classe) {return classe.equals(StyledLayerDescriptor.class);}
}
