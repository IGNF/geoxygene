/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialrelation.api;

/**
 * A {@link RelationProperty} is a property that characterises a spatial
 * relation. It can be quite simple like a distance, or a more complex object
 * like a convergence point for the "network follow network" relation of (Touya
 * et al. 2012).
 * 
 * @author GTouya
 * 
 */
public interface RelationProperty {

	/**
	 * Returns the value of the property.
	 * 
	 * @return
	 */
	public Object getValue();

	/**
	 * Returns the name of the property
	 */
	public String getName();

	/**
	 * The operation that quantifies the property.
	 * 
	 * @return
	 */
	public RelationOperation getOperation();
}
