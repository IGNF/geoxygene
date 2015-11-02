package fr.ign.cogit.geoxygene.appli.render.groups;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

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
        Logger.getRootLogger().error(o.getClass().getSimpleName()+" cannot be flattened.");
        return null;
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
