/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.measures;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class Compactness {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private IPolygon geom;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public Compactness(IPolygon geom) {
    this.geom = geom;
  }

  // Getters and setters //
  public void setGeom(IPolygon geom) {
    this.geom = geom;
  }

  public IPolygon getGeom() {
    return this.geom;
  }

  // Other public methods //
  public double getMillerIndex() {
    return 4.0 * Math.PI * this.geom.area()
        / (this.geom.perimeter() * this.geom.perimeter());
  }

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
