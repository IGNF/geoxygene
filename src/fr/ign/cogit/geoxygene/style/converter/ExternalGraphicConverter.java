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

import fr.ign.cogit.geoxygene.style.ExternalGraphic;

/**
 * @author Julien Perret
 *
 */
public class ExternalGraphicConverter implements Converter {

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		ExternalGraphic externalGraphic = (ExternalGraphic) source;
		writer.startNode("ExternalGraphic");
		if (externalGraphic.getHref()!=null) {
			writer.startNode("OnlineResource");
			writer.addAttribute("xmlns:xlink","http://www.w3.org/1999/xlink");
			writer.addAttribute("xlink:type","simple");
			writer.addAttribute("xlink:href",externalGraphic.getHref());
			writer.endNode();
		}
		if (externalGraphic.getFormat()!=null) {
			writer.startNode("Format");
			context.convertAnother(externalGraphic.getFormat());
			writer.endNode();
		}
		writer.endNode();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		ExternalGraphic externalGraphic = new ExternalGraphic();
		while(reader.hasMoreChildren()) {
			reader.moveDown();
			if (reader.getNodeName().equalsIgnoreCase("OnlineResource")) {
				externalGraphic.setHref(reader.getAttribute("xlink:href"));
			} else if (reader.getNodeName().equalsIgnoreCase("Format")) {
				externalGraphic.setFormat(reader.getValue());
			}
			reader.moveUp();
		}
		return externalGraphic;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class classe) {return classe.equals(ExternalGraphic.class);}

}
