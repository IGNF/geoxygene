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
            Logger.getRootLogger().error("Cannot create the Stroke parameters for a styling element of class" + style_element.getClass().getSimpleName());
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
