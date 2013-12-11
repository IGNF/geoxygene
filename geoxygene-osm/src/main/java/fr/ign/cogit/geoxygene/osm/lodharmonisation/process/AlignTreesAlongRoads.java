package fr.ign.cogit.geoxygene.osm.lodharmonisation.process;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.land.ITreePoint;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.osm.lodanalysis.relations.LoDSpatialRelation;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.BufferComputing;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.Side;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Segment;

public class AlignTreesAlongRoads {

  private Set<LoDSpatialRelation> leftInconsistencies, rightInconsistencies;
  private double alignmentOffset;

  public AlignTreesAlongRoads(Set<LoDSpatialRelation> leftInconsistencies,
      Set<LoDSpatialRelation> rightInconsistencies, double alignmentOffset) {
    super();
    this.leftInconsistencies = leftInconsistencies;
    this.rightInconsistencies = rightInconsistencies;
    this.alignmentOffset = alignmentOffset;
  }

  /**
   * 
   * @return a set of the modified features.
   */
  public Set<IGeneObj> harmonise() {
    Set<IGeneObj> modifiedFeats = new HashSet<IGeneObj>();

    // first search trees in relation with several roads
    Set<ITreePoint> multipleRelations = new HashSet<ITreePoint>();
    Set<IGeneObj> trees = new HashSet<IGeneObj>();
    for (LoDSpatialRelation relation : leftInconsistencies) {
      if (!trees.contains(relation.getFeature2()))
        trees.add(relation.getFeature2());
      else
        multipleRelations.add((ITreePoint) relation.getFeature2());
    }
    for (LoDSpatialRelation relation : rightInconsistencies) {
      if (!trees.contains(relation.getFeature2()))
        trees.add(relation.getFeature2());
      else
        multipleRelations.add((ITreePoint) relation.getFeature2());
    }

    // initialise a map to further handle multiple relations trees
    Map<ITreePoint, Set<ILineString>> multipleAlign = new HashMap<ITreePoint, Set<ILineString>>();
    for (LoDSpatialRelation relation : leftInconsistencies) {
      IRoadLine road = (IRoadLine) relation.getFeature1();
      ITreePoint tree = (ITreePoint) relation.getFeature2();
      ILineString leftBufferLine = BufferComputing.buildHalfOffsetLine(
          Side.LEFT, road.getGeom(),
          road.getWidth() * Legend.getSYMBOLISATI0N_SCALE() / 2000
              + alignmentOffset);
      if (multipleRelations.contains(tree)) {
        Set<ILineString> offsets = new HashSet<ILineString>();
        if (multipleAlign.containsKey(tree))
          offsets = multipleAlign.get(tree);
        offsets.add(leftBufferLine);
        multipleAlign.put(tree, offsets);
        continue;
      }
      IDirectPosition newTree = CommonAlgorithms.getNearestPoint(
          leftBufferLine, tree.getGeom());
      tree.setGeom(newTree.toGM_Point());
      modifiedFeats.add(tree);
    }

    for (LoDSpatialRelation relation : rightInconsistencies) {
      IRoadLine road = (IRoadLine) relation.getFeature1();
      ITreePoint tree = (ITreePoint) relation.getFeature2();
      ILineString rightBufferLine = BufferComputing.buildHalfOffsetLine(
          Side.RIGHT, road.getGeom(),
          road.getWidth() * Legend.getSYMBOLISATI0N_SCALE() / 2000
              + alignmentOffset);
      if (multipleRelations.contains(tree)) {
        Set<ILineString> offsets = new HashSet<ILineString>();
        if (multipleAlign.containsKey(tree))
          offsets = multipleAlign.get(tree);
        offsets.add(rightBufferLine);
        multipleAlign.put(tree, offsets);
        continue;
      }
      IDirectPosition newTree = CommonAlgorithms.getNearestPoint(
          rightBufferLine, tree.getGeom());
      tree.setGeom(newTree.toGM_Point());
      modifiedFeats.add(tree);
    }

    // now, handle the trees in multiple alignments
    for (ITreePoint tree : multipleAlign.keySet()) {
      Set<ILineString> offsets = multipleAlign.get(tree);
      if (offsets.size() != 2) {
        // align on the first one
        IDirectPosition newTree = CommonAlgorithms.getNearestPoint(offsets
            .iterator().next(), tree.getGeom());
        tree.setGeom(newTree.toGM_Point());
        modifiedFeats.add(tree);
      } else {
        // align on the intersection of both lines
        Iterator<ILineString> i = offsets.iterator();
        ILineString line1 = i.next();
        ILineString line2 = i.next();
        IGeometry intersection = line1.intersection(line2);
        if (intersection == null || intersection.isEmpty()) {
          // special case with parallel lines
          IDirectPosition newTree1 = CommonAlgorithms.getNearestPoint(line1,
              tree.getGeom());
          IDirectPosition newTree2 = CommonAlgorithms.getNearestPoint(line2,
              tree.getGeom());
          IDirectPosition newTree = new Segment(newTree1, newTree2)
              .getMiddlePoint();
          double dx = newTree.getX() - tree.getGeom().getPosition().getX();
          double dy = newTree.getY() - tree.getGeom().getPosition().getY();
          tree.displaceAndRegister(dx, dy);
          modifiedFeats.add(tree);
          continue;
        }
        tree.setGeom(intersection.centroid().toGM_Point());
        modifiedFeats.add(tree);
      }
    }

    return modifiedFeats;
  }
}
