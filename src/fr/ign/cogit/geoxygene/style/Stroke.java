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

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Julien Perret
 *
 */
public class Stroke {

	private GraphicType graphicType = null;

	/**
	 * Renvoie la valeur de l'attribut graphicType.
	 * @return la valeur de l'attribut graphicType
	 */
	public GraphicType getGraphicType() {return this.graphicType;}

	/**
	 * Affecte la valeur de l'attribut graphicType.
	 * @param graphicType l'attribut graphicType à affecter
	 */
	public void setGraphicType(GraphicType graphicType) {this.graphicType = graphicType;}

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
			if (parameter.getName().equalsIgnoreCase("stroke")) {
				stroke = Color.decode(parameter.getValue());
			} else if (parameter.getName().equalsIgnoreCase("stroke-opacity")) {
				strokeOpacity = Float.parseFloat(parameter.getValue());
			} else if (parameter.getName().equalsIgnoreCase("stroke-width")) {
				strokeWidth = Float.parseFloat(parameter.getValue());
			} else if (parameter.getName().equalsIgnoreCase("stroke-linejoin")) {
				this.setStrokeLineJoin(parameter.getValue());
			} else if (parameter.getName().equalsIgnoreCase("stroke-linecap")) {
				this.setStrokeLineCap(parameter.getValue());
			} else if (parameter.getName().equalsIgnoreCase("stroke-dasharray")) {
				this.setStrokeDashArray(parameter.getValue());
			} else if (parameter.getName().equalsIgnoreCase("stroke-dashoffset")) {
				this.setStrokeDashOffset(parameter.getValue());
			}
		}
	}

	private Color stroke = Color.black;
	/**
	 * Renvoie la valeur de l'attribut stroke.
	 * @return la valeur de l'attribut stroke
	 */
	public Color getStroke() {return this.stroke;}
	/**
	 * Affecte la valeur de l'attribut stroke.
	 * <p>
	 * Met à jout le paramètre CSS correspondant
	 * @param stroke l'attribut stroke à affecter
	 */
	public void setStroke(Color stroke) {
		this.stroke = stroke;
		for (CssParameter parameter:cssParameters) {
			if (parameter.getName().equalsIgnoreCase("stroke")) {
				String rgb = Integer.toHexString(stroke.getRGB());
				rgb = rgb.substring(2, rgb.length());
				parameter.setValue("#"+rgb);
			}
		}
	}

	private float strokeOpacity = 1.0f;
	/**
	 * Renvoie la valeur de l'attribut strokeOpacity.
	 * @return la valeur de l'attribut strokeOpacity
	 */
	public float getStrokeOpacity() {return this.strokeOpacity;}
	/**
	 * Affecte la valeur de l'attribut strokeOpacity.
	 * @param strokeOpacity l'attribut strokeOpacity à affecter
	 */
	public void setStrokeOpacity(float strokeOpacity) {
		this.strokeOpacity = strokeOpacity;
		for (CssParameter parameter:cssParameters) {
			if (parameter.getName().equalsIgnoreCase("strokeOpacity")) {
				parameter.setValue(Float.toString(strokeOpacity));
			}
		}
	}

	private float strokeWidth = 1.0f;
	/**
	 * Renvoie la valeur de l'attribut strokeWidth.
	 * @return la valeur de l'attribut strokeWidth
	 */
	public float getStrokeWidth() {return this.strokeWidth;}
	/**
	 * Affecte la valeur de l'attribut strokeWidth.
	 * @param strokeWidth l'attribut strokeWidth à affecter
	 */
	public void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = strokeWidth;
		for (CssParameter parameter:cssParameters) {
			if (parameter.getName().equalsIgnoreCase("strokeWidth")) {
				parameter.setValue(Float.toString(strokeWidth));
			}
		}
	}

	private int strokeLineJoin = BasicStroke.JOIN_ROUND;
	/**
	 * Renvoie la valeur de l'attribut strokeLineJoin.
	 * @return la valeur de l'attribut strokeLineJoin
	 */
	public int getStrokeLineJoin() {return this.strokeLineJoin;}
	/**
	 * Affecte la valeur de l'attribut strokeLineJoin.
	 * @param strokeLineJoin l'attribut strokeLineJoin à affecter
	 */
	public void setStrokeLineJoin(int strokeLineJoin) {
		this.strokeLineJoin = strokeLineJoin;
		for (CssParameter parameter:cssParameters) {
			if (parameter.getName().equalsIgnoreCase("strokeLineJoin")) {
				parameter.setValue(Integer.toString(strokeLineJoin));
			}
		}
	}
	/**
	 * Affecte la valeur de l'attribut strokeLineJoin.
	 * @param strokeLineJoin l'attribut strokeLineJoin à affecter
	 */
	private void setStrokeLineJoin(String strokeLineJoin) {
		if (strokeLineJoin.equalsIgnoreCase("mitre")) this.strokeLineJoin=BasicStroke.JOIN_MITER;
		else if (strokeLineJoin.equalsIgnoreCase("bevel")) this.strokeLineJoin=BasicStroke.JOIN_BEVEL;
		// sinon, c'est la valeur par défaut
	}

	private int strokeLineCap = BasicStroke.CAP_ROUND;
	/**
	 * Renvoie la valeur de l'attribut strokeLineCap.
	 * @return la valeur de l'attribut strokeLineCap
	 */
	public int getStrokeLineCap() {return this.strokeLineCap;}
	/**
	 * Affecte la valeur de l'attribut strokeLineCap.
	 * @param strokeLineCap l'attribut strokeLineCap à affecter
	 */
	public void setStrokeLineCap(int strokeLineCap) {
		this.strokeLineCap = strokeLineCap;
		for (CssParameter parameter:cssParameters) {
			if (parameter.getName().equalsIgnoreCase("strokeLineCap")) {
				parameter.setValue(Integer.toString(strokeLineCap));
			}
		}
	}
	/**
	 * Affecte la valeur de l'attribut strokeLineCap.
	 * @param strokeLineCap l'attribut strokeLineCap à affecter
	 */
	private void setStrokeLineCap(String strokeLineCap) {
		if (strokeLineCap.equalsIgnoreCase("butt")) this.strokeLineCap=BasicStroke.CAP_BUTT;
		else if (strokeLineCap.equalsIgnoreCase("square")) this.strokeLineCap=BasicStroke.CAP_SQUARE;
		// sinon, c'est la valeur par défaut
	}

	private float strokeDashOffset=0.0f;
	/**
	 * Renvoie la valeur de l'attribut strokeDashOffset.
	 * @return la valeur de l'attribut strokeDashOffset
	 */
	public float getStrokeDashOffset() {return this.strokeDashOffset;}
	/**
	 * Affecte la valeur de l'attribut strokeDashOffset.
	 * @param strokeDashOffset l'attribut strokeDashOffset à affecter
	 */
	public void setStrokeDashOffset(float strokeDashOffset) {
		this.strokeDashOffset = strokeDashOffset;
		for (CssParameter parameter:cssParameters) {
			if (parameter.getName().equalsIgnoreCase("strokeDashOffset")) {
				parameter.setValue(Float.toString(strokeDashOffset));
			}
		}
	}
	/**
	 * @param value
	 */
	private void setStrokeDashOffset(String value) {setStrokeDashOffset(Float.parseFloat(value));}

	private float[] strokeDashArray = null;
	/**
	 * Renvoie la valeur de l'attribut strokeDashArray.
	 * @return la valeur de l'attribut strokeDashArray
	 */
	public float[] getStrokeDashArray() {return this.strokeDashArray;}
	/**
	 * Affecte la valeur de l'attribut strokeDashArray.
	 * @param strokeDashArray l'attribut strokeDashArray à affecter
	 */
	public void setStrokeDashArray(float[] strokeDashArray) {
		this.strokeDashArray = strokeDashArray;
		for (CssParameter parameter:cssParameters) {
			if (parameter.getName().equalsIgnoreCase("strokeDashArray")) {
				String dashArray ="";
				for (int i = 0 ; i < strokeDashArray.length ; i++) 
					dashArray+=strokeDashArray[i]+" ";
				parameter.setValue(dashArray);
			}
		}
	}
	/**
	 * @param value
	 */
	private void setStrokeDashArray(String value) {
		String[] values = value.split(" ");
		strokeDashArray = new float[values.length];
		for (int index=0;index<values.length;index++) strokeDashArray[index]=Float.parseFloat(values[index]);
	}

	private Color color = null;
	public Color getColor() {
		if (color==null) {
			if (strokeOpacity==1.0f) color = stroke;
			else color = new Color(stroke.getRed(),stroke.getGreen(),stroke.getBlue(),(int)(strokeOpacity*255));
		}
		return color;
	}
	/**
	 * @param newColor
	 */
	public void setColor(Color newColor) {
		this.setStroke(newColor);
		if (strokeOpacity==1.0f) color = stroke;
		else color = new Color(stroke.getRed(),stroke.getGreen(),stroke.getBlue(),(int)(strokeOpacity*255));
	}

	private java.awt.Stroke awtStroke = null;
	/**
	 * @return
	 */
	public java.awt.Stroke toAwtStroke() {
		if (awtStroke==null) {
			awtStroke=new BasicStroke(this.getStrokeWidth(),this.getStrokeLineCap(),this.getStrokeLineJoin(),10.0f,this.getStrokeDashArray(),this.getStrokeDashOffset());
		}
		return awtStroke;
	}
}
