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
import fr.ign.cogit.geoxygene.style.filter.LayerFilter;
import fr.ign.cogit.geoxygene.util.Pair;
import fr.ign.cogit.geoxygene.util.gl.GLException;

/**
 * @author JeT, Bertrand Duménieu
 * 
 */
public class GeoxygeneBlendingFactory {

    private static final BlendingMode DEFAULT_BLENDING_MODE = BlendingMode.Normal;
    private static final Map<Pair<BlendingMode, LayerFilter>, GeoxygeneBlendingMode> blendingModes = new HashMap<Pair<BlendingMode, LayerFilter>, GeoxygeneBlendingMode>();

    /**
     * Blending shaders paths.
     * TODO : this should be in a resource file, not in the source code. 
     */
    private static final String shader_normalblending_path = "./src/main/resources/shaders/blending-normal.frag.glsl";
    private static final String shader_multiplyblending_path = "./src/main/resources/shaders/blending-multiply.frag.glsl";
    private static final String shader_hightoneblending_path = "./src/main/resources/shaders/blending-hightone.frag.glsl";
    private static final String shader_overlayblending_path = "./src/main/resources/shaders/blending-overlay.frag.glsl";
    
    /** register the shader resources.
     * TODO: this should not be here. 
     **/
    static{
        GLResourcesManager manager = GLResourcesManager.getInstance();
        manager.registerResource("shadernormalblending", shader_normalblending_path, false);
        manager.registerResource("shadermultiplyblending", shader_multiplyblending_path, false);
        manager.registerResource("shaderhightoneblending", shader_hightoneblending_path, false);
        manager.registerResource("shaderoverlayblending", shader_overlayblending_path, false);
    }
    
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
        switch(blendingMode){
            case Normal:
                return new GeoxygeneBlendingModeImpl(filter, glPanel, "shadernormalblending");
            case Multiply:
                return new GeoxygeneBlendingModeImpl(filter, glPanel, "shadermultiplyblending");
            case Overlay:
                return new GeoxygeneBlendingModeImpl(filter, glPanel, "shaderoverlayblending");
            case HighTone:
                return new GeoxygeneBlendingModeImpl(filter, glPanel, "shaderhightoneblending");
        }
        throw new UnsupportedOperationException("Blending mode "
                + blendingMode.getClass().getSimpleName() + " is not supported");
    }

}
