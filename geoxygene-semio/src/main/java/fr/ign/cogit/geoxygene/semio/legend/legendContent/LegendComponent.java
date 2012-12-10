package fr.ign.cogit.geoxygene.semio.legend.legendContent;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import fr.ign.cogit.geoxygene.semio.legend.metadata.SemanticRelation;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see Licence_CeCILL-C_fr.html
 *        see Licence_CeCILL-C_en.html
 * 
 *        see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 * @author Sébastien Mustière - IGN / Laboratoire COGIT
 * @author Charlotte Hoarau - IGN / Laboratoire COGIT
 * 
 * One component of the <code>Legend</code>, it may be either a 
 * <code>LegendLeaf</code> of the <code>Legend</code> tree, 
 * or a non-leaf node of the <code>Legend</code> tree representing
 *  a <code>LegendComposite</code> of other components.
 * <p>
 * LegendComponents have {@link SemanticRelation}ships 
 * (order, association, dissociation) between them.
 * <p>
 * <strong>French:</strong><br />
 * Un élément de légende, qui est soit une <code>LegendLeaf</code>
 *  (une ligne de la légende), soit un <code>LegendComposite</code>,
 *   un thème (lui même regroupant des feuilles et/ou des sous-thèmes).
 * <p>
 * Il est qualifié en particulier par des {@link SemanticRelation}
 * (relations sémantique d'ordre, d'association ou de différenciation)
 * avec les autres éléments de la légende.
 * <p>
 * 
 * @see Legend
 * @see LegendComposite
 * @see LegendLeaf
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LegendComponent",
		propOrder = {
			"name",
			"components",
			"container"
		})
public class LegendComponent {

	/**
	 * The name of the component, as shown in the key
	 */
	@XmlElement(name = "Name")
	private String name;
	
	/**
	 * Returns the name of the component, as shown in the key
	 * @return name of the component, as shown in the key
	 */
	public String getName() {
	  return this.name;
	}
	
	/**
	 * Sets the name of the component, as shown in the key
	 * @param name name of the component, as shown in the key
	 */
	public void setName (String name) {
	  this.name = name;
	}

	/**
	 * List of the <code>LegendComponent</code>s contained by this.
	 */
	@XmlElement(name = "Component")
	private List<LegendComponent> components = new ArrayList<LegendComponent>();
	
	/**
	 * Returns the list of the <code>LegendComponent</code>s contained by this.
	 * @return the list of the <code>LegendComponent</code>s contained by this.
	 */ 
	public List<LegendComponent> getComponents() {
	  return this.components;
	} 
	
	/**
	 * Element containing this (null if this is the root of the tree)
	 */
	@XmlElement(name = "container")
	private LegendComposite container;
	
	/**
	 * Returns the element containing this (null if this is the root of the tree)
	 * @return The element containing this (null if this is the root of the tree)
	 */
	public LegendComposite  getContainer() {
	  return this.container;
	}
	
	/**
	 * Sets the element containing this (null if this is the root of the tree)
	 * @param container Element containing this (null if this is the root of the tree)
	 */
	public void setContainer(LegendComposite container) {
		LegendComposite old = this.container;
		this.container= container;
		if ( old != null ) {
		  old.getComponents().remove(this);
		}
		if ( container != null) {
			if ( ! container.getComponents().contains(this) ) {
			  container.getComponents().add(this);
			}
		}
	}
	
	/**
	 * {@link SemanticRelation}s (between legend elements) concerning this.
	 */
	@XmlTransient
	private List<SemanticRelation> relations = new ArrayList<SemanticRelation>();
	
	/**
	 * Returns {@link SemanticRelation}s (between legend elements) concerning this.
	 * @return {@link SemanticRelation}s (between legend elements) concerning this. 
	 */
	public List<SemanticRelation> getRelations() {
	  return this.relations;
	}
	
	/**
	 * Sets {@link SemanticRelation}s (between legend elements) concerning this.
	 * @param relations {@link SemanticRelation}s (between legend elements) concerning this.
	 */
	public void setRelations(List<SemanticRelation> relations) {
		List <SemanticRelation>old = new ArrayList<SemanticRelation>(this.relations);
		for (SemanticRelation obj : old) {
			relations.remove(obj);
			obj.getRelatedComponents().remove(this);
		}
		for (SemanticRelation obj : relations) {
			this.relations.add(obj);
			obj.getRelatedComponents().add(this);
		}
	}
	
	/**
	 * Adds a relation to the {@link SemanticRelation}s 
	 * (between legend elements) concerning this.
	 * @param relation {@link SemanticRelation}s (between legend elements) concerning this.
	 */
	public void addRelation(SemanticRelation relation) {
		if ( relation == null ) return;
		this.relations.add(relation);
		relation.getRelatedComponents().add(this);
	}
	
	/**
	 * Removes a relation from the {@link SemanticRelation}s 
	 * (between legend elements) concerning this.
	 * @param relation {@link SemanticRelation}s (between legend elements) concerning this.
	 */
	public void removeRelation(SemanticRelation relation) {
		if ( relation == null ) return;
		this.relations.remove(relation) ;
		relation.getRelatedComponents().remove(this);
	}
	
	/**
	 * Returns the leaves directly contained by this (not through an intermediate component)
	 * @return The leaves directly contained by this (not through an intermediate component)
	 */
	public List<LegendLeaf> directLeaves() {
		List<LegendLeaf> feuilles = new ArrayList<LegendLeaf>();
		for (LegendComponent element : this.getComponents()) {
			if (element instanceof LegendLeaf) {
			  feuilles.add((LegendLeaf)element);
			}
		}
		return feuilles;
	}
	
	/**
	 * Returns the leaves contained by this (directly or not)
	 * @return The leaves contained by this (directly or not)
	 */
	public List<LegendLeaf> allLeaves() {
		List<LegendLeaf> leaves = new ArrayList<LegendLeaf>();
		for (LegendComponent element : this.getComponents()) {
			if (element instanceof LegendLeaf) {
			  leaves.add((LegendLeaf)element);
			}
			if (element instanceof LegendComposite) {
			  leaves.addAll(((LegendComposite)element).allLeaves());
			}
		}
		return leaves;
	}
	
	/**
	 * Returns the themes directly contained by this (not through an intermediate component)
	 * @return The themes directly contained by this (not through an intermediate component)
	 */
	public List<LegendComposite> directThemes() {
		List<LegendComposite> themes = new ArrayList<LegendComposite>();
		for (LegendComponent element : this.getComponents()) {
			if (element instanceof LegendComposite) {
			  themes.add((LegendComposite)element);
			}
		}
		return themes;
	}
	
	/**
	 * Returns the themes contained by this (directly or not)
	 * @return The themes contained by this (directly or not)
	 */
	public List<LegendComposite> allThemes() {
		List<LegendComposite> themes = new ArrayList<LegendComposite>();
		for (LegendComponent element : this.getComponents()) {
			if (element instanceof LegendComposite) {
				themes.addAll(((LegendComposite)element).allThemes());
				themes.add((LegendComposite)element);
			}
		}
		return themes;
	}
	
}