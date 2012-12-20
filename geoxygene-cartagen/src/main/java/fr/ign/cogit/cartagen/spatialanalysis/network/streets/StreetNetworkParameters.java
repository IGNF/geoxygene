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

public class StreetNetworkParameters {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  // the maximum cost authorised for a large city (0.4 advised for standard
  // criteria)
  public static double costLarge = 0.4;
  // the maximum cost authorised for a small city (0.3 advised for standard
  // criteria)
  public static double costSmall = 0.3;
  // the maximum cost authorised for a medium city (0.35 advised for standard
  // criteria)
  public static double costMed = 0.35;
  // the maximum block area authorised for a large city (250000.0 advised for
  // standard criteria)
  public static double surfLarge = 250000.0;
  // the maximum block area authorised for a small city (100000.0 advised for
  // standard criteria)
  public static double surfSmall = 100000.0;
  // the maximum block area authorised for a medium city (150000.0 advised for
  // standard criteria)
  public static double surfMed = 150000.0;
  // the importance value for a road under which it is considered as a city axis
  public static int importanceThreshold = 2;

}
