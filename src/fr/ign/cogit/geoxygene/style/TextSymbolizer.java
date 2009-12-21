/**
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
 * 
 */

package fr.ign.cogit.geoxygene.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.NoninvertibleTransformException;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.feature.FT_Feature;

/**
 * @author Julien Perret
 *
 */
public class TextSymbolizer extends AbstractSymbolizer {
    @Override
    public boolean isTextSymbolizer() {return true;}

    private String label;
    /**
     * Renvoie la valeur de l'attribut label.
     * @return la valeur de l'attribut label
     */
    public String getLabel() {return this.label;}
    /**
     * Affecte la valeur de l'attribut label.
     * @param label l'attribut label à affecter
     */
    public void setLabel(String label) {this.label = label;}

    private Font font;
    /**
     * Renvoie la valeur de l'attribut font.
     * @return la valeur de l'attribut font
     */
    public Font getFont() {return this.font;}
    /**
     * Affecte la valeur de l'attribut font.
     * @param font l'attribut font à affecter
     */
    public void setFont(Font font) {this.font = font;}

    private LabelPlacement labelPlacement;
    /**
     * Renvoie la valeur de l'attribut labelPlacement.
     * @return la valeur de l'attribut labelPlacement
     */
    public LabelPlacement getLabelPlacement() {return this.labelPlacement;}
    /**
     * Affecte la valeur de l'attribut labelPlacement.
     * @param labelPlacement l'attribut labelPlacement à affecter
     */
    public void setLabelPlacement(LabelPlacement labelPlacement) {this.labelPlacement = labelPlacement;}

    private Halo halo;
    /**
     * Renvoie la valeur de l'attribut halo.
     * @return la valeur de l'attribut halo
     */
    public Halo getHalo() {return this.halo;}
    /**
     * Affecte la valeur de l'attribut halo.
     * @param halo l'attribut halo à affecter
     */
    public void setHalo(Halo halo) {this.halo = halo;}

    private Fill fill;
    /**
     * Renvoie la valeur de l'attribut fill.
     * @return la valeur de l'attribut fill
     */
    public Fill getFill() {return this.fill;}

    /**
     * Affecte la valeur de l'attribut fill.
     * @param fill l'attribut fill à affecter
     */
    public void setFill(Fill fill) {this.fill = fill;}
    @Override
    public void paint(FT_Feature feature, Viewport viewport, Graphics2D graphics) {
	if (this.getLabel()==null) return;
	try {
	    Shape shape = viewport.toShape(feature.getGeom());
	    if (shape == null) return;
	    String text = (String) feature.getAttribute(this.getLabel());
	    this.paint(text, shape, graphics);
	} catch (NoninvertibleTransformException e) {e.printStackTrace();}
    }

    public void paint(String text, Shape shape, Graphics2D graphics) {
	Color fillColor = Color.black;
	if (this.getFill()!=null) fillColor = this.getFill().getColor();
	java.awt.Font awtFont = null;
	if (this.getFont()!=null) awtFont = this.getFont().toAwfFont();
	if (awtFont==null) awtFont = new java.awt.Font("Default",java.awt.Font.PLAIN,10); //$NON-NLS-1$
	Color haloColor = null;
	float haloRadius = 1.0f;
	if (this.getHalo()!=null) {
	    if (this.getHalo().getFill()!=null) haloColor = this.getHalo().getFill().getColor();
	    else haloColor = Color.white;
	    haloRadius = this.getHalo().getRadius();
	}
	if (text==null) return;
	// Find the size of string s in font f in the current Graphics context g.
	graphics.setFont(awtFont);
	// Center text horizontally and vertically

	FontRenderContext frc = graphics.getFontRenderContext();
	GlyphVector gv = awtFont.createGlyphVector(frc, text);
	//halo
	if (haloColor!=null) {
	    Shape textShape=gv.getOutline();//TODO reposition on the shape
	    graphics.setColor(haloColor);
	    graphics.setStroke(new BasicStroke(haloRadius,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
	    graphics.draw(textShape);
	}
	graphics.setColor(fillColor);
	graphics.drawGlyphVector(gv,(float)(shape.getBounds2D().getMinX()+shape.getBounds2D().getMaxX())/2, (float)(shape.getBounds2D().getMinY()+shape.getBounds2D().getMaxY())/2);//TODO reposition on the shape
    }
}
