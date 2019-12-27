package fr.ign.cogit.geoxygene.semio.legend.legendContent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
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

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.semio.legend.mapContent.Map;
import fr.ign.cogit.geoxygene.semio.legend.metadata.SemanticRelation;
import fr.ign.cogit.geoxygene.semio.legend.metadata.SemanticRelationDescriptor;

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
 * @author Elodie Buard - IGN / Laboratoire COGIT
 * @author Sébastien Mustière - IGN / Laboratoire COGIT
 * @author Charlotte Hoarau - IGN / Laboratoire COGIT
 * 
 * Symbolization choices made for one or several maps. The design pattern used
 *  is the Composite pattern.
 * <p>
 * The legend is modeled as a tree (the legend points to the root of the tree): <br />
 * - <code>LegendLeaf</code>s of the tree correspond to lines in the key. <br />
 * - <code>LegendLeaf</code>s may be grouped in <code>LegendComposite</code>s,
 * themselves grouped in other <code>LegendComposite</code>. <br />
 * - <code>LegendLeaf</code> and <code>LegendComposite</code> are <code>LegendComponent</code>.
 * <p>
 * <strong>French:</strong><br />
 * Légende complète d'une (ou plusieurs) carte(s).
 * <p>
 * La légende est organisée comme un arbre (l'objet <code>Legend</code> 
 * étant la racine de cet arbre): <br />
 * - les feuilles sont les lignes de légendes associées à un symbole 
 * (<code>LegendLeaf</code>) <br />
 * - les noeuds sont des regroupements de noeuds et/ou de feuilles, 
 * appelés thèmes (<code>LegendComposite</code>)
 * <p>
 * Le patron de modélisation est l'agrégation récursive: <br />
 * - les <code>LegendLeaf</code> (ligne de légende) et les <code>LegendComposite</code> (thème) 
 * sont des <code>LegendComponent</code> (élément de légende), <br />
 * - un <code>LegendComposite</code> (thème) est composé
 * d'un nombre quelconque de <code>LegendComponent</code> (élément de légende)
 * <p>
 * 
 * @see LegendComponent
 * @see LegendComposite
 * @see LegendLeaf
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Legend",
		propOrder = {
			"name",
			"legendRoot"
		})
@XmlRootElement(name = "Legend")
public class Legend  {
	
	static Logger logger = Logger.getLogger(Legend.class.getName());
	
	/**
	 * The legend name.
	 */
	@XmlElement(name = "Name")
	private String name;
	
	/**
	 * Returns the legend name.
	 * @return the legend name.
	 */
	public String getName() {
	  return name;
	}
	
	/**
	 * Sets the legend name.
	 * @param name The legend name.
	 */
	public void setName(String name) {
	  this.name = name;
	}

	/**
	 * The root of the tree describing the legend elements.
	 */
	@XmlElement(name = "legendRoot")
	private LegendComponent legendRoot;
	
	/**
	 * Returns the root of the tree describing the legend elements. <br />
     * <strong>French: </strong>Renvoie la racine de l'arbre des éléments
     *  de légende décrivant cette légende.
	 * @return root of the tree describing the legend elements.
	 */ 
	public LegendComponent getLegendRoot() {
	  return legendRoot;
	}
	
	/**
	 * Sets the root of the tree describing the legend elements. <br />
	 * <strong>French: </strong>Définit la racine de l'arbre des éléments
	 *  de légende décrivant cette légende.
	 * @param root The root of the tree describing the legend elements.
	 */ 
	public void setLegendRoot(LegendComponent root) {
		this.legendRoot = root;
	}
	
	/**
	 * Maps symbolized with this Legend
	 */
	@XmlTransient
	private List<Map> maps = new ArrayList<Map>();
	
	/**
	 * Returns a list of the maps symbolized with this Legend.
	 * @return Maps symbolized with this Legend.
	 */
	public List<Map> getMaps() {
	  return this.maps;
	}
	
	/**
	 * Sets the list of the maps symbolized with this Legend.
	 * @param L Maps symbolized with this Legend.
	 */
	public void setMaps(List<Map> L) {
		List<Map> old = new ArrayList<Map>(this.maps);
		Iterator<Map> it1 = old.iterator();
		while (it1.hasNext()) {
			Map O =  it1.next();
			O.setMapLegend(null);
		}
		Iterator<Map> it2 = L.iterator();
		while (it2.hasNext()) {
			Map O = it2.next();
			O.setMapLegend(this);
		}
	}
	
	/**
	 * Adds a map to the list of maps symbolized with this Legend.
	 * @param O Map symbolized with this legend.
	 */
	public void addMap(Map O) {
		if (O == null) {
		  return;
		}
		this.maps.add(O);
		O.setMapLegend(this);
	}
	
