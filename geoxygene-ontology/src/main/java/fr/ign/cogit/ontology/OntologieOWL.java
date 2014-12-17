/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.ontology;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;

import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import Jama.Matrix;

/**
 * 
 * @author Nathalie Abadie
 */
public final class OntologieOWL {
  
  private static final Logger LOGGER = Logger.getLogger(OntologieOWL.class);
  
  /** 
   * Nom de l'ontologie.
   * En memoire pour la differencier des autres.
   * Ex: "ontologie cible"
   */
  private String nom;
  
  /** Modele OWL */
  private OntModel owlmodel;
  
  /** Stockage des plus courts chemins entre une classe et thing. */
  private HashMap<OWLClass, Integer> pcc;

  /**
   * Default constructor.
   */
  public OntologieOWL() {
    this.owlmodel = ModelFactory.createOntologyModel();
    this.pcc = new HashMap<OWLClass, Integer>();
  }
  
  /**
   * Constructor.
   * @param nom
   * @param uri
   */
  public OntologieOWL(String nom, String uri) {
    this.nom = nom;
    this.pcc = new HashMap<OWLClass, Integer>();
    this.loadOntologie(uri);
  }
  
  /**
   * Constructor.
   * @param nom
   */
  public OntologieOWL(String nom) {
    this();
    this.nom = nom;
  }
  
  public String getNom() {
    return this.nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }
  
  /** Renvoie le modele de cette ontologie */
  public OntModel getOWLModel() {
    return this.owlmodel;
  }

  /** Affecte un modele owl a cette ontologie */
  public void setOWLModel(OntModel model) {
    this.owlmodel = model;
  }
  
  public HashMap<OWLClass, Integer> getPcc() {
    return pcc;
  }

  public void setPcc(HashMap<OWLClass, Integer> pcc) {
    this.pcc = pcc;
  }


