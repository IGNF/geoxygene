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
import fr.ign.cogit.geoxygene.style.expressive.GradientSubshaderDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.ShaderDescriptor;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.RenderingException;

/**
 * @author JeT
 * 
 */
public class GradientExpressiveRendering implements ExpressiveRendering {
    private GradientSubshaderDescriptor descriptor = null;
    private Subshader shader = null;

    /**
     * @param strtex
     * @param descriptor
     */
    public GradientExpressiveRendering(GradientSubshaderDescriptor descriptor) {
        super();
        this.setDescriptor(descriptor);
    }

    /**
     * @param descriptor
     *            the descriptor to set
     */
    public final void setDescriptor(GradientSubshaderDescriptor descriptor) {
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
    public GradientSubshaderDescriptor getDescriptor() {
        return this.descriptor;
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
     * @see fr.ign.cogit.geoxygene.style.expressive.GradientSubshaderDescriptor#getTextureResolution()
     */
    public double getTextureResolution() {
        return this.descriptor.getTextureResolution();
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.GradientSubshaderDescriptor#getMaxCoastlineLength()
     */
    public double getMaxCoastlineLength() {
        return this.descriptor.getMaxCoastlineLength();
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.GradientSubshaderDescriptor#getMapScale()
     */
    public double getMapScale() {
        return this.descriptor.getMapScale();
    }

    @Override
    public void initializeRendering(GLProgram program)
            throws RenderingException {
        if (this.shader != null) {
            try {
                this.shader.setUniforms(program);
            } catch (GLException e) {
                throw new RenderingException(e);
            }
        }
    }

    @Override
    public void finalizeRendering(GLProgram programId)
            throws RenderingException {
        // nothing to perform

    }

}
