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

package fr.ign.cogit.geoxygene.appli.render.texture;

import fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor;

/**
 * @author JeT
 * 
 */
public class BasicTextureExpressiveRendering implements ExpressiveRendering {
    private BasicTextureExpressiveRenderingDescriptor descriptor = null;

    /**
     * @param strtex
     * @param descriptor
     */
    public BasicTextureExpressiveRendering(
            BasicTextureExpressiveRenderingDescriptor descriptor) {
        super();
        this.descriptor = descriptor;
    }

    /**
     * @return the descriptor
     */
    public BasicTextureExpressiveRenderingDescriptor getDescriptor() {
        return this.descriptor;
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor#getBrushTextureFilename()
     */
    public String getBrushTextureFilename() {
        return this.descriptor.getBrushTextureFilename();
    }

    /**
     * @param brushTextureFilename
     * @see fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor#setBrushTextureFilename(java.lang.String)
     */
    public void setBrushTextureFilename(String brushTextureFilename) {
        this.descriptor.setBrushTextureFilename(brushTextureFilename);
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor#getAspectRatio()
     */
    public double getAspectRatio() {
        return this.descriptor.getAspectRatio();
    }

    /**
     * @param aspectRation
     * @see fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor#setAspectRatio(double)
     */
    public void setAspectRatio(double aspectRation) {
        this.descriptor.setAspectRatio(aspectRation);
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor#getTransitionSize()
     */
    public double getTransitionSize() {
        return this.descriptor.getTransitionSize();
    }

    /**
     * @param transitionSize
     * @see fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor#setTransitionSize(double)
     */
    public void setTransitionSize(double transitionSize) {
        this.descriptor.setTransitionSize(transitionSize);
    }

}
