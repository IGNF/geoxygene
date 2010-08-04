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

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Julien Perret
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractLayer implements Layer {
	
    @XmlElement(name = "Name", required = true)
	private String name;
	@Override
	public String getName() { return this.name; }
	@Override
	public void setName(String name) { this.name = name; }

    @XmlElement(name = "Description", required = false)
    private String description;
    @Override
    public String getDescription() { return this.description; }
    @Override
    public void setDescription(String d) { this.description = d; }

    //@XmlElement(name = "Description")
    //protected Description description;
    //@XmlElement(name = "LayerFeatureConstraints")
    //protected LayerFeatureConstraints layerFeatureConstraints;
	
    @XmlElements({
        @XmlElement(name = "UserStyle", type = UserStyle.class),
        @XmlElement(name = "NamedStyle", type = NamedStyle.class)
    })
	List<Style> styles = new ArrayList<Style>();

	@Override
	public List<Style> getStyles() {return this.styles;}

	@Override
	public void setStyles(List<Style> styles) {this.styles = styles;}

	@XmlTransient
	private boolean visible = true;
	@Override
	public boolean isVisible() {return this.visible;}
	@Override
	public void setVisible(boolean visible) {this.visible = visible;}

	@XmlTransient
	private boolean selectable = true;
	@Override
	public boolean isSelectable() { return this.selectable; }
	@Override
	public void setSelectable(boolean newSelectable) {
	    this.selectable = newSelectable;
	}
    @XmlTransient
    private boolean symbolized = true;
    @Override
    public boolean isSymbolized() { return this.symbolized; }
    @Override
    public void setSymbolized(boolean newSymbolized) {
        this.symbolized = newSymbolized;
    }
    @Override
    public Symbolizer getSymbolizer() {
        return this.getStyles().get(0).getSymbolizer();
    }
    @XmlTransient
    private Map<RasterSymbolizer, BufferedImage> rasterImage = new HashMap<RasterSymbolizer, BufferedImage>();
    @Override
    public void setImage(RasterSymbolizer symbolizer, BufferedImage image) {
        this.rasterImage.put(symbolizer, image);
    }
    @Override
    public BufferedImage getImage(RasterSymbolizer symbolizer) {
        return this.rasterImage.get(symbolizer);
    }
}
