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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLShader;

public class Shader extends GLShader {

    public final static URL DEFAULT_SHADERS_LOCATION_DIR = Shader.class.getClassLoader().getResource("shaders/");
    
    private URL location = null;


    public Shader(String _name, int _gltype, URL shader_location) {
        super(_name, _gltype);
        this.uniforms_names = new ArrayList<String>(1);
        this.uniforms_types = new ArrayList<Integer>(1);
        this.location = shader_location;
        this.setLocation(shader_location);
    }
    public Shader(String _name, String _gltype_as_string, URL shader_location) {
        super(_name, _gltype_as_string);
        this.uniforms_names = new ArrayList<String>(1);
        this.uniforms_types = new ArrayList<Integer>(1);
        this.location = shader_location;
        this.setLocation(shader_location);
    }

    public void setLocation(URL shader_location) {
        this.location = shader_location;
        if (this.location != null) {
            loadShaderSource();
        }
    }

    private void loadShaderSource() {
        try {
            InputStream source_stream = this.location.openStream();
            Writer writer = new StringWriter();
            IOUtils.copy(source_stream, writer);
            this.source = writer.toString();
            this.parseUniforms();
        } catch (IOException e) {
            Logger.getRootLogger().error("In shader " + this.name + " : the shader source cannot be read or parsed.");
            e.printStackTrace();
            this.source = null;
        }
    }

    private void parseUniforms() throws IOException {
        if (this.source != null) {
            BufferedReader reader = new BufferedReader(new StringReader(this.source));
            String line = null;
            int nline = 0;
            while ((line = reader.readLine()) != null) {
                // Parse the uniforms
                if (line.toLowerCase().startsWith("uniform")) {
                    String[] tokens = line.split(" ");
                    // The second token contains the uniform type
                    String utype = tokens[1];
                    // Third token is either the name or the name concatened
                    // with the default value.
                    String[] nametokens = tokens[2].split("=");
                    String uname = nametokens[0].replace(";", "");
                    // String udefault = "";
                    // if (nametokens.length > 1) {
                    // udefault = nametokens[1].replace(";", "");
                    // } else if (tokens.length >= 5) {
                    // udefault = tokens[4].replace(";", "");
                    // }
                    if (uname != null && utype != null) {
                        uniforms_names.add(uname);
                        uniforms_types.add(this.resolveType(utype));
                    } else {
                        Logger.getRootLogger().error("Encountered a problem while parsing line " + nline + " in shader file " + this.getName() + " : cannot parse uniform " + line);
                        reader.close();
                        throw new IOException();
                    }
                }
                nline++;
            }
        }
    }

    private int resolveType(String utype) {
        switch (utype.toLowerCase()) {
        case "float":
            return GL11.GL_FLOAT;
        case "int":
            return GL11.GL_INT;
        case "sampler2d":
            return GL20.GL_SAMPLER_2D;
        case "vec2":
            return GL20.GL_FLOAT_VEC2;
        default:
            Logger.getRootLogger().error("Error while parsing the uniforms in the shader " + this.getName() + " : uniform type " + utype + " is unknown");
            return 0;
        }
    }
    
    public void addToGLProgram(GLProgram p){
        p.addShader(this);
    }
}
