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

import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.appli.render.texture.ShaderFactory;
import fr.ign.cogit.geoxygene.style.filter.LayerFilter;
import fr.ign.cogit.geoxygene.style.filter.LayerFilterIdentity;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLProgramAccessor;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleVertex;
import fr.ign.cogit.geoxygene.util.gl.GLTools;

/**
 * @author JeT
 * 
 */
public abstract class AbstractGeoxygeneBlendingMode implements
        GeoxygeneBlendingMode {

    private LayerFilter filter = null;
    private LayerViewGLPanel glPanel = null;

    /**
     * @param filter
     */
    public AbstractGeoxygeneBlendingMode(LayerFilter filter,
            LayerViewGLPanel glPanel) {
        super();
        this.filter = filter;
        this.glPanel = glPanel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.gl.GeoxygeneBlendingMode#getFilter()
     */
    @Override
    public LayerFilter getFilter() {
        return this.filter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.gl.GeoxygeneBlendingMode#getProgram()
     */
    @Override
    public GLProgram getProgram() throws GLException {
        String shaderId = LayerViewGLPanel.getBlendingProgramName(this);
        GLProgramAccessor accessor = this.getGlContext().getProgramAccessor(
                shaderId);
        GLProgram program = null;
        if (accessor == null) {
            // set the accessor
            GLProgramAccessor blendingAccessor = this
                    .createScreenspaceBlendingAccessor(this.filter);
            this.getGlContext().addProgram(shaderId, blendingAccessor);
        }
        program = this.getGlContext().setCurrentProgram(shaderId);
        return program;
    }

    public abstract String getFragmentShaderFilename();

    private GLContext getGlContext() throws GLException {
        return this.glPanel.getGlContext();
    }

    /**
     * @param worldspaceVertexShaderId
     * @throws GLException
     */
    protected final GLProgram createProgram(String fragmentShaderFilename,
            LayerFilter filter) throws GLException, IOException {

        // color program
        Subshader shader = null;
        // special cases with null, the program name is not changed but a
        // LayerFilterIdentity is used
        if (filter != null) {
            shader = ShaderFactory.createFilterShader(filter);
        } else {
            shader = ShaderFactory
                    .createFilterShader(new LayerFilterIdentity());
        }
        GLProgram program = new GLProgram(
                LayerViewGLPanel.getBlendingProgramName(this));
        program.addVertexShader(
                GLTools.readFileAsString(LayerViewGLPanel.screenspaceVertexShaderFilename),
                LayerViewGLPanel.screenspaceVertexShaderFilename);
        program.addFragmentShader(
                GLTools.readFileAsString(this.getFragmentShaderFilename()),
                this.getFragmentShaderFilename());
        shader.configureProgram(program);
        program.addInputLocation(GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        program.addInputLocation(GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        program.addInputLocation(GLSimpleVertex.vertexColorVariableName,
                GLSimpleVertex.vertexColorLocation);
        program.addUniform(LayerViewGLPanel.globalOpacityUniformVarName);
        program.addUniform(LayerViewGLPanel.objectOpacityUniformVarName);
        program.addUniform(LayerViewGLPanel.colorTexture1UniformVarName);
        program.addUniform(LayerViewGLPanel.textureScaleFactorUniformVarName);
        program.addUniform(LayerViewGLPanel.antialiasingSizeUniformVarName);
        program.addUniform(LayerViewGLPanel.backgroundTextureUniformVarName);
        program.addUniform(LayerViewGLPanel.foregroundTextureUniformVarName);
        shader.declareUniforms(program);
        return program;
    }

    public final GLProgramAccessor createScreenspaceBlendingAccessor(
            LayerFilter filter) {
        return new GLProgramAccessorBlending(this.getFragmentShaderFilename(),
                this.filter);

    }

    /**
     * @author JeT This accessor returns a program created using the given
     *         shader descriptor
     */
    private class GLProgramAccessorBlending implements GLProgramAccessor {

        private LayerFilter filter = null;
        private String fragmentShaderFilename = null;

        /**
         * @param program
         */
        public GLProgramAccessorBlending(String fragmentShaderFilename,
                LayerFilter filter) {
            super();
            this.filter = filter;
            this.fragmentShaderFilename = fragmentShaderFilename;
        }

        @Override
        public GLProgram getGLProgram() throws GLException {
            try {
                return AbstractGeoxygeneBlendingMode.this.createProgram(
                        this.fragmentShaderFilename, this.filter);
            } catch (IOException e) {
                throw new GLException(e);
            }
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
                + ((this.filter == null) ? 0 : this.filter.hashCode());
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
        AbstractGeoxygeneBlendingMode other = (AbstractGeoxygeneBlendingMode) obj;
        if (this.filter == null) {
            if (other.filter != null) {
                return false;
            }
        } else if (!this.filter.equals(other.filter)) {
            return false;
        }
        return true;
    }

}
