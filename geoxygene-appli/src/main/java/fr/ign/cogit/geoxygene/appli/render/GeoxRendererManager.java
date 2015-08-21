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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.style.Fill2DDescriptor;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.TextSymbolizer;
import fr.ign.cogit.geoxygene.style.RasterSymbolizer;
import fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.GradientSubshaderDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.StrokeExpressiveRenderingDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor;
import fr.ign.cogit.geoxygene.util.gl.GLComplexRenderer;

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
    private static final Map<Symbolizer, GeoxComplexRenderer> textRenderers = new HashMap<Symbolizer, GeoxComplexRenderer>();
    private static Set<GeoxComplexRenderer> renderers = null;

    private GeoxRendererManager() {
        // private constructor
    }

    /**
     * Get all managed renderers
     * 
     * @return
     */
    public static Set<GeoxComplexRenderer> getRenderers() {
        if (renderers == null) {
            renderers = new HashSet<GeoxComplexRenderer>();
        }
        return renderers;
    }

    private static void invalidateRenderers() {
        renderers = null;
    }

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
                StrokeExpressiveRenderingDescriptor style = stroke
                        .getExpressiveRendering();

                if (style != null
                        && (style instanceof StrokeTextureExpressiveRenderingDescriptor)) {
                    StrokeTextureExpressiveRenderingDescriptor strtex = (StrokeTextureExpressiveRenderingDescriptor) style;
                    GeoxComplexRendererLinePainting gl4FeatureRendererLinePainting = new GeoxComplexRendererLinePainting(
                            layerRenderer, symbolizer, strtex);
                    renderer = gl4FeatureRendererLinePainting;
                } else if (style != null
                        && (style instanceof BasicTextureExpressiveRenderingDescriptor)) {
                    BasicTextureExpressiveRenderingDescriptor strtex = (BasicTextureExpressiveRenderingDescriptor) style;
                    renderer = new GeoxComplexRendererBezier(layerRenderer,
                            symbolizer, strtex);
                }
            }
            if (renderer == null) {
                renderer = new GeoxComplexRendererBasic(layerRenderer,
                        symbolizer);
                // logger.error("No known association between symbolizer "
                // + symbolizer + " and a Line Renderer...");
                // return null;
            }
            lineRenderers.put(symbolizer, renderer);
            invalidateRenderers();
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

            if (symbolizer.isPolygonSymbolizer()) {
                PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) symbolizer;
                Stroke stroke = polygonSymbolizer.getStroke();
                Fill2DDescriptor fill2dDescriptor = polygonSymbolizer.getFill()
                        .getFill2DDescriptor();
                if (fill2dDescriptor instanceof GradientSubshaderDescriptor) {
                    // GradientTextureDescriptor gradientDescriptor =
                    // (GradientTextureDescriptor) fill2dDescriptor;
                    renderer = new GeoxComplexRendererGradient(layerRenderer,
                            polygonSymbolizer);
                }

            } else if (symbolizer.isPointSymbolizer()) {
                // default renderer is ok
            } else if (symbolizer.isRasterSymbolizer()) {
                RasterSymbolizer rasterSymbolizer = (RasterSymbolizer) symbolizer;
                
                // create a renderer for Raster
                renderer = new GeoxComplexRendererRaster(layerRenderer, rasterSymbolizer);               
            } else {
                logger.error("No known association between symbolizer "
                        + symbolizer.getClass().getSimpleName()
                        + " and a SurfaceRenderer...");
                return null;
            }
            // default renderer if none has been set
            if (renderer == null) {
                renderer = new GeoxComplexRendererBasic(layerRenderer,
                        symbolizer);
                // logger.debug("a surface renderer is created for symbolizer "
                // + symbolizer);
            }
            surfaceRenderers.put(symbolizer, renderer);
            invalidateRenderers();
            return renderer;
        }
    }

    public static GLComplexRenderer getOrCreateTextRenderer(
            TextSymbolizer symbolizer, LwjglLayerRenderer layerRenderer) {
        synchronized (textRenderers) {
            GeoxComplexRenderer renderer = textRenderers.get(symbolizer);
            if (renderer != null) {
                return renderer;
            }
            renderer = new GeoxComplexRendererText(layerRenderer, symbolizer);
            textRenderers.put(symbolizer, renderer);
            invalidateRenderers();
            logger.debug("a text renderer is created for symbolizer "
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
            renderer = new GeoxComplexRendererBasic(layerRenderer, symbolizer);
            pointRenderers.put(symbolizer, renderer);
            invalidateRenderers();
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
        synchronized (textRenderers) {
            for (GeoxComplexRenderer renderer : textRenderers.values()) {
                renderer.reset();
            }
            surfaceRenderers.clear();
        }
        invalidateRenderers();
    }

}
