/**
 * 20 juil. 2009
 */
package fr.ign.cogit.geoxygene.shemageo.bati;

import fr.ign.cogit.geoxygene.api.schemageo.bati.Quartier;
import fr.ign.cogit.geoxygene.api.schemageo.bati.Ville;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.shemageo.support.elementsIndependants.MesoImpl;

/**
 * @author JGaffuri 20 juil. 2009
 * 
 */
public class QuartierImpl extends MesoImpl implements Quartier {

  public QuartierImpl(IGeometry geom) {
    super(geom);
  }

  @Override
  public Ville getMeso() {
    return (Ville) super.getMeso();
  }

}
