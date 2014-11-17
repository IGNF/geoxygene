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

import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.style.BlendingModeMultiply;
import fr.ign.cogit.geoxygene.style.filter.LayerFilter;

/**
 * @author JeT
 * 
 */
public class GeoxygeneBlendingModeMultiply extends
        AbstractGeoxygeneBlendingMode {

    public static final String FragmentShaderFilename = "./src/main/resources/shaders/blending-multiply.frag.glsl";

    public static final String BlendingMultiplyPrefix = "multiply";
    private BlendingModeMultiply blendingMode = null;

    /**
     * @param blendingMode
     */
    public GeoxygeneBlendingModeMultiply(BlendingModeMultiply blendingMode,
            LayerFilter filter, LayerViewGLPanel glPanel) {
        super(filter, glPanel);
        this.blendingMode = blendingMode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.gl.GeoxygeneBlendingMode#getBlendingMode()
     */
    @Override
    public BlendingModeMultiply getBlendingMode() {
        return this.blendingMode;
    }

    @Override
    public String getPrefix() {
        return BlendingMultiplyPrefix;
    }

    @Override
    public String getFragmentShaderFilename() {
        return FragmentShaderFilename;
    }

}
