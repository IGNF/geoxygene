/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.network.NetworkSectionType;
import fr.ign.cogit.cartagen.core.genericschema.road.IDeadEndGroup;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.graph.Graph;
import fr.ign.cogit.cartagen.graph.GraphPath;
import fr.ign.cogit.cartagen.graph.INode;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;

public class DeadEndGroup extends GeneObjDefault implements IDeadEndGroup {
  private HashSet<INetworkSection> features;
  private INetworkSection root;
  private HashSet<INetworkSection> leafs;
  private double length, totalLength;
  private DeadEndType type;
  private boolean access;

  // dead end type enumeration
  public enum DeadEndType {
    SIMPLE, TREE, NOT_DEFINED, TURN, COMPLEX
  }

  public HashSet<INetworkSection> getFeatures() {
    return this.features;
  }

  public void setFeatures(HashSet<INetworkSection> features) {
    this.features = features;
  }

  public INetworkSection getRoot() {
    return this.root;
  }

  public void setRoot(INetworkSection root) {
    this.root = root;
  }

  public HashSet<INetworkSection> getLeafs() {
    return this.leafs;
  }

  public void setLeafs(HashSet<INetworkSection> leafs) {
    this.leafs = leafs;
  }

  public double getLength() {
    return this.length;
  }

  public void setLength(double length) {
    this.length = length;
  }

  public double getTotalLength() {
    return this.totalLength;
  }

  public void setTotalLength(double totalLength) {
    this.totalLength = totalLength;
  }

  public DeadEndType getType() {
    return this.type;
  }

  public void setType(DeadEndType type) {
    this.type = type;
  }

  public boolean isAccess() {
    return this.access;
  }

  public void setAccess(boolean access) {
    this.access = access;
  }

  /**
   * <p>
   * Compute the dead end group length that is the longest of the shortest paths
   * between the root and a leaf of the group. For a simple dead end, it is the
   * total length of the features.
   * 
   */
  private void computeLength() {
    // simple case
    if (this.type.equals(DeadEndType.SIMPLE)) {
      this.length = 0.0;
      for (INetworkSection a : this.features) {
        this.length += a.getGeom().length();
      }
      return;
    }

    // dans les autres cas, il faut parcourir les extrémités et calculer des
    // plus courts chemins avec la graine. Ici, le plus court chemin est entre
    // des arcs et pas des noeuds donc on utilise un graphe dual
    // on commence par créer le graphe dual
    Graph graph = new Graph("dead_end_graph" + this.root.toString(), false,
        this.features);

    // we search through the leafs to compute shortest pathes to the root
    INetworkNode rootNode = this.getRootNode();
    for (INetworkSection leaf : this.leafs) {
      // first, easy case of a single road dead end
      if (this.root.equals(leaf)) {
        this.length = this.root.getGeom().length();
        continue;
      }
      // we compute the shortest path between leaf and root
      // first get the initial nodes for root and leaf
      INode ini = graph.getNodeFromGeoObj(rootNode);
      INode fin = graph.getNodeFromGeoObj(this.getLeafNode(leaf));
      GraphPath sp = graph.computeDijkstraShortestPath(ini, fin);

      // update length if needed
      if (sp.getLength() > this.length) {
        this.length = sp.getLength();
      }
    }// boucle sur les extrémités
  }

  public INetworkNode getRootNode() {
    INetworkNode start = this.root.getInitialNode();
    INetworkNode end = this.root.getFinalNode();
    Collection<INetworkSection> edges = start.getInSections();
    edges.addAll(start.getOutSections());
    edges.removeAll(this.features);
    if (edges.size() > 0) {
      return start;
    }
    return end;
  }

  public INetworkNode getLeafNode(INetworkSection leaf) {
    INetworkNode start = leaf.getInitialNode();
    INetworkNode end = leaf.getFinalNode();
    INetworkNode rootNode = this.getRootNode();
    if (start.equals(rootNode)) {
      return end;
    }
    if (end.equals(rootNode)) {
      return start;
    }
    if (start.getInSections().size() + start.getOutSections().size() < 2) {
      return start;
    }
    return end;
  }

