package fr.ign.cogit.ontology.similarite;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Iterator;

import org.junit.Test;

import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import fr.ign.cogit.ontology.OntologieOWL;

public class TestCalculSimilariteSemantiqueSpecial {
  
  @SuppressWarnings("unchecked")
  @Test
  public void testSommetMontagnePicEscarpement() {
    
    URL u = null;

    // Notre ontologie au format owl
    try {
      u = new URL("file", "", "./data/FusionTopoCarto2.owl");
    } catch(Exception e) {
      fail();
    }

    // On crée un modele à partir de notre ontologie
    OWLModel owlModel = ProtegeOWL.createJenaOWLModel();
    try {
      owlModel = ProtegeOWL
          .createJenaOWLModelFromInputStream(new FileInputStream(new File(u.getFile())));
    } catch (Exception ex) {
    }

    // On lit la liste des classes utilisateur (pas les classes systeme!) de
    // notre ontologie
    Iterator<OWLNamedClass> itc1 = owlModel.getUserDefinedOWLNamedClasses()
        .iterator();

    while (itc1.hasNext()) {
      OWLNamedClass c1 = itc1.next();
      // Pour chaque classe on calcule les similarités, distances, etc...aux
      // autres classes
      Iterator<OWLNamedClass> itc2 = owlModel.getUserDefinedOWLNamedClasses()
          .iterator();
      while (itc2.hasNext()) {
        OWLNamedClass c2 = itc2.next();
        
        if (c1.getLocalName().equals("montagne") && c2.getLocalName().equals("sommet")) {
          CalculSimilariteSemantique.similarite(owlModel, c1, c2);
        }
        if (c1.getLocalName().equals("pic") && c2.getLocalName().equals("escarpement")) {
          CalculSimilariteSemantique.similarite(owlModel, c1, c2);
        }
        
      }
    }

  }// Fin du main

}
