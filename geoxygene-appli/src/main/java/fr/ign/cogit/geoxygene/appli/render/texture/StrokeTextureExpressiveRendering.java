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
import fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.RenderingException;

/**
 * @author JeT
 * 
 */
public class StrokeTextureExpressiveRendering implements ExpressiveRendering {
    private StrokeTextureExpressiveRenderingDescriptor descriptor = null;
    private Subshader shader = null;

    /**
     * @param descriptor
     */
    public StrokeTextureExpressiveRendering(
            StrokeTextureExpressiveRenderingDescriptor descriptor) {
        super();
        this.setDescriptor(descriptor);
    }

    /**
     * @return the descriptor
     */
    public StrokeTextureExpressiveRenderingDescriptor getDescriptor() {
        return this.descriptor;
    }

    /**
     * @param descriptor
     *            the descriptor to set
     */
    public final void setDescriptor(
            StrokeTextureExpressiveRenderingDescriptor descriptor) {
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
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#getPaperTextureFilename()
     */
    public String getPaperTextureFilename() {
        return this.descriptor.getPaperTextureFilename();
    }

    /**
     * @param paperTextureFilename
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#setPaperTextureFilename(java.lang.String)
     */
    public void setPaperTextureFilename(String paperTextureFilename) {
        this.descriptor.setPaperTextureFilename(paperTextureFilename);
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#getBrushTextureFilename()
     */
    public String getBrushTextureFilename() {
        return this.descriptor.getBrushTextureFilename();
    }

    /**
     * @param brushTextureFilename
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#setBrushTextureFilename(java.lang.String)
     */
    public void setBrushTextureFilename(String brushTextureFilename) {
        this.descriptor.setBrushTextureFilename(brushTextureFilename);
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#getBrushStartLength()
     */
    public int getBrushStartLength() {
        return this.descriptor.getBrushStartLength();
    }

    /**
     * @param brushStartLength
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#setBrushStartLength(int)
     */
    public void setBrushStartLength(int brushStartLength) {
        this.descriptor.setBrushStartLength(brushStartLength);
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#getBrushEndLength()
     */
    public int getBrushEndLength() {
        return this.descriptor.getBrushEndLength();
    }

    /**
     * @param brushEndLength
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#setBrushEndLength(int)
     */
    public void setBrushEndLength(int brushEndLength) {
        this.descriptor.setBrushEndLength(brushEndLength);
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#getSampleSize()
     */
    public double getSampleSize() {
        return this.descriptor.getSampleSize();
    }

    /**
     * @param sampleSize
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#setSampleSize(double)
     */
    public void setSampleSize(double sampleSize) {
        this.descriptor.setSampleSize(sampleSize);
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#getMinAngle()
     */
    public double getMinAngle() {
        return this.descriptor.getMinAngle();
    }

    /**
     * @param minAngle
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#setMinAngle(double)
     */
    public void setMinAngle(double minAngle) {
        this.descriptor.setMinAngle(minAngle);
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#getBrushAspectRatio()
     */
    public double getBrushSize() {
        return this.descriptor.getBrushAspectRatio();
    }

    /**
     * @param brushSize
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#setBrushAspectRatio(double)
     */
    public void setBrushSize(double brushSize) {
        this.descriptor.setBrushAspectRatio(brushSize);
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#getPaperScaleFactor()
     */
    public double getPaperScaleFactor() {
        return this.descriptor.getPaperScaleFactor();
    }

    /**
     * @param paperScaleFactor
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#setPaperScaleFactor(double)
     */
    public void setPaperScaleFactor(double paperScaleFactor) {
        this.descriptor.setPaperScaleFactor(paperScaleFactor);
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#getPaperReferenceMapScale()
     */
    public double getPaperReferenceMapScale() {
        return this.descriptor.getPaperReferenceMapScale();
    }

    /**
     * @param paperReferenceMapScale
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#setPaperReferenceMapScale(double)
     */
    public void setPaperReferenceMapScale(double paperReferenceMapScale) {
        this.descriptor.setPaperReferenceMapScale(paperReferenceMapScale);
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#getPaperDensity()
     */
    public double getPaperDensity() {
        return this.descriptor.getPaperDensity();
    }

    /**
     * @param paperDensity
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#setPaperDensity(double)
     */
    public void setPaperDensity(double paperDensity) {
        this.descriptor.setPaperDensity(paperDensity);
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#getBrushDensity()
     */
    public double getBrushDensity() {
        return this.descriptor.getBrushDensity();
    }

    /**
     * @param brushDensity
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#setBrushDensity(double)
     */
    public void setBrushDensity(double brushDensity) {
        this.descriptor.setBrushDensity(brushDensity);
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#getStrokePressure()
     */
    public double getStrokePressure() {
        return this.descriptor.getStrokePressure();
    }

    /**
     * @param strokePressure
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#setStrokePressure(double)
     */
    public void setStrokePressure(double strokePressure) {
        this.descriptor.setStrokePressure(strokePressure);
    }

    /**
     * @return
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#getSharpness()
     */
    public double getSharpness() {
        return this.descriptor.getSharpness();
    }

    /**
     * @param sharpness
     * @see fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor#setSharpness(double)
     */
    public void setSharpness(double sharpness) {
        this.descriptor.setSharpness(sharpness);
    }

    @Override
    public void initializeRendering(GLProgram programId)
            throws RenderingException {
        // nothing to perform
    }

    @Override
    public void finalizeRendering(GLProgram programId)
            throws RenderingException {
        // nothing to perform
    }

}
