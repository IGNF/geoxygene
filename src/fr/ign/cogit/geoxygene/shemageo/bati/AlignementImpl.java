/**
 * 20 juil. 2009
 */
package fr.ign.cogit.geoxygene.shemageo.bati;

import fr.ign.cogit.geoxygene.api.schemageo.bati.Alignement;
import fr.ign.cogit.geoxygene.api.schemageo.bati.Ilot;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.shemageo.support.elementsIndependants.MesoImpl;

/**
 * @author JGaffuri 20 juil. 2009
 * 
 */
public class AlignementImpl extends MesoImpl implements Alignement {

  public AlignementImpl(IGeometry geom) {
    super(geom);
  }

  @Override
  public Ilot getMeso() {
    return (Ilot) super.getMeso();
  }

}
