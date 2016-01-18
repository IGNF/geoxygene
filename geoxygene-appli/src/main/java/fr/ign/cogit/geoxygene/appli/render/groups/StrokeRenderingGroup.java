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

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodDescriptor;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.style.expressive.ExpressiveDescriptor;

/**
 * The rendering group for the Stroke style element.
 */
public class StrokeRenderingGroup extends RenderingGroup {

    public static final String DEFAULT_RENDERING_METHOD_STROKE = "AWTStroke";
    public static final String DEFAULT_RENDERING_METHOD_RASTER = "Raster";
    
    public StrokeRenderingGroup(String name, RenderingMethodDescriptor desc, Stroke s) {
        super(name, desc, s);
    }

    @Override
    /**
     * Get or build the parameters for a stroke
     */
    protected Map<String, Object> create() {
        Map<String, Object> params = new HashMap<String, Object>();

        if (style_element instanceof Stroke) {
            Stroke stroke = (Stroke) style_element;
            Map<String, Object> sparams = StyleElementFlattener.flatten(stroke);
            params.putAll(sparams);
            if (stroke.getExpressiveStroke() != null) {
                ExpressiveDescriptor es = stroke.getExpressiveStroke();
                Map<String, Object> expparams = StyleElementFlattener.flatten(es);
                params.putAll(expparams);
            }
            if (stroke.getGraphicType() != null) {
                Logger.getRootLogger().error(stroke.getGraphicType() + " not yet implemented for Strokes in DisplayableCurves");
            }
        } else {
            Logger.getRootLogger().error("Class" + style_element.getClass().getSimpleName() + " is not a valid class for "+this.getClass());
        }
        return params;
    }



    public static RenderingMethodDescriptor getStrokeRenderingMethod(Stroke s) {
        String method_name = null;
        if (s!= null) {
            if (s.getExpressiveStroke() != null){
                method_name = s.getExpressiveStroke().getRenderingMethod();
                if(method_name == null)
                    Logger.getRootLogger().error("The stroke "+s+" has an expressive stroke but no Rendering Method. The default method "+DEFAULT_RENDERING_METHOD_STROKE+" wille be used" );
            }
            if(method_name == null)
                method_name = DEFAULT_RENDERING_METHOD_STROKE;
            return RenderingMethodDescriptor.retrieveMethod(method_name);
        }
        return null;
    }
    
}