  /**
   * <p>
   * Constructor that builds a dead end group from one element of the group and
   * the set of network edges marked as belonging to dead ends. Be careful, the
   * dead ends of the extorior face are not built.
   * @param obj : the network edge used to build the group
   * @param deadEnds : the set of network edges marked as belonging to dead
   *          ends.
   * 
   */
  public DeadEndGroup(INetworkSection obj,
      Collection<? extends INetworkSection> deadEnds) {
    this.features = new HashSet<INetworkSection>();
    this.leafs = new HashSet<INetworkSection>();
    this.type = DeadEndType.NOT_DEFINED;
    this.length = 0.0;
    // put obj in the stack
    Stack<INetworkSection> stack = new Stack<INetworkSection>();
    stack.add(obj);
    // as long as the stack is not empty, continue
    while (!stack.empty()) {
      INetworkSection edge = stack.pop();
      // add edge to the group features
      this.features.add(edge);
      edge.setDeadEnd(true);
      // look for dead ends connected to edge at start node
      INetworkNode start = edge.getInitialNode();
      Collection<INetworkSection> connected = new HashSet<INetworkSection>();
      connected.addAll(start.getInSections());
      connected.addAll(start.getOutSections());
      connected.remove(edge);
      if (connected.size() == 0) {
        // edge is a leaf
        this.leafs.add(edge);
      }

      // now filter to keep only dead ends
      boolean isRoot = false;
      for (INetworkSection neigh : connected) {
        if (!deadEnds.contains(neigh)) {
          // edge is the current root
          this.root = edge;
          isRoot = true;
          break;
        }
      }

      if (!isRoot) {
        for (INetworkSection neigh : connected) {
          // if neigh is already in the stack, continue
          if (stack.contains(neigh)) {
            continue;
          }
          // if neigh is already a group feature, continue
          if (this.features.contains(neigh)) {
            continue;
          }

          // arrived here, neigh is a new feature of the group
          stack.add(neigh);
        }
      }

      // do the same on end side
      // System.out.println(edge);
      INetworkNode end = edge.getFinalNode();
      connected.clear();
      connected.addAll(end.getInSections());
      connected.addAll(end.getOutSections());
      connected.remove(edge);
      if (connected.size() == 0) {
        // edge is a leaf
        this.leafs.add(edge);
      }

      // now filter to keep only dead ends
      isRoot = false;
      for (INetworkSection neigh : connected) {
        if (!deadEnds.contains(neigh)) {
          // edge is the current root
          this.root = edge;
          isRoot = true;
          break;
        }
      }

      if (!isRoot) {
        for (INetworkSection neigh : connected) {
          // if neigh is already in the stack, continue
          if (stack.contains(neigh)) {
            continue;
          }
          // if neigh is already a group feature, continue
          if (this.features.contains(neigh)) {
            continue;
          }

          // arrived here, neigh is a new feature of the group
          stack.add(neigh);
        }
      }
    }
    for (INetworkSection e : this.features) {
      this.totalLength += e.getGeom().length();
    }
    // case with an isolated edge
    if (this.root == null) {
      this.root = this.features.iterator().next();
    }
    this.computeLength();
    this.makeGeometry();
  }

  /**
   * Builds a set of dead end groups from a road network, using the faces method
   * from Cécile Duchêne. The roads have to be structured as networks with
   * nodes. A border line of the street network can be added to cut the outside
   * sections
   * 
   * @param roads the roads in which dead ends are searched.
   * @return a set of DeadEndGroup objects
   * @author GTouya
   */

  public static HashSet<DeadEndGroup> buildFromRoads(
      IFeatureCollection<IRoadLine> roads) {
    return DeadEndGroup.buildFromRoads(roads, null);
  }

