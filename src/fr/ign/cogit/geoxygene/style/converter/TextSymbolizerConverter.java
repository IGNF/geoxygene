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

import fr.ign.cogit.geoxygene.style.CssParameter;
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.Font;
import fr.ign.cogit.geoxygene.style.Halo;
import fr.ign.cogit.geoxygene.style.LabelPlacement;
import fr.ign.cogit.geoxygene.style.Placement;
import fr.ign.cogit.geoxygene.style.TextSymbolizer;

/**
 * @author Julien Perret
 *
 */
public class TextSymbolizerConverter implements Converter {

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		TextSymbolizer symbolizer = (TextSymbolizer) source;
		if (symbolizer.getGeometryPropertyName().length()>0) {
			writer.startNode("Geometry");
			writer.startNode("ogc:PropertyName");
			writer.setValue(symbolizer.getGeometryPropertyName());
			writer.endNode();
			writer.endNode();
		}
		if (symbolizer.getLabel()!=null) {
			writer.startNode("Label");
			writer.startNode("ogc:PropertyName");
			writer.setValue(symbolizer.getLabel());
			writer.endNode();
			writer.endNode();			
		}
		if (symbolizer.getFont()!=null) {
			writer.startNode("Font");
			for (CssParameter param:symbolizer.getFont().getCssParameters()) {
				writer.startNode("CssParameter");
				context.convertAnother(param);
				writer.endNode();
			}
			writer.endNode();
		}
		if (symbolizer.getLabelPlacement()!=null) {
			writer.startNode("LabelPlacement");
			if (symbolizer.getLabelPlacement().getPlacement()!=null) {
				writer.startNode(symbolizer.getLabelPlacement().getClass().getSimpleName());
				context.convertAnother(symbolizer.getLabelPlacement());
				writer.endNode();
			}
			writer.endNode();
		}
		if (symbolizer.getFill()!=null) {
			writer.startNode("Fill");
			context.convertAnother(symbolizer.getFill());
			writer.endNode();
		}
		if (symbolizer.getHalo()!=null) {
			writer.startNode("Halo");
			context.convertAnother(symbolizer.getHalo());
			writer.endNode();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		TextSymbolizer symbolizer = new TextSymbolizer();
		while(reader.hasMoreChildren()) {
			reader.moveDown();
			if (reader.getNodeName().equalsIgnoreCase("geometry")) {
				reader.moveDown();
				if (reader.getNodeName().equalsIgnoreCase("ogc:PropertyName")) {
					symbolizer.setGeometryPropertyName(reader.getValue());
				}
				reader.moveUp();
			} else if (reader.getNodeName().equalsIgnoreCase("fill")) {
				Fill fill = (Fill) context.convertAnother(symbolizer,Fill.class);
				symbolizer.setFill(fill);
			} else if (reader.getNodeName().equalsIgnoreCase("font")) {
				Font font = new Font();
				List<CssParameter> parameters = new ArrayList<CssParameter>();
				while(reader.hasMoreChildren()) {
					reader.moveDown();
					if (reader.getNodeName().equalsIgnoreCase("CssParameter")) {
						CssParameter css = (CssParameter) context.convertAnother(symbolizer,CssParameter.class);
						parameters.add(css);
					}
					reader.moveUp();
				}
				font.setCssParameters(parameters);
				symbolizer.setFont(font);
			} else if (reader.getNodeName().equalsIgnoreCase("label")) {
				reader.moveDown();
				if (reader.getNodeName().equalsIgnoreCase("ogc:PropertyName")) {
					symbolizer.setLabel(reader.getValue());
				}
				reader.moveUp();
			} else if (reader.getNodeName().equalsIgnoreCase("labelplacement")) {
				if (reader.hasMoreChildren()) {
					reader.moveDown();
					LabelPlacement labelPlacement = new LabelPlacement();
					Placement placement = (Placement) context.convertAnother(symbolizer,Placement.class);
					labelPlacement.setPlacement(placement);
					symbolizer.setLabelPlacement(labelPlacement);
					reader.moveUp();
				}
			} else if (reader.getNodeName().equalsIgnoreCase("halo")) {
				Halo halo = new Halo();
				while(reader.hasMoreChildren()) {
					reader.moveDown();
					if (reader.getNodeName().equalsIgnoreCase("Radius")) {
						halo.setRadius(Float.parseFloat(reader.getValue()));
					} else if (reader.getNodeName().equalsIgnoreCase("Fill")) {
						Fill fill = (Fill) context.convertAnother(symbolizer,Fill.class);
						halo.setFill(fill);
					}
					reader.moveUp();
				}
				symbolizer.setHalo(halo);
			}
			reader.moveUp();
		}
		return symbolizer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class classe) {return classe.equals(TextSymbolizer.class);}

}
