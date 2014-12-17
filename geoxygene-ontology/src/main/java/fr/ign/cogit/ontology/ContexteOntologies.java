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

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Nathalie Abadie
 */
public class ContexteOntologies {
	
  /** Instance unique de la classe. */
  private static ContexteOntologies contexteOntologies;
	
	
  /** 
   * Méthode qui renvoie notre instance de classe "ContexteSchemasSingleton" 
   */
  public static synchronized ContexteOntologies getContexteOntologiesSingleton() {
    if (contexteOntologies == null) {
      contexteOntologies = new ContexteOntologies();
    }
	return contexteOntologies;
  }
	
  /**
   * Méthode qui empêche le clonage de cette classe
   */
  @Override
  public Object clone()throws CloneNotSupportedException {
    throw new CloneNotSupportedException(); 
  }
	
  /**
   * Constructeur privé.
   */
  private ContexteOntologies() {
    this.ontologiesDisponibles = new ArrayList<OntologieOWL>();
	this.ontologiesDisponibles.clear();
  }
	
  /** 
   * Liste des ontologies disponibles 
   */
  private List<OntologieOWL> ontologiesDisponibles;
	
  public void setOntologiesDisponibles(List<OntologieOWL> o) { 
    this.ontologiesDisponibles=o;
  }
	
  public void addOntologieDisponible(OntologieOWL o) { 
    this.ontologiesDisponibles.add(o);
  }
	
  public List<OntologieOWL> getOntologiesDisponibles(){
    return this.ontologiesDisponibles;
  }
	
  public OntologieOWL getOntologieByName(String nom) {
	OntologieOWL onto = null;
	for (OntologieOWL o : this.ontologiesDisponibles) {
	  if (o.getNom().equalsIgnoreCase(nom)){
	    onto = o;
	    return onto;
	  } else {
	    continue;
	  }
	}
	return onto;
  }	

}
