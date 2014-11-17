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

package fr.ign.cogit.geoxygene.appli.gl;

import java.util.HashMap;
import java.util.Map;

import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.style.BlendingMode;
import fr.ign.cogit.geoxygene.style.BlendingModeMultiply;
import fr.ign.cogit.geoxygene.style.BlendingModeNormal;
import fr.ign.cogit.geoxygene.style.BlendingModeOverlay;
import fr.ign.cogit.geoxygene.style.filter.LayerFilter;
import fr.ign.cogit.geoxygene.util.Pair;
import fr.ign.cogit.geoxygene.util.gl.GLException;

/**
 * @author JeT
 * 
 */
public class GeoxygeneBlendingFactory {

    private static final BlendingMode DEFAULT_BLENDING_MODE = new BlendingModeNormal();
    private static final Map<Pair<BlendingMode, LayerFilter>, GeoxygeneBlendingMode> blendingModes = new HashMap<Pair<BlendingMode, LayerFilter>, GeoxygeneBlendingMode>();

    /**
     * private constructor
     */
    private GeoxygeneBlendingFactory() {
        // private constructor
    }

    public void invalidateCache() {
        blendingModes.clear();
    }

    public static GeoxygeneBlendingMode getGeoxygeneBlendingMode(
            BlendingMode blendingMode, LayerFilter filter,
            LayerViewGLPanel glPanel) throws GLException {
        Pair<BlendingMode, LayerFilter> key = new Pair<BlendingMode, LayerFilter>(
                blendingMode, filter);
        GeoxygeneBlendingMode geoxygeneBlendingMode = blendingModes.get(key);
        if (geoxygeneBlendingMode != null) {
            return geoxygeneBlendingMode;
        }

        geoxygeneBlendingMode = createGeoxygeneBlendingMode(blendingMode,
                filter, glPanel);
        blendingModes.put(key, geoxygeneBlendingMode);
        return geoxygeneBlendingMode;
    }

    /**
     * @param blendingMode
     * @return
     * @throws GLException
     */
    private static GeoxygeneBlendingMode createGeoxygeneBlendingMode(
            BlendingMode blendingMode, LayerFilter filter,
            LayerViewGLPanel glPanel) throws GLException {
        if (blendingMode == null) {
            return createGeoxygeneBlendingMode(DEFAULT_BLENDING_MODE, filter,
                    glPanel);
        }
        if (blendingMode instanceof BlendingModeNormal) {
            return new GeoxygeneBlendingModeNormal(
                    (BlendingModeNormal) blendingMode, filter, glPanel);
        }
        if (blendingMode instanceof BlendingModeOverlay) {
            return new GeoxygeneBlendingModeOverlay(
                    (BlendingModeOverlay) blendingMode, filter, glPanel);
        }
        if (blendingMode instanceof BlendingModeMultiply) {
            return new GeoxygeneBlendingModeMultiply(
                    (BlendingModeMultiply) blendingMode, filter, glPanel);
        }
        throw new UnsupportedOperationException("Blending mode "
                + blendingMode.getClass().getSimpleName() + " is not supported");
    }

}
