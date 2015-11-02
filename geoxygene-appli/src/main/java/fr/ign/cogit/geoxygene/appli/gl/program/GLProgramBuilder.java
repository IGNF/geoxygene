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
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.GeoxygeneConstants;
import fr.ign.cogit.geoxygene.appli.gl.ResourcesManager;
import fr.ign.cogit.geoxygene.appli.gl.Shader;
import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodDescriptor;
import fr.ign.cogit.geoxygene.appli.render.methods.ShaderRef;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;

/**
 * Builder class for GLProgram used to render geographical data with styling in
 * Geoxygene when OPENGL is enabled. TODO : maybe this builder should act as a
 * delegator so that new GLProgram can be easily added to Geoxygene?
 * 
 * @author Bertrand Duménieu
 *
 */
public class GLProgramBuilder {
    static Logger logger = Logger.getLogger(GLProgramBuilder.class);

    List<AbstractDelegateBuilder> delegateBuilders = new ArrayList<AbstractDelegateBuilder>();

    private GLProgram program;

    public void addDelegateBuilder(AbstractDelegateBuilder delegate) {
        this.delegateBuilders.add(delegate);
    }

    static void append(GLProgram program, RenderingMethodDescriptor method) throws Exception {
        for (ShaderRef shader_ref : method.getShadersReferences()) {
            Shader shader = null;
            try {
                if (shader_ref.location != null) {
                    URL location_resolved = null;
                    String shader_name = FilenameUtils.getName(shader_ref.location.toString());
                    if (shader_ref.location.isAbsolute()) {
                        location_resolved = shader_ref.location.toURL();
                        shader = new Shader(shader_name, shader_ref.type, shader_ref.location.toURL());
                    } else {
                        // Is this a relative path?
                        URI mlocation = method.getLocation().toURI();
                        location_resolved = mlocation.resolve(shader_ref.location).toURL();
                        if (!testURLValid(location_resolved)) {
                            location_resolved = Shader.DEFAULT_SHADERS_LOCATION_DIR.toURI().resolve(shader_ref.location).toURL();
                            if (!testURLValid(location_resolved))
                                location_resolved = null;
                        }
                    }
                    if (location_resolved != null) {
                        shader = new Shader(shader_name, shader_ref.type, location_resolved);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (shader == null) {
                logger.error("Failed to load the shader " + shader_ref + " for the redering method " + method + ". Rendering with this method may cause unexpected behaviors.");
                throw new Exception("SHADER "+shader_ref.toString()+" at location "+shader_ref.location+" WAS NOT FOUND : failed to build the program "+program.getName());
                }
            shader.addToGLProgram(program);
        }
    }

    private static boolean testURLValid(URL url) throws IOException {
        URLConnection huc = url.openConnection();
        try {
            huc.connect();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public GLProgram build(String progname, RenderingMethodDescriptor method) {
        if (program == null)
            program = new GLProgram(progname);
        for (AbstractDelegateBuilder delegate : this.delegateBuilders) {
            delegate.preBuildActions(program);
        }
        if (method != null) {
            // Load the rendering methods
            String supermethod = method.getSuperMethod();
            if (supermethod != null && !supermethod.equals("")) {
                RenderingMethodDescriptor generalMethod = (RenderingMethodDescriptor) ResourcesManager.Root().getSubManager(GeoxygeneConstants.GEOX_Const_RenderingMethodsManagerName)
                        .getResourceByName(supermethod);
                // Build the general Method
                this.build(progname, generalMethod);
            }
            try {
                GLProgramBuilder.append(program, method);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        for (AbstractDelegateBuilder delegate : this.delegateBuilders) {
            delegate.postBuildActions(program);
        }
        return program;

    }

}
