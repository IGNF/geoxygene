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

package fr.ign.cogit.geoxygene.appli.gl.program;

import org.lwjgl.opengl.GL20;

import fr.ign.cogit.geoxygene.appli.gl.Shader;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;

public class WorldspaceColorGLProgramBuilder extends AbstractDelegateBuilder {

    @Override
    public void preBuildActions(GLProgram colorProgram) {

        try {
            
            Shader vmainshader = new Shader("world2screenspace.vert", GL20.GL_VERTEX_SHADER, Shader.DEFAULT_SHADERS_LOCATION_DIR.toURI().resolve("./world2screenspace.vert.glsl").toURL());
            Shader vsubshader = new Shader("identity.vert", GL20.GL_VERTEX_SHADER, Shader.DEFAULT_SHADERS_LOCATION_DIR.toURI().resolve("./identity.vert.glsl").toURL());

            Shader fmainshader = new Shader("polygon.color.frag", GL20.GL_FRAGMENT_SHADER, Shader.DEFAULT_SHADERS_LOCATION_DIR.toURI().resolve("./polygon.color.frag.glsl").toURL());
            
            colorProgram.addShader(vmainshader);
            colorProgram.addShader(vsubshader);
            colorProgram.addShader(fmainshader);
            
//            ShaderDescriptor vertexshader = (ShaderDescriptor) ResourcesManager.Root().getSubManager(GeoxygeneConstants.GEOX_Const_ShadersManagerName).getResourceByName("world2screenspace.vert");
//            ShaderDescriptor vertexshaderidentity = (ShaderDescriptor) ResourcesManager.Root().getSubManager(GeoxygeneConstants.GEOX_Const_ShadersManagerName).getResourceByName("identity.vert");
//            colorProgram.addVertexShader(GLTools.readFileAsString(vertexshaderidentity.getShaderFilePath()), vertexshaderidentity.getName());
//
//            ShaderDescriptor polycolor = (ShaderDescriptor)  ResourcesManager.Root().getSubManager(GeoxygeneConstants.GEOX_Const_ShadersManagerName).getResourceByName("polygon.color.frag");
//            colorProgram.addVertexShader(GLTools.readFileAsString(vertexshader.getShaderFilePath()), vertexshader.getName());
//            colorProgram.addFragmentShader(GLTools.readFileAsString(polycolor.getShaderFilePath()), polycolor.getName());
//            colorProgram.addInputLocation(GLSimpleVertex.VertexUVVarName, GLSimpleVertex.vertexUVLocation);
//            colorProgram.addInputLocation(GLSimpleVertex.VertexPositionVarName, GLSimpleVertex.vertexPostionLocation);
//            colorProgram.addInputLocation(GLSimpleVertex.VertexColorVarName, GLSimpleVertex.vertexColorLocation);
//            colorProgram.addUniform(GeoxygeneConstants.GL_VarName_M00ModelToViewMatrix, GLUniformType.GL_FLOAT);
//            colorProgram.addUniform(GeoxygeneConstants.GL_VarName_M02ModelToViewMatrix, GLUniformType.GL_FLOAT);
//            colorProgram.addUniform(GeoxygeneConstants.GL_VarName_M11ModelToViewMatrix, GLUniformType.GL_FLOAT);
//            colorProgram.addUniform(GeoxygeneConstants.GL_VarName_M12ModelToViewMatrix, GLUniformType.GL_FLOAT);
//            colorProgram.addUniform(GeoxygeneConstants.GL_VarName_ScreenWidth, GLUniformType.GL_FLOAT);
//            colorProgram.addUniform(GeoxygeneConstants.GL_VarName_ScreenHeight, GLUniformType.GL_FLOAT);
//            colorProgram.addUniform(GeoxygeneConstants.GL_VarName_FboWidth, GLUniformType.GL_FLOAT);
//            colorProgram.addUniform(GeoxygeneConstants.GL_VarName_FboHeight, GLUniformType.GL_FLOAT);
//            colorProgram.addUniform(GeoxygeneConstants.GL_Const_GlobalOpacityVarName, GLUniformType.GL_FLOAT);
//            colorProgram.addUniform(GeoxygeneConstants.GL_Const_ObjectOpacityVarName, GLUniformType.GL_FLOAT);
//            colorProgram.addUniform(GeoxygeneConstants.GL_Const_ColorTexture1Name, GLUniformType.GL_SAMPLER2D);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

    @Override
    public void postBuildActions(GLProgram program) {
        // TODO Auto-generated method stub

    }

}
