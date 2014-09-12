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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.render.LwjglLayerRenderer;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.ExpressiveRenderingDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor;

/**
 * @author JeT
 * 
 */
public class GeoxRendererManager {

    private static final Logger logger = Logger
            .getLogger(GeoxRendererManager.class.getName()); // logger

    private static final Map<Symbolizer, GeoxComplexRenderer> pointRenderers = new HashMap<Symbolizer, GeoxComplexRenderer>();
    private static final Map<Symbolizer, GeoxComplexRenderer> lineRenderers = new HashMap<Symbolizer, GeoxComplexRenderer>();
    private static final Map<Symbolizer, GeoxComplexRenderer> surfaceRenderers = new HashMap<Symbolizer, GeoxComplexRenderer>();

    public static GeoxComplexRenderer getOrCreateLineRenderer(
            Symbolizer symbolizer, LwjglLayerRenderer layerRenderer) {
        synchronized (lineRenderers) {
            GeoxComplexRenderer renderer = lineRenderers.get(symbolizer);
            if (renderer != null) {
                return renderer;
            }

            Stroke stroke = null;
            if (symbolizer instanceof LineSymbolizer) {
                // Line Symbolizer cases
                LineSymbolizer lineSymbolizer = (LineSymbolizer) symbolizer;
                stroke = lineSymbolizer.getStroke();
            }

            if (symbolizer instanceof PolygonSymbolizer) {
                PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) symbolizer;
                stroke = polygonSymbolizer.getStroke();
            }

            if (stroke != null) {
                ExpressiveRenderingDescriptor style = stroke
                        .getExpressiveRendering();

                if (style != null
                        && (style instanceof StrokeTextureExpressiveRenderingDescriptor)) {
                    StrokeTextureExpressiveRenderingDescriptor strtex = (StrokeTextureExpressiveRenderingDescriptor) style;
                    GeoxComplexRendererLinePainting gl4FeatureRendererLinePainting = new GeoxComplexRendererLinePainting(
                            layerRenderer, strtex);
                    renderer = gl4FeatureRendererLinePainting;
                } else if (style != null
                        && (style instanceof BasicTextureExpressiveRenderingDescriptor)) {
                    BasicTextureExpressiveRenderingDescriptor strtex = (BasicTextureExpressiveRenderingDescriptor) style;
                    renderer = new GeoxComplexRendererBezier(layerRenderer,
                            strtex);
                }
            }
            if (renderer == null) {
                renderer = new GeoxComplexRendererBasic(layerRenderer);
                // logger.error("No known association between symbolizer "
                // + symbolizer + " and a Line Renderer...");
                // return null;
            }
            lineRenderers.put(symbolizer, renderer);
            return renderer;
        }
    }

    public static GeoxComplexRenderer getOrCreateSurfaceRenderer(
            Symbolizer symbolizer, LwjglLayerRenderer layerRenderer) {
        synchronized (surfaceRenderers) {
            GeoxComplexRenderer renderer = surfaceRenderers.get(symbolizer);
            if (renderer != null) {
                return renderer;
            }

            renderer = new GeoxComplexRendererBasic(layerRenderer);
            surfaceRenderers.put(symbolizer, renderer);
            logger.debug("a surface renderer is created for symbolizer "
                    + symbolizer);
            return renderer;
        }
    }

    public static GeoxComplexRenderer getOrCreatePointRenderer(
            PointSymbolizer symbolizer, LwjglLayerRenderer layerRenderer) {
        synchronized (pointRenderers) {
            GeoxComplexRenderer renderer = pointRenderers.get(symbolizer);
            if (renderer != null) {
                return renderer;
            }
            renderer = new GeoxComplexRendererBasic(layerRenderer);
            pointRenderers.put(symbolizer, renderer);
            logger.debug("a point renderer is created for symbolizer "
                    + symbolizer);
            return renderer;
        }

    }

    public static void reset() {
        synchronized (pointRenderers) {
            for (GeoxComplexRenderer renderer : pointRenderers.values()) {
                renderer.reset();
            }
            pointRenderers.clear();
        }
        synchronized (lineRenderers) {
            for (GeoxComplexRenderer renderer : lineRenderers.values()) {
                renderer.reset();
            }
            lineRenderers.clear();
        }
        synchronized (surfaceRenderers) {
            for (GeoxComplexRenderer renderer : surfaceRenderers.values()) {
                renderer.reset();
            }
            surfaceRenderers.clear();
        }
    }

}
