package fr.ign.cogit.geoxygene.appli.render.groups;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodDescriptor;
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
        
        Logger.getRootLogger().error("Cannot create a RenderingGroup for a style element of class "+style_element.getClass().getName());
        return null;
    }
}
