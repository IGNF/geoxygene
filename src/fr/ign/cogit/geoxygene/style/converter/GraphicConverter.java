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

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import fr.ign.cogit.geoxygene.style.ExternalGraphic;
import fr.ign.cogit.geoxygene.style.Graphic;
import fr.ign.cogit.geoxygene.style.Mark;

/**
 * @author Julien Perret
 *
 */
public class GraphicConverter implements Converter {

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		Graphic graphic = (Graphic) source;
		writer.startNode("Graphic");
		for(ExternalGraphic externalGraphic : graphic.getExternalGraphics()) {
			context.convertAnother(externalGraphic);
		}
		for(Mark mark : graphic.getMarks()) {
			context.convertAnother(mark);
		}
		writer.startNode("Opacity");
		writer.setValue(String.valueOf(graphic.getOpacity()));
		writer.endNode();
		writer.startNode("Size");
		writer.setValue(String.valueOf(graphic.getSize()));
		writer.endNode();
		writer.startNode("Rotation");
		writer.setValue(String.valueOf(graphic.getRotation()));
		writer.endNode();
		writer.endNode();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Graphic graphic = new Graphic();
		List<ExternalGraphic> externalGraphics = new ArrayList<ExternalGraphic>();
		List<Mark> marks = new ArrayList<Mark>();
		while(reader.hasMoreChildren()) {
			reader.moveDown();
			if (reader.getNodeName().equalsIgnoreCase("ExternalGraphic")) {
				ExternalGraphic eg = (ExternalGraphic) context.convertAnother(graphic,ExternalGraphic.class);
				externalGraphics.add(eg);
			} else if (reader.getNodeName().equalsIgnoreCase("Mark")) {
				Mark mark = (Mark) context.convertAnother(graphic,Mark.class);
				marks.add(mark);
			} if (reader.getNodeName().equalsIgnoreCase("Opacity")) {
				float opacity = Float.parseFloat(reader.getValue());
				graphic.setOpacity(opacity);
			} else if (reader.getNodeName().equalsIgnoreCase("Size")) {
				float size = Float.parseFloat(reader.getValue());
				graphic.setSize(size);
			} else if (reader.getNodeName().equalsIgnoreCase("Rotation")) {
				float rotation = Float.parseFloat(reader.getValue());
				graphic.setRotation(rotation);
			}
            reader.moveUp();
        }
		graphic.setExternalGraphics(externalGraphics);
		graphic.setMarks(marks);
		return graphic;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class classe) {return classe.equals(Graphic.class);}
}
