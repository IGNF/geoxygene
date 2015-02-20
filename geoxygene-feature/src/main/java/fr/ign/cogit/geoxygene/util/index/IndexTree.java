package fr.ign.cogit.geoxygene.util.index;

import java.util.Collection;
import java.util.HashSet;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.index.SpatialIndex;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public abstract class IndexTree<Feat extends IFeature> implements
    SpatialIndex<Feat> {

  protected IFeatureCollection<Feat> collection;

  public Collection<Feat> select(IGeometry geometry, double distance) {
    if (distance == 0) {
      return this.select(geometry);
    }
    try {
      return this.select(geometry.buffer(distance));
    } catch (Exception e) {
      System.out
          .println("PROBLEME AVEC LA FABRICATION DU BUFFER LORS D'UNE REQUETE SPATIALE");
      e.printStackTrace();
      return new HashSet<Feat>(0);
    }
  }

  public void clear() {
    this.collection = null;
  }

}
