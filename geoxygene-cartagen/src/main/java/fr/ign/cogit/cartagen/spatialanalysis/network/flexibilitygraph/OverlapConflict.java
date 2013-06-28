package fr.ign.cogit.cartagen.spatialanalysis.network.flexibilitygraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen.LineConnectionInfo;

public class OverlapConflict extends NetworkConflict {

  private double worstProxi;

  public OverlapConflict(Set<INetworkSection> sections) {
    super(sections);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((getSections() == null) ? 0 : getSections().hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    OverlapConflict other = (OverlapConflict) obj;
    if (getSections() == null) {
      if (other.getSections() != null)
        return false;
    } else if (!getSections().equals(other.getSections()))
      return false;
    return true;
  }

  public static OverlapConflict computeConflictCost(INetworkSection section,
      Collection<INetworkSection> neighbours, Set<MinimumSeparation> minSeps,
      double networkRes) {
    double totalCost = 0.0;
    double worstProxi = Double.MAX_VALUE;
    Set<INetworkSection> conflictSections = new HashSet<INetworkSection>();
    for (INetworkSection other : neighbours) {
      double minSep = getMinSepBetween(section, other, minSeps);
      double minDist = (section.getWidth() * Legend.getSYMBOLISATI0N_SCALE() / 1000.0)
          + (other.getWidth() * Legend.getSYMBOLISATI0N_SCALE() / 1000.0)
          + minSep;

      // check that lines are close enough to have a conflict
      if (!section.getGeom().buffer(minDist).intersects(other.getGeom()))
        continue;

      // compute the step of the buffer
      double circleStep = 0.0;
      if ((2.0 * minDist - networkRes) > 0.0)
        circleStep = (2.0 * Math
            .sqrt(networkRes * (2.0 * minDist - networkRes)));
      else
        circleStep = (minDist / 3.5);
      // To respect the resolution it must be <
      // 2.sqrt(resolution(2.radius-resolution)
      circleStep = (circleStep / 3.0);
      int nSegments = (int) Math.round(Math.PI * minDist / circleStep);

      if (worstProxi > minDist)
        worstProxi = minDist;

      IPolygon boundaryGeom = (IPolygon) other.getGeom().buffer(minDist,
          nSegments);

      // check lines connections
      LineConnectionInfo connectionInfo = CommonAlgorithmsFromCartAGen
          .getLineConnectionInfo(section.getGeom(), other.getGeom());

      // compute the intersection of boundaryGeom and section.getGeom() to see
      // which parts of the section intersect the boundaryGeom.
      IGeometry inter = boundaryGeom.intersection(section.getGeom());
      double averageCost = minDist;
      if (inter instanceof ILineString) {
        // TODO
      } else {
        // TODO
      }

      averageCost = averageCost / (minDist * section.getGeom().length());
      totalCost += averageCost;
    }
    if (totalCost > 0.0) {
      OverlapConflict conflict = new OverlapConflict(conflictSections);
      conflict.setConflictCost(totalCost);
      conflict.setWorstProxi(worstProxi);
      return conflict;
    }
    return null;
  }

  /*
   * private static double[] getOverlapingCost(ILineString line1, ILineString
   * line2, double minDist, LineConnectionInfo info) { // search the nearest
   * points of section from inter extreme points int nearestS =
   * CommonAlgorithmsFromCartAGen .getNearestVertexPositionFromPoint(line1,
   * line2.startPoint()); int nearestE = CommonAlgorithmsFromCartAGen
   * .getNearestVertexPositionFromPoint(line1, line2.endPoint()); boolean
   * connected = false; if(nearestS==0 && info.getAngleS()>-10) connected =
   * true; else if(nearestE == line1.numPoints()-1 && info.getAngleE() >-10)
   * connected = true;
   * 
   * if(!connected){
   * 
   * } }
   */

  public static double getMinSepBetween(INetworkSection section1,
      INetworkSection section2, Set<MinimumSeparation> minSeps) {
    for (MinimumSeparation minSep : minSeps) {
      if (minSep.appliesTo(section1, section2))
        return minSep.getMinSepMeters();
    }
    return -1.0;
  }

  public double getWorstProxi() {
    return worstProxi;
  }

  public void setWorstProxi(double worstProxi) {
    this.worstProxi = worstProxi;
  }

  public static Set<OverlapConflict> searchOverlapConflictSimple(
      Collection<INetworkSection> features, Set<MinimumSeparation> minSeps) {
    Set<OverlapConflict> conflicts = new HashSet<OverlapConflict>();
    Set<INetworkSection> conflictCenters = new HashSet<INetworkSection>();
    for (INetworkSection section : features) {
      if (section.isEliminated())
        continue;

      IFeatureCollection<INetworkSection> fc = new FT_FeatureCollection<INetworkSection>();
      fc.addAll(features);
      fc.remove(section);
      fc.removeAll(conflictCenters);
      // first get the section envelope
      IEnvelope env = section.getGeom().envelope();
      // expand it a little bit
      env = new GM_Envelope(env.minX() - 0.5, env.maxX() + 0.5,
          env.minY() - 0.5, env.maxY() + 0.5);

      Collection<INetworkSection> neighbours = fc.select(env);
      Set<INetworkSection> overlapping = new HashSet<INetworkSection>();
      for (INetworkSection other : neighbours) {

        if (other.isEliminated())
          continue;

        double minSep = getMinSepBetween(section, other, minSeps);
        if (minSep == -1.0)
          continue;

        double minDist = (section.getWidth() * Legend.getSYMBOLISATI0N_SCALE() / 1000.0)
            + (other.getWidth() * Legend.getSYMBOLISATI0N_SCALE() / 1000.0)
            + minSep;

        // check that lines are close enough to have a conflict
        if (!section.getGeom().buffer(minDist).intersects(other.getGeom()))
          continue;

        // checks connections
        if (section.getGeom().intersects(other.getGeom()))
          continue;

        // checks that the conflict is not with an extreme node of the line that
        // is connected to other network sections.
        IDirectPosition nearestPt = CommonAlgorithms.getNearestPoint(
            other.getGeom(), section.getGeom());
        if (other.getGeom().startPoint().equals(nearestPt)) {
          INetworkNode node = other.getInitialNode();
          if (node.getDegree() > 1)
            continue;
        } else if (other.getGeom().endPoint().equals(nearestPt)) {
          INetworkNode node = other.getFinalNode();
          // case with a problem in enrichment
          if (node == null)
            continue;

          if (node.getDegree() > 1)
            continue;
        }

        overlapping.add(other);
      }

      if (overlapping.size() == 0)
        continue;

      // add section to the overlapping set
      overlapping.add(section);
      conflictCenters.add(section);

      // add a new conflict
      conflicts.add(new OverlapConflict(overlapping));
    }

    return conflicts;
  }

  public boolean areInConflict(INetworkSection section1,
      INetworkSection section2) {
    if (this.getSections().contains(section1)
        && this.getSections().contains(section2))
      return true;
    return false;
  }
}
