/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.bati;

import fr.ign.cogit.geoxygene.api.schemageo.bati.AutreConstruction;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.shemageo.support.elementsIndependants.MicroImpl;

/**
 * toute autre construction qui n'est pas un batiment
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class AutreConstructionImpl extends MicroImpl implements
    AutreConstruction {

  public AutreConstructionImpl(IGeometry geom) {
    super(geom);
  }

}
