package fr.ign.cogit.geoxygene.semio.legend.metadata;

import java.io.CharArrayWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import fr.ign.cogit.geoxygene.semio.legend.legendContent.Legend;
import fr.ign.cogit.geoxygene.semio.legend.legendContent.LegendComponent;

import org.apache.log4j.Logger;

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
 * SemanticRelationshipDescriptor of a legend.It describe all the relationships
 *  of association, differentiation or order of the legendLeaf of the legend.
 * 
 * @see SemanticRelation
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SemanticRelationDescriptor",
		propOrder = {
			"name",
			"relations"
		})
@XmlRootElement(name = "SemanticRelationDescriptor")
public class SemanticRelationDescriptor {
	
	static Logger logger = Logger.getLogger(SemanticRelationDescriptor.class
            .getName());
	
	/**
	 * The name of the <code>SemanticRelationDescriptor</code>
	 */
	@XmlElement(name = "Name")
	protected String name;
	
	/**
	 * Gets the name of the <code>SemanticRelationDescriptor</code>
	 * @return The name of the <code>SemanticRelationDescriptor</code>
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of the <code>SemanticRelationDescriptor</code>
	 * @param name The name of the <code>SemanticRelationDescriptor</code>
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
     * Map legend
     */
	@XmlTransient
    private Legend legend;
    /**
     * returns the map legend
     */
    public Legend getLegend() {
      return this.legend;
    }
    
    /**
     * Specifies the map legend
     */
    public void setLegend(Legend O) {
        Legend old = this.legend;
        this.legend = O;
        
        if ( old != null ) {
          old.setSRD(null);
        }
        if ( O != null) {
          if ( O.getSRD() == null) {
            O.setSRD(this);
          }
        }
    }

	@XmlElement(name = "Relation")
	private List<SemanticRelation> relations = 
	  new ArrayList<SemanticRelation>();
	
	public List<SemanticRelation> getRelations() {
		return this.relations;
	}

	/**
     * Specifies the symbolized feature collections of the map.
     */
    public void setRelations(List<SemanticRelation> L) {
        List<SemanticRelation> old = 
          new ArrayList<SemanticRelation>(this.relations);
        Iterator<SemanticRelation> it1 = old.iterator();
        while ( it1.hasNext() ) {
          SemanticRelation O = it1.next();
            O.setSRD(null);
        }
        Iterator<SemanticRelation> it2 = L.iterator();
        while ( it2.hasNext() ) {
          SemanticRelation O = it2.next();
            O.setSRD(this);
        }
    }
    
    /**
     * Add one symbolized feature collection to the map.
     */
    public void addRelation (SemanticRelation O) {
        if ( O == null ) return;
        this.relations.add(O);
        O.setSRD(this) ;
    }
	
	public int getNbRelations(){
		return this.relations.size();
	}
	
	/**
	 * Empty Constructor.
	 */
	public SemanticRelationDescriptor() {
		super();
	}
	
	public SemanticRelationDescriptor(String name,
			List<SemanticRelation> relations, int nbRelations) {
		super();
		this.name = name;
		this.relations = relations;
	}
	
	public SemanticRelationDescriptor(
			List<SemanticRelation> relations, int nbRelations) {
		super();
		this.relations = relations;
	}
	
	@Override
  public String toString(){
		CharArrayWriter writer = new CharArrayWriter();
        this.marshall(writer);
        return writer.toString();
	}

	public static void main(String[] args) {
		SemanticRelationDescriptor srd = SemanticRelationDescriptor.unmarshall(
		    SemanticRelationDescriptor.class.getResource("MapSRD.xml").getPath());
		logger.info(srd.name);
		logger.info(srd);
	}
	
    

	/**
	 * Load the SRD described in the XML file. 
	 * If the file does not exists, an empty SRD is created.
     * <p>
     * <strong>French:</strong><br />
     * Charge le SRD decrit dans le fichier XML.
     * Si le fichier n'existe pas, cree un nouveau SRD vide.
     * @param fileName The name of the XML file describing the SRD to load.
     * @return The SRD described in the XML file or an empty SRC if 
     * the XML file does not exist.
     */
    public static SemanticRelationDescriptor unmarshall(String fileName) {
        try {
            return unmarshall(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            logger.error("File " + fileName + " could not be read");
            return new SemanticRelationDescriptor();
        }
    }

    /**
     * Load the SRD described in the XML file using the component of the given
     *  {@link Legend} object. If the file does not exists, an empty SRD is created.
     * <p>
     * <strong>French:</strong><br />
     * Charge le SRD decrit dans le fichier XML en utilisant les composant de 
     * l'object {@link Legend} donné en paramètre.
     * Si le fichier n'existe pas, cree un nouveau SRD vide.
     * 
     * @param fileName The name of the XML file describing the SRD to load.
     * @param legend Legend
     * @return The SRD described in the XML file or an empty SRC if 
     * the XML file does not exist.
     */
    public static SemanticRelationDescriptor unmarshall(String fileName, Legend legend) {
        try {
        	SemanticRelationDescriptor srd = unmarshall(new FileInputStream(fileName));
        	
        	SemanticRelationDescriptor new_srd = new SemanticRelationDescriptor();
        	List<SemanticRelation> new_relations = new ArrayList<SemanticRelation>();
        	
        	for (SemanticRelation relation : srd.getRelations()) {
				List<LegendComponent> new_components = new ArrayList<LegendComponent>();
				for (LegendComponent relatedComponent : relation.getRelatedComponents()) {
					for (LegendComponent legendComponent : legend.getLeaves()) {
						if (relatedComponent.getName().equalsIgnoreCase(legendComponent.getName())) {
							new_components.add(legendComponent);
							
						}
					}
				}
				SemanticRelation new_relation = new SemanticRelation(new_components,relation.getType());
				new_relations.add(new_relation);
			}
        	new_srd.setRelations(new_relations);
        	new_srd.setLegend(legend);
        	return new_srd;
        } catch (FileNotFoundException e) {
            logger.error("File " + fileName + " could not be read");
            return new SemanticRelationDescriptor();
        }
    }
    
    public static SemanticRelationDescriptor unmarshall(InputStream stream) {
        try {
            JAXBContext context = JAXBContext.newInstance(
            		SemanticRelationDescriptor.class);
            Unmarshaller m = context.createUnmarshaller();
            SemanticRelationDescriptor srd = (SemanticRelationDescriptor) m
            .unmarshal(stream);
            return srd;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return new SemanticRelationDescriptor();
    }
    
    public void marshall(Writer writer) {
        try {
            JAXBContext context = JAXBContext.newInstance(
            		SemanticRelationDescriptor.class);
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
            		SemanticRelationDescriptor.class);
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

}
