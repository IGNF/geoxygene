package fr.ign.cogit.cartagen.genealgorithms.rail;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.cartagen.spatialanalysis.network.StrokesNetwork;
import fr.ign.cogit.cartagen.spatialanalysis.network.railways.ParallelRailsGroup;
import fr.ign.cogit.cartagen.spatialanalysis.network.railways.ParallelStroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.railways.ParallelismEndingType;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.NoeudReseau;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class CollapseParallelRailways {

  private double searchRadius = 10.0;
  private double hausdorffThreshold = 10.0;
  private double minimalLength = 800.0;
  private Set<IRailwayLine> rails;
  private Map<ArcReseau, IRailwayLine> map = new HashMap<ArcReseau, IRailwayLine>();
  private StrokesNetwork strokes;
  private String attributeName = "nom";
  private Set<ParallelRailsGroup> groups;

  public CollapseParallelRailways(Collection<IRailwayLine> rails) {
    super();
    if (rails == null || rails.size() == 0)
      this.rails = new HashSet<IRailwayLine>(CartAGenDoc.getInstance()
          .getCurrentDataset().getRailwayLines());
    else
      this.rails = new HashSet<IRailwayLine>(rails);
  }

  private void computeStrokes() {
    strokes = new StrokesNetwork(map.keySet());
    HashSet<String> attributeNames = new HashSet<String>();
    if (this.attributeName != null) {
      attributeNames.add(this.attributeName);
    }
    strokes.buildStrokes(attributeNames, 112.5, 45.0, true);
  }

  @SuppressWarnings("unchecked")
  private void enrichAndMakePlanar() {
    CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
    IFeature feature = rails.iterator().next();
    if (!(feature instanceof IGeneObj))
      return;
    INetwork net = dataset
        .getNetworkFromClass((Class<? extends IGeneObj>) feature.getClass());
    if (net.getNodes().size() == 0) {
      if (net.getSections().size() == 0) {
        for (IFeature section : rails)
          net.addSection((INetworkSection) section);
      }
      NetworkEnrichment.buildTopology(dataset, net, false);
    }

    HashSet<NoeudReseau> noeuds = new HashSet<NoeudReseau>();
    for (IFeature feat : rails) {
      if (feat instanceof IRailwayLine) {
        map.put((ArcReseau) ((IGeneObj) feat).getGeoxObj(), (IRailwayLine) feat);
        NoeudReseau noeudIni = ((ArcReseau) ((IGeneObj) feat).getGeoxObj())
            .getNoeudInitial();
        NoeudReseau noeudFin = ((ArcReseau) ((IGeneObj) feat).getGeoxObj())
            .getNoeudFinal();
        noeuds.add(noeudIni);
        noeuds.add(noeudFin);
      }
    }
  }

  private ILineString computeMiddleLine(ILineString line1, ILineString line2) {
    IDirectPosition start1 = line1.coord().get(0);
    IDirectPosition end1 = line1.coord().get((line1.coord().size() - 1));
    IDirectPosition start2 = line2.coord().get(0);
    IDirectPosition end2 = line2.coord().get((line2.coord().size() - 1));
    ILineString middle = CommonAlgorithmsFromCartAGen.getMeanLine(line1, line2);
    // reconnection
    middle.removeControlPoint(0);
    middle.removeControlPoint(middle.coord().size() - 1);
    // extend middle at its start
    IDirectPosition start = middle.coord().get(0);
    if (start.distance2D(start1) < start.distance2D(end1)) {
      middle.addControlPoint(0, start1);
    } else {
      middle.addControlPoint(0, end1);
    }
    if (start.distance2D(start2) < start.distance2D(end2)) {
      middle.addControlPoint(0, start2);
    } else {
      middle.addControlPoint(0, end2);
    }

    // extend middle at its end
    IDirectPosition end = middle.coord().get((middle.coord().size() - 1));
    if (end.distance2D(start1) < end.distance2D(end1)) {
      middle.addControlPoint(start1);
      ;
    } else {
      middle.addControlPoint(end1);
    }
    if (end.distance2D(start2) < end.distance2D(end2)) {
      middle.addControlPoint(start2);
    } else {
      middle.addControlPoint(end2);
    }

    return middle;
  }

  public void collapseParallelRailwayGroups() {
    enrichAndMakePlanar();

    computeStrokes();

    // compute the parallel strokes groups
    groups = ParallelRailsGroup.findParallelRailsGroup(strokes, minimalLength,
        searchRadius);

    // collapse each group
    for (ParallelRailsGroup group : groups) {
      // loop on the parallel tracks and eliminate the corresponding railway
      // features
      for (ParallelStroke stroke : group.getParallelStrokes()) {
        if (stroke.isCollapsible()) {
          for (ArcReseau arc : stroke.getStroke().getFeatures()) {
            map.get(arc).eliminate();
          }
          continue;
        }

        // arrived here, the parallel stroke is diverging at least once, so it
        // has to be cut and reconnected
        for (ArcReseau arc : stroke.getStroke().getFeatures()) {
          // if the parallel part of the parallel stroke contains the geometry
          // of arc, arc can be removed entirely
          if (stroke.getParallelGeom().contains(arc.getGeom())) {
            map.get(arc).eliminate();
            continue;
          }

          // if the geometry of arc is disjoint with the parallel part of the
          // parallel stroke, the feature related to the arc can be kept
          // entirely.
          if (stroke.getParallelGeom().disjoint(arc.getGeom())) {
            continue;
          }

          // arrived here, the feature related to arc has to be cut around the
          // diverging point and extended to the central stroke of the group to
          // preserve connectivity
          IRailwayLine railway = map.get(arc);
          IGeometry geom = railway.getGeom().difference(
              stroke.getParallelGeom());
          if (geom instanceof ILineString) {
            IDirectPosition diverging = null;
            if (stroke.getStart().getType()
                .equals(ParallelismEndingType.DIVERGING)) {
              diverging = stroke.getStart().getPosition();
              if (!railway.getGeom().contains(diverging.toGM_Point()))
                diverging = stroke.getEnd().getPosition();
            } else
              diverging = stroke.getEnd().getPosition();

            ((ILineString) geom).removeControlPoint(diverging);
            IDirectPosition closest = JtsAlgorithms.getClosestPoint(diverging,
                group.getCentreStroke().getGeomStroke());
            double dist1 = closest
                .distance2D(((ILineString) geom).startPoint());
            double dist2 = closest.distance2D(((ILineString) geom).endPoint());
            if (dist1 < dist2) {
              ((ILineString) geom).addControlPoint(0, closest);
            } else {
              ((ILineString) geom).addControlPoint(closest);
            }

            railway.setGeom(geom);
          } else if (geom instanceof IMultiCurve<?>) {
            // TODO case with two diverging endings
          }
        }
      }
    }
  }
}
