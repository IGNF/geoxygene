/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.elementsIndependants;

import fr.ign.cogit.geoxygene.api.schemageo.support.elementsIndependants.Micro;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public class MicroImpl extends ElementIndependantImpl implements Micro {

  /**
   * constructeur par defaut
   * 
   * @param geom
   */
  public MicroImpl(IGeometry geom) {
    this.setGeom(geom);
  }

}
