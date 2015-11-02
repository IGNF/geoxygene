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

package fr.ign.cogit.geoxygene.appli.render.methods;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.gl.Shader;

public class RenderingMethodBuilder {
    public final static URL DEFAULT_METHODS_LOCATION_DIR = Shader.class.getClassLoader().getResource("methods/");

    public static RenderingMethodDescriptor build(URI location) {
        RenderingMethodDescriptor method = RenderingMethodDescriptor.unmarshall(new File(location));
        if(method == null){
            Logger.getRootLogger().info("Failed to build the RenderingMethod located at " +location);
        }else{
            Logger.getRootLogger().info("RenderingMethod "+method.getName()+" at " +location+" built.");    
        }
        return method;
    }

    public static RenderingMethodDescriptor build(URI root_location, String relative_location) {
        try {

            URI rel_uri = new URI(relative_location);
            if(rel_uri.isAbsolute()){
                return build(new URI(relative_location));
            }
            URI root_loc = root_location;
            if(root_location == null){
                root_loc = DEFAULT_METHODS_LOCATION_DIR.toURI();
            }
            URI r = root_loc.resolve(rel_uri);
            if(r != rel_uri){
                return build(r);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
