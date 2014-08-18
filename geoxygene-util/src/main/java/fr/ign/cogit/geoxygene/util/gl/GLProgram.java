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

package fr.ign.cogit.geoxygene.util.gl;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

/**
 * @author JeT A GLProgram is equivalent to a gl-program with gl Uniform data
 *         management
 */
public class GLProgram {

    private static final Logger logger = Logger.getLogger(GLProgram.class
            .getName()); // logger

    private int programId = -1;
    private String name = "noname";

    // gl uniform variable name-gl location mapping
    private final Map<String, Integer> uniformLocations = new HashMap<String, Integer>();

    // gl input variable name-gl location mapping
    private final Map<String, Integer> inputLocations = new HashMap<String, Integer>();
    // uniform values
    private final List<GLUniform> uniforms = new ArrayList<GLUniform>();
    private int vertexShaderId = -1;
    private int fragmentShaderId = -1;
    private int geometryShaderId = -1;

    /**
     * @param name
     */
    public GLProgram(String name) {
        super();
        this.setName(name);
    }

    /**
     * @return the collection of registered uniform variables
     */
    public Set<String> getUniformNames() {
        return this.uniformLocations.keySet();
    }

    /**
     * Set the mapping between an input and a location
     */
    public void addInputLocation(String name, int location) {
        this.inputLocations.put(name, location);
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     *            the name to set
     */
    private final void setName(String name) {
        this.name = name;
    }

    /**
     * @return the programId
     */
    public int getProgramId() throws GLException {
        if (this.programId < 0) {
            int newProgramId = glCreateProgram();
            if (newProgramId <= 0) {
                throw new GLException("Unable to create GL program "
                        + this.getName() + " using vertex shader "
                        + this.vertexShaderId + " and fragment shader "
                        + this.fragmentShaderId);
            }

            // if the vertex and fragment shaders setup successfully,
            // attach them to the shader program, link the shader program
            // into the GL context, and validate
            glAttachShader(newProgramId, this.vertexShaderId);
            glAttachShader(newProgramId, this.fragmentShaderId);
            if (this.geometryShaderId >= 0) {
                glAttachShader(newProgramId, this.geometryShaderId);
            }
            // glAttachShader(newProgramId, fragLineShader);
            for (String varName : this.inputLocations.keySet()) {
                GL20.glBindAttribLocation(newProgramId,
                        this.inputLocations.get(varName), varName);
            }

            glLinkProgram(newProgramId);
            if (glGetProgrami(newProgramId, GL_LINK_STATUS) == GL_FALSE) {
                throw new GLException("GL program link error: "
                        + GLTools.getProgramLogInfo(newProgramId));
            }
            glValidateProgram(newProgramId);
            if (glGetProgrami(newProgramId, GL_VALIDATE_STATUS) == GL_FALSE) {
                throw new GLException("GL program validation error: "
                        + GLTools.getProgramLogInfo(newProgramId));
            }

            this.programId = newProgramId;
        }
        return this.programId;
    }

    /**
     * add a variable into registered uniform variables. Set it to -1 It's
     * location will be automatically defined at first use
     * 
     * @param uniformName
     */
    public void addUniform(final String uniformName) {
        this.uniformLocations.put(uniformName, -1);
    }

    /**
     * set uniform value (float)
     */
    public void setUniform1f(final String uniformName, float value)
            throws GLException {
        int uniformLocation = this.getUniformLocation(uniformName);
        this.setUniform1f(uniformLocation, value);
    }

    /**
     * @param value
     * @param uniformLocation
     */
    private void setUniform1f(int uniformLocation, float value) {
        GL20.glUniform1f(uniformLocation, value);
    }

    /**
     * set uniform value (int)
     */
    public void setUniform1i(final String uniformName, int value)
            throws GLException {
        int uniformLocation = this.getUniformLocation(uniformName);
        if (uniformLocation < 0) {
            logger.error("uniform variable '" + uniformName
                    + "' has invalid location " + uniformLocation
                    + " in program '" + this.getName() + "'");
            Thread.dumpStack();
        } else {
            this.setUniform1i(uniformLocation, value);
        }
    }

    /**
     * @param uniformLocation
     * @param value
     */
    private void setUniform1i(int uniformLocation, int value) {
        GL20.glUniform1i(uniformLocation, value);
    }

    /**
     * set uniform value (int)
     */
    public void setUniform2f(final String uniformName, float x, float y)
            throws GLException {
        int uniformLocation = this.getUniformLocation(uniformName);
        this.setUniform2f(uniformLocation, x, y);
    }

    public void setUniform2f(int uniformLocation, float x, float y) {
        GL20.glUniform2f(uniformLocation, x, y);

    }

    /**
     * get or create a variable location in this program. If the uniform name is
     * not registered an exception is thrown
     * 
     * @param uniformName
     * @return
     */
    public int getUniformLocation(String uniformName) throws GLException {
        Integer uniformLocation = this.uniformLocations.get(uniformName);
        if (uniformLocation == null) {
            throw new GLException("No registered uniform variable named '"
                    + uniformName + "' in program " + this.getName());
        }
        if (uniformLocation < 0) {
            uniformLocation = GL20.glGetUniformLocation(this.getProgramId(),
                    uniformName);
            this.uniformLocations.put(uniformName, uniformLocation);
        }
        return uniformLocation;
    }

    /**
     * Set vertex shader
     * 
     * @param vertShader
     *            shader id generated by glGenShader
     */
    public void setVertexShader(int vertShaderId) throws GLException {
        this.vertexShaderId = vertShaderId;
    }

    /**
     * Set fragment shader
     * 
     * @param fragShader
     *            shader id generated by glGenShader
     */
    public void setFragmentShader(int fragShaderId) throws GLException {
        this.fragmentShaderId = fragShaderId;
    }

    /**
     * Set geometry shader
     * 
     * @param geomShader
     *            shader id generated by glGenShader
     */
    public void setGeometryShader(int geomShaderId) throws GLException {
        this.geometryShaderId = geomShaderId;
    }

    /**
     * Try to create a compiled vertex shader
     * 
     * @param filename
     *            shader file
     * @return the shader id
     * @throws GLException
     *             on shader creation error
     */
    public static final int createVertexShader(String filename)
            throws GLException {
        return GLTools.createShader(filename, GL20.GL_VERTEX_SHADER);
    }

    /**
     * Try to create a compiled geometry shader
     * 
     * @param filename
     *            shader file
     * @return the shader id
     * @throws GLException
     *             on shader creation error
     */
    public static final int createGeometryShader(String filename)
            throws GLException {
        return GLTools.createShader(filename, GL32.GL_GEOMETRY_SHADER);
    }

    /**
     * Try to create a compiled fragment shader
     * 
     * @param filename
     *            shader file
     * @return the shader id
     * @throws GLException
     *             on shader creation error
     */
    public static final int createFragmentShader(String filename)
            throws GLException {
        return GLTools.createShader(filename, GL20.GL_FRAGMENT_SHADER);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GLProgram [programId=" + this.programId + ", name=" + this.name
                + "]";
    }

}
