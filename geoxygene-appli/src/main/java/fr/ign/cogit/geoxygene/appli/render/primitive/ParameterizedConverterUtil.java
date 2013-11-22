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

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;

/**
 * @author JeT
 * 
 */
public final class ParameterizedConverterUtil {

    private static Logger logger = Logger.getLogger(ParameterizedConverterUtil.class.getName());

    private static ParameterizedConverter converter = new CachedParameterizedConverter(); // converter to use for everybody

    /**
     * Private Constructor
     */
    private ParameterizedConverterUtil() {
        // utility class
    }

    /**
     * @return the current converter
     */
    public static ParameterizedConverter getConverter() {
        return converter;
    }

    /**
     * @param converter
     *            the converter to set
     */
    public static void setConverter(final ParameterizedConverter converter) {
        ParameterizedConverterUtil.converter = converter;
    }

    /**
     * Convert geometry to line drawing primitives
     * 
     * @param geometry
     *            geometry to convert
     * @param viewport
     *            viewport used to generate shapes
     * @return a list of drawing primitives
     */
    public static DrawingPrimitive generateParameterizedPolyline(final LineSymbolizer lineSymbolizer, final IGeometry geometry, final Viewport viewport) {
        //ligne suivante à déplacer ailleurs (update line ?)
        //        List<Shape> shapes = ParameterizedLineConverterUtil.getShapeList(lineSymbolizer, geometry, viewport, false);
        //ici il faudrait pouvoir faire un update à partir de viewport
        DrawingPrimitive primitive = ParameterizedLineConverterUtil.generateParameterizedPolyline(lineSymbolizer, geometry, viewport);
        return primitive;
    }

    /**
     * Convert geometry to polygon drawing primitives
     * 
     * @param geometry
     *            geometry to convert
     * @param viewport
     *            viewport used to generate shapes
     * @return a list of drawing primitives
     */
    public static DrawingPrimitive generateParameterizedPolygon(final PolygonSymbolizer polygonSymbolizer, final IGeometry geometry, final Viewport viewport) {
        DrawingPrimitive primitive = ParameterizedPolygonConverterUtil.generateParameterizedPolygon(polygonSymbolizer, geometry, viewport);
        return primitive;
    }
}