	/**
	 * Removes a map from the list of maps symbolized with this Legend.
	 * @param O Map symbolized with this legend.
	 */
	public void removeMap(Map O) {
		if (O == null) {
		  return;
		}
		this.maps.remove(O);
		O.setMapLegend(null);
	}
	
	
	/**
	 * The {@link SemanticRelationDescriptor} associated to this <code>Legend</code>.
	 * It contains all the {@link SemanticRelation} described 
	 * about the components of this <code>Legend</code>.
	 */
	@XmlTransient
	private SemanticRelationDescriptor srd;
	
	/**
	 * Returns the {@link SemanticRelationDescriptor} associated to this <code>Legend</code>.
	 * @return The {@link SemanticRelationDescriptor} associated to this <code>Legend</code>.
	 */
	public SemanticRelationDescriptor getSRD() {
      return this.srd;
    }

	/**
	 * Sets the {@link SemanticRelationDescriptor} associated to this <code>Legend</code>.
	 * @param srd The {@link SemanticRelationDescriptor} associated to this <code>Legend</code>.
	 */
	public void setSRD(SemanticRelationDescriptor srd) {
	  SemanticRelationDescriptor old = this.srd;
	  this.srd = srd;

	  if ( old != null ) {
        old.setLegend(null);
      }
      if ( srd != null) {
        if (srd.getLegend() == null) {
          srd.setLegend(this);
        }
      }
	}

	/**
	 * This method finds and returns a {@link LegendLeaf} among
	 * all the leaves of this legend by its name.
	 * @param name Name of the {@link LegendLeaf} to find.
	 * @return The corresponding {@link LegendLeaf}.
	 */
	public LegendLeaf getLeaf(String name) {
		LegendLeaf leaf = new LegendLeaf();
		
		List<LegendLeaf> allLeaves = this.getLegendRoot().allLeaves();
		for (LegendLeaf legendLeaf : allLeaves) {
			if (name.equalsIgnoreCase(legendLeaf.getName())) {
				leaf = legendLeaf;
			}
		}
		
		return leaf;
	}
	
	/**
	 * This method finds and returns a {@link LegendComponent}
	 * among all the components (leaves or themes) of this legend by its name.
	 * @param name Name of the {@link LegendComponent} to find.
	 * @return The corresponding {@link LegendComponent}.
	 */
	public LegendComponent getComponent(String name) {
		LegendComponent component = new LegendComponent();
		
		List<LegendLeaf> allLeaves = this.getLegendRoot().allLeaves();
		for (LegendLeaf legendLeaf : allLeaves) {
			if (name.equalsIgnoreCase(legendLeaf.getName())) {
				component = legendLeaf;
			}
		}
		
		List<LegendComposite> allThemes = this.getLegendRoot().allThemes();
		for (LegendComposite legendTheme : allThemes) {
			if (name.equalsIgnoreCase(legendTheme.getName())) {
				component = legendTheme;
			}
		}
		
		return component;
	}
	
	/**
	 * This method finds and returns a {@link LegendComposite}
	 * among all the themes of this legend by its name.
	 * @param name Name of the {@link LegendComposite} to find.
	 * @return The corresponding {@link LegendComposite}.
	 */
	public LegendComposite getTheme(String name) {
		LegendComposite composite = new LegendComposite();
		
		List<LegendComposite> allThemes = this.getLegendRoot().allThemes();
		for (LegendComposite legendTheme : allThemes) {
			if (name.equalsIgnoreCase(legendTheme.getName())) {
				composite = legendTheme;
			}
		}
		
		return composite;
	}
	
	/**
	 * Method to build recursively a legend tree.
	 */
	public static void buildLegend(LegendComposite container, LegendComponent legendComponent){
		List<LegendComponent> legendComponents = legendComponent.getComponents();
		for (LegendComponent component : legendComponents) {
		    
		    //Leaf creation
		    //FRENCH : Création d'une feuille (ligne de légende)
			if (component.getComponents().size()==0) {
				LegendLeaf leaf = new LegendLeaf();
				leaf.setName(component.getName());
				leaf.setContainer(container);
			}
			
			//Composite creation
			//FRENCH : Création d'un thème
			else{ 
				LegendComposite legendComposite = new LegendComposite();
				legendComposite.setName(component.getName());
				legendComposite.setContainer(container);
				legendComposite.setComponents(component.getComponents());
				buildLegend(legendComposite,component);
			}
		}
	}
	
