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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * @author JeT This class manages a GL3/4 context with gl-programs and
 *         gl-uniform management
 */
public class GLContext {

    private static final Logger logger = Logger.getLogger(GLContext.class
            .getName()); // logger

    private final Map<String, GLProgramAccessor> programAccessors = new HashMap<String, GLProgramAccessor>();
    private final Map<String, GLProgram> programs = new HashMap<String, GLProgram>();
    private GLProgram currentProgram = null;

    /**
     * 
     */
    public GLContext() {
        // TODO Auto-generated constructor stub
    }

    public void addProgram(String programName, GLProgramAccessor programAccessor) {
        synchronized (this.programs) {
            this.programs.put(programName, null);
            this.programAccessors.put(programName, programAccessor);
            logger.debug("Add program accessor to '" + programName + "'");
        }
    }

    /**
     * @return the currentProgram
     */
    public GLProgram getCurrentProgram() {
        return this.currentProgram;
    }

    /**
     * set the given program as the current program in use (glUse) If the
     * requested program is already the current one: do nothing
     * 
     * @param programName
     *            program name
     * @return false on error
     * @throws GLException
     */
    public GLProgram setCurrentProgram(String programName) throws GLException {
        if (programName == null) {
            this.currentProgram = null;
            return this.currentProgram;
        }
        GLProgram program;
        synchronized (this.programs) {
            program = this.programs.get(programName);
            if (program == null) {
                GLProgramAccessor glProgramAccessor = this.programAccessors
                        .get(programName);
                if (glProgramAccessor == null) {
                    logger.warn("Cannot create program named " + programName
                            + ": no accessor is associated with this name");
                    Thread.dumpStack();
                    for (Map.Entry<String, GLProgramAccessor> entry : this.programAccessors
                            .entrySet()) {
                        logger.debug("\t" + entry.getKey() + " => "
                                + entry.getValue());
                    }
                    return null;
                }
                program = glProgramAccessor.getGLProgram();
                if (program == null) {
                    logger.warn("Cannot create program named " + programName
                            + ": accessor returned a null program");
                    return null;
                }
                if (!GL20.glIsProgram(program.getProgramId())) {
                    logger.warn("Invalid creation of program named "
                            + programName + ". Id = " + program.getProgramId());
                    return null;
                }
                logger.info("GL program creation " + programName
                        + " complete with Id " + program.getProgramId());
                this.programs.put(programName, program);
            }
        }
        return this.setCurrentProgram(program);
    }

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
            logger.error("Program Id " + program.getProgramId() + " ("
                    + program.getName() + ") is not a valid GL program");
            Thread.dumpStack();
            GL20.glUseProgram(0);
            this.currentProgram = null;
            return null;
        }
        GL20.glUseProgram(program.getProgramId());
        if (!GLTools.glCheckError("Use program '" + program.getName()
                + "' id = " + program.getProgramId())) {
            GL20.glUseProgram(0);
            this.currentProgram = null;
            return null;
        }
        RenderingStatistics.switchProgram();
        this.currentProgram = program;
        // check correctness
        if (GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM) != this
                .getCurrentProgram().getProgramId()
                || program.getProgramId() != this.getCurrentProgram()
                        .getProgramId()) {
            logger.info("Set program id to " + program.getProgramId());
            logger.info("Current program id to "
                    + this.getCurrentProgram().getProgramId());
            logger.info("GL Current program id "
                    + GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM));
            throw new GLException("Unable to set program id "
                    + program.getProgramId() + " as current");
        }
        return program;
    }

    public void initializeContext() throws GLException {
        // this.setCurrentProgram((GLProgram) null);

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

    /**
     * This getter retrieves a program name accessor or null if it doesn't exist
     * 
     * @param programName
     * @return
     */
    public GLProgramAccessor getProgramAccessor(String programName) {
        return this.programAccessors.get(programName);
    }

    // public void checkContext() {
    // logger.debug("Check GLContext " + this.hashCode() + " containing "
    // + this.programs.size() + " programs");
    // try {
    // for (GLProgram program : this.programs) {
    // logger.debug("\tprogram name : " + program.getName());
    // if (GL20.glIsProgram(program.getProgramId()) == false) {
    // logger.error("Program Id " + program.getProgramId() + "("
    // + program.getName() + ") is not a valid GL program");
    // }
    // for (String uniformName : program.getUniformNames()) {
    // logger.debug("\tuniform : " + uniformName + " location = "
    // + program.getUniformLocation(uniformName));
    // }
    // }
    // } catch (GLException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }

}
