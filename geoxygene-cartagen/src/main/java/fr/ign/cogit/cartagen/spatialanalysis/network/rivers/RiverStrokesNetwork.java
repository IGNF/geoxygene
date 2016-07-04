package fr.ign.cogit.cartagen.spatialanalysis.network.rivers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;

import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.StrokesNetwork;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.NoeudHydrographique;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.TronconHydrographique;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.impl.hydro.NoeudHydrographiqueImpl;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class RiverStrokesNetwork extends StrokesNetwork {

  private static Logger logger = Logger
      .getLogger(RiverStrokesNetwork.class.getName());
  private Set<NoeudHydrographique> sources, sinks;
  private Set<RiverIsland> simpleIslands;
  private Set<RiverIslandGroup> complexIslands;
  private Map<ArcReseau, Integer> strahlerOrders;

  public RiverStrokesNetwork() {
    super();
    this.sources = new HashSet<NoeudHydrographique>();
    this.sinks = new HashSet<NoeudHydrographique>();
    this.simpleIslands = new HashSet<RiverIsland>();
    this.complexIslands = new HashSet<RiverIslandGroup>();
    this.setStrahlerOrders(new HashMap<ArcReseau, Integer>());
  }

  public RiverStrokesNetwork(Collection<ArcReseau> features) {
    super(features);
    this.sources = new HashSet<NoeudHydrographique>();
    this.sinks = new HashSet<NoeudHydrographique>();
    this.simpleIslands = new HashSet<RiverIsland>();
    this.complexIslands = new HashSet<RiverIslandGroup>();
    this.setStrahlerOrders(new HashMap<ArcReseau, Integer>());
  }

  public Set<NoeudHydrographique> getSources() {
    return this.sources;
  }

  public void setSources(Set<NoeudHydrographique> sources) {
    this.sources = sources;
  }

  public Set<NoeudHydrographique> getSinks() {
    return this.sinks;
  }

  public void setSinks(Set<NoeudHydrographique> sinks) {
    this.sinks = sinks;
  }

  public Set<RiverIsland> getSimpleIslands() {
    return this.simpleIslands;
  }

  public void setSimpleIslands(Set<RiverIsland> simpleIslands) {
    this.simpleIslands = simpleIslands;
  }

  public Set<RiverIslandGroup> getComplexIslands() {
    return this.complexIslands;
  }

  public void setComplexIslands(Set<RiverIslandGroup> complexIslands) {
    this.complexIslands = complexIslands;
  }

  public Map<ArcReseau, Integer> getStrahlerOrders() {
    return this.strahlerOrders;
  }

  public void setStrahlerOrders(Map<ArcReseau, Integer> strahlerOrders) {
    this.strahlerOrders = strahlerOrders;
  }

  /**
   * Identifies and stores the sources and sinks of the river strokes network.
   */
  public void findSourcesAndSinks() {
    for (ArcReseau arc : this.getFeatures()) {
      // treat initial node
      NoeudHydrographique node = (NoeudHydrographique) arc.getNoeudInitial();
      if (this.getSources().contains(node)) {
        continue;
      }
      if (node == null) {
        node = new NoeudHydrographiqueImpl();
        node.setGeom(arc.getGeom().coord().get(0).toGM_Point());
        arc.setNoeudInitial(node);
        node.getArcsSortants().add(arc);
      }
      if (node.getArcsEntrants().size() != 0) {
        continue;
      }
      this.getSources().add(node);

      // treat final node
      node = (NoeudHydrographique) arc.getNoeudFinal();
      if (this.getSinks().contains(node)) {
        continue;
      }
      if (node == null) {
        node = new NoeudHydrographiqueImpl();
        node.setGeom(arc.getGeom().coord().get(0).toGM_Point());
        arc.setNoeudFinal(node);
        node.getArcsEntrants().add(arc);
      }
      if (node.getArcsSortants().size() != 0) {
        continue;
      }
      this.getSinks().add(node);
    }
  }

  public void findSimpleIslands(double areaThreshold) {
    IPopulation<ArcReseau> pop = new Population<ArcReseau>();
    pop.addAll(this.getFeatures());
    CarteTopo carteTopo = new CarteTopo("make planar");
    carteTopo.importClasseGeo(pop, true);
    carteTopo.rendPlanaire(1.0);
    carteTopo.creeTopologieFaces();

    for (Face face : carteTopo.getListeFaces()) {
      if (face.getGeom().area() < areaThreshold) {
        Set<TronconHydrographique> outline = new HashSet<TronconHydrographique>();
        for (Arc arc : face.arcs()) {
          outline.add((TronconHydrographique) arc.getCorrespondant(0));
        }
        this.getSimpleIslands()
            .add(new RiverIsland((IPolygon) face.getGeom(), outline));
      }
    }
  }

  public void findComplexIslands() {
    IFeatureCollection<RiverIsland> rivers = new FT_FeatureCollection<RiverIsland>();
    rivers.addAll(this.simpleIslands);
    while (!rivers.isEmpty()) {
      RiverIsland island = rivers.get(0);
      rivers.remove(0);
      Set<RiverIsland> group = new HashSet<RiverIsland>();

      // search for neighbours
      Stack<RiverIsland> islands = new Stack<RiverIsland>();
      islands.add(island);
      while (!islands.isEmpty()) {
        RiverIsland current = islands.pop();
        group.add(current);
        Collection<RiverIsland> neighbours = rivers.select(current.getGeom());
        neighbours.removeAll(group);
        islands.addAll(neighbours);
      }

      if (group.size() > 1) {
        // build a new river island group
        this.complexIslands.add(new RiverIslandGroup(group));
        this.simpleIslands.removeAll(group);
      }
      // remove the island previously grouped from the feature collection
      rivers.removeAll(group);
    }
  }

  /**
   * Build the river strokes of {@code this} network using the method from
   * (Touya, 2007).
   */
  public void buildRiverStrokes() {

    // ****************************
    // INITIALISATION STEP
    // ****************************
    Stack<NoeudHydrographique> downstreamNodes = new Stack<NoeudHydrographique>();
    for (NoeudHydrographique source : this.getSources()) {
      // first get the downstream section
      TronconHydrographique section = (TronconHydrographique) source
          .getArcsSortants().iterator().next();
      // build a new RiverStroke with this section
      RiverStroke stroke = new RiverStroke(this, section);
      this.getStrokes().add(stroke);
      this.getGroupedFeatures().add(section);
      if (!downstreamNodes.contains(section.getNoeudFinal())) {
        downstreamNodes.add((NoeudHydrographique) section.getNoeudFinal());
      }
      // compute Strahler orders
      this.strahlerOrders.put(section, 1);
    }

    // ****************************
    // MAIN LOOP
    // ****************************
    int counter = 0;
    while (!downstreamNodes.isEmpty()) {
      // test to break the loop when it's stuck
      if (counter == downstreamNodes.size()) {
        break;
      }

      NoeudHydrographique node = downstreamNodes.pop();

      if (this.getSinks().contains(node)) {
        continue;
      }

      // check that all entering sections already belong to a stroke
      if (!this.allBelongToStroke(node.getArcsEntrants())) {
        downstreamNodes.add(0, node);
        counter++;
        continue;
      }

      // arrived there, it has to be decided which stroke is stopped and which
      // one continues.
      counter = 0;
      if (node.getArcsSortants().size() == 1) {
        TronconHydrographique downstreamSection = (TronconHydrographique) node
            .getArcsSortants().iterator().next();
        // get the upstream strokes and find the one that continues
        RiverStroke continuing = this.makeDecisionAtConfluence(node,
            downstreamSection, this.getUpstreamStrokes(node));
        System.out.println("continuing: " + continuing);
        // now extends the continuing stroke with downstreamSection
        continuing.getFeatures().add(downstreamSection);
        this.getGroupedFeatures().add(downstreamSection);

        // find the next node
        if (!downstreamNodes.contains(downstreamSection.getNoeudFinal())) {
          downstreamNodes
              .add((NoeudHydrographique) downstreamSection.getNoeudFinal());
        }

        // compute Strahler order
        List<Integer> orders = new ArrayList<Integer>();
        for (ArcReseau arc : node.getArcsEntrants()) {
          orders.add(this.strahlerOrders.get(arc));
        }
        this.strahlerOrders.put(downstreamSection,
            this.computeStrahlerAtConfluence(orders));

      } else if (node.getArcsSortants().size() > 1) {
        // braided stream case
        if (node.getArcsEntrants().size() == 1) {
          // normal case
          // the main branch has to be found
          ArcReseau upStream = node.getArcsEntrants().iterator().next();
          ArcReseau mainBranch = this.manageBraidedConfluence(node, upStream,
              downstreamNodes);

          // build new RiverStrokes with remaining branches
          Collection<ArcReseau> remainingBranches = new HashSet<ArcReseau>(
              node.getArcsSortants());
          remainingBranches.remove(mainBranch);
          for (ArcReseau branch : remainingBranches) {
            RiverStroke stroke = new RiverStroke(this, branch);
            stroke.setBraided(true);
            this.getStrokes().add(stroke);
            this.getGroupedFeatures().add(branch);
            if (!downstreamNodes.contains(branch.getNoeudFinal())) {
              downstreamNodes.add(0,
                  (NoeudHydrographique) branch.getNoeudFinal());
            }
            // compute Strahler orders
            this.strahlerOrders.put(branch, 1);
          }
        } else {
          // complex case with braids at a confluence point
          Set<RiverStroke> upstreamStrokes = this.getUpstreamStrokes(node);
          Collection<ArcReseau> remainingBranches = new HashSet<ArcReseau>(
              node.getArcsSortants());
          RiverStroke unbraided = this.getNonBraidedStroke(upstreamStrokes);

          if (unbraided != null) {
            upstreamStrokes.remove(unbraided);
            ArcReseau mainBranch = this.manageBraidedConfluence(node,
                unbraided.getLastFeat(), downstreamNodes);
            remainingBranches.remove(mainBranch);
          }
          for (ArcReseau branch : remainingBranches) {
            RiverStroke stroke = new RiverStroke(this, branch);
            stroke.setBraided(true);
            this.getStrokes().add(stroke);
            this.getGroupedFeatures().add(branch);
            if (!downstreamNodes.contains(branch.getNoeudFinal())) {
              downstreamNodes.add(0,
                  (NoeudHydrographique) branch.getNoeudFinal());
            }
            // compute Strahler orders
            this.strahlerOrders.put(branch, 1);
          }
        }
      }
    }
    for (NoeudHydrographique n : downstreamNodes) {
      RiverStrokesNetwork.logger.fine("noeud restant : " + n);
    }
  }

  private ArcReseau manageBraidedConfluence(NoeudHydrographique node,
      ArcReseau upStream, Stack<NoeudHydrographique> downstreamNodes) {
    ArcReseau mainBranch = null;
    double min = 1.0;
    for (ArcReseau branch : node.getArcsSortants()) {
      double angle = CommonAlgorithmsFromCartAGen.angleBetween2Lines(
          (ILineString) upStream.getGeom(), (ILineString) branch.getGeom());
      if (Math.cos(angle) < min) {
        min = Math.cos(angle);
        mainBranch = branch;
      }
    }
    if (mainBranch == null) {
      return null;
    }

    // continue the upstream stroke with mainBranch
    System.out.println(node);
    System.out.println(node.getArcsEntrants());
    RiverStroke upstreamStroke = this.getUpstreamStrokes(node).iterator()
        .next();
    upstreamStroke.getFeatures().add(mainBranch);
    this.getGroupedFeatures().add(mainBranch);
    // find the next node
    if (!downstreamNodes.contains(mainBranch.getNoeudFinal())) {
      downstreamNodes.add(0, (NoeudHydrographique) mainBranch.getNoeudFinal());
    }
    this.strahlerOrders.put(mainBranch, this.strahlerOrders.get(upStream));

    return mainBranch;
  }

  /**
   * First, decision is based on river name. If there is no name preferrence,
   * the length of upstream stroke makes the decision (the longest continues).
   * If lengthes are close (less than 25% difference), the angle is used to make
   * the decision.
   * @param node
   * @param downstreamSection
   * @param upstreamStrokes
   * @return
   */
  private RiverStroke makeDecisionAtConfluence(NoeudHydrographique node,
      TronconHydrographique downstreamSection,
      Set<RiverStroke> upstreamStrokes) {
    // if there is only one upstream river, it continues
    if (upstreamStrokes.size() == 1) {
      return upstreamStrokes.iterator().next();
    }

    // first, make a decision on braided strokes
    RiverStroke unbraidedStroke = this.getNonBraidedStroke(upstreamStrokes);
    if (unbraidedStroke != null) {
      return unbraidedStroke;
    }

    // first, make a decision on river name
    if (!downstreamSection.getNom().equals("")) {
      for (RiverStroke stroke : upstreamStrokes) {
        if (downstreamSection.getNom().equals(stroke.getLastName())) {
          return stroke;
        }
      }
    }

    // arrived here, decision is made on stroke length
    RiverStroke longest = null;
    double maxLength = 0.0;
    double lengthDiff = 0.0;
    for (RiverStroke stroke : upstreamStrokes) {
      double length = stroke.getLength();
      if (length > maxLength) {
        longest = stroke;
        lengthDiff = maxLength / length;
        maxLength = length;
      } else {
        double diff = length / maxLength;
        if (diff < lengthDiff) {
          lengthDiff = diff;
        }
      }
    }
    if (lengthDiff >= 0.25) {
      return longest;
    }

    // arrived here, make difference on angle difference
    RiverStroke best = null;
    double min = 1.0;
    for (RiverStroke stroke : upstreamStrokes) {
      double angle = CommonAlgorithmsFromCartAGen.angleBetween2Lines(
          stroke.getLastLine(), (ILineString) downstreamSection.getGeom());
      if (Math.cos(angle) < min) {
        min = Math.cos(angle);
        best = stroke;
      }
    }

    return best;
  }

  private RiverStroke getNonBraidedStroke(Set<RiverStroke> upstreamStrokes) {
    int nb = 0;
    RiverStroke unbraided = null;
    for (RiverStroke stroke : upstreamStrokes) {
      if (stroke.isBraided()) {
        continue;
      }
      unbraided = stroke;
      nb++;
    }
    if (nb == 1) {
      return unbraided;
    }
    return null;
  }

  /**
   * Finds if all sections given as parameters already belong to stroke.
   * @param sections
   * @return
   */
  private boolean allBelongToStroke(Collection<ArcReseau> sections) {
    for (ArcReseau section : sections) {
      if (!this.getGroupedFeatures().contains(section)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Find the upstream strokes at a given node.
   * @param node
   * @return
   */
  private Set<RiverStroke> getUpstreamStrokes(NoeudHydrographique node) {
    Set<RiverStroke> upstreamStrokes = new HashSet<RiverStroke>();
    for (Stroke str : this.getStrokes()) {
      Set<ArcReseau> feats = new HashSet<ArcReseau>(str.getFeatures());
      feats.retainAll(node.getArcsEntrants());
      if (feats.size() != 0) {
        upstreamStrokes.add((RiverStroke) str);
      }
    }

    return upstreamStrokes;
  }

  private Integer computeStrahlerAtConfluence(List<Integer> orders) {
    if (orders.size() == 1) {
      return orders.get(0);
    }

    Integer max = Collections.max(orders);
    int nbMax = Collections.frequency(orders, max);
    if (nbMax > 1) {
      return max + 1;
    }
    return max;
  }
}
