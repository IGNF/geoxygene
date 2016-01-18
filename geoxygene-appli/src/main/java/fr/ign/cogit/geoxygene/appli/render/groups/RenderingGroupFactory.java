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

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodDescriptor;
import fr.ign.cogit.geoxygene.style.ExternalGraphic;
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.RasterSymbolizer;
import fr.ign.cogit.geoxygene.style.Stroke;

public class RenderingGroupFactory {

    
    public static RenderingGroup createRenderingGroup(Object style_element){
        if(style_element instanceof Stroke){
            Stroke s = (Stroke) style_element ;
            RenderingMethodDescriptor desc = StrokeRenderingGroup.getStrokeRenderingMethod(s);
            return new StrokeRenderingGroup("RGroup.Stroke."+s.hashCode(),desc, s);
        }
        if(style_element instanceof Fill){
            Fill f = (Fill) style_element ;
            RenderingMethodDescriptor desc = FillRenderingGroup.getFillRenderingMethod(f);
            return new FillRenderingGroup("RGroup.Fill."+f.hashCode(),desc, f);
        }
        if(style_element instanceof RasterSymbolizer){
            RasterSymbolizer raster = (RasterSymbolizer) style_element ;
            RenderingMethodDescriptor desc =RasterRenderingGroup.getRasterRenderingMethod(raster);
            return new RasterRenderingGroup("RGroup.Raster."+raster.hashCode(),desc, raster);
        }
        if(style_element instanceof ExternalGraphic){
            ExternalGraphic ec = (ExternalGraphic) style_element;
            //To display External Graphics, we use the default texturing method
            RenderingMethodDescriptor desc = RenderingMethodDescriptor.retrieveMethod("TextureFill");
            return new ExternalGraphicRenderingGroup("RGroup.ExternalGraphic."+ec.hashCode(),desc,ec);
        }
        Logger.getRootLogger().error("Cannot create a RenderingGroup for a style element of class "+style_element.getClass().getName());
        return null;
    }
}