  /**
   * Charge une ontologie à l'aide de son chemin d'accès
   * @param uri
   */
  public void loadOntologie(String uri) {
    
    owlmodel = ModelFactory.createOntologyModel();
    
    // On charge l'ontologie a partir du fichier OWL
    try {
      InputStream in = FileManager.get().open(uri);
      if (in == null) {
        throw new IllegalArgumentException("File: " + uri + " not found");
      }
      owlmodel.read(in, "");
      // owlmodel.close();
      in.close();
      
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    // On la met dans la liste des ontologies disponibles
    ContexteOntologies oContexte = ContexteOntologies.getContexteOntologiesSingleton();
    oContexte.addOntologieDisponible(this);

  }
  
  /**
   * 
   */
  public void close() {
    if (owlmodel != null) {
      owlmodel.close();
    }
  }


  /**
   * Affiche le contenu d'une ontologie dans la console
   * @param uri
   */
  public void affiche() {
    
    // On lit la liste des classes utilisateur (pas les classes systeme!) de notre ontologie
    LOGGER.info("Classes");
    List<OntClass> listClass = owlmodel.listClasses().toList();
    for (int i = 0; i < listClass.size(); i++) {
      OntClass essaClasse = listClass.get(i);
      if (essaClasse.getLocalName() != null) {
        String vClasse = essaClasse.getLocalName().toString();
        LOGGER.info("  " + vClasse);
      }
    }
    
    LOGGER.info("Properties");
    List<OntProperty> listObProperty = owlmodel.listOntProperties().toList();
    for (int i = 0; i < listObProperty.size(); i++) {
      OntProperty obProperty = listObProperty.get(i);
      String vClasse = obProperty.getLocalName().toString();
      LOGGER.info("  " + vClasse);
    }
    
    LOGGER.info("Datatype Properties");
    List<DatatypeProperty> listDataType = owlmodel.listDatatypeProperties().toList();
    for (int i = 0; i < listDataType.size(); i++) {
      DatatypeProperty datatypeProperty = listDataType.get(i);
      String vClasse = datatypeProperty.getLocalName().toString();
      LOGGER.info("  " + vClasse);
    }

    LOGGER.info("Annotations");
    List<AnnotationProperty> listAnnotation = owlmodel.listAnnotationProperties().toList();
    for (int i = 0; i < listAnnotation.size(); i++) {
      AnnotationProperty annotationProperty = listAnnotation.get(i);
      String vClasse = annotationProperty.getLocalName().toString();
      LOGGER.info("  " + vClasse);
    }

  }


  /**
   * Détermine le plus petit parent commun de deux classes, i.e. la classe la
   * plus spécifique qui les subsume.
   * 
   * @param c1 la classe 1
   * @param c2 la classe 2
   * @return le plus petit parent commun des deux classes en entrée
   * 
   */
  public OntClass getPPPC(OntClass c1, OntClass c2) {
    
    LOGGER.info("Calcul du plus petit subsumant commun");
    
    /*
     * Initialisation des variables : 
     * on considère que le plus parent commun est la racine de l'arbre, 
     * et que la distance min de ce PPPC aux 2 classes vaut l'infini (=1000000);
     */
    List<OntClass> list = owlmodel.listHierarchyRootClasses().toList();
    for (int i = 0; i< list.size(); i++) {
      System.out.println(list.get(i).getLocalName());
    }
    // OWLClass thing = owlmodel.getOWLThingClass();
    // OWLClass pppc = thing;
    int distMin = 1000000;

    // Récupère la collection des superclasses à comparer pour C1
    // Collection<OWLNamedClass> superClassesC1 = c1.getNamedSuperclasses(true);

    /*if (!superClassesC1.contains(thing)) {
      superClassesC1.add(thing);
    }
    LOGGER.info("SuperClassesC1 OK");
    // Récupère la collection des superclasses à comparer pour C2
    Collection<OWLNamedClass> superClassesC2 = c2.getNamedSuperclasses(true);

    if (!superClassesC2.contains(thing)) {
      superClassesC2.add(thing);
    }
    LOGGER.info("SuperClassesC2 OK");*/

    // Testons les cas où nos deux classes sont confondues ou parents
    // directs l'une de l'autre

    /*if (c1.equals(c2)) {
      logger.info("C1 == C2");
      Integer distC1C2 = 0;
      pppc = c1;
      distMin = 0;
      return pppc;
    } else {
    }
    if (superClassesC1.contains(c2)) {
      logger.info("SuperClassesC1 contient C2");
      Integer distC2 = 10000000;
      logger.info("distC2 à calculer");
      distC2 = this.getShortestPathLengthWithoutMatrix(c2, c1);
      if (distC2 < distMin) {
        pppc = c2;
        distMin = distC2;
        return pppc;
      } else {
      }
    } else {
    }

    if (superClassesC2.contains(c1)) {
      logger.info("SuperClassesC2 contient C1");
      Integer distC1 = 10000000;
      logger.info("distC1 à calculer");
      distC1 = this.getShortestPathLengthWithoutMatrix(c1, c2);
      if (distC1 < distMin) {
        pppc = c1;
        distMin = distC1;
        return pppc;
      } else {
      }
    } else {
    }

    // Eliminons le cas où un des ensembles de parents est réduit à 1
    if ((superClassesC1.size() == 1) || (superClassesC2.size() == 1)) {
      return pppc;
    }

    *//*
     * Nos classes ne sont pas des parents directs et ne sont pas confondues:
     * Cherchons quel est leur plus petit parent commun!
     *//*
    Integer distanceToClasses = 1000000;
    Iterator<OWLNamedClass> it = superClassesC1.iterator();
    while (it.hasNext()) {
      OWLNamedClass superC = (OWLNamedClass) it.next();
      // Si cette classe est commune aux deux listes...
      if (superClassesC2.contains(superC)) {
        logger.info("PPPC potentiel: " + superC.getLocalName());
        // ...et si sa distance aux deux classes C1 et C2 est inférieure à
        // distMin

        logger.info("distanceToClasses à calculer");
        distanceToClasses = this.getShortestPathLengthWithoutMatrix(c1, superC)
            + this.getShortestPathLengthWithoutMatrix(c2, superC);

        // On teste la distance
        if (distanceToClasses < distMin) {
          // alors c'est le pppc des deux classes
          pppc = superC;
          distMin = distanceToClasses;
        } else {// Sinon ce n'est pas le pppc des deux classes
          continue;
        }
      }
      // sinon, la classe ne peut être une classe parente
      else {
        continue;
      }
    }
    logger.info("And the winner is..... " + pppc.getLocalName());*/
    // return pppc;
    return null;
  }// Fin getPPPC


  /**
   * Renvoie la longueur du plus court chemin entre deux classes de l'ontologie
   * passant par une classe donnée sans créer de matrice d'adjacence complète.
   * 
   * @param start la classe de départ
   * @param via la classe de passage imposé
   * @param end la classe d'arrivée
   * @return la distance entre start et end en nombre d'arcs (ie. relations isA)
   */
  public int getShortestPathViaXLengthWithoutMatrix(OntClass start,
      OntClass via, OntClass end) {
    LOGGER.info("Calcul de plus court chemin via X");
    int length = 0;
    length = this.getShortestPathLengthWithoutMatrix(start, via);
    LOGGER.info("Calcul de plus court chemin entre " + start.getLocalName()
        + " et " + via.getLocalName());
    length = length + this.getShortestPathLengthWithoutMatrix(via, end);
    LOGGER.info("Calcul de plus court chemin entre " + via.getLocalName()
        + " et " + end.getLocalName());
    return length;
  }

  /**
   * Renvoie la longueur du plus court chemin entre deux classes de l'ontologie
   * sans créer de matrice d'adjacence complète.
   * 
   * @param start la classe de départ
   * @param end la classe d'arrivée
   * @return la distance entre start et end en nombre d'arcs (ie. relations isA)
   */
  public int getShortestPathLengthWithoutMatrix(OntClass start, OntClass end) {
    
    int length = 0;

    // Cas où start et end sont confondues
    /*if (start.equals(end)) {
      length = 0;
      LOGGER.info("Longueur plus court chemin entre " + start.getLocalName()
          + " et " + end.getLocalName() + " = " + length);
      return length;
    }*/

    // stockage des couples classei-classej dont les valeurs dans la matrice
    // d'adjacence sont non nulles

    // Initialisation des adjacences: on met des 1 là où les classes ont une
    // relation de subsomption
    /*List<ValeurMatriceCreuse> adj = new ArrayList<ValeurMatriceCreuse>();

    List<OWLNamedClass> nodes = (List<OWLNamedClass>) this.getOWLModel()
        .getUserDefinedOWLNamedClasses();
    nodes.add(this.getOWLModel().getOWLThingClass());
    for (OWLNamedClass cls : nodes) {
      // On récupère la liste des voisines de cls
      List<OWLNamedClass> voisines = new ArrayList<OWLNamedClass>();
      List<OWLNamedClass> directClasses1 = new ArrayList<OWLNamedClass>();
      directClasses1.addAll(cls.getNamedSubclasses(false));
      directClasses1.addAll(cls.getNamedSuperclasses(false));
      for (RDFResource owlNamedClass : directClasses1) {
        if ((!owlNamedClass.isSystem()) && (!owlNamedClass.isAnonymous())) {
          if (!owlNamedClass.equals(cls)) {
            voisines.add((OWLNamedClass) owlNamedClass);
          }
        } else if (owlNamedClass == this.getOWLModel().getOWLThingClass()) {
          voisines.add((OWLNamedClass) owlNamedClass);
        } else {
          continue;
        }
      }

      for (OWLNamedClass v : voisines) {
        ValeurMatriceCreuse val = new ValeurMatriceCreuse(cls, v, 1);
        adj.add(val);
      }
    }*/

    // Initialisation du critère d'arrêt
    Integer valeurStop = 0;
    // Initialisation du vecteur startVector
    /*HashMap<OWLNamedClass, Integer> startVector = new HashMap<OWLNamedClass, Integer>();
    for (ValeurMatriceCreuse val : adj) {
      if (val.getLine() == start) {
        startVector.put((OWLNamedClass) val.getRow(), val.getValue());
      }
    }

    // On vérifie si les classes sont voisines directes
    if (startVector.get(end) != null) {
      valeurStop = startVector.get(end);
    }*/

    if (valeurStop == 1) {
      // les classes sont voisines directes: on sort
      /*length = 1;
      logger.info("Longueur plus court chemin entre " + start.getLocalName()
          + " et " + end.getLocalName() + " = " + length);*/
      return length;
    } else {
      // les classes ne sont pas voisines directes: on calcule la longueur
      // du plus court chemin
      /*int puissance = 1;
      while (valeurStop == 0) {
        // Calcul des puissances du vecteur startVector
        HashMap<OWLNamedClass, Integer> temp = new HashMap<OWLNamedClass, Integer>();
        Set<OWLNamedClass> colonnes = startVector.keySet();

        for (OWLNamedClass c : colonnes) {
          OWLNamedClass cL = start;
          OWLNamedClass c1 = c;
          for (ValeurMatriceCreuse val : adj) {
            OWLNamedClass c2 = (OWLNamedClass) val.getLine();
            OWLNamedClass cC = (OWLNamedClass) val.getRow();
            if (c1 == c2) {
              if (temp.keySet().size() != 0) {
                boolean found = false;
                for (OWLNamedClass t : temp.keySet()) {
                  if (t == cC) {
                    int old = temp.get(t);
                    temp.put(t, old + startVector.get(c1) * val.getValue());
                    found = true;
                    break;
                  } else {
                    continue;
                  }
                }
                if (!found) {
                  temp.put(cC, startVector.get(c1) * val.getValue());
                }
              } else {
                temp.put(cC, startVector.get(c1) * val.getValue());
              }
            }
          }
        }// On a notre nouveau vecteur
        startVector = temp;
        puissance++;
        if (startVector.get(end) != null) {
          valeurStop = startVector.get(end);
        }

      }// Fin de la boucle sur les claculs de puissance
      length = puissance;
      logger.info("Longueur plus court chemin entre " + start.getLocalName()
          + " et " + end.getLocalName() + " = " + length);*/
      return length;
    }// Fin du else
  
  }// Fin de methode

}
