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

package fr.ign.cogit.geoxygene.appli.render.primitive;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.render.LwjglLayerRenderer;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.ExpressiveRenderingDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor;

/**
 * @author JeT
 * 
 */
public class GL4FeatureRendererFactory {

    private static final Logger logger = Logger
            .getLogger(GL4FeatureRendererFactory.class.getName()); // logger

    public static GL4FeatureRenderer createRenderer(Layer layer,
            Symbolizer symbolizer, LwjglLayerRenderer layerRenderer) {
        GL4FeatureRenderer renderer = null;
        if (symbolizer instanceof LineSymbolizer) {
            // Line Symbolizer cases
            LineSymbolizer lineSymbolizer = (LineSymbolizer) symbolizer;
            ExpressiveRenderingDescriptor style = lineSymbolizer.getStroke()
                    .getExpressiveRendering();
            if (style != null
                    && (style instanceof StrokeTextureExpressiveRenderingDescriptor)) {
                StrokeTextureExpressiveRenderingDescriptor strtex = (StrokeTextureExpressiveRenderingDescriptor) style;
                GL4FeatureRendererLinePainting gl4FeatureRendererLinePainting = new GL4FeatureRendererLinePainting(
                        layerRenderer, strtex);
                renderer = gl4FeatureRendererLinePainting;
                System.err
                        .println("renderer Line Painting with shader descriptor code = "
                                + gl4FeatureRendererLinePainting
                                        .getShaderDescriptor().hashCode());
            } else if (style != null
                    && (style instanceof BasicTextureExpressiveRenderingDescriptor)) {
                BasicTextureExpressiveRenderingDescriptor strtex = (BasicTextureExpressiveRenderingDescriptor) style;
                renderer = new GL4FeatureRendererBezier(layerRenderer, strtex);
            }
            if (style != null) {
                System.err.println("style type "
                        + style.getClass().getCanonicalName());
            }
        } else if (symbolizer instanceof PolygonSymbolizer) {
            // Polygon symbolizer cases
            renderer = new GL4FeatureRendererBasic(layerRenderer);
        }
        if (renderer == null) {
            logger.warn("layer " + layer.getName() + " with symbolizer "
                    + symbolizer + " use default renderer");
            renderer = new GL4FeatureRendererBasic(layerRenderer);
        }
        return renderer;
    }

}
