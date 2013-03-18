package fr.ign.cogit.geoxygene.semio.legend.symbol.color;

import java.io.CharArrayWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see Licence_CeCILL-C_fr.html
 *        see Licence_CeCILL-C_en.html
 * 
 *        see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 * @author Charlotte Hoarau - IGN / Laboratoire COGIT
 *
 * @see Contrast
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "",
		propOrder = {
			"name",
			"nbContrasts",
			"contrasts"
		})
@XmlRootElement(name = "ContrastCollection")
public class ContrastCollection {
	
	static Logger logger = Logger.getLogger(ContrastCollection.class.getName());
	
	@XmlElement(name = "Name")
	protected String name;
	
	public String getName() {
		return this.name;
	}

	@XmlElement(name = "Contrast")
	private List<Contrast> contrasts = new ArrayList<Contrast>();
	
	public List<Contrast> getContrasts() {
		return this.contrasts;
	}

	public void setContrasts(List<Contrast> contrasts) {
		this.contrasts = contrasts;
		this.nbContrasts = contrasts.size();
	}

	@SuppressWarnings("unused")
    @XmlElement(name = "NbContrasts")
	private int nbContrasts;
	
	public int getNbContrasts(){
		return this.contrasts.size();
	}
	
	@SuppressWarnings("unused")
	private void setNbContrasts(int nbContrasts){
		this.nbContrasts = nbContrasts;
	}
	
	/**
	 * Empty Constructor.
	 */
	public ContrastCollection() {
		super();
	}
	
	public ContrastCollection(String name,
			List<Contrast> contrasts, int nbContrasts) {
		super();
		this.name = name;
		this.contrasts = contrasts;
		this.nbContrasts = nbContrasts;
	}
	
	public ContrastCollection(
			List<Contrast> contrasts, int nbContrasts) {
		super();
		this.contrasts = contrasts;
		this.nbContrasts = nbContrasts;
	}
	
	public ContrastCollection(
			List<Contrast> contrasts) {
		super();
		this.contrasts = contrasts;
		this.nbContrasts = contrasts.size();
	}
	
	@Override
  public String toString(){
		CharArrayWriter writer = new CharArrayWriter();
        this.marshall(writer);
        return writer.toString();
	}
	
	/**
     * Charge les valeurs de contrastes decrites dans le fichier XML.
     * Si le fichier n'existe pas, cree un nouveau fichier vide.
     * @param fileName fichier XML decrivant le fichier à charger
     * @return les valeurs de contrastes decrites dans le fichier XML
     * 				ou un fihier vide si le fichier n'existe pas.
     */
    public static ContrastCollection unmarshall(String fileName) {
    	logger.info("unmarshalling the contrast matrix");
        try {
            return unmarshall(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            logger.error("File " + fileName + " could not be read");
            return new ContrastCollection();
        }
    }

    public static ContrastCollection unmarshall(InputStream stream) {
        try {
            JAXBContext context = JAXBContext.newInstance(
            		ContrastCollection.class);
            Unmarshaller m = context.createUnmarshaller();
            ContrastCollection contrastcollection = (ContrastCollection) m
            .unmarshal(stream);
            return contrastcollection;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return new ContrastCollection();
    }
    
    public void marshall(Writer writer) {
        try {
            JAXBContext context = JAXBContext.newInstance(
            		ContrastCollection.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(this, writer);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public void marshall(OutputStream stream) {
        try {
            JAXBContext context = JAXBContext.newInstance(
            		ContrastCollection.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(this, stream);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sauve le SLD dans le fichier en parametre
     * @param fileName fichier dans lequel on sauve le SLD
     */
    public void marshall(String fileName) {
        try {
            this.marshall(new FileOutputStream(fileName));
        } catch (FileNotFoundException e) {
            logger.error("File " + fileName + " could not be written to"); //$NON-NLS-1$//$NON-NLS-2$
        }
    }
    
    private static ContrastCollection cogitContrasts = null;
    
    public static Contrast getCOGITContrast(ColorimetricColor c1, ColorimetricColor c2) {
    	Contrast c = null;
    	
    	if (cogitContrasts == null) {
    		  cogitContrasts = ContrastCollection
				.unmarshall(ContrastCollection.class.getResource("/symbol/color/Contrast.xml").getPath());
    		 }
    	for (Contrast contrast : cogitContrasts.getContrasts()) {
			if (contrast.getIdC1() == c1.getIdColor()) {
				if (contrast.getIdC2() == c2.getIdColor()) {
					c = contrast;
				}
			}
		}
    	return c;
    }
    
    public Contrast getCOGITContrastPerso(ColorimetricColor c1, ColorimetricColor c2){
    	Contrast c = null;
    	for (Contrast contrast : this.getContrasts()) {
			if (contrast.getIdC1() == c1.getIdColor()) {
				if (contrast.getIdC2() == c2.getIdColor()) {
					c = contrast;
					break;
				}
			}
		}
    	return c;
    }
}
