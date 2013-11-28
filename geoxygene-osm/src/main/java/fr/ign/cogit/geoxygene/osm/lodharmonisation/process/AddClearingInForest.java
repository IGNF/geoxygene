package fr.ign.cogit.geoxygene.osm.lodharmonisation.process;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.defaultschema.urban.UrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.osm.lodanalysis.relations.LoDSpatialRelation;
import fr.ign.cogit.geoxygene.osm.schema.OSMSchemaFactory;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObj;
import fr.ign.cogit.geoxygene.osm.schema.landuse.OsmLandUseTypology;
import fr.ign.cogit.geoxygene.osm.schema.landuse.OsmSimpleLandUseArea;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class AddClearingInForest {

  private Set<LoDSpatialRelation> inconsistencies;

  public enum ClearingShape {
    BUFFER, CONVEX, RECTANGLE
  }

  private ClearingShape shape;
  private boolean networkCut;
  private double erosionThreshold;

  public AddClearingInForest(Set<LoDSpatialRelation> inconsistencies,
      ClearingShape shape, boolean networkCut, double erosionThreshold) {
    super();
    this.inconsistencies = inconsistencies;
    this.shape = shape;
    this.networkCut = networkCut;
    this.erosionThreshold = erosionThreshold;
  }

  /**
   * 
   * @return a set of the modified features.
   */
  @SuppressWarnings("unchecked")
  public Set<IGeneObj> harmonise() {
    Set<IGeneObj> modifiedFeats = new HashSet<IGeneObj>();
    for (LoDSpatialRelation instance : inconsistencies) {
      IGeometry geomClearing = computeClearingGeometry(instance);
      // cut the clearing by the network if necessary
      if (networkCut) {
        for (IGeneObj face : getNetworkFaces()) {
          if (!face.getGeom().intersects(geomClearing))
            continue;
          else {
            IGeometry intersection = face.getGeom().intersection(geomClearing);
            if (((UrbanBlock) instance.getFeature1()).getUrbanElements()
                .select(intersection).isEmpty())
              geomClearing = geomClearing.difference(intersection);
          }
        }
      }
      // apply the new geometry to the forest
      // first check that the clearing is totally inside the forest
      if (!instance.getFeature2().getGeom().contains(geomClearing)) {
        IGeometry newGeom = instance.getFeature2().getGeom()
            .difference(geomClearing);
        if (newGeom instanceof IPolygon)
          instance.getFeature2().setGeom(newGeom);
        else {
          OSMSchemaFactory factory = (OSMSchemaFactory) CartAGenDoc
              .getInstance().getCurrentDataset().getCartAGenDB()
              .getGeneObjImpl().getCreationFactory();
          for (int i = 0; i < ((IMultiSurface<IPolygon>) newGeom).size(); i++) {
            IPolygon simple = ((IMultiSurface<IPolygon>) newGeom).get(i);
            OsmSimpleLandUseArea newObj = (OsmSimpleLandUseArea) factory
                .createSimpleLandUseArea(simple,
                    OsmLandUseTypology.FOREST.ordinal());
            newObj.setTags(((OsmGeneObj) instance.getFeature2()).getTags());
            CartAGenDoc.getInstance().getCurrentDataset().getLandUseAreas()
                .add(newObj);
          }
          instance.getFeature2().eliminateBatch();
        }
      }
      // general case: the clearing is inside the forest
      else {
        if (geomClearing instanceof IPolygon) {
          ((IPolygon) instance.getFeature2().getGeom())
              .addInterior(((IPolygon) geomClearing).getExterior());
        } else {
          // cutting by the network may have cut the clearing into parts
          IPolygon simplePol = CommonAlgorithmsFromCartAGen
              .getBiggerFromMultiSurface((IMultiSurface<IOrientableSurface>) geomClearing);
          ((IPolygon) instance.getFeature2().getGeom()).addInterior(simplePol
              .getExterior());
        }
      }
      modifiedFeats.add(instance.getFeature2());
    }

    return modifiedFeats;
  }

  private IPolygon computeClearingGeometry(LoDSpatialRelation relation) {
    IPolygon bufferGeom = (IPolygon) relation.getFeature1().getGeom()
        .buffer(-erosionThreshold);
    if (shape.equals(ClearingShape.BUFFER))
      return bufferGeom;
    if (shape.equals(ClearingShape.CONVEX))
      return (IPolygon) bufferGeom.convexHull();
    if (shape.equals(ClearingShape.RECTANGLE))
      return bufferGeom.envelope().getGeom();
    return bufferGeom;
  }

  private IPopulation<IGeneObj> getNetworkFaces() {
    IPopulation<IGeneObj> pop = CartAGenDoc.getInstance().getCurrentDataset()
        .getCartagenPop(CartAGenDataSet.NETWORK_FACES_POP);
    if (pop == null) {
      NetworkEnrichment.buildNetworkFaces(CartAGenDoc.getInstance()
          .getCurrentDataset());
      pop = CartAGenDoc.getInstance().getCurrentDataset()
          .getCartagenPop(CartAGenDataSet.NETWORK_FACES_POP);
    }
    return pop;
  }
}
