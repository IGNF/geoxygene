package fr.ign.cogit.ontology.similarite;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.semanticweb.owlapi.io.RDFResource;

import com.hp.hpl.jena.ontology.OntClass;

import fr.ign.cogit.ontology.OntologieOWL;

/**
 * 
 *
 */
public class TestWuPalmer {
  
  @Test
  public void testWP1() {
    
    // On charge l'ontologie 'spatial relations'
    OntologieOWL ontologie = new OntologieOWL("Spatial relations", 
        TestWuPalmer.class.getClassLoader().getResource("spatialrelations2.owl").getPath());
    // ontologie.affiche();
    ontologie.close();
    System.out.println("----------------");
  }
  
  /*@Test
  public void testWP2() {
    
    // On charge l'ontologie 'spatial relations'
    OntologieOWL ontoTopo = new OntologieOWL("Topo", 
        TestWuPalmer.class.getClassLoader().getResource("topo.rdf").getPath());
    // ontoTopo.affiche();
    
    OntClass val1 = ontoTopo.getOWLModel().getOntClass("http://data.ign.fr/def/topo#ElementDuRelief");
    if (val1 != null) {
      Assert.assertEquals("Entité topographique correspondant à une rupture de pente artificielle, ou à un élément remarquable du relief nommé.", val1.getComment("fr"));
      Assert.assertEquals("ElementDuRelief", val1.getLocalName());
    }
    
    OntClass val2 = ontoTopo.getOWLModel().getOntClass("http://data.ign.fr/def/topo#ZoneDeVegetation");
    if (val2 != null) {
      Assert.assertEquals("Espace végétal naturel ou non différencié selon le couvert forestier.", val2.getComment("fr"));
      Assert.assertEquals("ZoneDeVegetation", val2.getLocalName());
    }

    OntClass c = ontoTopo.getPPPC(val1, val2);
    
    // Close
    ontoTopo.close();
    System.out.println("----------------");
  }*/
  
  @Test
  public void testWP3() {
    
    // On charge l'ontologie 'spatial relations'
    OntologieOWL ontoTopo = new OntologieOWL("TopoCarto", 
        TestWuPalmer.class.getClassLoader().getResource("FusionTopoCarto2.owl").getPath());
    // ontoTopo.affiche();
    
    //RDFResource val1 = (RDFResource) ontoTopo.getOWLModel().getOntClass("http://www.owl-ontologies.com/Ontology1176999717.owl#parc_à_huîtres");
    OntClass val2 = ontoTopo.getOWLModel().getOntClass("http://www.owl-ontologies.com/Ontology1176999717.owl#hôtel_de_préfecture");
    
    // Calcul des similarité sémantiques
    MesureSimilariteSemantique mesureSim = new WuPalmerSemanticSimilarity(ontoTopo);
    //double scoreSimilariteSemantique = mesureSim.calcule(val1, val2);
    //System.out.println("Score de similarité sémantique = " + scoreSimilariteSemantique);
    
    System.out.println("----------------");
  
  }

}

