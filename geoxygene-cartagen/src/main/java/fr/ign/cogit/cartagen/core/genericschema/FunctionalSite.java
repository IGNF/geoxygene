/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema;

/**
 * A GeneObj class that implements FunctionalSite contains objects that are
 * functional sites according to (Chaudhry et al. 2009) definition. Schools,
 * hospitals, airports, retail areas, prisons, parks are instances of functional
 * sites, i.e. sites that are aggregations of common geographical features like
 * buildings, roads, lakes, sports ground, runways etc.
 * @author GTouya
 * 
 */
public interface FunctionalSite {

  /**
   * Defines the likelihood of a given feature to be part of {@code this}
   * functional site, according to function considerations. i.e. a sports ground
   * is very likely to be part of a school, while it is unlikely to be part of
   * an airport.
   * @param obj
   * @return 0 if likelihood is neutral, negative values for unlikeliness and
   *         positive values for likeliness.
   */
  public int getFunctionalBelonging(IGeneObj obj);

  /**
   * Defines the likelihood of a given feature to be part of {@code this}
   * functional site, according to spatial considerations. i.e. a sports ground
   * is very likely to be part of a school if it's inside it, and it is unlikely
   * to be part of it if it is 100 m oustide the school.
   * @param obj
   * @return 0 if likelihood is neutral, negative values for unlikeliness and
   *         positive values for likeliness.
   */
  public int getSpatialBelonging(IGeneObj obj);
}
