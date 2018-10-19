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

package fr.ign.cogit.geoxygene.appli.gl.setters;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import fr.ign.cogit.geoxygene.appli.render.methods.NamedRenderingParametersMap;
import fr.ign.cogit.geoxygene.appli.render.texture.TextureManager;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLTexture;
import fr.ign.cogit.geoxygene.util.gl.GLTools;
import fr.ign.cogit.geoxygene.util.gl.GLUniform;

public class GLProgramUniformSetter {
    Logger logger = Logger.getLogger(GLProgramUniformSetter.class);

    // Ordered user-defined program setters
    private List<UserDefinedGLProgramSetter> userSetters;

    public GLProgramUniformSetter(final List<UserDefinedGLProgramSetter> lUserSetters) {
        this.userSetters = new LinkedList<UserDefinedGLProgramSetter>(lUserSetters);
    }

    
    public boolean set(NamedRenderingParametersMap cParams, GLProgram program) throws GLException {
        logger.debug("Apply preSet actions");
        boolean succ = true;
        if (!this.userSetters.isEmpty()) {
            succ &= this.preSetActions(cParams,program);
        }
        if (!succ)
            return false;
        int texcount = 0;
        Collection<GLUniform> uniforms = program.getUniforms();
        for (GLUniform uniform : uniforms) {
            Object uniform_value = cParams.containsParameterWithName(uniform.getName())? cParams.getByName(uniform.getName()) : cParams.getByUniformRef(uniform.getName()) ;
            //If the uniform is a texture, we have to retrieve it from the TextureManager
            if(uniform.getGlType() == GL20.GL_SAMPLER_2D || uniform.getGlType() == GL20.GL_SAMPLER_1D){
                if(uniform_value  != null && uniform_value instanceof URI){
                    GLTexture btex = TextureManager.retrieveTexture((URI)uniform_value);
                    if(btex == null){
                        logger.debug("Setting the texture uniform "+uniform.getName()+" failed because no texture was found: maybe the texture is not yet ready?");
                    }else{
                        btex.setTextureSlot(uniform.getName(), GL13.GL_TEXTURE0 + texcount);
                        if (!btex.initializeRendering(program.getProgramId())) {
                            logger.error("An error occured while initilializing the texture " + uniform);
                            GL11.glDeleteTextures(GL13.GL_TEXTURE0 + texcount);
                            succ = false;
                        }
                        program.setUniform(uniform.getName(), texcount);
                        texcount++;
                    }

                }else{
                    logger.error("Setting the texture uniform "+uniform.getName()+" failed : there is no parameter with such name or its value is null.");
                }
            }else{
                if(uniform_value != null){
                    program.setUniform(uniform.getName(), uniform_value);
                    GLTools.glCheckError("When setting uniform "+uniform.getName()+" : "+uniform_value );

                }else{
                    logger.debug("Uniform "+uniform.getName()+" has no value set ");
                }
            }
        }
        if (!this.userSetters.isEmpty()) {
            succ &= this.postSetActions(cParams,program);
        }
        return succ;
    }

    private boolean preSetActions(NamedRenderingParametersMap cParams, GLProgram program) {
        boolean succ = true;
        for (UserDefinedGLProgramSetter usetter : userSetters) {
            succ &= usetter.preSetActions(cParams, program);
        }
        return succ;
    }

    private boolean postSetActions(NamedRenderingParametersMap cParams, GLProgram program) {
        boolean succ = true;
        for (UserDefinedGLProgramSetter usetter : userSetters) {
            succ &= usetter.postSetActions(cParams, program);
        }
        return succ;
    }



}
