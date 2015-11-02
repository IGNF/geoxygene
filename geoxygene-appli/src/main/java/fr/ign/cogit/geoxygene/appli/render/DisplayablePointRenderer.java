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

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.render.groups.RenderingGroup;
import fr.ign.cogit.geoxygene.appli.render.primitive.DisplayablePoint;
import fr.ign.cogit.geoxygene.style.ExternalGraphic;
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;

public class DisplayablePointRenderer extends DisplayableRenderer<DisplayablePoint> {

    public DisplayablePointRenderer(Viewport _viewport) {
        super(_viewport);
    }

    @Override
    public boolean render(DisplayablePoint displayable_to_draw, double global_opacity) {
        boolean global_success = true;
        if (displayable_to_draw.getSymbolizer().isPointSymbolizer()) {
            PointSymbolizer ps = (PointSymbolizer) displayable_to_draw.getSymbolizer();
            if (ps.getGraphic() != null) {
                if (ps.getGraphic().getMarks() != null && !ps.getGraphic().getMarks().isEmpty()) {
                    for (Mark m : ps.getGraphic().getMarks()) {
                        Stroke s = m.getStroke();
                        Fill f = m.getFill();
                        List<Object> elements = new ArrayList<Object>();
                        if (s != null)
                            elements.add(s);
                        if (f != null)
                            elements.add(f);
                        global_success &= super.render(displayable_to_draw, global_opacity * ps.getGraphic().getOpacity(), elements.toArray());
                    }
                }
            }
            if (ps.getGraphic().getExternalGraphics() != null && !ps.getGraphic().getExternalGraphics().isEmpty()) {
                Logger.getRootLogger().error("ExternalGraphic is not yet implemented for the GL version of Geoxygene");
            }
        }else {
            Logger.getRootLogger().error("Cannot apply a symbolizer of type " + displayable_to_draw.getSymbolizer().getClass().getSimpleName() + " to a Point");
            global_success = false;
        }
        return global_success;
    }

    @Override
    protected Collection<GLComplex> getComplexesForGroup(RenderingGroup g, DisplayablePoint disp) {
        if (g.getStyleElement() instanceof ExternalGraphic) {
            Logger.getRootLogger().error("ExternalGraphic is not yet implemented for the GL version of Geoxygene");
        } else {
            return disp.getMarkGLComplexes(g.getStyleElement());
        }
        return null;
    }

}
