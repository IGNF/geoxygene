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

package fr.ign.cogit.geoxygene.appli.render;

import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLComplexRenderer;
import fr.ign.cogit.geoxygene.util.gl.RenderingException;

/**
 * @author JeT This renderer writes GL Code to perform GL rendering
 */
public interface GeoxComplexRenderer extends GLComplexRenderer {

    /**
     * this method is called the first time a rendering is requested of this
     * renderer. Multiple cycle initialize/render/finalize rendering can be
     * called between activate and switch methods
     * 
     * @throws RenderingException
     */
    void activateRenderer() throws RenderingException;

    /**
     * this method is called just before changing type of renderer or at the end
     * of global rendering. Multiple cycle initialize/render/finalize rendering
     * can be called between activate and switch methods
     * 
     * @throws RenderingException
     */
    void switchRenderer() throws RenderingException;

    @Override
    void initializeRendering() throws RenderingException;

    @Override
    void render(GLComplex complex, double opacity) throws RenderingException;

    @Override
    void finalizeRendering() throws RenderingException;

    @Override
    void reset();

    /**
     * Get the symbolizer used to create this renderer
     */
    public Symbolizer getSymbolizer();

    /**
     * @return if rendering is done inside a FBO context or not
     */
    public boolean getFBORendering();

    /**
     * this method has to be called before launching render if the rendering is
     * done in a FBO context
     * 
     * @param fboRendering
     *            the fboRendering to set
     */
    public void setFBORendering(boolean fboRendering);
}
