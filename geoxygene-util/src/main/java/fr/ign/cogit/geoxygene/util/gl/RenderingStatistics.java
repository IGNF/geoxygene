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

package fr.ign.cogit.geoxygene.util.gl;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author JeT
 * 
 */
public class RenderingStatistics {

    private static final Logger logger = Logger
            .getLogger(RenderingStatistics.class.getName()); // logger

    private static boolean on = false;
    private static long renderingStart = 0;
    private static long renderingEnd = 0;
    private static int nbProgramSwitch = 0;
    private static int nbDrawCall = 0;
    private static int nbGLComplex = 0;
    private static int meshCount = 0;
    private static int vertexCount = 0;

    /**
     * private constructor
     */
    private RenderingStatistics() {
        // TODO Auto-generated constructor stub
    }

    public static void startRendering() {
        if (on) {
            renderingStart = new Date().getTime();
            nbProgramSwitch = 0;
            nbDrawCall = 0;
            nbGLComplex = 0;
            meshCount = 0;
            vertexCount = 0;
        }
    }

    public static void endRendering() {
        if (on) {
            renderingEnd = new Date().getTime();
        }
    }

    public static void switchProgram() {
        if (on) {
            nbProgramSwitch++;
        }
    }

    public static void doDrawCall() {
        if (on) {
            nbDrawCall++;
        }
    }

    public static void drawGLComplex(GLComplex primitive) {
        if (on) {
            nbGLComplex++;
            meshCount += primitive.getMeshes().size();
            vertexCount += primitive.getVertices().size();
        }
    }

    public static void printStatistics() {
        if (on) {
            logger.info("------------------------------------------------------");
            logger.info("Rendering time = " + (renderingEnd - renderingStart)
                    + "ms");
            logger.info("nb program switch = " + nbProgramSwitch);
            logger.info("nb draw call = " + nbDrawCall);
            logger.info("nb gl complex = " + nbGLComplex);
            logger.info("nb meshes = " + meshCount);
            logger.info("nb vertices = " + vertexCount);
        }
    }

}
