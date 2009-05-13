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

import fr.ign.cogit.geoxygene.style.AnchorPoint;
import fr.ign.cogit.geoxygene.style.Displacement;
import fr.ign.cogit.geoxygene.style.LinePlacement;
import fr.ign.cogit.geoxygene.style.Placement;
import fr.ign.cogit.geoxygene.style.PointPlacement;

/**
 * @author Julien Perret
 *
 */
public class PlacementConverter implements Converter {

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		Placement placement = (Placement) source;
		if (placement!=null) {
			if (placement instanceof PointPlacement) {
				PointPlacement pointPlacement = (PointPlacement) placement;
				if (pointPlacement.getAnchorPoint()!=null) {
					writer.startNode("AnchorPoint");
					writer.startNode("AnchorPointX");
					writer.setValue(String.valueOf(pointPlacement.getAnchorPoint().getAnchorPointX()));
					writer.endNode();
					writer.startNode("AnchorPointY");
					writer.setValue(String.valueOf(pointPlacement.getAnchorPoint().getAnchorPointY()));
					writer.endNode();
					writer.endNode();
				}
				if (pointPlacement.getDisplacement()!=null) {
					writer.startNode("Displacement");
					writer.startNode("DisplacementX");
					writer.setValue(String.valueOf(pointPlacement.getDisplacement().getDisplacementX()));
					writer.endNode();
					writer.startNode("DisplacementY");
					writer.setValue(String.valueOf(pointPlacement.getDisplacement().getDisplacementY()));
					writer.endNode();
					writer.endNode();
				}
				if (pointPlacement.getRotation()!=0.0) {
					writer.startNode("Displacement");
					writer.setValue(String.valueOf(pointPlacement.getRotation()));
					writer.endNode();
				}
			} else if (placement instanceof LinePlacement) {
				LinePlacement linePlacement = (LinePlacement) placement;
				if (linePlacement.getPerpendicularOffset()!=0) {
					writer.startNode("PerpendicularOffset");
					writer.setValue(String.valueOf(linePlacement.getPerpendicularOffset()));
					writer.endNode();					
				}
			}
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		if (reader.getNodeName().equalsIgnoreCase("PointPlacement")) {
			PointPlacement pointPlacement = new PointPlacement();
			while(reader.hasMoreChildren()) {
				reader.moveDown();
				if (reader.getNodeName().equalsIgnoreCase("Rotation")) {
					pointPlacement.setRotation(Float.parseFloat(reader.getValue()));
				} else if (reader.getNodeName().equalsIgnoreCase("AnchorPoint")) {
					AnchorPoint anchorPoint = new AnchorPoint();
					while(reader.hasMoreChildren()) {
						reader.moveDown();
						if (reader.getNodeName().equalsIgnoreCase("AnchorPointX")) {
							anchorPoint.setAnchorPointX(Float.parseFloat(reader.getValue()));
						} else if (reader.getNodeName().equalsIgnoreCase("AnchorPointY")) {
							anchorPoint.setAnchorPointY(Float.parseFloat(reader.getValue()));
						}
						reader.moveUp();
					}
					pointPlacement.setAnchorPoint(anchorPoint);
				} else if (reader.getNodeName().equalsIgnoreCase("Displacement")) {
					Displacement displacement = new Displacement();
					while(reader.hasMoreChildren()) {
						reader.moveDown();
						if (reader.getNodeName().equalsIgnoreCase("DisplacementX")) {
							displacement.setDisplacementX(Float.parseFloat(reader.getValue()));
						} else if (reader.getNodeName().equalsIgnoreCase("DisplacementY")) {
							displacement.setDisplacementY(Float.parseFloat(reader.getValue()));
						}
						reader.moveUp();
					}
					pointPlacement.setDisplacement(displacement);
				}
				reader.moveUp();
			}
			return pointPlacement;
		}
		if (reader.getNodeName().equalsIgnoreCase("LinePlacement")) {
			LinePlacement linePlacement = new LinePlacement();
			if (reader.hasMoreChildren()) {
				reader.moveDown();
				if (reader.getNodeName().equalsIgnoreCase("PerpendicularOffset")) {
					linePlacement.setPerpendicularOffset(Float.parseFloat(reader.getValue()));
				}
				reader.moveUp();
			}
			return linePlacement;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class classe) {return Placement.class.isAssignableFrom(classe);}

}
