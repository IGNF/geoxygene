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
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Iterator;

import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

/**
 * 
 * @author Nathalie Abadie
 */
public class CalculSimilariteSemantique {

  /**
   * Détermine le plus petit parent commun de deux classes
   * @param owlModel
   * @param c1
   * @param c2
   * @return le plus petit parent commun des deux classes en entrée
   */
  @SuppressWarnings("unchecked")
  public static OWLNamedClass getPPPC(OWLModel owlModel, OWLNamedClass c1,
      OWLNamedClass c2) {

    /*
     * Initialisation des variables: on considère que le plus parent commun est
     * la racine de l'arbre, et que la distance maximale de ce PPPC à la racine
     * vaut 0
     */
    OWLNamedClass pppc = (OWLNamedClass) owlModel.getOWLThingClass();
    int distMax = 0;

    // Récupère la collection des superclasses directes de ma classe
    Collection<OWLNamedClass> superClassesC1 = c1.getSuperclasses(true);

    // Récupère la collection des superclasses de la classe à comparer
    Collection<OWLNamedClass> superClassesC2 = c2.getSuperclasses(true);

    // Testons les cas où nos deux classes sont confondues ou parents directs
    if (superClassesC1.contains(c2)) {
      if (superClassesC2.size() > distMax) {
        pppc = c2;
        distMax = superClassesC2.size();
        return pppc;
      } else {
      }
    } else {
    }

    if (superClassesC2.contains(c1)) {
      if (superClassesC1.size() > distMax) {
        pppc = c1;
        distMax = superClassesC1.size();
        return pppc;
      } else {
      }
    } else {
    }

    if (c1.equals(c2)) {
      pppc = c1;
      distMax = superClassesC1.size();
      return pppc;
    } else {
    }

    /*
     * Nos classes ne sont pas des parents directs et ne sont pas confondues:
     * Cherchons quel est leur plus petit parent commun!
     */

    Iterator<OWLNamedClass> it = superClassesC1.iterator();
    while (it.hasNext()) {
      OWLNamedClass superC = it.next();
      // Si cette classe est commune aux deux listes...
      if (superClassesC2.contains(superC)) {
        // ...et si sa distance à la racine est supérieure à distMax
        if (superC.getSuperclasses(true).size() > distMax) {
          // alors c'est le pppc des deux classes
          pppc = superC;
          distMax = superC.getSuperclasses(true).size();
        } else {// Sinon ce n'est pas le pppc des deux classes
          continue;
        }
      }
      // sinon, la classe ne peut être une classe parente
      else {
        continue;
      }
    }
    return pppc;
  }// Fin getDistancePPPC

  /**
   * Calcul de la similarite, de la distance et de la distance cubique (sic!)
   * entre deux classes
   * @param owlModel
   * @param c1
   * @param c2
   */
  public static void similarite(OWLModel owlModel, OWLNamedClass c1,
      OWLNamedClass c2) {

    /* Initialisatuion des variables */
    double sim = 0;
    double dist = 1;
    // double distCube = 1;

    // Calcul du plus petit parent commun à nos deux classes
    OWLNamedClass pppc = CalculSimilariteSemantique.getPPPC(owlModel, c1, c2);

    // Calcul des la distance du PPPC et des classes à la racine
    double profC = pppc.getSuperclasses(true).size();
    double profC1 = c1.getSuperclasses(true).size();
    double profC2 = c2.getSuperclasses(true).size();

    // Contrainte de calcul: on distingue les entités terrestres et marines!
    if (pppc.getLocalName().equals("entites_topographiques_naturelles")) {
      sim = 0;
      dist = 1;
      // distCube = 1;
      // System.out.println(c1.getLocalName() + "-" + c2.getLocalName()
      //    + ": sim =" + sim + " dist =" + dist + " dist cube =" + distCube);
      System.out.println(c1.getLocalName() + "-" + c2.getLocalName()
          + ": dist = " + dist + " ppc = " + pppc.getLocalName());
      // System.out.println("Similarite ("+c1.getLocalName()+"-"+c2.getLocalName()+") ="+sim);
      // System.out.println("Distance ("+c1.getLocalName()+"-"+c2.getLocalName()+") ="+dist);
      // System.out.println("Distance au cube ("+c1.getLocalName()+"-"+c2.getLocalName()+") ="+distCube);
      return;
    } else {
      sim = ((2 * profC) / (profC1 + profC2));
      dist = 1 - sim;
      // distCube = Math.pow(dist, 3);
      System.out.println(c1.getLocalName() + "-" + c2.getLocalName()
          + ": dist = " + dist + " ppc = " + pppc.getLocalName() + " profC = " + profC);
      // System.out.println("Similarite ("+c1.getLocalName()+"-"+c2.getLocalName()+") ="+sim);
      // System.out.println("Distance ("+c1.getLocalName()+"-"+c2.getLocalName()+") ="+dist);
      // System.out.println("Distance au cube ("+c1.getLocalName()+"-"+c2.getLocalName()+") ="+distCube);
      return;
    }
  }

  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    
    // Notre ontologie au format owl
    String uri = CalculSimilariteSemantique.class.getClassLoader().getResource("FusionTopoCartoExtract.owl").getPath();

    // On crée un modele à partir de notre ontologie
    OWLModel owlModel = ProtegeOWL.createJenaOWLModel();
    try {
      owlModel = ProtegeOWL
          .createJenaOWLModelFromInputStream(new FileInputStream(new File(uri)));
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
        if (
            (c2.getLocalName().equals("montagne") || c2.getLocalName().equals("sommet") || c2.getLocalName().equals("pic") || c2.getLocalName().equals("escarpement"))
            && (c1.getLocalName().equals("montagne") || c1.getLocalName().equals("sommet") || c1.getLocalName().equals("pic") || c1.getLocalName().equals("escarpement"))) {
          CalculSimilariteSemantique.similarite(owlModel, c1, c2);
        }
      }
    }

  }// Fin du main

}// Fin de ma classe
