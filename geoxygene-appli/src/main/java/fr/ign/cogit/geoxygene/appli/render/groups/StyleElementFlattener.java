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
package fr.ign.cogit.geoxygene.appli.render.groups;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.style.ExternalGraphic;
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.style.SvgParameter;
import fr.ign.cogit.geoxygene.style.expressive.ExpressiveDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.ExpressiveParameter;

public class StyleElementFlattener {
    
    public static Map<String, Object> flatten(Object o){
        if(o instanceof Stroke){
            return flattenStroke((Stroke)o);
        }
        if(o instanceof ExpressiveDescriptor){
            return flattenExpressiveDescriptor((ExpressiveDescriptor)o);
        }
        if(o instanceof Fill){
            return flattenFill((Fill)o);
        }
        if(o instanceof ExternalGraphic){
            return flattenExternalGraphic((ExternalGraphic)o);
        }
        Logger.getRootLogger().error(o.getClass().getSimpleName()+" cannot be flattened.");
        return null;
    }

    /**
     * Flattens the parameters of an {@link ExternalGraphic} object. 
     * 
     * TODO : add the graphic Node to the flattened parameters.
     * @param o : the {@link ExternalGraphic} element to flatten.
     * @return the flattened parameters
     */
    
    private static Map<String, Object> flattenExternalGraphic(ExternalGraphic o) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("format", o.getFormat());
//        parameters.put("graphicnode", o.getGraphicsNode()); /// XXX buggy atm (18/01/2016)
        try {
            parameters.put("fill-texture", new URI(o.getHref()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } 
        return parameters;
    }

    private static Map<String, Object> flattenFill(Fill o) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        for(SvgParameter p : o.getSvgParameters()){
            parameters.put(p.getName(),p.getValue());    
        }
        return parameters;
    }

    private static Map<String, Object> flattenExpressiveDescriptor(ExpressiveDescriptor o) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        for(ExpressiveParameter p : o.getExpressiveParameters()){
            parameters.put(p.getName(),p.getValue());    
        }
        parameters.put("RenderingMethod", o.getRenderingMethod());
        return parameters;
    }

    private static Map<String, Object> flattenStroke(Stroke o) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        for(SvgParameter p : o.getSvgParameters()){
            parameters.put(p.getName(),p.getValue());    
        }
        return parameters;
    }


}
