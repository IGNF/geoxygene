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

import fr.ign.cogit.geoxygene.style.expressive.ParameterDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.ParameterDescriptorFloat;
import fr.ign.cogit.geoxygene.style.expressive.UserShaderDescriptor;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLTools;

/**
 * @author JeT
 * 
 */
public class UserSubshader implements Subshader {

    private static final Logger logger = Logger.getLogger(UserSubshader.class
            .getName()); // logger

    private String subshaderFilename = "./src/main/resources/shaders/linepainting.subshader.default.glsl";

    private UserShaderDescriptor descriptor = null;

    public UserSubshader(UserShaderDescriptor descriptor) {
        if (descriptor == null) {
            throw new IllegalArgumentException("Null descriptor");
        }
        this.descriptor = descriptor;
        this.subshaderFilename = descriptor.getFilename();
    }

    @Override
    public void declareUniforms(GLProgram program) throws GLException {
        for (ParameterDescriptor param : this.descriptor.getParameters()) {
            program.addUniform(param.getName());
        }
    }

    /**
     * Initialize the shader before rendering (set uniforms)
     * 
     * @throws GLException
     */
    @Override
    public void setUniforms(GLProgram program) throws GLException {
        for (ParameterDescriptor param : this.descriptor.getParameters()) {
            this.setUniform(program, param);
        }
    }

    /**
     * @param program
     * @param param
     * @throws GLException
     */
    private void setUniform(GLProgram program, ParameterDescriptor param)
            throws GLException {
        if (param instanceof ParameterDescriptorFloat) {
            ParameterDescriptorFloat floatParam = (ParameterDescriptorFloat) param;
            program.setUniform1f(floatParam.getName(), floatParam.getValue());
        } else {
            throw new IllegalStateException("no uniform set for "
                    + param.getClass().getSimpleName());
        }
    }

    @Override
    public void configureProgram(GLProgram program) throws GLException {
        try {
            program.addFragmentShader(
                    GLTools.readFileAsString(this.subshaderFilename),
                    this.subshaderFilename);
        } catch (IOException e) {
            throw new GLException(e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.descriptor == null) ? 0 : this.descriptor.hashCode());
        result = prime
                * result
                + ((this.subshaderFilename == null) ? 0
                        : this.subshaderFilename.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        UserSubshader other = (UserSubshader) obj;
        if (this.descriptor == null) {
            if (other.descriptor != null) {
                return false;
            }
        } else if (!this.descriptor.equals(other.descriptor)) {
            return false;
        }
        if (this.subshaderFilename == null) {
            if (other.subshaderFilename != null) {
                return false;
            }
        } else if (!this.subshaderFilename.equals(other.subshaderFilename)) {
            return false;
        }
        return true;
    }

}
