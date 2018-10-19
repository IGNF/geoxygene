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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

/**
 * @author JeT A GLProgram is equivalent to a gl-program with gl Uniform data
 *         management
 */
public class GLProgram {

    private static final Logger logger = Logger.getLogger(GLProgram.class.getName());

    private int programId = -1;
    private String name = "unnamed";
    

    //the list of shaders used by this program. 
    private final List<GLShader> shaders = new ArrayList<GLShader>(0);
    
    //The map of all the uniforms contained in the shaders used by this GLProgram.
    private final Map<String, GLUniform> uniforms = new HashMap<String, GLUniform>(0);
    //The locations of each uniform used if this program.
    private final Map<String, Integer> uniforms_location = new HashMap<String, Integer>(0);
    
    private boolean displayWarnings = false;

    /**
     * @param name
     */
    public GLProgram(String name) {
        super();
        this.setName(name);
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


    
    public synchronized int getProgramId() throws GLException {
        if (this.programId < 0) {
            int newProgramId = glCreateProgram();
            if (newProgramId <= 0) {
                throw new GLException("Unable to create GL program " + this.getName() + " using "+this.shaders.size()+" shaders");
            }
            // Set up the shaders used by the program.
            // If the vertex and fragment shaders setup successfully,
            // attach them to the shader program, link the shader program
            // into the GL context, and validate
            for(GLShader shader : this.shaders){
                int shaderId = shader.getId();
                glAttachShader(newProgramId, shaderId);
            }
            glLinkProgram(newProgramId);
            if (glGetProgrami(newProgramId, GL_LINK_STATUS) == GL_FALSE) {
                System.out.println("GL20.GL_VERTEX_SHADER =" +GL20.GL_VERTEX_SHADER);
                System.out.println("GL20.GL_FRAGMENT_SHADER =" +GL20.GL_FRAGMENT_SHADER);
                logger.error("Link error in program " + this.getName());
                for(GLShader shader : this.shaders){
                    logger.error("- shader "+shader.getName()+" : id = " + shader.getId() + " content = '" + shader.getSource() + "'");
                }
                throw new GLException("GL program '" + this.getName() + "' link error: " + GLTools.getProgramLogInfo(newProgramId));
            }
            glValidateProgram(newProgramId);
            if (glGetProgrami(newProgramId, GL_VALIDATE_STATUS) == GL_FALSE) {
                throw new GLException("GL program '" + this.getName() + "' validation error: " + GLTools.getProgramLogInfo(newProgramId));
            } else {
                logger.info("\tProgram '" + this.getName() + "' created and validated. Shader count=" + this.shaders.size());
            }
            this.programId = newProgramId;
        }
        return this.programId;
    }
    
    

    /**
     * set uniform value (float)
     */
    private void setUniform1f(final String uniformName, float value) throws GLException {
        int uniformLocation = this.getUniformLocation(uniformName);
        this.setUniform1f(uniformLocation, value);
    }

    /**
     * set uniform value (vec4 / color)
     */
    private void setUniform4f(final String uniformName, float... values) throws GLException {
        int uniformLocation = this.getUniformLocation(uniformName);
        this.setUniform4f(uniformLocation, values);
    }

    /**
     * @param value
     * @param uniformLocation
     */
    private void setUniform1f(int uniformLocation, float value) {
        GL20.glUniform1f(uniformLocation, value);
    }

    /**
     * @param value
     * @param uniformLocation
     */
    private void setUniform4f(int uniformLocation, float... values) {
        GL20.glUniform4f(uniformLocation, values[0], values[1], values[2], values[3]);
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
        GLTools.glCheckError("GLERROR : before  GL20.glUniform1i");
        GL20.glUniform1i(uniformLocation, value);
        GLTools.glCheckError("GLERROR : after GL20.glUniform1i");

    }

    /**
     * set uniform value (int)
     */
    public void setUniform1i(final String uniformName, int value)
            throws GLException {
        int uniformLocation = this.getUniformLocation(uniformName);
        if (uniformLocation < 0) {
            GLProgram.logger.debug("The uniform "+uniformName+" was not bound in the GLProgram "+this.name+". Maybe this uniform is set but not used by any shader?");
        } else {
            this.setUniform1i(uniformLocation, value);
        }
    }
    
    /**
     * set uniform value (int)
     */
    public void setUniform2f(final String uniformName, float x, float y) throws GLException {
        int uniformLocation = this.getUniformLocation(uniformName);
        this.setUniform2f(uniformLocation, x, y);
    }

    public void setUniform2f(int uniformLocation, float x, float y) {
        GL20.glUniform2f(uniformLocation, x, y);

    }

    public void setUniform(String name, Object value) throws GLException {
        logger.debug("Setting " + name + " to " + value);
        if(this.uniforms.get(name) == null){
            logger.error(" Unknown uniform named "+name+ "(GLProgram "+this.name+")");
            return;
        }
        switch (this.uniforms.get(name).getGlType()) {
        case GL11.GL_FLOAT:
            //Value must be a Number
            Number nval = (Number) value;
            this.setUniform1f(name, nval.floatValue());
            break;
        case GL11.GL_INT:
            nval = (Number) value;
            this.setUniform1i(name, nval.intValue());
            break;
        case GL11.GL_UNSIGNED_INT:
            nval = (Number) value;
            this.setUniform1i(name, nval.intValue());
            break;
        case GL20.GL_FLOAT_VEC2:
            Number[] ftab = (Number[]) value;
            this.setUniform2f(name, ftab[0].floatValue(), ftab[1].floatValue());
            break;
        case GL20.GL_FLOAT_VEC4:
            ftab = (Number[]) value;
            this.setUniform4f(name, ftab[0].floatValue(), ftab[1].floatValue(), ftab[2].floatValue(), ftab[3].floatValue());
            break;
        case GL20.GL_SAMPLER_2D:
            nval = (Number) value;
            //The value must be the Texture slot number.
            this.setUniform1i(name, nval.intValue());
            break;
        default:
            logger.error(this.uniforms.get(name).getGlType() + " uniform type is not yet implemented!");
            break;
        }
    }

    /**
     * get or create a variable location in this program. If the uniform name is
     * not registered an exception is thrown
     * 
     * @param uniformName
     * @return
     */
    public int getUniformLocation(String uniformName) throws GLException {
        Integer uniformLocation = this.uniforms_location.get(uniformName);
        if (uniformLocation ==null) {
            throw new GLException("No registered uniform variable named '" + uniformName + "' in program " + this.getName());
        }
        if (uniformLocation < 0) {
            GLTools.glCheckError("GLERROR : before  GL20.glGetUniformLocation");
            uniformLocation = GL20.glGetUniformLocation(this.getProgramId(), uniformName);
            GLTools.glCheckError("GLERROR : after  GL20.glGetUniformLocation");
            this.uniforms_location.put(uniformName, uniformLocation);
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
//    public void addVertexShader(String shaderSource, String filename) {
//        this.vertexShaderIds.put(shaderSource, -1);
//
//        this.vertexShaderFilenames.put(shaderSource, filename);
//    }

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
//    public void addFragmentShader(String shaderSource, String filename) {
//        this.fragmentShaderIds.put(shaderSource, -1);
//        this.fragmentShaderFilenames.put(shaderSource, filename);
//    }

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
//    public void addGeometryShader(String shaderSource, String filename) {
//        this.geometryShaderIds.put(shaderSource, -1);
//        this.geometryShaderFilenames.put(shaderSource, filename);
//    }

    // public void addGeometryShader(Collection<String> shaderSources) {
    // for (String source : shaderSources) {
    // this.addGeometryShader(source);
    // }
    // }

    public void addShader(GLShader shader) {
        this.shaders.add(shader);
        //Update the uniforms
        for(int i = 0; i < shader.getUniformsCount(); i++ ){
            String uname = shader.getUniformName(i);
            if(this.uniforms.get(uname) == null){
                int utype = shader.getUniformType(i);
                this.uniforms.put(uname, new GLUniform(uname, utype,false,0));
                this.uniforms_location.put(uname, -1);
            }
        }
    }
    
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
    public static final int createVertexShader(String shaderContent, String filename) throws GLException {
        return GLTools.createShader(GL20.GL_VERTEX_SHADER, shaderContent, filename);
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
    public static final int createGeometryShader(String shaderContent, String filename) throws GLException {
        return GLTools.createShader(GL32.GL_GEOMETRY_SHADER, shaderContent, filename);
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
    public static final int createFragmentShader(String shaderContent, String filename) throws GLException {
        return GLTools.createShader(GL20.GL_FRAGMENT_SHADER, shaderContent, filename);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GLProgram [programId=" + this.programId + ", name=" + this.name + "]";
    }

    /**
     * Mark all shaders and program for deletion
     */
    public synchronized void dispose() {
        if (this.programId != -1) {
            GLTools.glCheckError("before program " + this.getName() + " shader detach");
            for (GLShader shader : this.shaders) {
                GL20.glDetachShader(this.programId, shader.getId());
            }
            GLTools.glCheckError("before program " + this.getName() + " deletion");
            GL20.glDeleteProgram(this.programId);
            GLTools.glCheckError("after program " + this.getName() + " deletion");
            this.programId = -1;
            this.shaders.clear();
            this.uniforms.clear();
            this.uniforms_location.clear();
        }
    }
    

    public  Collection<GLUniform> getUniforms() {
        return this.uniforms.values();
    }

}
