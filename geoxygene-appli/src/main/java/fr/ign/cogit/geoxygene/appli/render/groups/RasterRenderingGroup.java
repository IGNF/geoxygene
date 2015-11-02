package fr.ign.cogit.geoxygene.appli.render.groups;

import java.util.HashMap;
import java.util.Map;

import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodDescriptor;
import fr.ign.cogit.geoxygene.style.RasterSymbolizer;

public class RasterRenderingGroup  extends RenderingGroup {
    
    public static final String DEFAULT_RENDERING_METHOD_RASTER = "Raster";

    public RasterRenderingGroup(String _name, RenderingMethodDescriptor renderingmethod, Object style_element) {
        super(_name, renderingmethod, style_element);
    }

    @Override
    /**
     * Get or build the parameters for a raster
     */
    protected Map<String, Object> create() {
        Map<String, Object> params =  new HashMap<String, Object>();
        RasterSymbolizer rs = (RasterSymbolizer) this.style_element;
        params.put("Animate", rs.getAnimate());
        return params;
    }
    
    public static RenderingMethodDescriptor getRasterRenderingMethod(RasterSymbolizer r) {
        return RenderingMethodDescriptor.retrieveMethod(DEFAULT_RENDERING_METHOD_RASTER);
    }

}
