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

import fr.ign.cogit.geoxygene.appli.gl.Subshader;
import fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.ShaderDescriptor;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;

/**
 * @author JeT
 * 
 */
public class BasicTextureExpressiveRendering implements ExpressiveRendering {
    private BasicTextureExpressiveRenderingDescriptor descriptor = null;
    private Subshader shader = null;

    /**
     * @param strtex
     * @param descriptor
     */
    public BasicTextureExpressiveRendering(
            BasicTextureExpressiveRenderingDescriptor descriptor) {
        super();
        this.setDescriptor(descriptor);
    }

    /**
     * @param descriptor
     *            the descriptor to set
     */
    public final void setDescriptor(
            BasicTextureExpressiveRenderingDescriptor descriptor) {
        this.descriptor = descriptor;

        this.shader = ShaderFactory.createShader(this.descriptor
                .getShaderDescriptor());
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#getShaderDescriptor()
     */
    public Subshader getShader() {
        return this.shader;
    }

    /**
     * @return the descriptor
     */
    public BasicTextureExpressiveRenderingDescriptor getDescriptor() {
        return this.descriptor;
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor#getTransitionSize()
     */
    public double getTransitionSize() {
        return this.descriptor.getTransitionSize();
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor#getShaderDescriptor()
     */
    public ShaderDescriptor getShaderDescriptor() {
        return this.descriptor.getShaderDescriptor();
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor#getPaperTextureFilename()
     */
    public String getPaperTextureFilename() {
        return this.descriptor.getPaperTextureFilename();
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor#getBrushTextureFilename()
     */
    public String getBrushTextureFilename() {
        return this.descriptor.getBrushTextureFilename();
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor#getBrushStartLength()
     */
    public int getBrushStartLength() {
        return this.descriptor.getBrushStartLength();
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor#getBrushEndLength()
     */
    public int getBrushEndLength() {
        return this.descriptor.getBrushEndLength();
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor#getBrushAspectRatio()
     */
    public double getBrushAspectRatio() {
        return this.descriptor.getBrushAspectRatio();
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor#getPaperSizeInCm()
     */
    public double getPaperSizeInCm() {
        return this.descriptor.getPaperSizeInCm();
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor#getPaperReferenceMapScale()
     */
    public double getPaperReferenceMapScale() {
        return this.descriptor.getPaperReferenceMapScale();
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor#getPaperDensity()
     */
    public double getPaperDensity() {
        return this.descriptor.getPaperDensity();
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor#getBrushDensity()
     */
    public double getBrushDensity() {
        return this.descriptor.getBrushDensity();
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor#getStrokePressure()
     */
    public double getStrokePressure() {
        return this.descriptor.getStrokePressure();
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor#getSharpness()
     */
    public double getSharpness() {
        return this.descriptor.getSharpness();
    }

    @Override
    public void initializeRendering(GLProgram programId) {
        // nothing to perform

    }

    @Override
    public void finalizeRendering(GLProgram programId) {
        // nothing to perform

    }

}
