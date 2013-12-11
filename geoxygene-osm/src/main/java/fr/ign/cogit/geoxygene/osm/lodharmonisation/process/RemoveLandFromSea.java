package fr.ign.cogit.geoxygene.osm.lodharmonisation.process;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.osm.lodanalysis.relations.LoDSpatialRelation;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.BufferComputing;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.Side;

public class RemoveLandFromSea {

  private Set<LoDSpatialRelation> inconsistencies;

  public RemoveLandFromSea(Set<LoDSpatialRelation> inconsistencies) {
    super();
    this.inconsistencies = inconsistencies;
  }

  /**
   * 
   * @return a set of the modified features.
   */
  public Set<IGeneObj> harmonise() {
    Set<IGeneObj> modifiedFeats = new HashSet<IGeneObj>();
    // harmonise each group
    for (LoDSpatialRelation relation : inconsistencies) {
      IGeometry halfBuffer = BufferComputing.buildLineHalfBuffer(
          (ILineString) relation.getFeature1().getGeom(), 0.1,
          (Side) Side.RIGHT);
      IGeometry diff = relation.getFeature2().getGeom().difference(halfBuffer);
      if (diff instanceof IPolygon) {
        relation.getFeature2().setGeom(diff);
        modifiedFeats.add(relation.getFeature2());
      } else {
        @SuppressWarnings("unchecked")
        IPolygon simplePol = CommonAlgorithmsFromCartAGen
            .getBiggerFromMultiSurface((IMultiSurface<IOrientableSurface>) diff);
        relation.getFeature2().setGeom(simplePol);
        modifiedFeats.add(relation.getFeature2());
      }
    }
    return modifiedFeats;
  }
}
