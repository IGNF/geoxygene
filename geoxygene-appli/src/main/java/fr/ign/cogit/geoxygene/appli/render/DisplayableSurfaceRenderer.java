/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.appli.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.render.groups.RenderingGroup;
import fr.ign.cogit.geoxygene.appli.render.primitive.DisplayableSurface;
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.RasterSymbolizer;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;

public class DisplayableSurfaceRenderer extends DisplayableRenderer<DisplayableSurface> {

    public DisplayableSurfaceRenderer(Viewport _viewport) {
        super(_viewport);
    }

    @Override
    public boolean render(DisplayableSurface displayable, double layer_opacity) {
        List<Object> elements = new ArrayList<Object>();
        if (displayable.getSymbolizer() instanceof PolygonSymbolizer) {
            Stroke s = displayable.getSymbolizer().getStroke();
            Fill f = null;
            f = ((PolygonSymbolizer) displayable.getSymbolizer()).getFill();
            if (f != null)
                elements.add(f);
            if (s != null)
                elements.add(s);
        }
        // Special case if the Surface is the support for a Raster.
        if (displayable.getSymbolizer() instanceof RasterSymbolizer) {
            elements.add(displayable.getSymbolizer());
        }
        return super.render(displayable, layer_opacity, elements.toArray());
    }

    @Override
    protected Collection<GLComplex> getComplexesForGroup(RenderingGroup g, DisplayableSurface displayable_to_draw) {
        if (g.getStyleElement() instanceof Stroke)
            return ((DisplayableSurface) displayable_to_draw).getOutlinePrimitives();
        if (g.getStyleElement() instanceof Fill)
            return ((DisplayableSurface) displayable_to_draw).getInnerPrimitives();
        if (g.getStyleElement() instanceof RasterSymbolizer)
            return ((DisplayableSurface) displayable_to_draw).getInnerPrimitives();
        return null;
    }

}
