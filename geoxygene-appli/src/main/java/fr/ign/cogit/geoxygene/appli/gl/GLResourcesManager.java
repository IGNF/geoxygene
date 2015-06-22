package fr.ign.cogit.geoxygene.appli.gl;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * @author Bertrand Duménieu
 *
 */
public class GLResourcesManager {

    Logger logger = Logger.getLogger(GLResourcesManager.class);

    /**
     * GLResourcesManager is a global manager viewed as a singleton object
     * shared by all Geoxygene projects.
     */

    /** singleton holder **/
    private static class SingletonHolder {
        private final static GLResourcesManager singleton = new GLResourcesManager();
    }

    public static GLResourcesManager getInstance() {
        return SingletonHolder.singleton;
    }

    private Map<Integer, Object> resources_dic;

    private Map<String, Integer> resources_names_dic;

    private final ResourceUIDProvider uidprovider;

    public GLResourcesManager() {
        this.resources_dic = new HashMap<>();
        this.resources_names_dic = new HashMap<>();
        this.uidprovider = new ResourceUIDProvider(0);
    }

    public void registerResource(String resource_name, Object resource,
            boolean allow_override) {
        assert (resource_name != null && resource_name != "" && resource != null);

        if (resource_name == null || resource_name.equalsIgnoreCase("")) {
            logger.error("The name " + resource_name
                    + " is an invalid resource name.");
            return;
        }

        if (resource == null) {
            logger.error("Cannot register null resources");
            return;
        }

        if (this.resources_names_dic.containsKey(resource_name)
                && !allow_override) {
            logger.error("A ressource named "
                    + resource_name
                    + " is already registered. Cannot override without the allow_override option.");
            return;
        }
        if (this.resources_names_dic.containsKey(resource_name)
                && allow_override) {
            Integer resource_uid = this.resources_names_dic.get(resource_name);
            this.resources_dic.put(resource_uid, resource);
        }

        Integer uid = this.uidprovider.generateNewUID();
        assert (getResourceByID(uid) == null);

        this.resources_names_dic.put(resource_name, uid);
        this.resources_dic.put(uid, resource);
        
        if(logger.isDebugEnabled()){
            logger.debug("GLResourcesManager stats : "+this.resources_names_dic.size()+" resource name entries and "+this.resources_dic.size()+" resource entries");
        }
    }

    public void unregisterResource(String resource_name) {
        Integer uid = this.resources_names_dic.get(resource_name);
        if (uid == null) {
            logger.error("UnregisterResource : the resource " + resource_name
                    + " doesn't exist in the dictionnary.");
            return;
        }
        this.resources_names_dic.remove(resource_name);
        this.resources_dic.remove(uid);
        this.uidprovider.releaseUID(uid);
    }

    public Object getResourceByName(final String name) {
        assert (name != null && name != "");
        assert (resources_names_dic.get(name) != null);

        if (resources_names_dic.containsKey(name)) {
            int resource_keyID = resources_names_dic.get(name);
            Object o = resources_dic.get(resource_keyID);
            if (o == null) {
                logger.error("The shared resource " + name
                        + " exists but has no value");
            }
            return o;
        } else {
            logger.error("No resource named" + name
                    + " was found in the GLResourceManager");
            return null;
        }
    }

    public Object getResourceByID(final Integer id) {
        Object o = null;
        if (this.resources_dic.containsKey(id)) {
            o = this.resources_dic.get(id);
        } else {
            logger.error("There is no resource registered with the id " + id);
        }
        return o;
    }

    private class ResourceUIDProvider {
        private LinkedList<Integer> released_uids;

        private Integer greatest_UID;

        public ResourceUIDProvider(int seed) {
            released_uids = new LinkedList<Integer>();
            this.greatest_UID = seed;
        }

        public boolean releaseUID(final Integer uid) {
            boolean b = released_uids.add(uid);
            if (b) {
                Collections.sort(released_uids);
            }
            return b;
        }

        public Integer generateNewUID() {
            Integer uid;
            if (!released_uids.isEmpty()) {
                uid = released_uids.get(0);
                released_uids.remove(0);
            } else {
                uid = greatest_UID++;
            }
            return uid;
        }
    }

}