  /**
   * Builds a set of dead end groups from a road network, using the faces method
   * from Cécile Duchêne. The roads have to be structured as networks with
   * nodes. A border line of the street network can be added to cut the outside
   * sections
   * 
   * @param roads the roads in which dead ends are searched.
   * @param borderLine the outline of the street network
   * @return a set of DeadEndGroup objects
   * @author GTouya
   */

  public static HashSet<DeadEndGroup> buildFromRoads(
      IFeatureCollection<IRoadLine> roads, IGeometry border) {

    // Carte topo creation with road sections
    CarteTopo carteTopo = new CarteTopo("deadEnds");
    carteTopo.importClasseGeo(roads);

    // Addition of the city centre contour if existing
    ILineString borderLine = null;
    if (border != null) {
      IFeatureCollection<DefaultFeature> contours = new FT_FeatureCollection<DefaultFeature>();
      DefaultFeature contourVille = new DefaultFeature();
      if (border instanceof IPolygon) {
        borderLine = ((IPolygon) border).exteriorLineString();
      } else if (border instanceof ILineString) {
        borderLine = (ILineString) border;
      }
      contourVille.setGeom(borderLine);
      contours.add(contourVille);
      carteTopo.importClasseGeo(contours, true);
    }

    // Computation of the topology
    carteTopo.creeNoeudsManquants(0.001);
    carteTopo.rendPlanaire(1.0);
    carteTopo.creeTopologieFaces();

    HashSet<DeadEndGroup> deadEndGroups = new HashSet<DeadEndGroup>();

    // First get all the roads that belong to a dead end group
    HashSet<IRoadLine> deadEnds = new HashSet<IRoadLine>();
    // loop on the faces of the topological map
    for (Face f : carteTopo.getListeFaces()) {

      // first find out if the block is inside another block, to detect rackets
      HashSet<Face> neighFaces = new HashSet<Face>();
      for (Noeud n : f.noeuds()) {
        neighFaces.addAll(n.faces());
      }
      if (carteTopo.getListeFaces().size() > 2 && neighFaces.size() < 3) {
        // it's a hole and all the roads intersecting the face are dead ends
        deadEnds.addAll(roads.select(f.getGeometrie()));
        continue;
      }

      // loop on the roads to find the interior ones
      for (Arc arc : f.getArcsPendants()) {
        if (!(arc.getCorrespondant(0) instanceof IRoadLine)) {
          continue;
        }
        // Ne pas tenir compte des routes sortant de la ville
        if (borderLine != null
            && arc.getCorrespondant(0).getGeom().intersects(borderLine)) {
          continue;
        }
        deadEnds.add((IRoadLine) arc.getCorrespondant(0));
      }
    }
    // now build the dead end groups from the dead end roads
    Stack<IRoadLine> deadEndStack = new Stack<IRoadLine>();
    deadEndStack.addAll(deadEnds);
    while (!deadEndStack.isEmpty()) {
      DeadEndGroup group = new DeadEndGroup(deadEndStack.pop(), deadEnds);

      deadEndGroups.add(group);
      deadEndStack.removeAll(group.features);
    }

    return deadEndGroups;

  }

  /**
   * Builds a set of dead end groups from a road network, using the faces method
   * from Cécile Duchêne. The roads have to be structured as networks with
   * nodes. A border line of the street network can be added to cut the outside
   * sections
   * 
   * @param roads the roads in which dead ends are searched.
   * @param borderLine the outline of the street network
   * @return a set of DeadEndGroup objects
   * @author GTouya
   */

