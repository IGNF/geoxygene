package fr.ign.cogit.geoxygene.spatialrelation.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.spatialrelation.relation.BuildingAlongARoad;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

public class RoadAndBuildingsGeometry extends DefaultFeature {

  private IFeature road;
  private List<BuildingAlongARoad> roadRelations;

  // *********************
  // CONSTRUCTOR
  // *********************
  public RoadAndBuildingsGeometry(IFeature roadToSet,
      List<BuildingAlongARoad> roadRelationsToSet) throws Exception {

    // set the properties
    this.road = roadToSet;
    this.roadRelations = roadRelationsToSet;
    if (roadRelationsToSet.size() != 0) {
      this.setGeom(createGeom(roadToSet, roadRelationsToSet));
    } else
      this.setGeom(roadToSet.getGeom().buffer(3));

    // create the attribute schema
    SchemaDefaultFeature schemaBR = new SchemaDefaultFeature();
    Map<Integer, String[]> lookup = new HashMap<Integer, String[]>();
    String[] attr1 = { "road", "road" };
    String[] attr2 = { "nbOfB", "nbOfB" };
    lookup.put(0, attr1);
    lookup.put(1, attr2);
    schemaBR.setAttLookup(lookup);
    this.setSchema(schemaBR);

    // set the attributes
    Object[] attributes = { this.road.getAttribute("refCleBDUni"),
        this.roadRelations.size() };
    this.setAttributes(attributes);
  }

  public IPolygon createGeom(IFeature road,
      List<BuildingAlongARoad> roadRelations) throws Exception {
    List<IGeometry> listeGeometries = new ArrayList<IGeometry>();
    listeGeometries.add(road.getGeom().buffer(2));
    for (BuildingAlongARoad relation : roadRelations) {
      listeGeometries.add(relation.getGeom().buffer(2));
      listeGeometries.add(relation.getMember1().getGeom().buffer(2));
    }

    IPolygon RoadBuildingsExtent = (IPolygon) JtsAlgorithms
        .union(listeGeometries);
    while (RoadBuildingsExtent.getInterior().isEmpty() != true) {
      RoadBuildingsExtent.removeInterior(0);
    }
    return RoadBuildingsExtent;
  }
}
