/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.util.index;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.index.strtree.STRtree;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Index spatial utilisant le STRtree de JTS. Sort-Tile-Recursive (STR)
 * algorithm
 * 
 */

public class STRtreeJts<Feat extends IFeature> extends IndexTree<Feat> {
  static Logger logger = LogManager.getLogger(STRtreeJts.class.getName());
  private STRtree stree;

  public STRtreeJts(IFeatureCollection<Feat> collection) {
    this.collection = collection;
    stree = new STRtree();
    for (int i = 0; i < collection.size(); ++i) {
      IEnvelope egeox = collection.get(i).getGeom().getEnvelope();
      Envelope envelopeJts = new Envelope(egeox.maxX(), egeox.minX(),
          egeox.maxY(), egeox.minY());
      stree.insert(envelopeJts, i);
    }
  }

  /**
   * Not currently implemented
   */
  @Override
  public List<Object> getParametres() {
    // TODO Auto-generated method stub
    logger.warn("Method "
        + Thread.currentThread().getStackTrace()[1].getMethodName()
        + " not implemented");
    return null;
  }

  /**
   * Not currently implemented
   */
  @Override
  public boolean hasAutomaticUpdate() {
    // TODO Auto-generated method stub
    logger.warn("Method "
        + Thread.currentThread().getStackTrace()[1].getMethodName()
        + " not implemented");
    return false;
  }

  /**
   * Not currently implemented
   */
  @Override
  public void setAutomaticUpdate(boolean auto) {
    // TODO Auto-generated method stub
    logger.warn("Method "
        + Thread.currentThread().getStackTrace()[1].getMethodName()
        + " not implemented");
  }

  /**
   * Not currently implemented
   */
  @Override
  public void update(Feat value, int cas) {
    // TODO Auto-generated method stub
    logger.warn("Method "
        + Thread.currentThread().getStackTrace()[1].getMethodName()
        + " not implemented");
  }

  /**
   * Not currently implemented
   */
  @Override
  public Collection<Feat> select(IDirectPosition P, double D) {
    // TODO Auto-generated method stub
    logger.warn("Method "
        + Thread.currentThread().getStackTrace()[1].getMethodName()
        + " not implemented");
    return null;
  }

  /**
   * Not currently implemented
   */
  @Override
  public Collection<Feat> select(IEnvelope env) {
    // TODO Auto-generated method stub
    logger.warn("Method "
        + Thread.currentThread().getStackTrace()[1].getMethodName()
        + " not implemented");
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<Feat> select(IGeometry geometry) {
    IEnvelope egeox = geometry.getEnvelope();
    Envelope envelopeJts = new Envelope(egeox.maxX(), egeox.minX(),
        egeox.maxY(), egeox.minY());
    List<Integer> indexes = stree.query(envelopeJts);
    Collection<Feat> result = new HashSet<Feat>();
    for (int ind : indexes) {
      Feat f = collection.get(ind);
      if (f.getGeom().intersects(geometry))
        result.add(f);
    }
    return result;
  }

  /**
   * Not currently implemented
   */
  @Override
  public Collection<Feat> select(IGeometry geometry, boolean strictlyCrosses) {
    // TODO Auto-generated method stub
    logger.warn("Method "
        + Thread.currentThread().getStackTrace()[1].getMethodName()
        + " not implemented");
    return null;
  }

  @Override
  public void clear() {
    super.clear();
    this.stree = null;
  }

}