  public static HashSet<DeadEndGroup> buildFromRoads(
      IFeatureCollection<IRoadLine> roads, IGeometry border, CarteTopo carteTopo) {

    // Addition of the city centre contour if existing
    ILineString borderLine = null;
    if (border instanceof IPolygon) {
      borderLine = ((IPolygon) border).exteriorLineString();
    } else if (border instanceof ILineString) {
      borderLine = (ILineString) border;
    }

    HashSet<DeadEndGroup> deadEndGroups = new HashSet<DeadEndGroup>();

    // First get all the roads that belong to a dead end group
    HashSet<IRoadLine> deadEnds = new HashSet<IRoadLine>();
    // loop on the faces of the topological map
    for (Face f : carteTopo.getListeFaces()) {
      if (f.isInfinite()) {
        continue;
      }

      // first find out if the block is inside another block, to detect rackets
      HashSet<Face> neighFaces = new HashSet<Face>();
      for (Noeud n : f.noeuds()) {
        neighFaces.addAll(n.faces());
      }
      if (carteTopo.getListeFaces().size() > 2 && neighFaces.size() < 3) {
        // it's a hole and all the roads intersecting the face are dead ends
        deadEnds.addAll(roads.select(f.getGeometrie()));
        continue;
      }

      // loop on the roads to find the interior ones
      for (Arc arc : f.getArcsPendants()) {
        if (!(arc.getCorrespondant(0) instanceof IRoadLine)) {
          continue;
        }
        // Ne pas tenir compte des routes sortant de la ville
        if (borderLine != null
            && arc.getCorrespondant(0).getGeom().intersects(borderLine)) {
          continue;
        }
        deadEnds.add((IRoadLine) arc.getCorrespondant(0));
      }
    }
    // now build the dead end groups from the dead end roads
    Stack<IRoadLine> deadEndStack = new Stack<IRoadLine>();
    deadEndStack.addAll(deadEnds);
    while (!deadEndStack.isEmpty()) {
      DeadEndGroup group = new DeadEndGroup(deadEndStack.pop(), deadEnds);

      deadEndGroups.add(group);
      deadEndStack.removeAll(group.features);
    }

    return deadEndGroups;

  }

  /**
   * Builds a set of dead end groups from a town, using the blocks inside the
   * town PAS AU POINT !
   * @param ITownAgent the town being computed
   * @return a set of DeadEndGroup objects
   */

  // public static HashSet<DeadEndGroup> buildFromTown(ITownAgent town){
  //
  // HashSet<DeadEndGroup> deadEndGroups = new HashSet<DeadEndGroup>();
  //
  // // First get all the roads that belong to a dead end group
  // HashSet<IRoadLine> deadEnds = new HashSet<IRoadLine>();
  // // loop on the blocks of the town
  // for(IBlockAgent block: town.getComponents()){
  // if (!town.getGeom().contains(block.getGeom())) continue;
  // // first find out if the block is inside another block, to detect rackets
  // boolean isARacket = false;
  // for(IBlockAgent block2: town.getComponents()){
  // if (!town.getGeom().contains(block2.getGeom())) continue;
  // if (block.equals(block2)) continue;
  // if (block2.getGeom().convexHull().buffer(-5.0).contains(block.getGeom())) {
  // for (ISectionAgent section: block.getSectionAgents()) {
  // if (!(section.getFeature() instanceof IRoadLine)) continue;
  // deadEnds.add((IRoadLine)section.getFeature());
  // }
  // isARacket = true;
  // CartagenApplication.getInstance().getFrame().getLayerManager().addToGeometriesPool(block.getGeom());
  // break;
  // }
  // }
  // if (isARacket) continue;
  //
  // // loop on the block roads to find the interior ones
  // for (ISectionAgent section: block.getSectionAgents()) {
  // if (!(section.getFeature() instanceof IRoadLine)) continue;
  // IPoint pointInitial = new GM_Point(section.getGeom().coord().get(0));
  // IPoint pointFinal = new
  // GM_Point(section.getGeom().coord().get(section.getGeom().coord().size()-1));
  // if ( block.getGeom().contains(pointInitial.buffer(1.0)) ||
  // block.getGeom().contains(pointFinal.buffer(1.0)) )
  // deadEnds.add((IRoadLine)section.getFeature());
  // }
  //
  //
  // }
  //
  // for (IRoadLine road: deadEnds)
  // CartagenApplication.getInstance().getFrame().getLayerManager().addToGeometriesPool(road.getGeom());
  //
  // // now build the dead end groups from the dead end roads
  // Stack<IRoadLine> deadEndStack = new Stack<IRoadLine>();
  // deadEndStack.addAll(deadEnds);
  // while(!deadEndStack.isEmpty()){
  // DeadEndGroup group = new DeadEndGroup(deadEndStack.pop(),deadEnds);
  //
  // deadEndGroups.add(group);
  // deadEndStack.removeAll(group.features);
  // }
  //
  // return deadEndGroups;
  //
  // }