	/**
     * Loads the Legend described in the input file.
     * If the file does'nt exist, a new empty Legend is created.
     * @param stream Input file describing the Legend to load.
     * @return The Legend described in the input file or a new empty Legend if the file does'nt exist.
     */
	public static Legend unmarshall(InputStream stream) {
        try {
            JAXBContext context = JAXBContext.newInstance(
            		Legend.class);
            Unmarshaller m = context.createUnmarshaller();
            Legend legendUnmarshall =
            	(Legend) m.unmarshal(stream);
            
            Legend legend = new Legend();
            legend.setName(legendUnmarshall.getName());
            
            LegendComposite root = new LegendComposite();
            root.setName(legendUnmarshall.getLegendRoot().getName());
            
            buildLegend(root, legendUnmarshall.getLegendRoot());
            legend.setLegendRoot(root);
            return legend;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return new Legend();
    }

    /**
     * Loads the Legend described in the XML file.
     * If the file does'nt exist, a new empty Legend is created.
     * @param fileName XML file describing the Legend to load.
     * @return The Legend described in the XML file or a new empty Legend if the file does'nt exist.
     */
    public static Legend unmarshall(String fileName) {
        try {
            return unmarshall(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            logger.error("File " + fileName + " could not be read");
            return new Legend();
        }
    }
    
    /**
     * Loads the Legend described in the XML file.
     * If the file does'nt exist, a new empty Legend is created.
     * @param XML file describing the Legend to load.
     * @return The Legend described in the XML file or a new empty Legend if the file does'nt exist.
     */
    public static Legend unmarshall(File file) {
        try {
          JAXBContext context = JAXBContext.newInstance(Legend.class);
          Unmarshaller unmarshaller = context.createUnmarshaller(); 
          return (Legend) unmarshaller.unmarshal(file); 
        } catch (Exception e) {
            logger.error("File " + file.getName() + " could not be unmarshall");
            return new Legend();
        }
    }
    
    public static Legend unmarshall(StringReader reader) throws Exception { 
      try { 
        JAXBContext context = JAXBContext.newInstance(Legend.class);
        Unmarshaller msh = context.createUnmarshaller(); 
        return(Legend)msh.unmarshal(reader); 
      } catch (Exception e1) {
        e1.printStackTrace(); 
        throw e1;
      }
    }

    /**
     * Writes the Legend.
     * @param writer The writer to write the Legend.
     */
    public void marshall(Writer writer) {
        try {
            JAXBContext context = JAXBContext.newInstance(
            		Legend.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(this, writer);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the Legend in the given file.
     * @param stream The file to save the Legend.
     */
    public void marshall(OutputStream stream) {
        try {
            JAXBContext context = JAXBContext.newInstance(
            		Legend.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(this, stream);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the Legend in the given file.
     * @param fileName The name of the file to save the Legend.
     */
    public void marshall(String fileName) {
        try {
            this.marshall(new FileOutputStream(fileName));
        } catch (FileNotFoundException e) {
            logger.error("File " + fileName + " could not be written to");
        }
    }
   
    @Override
    public String toString(){
    	String legendText = new String(
    			"Legend : " + this.getName()
    			+ System.getProperty("line.separator"));
    	
    	legendText += 
    		"Legend root : " 
    		+ this.getLegendRoot().getName()
    		+ System.getProperty("line.separator");
    	
    	List<LegendLeaf> listDirectLeaves = this.getLegendRoot().directLeaves();
    	legendText += 
    		"Nb direct leaves : " 
    		+ listDirectLeaves.size()
    		+ System.getProperty("line.separator");
    	
    	List<LegendLeaf> listAllLeaves = this.getLegendRoot().allLeaves();
    	legendText += 
    		"Nb all leaves : " 
    		+ listAllLeaves.size()
    		+ System.getProperty("line.separator");
    	
    	legendText += 
    		"FEUILLES :"
    		+ System.getProperty("line.separator");
    	for (LegendLeaf legendLeaf : this.getLeaves()) {
			legendText += "    - "+
				legendLeaf.getName()
				+ System.getProperty("line.separator");
		}
    	
    	legendText +=
    		"THEMES :"+ System.getProperty("line.separator");
    	for (LegendComposite composite : this.getThemes()) {
			legendText += "    - "+
				composite.getName()
				+ System.getProperty("line.separator");
		}
    	return legendText;
    }
    
    /**
     * Returns a list of the LegendComposite of the Legend tree. <br />
     * <strong>French: </strong>Renvoie les thèmes de la légende.
     * @return A list of the LegendComposite of the Legend tree.
     */
    public List<LegendComposite> getThemes(){
    	return this.getLegendRoot().allThemes();
    }
    
    /**
     * Returns a list of the LegendLeaves of the legend tree. <br />
     * <strong>French: </strong>Renvoie les feuilles (lignes de légende) de la légende.
     * @return A list of the LegendLeaves of the legend tree.
     */
    public List<LegendLeaf> getLeaves() {
    	return this.getLegendRoot().allLeaves();
    }
}