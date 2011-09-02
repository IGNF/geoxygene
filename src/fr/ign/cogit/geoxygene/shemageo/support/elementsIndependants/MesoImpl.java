/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.elementsIndependants;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.schemageo.support.elementsIndependants.ElementIndependant;
import fr.ign.cogit.geoxygene.api.schemageo.support.elementsIndependants.Meso;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public class MesoImpl extends ElementIndependantImpl implements Meso {

  /**
   * constructeur par defaut
   * 
   * @param geom
   */
  public MesoImpl(IGeometry geom) {
    this(geom, new FT_FeatureCollection<ElementIndependant>());
  }

  /**
   * @param geom
   * @param composants
   */
  public MesoImpl(IGeometry geom, Collection<ElementIndependant> composants) {
    this.setGeom(geom);
    this.setComposants(composants);
  }

  /**
   * les composants de l'objet
   */
  private Collection<ElementIndependant> composants = null;

  @Override
  public Collection<ElementIndependant> getComposants() {
    return this.composants;
  }

  @Override
  public void setComposants(Collection<ElementIndependant> composants) {
    this.composants = composants;
  }

}
