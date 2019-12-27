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

import edu.stanford.smi.protegex.owl.model.RDFResource;
import fr.ign.cogit.ontology.OntologieOWL;

/**
 * 
 * @author Nathalie Abadie
 */
public abstract class MesureSimilariteSemantique implements MesureSimilarite {
	
	private OntologieOWL onto;
	
	public OntologieOWL getOnto() {
		return onto;
	}

	public void setOnto(OntologieOWL onto) {
		this.onto = onto;
	}

	public MesureSimilariteSemantique(OntologieOWL onto) {
		super();
		this.onto = onto;
	}

	/**
	 * Renvoie la mesure de similarité sémantique entre les deux ressources RDF passées en paramètre.
	 * 
	 * @param la 
	 *            ressource 1
	 * @param la
	 *            ressource 2
	 * 
	 */
	public double calcule(RDFResource o1, RDFResource o2) {
		return this.calculeSimilariteConceptsOntologieUnique(o1, o2);
	}
	
	
	public abstract double calculeSimilariteConceptsOntologieUnique(RDFResource c1, RDFResource c2);
}
