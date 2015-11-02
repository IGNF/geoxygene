package fr.ign.cogit.geoxygene.appli.render.groups;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.GeoxygeneConstants;
import fr.ign.cogit.geoxygene.appli.gl.ResourcesManager;
import fr.ign.cogit.geoxygene.appli.render.methods.NamedRenderingParametersMap;
import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodDescriptor;
import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodParameterDescriptor;
import fr.ign.cogit.geoxygene.appli.render.primitive.AbstractDisplayable;
import fr.ign.cogit.geoxygene.appli.render.primitive.GLDisplayable;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.texture.Texture;

/**
 * A rendering group is a set of rendering parameters to apply to a part (or
 * the whole) of a {@link GLDisplayable}. <br/>
 * E.g : a {@link PolygonSymbolizer} will contain two groups : one for the rendering
 * of its stroke and one for the rendering of its fill.
 * 
 * @author Bertrand Dumenieu
 */

public abstract class RenderingGroup {

    private Map<GLDisplayable, NamedRenderingParametersMap> parameters_cache = new WeakHashMap<>();
    private String name;

    private RenderingMethodDescriptor method;
    protected Object style_element;

    public RenderingGroup(String _name, RenderingMethodDescriptor renderingmethod, Object style_element) {
        this.name = _name;
        this.method = renderingmethod;
        this.style_element = style_element;
    }

    /**
     * Get the rendering parameters corresponding to this RenderingGroup for the
     * given {@link GLDisplayable} <br/>
     * This method run the following pipeline:
     * <ul>
     * <li>1 : call to {@link #create(GLDisplayable)}</li>
     * <li>2 : call to {@link #filter(GLDisplayable)}</li>
     * <li>3 : call to {@link #fill(GLDisplayable, Map)}</li>
     * </ul>
     * 
     * @param d
     * @return
     */
    public NamedRenderingParametersMap getRenderingParameters(GLDisplayable d) {
        NamedRenderingParametersMap resolved_parameters;
        if (this.parameters_cache.containsKey(d)) {
            resolved_parameters = this.parameters_cache.get(d);
        } else {
            Logger.getRootLogger().info("Generating a new set of parameters for the displayable "+d);
            Map<String, Object> parameters = create();
            resolved_parameters = filter(parameters, method);
            resolved_parameters = fill(d, resolved_parameters);
            this.parameters_cache.put(d, resolved_parameters);
        }
        return resolved_parameters;
    }


    /**
     * Get the parameters needed by this {@link RenderingGroup}. These
     * parameters are usually extracted from the {@link GLDisplayable}
     * symbolizer but can be anything. {@link RenderingGroup}.
     * 
     * @return the parameters descriptors for this {@link RenderingGroup}.
     */
    protected abstract Map<String, Object> create();

    /**
     * Retain only the parameters that are described in the given
     * {@link RenderingMethodDescriptor} and return the parameters value
     * associated with the parameters descriptors of the method. If not method
     * is provided, this function will create a new
     * {@link RenderingMethodParameterDescriptor} for each of the given
     * parameter.
     * 
     * @param p
     *            : the displayable
     * @param m
     *            : the rendering method for this {@link RenderingGroup}.
     * @return the input parameters p filtered by the method m and matched with
     *         their {@link RenderingMethodParameterDescriptor}. THe result is
     *         stored in a {@link NamedRenderingParametersMap} that allow
     *         accesses to parameters through their name or the the uniform name
     *         they reference.
     */
    protected NamedRenderingParametersMap filter(Map<String, Object> p, RenderingMethodDescriptor m) {
        NamedRenderingParametersMap resolved_params = new NamedRenderingParametersMap();
        if (m == null) {
            for (Entry<String, Object> e : p.entrySet()) {
                RenderingMethodParameterDescriptor desc = new RenderingMethodParameterDescriptor();
                desc.setName(e.getKey());
                desc.setType(e.getValue().getClass().getSimpleName());
                desc.setDescription("Generated in the RenderingGroup " + this.getName());
                resolved_params.put(desc, e.getValue());
            }
        }
        // Recursive method to filter the superMethod parameters
        this.filter(p, m, resolved_params);
        return resolved_params;
    }

    private void filter(Map<String, Object> p, RenderingMethodDescriptor m, NamedRenderingParametersMap resolved_params) {
        if (m.hasSuperMethod()) {
            RenderingMethodDescriptor supermethod = (RenderingMethodDescriptor) ResourcesManager.Root().getSubManager(GeoxygeneConstants.GEOX_Const_RenderingMethodsManagerName)
                    .getResourceByName(m.getSuperMethod());
            if (supermethod != null) {
                filter(p, supermethod, resolved_params);
            } else {
                Logger.getRootLogger().error(
                        "While building the rnedering parameters for " + this.getName() + " : ERROR, method " + m.getName() + " has a declared supermethod " + m.getSuperMethod()
                                + " but this method was not found in the ResourceManager");
                return;
            }
        }
        Set<String> params_names = p.keySet();
        for (RenderingMethodParameterDescriptor mp : m.getParameters()) {
            if (params_names.contains(mp.getName())) {
                resolved_params.put(mp, p.get(mp.getName()));
            }
        }
    }

    /**
     * Allow to replace or add some parameters.
     * 
     * @param d
     *            : the displayable
     * @param p
     *            : the map of parameters descriptors with their values so far.
     * @return the actual parameters used to render this {@link RenderingGroup}.
     */
    protected NamedRenderingParametersMap fill(GLDisplayable d, NamedRenderingParametersMap p) {
        AbstractDisplayable ad = (AbstractDisplayable) d;
        for (Entry<RenderingMethodParameterDescriptor, Object> entry : p.entrySet()) {
            if (entry.getValue() instanceof Texture) {
                URI uri = ad.createTexture((Texture) entry.getValue(), false);
                p.put(entry.getKey(), uri);
            }
        }
        ad.setCustomRenderingParameters(p);
        return p;
    }


    public String toString() {
        return "Rendering group " + this.name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RenderingGroup)
            if (((RenderingGroup) obj).name.equals(this.name))
                return true;
        return false;
    }

    public Object getName() {
        return this.name;
    }

    public Object getStyleElement() {
        return this.style_element;
    }

    public RenderingMethodDescriptor getMethod() {
        return this.method;
    }
}
