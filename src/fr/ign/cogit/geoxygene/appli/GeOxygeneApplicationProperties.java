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

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "GeOxygeneApplicationProperties")
public class GeOxygeneApplicationProperties {
    static Logger logger = Logger
    .getLogger(GeOxygeneApplicationProperties.class.getName());
    @XmlElements(@XmlElement(name = "plugin", type = String.class))
    private List<String> plugins = new ArrayList<String>();
    public List<String> getPlugins() {
        return this.plugins;
    }
    private String lastOpenedFile = "";
    public String getLastOpenedFile() {
        return this.lastOpenedFile;
    }
    public void setLastOpenedFile(String lastOpenedFile) {
        this.lastOpenedFile = lastOpenedFile;
    }

    /**
     * Load the properties from the specified stream.
     * 
     * @param stream
     *            stream to load the properties from
     * @return the properties loaded from the specified stream
     */
    public static GeOxygeneApplicationProperties unmarshall(InputStream stream) {
        try {
            JAXBContext context = JAXBContext.newInstance(
                    GeOxygeneApplicationProperties.class);
            Unmarshaller m = context.createUnmarshaller();
            GeOxygeneApplicationProperties properties = (GeOxygeneApplicationProperties) m
            .unmarshal(stream);
            return properties;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return new GeOxygeneApplicationProperties();
    }

    /**
     * Load the properties from the specified file.
     * 
     * @param fileName
     *            file to load the properties from
     * @return the properties loaded from the specified file
     */
    public static GeOxygeneApplicationProperties unmarshall(String fileName) {
        try {
            return unmarshall(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            logger.error("File " + fileName + " could not be found"); //$NON-NLS-1$//$NON-NLS-2$
            return new GeOxygeneApplicationProperties();
        }
    }

    /**
     * Save the properties using the specified writer.
     * 
     * @param writer
     *            writer to save the properties into
     */
    public void marshall(Writer writer) {
        try {
            JAXBContext context = JAXBContext
            .newInstance(GeOxygeneApplicationProperties.class);
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
     * @param stream
     *            stream to save the properties into
     */
    public void marshall(OutputStream stream) {
        try {
            JAXBContext context = JAXBContext
            .newInstance(GeOxygeneApplicationProperties.class);
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
     * @param fileName
     *            name of the file to save the properties into
     */
    public void marshall(String fileName) {
        try {
            this.marshall(new FileOutputStream(fileName));
        } catch (FileNotFoundException e) {
            logger.error("File " + fileName + " could not be written to"); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    public static void main(String[] args) {
        GeOxygeneApplicationProperties newPlugins = new GeOxygeneApplicationProperties();
        newPlugins.getPlugins().add("test1");
        String fileName = GeOxygeneApplication.class
        .getResource("/plugins.xml").getFile();
        System.out.println(fileName);
        newPlugins.marshall(fileName);
        GeOxygeneApplicationProperties plugins = GeOxygeneApplicationProperties
        .unmarshall(GeOxygeneApplication.class
                .getResource("/plugins.xml").getFile()); //$NON-NLS-1$
        for (String plugin : plugins.getPlugins()) {
            System.out.println(plugin);
        }
    }
}