  public static void classifyNetworkSections(
      IFeatureCollection<INetworkSection> roads, IPolygon borderLine) {
    // Carte topo creation with road sections
    CarteTopo carteTopo = new CarteTopo("deadEnds");
    carteTopo.importClasseGeo(roads);

    // Addition of the city centre contour if existing
    if (borderLine != null) {
      IFeatureCollection<DefaultFeature> contours = new FT_FeatureCollection<DefaultFeature>();
      DefaultFeature contourVille = new DefaultFeature();
      contourVille.setGeom(borderLine.exteriorLineString());
      contours.add(contourVille);
      carteTopo.importClasseGeo(contours, true);
    }

    // Computation of the topology
    carteTopo.creeNoeudsManquants(0.001);
    carteTopo.rendPlanaire(1.0);
    carteTopo.creeTopologieFaces();

    // Removal of the exterior face of the city
    if (borderLine != null) {
      ArrayList<Face> exteriorFace = new ArrayList<Face>();
      for (Face f : carteTopo.getListeFaces()) {
        if (!borderLine.buffer(5.0).contains(f.getGeom())) {
          exteriorFace.add(f);
          break;
        }
      }
      carteTopo.enleveFaces(exteriorFace);
    }

    HashSet<DeadEndGroup> deadEndGroups = new HashSet<DeadEndGroup>();

    // First get all the roads that belong to a dead end group
    HashSet<INetworkSection> deadEnds = new HashSet<INetworkSection>();
    // loop on the faces of the topological map
    for (Face f : carteTopo.getListeFaces()) {

      // first find out if the block is inside another block, to detect rackets
      HashSet<Face> neighFaces = new HashSet<Face>();
      for (Noeud n : f.noeuds()) {
        neighFaces.addAll(n.faces());
      }
      if (carteTopo.getListeFaces().size() > 2 && neighFaces.size() < 3) {
        // it's a hole and all the roads intersecting the face are dead ends
        deadEnds.addAll(roads.select(f.getGeometrie()));
      }

      // loop on the roads to find the interior ones
      for (Arc arc : f.getArcsPendants()) {
        if (!(arc.getCorrespondant(0) instanceof INetworkSection)) {
          continue;
        }
        // Ne pas tenir compte des routes sortant de la ville
        if (borderLine != null
            && arc.getCorrespondant(0).getGeom()
                .intersects(borderLine.exteriorLineString())) {
          continue;
        }
        INetworkSection section = (INetworkSection) arc.getCorrespondant(0);
        // test if the arc is completely inside the face
        if (!f.getGeom().contains(section.getGeom())) {
          // the section can either be a double dead end or an hybrid section
          Stack<Object> connected = new Stack<Object>();
          connected.addAll(arc.arcPrecedentDebut());
          connected.addAll(arc.arcPrecedentFin());
          connected.addAll(arc.arcSuivantDebut());
          connected.addAll(arc.arcSuivantFin());
          HashSet<Arc> treated = new HashSet<Arc>();
          treated.add(arc);
          boolean connection = false;
          boolean continueLoop = true;
          while (continueLoop) {
            if (connected.size() == 0) {
              break;
            }
            Arc connectedArc = (Arc) connected.pop();
            if (treated.contains(connectedArc)) {
              continue;
            }
            if (!section.equals(connectedArc.getCorrespondant(0))) {
              connection = true;
              continueLoop = false;
            } else {
              treated.add(connectedArc);
              connected.addAll(connectedArc.arcPrecedentDebut());
              connected.addAll(connectedArc.arcPrecedentFin());
              connected.addAll(connectedArc.arcSuivantDebut());
              connected.addAll(connectedArc.arcSuivantFin());
            }
          }

          // if connection is true, then the section is a hybrid section
          if (connection) {
            section.setNetworkSectionType(NetworkSectionType.HYBRID);
            // else, the section is a double dead end section
          } else {
            section.setNetworkSectionType(NetworkSectionType.DOUBLE_DEAD_END);
          }
          continue;
        }
        deadEnds.add(section);
      }
      // classify the sections that form the face as "normal"
      for (Arc arc : f.getArcsDirects()) {
        ((INetworkSection) arc.getCorrespondant(0))
            .setNetworkSectionType(NetworkSectionType.NORMAL);
      }
    }

    // now build the dead end groups from the dead end roads
    Stack<INetworkSection> deadEndStack = new Stack<INetworkSection>();
    deadEndStack.addAll(deadEnds);
    while (!deadEndStack.isEmpty()) {
      DeadEndGroup group = new DeadEndGroup(deadEndStack.pop(), deadEnds);

      deadEndGroups.add(group);
      deadEndStack.removeAll(group.features);
    }

    // the unusual type of roads are the one belonging to a dead end group
    // so loop on the dead end groups
    for (DeadEndGroup group : deadEndGroups) {
      // 1. unitary groups: either a direct dead end or an isolated section
      if (group.getFeatures().size() == 1) {
        if (group.getFeaturesConnectedToRoot().size() == 0) {
          // 1.1. it's an isolated section
          (group.getFeatures().iterator().next())
              .setNetworkSectionType(NetworkSectionType.ISOLATED);
        } else {
          // 1.2. it's a simple direct dead end
          (group.getFeatures().iterator().next())
              .setNetworkSectionType(NetworkSectionType.DIRECT_DEAD_END);
        }
        continue;
      }

      // 2. the sections of the group are either bridges or indirect dead ends
      for (INetworkSection section : group.getLeafs()) {
        // 2.1. the leafs are indirect dead ends
        section.setNetworkSectionType(NetworkSectionType.INDIRECT_DEAD_END);
      }
      // 2.2. the remaining sections are bridges
      for (INetworkSection section : group.getFeatures()) {
        if (group.getLeafs().contains(section)) {
          continue;
        }
        section.setNetworkSectionType(NetworkSectionType.BRIDGE);
      }
    }
  }

