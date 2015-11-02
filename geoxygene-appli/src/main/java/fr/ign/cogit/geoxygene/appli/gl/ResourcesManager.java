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

package fr.ign.cogit.geoxygene.appli.gl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.resources.DictionaryEntry;
import fr.ign.cogit.geoxygene.appli.resources.ResourceDictionary;

/**
 * GLResourcesManager is a global manager viewed as a singleton object shared by
 * all Geoxygene projects.
 * 
 * A GLResourceManager may contain multiple sub-managers.
 * 
 * @author Bertrand Duménieu
 *
 */
public class ResourcesManager {

    Logger logger = Logger.getLogger(ResourcesManager.class);

    private Map<String, Object> resources;
    
    private String name="RootManager";


    /** singleton holder **/
    private static class SingletonHolder {
        private final static ResourcesManager singleton = new ResourcesManager();
    }

    public static ResourcesManager Root() {
        return SingletonHolder.singleton;
    }

    private ResourcesManager() {
        this.resources= new HashMap<>();
    }

    public synchronized ResourcesManager addSubManager(String submanagername) {
        ResourcesManager submanager = new ResourcesManager();
        submanager.name=submanagername;
        if(this.registerResource(submanagername, submanager,
                false)){
            return submanager;    
        }
        return null;
    }

    public ResourcesManager getSubManager(String submanagername) {
        return (ResourcesManager) this.getResourceByName(submanagername);
    }

    public synchronized boolean registerResource(String resource_name,
            Object resource, boolean allow_override) {
        assert (resource_name != null && resource_name != "" && resource != null);

        if (resource_name == null || resource_name.equalsIgnoreCase("")) {
            logger.error("The name " + resource_name
                    + " is an invalid resource name.");
            return false;
        }

        if (resource == null) {
            logger.error("Cannot register null resources");
            return false;
        }

        if (this.resources.containsKey(resource_name)
                && !allow_override) {
            logger.error("A ressource named "
                    + resource_name
                    + " is already registered. Cannot override without the allow_override option.");
            return false;
        }
        if (this.resources.containsKey(resource_name)
                && allow_override) {
            this.resources.put(resource_name, resource);
            return true;
        }

        this.resources.put(resource_name, resource);

        if (logger.isInfoEnabled()) {
            logger.info("Added the resource "+resource_name+" to the ResourceManager "+this.name+". This manager now register "+this.resources.size()+" resources.");
        }
        return true;
    }

    public synchronized void unregisterResource(String resource_name) {
        
        if (!this.resources.containsKey(resource_name)) {
            logger.debug("UnregisterResource : the resource " + resource_name
                    + " doesn't exist in the dictionnary.");
            return;
        }
        this.resources.remove(resource_name);

    }

    public final Object getResourceByName(final String name) {
        assert (name != null && name != "");
        assert (resources.get(name) != null);

        if (resources.containsKey(name)) {
            Object o = resources.get(name);
            return o;
        } else {
            logger.warn("No resource named " + name
                    + " was found in the GLResourceManager");
            return null;
        }
    }


    public synchronized void loadDictionary(String name,
            ResourceDictionary dictionary, boolean allow_override) {
        if (dictionary != null) {
            if (!!allow_override && this.getResourceByName(name) != null) {
                logger.error("Cannot override existing dictionary");
                return;
            }
            if (this.addSubManager(name)!=null) {
                ResourcesManager m = this.getSubManager(name);
                for (DictionaryEntry e : dictionary.getEntries()) {
                    m.registerResource(e.getResourceName(),
                            e.getResourceValue(), false);
                }
            }

        }
    }

    public synchronized void loadDictionary(ResourceDictionary dictionary,
            boolean allow_override) {
        this.loadDictionary(dictionary.getName(), dictionary, allow_override);
    }

    public Set<String> getResourcesKeySet() {
        return this.resources.keySet();
    }

    public Collection<Object> getResourcesValueSet() {
        return this.resources.values();
    }

    public void renameResource(String old_name, String new_name) {
        synchronized (this.resources) {

            if (this.getResourceByName(old_name) != null) {
                Object value = this.resources.get(old_name);
                this.resources.remove(old_name);
                this.resources.put(new_name, value);
            }
        }
    }

    public Set<Entry<String, Object>> getResourcesEntrySet() {
        return this.resources.entrySet();
    }

    public boolean containsResource(String shadername) {
        return this.resources.containsKey(shadername);
     
    }
}
