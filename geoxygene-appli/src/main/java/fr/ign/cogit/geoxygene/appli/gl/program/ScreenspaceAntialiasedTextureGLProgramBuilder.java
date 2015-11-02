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

import java.io.IOException;
import java.net.URISyntaxException;

import org.lwjgl.opengl.GL20;

import fr.ign.cogit.geoxygene.appli.gl.Shader;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;

public class ScreenspaceAntialiasedTextureGLProgramBuilder extends AbstractDelegateBuilder {

    @Override
    public void preBuildActions(GLProgram program) {
        try {
            
            
            Shader vmainshader = new Shader("screenspace.vert", GL20.GL_VERTEX_SHADER, Shader.DEFAULT_SHADERS_LOCATION_DIR.toURI().resolve("./screenspace.vert.glsl").toURL());

            Shader fmainshader = new Shader("antialiased.frag", GL20.GL_FRAGMENT_SHADER, Shader.DEFAULT_SHADERS_LOCATION_DIR.toURI().resolve("./antialiased.frag.glsl").toURL());
            Shader fsubshader = new Shader("filteridentity.frag", GL20.GL_FRAGMENT_SHADER, Shader.DEFAULT_SHADERS_LOCATION_DIR.toURI().resolve("./filterIdentity.frag.glsl").toURL());
            
            program.addShader(vmainshader);
            program.addShader(fmainshader);
            program.addShader(fsubshader);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void postBuildActions(GLProgram program) {
        // TODO Auto-generated method stub

    }

}
