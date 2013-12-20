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

package fr.ign.cogit.geoxygene.appli.render.primitive;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.render.RenderingException;
import fr.ign.cogit.geoxygene.style.Symbolizer;

/**
 * @author JeT
 *         Convert geoxygene features to rendering primitives.
 *         This class stores previous conversion and return them if asked
 *         multiple times
 */
public class CachedParameterizedConverter implements ParameterizedConverter {

    private static Logger logger = Logger.getLogger(CachedParameterizedConverter.class.getName());
    private final Map<IFeature, DrawingPrimitive> previousConversions = new HashMap<IFeature, DrawingPrimitive>();
    private final ParameterizedConverter converter = new DirectParameterizedConverter();

    /**
     * Constructor
     */
    public CachedParameterizedConverter() {
    }

    @Override
    public DrawingPrimitive convert(final IFeature feature, final Symbolizer symbolizer, final Viewport viewport) throws RenderingException {
        DrawingPrimitive primitive = this.previousConversions.get(feature);
        if (primitive == null) {
            primitive = this.converter.convert(feature, symbolizer, viewport);
            this.previousConversions.put(feature, primitive);
        } else {
            primitive.update(viewport);
        }
        return primitive;
    }
}
