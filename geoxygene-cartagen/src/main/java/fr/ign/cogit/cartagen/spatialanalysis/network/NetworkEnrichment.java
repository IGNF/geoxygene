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
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayNode;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.spatialanalysis.urban.UrbanEnrichment;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Static class compiling all methods used for enriching networks
 * @author JRenard
 * 
 */
public class NetworkEnrichment {
  private static Logger logger = Logger.getLogger(NetworkEnrichment.class
      .getName());

  /**
   * Enriches a network by building its topology and dealing with consequences
   * on sections, nodes and faces
   * @param net
   */
  public static void enrichNetwork(INetwork net) {

    if (NetworkEnrichment.logger.isInfoEnabled()) {
      NetworkEnrichment.logger.info("topology creation for " + net);
    }
    NetworkEnrichment.buildTopology(net, false);

    if (NetworkEnrichment.logger.isDebugEnabled()) {
      NetworkEnrichment.logger.debug("nodes importance computation for " + net);
    }

  }

  /**
   * Enriches a network by building its topology and dealing with consequences
   * on sections, nodes and faces
   * @param net
   * @param deleted true if deleted sections of the network have to be included
   */
  public static void enrichNetwork(INetwork net, boolean deleted) {

    if (NetworkEnrichment.logger.isInfoEnabled()) {
      NetworkEnrichment.logger.info("topology creation for " + net);
    }
    NetworkEnrichment.buildTopology(net, deleted);

    if (NetworkEnrichment.logger.isDebugEnabled()) {
      NetworkEnrichment.logger.debug("nodes importance computation for " + net);
    }

  }

  /**
   * Builds the topology of a network. The network is supposed to be constituted
   * of linear geometries with common extremities. This method builds the
   * topological map of the network anf its nodes
   * @param net
   * @param deleted true if deleted sections of the network have to be included
   */
  public static void buildTopology(INetwork net, boolean deleted) {

    // if necessary
    NetworkEnrichment.destroyTopology(net);

    // topo map construction
    net.setCarteTopo(new CarteTopo("cartetopo"));
    if (deleted)
      net.getCarteTopo().importClasseGeo(net.getSections(), true);
    else
      net.getCarteTopo().importClasseGeo(net.getNonDeletedSections(), true);

    if (NetworkEnrichment.logger.isInfoEnabled()) {
      NetworkEnrichment.logger.info("Nodes creation");
    }
    net.getCarteTopo().creeNoeudsManquants(1.0);

    if (NetworkEnrichment.logger.isInfoEnabled()) {
      NetworkEnrichment.logger.info("nodes merging");
    }
    net.getCarteTopo().fusionNoeuds(1.0);

    // Creates the nodes
    // The node-section topology in CartAGen is updated through the constructor
    // of nodes
    for (Noeud n : net.getCarteTopo().getPopNoeuds()) {

      if (net.getSections().get(0) instanceof IRoadLine) {
        IRoadNode roadNode = CartagenApplication.getInstance()
            .getCreationFactory().createRoadNode(n);
        IPopulation<IRoadNode> popRoad = CartAGenDoc.getInstance()
            .getCurrentDataset().getRoadNodes();
        popRoad.add(roadNode);
        CartAGenDoc.getInstance().getCurrentDataset().getRoadNetwork()
            .addNode(roadNode);
      }

      if (net.getSections().get(0) instanceof IWaterLine) {
        IWaterNode waterNode = CartagenApplication.getInstance()
            .getCreationFactory().createWaterNode(n);
        IPopulation<IWaterNode> popWater = CartAGenDoc.getInstance()
            .getCurrentDataset().getWaterNodes();
        popWater.add(waterNode);
        CartAGenDoc.getInstance().getCurrentDataset().getHydroNetwork()
            .addNode(waterNode);
      }

      if (net.getSections().get(0) instanceof IRailwayLine) {
        IRailwayNode railNode = CartagenApplication.getInstance()
            .getCreationFactory().createRailwayNode(n);
        CartAGenDoc.getInstance().getCurrentDataset().getRailwayNetwork()
            .addNode(railNode);
      }

    }

  }

  /**
   * Destructs all elements of the topology of a network: topo map, nodes and
   * faces
   * @param net
   */
  public static void destroyTopology(INetwork net) {

    if (net.getCarteTopo() != null) {
      net.getCarteTopo().nettoyer();
    }

    for (INetworkNode n : net.getNodes()) {
      n.eliminate();
    }

    net.setCarteTopo(null);
    net.getNodes().clear();

  }

  /**
   * Networks used to build network faces
   */
  private static Set<INetwork> structuringNetworks = null;

