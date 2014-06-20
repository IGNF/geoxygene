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

package fr.ign.cogit.geoxygene.appli.ui;

import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.style.expressive.ExpressiveRendering;
import fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRendering;

/**
 * @author JeT
 * 
 */
public class ExpressiveRenderingUIFactory {

    /**
     * private constructor
     */
    private ExpressiveRenderingUIFactory() {
        // private constructor
    }

    /**
     * Create an expressiveRendering UI
     * 
     * @param expressiveRendering
     * @return
     */
    public static ExpressiveRenderingUI getExpressiveRenderingUIFactory(
            ExpressiveRendering expressiveRendering, ProjectFrame projectFrame) {
        if (expressiveRendering instanceof StrokeTextureExpressiveRendering) {
            return getExpressiveRenderingUI(
                    (StrokeTextureExpressiveRendering) expressiveRendering,
                    projectFrame);
        } else {
            throw new IllegalStateException("ExpressiveRendering type "
                    + expressiveRendering.getClass().getSimpleName() + " ");
        }
    }

    /**
     * Create a stroke texture Rendering UI
     * 
     * @param strtex
     * @return
     */
    private static StrokeTextureExpressiveRenderingUI getExpressiveRenderingUI(
            final StrokeTextureExpressiveRendering strtex,
            ProjectFrame projectFrame) {

        return new StrokeTextureExpressiveRenderingUI(strtex, projectFrame);
    }
}
