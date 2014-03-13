package fr.ign.cogit.cartagen.genealgorithms.rail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.StrokesNetwork;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;

public class TypifySideTracks {

  private Set<String> attributeNames;
  // the minimum distance between two sidetrack symbols (in map mm)
  private double separationDist = 0.04;
  // the symbol width for sidetracks at the current scale
  private double symbolWidth = -1;
  // the minimum length for a side track stroke to be kept
  private double minLength;
  private Map<ArcReseau, IRailwayLine> map = new HashMap<ArcReseau, IRailwayLine>();
  private AbstractCreationFactory factory;
  private CartAGenDataSet dataset;

  public TypifySideTracks(double minLength, CartAGenDataSet dataset) {
    super();
    this.minLength = minLength;
    attributeNames = new HashSet<String>();
    this.factory = dataset.getCartAGenDB().getGeneObjImpl()
        .getCreationFactory();
    this.dataset = dataset;
  }

  public void typifySideTracks() {
    // first identify triage areas in the railroad network
    enrichAndMakePlanar();

    Set<TriageArea> triageAreas = identifyTriageAreas();
    for (TriageArea area : triageAreas)
      typifyTriageArea(area);

  }

  private Set<TriageArea> identifyTriageAreas() {
    Set<TriageArea> triageAreas = new HashSet<TypifySideTracks.TriageArea>();
    Set<IRailwayLine> treatedLines = new HashSet<IRailwayLine>();
    for (IRailwayLine rail : dataset.getRailwayLines()) {
      if (!rail.isSidetrack())
        continue;
      if (treatedLines.contains(rail))
        continue;
      IFeatureCollection<IRailwayLine> triageLines = new FT_FeatureCollection<IRailwayLine>();
      Set<IRailwayLine> rootRails = new HashSet<IRailwayLine>();
      triageLines.add(rail);
      treatedLines.add(rail);
      Stack<IRailwayLine> connected = new Stack<IRailwayLine>();
      Collection<IRailwayLine> neighbours = getConnectedRails(rail, rootRails);
      neighbours.removeAll(treatedLines);
      connected.addAll(neighbours);
      neighbours.clear();
      while (!connected.isEmpty()) {
        IRailwayLine follower = connected.pop();
        if (!follower.isSidetrack())
          continue;
        triageLines.add(follower);
        treatedLines.add(follower);
        neighbours = getConnectedRails(follower, rootRails);
        neighbours.removeAll(treatedLines);
        connected.addAll(neighbours);
      }
      IGeometry geom = triageLines.getGeomAggregate().convexHull();
      if (geom instanceof IPolygon)
        triageAreas.add(new TriageArea((IPolygon) geom, triageLines, rootRails,
            attributeNames));
      else
        triageAreas.add(new TriageArea((IPolygon) geom.buffer(1.0),
            triageLines, rootRails, attributeNames));
    }

    return triageAreas;
  }

  private Collection<IRailwayLine> getConnectedRails(IRailwayLine rail,
      Set<IRailwayLine> rootRails) {
    Collection<IRailwayLine> connected = new HashSet<IRailwayLine>();
    if (rail.getInitialNode() != null) {
      for (INetworkSection line : rail.getInitialNode().getInSections()) {
        if (((IRailwayLine) line).isSidetrack())
          connected.add((IRailwayLine) line);
        else
          rootRails.add(rail);
      }
      for (INetworkSection line : rail.getInitialNode().getOutSections()) {
        if (((IRailwayLine) line).isSidetrack())
          connected.add((IRailwayLine) line);
        else
          rootRails.add(rail);
      }
    }
    if (rail.getFinalNode() != null) {
      for (INetworkSection line : rail.getFinalNode().getInSections()) {
        if (((IRailwayLine) line).isSidetrack())
          connected.add((IRailwayLine) line);
        else
          rootRails.add(rail);
      }
      for (INetworkSection line : rail.getFinalNode().getOutSections()) {
        if (((IRailwayLine) line).isSidetrack())
          connected.add((IRailwayLine) line);
        else
          rootRails.add(rail);
      }
    }
    return connected;
  }

  /**
   * Typifies a triage area keeping only long strokes and maintaining a given
   * distance between two branches of the pattern, i.e. if two long strokes are
   * too close, the shorter one is eliminated.
   * @param area
   */
  private void typifyTriageArea(TriageArea area) {
    area.computeStrokes();
    List<Stroke> strokeList = new ArrayList<Stroke>();
    IFeatureCollection<Stroke> strokeFc = new FT_FeatureCollection<Stroke>();
    strokeList.addAll(area.network.getStrokes());
    strokeFc.addAll(area.network.getStrokes());
    HashSet<Stroke> preservedStrokes = new HashSet<Stroke>();
    // sort the strokes from the longest to the shortest
    Collections.sort(strokeList);
    Collections.reverse(strokeList);
    // put the longest stroke connected to the main network at the beginning of
    // the list to make sure it's kept if long enough
    Stroke longest = area.getLongestConnectingStroke();
    if (longest != null) {
      strokeList.remove(longest);
      strokeList.add(0, longest);
      preservedStrokes.add(longest);
    }

    // initialise the minimum distance between two strokes
    double minDist = symbolWidth + separationDist
        * Legend.getSYMBOLISATI0N_SCALE() / 1000;
    for (Stroke stroke : strokeList) {
      // check the stroke has not been deleted yet
      if (stroke.isDeleted())
        continue;
      // filter according to length
      if (stroke.getLength() < minLength) {
        stroke.setDeleted(true);
        strokeFc.remove(stroke);
        for (ArcReseau arc : stroke.getFeatures()) {
          IRailwayLine rail = map.get(arc);
          if (rail != null) {
            rail.eliminateBatch();
          }
        }
      }
      preservedStrokes.add(stroke);
      // arrived here, the stroke is kept, so its shorter too close neighbours
      // have to be eliminated
      Collection<Stroke> neighbours = strokeFc.select(stroke.getGeom().buffer(
          minDist));
      neighbours.removeAll(preservedStrokes);
      // loop on the neighbours to test if they have to be eliminated
      for (Stroke neighbour : neighbours) {
        // the neighbour has to be eliminated if at least 50% of its length is
        // in the buffer
        IGeometry intersection = stroke.getGeom().buffer(minDist)
            .intersection(neighbour.getGeom());
        if (intersection.length() / neighbour.getLength() > 0.5) {
          // eliminate the stroke and its components
          neighbour.setDeleted(true);
          strokeFc.remove(neighbour);
          for (ArcReseau arc : neighbour.getFeatures()) {
            IRailwayLine rail = map.get(arc);
            if (rail != null) {
              rail.eliminateBatch();
            }
          }
        }
      }
    }
  }

