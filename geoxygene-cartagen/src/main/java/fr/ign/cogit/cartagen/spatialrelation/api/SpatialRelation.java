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

import java.util.List;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * Interface to implement with Java objects the spatial relation ontological
 * model from Touya et al. (2012).
 * 
 * @author GTouya
 * 
 */
public interface SpatialRelation {

	/**
	 * The members of the spatial relation. There are two members for common
	 * binary relations.
	 * 
	 * @return
	 */
	public List<IFeature> getMembers();

	public Set<RelationProperty> getProperties();

	/**
	 * Returns the name of the spatial relation.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * The condition of relevance of the spatial relation (e.g. two features are
	 * near if their relative distance is less than 10m).
	 * 
	 * @return
	 */
	public RelationExpression getConditionOfRelevance();

}
