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

import java.io.IOException;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.style.expressive.DefaultLineShaderDescriptor;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLTools;

/**
 * @author JeT
 * 
 */
public class DefaultSubshader1D implements Subshader {

    private static final Logger logger = Logger
            .getLogger(DefaultSubshader1D.class.getName()); // logger

    private static final String subshaderFilename = "./src/main/resources/shaders/subshader1d.default.glsl";

    public DefaultSubshader1D(DefaultLineShaderDescriptor descriptor) {
    }

    @Override
    public void declareUniforms(GLProgram program) {
    }

    /**
     * Initialize the shader before rendering (set uniforms)
     * 
     * @throws GLException
     */
    @Override
    public void setUniforms(GLProgram program) throws GLException {

    }

    @Override
    public void configureProgram(GLProgram program) throws GLException {
        try {
            program.addFragmentShader(
                    GLTools.readFileAsString(subshaderFilename),
                    subshaderFilename);
        } catch (IOException e) {
            throw new GLException(e);
        }

    }
}
