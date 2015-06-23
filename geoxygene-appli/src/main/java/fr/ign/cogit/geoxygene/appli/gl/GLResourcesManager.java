package fr.ign.cogit.geoxygene.appli.gl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
        URL schema_path = this.getClass().getResource(
                "/xml/resource-dic-schema.xsd");
        this.registerResource("DICTIONARY_SCHEMA", schema_path, true);
    }

    public synchronized boolean  registerResource(String resource_name, Object resource,
            boolean allow_override) {
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

        if (this.resources_names_dic.containsKey(resource_name)
                && !allow_override) {
            logger.error("A ressource named "
                    + resource_name
                    + " is already registered. Cannot override without the allow_override option.");
            return false;
        }
        if (this.resources_names_dic.containsKey(resource_name)
                && allow_override) {
            Integer resource_uid = this.resources_names_dic.get(resource_name);
            this.resources_dic.put(resource_uid, resource);
        }

        Integer uid = this.uidprovider.generateNewUID();
        assert (allow_override || getResourceByID(uid) == null);

        this.resources_names_dic.put(resource_name, uid);
        this.resources_dic.put(uid, resource);

        if (logger.isDebugEnabled()) {
            logger.debug("GLResourcesManager stats : "
                    + this.resources_names_dic.size()
                    + " resource name entries and " + this.resources_dic.size()
                    + " resource entries");
        }
        return true;
    }

    public synchronized void unregisterResource(String resource_name) {
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
            logger.error("No resource named " + name
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
                uid = ++greatest_UID;
            }
            return uid;
        }
    }

    public synchronized void registerDictionary(URL dictionary_location,
            boolean allow_override) {
        if (dictionary_location != null) {
            File dic_file = new File(dictionary_location.getPath());
            if (!dic_file.exists()) {
                logger.error("Dictionary " + dic_file.getPath() + " not found");
            }
            if (this.parseAndAddDictionary(dic_file, allow_override) > 0) {
                logger.debug("Resource dictionary parsed and added to the GLResourceManager");
            }
        }
    }

    private int parseAndAddDictionary(File dic_file, boolean allow_override) {
        int entries_added = 0;
        try {
            // We parse manually since the dictionary file is simple
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            dbFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(dic_file);
            doc.getDocumentElement().normalize();

            // Validate the XML against the dictionary schema.
            SchemaFactory factory = SchemaFactory
                    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL schemaPath = (URL) GLResourcesManager.getInstance()
                    .getResourceByName("DICTIONARY_SCHEMA");
            if (schemaPath == null) {
                logger.error("The resources dictionary schema is unavailable, dictionaries cannot be validated. Register a valid schema before adding new dictionaries to the resources manager.");
                return 0;
            }
            File schemafile = new File(schemaPath.getFile());
            if (schemafile == null || !schemafile.exists()) {
                logger.error("Cannot read the resource dictionary schema located at "
                        + schemaPath);
                return 0;
            }
            Schema schema = factory.newSchema(schemafile);
            Validator validator = schema.newValidator();
            try {
                validator.validate(new DOMSource(doc));
            } catch (SAXException e) {
                logger.error("XML validation failed!");
                e.printStackTrace();
            }

            NodeList dicprop = doc.getElementsByTagName("DictionaryProperties");
            int number_of_entries_awaited = -1;
            if (dicprop != null && dicprop.getLength() > 0) {
                NodeList properties = dicprop.item(0).getChildNodes();
                if (properties != null) {
                    for (int i = 0; i < properties.getLength(); i++) {
                        if (properties.item(i).getNodeName()
                                .equalsIgnoreCase("ResourcesCount")) {
                            number_of_entries_awaited = Integer
                                    .parseInt(properties.item(i).getNodeValue());
                        }
                    }
                }
            }
            NodeList dicentries = doc.getElementsByTagName("ResourceEntry");
            if (dicentries != null && dicentries.getLength() > 0) {
                if (number_of_entries_awaited > -1
                        && number_of_entries_awaited != dicentries.getLength()) {
                    logger.error("While parsing a resource dictionary : "
                            + number_of_entries_awaited + " awaited but "
                            + dicentries.getLength() + " were found");
                    return 0;
                }
                for (int i = 0; i < dicentries.getLength(); i++) {
                    Node resourceentry = dicentries.item(i);
                    NodeList children = resourceentry.getChildNodes();
                    assert (children.getLength() == 2);
                    if (children == null || children.getLength() < 2) {
                        logger.error("While parsing a resource dictionary : ResourceEntry is null or has an invalid number of children.");
                    } else {
                        String resource_name = null;
                        String resource_value = null;
                        for (int j = 0; j < children.getLength(); j++) {
                            Node n = children.item(j);
                            if (n.getNodeType() == Node.ELEMENT_NODE) {
                                if (n.getNodeName().equalsIgnoreCase(
                                        "ResourceName")) {
                                    resource_name = n.getFirstChild().getNodeValue();
                                } else if (n.getNodeName().equalsIgnoreCase(
                                        "ResourceValue")) {
                                    resource_value = n.getFirstChild().getNodeValue();
                                }
                            }
                        }
                        if (resource_name != null
                                && resource_value != null
                                && this.registerResource(resource_name,
                                        resource_value, allow_override)) {
                            entries_added++;
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return 0;
        }
        return entries_added;
    }
}
