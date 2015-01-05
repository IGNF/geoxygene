/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.ontology.similarite;

import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import fr.ign.cogit.ontology.OntologieOWL;

/**
 * 
 *
 */
public class TestWuPalmer {
  
  @Test
  public void testSommetMontagnePicEscarpement() {
    
    OntologieOWL ontoTopoCarto = null;
    
    try {
      // On charge l'ontologie 'FusionTopoCartoExtract'
      URL u = new URL("file", "", "./data/FusionTopoCarto2.owl");
      ontoTopoCarto = new OntologieOWL("FusionTopoCartoExtract", u.getFile());
      // ontoTopoCarto.affiche();
    } catch(Exception e) {
      fail();
    }
        
    RDFResource rSommet = ontoTopoCarto.getOWLModel().getRDFResource("sommet");
    Assert.assertEquals("", "sommet", rSommet.getLocalName());
    
    RDFResource rMontagne = ontoTopoCarto.getOWLModel().getRDFResource("montagne");
    Assert.assertEquals("", "montagne", rMontagne.getLocalName());
    
    RDFResource rEscarpement = ontoTopoCarto.getOWLModel().getRDFResource("escarpement");
    Assert.assertEquals("", "escarpement", rEscarpement.getLocalName());
    
    RDFResource rPic = ontoTopoCarto.getOWLModel().getRDFResource("pic");
    Assert.assertEquals("", "pic", rPic.getLocalName());
    
    // PPPC
    OWLNamedClass pppc = ontoTopoCarto.getPPPC((OWLNamedClass)rSommet, (OWLNamedClass)rMontagne);
    Assert.assertEquals("Plus petit parent commun. ", "sommet", pppc.getLocalName());

    // Distance à la racine
    Assert.assertEquals("distance(pppc, thing)" , 3, pppc.getSuperclasses(true).size());
    Assert.assertEquals("distance(pppc, sommet)" , 3, ((OWLNamedClass)rSommet).getSuperclasses(true).size());
    Assert.assertEquals("distance(pppc, montagne)" , 4, ((OWLNamedClass)rMontagne).getSuperclasses(true).size());
    Assert.assertEquals("distance(pppc, escarpement)" , 3, ((OWLNamedClass)rEscarpement).getSuperclasses(true).size());
    Assert.assertEquals("distance(pppc, pic)." , 4, ((OWLNamedClass)rPic).getSuperclasses(true).size());
    
    // Calcul des similarités sémantiques
    MesureSimilariteSemantique mesureSim = new WuPalmerSemanticSimilarity(ontoTopoCarto);
    
    // SOMMET-MONTAGNE
    double scoreSimilariteSemantique = 1 - mesureSim.calcule(rSommet, rMontagne);
    Assert.assertEquals("d(sommet,montagne)", 0.143, scoreSimilariteSemantique, 0.001);
    
    // PIC-ESCARPEMENT
    scoreSimilariteSemantique = 1 - mesureSim.calcule(rPic, rEscarpement);
    Assert.assertEquals("d(pic,escarpement)", 0.429, scoreSimilariteSemantique, 0.001);
  }
  
}

