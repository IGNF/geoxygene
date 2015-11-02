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

package fr.ign.cogit.geoxygene.appli.render.stats;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.render.GeoxygeneGLRenderer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;

/**
 * @author JeT RenderingStatistics is a GL/Geoxygene specific static class. Just
 *         set the "on" static boolean to true to enable displaying statistics
 *         on the rendering process
 */
public class RenderingStatistics {

    private static final Logger logger = Logger
            .getLogger(RenderingStatistics.class.getName()); // logger

    private static boolean on = false;
    private static long renderingStart = 0;
    private static long renderingEnd = 0;
    private static long overlayStart = 0;
    private static long overlayEnd = 0;
    private static int nbProgramSwitch = 0;
    private static int nbDrawCall = 0;
    private static int nbGLComplex = 0;
    private static int meshCount = 0;
    private static int vertexCount = 0;
    private static int triangleCount = 0;
    private static String userMessage = "";
    private static Map<GeoxygeneGLRenderer, RendererStatistics> renderers = new HashMap<GeoxygeneGLRenderer, RendererStatistics>();

    private static int nbCoupleFeatureSymbolizer = 0;
    private static Set<IFeature> features = new HashSet<IFeature>();
    private static Set<Symbolizer> symbolizers = new HashSet<Symbolizer>();

    /**
     * private constructor
     */
    private RenderingStatistics() {
        setStatistics(on);
    }

    public static boolean isActive() {
        return on;
    }

    public static void setStatistics(boolean on) {
        if (on) {
            logger.info("!!!!!!!!!!! Activate rendering Statistics !!!!!!!!!!!! This mode should be only used for debug purpose");
        }
        RenderingStatistics.on = on;
    }

    /**
     * @param renderer
     * @return
     */
    private static RendererStatistics getOrGenerateStatistics(
            GeoxygeneGLRenderer renderer) {
        RendererStatistics stats = renderers.get(renderer);
        if (stats == null) {
            stats = new RendererStatistics(renderer);
            renderers.put(renderer, stats);
        }
        return stats;
    }

    /**
     * @return the userMessage
     */
    public static String getUserMessage() {
        return userMessage;
    }

    /**
     * @param userMessage
     *            the userMessage to set
     */
    public static void setUserMessage(String userMessage) {
        RenderingStatistics.userMessage = userMessage;
    }

    public static void doActivateRenderer(GeoxygeneGLRenderer renderer) {
        if (!on) {
            return;
        }
        RendererStatistics stats = getOrGenerateStatistics(renderer);
        stats.doActivateRenderer();
    }

    public static void doSwitchRenderer(GeoxygeneGLRenderer renderer) {
        if (!on) {
            return;
        }
        RendererStatistics stats = getOrGenerateStatistics(renderer);
        stats.doSwitchRenderer();
    }

    public static void doInitializeRenderer(GeoxygeneGLRenderer renderer) {
        if (!on) {
            return;
        }
        RendererStatistics stats = getOrGenerateStatistics(renderer);
        stats.doInitializeRenderer();
    }

    public static void doFinalizeRenderer(GeoxygeneGLRenderer renderer) {
        if (!on) {
            return;
        }
        RendererStatistics stats = getOrGenerateStatistics(renderer);
        stats.doFinalizeRenderer();
    }

    public static void doStartRendering(GeoxygeneGLRenderer renderer) {
        if (!on) {
            return;
        }
        RendererStatistics stats = getOrGenerateStatistics(renderer);
        stats.doStartRendering();
    }

    public static void doStopRendering(GeoxygeneGLRenderer renderer) {
        if (!on) {
            return;
        }
        RendererStatistics stats = getOrGenerateStatistics(renderer);
        stats.doStopRendering();
    }

    public static void doRender(GeoxygeneGLRenderer renderer) {
        RendererStatistics stats = getOrGenerateStatistics(renderer);
        stats.doRender();
    }

    public static void startRendering() {
        if (!on) {
            return;
        }
        renderingStart = new Date().getTime();
        nbProgramSwitch = 0;
        nbDrawCall = 0;
        nbGLComplex = 0;
        meshCount = 0;
        vertexCount = 0;
        triangleCount = 0;
        nbCoupleFeatureSymbolizer = 0;
        renderers.clear();
        features.clear();
        symbolizers.clear();

    }

    public static void endRendering() {
        if (!on) {
            return;
        }
        renderingEnd = new Date().getTime();
    }

    public static void startOverlayRendering() {
        if (!on) {
            return;
        }
        overlayStart = new Date().getTime();

    }

    public static void endOverlayRendering() {
        if (!on) {
            return;
        }
        overlayEnd = new Date().getTime();
    }

    public static void switchProgram() {
        if (!on) {
            return;
        }
        nbProgramSwitch++;
    }

    public static void doDrawCall() {
        if (!on) {
            return;
        }
        nbDrawCall++;
    }

    public static void drawGLComplex(GLComplex primitive) {
        if (!on) {
            return;
        }
        nbGLComplex++;
        meshCount += primitive.getMeshes().size();
        vertexCount += primitive.getVertices().size();
        for (GLMesh mesh : primitive.getMeshes()) {
            triangleCount += (mesh.getLastIndex() - mesh.getFirstIndex()) / 3;
        }
    }

    public static void printStatistics(OutputStream out) {
        if (!on) {
            return;
        }
        PrintStream pos = new PrintStream(out);
        pos.println("------------------------------------------------------");
        pos.println(getUserMessage());
        pos.println("Rendering time = " + (renderingEnd - renderingStart)
                + "ms (overlay included)");
        pos.println("overlay time = " + (overlayEnd - overlayStart) + "ms");
        pos.println("nb program switch = " + nbProgramSwitch);
        pos.println("nb draw call = " + nbDrawCall);
        pos.println("nb gl complex = " + nbGLComplex);
        pos.println("nb meshes = " + meshCount);
        pos.println("nb vertices = " + vertexCount);
        pos.println("nb triangles (approx) = " + triangleCount);
        pos.println("nb couple feature/symbolizer renderered = "
                + nbCoupleFeatureSymbolizer);
        pos.println("nb different features = " + features.size());
        pos.println("nb different symbolizers = " + symbolizers.size());
        for (Map.Entry<GeoxygeneGLRenderer, RendererStatistics> entry : renderers
                .entrySet()) {
            // GeoxComplexRenderer renderer = entry.getKey();
            RendererStatistics stats = entry.getValue();
            ByteArrayOutputStream statsOut = new ByteArrayOutputStream();
            stats.printStatistics(statsOut);
            pos.println(statsOut.toString());
        }
    }

    public static void renderCoupleFeatureSymbolizer(IFeature feature,
            Symbolizer symbolizer) {
        features.add(feature);
        symbolizers.add(symbolizer);
        nbCoupleFeatureSymbolizer++;

    }

}
