
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
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.expressive.ExpressiveDescriptor;

/**
 * The rendering group for the Fill style element.
 */
public class FillRenderingGroup extends RenderingGroup {

    public static final String DEFAULT_RENDERING_METHOD_FILL = "AWTFill";

    public FillRenderingGroup(String name, RenderingMethodDescriptor desc, Fill f) {
        super(name, desc, f);
    }

    @Override
    /**
     * Get or build the parameters for a stroke
     */
    protected Map<String, Object> create() {
        Map<String, Object> params = new HashMap<String, Object>();

        if (style_element instanceof Fill) {
            Fill fill = (Fill) style_element;
            Map<String, Object> sparams = StyleElementFlattener.flatten(fill);
            params.putAll(sparams);
            if (fill.getExpressiveFill() != null) {
                ExpressiveDescriptor es = fill.getExpressiveFill();
                Map<String, Object> expparams = StyleElementFlattener.flatten(es);
                params.putAll(expparams);
            }
        } else {
            Logger.getRootLogger().error("Class" + style_element.getClass().getSimpleName() + " is not a valid class for "+this.getClass());
        }
        return params;
    }

    public static RenderingMethodDescriptor getFillRenderingMethod(Fill f) {
        String method_name = null;
        if (f != null) {
            if (f.getExpressiveFill() != null) {
                method_name = f.getExpressiveFill().getRenderingMethod();
                if (method_name == null)
                    Logger.getRootLogger().error("The Fill " + f + " has an expressive Fill but no Rendering Method. The default method " + DEFAULT_RENDERING_METHOD_FILL + " wille be used");
            }

            if (method_name == null)
                method_name = DEFAULT_RENDERING_METHOD_FILL;
            return RenderingMethodDescriptor.retrieveMethod(method_name);
        }
        return null;
    }

}