  /**
   * @return
   * @author CDuchene
   */
  public static Set<INetwork> getStructuringNetworks(CartAGenDataSet dataset) {
    NetworkEnrichment.structuringNetworks = new HashSet<INetwork>();
    NetworkEnrichment.structuringNetworks.add(dataset.getRoadNetwork());
    NetworkEnrichment.structuringNetworks.add(dataset.getHydroNetwork());
    NetworkEnrichment.structuringNetworks.add(dataset.getRailwayNetwork());
    return NetworkEnrichment.structuringNetworks;
  }

  /**
   * Constructs the network faces of a dataset based on its structuring networks
   * @param dataset
   */
  public static void buildNetworkFaces(CartAGenDataSet dataset) {

    // Deleting network faces
    dataset.eraseFacesReseau();

    // Building topological map
    CarteTopo carteTopo = NetworkEnrichment.buildNetworksTopoMap(dataset);

    // Construction of the NetworkFaceAgents
    if (NetworkEnrichment.logger.isDebugEnabled()) {
      NetworkEnrichment.logger.debug("Building the NetworkFaceAgents");
    }
    for (Face face : carteTopo.getPopFaces()) {
      // Gets the geometry of the face
      IPolygon polygon = face.getGeometrie();
      // Converts it into 2D
      try {
        polygon = (IPolygon) AdapterFactory.to2DGM_Object(polygon);
      } catch (Exception e) {
        NetworkEnrichment.logger
            .error("Failed during conversion of face geometry into 2D");
        NetworkEnrichment.logger.error(polygon.toString());
        continue;
      }
      INetworkFace netFace = CartagenApplication.getInstance()
          .getCreationFactory().createNetworkFace(polygon);
      dataset.getFacesReseau().add(netFace);
    }

    // Tidy up
    carteTopo.nettoyer();

    // Remove the large metaFace covering the whole dataset
    INetworkFace metaFace = null;
    for (IFeature mask : dataset.getMasks()) {
      for (INetworkFace face : dataset.getFacesReseau()) {
        if (face.getGeom().contains(mask.getGeom())) {
          metaFace = face;
        }
      }
    }
    if (metaFace != null) {
      dataset.getFacesReseau().remove(metaFace);
    }

  }

  /**
   * Constructs the topological map of a dataset based on its structuring
   * networks
   * @param dataset the dataset in which to construct the network faces
   */
  public static CarteTopo buildNetworksTopoMap(CartAGenDataSet dataset) {

    if (NetworkEnrichment.logger.isDebugEnabled()) {
      NetworkEnrichment.logger.debug("Building network faces...");
    }

    // Constructs a CarteTopo (topological map) and fills it in with the
    // network sections
    CarteTopo carteTopo = new CarteTopo("cartetopo");
    for (INetwork res : UrbanEnrichment.getStructuringNetworks(dataset)) {
      if (res.getSections().size() > 0) {
        carteTopo.importClasseGeo(res.getSections(), true);
      }
    }

    // Creates Nodes, etc. and makes the topological map planar, create
    // faces
    if (NetworkEnrichment.logger.isInfoEnabled()) {
      NetworkEnrichment.logger.info("creating nodes");
    }
    carteTopo.creeNoeudsManquants(1.0);
    if (NetworkEnrichment.logger.isInfoEnabled()) {
      NetworkEnrichment.logger.info("merging nodes");
    }
    carteTopo.fusionNoeuds(1.0);
    if (NetworkEnrichment.logger.isInfoEnabled()) {
      NetworkEnrichment.logger.info("filtering duplicated edges");
    }
    carteTopo.filtreArcsDoublons();
    if (NetworkEnrichment.logger.isInfoEnabled()) {
      NetworkEnrichment.logger.info("making planar");
    }
    carteTopo.rendPlanaire(1.0);
    if (NetworkEnrichment.logger.isInfoEnabled()) {
      NetworkEnrichment.logger.info("merging duplicated nodes");
    }
    carteTopo.fusionNoeuds(1.0);
    if (NetworkEnrichment.logger.isInfoEnabled()) {
      NetworkEnrichment.logger.info("filtering duplicated edges");
    }
    carteTopo.filtreArcsDoublons();
    if (NetworkEnrichment.logger.isInfoEnabled()) {
      NetworkEnrichment.logger.info("creating topological faces");
    }
    carteTopo.creeTopologieFaces();
    if (NetworkEnrichment.logger.isDebugEnabled()) {
      NetworkEnrichment.logger.debug(carteTopo.getListeFaces().size()
          + " faces found");
    }
    if (NetworkEnrichment.logger.isDebugEnabled()) {
      NetworkEnrichment.logger.debug("building spatial index on faces");
    }
    carteTopo.getPopFaces().initSpatialIndex(Tiling.class, false);

    return carteTopo;

  }