  public void markDeadEndAsAttribute(String attributeName) {
    AttributeType att = (AttributeType) this.root.getFeatureType()
        .getFeatureAttributeByName(attributeName);
    if (att == null) {
      att = new AttributeType();
      att.setNomField(attributeName);
      att.setMemberName(attributeName);
      att.setValueType("bool");
      this.root.getFeatureType().addFeatureAttribute(att);
    }
    for (INetworkSection a : this.features) {
      a.setAttribute(att, new Boolean(true));
    }
  }

  @Override
  public IFeature getGeoxObj() {
    return null;
  }

  private void makeGeometry() {
    IMultiCurve<ICurve> complex = new GM_MultiCurve<ICurve>();
    for (INetworkSection feat : this.features) {
      complex.add(feat.getGeom());
    }
    this.setGeom(complex.convexHull());
  }

  /**
   * Get the network sections connected to the root of the dead end group.
   * 
   * @return
   * @author GTouya
   */
  public HashSet<INetworkSection> getFeaturesConnectedToRoot() {
    HashSet<INetworkSection> connected = new HashSet<INetworkSection>();

    connected.addAll(this.getRootNode().getInSections());
    connected.addAll(this.getRootNode().getOutSections());
    connected.removeAll(this.features);

    return connected;
  }
}
