/**
 * 20 juil. 2009
 */
package fr.ign.cogit.geoxygene.shemageo.bati;

import java.util.Collection;
import java.util.HashSet;

import fr.ign.cogit.geoxygene.api.schemageo.bati.Ilot;
import fr.ign.cogit.geoxygene.api.schemageo.bati.Quartier;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.shemageo.support.elementsIndependants.MesoImpl;

/**
 * @author JGaffuri 20 juil. 2009
 * 
 */
public class IlotImpl extends MesoImpl implements Ilot {

  public IlotImpl(IGeometry geom) {
    super(geom);
    this.arcsReseaux = new HashSet<ArcReseau>();
  }

  @Override
  public Quartier getMeso() {
    return (Quartier) super.getMeso();
  }

  /**
   * les arcs de reseau limitrophes ou contenus dans l'ilot
   */
  private Collection<ArcReseau> arcsReseaux;

  @Override
  public Collection<ArcReseau> getArcsReseaux() {
    return this.arcsReseaux;
  }

}
