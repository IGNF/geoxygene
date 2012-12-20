/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.relief;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjPointDefault;
import fr.ign.cogit.cartagen.core.genericschema.relief.IDEMPixel;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/*
 * ###### IGN / CartAGen ###### Title: ReliefElementPoint Description: Points
 * cotés Author: J. Renard Date: 30/06/2010
 */

public class DEMPixel extends GeneObjPointDefault implements IDEMPixel {

  double x, y, z;

  /**
   * Constructor
   */
  public DEMPixel(double x, double y, double z) {
    super();
    this.x = x;
    this.y = y;
    this.z = z;
    this.setGeom(new GM_Point(new DirectPosition(x, y)));
    this.setInitialGeom(new GM_Point(new DirectPosition(x, y)));
    this.setEliminated(false);
  }

  @Override
  public double getX() {
    return this.x;
  }

  @Override
  public void setX(double x) {
    this.x = x;
    this.setGeom(new GM_Point(new DirectPosition(x, this.getGeom()
        .getPosition().getY())));
  }

  @Override
  public double getY() {
    return this.y;
  }

  @Override
  public void setY(double y) {
    this.y = y;
    this.setGeom(new GM_Point(new DirectPosition(this.getGeom().getPosition()
        .getX(), y)));
  }

  @Override
  public void setCoordinates(double x, double y) {
    this.x = x;
    this.y = y;
    this.setGeom(new GM_Point(new DirectPosition(x, y)));
  }

  @Override
  public double getZ() {
    return this.z;
  }

  @Override
  public void setZ(double z) {
    this.z = z;
  }

  @Override
  public boolean intersecte(IEnvelope env) {
    return env.maxX() > this.getX()
        - CartagenApplication.getInstance().getDEMResolution()
        && env.minX() < this.getX()
            + CartagenApplication.getInstance().getDEMResolution()
        && env.maxY() > this.getY()
            - CartagenApplication.getInstance().getDEMResolution()
        && env.minY() < this.getY()
            + CartagenApplication.getInstance().getDEMResolution();
  }

}
