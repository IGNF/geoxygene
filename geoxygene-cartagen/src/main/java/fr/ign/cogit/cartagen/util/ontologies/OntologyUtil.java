/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.util.ontologies;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Functions useful to handle OWL ontologies within CartAGen through the OWL2.0
 * API.
 * @author GTouya
 * 
 */
public class OntologyUtil {

  private static final String FOLDER_PATH = "/ontologies";

  /**
   * Open the OWL ontology (from the ontology resources of CartAGen) whose name
   * is passed as parameter.
   * @param name
   * @return
   * @throws OWLOntologyCreationException
   */
  public static OWLOntology getOntologyFromName(String name)
      throws OWLOntologyCreationException {
    // create the URI from the name and the CartAGen ontologies folder path
    String uri = FOLDER_PATH + "/" + name + ".owl";
    InputStream stream = OntologyUtil.class.getResourceAsStream(uri);
    File file = new File(stream.toString());
    String path = file.getAbsolutePath().substring(0,
        file.getAbsolutePath().lastIndexOf('\\'));
    path = path.replaceAll(new String("\\\\"), new String("//"));
    path = path + "//src/main//resources//ontologies//" + name + ".owl";
    // create the ontology from the URI using an OWLOntologyManager
    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    IRI physicalURI = IRI.create(new File(path));
    OWLOntology ontology = manager
        .loadOntologyFromOntologyDocument(physicalURI);

    return ontology;
  }

  /**
   * Get the {@link OWLClass} object corresponding to a named concept of the
   * ontology.
   * @param onto
   * @param name
   * @return
   */
  public static OWLClass getOntoNamedClass(OWLOntology onto, String name) {
    for (OWLClass c : onto.getClassesInSignature())
      if (c.getIRI().getFragment().equals(name))
        return c;
    return null;
  }

  /**
   * Get the {@link OWLDataProperty} object corresponding to a named data
   * property (i.e. attribute in OO) of the ontology.
   * @param onto
   * @param name
   * @return
   */
  public static OWLDataProperty getOntoDataProperty(OWLOntology onto,
      String name) {
    for (OWLDataProperty p : onto.getDataPropertiesInSignature())
      if (p.getIRI().getFragment().equals(name))
        return p;
    return null;
  }

  /**
   * Get the {@link OWLObjectProperty} object corresponding to a named object
   * property (i.e. relation in OO) of the ontology.
   * @param onto
   * @param name
   * @return
   */
  public static OWLObjectProperty getOntoObjectProperty(OWLOntology onto,
      String name) {
    for (OWLObjectProperty p : onto.getObjectPropertiesInSignature())
      if (p.getIRI().getFragment().equals(name))
        return p;
    return null;
  }

  /**
   * Get all the super classes of a named concept in the ontology (not only the
   * direct super classes).
   * @param concept
   * @param onto
   * @return
   */
  public static Set<OWLClass> getAllSuperClasses(String concept,
      OWLOntology onto) {
    OWLClass classe = getOntoNamedClass(onto, concept);
    Set<OWLClass> set = new HashSet<OWLClass>();
    Stack<OWLClass> pile = new Stack<OWLClass>();
    for (OWLClassExpression superC : classe.getSuperClasses(onto))
      if (OWLClass.class.isInstance(superC))
        pile.addElement((OWLClass) superC);
    while (!pile.empty()) {
      OWLClass c = pile.pop();
      set.add(c);
      for (OWLClassExpression superC : c.getSuperClasses(onto))
        if (OWLClass.class.isInstance(superC))
          if (!set.contains(superC))
            pile.addElement((OWLClass) superC);
    }
    return set;
  }

  /**
   * Determines if, in the given ontology, the childConcept "is a"
   * parentConcept. The String used must correspond to the OWL ontology concepts
   * names.
   * @param ontology
   * @param childConcept
   * @param parentConcept
   * @return
   */
  public static boolean isA(OWLOntology ontology, String childConcept,
      String parentConcept) {
    OWLClass child = getOntoNamedClass(ontology, childConcept);
    OWLClass parent = getOntoNamedClass(ontology, parentConcept);

    Set<OWLClass> set = new HashSet<OWLClass>();
    Stack<OWLClass> pile = new Stack<OWLClass>();
    for (OWLClassExpression superC : child.getSuperClasses(ontology))
      if (OWLClass.class.isInstance(superC))
        pile.addElement((OWLClass) superC);
    while (!pile.empty()) {
      OWLClass c = pile.pop();
      if (c.equals(parent))
        return true;
      set.add(c);
      for (OWLClassExpression superC : c.getSuperClasses(ontology))
        if (OWLClass.class.isInstance(superC))
          if (!set.contains(superC))
            pile.addElement((OWLClass) superC);
    }
    return false;
  }
}
