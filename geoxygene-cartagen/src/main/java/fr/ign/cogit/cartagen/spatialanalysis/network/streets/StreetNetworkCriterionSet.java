/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.network.streets;

public class StreetNetworkCriterionSet {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //
  boolean compactCrit = true;// compactness criterion
  boolean areaCrit = true;// area compactness
  boolean strokeCrit = true;// strokes length criterion
  boolean traffCrit = false;// traffic criterion
  boolean centrCrit = false;// strokes centrality criterion
  boolean densBuildCrit = true;// building density criterion
  boolean densDiffCrit = true;// density difference criterion
  boolean crossCrit = true;// crossing strokes criterion

  // Private fields //

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public StreetNetworkCriterionSet(boolean compactCrit, boolean areaCrit,
      boolean strokeCrit, boolean traffCrit, boolean centrCrit,
      boolean densBuildCrit, boolean densDiffCrit, boolean crossCrit) {
    super();
    this.compactCrit = compactCrit;
    this.areaCrit = areaCrit;
    this.strokeCrit = strokeCrit;
    this.traffCrit = traffCrit;
    this.centrCrit = centrCrit;
    this.densBuildCrit = densBuildCrit;
    this.densDiffCrit = densDiffCrit;
    this.crossCrit = crossCrit;
  }

  public StreetNetworkCriterionSet() {
    super();
  }

  // Getters and setters //

  // Other public methods //

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
