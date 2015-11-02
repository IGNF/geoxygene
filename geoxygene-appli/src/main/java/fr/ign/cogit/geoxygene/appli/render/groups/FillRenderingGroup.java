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
            Logger.getRootLogger().error("Cannot create the Stroke parameters for a styling element of class" + style_element.getClass().getSimpleName());
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
