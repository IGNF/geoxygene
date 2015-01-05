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

import java.io.File;

import org.apache.log4j.Logger;

import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import fr.ign.cogit.ontology.OntologieOWL;

/**
 * 
 * @author Nathalie Abadie
 */
public class WuPalmerSemanticSimilarity extends MesureSimilariteSemantique {

  /** Logger. */
  private final static Logger LOGGER = Logger.getLogger(WuPalmerSemanticSimilarity.class);

  public WuPalmerSemanticSimilarity(OntologieOWL onto) {
    super(onto);
  }

  @Override
  public double calcule(RDFResource o1, RDFResource o2) {
    return this.calculeSimilariteConceptsOntologieUnique(o1, o2);
  }

  @Override
  public double calculeSimilariteConceptsOntologieUnique(RDFResource c1, RDFResource c2) {
		
    /* Initialisation des variables */
	double sim = 0;
	OWLNamedClass cls1 = (OWLNamedClass) c1;
	OWLNamedClass cls2 = (OWLNamedClass) c2;
	
	if (cls1 == null || cls2 == null) {
	  sim=0.0;
      LOGGER.info("Valeur = " + sim);
      return sim;
	}
	
	LOGGER.info("Similarite entre " + cls1.getLocalName() + " et " + cls2.getLocalName());
	
	if(c1.equals(c2)){
      sim=1.0;
      LOGGER.info("Valeur = " + sim);
      return sim;
    }
	
	// Calcul du plus petit parent commun à nos deux classes
	OWLNamedClass C = this.getOnto().getPPPC(cls1, cls2);
    LOGGER.info("Plus petit parent commun: " + C);
    
    // Calcul de la distance du PPPC et des classes à la racine
    OWLNamedClass thing = this.getOnto().getOWLModel().getOWLThingClass();
	if (C.equals(thing)) {
	  sim = 0.0;
	} else {
	  double profC = this.getOnto().getShortestPathLengthWithoutMatrix(C, thing);
	  LOGGER.info("ProfC = " + profC);
	  double profC1 = this.getOnto().getShortestPathViaXLengthWithoutMatrix(cls1, C, thing);
	  LOGGER.info("ProfC1 = " + profC1);
	  double profC2 = this.getOnto().getShortestPathViaXLengthWithoutMatrix(cls2, C, thing);
	  LOGGER.info("ProfC2 = " + profC2);

	  // Calcul
	  sim = ((2 * profC) / (profC1 + profC2));
	}

	LOGGER.info("Valeur = " + sim);
	return sim;
  }
  
  
  public static double getEvaluation(String filepath, String s, String t) {
    OntologieOWL ontoTopoCarto = null;
    try {
      // On charge l'ontologie 
      LOGGER.info("On charge l'ontologie.");
      File file = new File(WuPalmerSemanticSimilarity.class.getClassLoader().getResource(filepath).getFile());
      LOGGER.info(file.getAbsolutePath());
      ontoTopoCarto = new OntologieOWL("Onto", file.getPath());
      // ontoTopoCarto.affiche();
    } catch(Exception e) {
      e.printStackTrace();
    }
    
    LOGGER.trace("s = " + s);
    LOGGER.trace("t = " + t);
    
    RDFResource rS = ontoTopoCarto.getOWLModel().getRDFResource(s.toLowerCase());
    RDFResource rT = ontoTopoCarto.getOWLModel().getRDFResource(t.toLowerCase());
    
    MesureSimilariteSemantique mesureSim = new WuPalmerSemanticSimilarity(ontoTopoCarto);
    
    // SOMMET-MONTAGNE
    double scoreSimilariteSemantique = 1 - mesureSim.calcule(rS, rT);
    LOGGER.debug("scoreSimilariteSemantique = " + scoreSimilariteSemantique);
    return scoreSimilariteSemantique;
  }

}
