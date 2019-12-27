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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import fr.ign.cogit.geoxygene.appli.gl.program.GLProgramBuilder;
import fr.ign.cogit.geoxygene.appli.gl.setters.GLProgramSetterFactory;
import fr.ign.cogit.geoxygene.appli.gl.setters.GLProgramUniformSetter;
import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodDescriptor;
import fr.ign.cogit.geoxygene.appli.render.stats.RenderingStatistics;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLTools;

/**
 * @author JeT This class manages a GL3/4 context with gl-programs and
 *         gl-uniform management
 * @author JeT, Bertrand Duménieu
 */
public class GLContext {

    private static final Logger logger = Logger.getLogger(GLContext.class.getName());

    private final Map<GLProgram, GLProgramUniformSetter> setters = new HashMap<GLProgram, GLProgramUniformSetter>();
    private final Map<String, GLProgram> programs = new HashMap<String, GLProgram>();
    private final Map<String, Object> shareduniforms = new HashMap<String, Object>();
    private GLProgram currentProgram = null; 
    
    public void addProgram(GLProgram program) {
        if (this.programs.containsKey(program.getName())) {
            logger.warn("There is already a registered GLProgram with the name " + program.getName() + ". The previous program will be erased.");
            this.programs.get(program.getName()).dispose();
        }
        this.programs.put(program.getName(), program);
    }

    private void addSharedUniform(String name, Object value) {
        if (this.programs.containsKey(name)) {
            logger.warn("There is already a registered Uniform with the name " + name + ". The previous uniform will be erased.");
        }
        this.shareduniforms.put(name, value);
    }

    public Object getSharedUniform(String uniform) {
        if (this.shareduniforms.containsKey(uniform)) {
            return this.shareduniforms.get(uniform);
        }
        return null;
    }
    

    /**
     * @return the currentProgram
     */
    public GLProgram getCurrentProgram() {
        return this.currentProgram;
    }
//
//    /**
//     * set the given program as the current program in use (glUse) If the
//     * requested program is already the current one: do nothing
//     * 
//     * @param programName
//     *            program name
//     * @return false on error
//     * @throws GLException
//     */
//    public GLProgram setCurrentProgram(String programName) throws GLException {
//        if (programName == null) {
//            this.currentProgram = null;
//            return this.currentProgram;
//        }
//        if (this.currentProgram != null && programName.equalsIgnoreCase(this.currentProgram.getName())) {
//            return this.currentProgram;
//        }
//        GLProgram program;
//        synchronized (this.programs) {
//            program = this.programs.get(programName);
//            if (program == null) {
//                logger.error("Cannot create program named " + programName + ": no program found with this name");
//                Thread.dumpStack();
//                for (Map.Entry<String, GLProgram> entry : this.programs.entrySet()) {
//                    logger.debug("\t" + entry.getKey() + " => " + entry.getValue());
//                }
//
//                if (!GL20.glIsProgram(program.getProgramId())) {
//                    logger.warn("Invalid creation of program named " + programName + ". Id = " + program.getProgramId());
//                    return null;
//                }
//                logger.info("GL program creation " + programName + " complete with Id " + program.getProgramId());
//            }
//            return this.setCurrentProgram(program);
//        }
//    }

    /**
     * set the given program as the current program in use (glUse) If the
     * requested program is already the current one: do nothing
     * 
     * @param program
     *            program instance
     * @return false on error
     */
    public GLProgram setCurrentProgram(GLProgram program) throws GLException {
        if (program == null) {
            GL20.glUseProgram(0);
            RenderingStatistics.switchProgram();
            this.currentProgram = null;
            return null;
        }
        // check if the current program is already the requested one
        if (program == this.getCurrentProgram()) {
            return program;
        }
        if (GL20.glIsProgram(program.getProgramId()) == false) {
            logger.error("Program Id " + program.getProgramId() + " (" + program.getName() + ") is not a valid GL program");
            Thread.dumpStack();
            GL20.glUseProgram(0);
            this.currentProgram = null;
            return null;
        }
        GL20.glUseProgram(program.getProgramId());
        if (!GLTools.glCheckError("Use program '" + program.getName() + "' id = " + program.getProgramId())) {
            GL20.glUseProgram(0);
            this.currentProgram = null;
            return null;
        }
        RenderingStatistics.switchProgram();
        this.currentProgram = program;
        // check correctness
        if (GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM) != this.getCurrentProgram().getProgramId() || program.getProgramId() != this.getCurrentProgram().getProgramId()) {
            logger.info("Set program id to " + program.getProgramId());
            logger.info("Current program id to " + this.getCurrentProgram().getProgramId());
            logger.info("GL Current program id " + GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM));
            throw new GLException("Unable to set program id " + program.getProgramId() + " as current");
        }
        return program;
    }

    public void disposeContext() throws GLException {
        synchronized (this.programs) {
            for (GLProgram program : this.programs.values()) {
                if (program != null) {
                    program.dispose();
                }
            }
            this.programs.clear();
        }

    }

    public GLProgram getProgram(String programName) {
        return this.programs.get(programName);
    }

    public boolean containsProgram(String progname) {
        return this.programs.containsKey(progname);
    }

    public Object getUniform(String uniform) {
        return this.shareduniforms.get(uniform);
    }

    public void setSharedUniform(String name, Object value) {
        if (!this.shareduniforms.containsKey(name)) {
            this.addSharedUniform(name, value);
        } else {
            this.shareduniforms.put(name, value);
        }
    }

    public GLProgramUniformSetter getSetter(GLProgram program) {
        return this.setters.get(program);
    }

    public GLProgramUniformSetter setSetter(GLProgram program, GLProgramUniformSetter setter) {
        return this.setters.put(program, setter);
    }

    public GLProgram createProgram(String progname, RenderingMethodDescriptor method) throws Exception {
        GLProgram program = new GLProgramBuilder().build(progname, method);
        if (progname == null) {
            throw new Exception("Failed to build the program " + progname + " associated with the rendering method " + method.getName());
        }
        // Create the Specialized Setter if there is one
        GLProgramUniformSetter setter = GLProgramSetterFactory.getSetter(method);
        if (setter != null) {
            this.setters.put(program, setter);
        }
        this.addProgram(program);
        return program;
    }

    
    /** Holder */
    private static class GLContextHolder
    {               
            private final static GLContext instance = new GLContext();
    }

    /** Point d'accès pour l'instance unique du singleton */
    public static GLContext getActiveGlContext()
    {
            return GLContextHolder.instance;
    }



}
