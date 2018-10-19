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

import java.net.URI;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Symbolizer;

/**
 * @author JeT factory for creating GLDisplayables from a geOx geometry
 */
public final class DisplayableFactory {

    private static final Logger logger = Logger.getLogger(DisplayableFactory.class.getName()); // logger

    /**
     * private constructor for factory
     */
    private DisplayableFactory() {
        // factory
    }

    /**
     * @param feature
     * @param symbolizer
     * @param displayable
     * @param geometry
     * @param layerRenderer
     * @param viewport
     * @param layer
     * @param textures_root_uri 
     * @param partialRenderer
     * @return
     */
    public static GLDisplayable generateDisplayable(final IFeature feature, final Symbolizer symbolizer, IGeometry geometry, Viewport viewport, Layer layer, URI textures_root_uri) {
        GLDisplayable disp = null;
        if (geometry.isPolygon()) {
            disp = new DisplayableSurface(layer.getName() + "-polygon #" + feature.getId(),(IPolygon) geometry, feature, symbolizer,viewport,textures_root_uri);
        } else if (geometry.isMultiSurface()) {
            disp = new DisplayableSurface(layer.getName() + "-multisurface #" + feature.getId(), (IMultiSurface<?>) geometry, feature, symbolizer,viewport,textures_root_uri);
        } else if (geometry.isMultiCurve()) {
            disp = new DisplayableCurve(layer.getName() + "-multicurve #" + feature.getId(),  (IMultiCurve<?>) geometry, feature, symbolizer,viewport,textures_root_uri);
        } else if (geometry.isLineString()) {
            disp = new DisplayableCurve(layer.getName() + "-linestring #" + feature.getId(),  (ILineString) geometry, feature, symbolizer,viewport,textures_root_uri);
        } else if (geometry.isPoint() || (geometry instanceof IMultiPoint)) {
            disp = new DisplayablePoint(layer.getName() + "-multipoint #" + feature.getId(), geometry, feature, symbolizer,viewport,textures_root_uri);
        } else {
            logger.warn("LwjglLayerRenderer cannot handle geometry type " + geometry.getClass().getSimpleName());
            return null;
        }
        return disp;
    }

}