  private void enrichAndMakePlanar() {
    IFeatureCollection<IGeneObj> fc = new FT_FeatureCollection<IGeneObj>();
    IPopulation<IRailwayLine> pop = dataset.getRailwayLines();
    for (IRailwayLine obj : pop) {
      if (symbolWidth == -1 && obj.isSidetrack())
        symbolWidth = obj.getWidth() * Legend.getSYMBOLISATI0N_SCALE() / 1000;
      fc.add(obj);
    }
    CarteTopo carteTopo = new CarteTopo("planar");
    carteTopo.importClasseGeo(fc, true);
    carteTopo.rendPlanaire(0.01);
    for (IGeneObj obj : fc) {
      try {
        // test if the section has been cut by topological map
        if (obj.getCorrespondants().size() == 1) {
          dataset.getRailwayNetwork().getSections().add((INetworkSection) obj);
          continue;
        }

        // update the section geometry with the first edge of the
        // topological
        // map
        obj.setGeom(obj.getCorrespondants().get(0).getGeom());
        dataset.getRailwayNetwork().getSections().add((INetworkSection) obj);

        // loop on the other edges to make new instances
        for (int i = 1; i < obj.getCorrespondants().size(); i++) {
          ILineString newLine = (ILineString) obj.getCorrespondants().get(i)
              .getGeom();
          if (obj instanceof IRailwayLine) {
            IRailwayLine newObj = factory.createRailwayLine(newLine, 0);
            newObj.setSymbolId(obj.getSymbolId());

            // copy attributes
            newObj.copyAttributes(obj);
            pop.add(newObj);
            dataset.getRailwayLines().add(newObj);
            dataset.getRailwayNetwork().getSections().add(newObj);
          }
        }
      } catch (SecurityException e1) {
        e1.printStackTrace();
      } catch (IllegalArgumentException e1) {
        e1.printStackTrace();
      }
    }

    // now enrich the network
    NetworkEnrichment.buildTopology(dataset, dataset.getRailwayNetwork(),
        false, factory);

    for (IRailwayLine obj : pop) {
      if (((INetworkSection) obj).getInitialNode() == null) {
        continue;
      }
      if (((INetworkSection) obj).getFinalNode() == null) {
        continue;
      }
      if (!obj.isSidetrack()) {
        continue;
      }
      map.put((ArcReseau) obj.getGeoxObj(), (IRailwayLine) obj);
    }
  }

  public double getSeparationDist() {
    return separationDist;
  }

  public void setSeparationDist(double separationDist) {
    this.separationDist = separationDist;
  }

  public Set<String> getAttributeNames() {
    return attributeNames;
  }

  public void setAttributeNames(Set<String> attributeNames) {
    this.attributeNames = attributeNames;
  }

  class TriageArea extends AbstractFeature {

    private Collection<IRailwayLine> rails;
    private StrokesNetwork network;
    private Set<String> attributeNames;
    private Set<IRailwayLine> connectingRails;

    TriageArea(IPolygon geom, Collection<IRailwayLine> rails,
        Set<IRailwayLine> connectingRails, Set<String> attributeNames) {
      super(geom);
      this.attributeNames = attributeNames;
      this.rails = rails;
      this.connectingRails = connectingRails;
    }

    @Override
    public IFeature cloneGeom() throws CloneNotSupportedException {
      return null;
    }

    private void computeStrokes() {
      network = new StrokesNetwork();
      for (ArcReseau arc : map.keySet()) {
        if (rails.contains(map.get(arc)))
          network.getFeatures().add(arc);
      }
      network.buildStrokes(attributeNames, 112.5, 45.0, true);
    }

    /**
     * Get the longest of the sidetrack strokes that is connected to the main
     * railroad network.
     * @return
     */
    private Stroke getLongestConnectingStroke() {
      Stroke longest = null;
      double max = 0.0;
      for (Stroke stroke : network.getStrokes()) {
        boolean connecting = false;
        for (ArcReseau arc : stroke.getFeatures()) {
          if (connectingRails.contains(map.get(arc))) {
            connecting = true;
            break;
          }
        }

        if (!connecting)
          continue;

        double length = stroke.getLength();
        if (length > max) {
          max = length;
          longest = stroke;
        }
      }
      return longest;
    }
  }
}
