package fr.ign.cogit.ontology.similarite;

import java.util.List;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntClass;

import fr.ign.cogit.ontology.OntologieOWL;

/**
 * 
 *
 */
public class TestWuPalmer {
  
  /*@Test
  public void testWP1() {
    
    // On charge l'ontologie 'spatial relations'
    OntologieOWL1 ontologie = new OntologieOWL1("Spatial relations", 
        TestWuPalmer.class.getClassLoader().getResource("spatialrelations2.owl").getPath());
    // ontologie.affiche();
    ontologie.close();
    System.out.println("----------------");
  }*/
  
  @Test
  public void testWP2() {
    
    // On charge l'ontologie 'spatial relations'
    OntologieOWL ontoTopo = new OntologieOWL("Topo", 
        TestWuPalmer.class.getClassLoader().getResource("topo.rdf").getPath());
    // ontoTopo.affiche();
    
    // Calcul des similarité sémantiques
    MesureSimilariteSemantique mesureSim = new WuPalmerSemanticSimilarity(ontoTopo);
    
    OntClass val1 = ontoTopo.getOWLModel().getOntClass("caserne de pompiers");
    OntClass val2 = ontoTopo.getOWLModel().getOntClass("Parking");
    //System.out.println(val1.getComment("fr"));
    
    // double scoreSimilariteSemantique = mesureSim.calcule(val1, val2);
    // System.out.println("Score de similarité sémantique = " + scoreSimilariteSemantique);
    
    // Close
    ontoTopo.close();
    System.out.println("----------------");
  }

}

