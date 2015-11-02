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

package fr.ign.cogit.geoxygene.appli.render.methods;

import java.util.HashMap;
import java.util.Map;

public class NamedRenderingParametersMap extends HashMap<RenderingMethodParameterDescriptor, Object> {

    private static final long serialVersionUID = -4707717986712910691L;

    Map<String, RenderingMethodParameterDescriptor> map_desc_names;
    Map<String, RenderingMethodParameterDescriptor> map_desc_uniforms_names;

    public NamedRenderingParametersMap() {
        super();
        this.map_desc_names = new HashMap<>();
        this.map_desc_uniforms_names = new HashMap<>();
    }

    public NamedRenderingParametersMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        this.map_desc_names = new HashMap<>(initialCapacity, loadFactor);
        this.map_desc_uniforms_names = new HashMap<>(initialCapacity, loadFactor);
    }

    public NamedRenderingParametersMap(int initialCapacity) {
        super(initialCapacity);
        this.map_desc_names = new HashMap<>(initialCapacity);
        this.map_desc_uniforms_names = new HashMap<>(initialCapacity);
    }

    public NamedRenderingParametersMap(Map<? extends RenderingMethodParameterDescriptor, ? extends Object> m) {
        super(m);
        for (java.util.Map.Entry<? extends RenderingMethodParameterDescriptor, ? extends Object> e : m.entrySet()) {
            this.map_desc_names.put(e.getKey().getName(), e.getKey());
            if (e.getKey().getParameterUniformName() != null && !e.getKey().getParameterUniformName().isEmpty())
                this.map_desc_uniforms_names.put(e.getKey().getUniform_name(), e.getKey());
        }
    }

    public Object getByName(String desc_name) {
        RenderingMethodParameterDescriptor dec = this.map_desc_names.get(desc_name);
        if (dec == null)
            return null;
        return this.get(dec);
    }

    public Object getByUniformRef(String uniform_reference_name) {
        RenderingMethodParameterDescriptor dec = this.map_desc_uniforms_names.get(uniform_reference_name);
        if (dec == null)
            return null;
        return this.get(dec);
    }

    @Override
    public Object put(RenderingMethodParameterDescriptor key, Object value) {
        this.map_desc_names.put(key.getName(), key);
        if (key.getParameterUniformName() != null && !key.getParameterUniformName().isEmpty())
            this.map_desc_uniforms_names.put(key.getParameterUniformName(), key);
        return super.put(key, value);
    }

    public boolean containsParameterWithName(String param_name) {
        return this.map_desc_names.containsKey(param_name);
    }

    public boolean containsParameterWithUniformRef(String uniform_name) {
        return this.map_desc_uniforms_names.containsKey(uniform_name);
    }

    @Override
    public Object remove(Object key) {
        if (key instanceof RenderingMethodParameterDescriptor) {
            this.map_desc_names.remove(((RenderingMethodParameterDescriptor) key).getName());
            this.map_desc_names.remove(((RenderingMethodParameterDescriptor) key).getParameterUniformName());
        }
        return super.remove(key);
    }

    @Override
    public void clear() {
        super.clear();
        this.map_desc_names.clear();
        this.map_desc_uniforms_names.clear();
    }

}
