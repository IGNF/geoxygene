package fr.ign.cogit.cartagen.spatialrelation.api;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * A spatial relation between two objects.
 * 
 * @author GTouya
 * 
 */
public interface BinarySpatialRelation extends SpatialRelation {

	/**
	 * Get the first member of the {@link BinarySpatialRelation}.
	 * 
	 * @return
	 */
	public IFeature getMember1();

	/**
	 * Get the second member of the {@link BinarySpatialRelation}.
	 * 
	 * @return
	 */
	public IFeature getMember2();
}
