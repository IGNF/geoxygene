package fr.ign.cogit.cartagen.genealgorithms.rail;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.StrokesNetwork;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;

public class CollapseDualRails {

  private double searchRadius = 10.0;
  private double hausdorffThreshold = 10.0;
  private Set<IRailwayLine> rails;
  private Map<ArcReseau, IRailwayLine> map = new HashMap<ArcReseau, IRailwayLine>();
  private StrokesNetwork strokes;
  private String attributeName = null;

  public CollapseDualRails(Collection<IRailwayLine> rails) {
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

  private void enrichAndMakePlanar() {
    AbstractCreationFactory factory = CartAGenDoc.getInstance()
        .getCurrentDataset().getCartAGenDB().getGeneObjImpl()
        .getCreationFactory();
    IFeatureCollection<IGeneObj> fc = new FT_FeatureCollection<IGeneObj>();
    for (IRailwayLine obj : rails)
      fc.add(obj);
    CarteTopo carteTopo = new CarteTopo("build faces");
    carteTopo.importClasseGeo(fc, true);
    carteTopo.rendPlanaire(0.01);
    for (IGeneObj obj : fc) {
      try {
        // test if the section has been cut by topological map
        if (obj.getCorrespondants().size() == 1) {
          continue;
        }

        // update the section geometry with the first edge of the
        // topological
        // map
        obj.setGeom(obj.getCorrespondants().get(0).getGeom());

        // loop on the other edges to make new instances
        for (int i = 1; i < obj.getCorrespondants().size(); i++) {
          ILineString newLine = (ILineString) obj.getCorrespondants().get(i)
              .getGeom();
          if (obj instanceof IRailwayLine) {
            IRailwayLine newObj = factory.createRailwayLine(newLine, 0);
            newObj.setSymbolId(obj.getSymbolId());
            // copy attributes
            newObj.copyAttributes(obj);
            rails.add(newObj);
            CartAGenDoc.getInstance().getCurrentDataset().getRailwayLines()
                .add(newObj);
          }
        }
      } catch (SecurityException e1) {
        e1.printStackTrace();
      } catch (IllegalArgumentException e1) {
        e1.printStackTrace();
      }
    }

    // now enrich the network
    NetworkEnrichment.enrichNetwork(CartAGenDoc.getInstance()
        .getCurrentDataset(), CartAGenDoc.getInstance().getCurrentDataset()
        .getRailwayNetwork(), factory);

    for (IRailwayLine obj : rails) {
      if (((INetworkSection) obj).getInitialNode() == null) {
        obj.eliminateBatch();
        continue;
      }
      if (((INetworkSection) obj).getFinalNode() == null) {
        obj.eliminateBatch();
        continue;
      }
      if (obj.isSidetrack()) {
        continue;
      }
      map.put((ArcReseau) obj.getGeoxObj(), (IRailwayLine) obj);
    }
  }

  private Map<Stroke, Stroke> matchStrokes() {
    Map<Stroke, Stroke> map = new HashMap<Stroke, Stroke>();
    Stack<Stroke> toMatch = new Stack<Stroke>();
    toMatch.addAll(strokes.getStrokes());
    while (!toMatch.isEmpty()) {
      Stroke stroke = toMatch.pop();
      // search for neighbouring strokes
      IFeatureCollection<Stroke> others = new FT_FeatureCollection<Stroke>();
      others.addAll(toMatch);
      // railroads are usually separated by 13-14 meters, so a 15m threshold is
      // used
      Collection<Stroke> neighbours = others.select(stroke.getGeom().buffer(
          15.0));
      Stroke match = null;
      for (Stroke neighbour : neighbours) {
        // filter the candidates with length, as matched strokes should have
        // approximately the
      }

      // if no correct match has been found, continue
      if (match == null)
        continue;

      // arrived here, the current stroke has been matched to another one
      toMatch.remove(match);
      map.put(stroke, match);
    }
    return map;
  }

  private ILineString collapseMatch(Stroke stroke1, Stroke stroke2) {
    IDirectPositionList coord = new DirectPositionList();

    // TODO
    return new GM_LineSegment(coord);
  }

  public void collapseDualRailways() {
    enrichAndMakePlanar();

    computeStrokes();

    Map<Stroke, Stroke> matching = matchStrokes();

    AbstractCreationFactory factory = CartAGenDoc.getInstance()
        .getCurrentDataset().getCartAGenDB().getGeneObjImpl()
        .getCreationFactory();

    for (Stroke stroke1 : matching.keySet()) {
      ILineString newLine = collapseMatch(stroke1, matching.get(stroke1));
      IRailwayLine obj = null;
      for (ArcReseau arc : stroke1.getFeatures()) {
        IRailwayLine rail = map.get(arc);
        if (rail != null) {
          rail.eliminateBatch();
          if (obj == null)
            obj = rail;
        }
      }
      for (ArcReseau arc : matching.get(stroke1).getFeatures()) {
        IRailwayLine rail = map.get(arc);
        if (rail != null) {
          rail.eliminateBatch();
        }
      }

      // create the new railway
      IRailwayLine newObj = factory.createRailwayLine(newLine, 0);
      newObj.setSymbolId(obj.getSymbolId());
      // copy attributes
      newObj.copyAttributes(obj);
      rails.add(newObj);
      CartAGenDoc.getInstance().getCurrentDataset().getRailwayLines()
          .add(newObj);
    }
  }
}
