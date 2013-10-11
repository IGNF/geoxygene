package fr.ign.cogit.geoxygene.appli;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.jdbc.postgis.ConnectionParam;

/**
 * properties for geoxygene configuration
 * plugin: plugin package to load (multiple)
 * lastOpenedFile: directory for file/save file chooser (single)
 * preload: file to load at startup (multiple)
 * DefaultVisualizationType: "AWT" or "LWJGL" (single) if other, default is AWT
 * ConnectionParam: (single)
 * ProjectFrameLayout: "floating" or "tabbed" (single) default is Floating
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "GeOxygeneApplicationProperties")
public class GeOxygeneApplicationProperties {
  
    /** Logger. */
    static Logger logger = Logger.getLogger(GeOxygeneApplicationProperties.class.getName());
  
    @XmlElements(@XmlElement(name = "plugin", type = String.class))
    private List<String> plugins = new ArrayList<String>();
    
    @XmlElement(name = "lastOpenedFile")
    private String lastOpenedFile = ""; //$NON-NLS-1$

    @XmlElements(@XmlElement(name = "preload", type = String.class))
    private List<String> preloads = new ArrayList<String>();
    
    @XmlElement(name = "ConnectionParam")
    private ConnectionParam connectionParam = new ConnectionParam();

    @XmlElement(name = "DefaultVisualizationType")
    private String defaultVisualizationType = "AWT";

    @XmlElement(name = "ProjectFrameLayout") // "floating" or "tabbed"
    private String projectFrameLayout = "floating";

    /**
     * Return plugin list.
     * @return List<String>
     */
    public List<String> getPlugins() {
        return this.plugins;
    }

    /**
     * Return preload list.
     * @return List<String>
     */
    public List<String> getPreloads() {
        return this.preloads;
    }

    /** 
     * Return last opened file.
     * @return string
     */
    public String getLastOpenedFile() {
        return this.lastOpenedFile;
    }
    
    /**
     * Return connection list.
     * @return List<String>
     */
    public ConnectionParam getConnectionParam() {
        return this.connectionParam;
    }

    /**
     * Set last opened file.
     * @param lastOpenedFile
     */
    public void setLastOpenedFile(String lastOpenedFile) {
        this.lastOpenedFile = lastOpenedFile;
    }
 
    /** 
     * Return the default visualization type "AWT" or "LWJGL".
     * @return string "AWT" or "LWJGL"
     */
    public String getDefaultVisualizationType() {
        return this.defaultVisualizationType;
    }
    
   /** 
     * Set the default visualization type "AWT" or "LWJGL".
     * @param defaultViz "AWT" or "LWJGL"
     */
    public void setDefaultVisualizationType( String defaultViz ) {
        this.defaultVisualizationType = defaultViz;
    }

    /**
     * "floating" uses JInternal frames for ProjectFrames
     * "tabbed" uses A JTabbedPane to dock ProjectFrames
     * @return the main frame layout: 
     */ 
    public String getProjectFrameLayout() {
    return projectFrameLayout;
  }

  /**
   *      * "Floating" uses JInternal frames for ProjectFrames
     * "Tabbed" uses A JTabbedPane to dock ProjectFrames
   * @param projectFrameLayout "floating" or "tabbed"
   */
  public void setProjectFrameLayout(String projectFrameLayout) {
    this.projectFrameLayout = projectFrameLayout;
  }

    public void setConnectionParam(ConnectionParam param) {
        this.connectionParam = param;
    }

    /**
     * Load the properties from the specified stream.
     * 
     * @param stream stream to load the properties from
     * @return the properties loaded from the specified stream
     */
    public static GeOxygeneApplicationProperties unmarshall(InputStream stream) {
        try {
            JAXBContext context = JAXBContext.newInstance(GeOxygeneApplicationProperties.class);
            Unmarshaller m = context.createUnmarshaller();
            GeOxygeneApplicationProperties properties = (GeOxygeneApplicationProperties) m.unmarshal(stream);
            return properties;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return new GeOxygeneApplicationProperties();
    }

    /**
     * Load the properties from the specified file.
     * 
     * @param fileName file to load the properties from
     * @return the properties loaded from the specified file
     */
    public static GeOxygeneApplicationProperties unmarshall(String fileName) {
        try {
            return GeOxygeneApplicationProperties.unmarshall(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            GeOxygeneApplicationProperties.logger
                .error("File " + fileName + " could not be found"); //$NON-NLS-1$//$NON-NLS-2$
            return new GeOxygeneApplicationProperties();
        }
    }

    /**
     * Save the properties using the specified writer.
     * 
     * @param writer writer to save the properties into
     */
    public void marshall(Writer writer) {
        try {
            JAXBContext context = JAXBContext.newInstance(GeOxygeneApplicationProperties.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(this, writer);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the properties using the specified stream.
     * 
     * @param stream stream to save the properties into
     */
    public void marshall(OutputStream stream) {
        try {
            JAXBContext context = JAXBContext.newInstance(GeOxygeneApplicationProperties.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(this, stream);
            stream.close();
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the properties in the specified file.
     * 
     * @param fileName name of the file to save the properties into
     */
    public void marshall(String fileName) {
        try {
            this.marshall(new FileOutputStream(fileName));
        } catch (FileNotFoundException e) {
            GeOxygeneApplicationProperties.logger.error("File " + fileName + " could not be written to");
        }
    }

    
}
