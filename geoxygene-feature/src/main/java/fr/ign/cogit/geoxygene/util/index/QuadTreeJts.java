package fr.ign.cogit.geoxygene.util.index;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.quadtree.Quadtree;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class QuadTreeJts<Feat extends IFeature> extends IndexTree<Feat> {

  private Quadtree quad;

  public QuadTreeJts(IFeatureCollection<Feat> collection) {
    this.collection = collection;
    quad = new Quadtree();
    for (int i = 0; i < collection.size(); ++i) {
      IEnvelope egeox = collection.get(i).getGeom().getEnvelope();
      Envelope envelopeJts = new Envelope(egeox.maxX(), egeox.minX(),
          egeox.maxY(), egeox.minY());
      quad.insert(envelopeJts, i);
    }
  }

  @Override
  public List<Object> getParametres() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean hasAutomaticUpdate() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void setAutomaticUpdate(boolean auto) {
    // TODO Auto-generated method stub

  }

  @Override
  public void update(Feat value, int cas) {
    // TODO Auto-generated method stub

  }

  @Override
  public Collection<Feat> select(IDirectPosition P, double D) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<Feat> select(IEnvelope env) {
    // TODO Auto-generated method stub
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<Feat> select(IGeometry geometry) {
    IEnvelope egeox = geometry.getEnvelope();
    Envelope envelopeJts = new Envelope(egeox.maxX(), egeox.minX(),
        egeox.maxY(), egeox.minY());
    List<Integer> indexes = quad.query(envelopeJts);
    Collection<Feat> result = new HashSet<Feat>();
    for (int ind : indexes) {
      Feat f = collection.get(ind);
      if (f.getGeom().intersects(geometry))
        result.add(f);
    }
    return result;
  }

  @Override
  public Collection<Feat> select(IGeometry geometry, boolean strictlyCrosses) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void clear() {
    super.clear();
    this.quad = null;
  }

}
