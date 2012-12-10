package fr.ign.cogit.geoxygene.semio.legend.legendContent;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

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
 * @author Elodie Buard - IGN / Laboratoire COGIT
 * @author Sébastien Mustière - IGN / Laboratoire COGIT
 * @author Charlotte Hoarau - IGN / Laboratoire COGIT
 * 
 * An element of the <code>Legend</code> composed of other elements.
 * It can be composed by <code>LegendLeaf</code> or <code>LegendComposite</code>
 * (themselves composed by <code>LegendLeaf</code> or <code>LegendComposite</code> etc.)
 * <p>
 * <strong>French:</strong><br />
 * Un thème d'une légende de carte. 
 * Il est composé de feuilles directement et/ou de sous-thèmes, 
 * eux mêmes composés de feuilles et/ou sous-thèmes, etc. 
 * <p>
 *
 * @see Legend
 * @see LegendComponent
 * @see LegendLeaf
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LegendComposite",
		propOrder = {
			"components"
		})
public class LegendComposite extends LegendComponent {

	/**
	 * List of the <code>LegendComponent</code>s contained by this.
	 */
	@XmlElement(name = "Component")
	private List<LegendComponent> components = 
	  new ArrayList<LegendComponent>();
	
	/**
	 * Returns the list of the <code>LegendComponent</code>s contained by this.
	 * @return the list of the <code>LegendComponent</code>s contained by this.
	 */ 
	@Override
  public List<LegendComponent> getComponents() {
	  return this.components;
	} 
	
	/**
	 * Sets a list of the <code>LegendComponent</code>s contained by this.
	 * @param components <code>LegendComponent</code>s contained by this.
	 */ 
	public void setComponents (List<LegendComponent> components) {
		List<LegendComponent> old = 
		  new ArrayList<LegendComponent>(this.components);
		for (LegendComponent O : old) {
		  O.setContainer(null);
		}
		for (LegendComponent O : components) {
		  O.setContainer(this);
		}
	}
	
	/**
	 * Adds a <code>LegendComponent</code> to the List of 
	 * <code>LegendComponent</code>s contained by this.
	 * @param component <code>LegendComponent</code> contained by this.
	 */ 
	public void addComponent (LegendComponent component) {
		if ( component == null ) {
		  return;
		}
		this.components.add(component);
		component.setContainer(this) ;
	}
	
}