  /**
   * Raccorde tous les troncons analogues (semantiquement) et adjacents !! ALGO
   * TRES LONG ET AUX RESLULTATS INCERTAINS !! !! LUI PREFERER LA NOUVELLES
   * VERSION CI-DESSOUS (NOM EN ANGLAIS) !!
   */
  public static void agregerTronconsAnaloguesAdjacents(INetwork net) {
    INetworkSection at0, at1;

    // recupere un couple de troncons analogues adjacents
    IFeatureCollection<INetworkSection> ats = NetworkEnrichment
        .getTronconsAnaloguesAdjacents(net);

    // tant qu'il y a des troncons a raccorder, les raccorder
    while (ats != null) {
      // recupere les deux troncons a raccorder
      at0 = ats.get(0);
      at1 = ats.get(1);

      if (NetworkEnrichment.logger.isInfoEnabled()) {
        NetworkEnrichment.logger.info("raccordement de");
        NetworkEnrichment.logger.info(" geom at0 " + at0.getGeom());
        NetworkEnrichment.logger.info(" geom at1 " + at1.getGeom());
      }

      // change la geometrie de at0
      IDirectPositionList coords0 = at0.getGeom().coord();
      IDirectPositionList coords1 = at1.getGeom().coord();

      // construit la geometrie union
      IDirectPosition c00 = coords0.get(0), c0n = coords0
          .get(coords0.size() - 1);
      IDirectPosition c10 = coords1.get(0), c1n = coords1
          .get(coords1.size() - 1);

      if (c00.equals(c10)) {
        coords0.inverseOrdre();
      } else if (c0n.equals(c10)) {
      } else if (c00.equals(c1n)) {
        coords0.inverseOrdre();
        coords1.inverseOrdre();
      } else if (c0n.equals(c1n)) {
        coords1.inverseOrdre();
      } else {
        NetworkEnrichment.logger
            .warn("Warning: troncons se touchent bizarrement " + at0.getGeom()
                + "   " + at1.getGeom());
      }

      for (int i = 1; i < coords1.size(); i++) {
        coords0.add(coords1.get(i));
      }

      GM_LineString ls = new GM_LineString(coords0);

      if (NetworkEnrichment.logger.isInfoEnabled()) {
        NetworkEnrichment.logger.info("union: " + ls);
      }

      // affecte la nouvelle geometrie
      at0.setGeom(ls);

      // supprime at1
      at1.eliminate();

      // construire topologie
      NetworkEnrichment.buildTopology(net, false);

      // aux suivants
      ats = NetworkEnrichment.getTronconsAnaloguesAdjacents(net);
    }
  }

  /**
   * recupere un couple de troncons adjacents analogues eventuel suppose d'avoir
   * construit le topologie avec {@link #construireTopologie()}
   * @return
   */
  private static IFeatureCollection<INetworkSection> getTronconsAnaloguesAdjacents(
      INetwork net) {

    // parcours des noeuds
    for (INetworkNode node : net.getNodes()) {
      // ne traite pas les noeuds de degre non 2
      if (node.getDegree() != 2) {
        continue;
      }

      // recupere la liste des troncons non supprimes relies a ac
      FT_FeatureCollection<INetworkSection> ats = new FT_FeatureCollection<INetworkSection>();

      for (INetworkSection at : node.getInSections()) {
        if (!at.isDeleted()) {
          ats.add(at);
        }
      }
      for (INetworkSection at : node.getOutSections()) {
        if (!at.isDeleted()) {
          ats.add(at);
        }
      }

      // renvoit la liste des troncons uniquement s'ils sont analogues
      if (ats.get(0).isAnalog(ats.get(1))) {
        return ats;
      }
    }

    // aucun couple trouve
    return null;
  }

