package fr.ign.cogit.osm.lodharmonisation.process;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.osm.lodanalysis.relations.LoDSpatialRelation;
import fr.ign.cogit.osm.lodharmonisation.operation.ReDetailArea;

public class ExtendBuiltUpAreas {

  private Set<LoDSpatialRelation> inconsistencies;
  private int iterations = 1;

  private double radius;
  /**
   * The Douglas&Peucker simplification threshold to apply on the extended
   * outline of the built-up areas.
   */
  private double doug;

  public ExtendBuiltUpAreas(Set<LoDSpatialRelation> inconsistencies,
      double radius, double doug) {
    super();
    this.inconsistencies = inconsistencies;
    this.radius = radius;
    this.doug = doug;
  }

  /**
   * 
   * @return a set of the modified features.
   */
  public Set<IGeneObj> harmonise() {
    Set<IGeneObj> modifiedFeats = new HashSet<IGeneObj>();
    List<Set<LoDSpatialRelation>> groups = sortByBuiltUpArea();
    // harmonise each group
    for (Set<LoDSpatialRelation> group : groups) {
      Set<IGeometry> detailedComponents = new HashSet<IGeometry>();
      IPolygon areaToDetail = null;
      IGeneObj builtUp = null;
      for (LoDSpatialRelation rel : group) {
        if (areaToDetail == null) {
          areaToDetail = (IPolygon) rel.getFeature2().getGeom();
          builtUp = rel.getFeature2();
        }
        detailedComponents.add(areaToDetail);
      }
      ReDetailArea operation = new ReDetailArea(areaToDetail,
          detailedComponents);
      builtUp.setGeom(operation.reDetailByBuffer(radius, doug));
      modifiedFeats.add(builtUp);

      // iterative case, look for other relations with the new geometry
      if (iterations > 1) {
        // TODO
      }
    }
    return modifiedFeats;
  }

  /**
   * Group the inconsistencies that share a same built-up area but different
   * buildings.
   * @return
   */
  private List<Set<LoDSpatialRelation>> sortByBuiltUpArea() {
    List<Set<LoDSpatialRelation>> groups = new ArrayList<Set<LoDSpatialRelation>>();
    Stack<LoDSpatialRelation> stack = new Stack<LoDSpatialRelation>();
    stack.addAll(inconsistencies);
    while (!stack.isEmpty()) {
      // it's a new group
      Set<LoDSpatialRelation> group = new HashSet<LoDSpatialRelation>();
      LoDSpatialRelation current = stack.pop();
      IGeneObj builtUp = current.getFeature2();
      group.add(current);
      for (LoDSpatialRelation rel : inconsistencies) {
        if (group.contains(rel))
          continue;
        if (builtUp.equals(rel.getFeature2())) {
          group.add(rel);
          stack.remove(rel);
        }
      }
      groups.add(group);
    }
    return groups;
  }

  public int getIterations() {
    return iterations;
  }

  public void setIterations(int iterations) {
    this.iterations = iterations;
  }

}
