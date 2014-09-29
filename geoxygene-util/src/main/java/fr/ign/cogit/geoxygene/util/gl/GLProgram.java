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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
    private final Map<String, Integer> vertexShaderIds = new HashMap<String, Integer>();
    private final Map<String, Integer> fragmentShaderIds = new HashMap<String, Integer>();
    private final Map<String, Integer> geometryShaderIds = new HashMap<String, Integer>();
    private final Map<String, String> vertexShaderFilenames = new HashMap<String, String>();
    private final Map<String, String> fragmentShaderFilenames = new HashMap<String, String>();
    private final Map<String, String> geometryShaderFilenames = new HashMap<String, String>();
    // list of uniform with errors already logged (display only once an error
    // message)
    private final Set<String> uniformErrorLogged = new HashSet<String>();

    private boolean displayWarnings = false;

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
    public synchronized int getProgramId() throws GLException {
        if (this.programId < 0) {
            int newProgramId = glCreateProgram();
            if (newProgramId <= 0) {
                throw new GLException("Unable to create GL program "
                        + this.getName() + " using vertex shader "
                        + this.vertexShaderIds.size() + " and fragment shader "
                        + this.fragmentShaderIds.size());
            }

            // if the vertex and fragment shaders setup successfully,
            // attach them to the shader program, link the shader program
            // into the GL context, and validate
            for (Entry<String, Integer> entry : this.vertexShaderIds.entrySet()) {
                String content = entry.getKey();
                int shaderId = GLProgram.createVertexShader(content,
                        this.vertexShaderFilenames.get(content));
                this.vertexShaderIds.put(content, shaderId);
                glAttachShader(newProgramId, shaderId);
            }

            for (Entry<String, Integer> entry : this.fragmentShaderIds
                    .entrySet()) {
                String content = entry.getKey();
                int shaderId = GLProgram.createFragmentShader(content,
                        this.fragmentShaderFilenames.get(content));
                this.fragmentShaderIds.put(content, shaderId);
                glAttachShader(newProgramId, shaderId);
            }

            for (Entry<String, Integer> entry : this.geometryShaderIds
                    .entrySet()) {
                String content = entry.getKey();
                int shaderId = GLProgram.createGeometryShader(content,
                        this.geometryShaderFilenames.get(content));
                this.geometryShaderIds.put(content, shaderId);
                glAttachShader(newProgramId, shaderId);
            }

            glLinkProgram(newProgramId);
            if (glGetProgrami(newProgramId, GL_LINK_STATUS) == GL_FALSE) {
                logger.error("Link error in program " + this.getName());
                for (Entry<String, Integer> entry : this.vertexShaderIds
                        .entrySet()) {
                    String content = entry.getKey();
                    int shaderId = entry.getValue();
                    logger.error("id = " + shaderId + " content = '" + content
                            + "'");
                }

                for (Entry<String, Integer> entry : this.fragmentShaderIds
                        .entrySet()) {
                    String content = entry.getKey();
                    int shaderId = entry.getValue();
                    logger.error("id = " + shaderId + " content = '" + content
                            + "'");
                }

                for (Entry<String, Integer> entry : this.geometryShaderIds
                        .entrySet()) {
                    String content = entry.getKey();
                    int shaderId = entry.getValue();
                    logger.error("id = " + shaderId + " content = '" + content
                            + "'");
                }

                throw new GLException("GL program '" + this.getName()
                        + "' link error: "
                        + GLTools.getProgramLogInfo(newProgramId));
            }
            glValidateProgram(newProgramId);
            if (glGetProgrami(newProgramId, GL_VALIDATE_STATUS) == GL_FALSE) {
                throw new GLException("GL program '" + this.getName()
                        + "' validation error: "
                        + GLTools.getProgramLogInfo(newProgramId));
            } else {
                logger.info("\tProgram '" + this.getName()
                        + "' created and validated. Vertex shader count="
                        + this.vertexShaderIds.size());
                // // detach all shaders
                //
                // for (int vertexShaderId : this.vertexShaderIds) {
                // GL20.glDetachShader(this.programId, vertexShaderId);
                // }
                // for (int fragmentShaderId : this.fragmentShaderIds) {
                // GL20.glDetachShader(this.programId, fragmentShaderId);
                // }
                // for (int geometryShaderId : this.geometryShaderIds) {
                // GL20.glDetachShader(this.programId, geometryShaderId);
                // }

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
            if (this.displayWarnings()
                    && !this.uniformErrorLogged.contains(uniformName)) {
                this.uniformErrorLogged.add(uniformName);
                logger.warn("uniform variable '" + uniformName
                        + "' has invalid location " + uniformLocation
                        + " in program '" + this.getName() + "'");
                logger.info("Registered uniforms:");
                for (Map.Entry<String, Integer> entry : this.uniformLocations
                        .entrySet()) {
                    logger.info("\t" + entry.getKey() + " : "
                            + entry.getValue());
                }
            }
            // Thread.dumpStack();
        } else {
            this.setUniform1i(uniformLocation, value);
        }
    }

    private boolean displayWarnings() {
        return this.displayWarnings;
    }

    /**
     * @param displayWarnings
     *            the displayWarnings to set
     */
    public void setDisplayWarnings(boolean displayWarnings) {
        this.displayWarnings = displayWarnings;
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
     * @param filename
     *            file containing the shader content. It can be null, it is used
     *            only to display the filename when an error occured
     */
    public void addVertexShader(String shaderSource, String filename) {
        this.vertexShaderIds.put(shaderSource, -1);
        this.vertexShaderFilenames.put(shaderSource, filename);
    }

    // public void addVertexShader(Collection<String> shaderSources)
    // throws GLException {
    // for (String source : shaderSources) {
    // this.addVertexShader(source);
    // }
    // }

    /**
     * Set fragment shader
     * 
     * @param fragShader
     *            shader id generated by glGenShader
     * @param filename
     *            file containing the shader content. It can be null, it is used
     *            only to display the filename when an error occured
     */
    public void addFragmentShader(String shaderSource, String filename) {
        this.fragmentShaderIds.put(shaderSource, -1);
        this.fragmentShaderFilenames.put(shaderSource, filename);
    }

    // public void addFragmentShader(Collection<String> shaderSources) {
    // for (String source : shaderSources) {
    // this.addFragmentShader(source);
    // }
    // }

    /**
     * Set geometry shader
     * 
     * @param geomShader
     *            shader id generated by glGenShader
     */
    /**
     * Set fragment shader
     * 
     * @param fragShader
     *            shader id generated by glGenShader
     * @param filename
     *            file containing the shader content. It can be null, it is used
     *            only to display the filename when an error occured
     * 
     */
    public void addGeometryShader(String shaderSource, String filename) {
        this.geometryShaderIds.put(shaderSource, -1);
        this.geometryShaderFilenames.put(shaderSource, filename);
    }

    // public void addGeometryShader(Collection<String> shaderSources) {
    // for (String source : shaderSources) {
    // this.addGeometryShader(source);
    // }
    // }

    /**
     * Try to create a compiled vertex shader
     * 
     * @param shaderContent
     *            shader content as string
     * @param filename
     *            file containing the shader content. It can be null, it is used
     *            only to display the filename when an error occured
     * @return the shader id
     * @throws GLException
     *             on shader creation error
     */
    public static final int createVertexShader(String shaderContent,
            String filename) throws GLException {
        return GLTools.createShader(GL20.GL_VERTEX_SHADER, shaderContent,
                filename);
    }

    /**
     * Try to create a compiled geometry shader
     * 
     * @param shaderContent
     *            shader content as a string
     * @param filename
     *            file containing the shader content. It can be null, it is used
     *            only to display the filename when an error occured
     * @return the shader id
     * @throws GLException
     *             on shader creation error
     */
    public static final int createGeometryShader(String shaderContent,
            String filename) throws GLException {
        return GLTools.createShader(GL32.GL_GEOMETRY_SHADER, shaderContent,
                filename);
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
    public static final int createFragmentShader(String shaderContent,
            String filename) throws GLException {
        return GLTools.createShader(GL20.GL_FRAGMENT_SHADER, shaderContent,
                filename);
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

    /**
     * Mark all shaders and program for deletion
     */
    public synchronized void dispose() {
        if (this.programId != -1) {
            GLTools.glCheckError("before program " + this.getName()
                    + " shader detach");
            for (int vertexShaderId : this.vertexShaderIds.values()) {
                GL20.glDetachShader(this.programId, vertexShaderId);
            }
            for (int fragmentShaderId : this.fragmentShaderIds.values()) {
                GL20.glDetachShader(this.programId, fragmentShaderId);
            }
            for (int geometryShaderId : this.geometryShaderIds.values()) {
                GL20.glDetachShader(this.programId, geometryShaderId);
            }
            GLTools.glCheckError("before program " + this.getName()
                    + " deletion");
            GL20.glDeleteProgram(this.programId);
            GLTools.glCheckError("after program " + this.getName()
                    + " deletion");
            this.programId = -1;
            this.uniformLocations.clear();
        }
    }
}
