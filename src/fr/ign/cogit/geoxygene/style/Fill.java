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

package fr.ign.cogit.geoxygene.style;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Julien Perret
 *
 */
public class Fill {
	public enum LineJoin {MITRE,ROUND,BEVEL};
	public enum LineCap {BUTT,ROUND,SQUARE};

	private GraphicFill graphicFill = null;
	
	/**
	 * Renvoie la valeur de l'attribut graphicFill.
	 * @return la valeur de l'attribut graphicFill
	 */
	public GraphicFill getGraphicFill() {return this.graphicFill;}

	/**
	 * Affecte la valeur de l'attribut graphicFill.
	 * @param graphicFill l'attribut graphicFill à affecter
	 */
	public void setGraphicFill(GraphicFill graphicFill) {this.graphicFill = graphicFill;}

	private List<CssParameter> cssParameters = new ArrayList<CssParameter>();

	/**
	 * Renvoie la valeur de l'attribut cssParameters.
	 * @return la valeur de l'attribut cssParameters
	 */
	public List<CssParameter> getCssParameters() {return this.cssParameters;}

	/**
	 * Affecte la valeur de l'attribut cssParameters.
	 * @param cssParameters l'attribut cssParameters à affecter
	 */
	public void setCssParameters(List<CssParameter> cssParameters) {
		this.cssParameters = cssParameters;
		for (CssParameter parameter:cssParameters) {
			if (parameter.getName().equalsIgnoreCase("fill")) {
				this.setFill(Color.decode(parameter.getValue()));
			} else {
				if (parameter.getName().equalsIgnoreCase("fill-opacity")) {
					this.setFillOpacity(Float.parseFloat(parameter.getValue()));
				}
			}
		}
	}
	
	private Color fill = Color.gray;

	/**
	 * Renvoie la valeur de l'attribut fill.
	 * @return la valeur de l'attribut fill
	 */
	public Color getFill() {return this.fill;}

	/**
	 * Affecte la valeur de l'attribut fill.
	 * @param fill l'attribut fill à affecter
	 */
	public void setFill(Color fill) {
		this.fill = fill;
		for (CssParameter parameter:cssParameters) {
			if (parameter.getName().equalsIgnoreCase("fill")) {
				String rgb = Integer.toHexString(fill.getRGB());
				rgb = rgb.substring(2, rgb.length());
				parameter.setValue("#"+rgb);
			}
		}
	}
	
	private float fillOpacity = 1.0f;
	
	/**
	 * Renvoie la valeur de l'attribut fillOpacity.
	 * @return la valeur de l'attribut fillOpacity
	 */
	public float getFillOpacity() {return this.fillOpacity;}

	/**
	 * Affecte la valeur de l'attribut fillOpacity.
	 * @param fillOpacity l'attribut fillOpacity à affecter
	 */
	public void setFillOpacity(float fillOpacity) {
		this.fillOpacity = fillOpacity;
		for (CssParameter parameter:cssParameters) {
			if (parameter.getName().equalsIgnoreCase("fillOpacity")) {
				parameter.setValue(Float.toString(fillOpacity));
			}
		}
	}

	private Color color = null;
	public Color getColor() {
		if (color==null) {
			if (fillOpacity==1.0f) color = fill;
			else color = new Color(fill.getRed(),fill.getGreen(),fill.getBlue(),(int)(fillOpacity*255));
		}
		return color;
	}
	/**
	 * @param newColor
	 */
	public void setColor(Color newColor) {
		this.setFill(newColor);
		if (fillOpacity==1.0f) color = fill;
		else color = new Color(fill.getRed(),fill.getGreen(),fill.getBlue(),(int)(fillOpacity*255));
	}
}