  /**
   * Aggregates all analog adjacent sections in a network, taking into account
   * their semantics It results in eliminating all nodes of degree 2 and
   * recomputing the topological map
   * @author JRenard
   */
  public static void aggregateAnalogAdjacentSections(INetwork net) {

    HashSet<INetworkSection> sectionsToRemove = new HashSet<INetworkSection>();

    for (INetworkNode node : net.getNodes()) {

      // Test of the incident sections of the node
      ArrayList<INetworkSection> sections = new ArrayList<INetworkSection>();
      sections.addAll(node.getInSections());
      sections.addAll(node.getOutSections());
      if (sections.size() != 2) {
        // Not a simple node, doesn't have to be filtered
        continue;
      }

      if (sections.get(0).getImportance() != sections.get(1).getImportance()) {
        // Different roads should not be aggregated
        continue;
      }

      // Update of the link between sections and nodes
      INetworkNode otherNode = sections.get(1).getInitialNode();
      if (otherNode.equals(node)) {
        otherNode = sections.get(1).getFinalNode();
        otherNode.getInSections().remove(sections.get(1));
      } else {
        otherNode.getOutSections().remove(sections.get(1));
      }
      if (node.getOutSections().contains(sections.get(0))) {
        sections.get(0).setInitialNode(otherNode);
        otherNode.getOutSections().add(sections.get(0));
      } else {
        sections.get(0).setFinalNode(otherNode);
        otherNode.getInSections().add(sections.get(0));
      }

      // Concordance of the two geometries
      IPoint section0InitialPoint = new GM_Point(sections.get(0).getGeom()
          .coord().get(0));
      IPoint section0FinalPoint = new GM_Point(sections.get(0).getGeom()
          .coord().get(sections.get(0).getGeom().coord().size() - 1));
      IPoint section1InitialPoint = new GM_Point(sections.get(1).getGeom()
          .coord().get(0));
      IPoint section1FinalPoint = new GM_Point(sections.get(1).getGeom()
          .coord().get(sections.get(1).getGeom().coord().size() - 1));
      if (node.getGeom().distance(section0InitialPoint) < node.getGeom()
          .distance(section0FinalPoint)) {
        sections.get(0).getGeom().coord().inverseOrdre();
      }
      if (node.getGeom().distance(section1FinalPoint) < node.getGeom()
          .distance(section1InitialPoint)) {
        sections.get(1).getGeom().coord().inverseOrdre();
      }

      // Affectation of the new geometry to the first section and elimination of
      // the second section
      sections.get(0).getGeom().coord()
          .addAll(sections.get(1).getGeom().coord());
      sections.get(0).setInitialGeom(
          (ILineString) sections.get(0).getGeom().clone());
      sectionsToRemove.add(sections.get(1));

      // Update of the treated node
      node.getInSections().clear();
      node.getOutSections().clear();

    }

    // Elimination of inconsistent sections
    for (INetworkSection section : sectionsToRemove) {
      section.setGeom(null);
      section.eliminate();
    }

    // Removal of all nodes of the dataset (will be re-created with enrichment
    for (INetworkNode node : net.getNodes()) {
      node.setDeleted(true);
    }
    net.getNodes().clear();

    // Re-enrich the network with new aggregated sections
    NetworkEnrichment.enrichNetwork(net);

  }

  /**
   * recupere toutes les impasses du reseau
   * @return
   */
  public static IFeatureCollection<INetworkSection> getImpasses(INetwork net) {
    return NetworkEnrichment.getImpasses(net, 0.0);
  }

  /**
   * Recupere les impasses du reseau plus courtes qu'un certain seuil Suppose
   * d'avoir construit la topologie via la methode
   * {@link #construireTopologie()}
   * 
   * @param longueurMin
   * @return
   */
  public static IFeatureCollection<INetworkSection> getImpasses(INetwork net,
      double longueurMin) {
    FT_FeatureCollection<INetworkSection> impasses = new FT_FeatureCollection<INetworkSection>();

    // parcours des noeuds
    for (INetworkNode node : net.getNodes()) {
      if (node.isDeleted()) {
        continue;
      }

      if (node.getDegree() != 1) {
        continue;
      }

      // n est un noeud extreme d'impasse
      // recupere le troncon non supprime relie au noeud
      INetworkSection section = null;
      for (INetworkSection at_ : node.getOutSections()) {
        if (!at_.isDeleted()) {
          section = at_;
        }
      }
      if (section == null) {
        for (INetworkSection at_ : node.getInSections()) {
          if (!at_.isDeleted()) {
            section = at_;
          }
        }
      }

      if (section == null) {
        NetworkEnrichment.logger
            .error("Erreur lors de recuperation d'impasses de " + net
                + ". aucun troncon trouve pour un noeud de degre 1: X="
                + node.getPosition().getX() + " Y=" + node.getPosition().getY());
        continue;
      }

      if (longueurMin <= 0.0) {
        impasses.add(section);
      } else if (section.getGeom().length() <= longueurMin) {
        impasses.add(section);
      }
    }

    return impasses;
  }

  /**
   * @param longueurMin
   */
  public static void supprimerImpasses(INetwork net, double longueurMin) {

    // recupere les impasses
    IFeatureCollection<INetworkSection> impasses = NetworkEnrichment
        .getImpasses(net, longueurMin);

    // supprime les impasses tant qu'il y en a
    while (impasses.size() > 0) {

      NetworkEnrichment.logger.info(impasses.size() + " impasses a supprimer");

      for (INetworkSection obj : impasses) {

        // supression de l'impasse
        obj.eliminate();

        // supression du/des noeuds au bout de l'impasse supprimee
        if (obj.getFinalNode().getDegree() == 0) {
          obj.getFinalNode().eliminate();
        }
        if (obj.getInitialNode().getDegree() == 0) {
          obj.getInitialNode().eliminate();
        }
      }
      impasses = NetworkEnrichment.getImpasses(net, longueurMin);
      NetworkEnrichment.logger.info(impasses.size()
          + " impasses a supprimer *******");
    }

  }
}
