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

import org.semanticweb.owlapi.io.RDFResource;



/**
 * 
 * 
 * @author Nathalie Abadie
 */
public interface MesureSimilarite {
	
	/** 
	 * Calcule le score de similarite de deux objets pour cette mesure la.
     */
  public double calcule(RDFResource o1, RDFResource o2);

}
