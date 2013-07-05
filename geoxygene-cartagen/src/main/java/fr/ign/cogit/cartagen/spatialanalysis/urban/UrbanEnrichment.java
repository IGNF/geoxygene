/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.urban;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.operation.buffer.BufferParameters;

import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IBranchingCrossroad;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadStroke;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoundAbout;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.ITown;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanAlignment;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.interfacecartagen.GeneralisationLeftPanelComplement;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.cartagen.spatialanalysis.network.DeadEndGroup;
import fr.ign.cogit.cartagen.spatialanalysis.network.streets.StreetNetwork;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Static class compiling all methods used for enriching urban data (buildings,
 * alignments, blocks, towns)
 * @author JRenard
 * 
 */
public class UrbanEnrichment {
  private static Logger logger = Logger.getLogger(UrbanEnrichment.class
      .getName());

  // Parameters for towns construction
  private static final double DISTANCE_BUFFER = 50.0;
  private static final double DISTANCE_EROSION = 30.0;
  private static final double SEUIL_DP = 10.0;
  private static final int DEFAUT_NB_QUADRANT_SEGMENT_BUFFER = 4;

  /**
   * Networks used to build urban blocks
   */
  private static Set<INetwork> structuringNetworks = null;

  /**
   * @return
   * @author CDuchene
   */
  public static Set<INetwork> getStructuringNetworks(CartAGenDataSet dataset) {
    UrbanEnrichment.structuringNetworks = new HashSet<INetwork>();
    UrbanEnrichment.structuringNetworks.add(dataset.getRoadNetwork());
    UrbanEnrichment.structuringNetworks.add(dataset.getHydroNetwork());
    UrbanEnrichment.structuringNetworks.add(dataset.getRailwayNetwork());
    return UrbanEnrichment.structuringNetworks;
  }

  /**
   * Construction of the towns of a dataset by enriching its buildings By
   * default, used on the current dataset of the application By default, urban
   * alignments are not built
   */
  public static void buildTowns() {
    UrbanEnrichment.buildTowns(CartAGenDoc.getInstance().getCurrentDataset(),
        false);
  }

  /**
   * Construction of the towns of a dataset by enriching its buildings By
   * default, used on the current dataset of the application
   * @param buildUrbanAlignments a boolean to determine if urban alignments have
   *          to be built
   */
  public static void buildTowns(boolean buildUrbanAlignments) {
    UrbanEnrichment.buildTowns(CartAGenDoc.getInstance().getCurrentDataset(),
        buildUrbanAlignments);
  }

  /**
   * Construction of the towns of a dataset by enriching its buildings By
   * default, urban alignments are not built
   * @param jdd the dataset
   */
  public static void buildTowns(CartAGenDataSet jdd) {
    UrbanEnrichment.buildTowns(jdd,
        UrbanEnrichment.DEFAUT_NB_QUADRANT_SEGMENT_BUFFER, false);
  }

  /**
   * Construction of the towns of a dataset by enriching its buildings
   * @param jdd the dataset
   * @param buildUrbanAlignments a boolean to determine if urban alignments have
   *          to be built
   */
  public static void buildTowns(CartAGenDataSet jdd,
      boolean buildUrbanAlignments) {
    UrbanEnrichment
        .buildTowns(jdd, UrbanEnrichment.DEFAUT_NB_QUADRANT_SEGMENT_BUFFER,
            buildUrbanAlignments);
  }

  /**
   * Construction of the towns of a dataset by enriching its buildings
   * @param dataset the dataset
   * @param quadrantSegments parameter for round parts of buffers
   * @param buildUrbanAlignments a boolean to determine if urban alignments have
   *          to be built
   */
  public static void buildTowns(CartAGenDataSet dataset, int quadrantSegments,
      boolean buildUrbanAlignments) {

    if (UrbanEnrichment.logger.isDebugEnabled()) {
      UrbanEnrichment.logger.debug("construction des villes");
    }

    // suppression des villes existantes
    dataset.eraseTowns();

    if (dataset.getBuildings().size() == 0) {
      return;
    }

    if (UrbanEnrichment.logger.isDebugEnabled()) {
      UrbanEnrichment.logger
          .debug("recupere liste des geometries des batiments");
    }
    ArrayList<IGeometry> geoms = new ArrayList<IGeometry>();
    for (IBuilding bat : dataset.getBuildings()) {
      geoms.add(bat.getGeom());
    }

    if (UrbanEnrichment.logger.isInfoEnabled()) {
      UrbanEnrichment.logger.info("urban area computation");
    }

    // // With union of buffers on buildings
    IGeometry union = UrbanAreaComputationJTS.calculTacheUrbaine(geoms,
        UrbanEnrichment.DISTANCE_BUFFER, UrbanEnrichment.DISTANCE_EROSION,
        quadrantSegments, UrbanEnrichment.SEUIL_DP);

    // With swinging arm non convex hull on buildings
    // ArrayList<IDirectPosition> points = new ArrayList<IDirectPosition>();
    // for (IGeometry geom : geoms) {
    // int i = 0;
    // for (IDirectPosition dp : geom.coord()) {
    // if (dp.equals(geom.coord().get(0))) {
    // if (i > 0) {
    // continue;
    // }
    // i++;
    // }
    // points.add(dp);
    // }
    // }
    // IGeometry union = (new SwingingArmNonConvexHull(points, 200.0)).compute()
    // .buffer(UrbanEnrichment.DISTANCE_BUFFER, quadrantSegments,
    // BufferParameters.CAP_ROUND, BufferParameters.CAP_ROUND);

    if (UrbanEnrichment.logger.isInfoEnabled()) {
      UrbanEnrichment.logger.info("end of urban area computation");
    }

    if (UrbanEnrichment.logger.isInfoEnabled()) {
      UrbanEnrichment.logger
          .info("construction of the cities with agent hierarchy");
    }

    if (UrbanEnrichment.logger.isDebugEnabled()) {
      UrbanEnrichment.logger.debug("construction des objets ville");
    }
    if (union instanceof IPolygon) {
      dataset.getTowns().add(
          CartagenApplication.getInstance().getCreationFactory()
              .createTown((IPolygon) union));
    } else if (union instanceof IMultiSurface<?>) {
      IMultiSurface<?> mp = (IMultiSurface<?>) union;
      int nb = mp.size();
      for (int i = 0; i < nb; i++) {
        if (UrbanEnrichment.logger.isInfoEnabled()) {
          UrbanEnrichment.logger.info("   construction ville: " + i + "/" + nb);
        }
        ITown town = CartagenApplication.getInstance().getCreationFactory()
            .createTown((IPolygon) mp.get(i));
        dataset.getTowns().add(town);
        if (town.getGeom().area() < 100000.0) {
          town.setDeleted(true);
        }
      }
    } else {
      UrbanEnrichment.logger
          .error("Impossible de creer ville. Type de geometrie non traite: "
              + union);
      return;
    }

    // remplit carte topo avec les troncons
    CarteTopo carteTopo = new CarteTopo("cartetopo");
    for (INetwork res : UrbanEnrichment.getStructuringNetworks(dataset)) {
      if (res.getSections().size() > 0) {
        carteTopo.importClasseGeo(res.getSections(), true);
      }
    }

    // remplit carte topo avec limites de villes
    IFeatureCollection<DefaultFeature> contours = new FT_FeatureCollection<DefaultFeature>();
    for (ITown ville : dataset.getTowns()) {
      DefaultFeature contour = new DefaultFeature();
      contour.setGeom((ville.getGeom()).exteriorLineString());
      contours.add(contour);
    }
    carteTopo.importClasseGeo(contours, true);
    carteTopo.setBuildInfiniteFace(true);

    if (UrbanEnrichment.logger.isInfoEnabled()) {
      UrbanEnrichment.logger.info("creating nodes");
    }
    carteTopo.creeNoeudsManquants(1.0);

    if (UrbanEnrichment.logger.isInfoEnabled()) {
      UrbanEnrichment.logger.info("merging nodes");
    }
    carteTopo.fusionNoeuds(1.0);

    if (UrbanEnrichment.logger.isInfoEnabled()) {
      UrbanEnrichment.logger.info("filtering duplicated edges");
    }
    carteTopo.filtreArcsDoublons();

    if (UrbanEnrichment.logger.isInfoEnabled()) {
      UrbanEnrichment.logger.info("making planar");
    }
    carteTopo.rendPlanaire(1.0);

    if (UrbanEnrichment.logger.isInfoEnabled()) {
      UrbanEnrichment.logger.info("merging duplicated nodes");
    }
    carteTopo.fusionNoeuds(1.0);

    if (UrbanEnrichment.logger.isInfoEnabled()) {
      UrbanEnrichment.logger.info("filtering duplicated edges");
    }
    carteTopo.filtreArcsDoublons();

    if (UrbanEnrichment.logger.isInfoEnabled()) {
      UrbanEnrichment.logger.info("creating topological faces");
    }
    carteTopo.creeTopologieFaces();

    if (UrbanEnrichment.logger.isDebugEnabled()) {
      UrbanEnrichment.logger.debug(carteTopo.getListeFaces().size()
          + " faces trouvées");
    }

    if (UrbanEnrichment.logger.isDebugEnabled()) {
      UrbanEnrichment.logger
          .debug("construction de l'index spatial sur les faces");
    }
    carteTopo.getPopFaces().initSpatialIndex(Tiling.class, false);

    // for(ITown ville : jdd.getVilles()) {
    // // build the street Network of the town agent
    // IPopulation<IRoadLine> roads = new Population<IRoadLine>();
    // for (SectionAgent section:
    // CartAGenDoc.getInstance().getCurrentDataset().getReseauRoutier().getComponents())
    // {
    // roads.add((IRoadLine)section.getFeature());
    // }
    // // for(IFeature f:jdd.getPopulation(GeneralisationDataSet.ROAD_POP))
    // // roads.add((IRoadLine) f);
    // IPopulation<IRoadStroke> strokes = new Population<IRoadStroke>();
    // // for(IFeature f:jdd.getPopulation(GeneralisationDataSet.STROKES_POP))
    // // strokes.add((IRoadStroke) f);
    // IPopulation<IRoundAbout> rounds = new Population<IRoundAbout>();
    // // for(IFeature
    // f:jdd.getPopulation(GeneralisationDataSet.ROUNDABOUT_POP))
    // // rounds.add((IRoundAbout) f);
    // IPopulation<IBranchingCrossroad> branchs = new
    // Population<IBranchingCrossroad>();
    // // for(IFeature f:jdd.getPopulation(GeneralisationDataSet.BRANCHING_POP))
    // // branchs.add((IBranchingCrossroad) f);
    // ((TownAgent)AgentUtil.getAgentAgentFromGeneObj(ville)).streetNetwork =
    // new StreetNetwork(ville.getGeom(), roads,
    // strokes, rounds, branchs, jdd.getBlocks());
    // }

    // MAJ layerManager

    if (UrbanEnrichment.logger.isDebugEnabled()) {
      UrbanEnrichment.logger.debug("construction des ilots des "
          + dataset.getTowns().size() + " villes");
    }
    for (ITown ville : dataset.getTowns()) {

      // Lien de la ville avec ses tronçons de route
      IFeatureCollection<IRoadLine> roads = new FT_FeatureCollection<IRoadLine>();
      for (INetworkSection section : dataset.getRoads()) {
        if (!(section instanceof IRoadLine)) {
          continue;
        }
        if (section.getGeom().intersects(ville.getGeom())) {
          roads.add((IRoadLine) section);
        }
      }
      StreetNetwork net = new StreetNetwork(ville.getGeom(), roads,
          new FT_FeatureCollection<IRoadStroke>(),
          new FT_FeatureCollection<IRoundAbout>(),
          new FT_FeatureCollection<IBranchingCrossroad>(),
          new FT_FeatureCollection<IUrbanBlock>());
      ville.setStreetNetwork(net);

      // Construction des ilots
      if (UrbanEnrichment.logger.isInfoEnabled()) {
        UrbanEnrichment.logger.info("construction des ilots de " + ville);
      }
      // ville.construireIlotsPolygonizerJTS();
      // ville.construireIlotsCarteTopoGeoxygene();
      UrbanEnrichment.buildBlocksInTown(ville, carteTopo, buildUrbanAlignments);

      // Lien de la ville avec ses impasses
      HashSet<DeadEndGroup> deadEnds = DeadEndGroup.buildFromRoads(roads,
          ville.getGeom(), carteTopo);
      IFeatureCollection<DeadEndGroup> deadEndColl = new FT_FeatureCollection<DeadEndGroup>();
      for (DeadEndGroup deadEnd : deadEnds) {
        deadEndColl.add(deadEnd);
      }
      ville.setDeadEnds(deadEndColl);

    }

    // nettoyage
    carteTopo.nettoyer();

    // MAJ du panel de gauche
    GeneralisationLeftPanelComplement.getInstance().cVoirVille.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirVille
        .setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectVille
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirVilleInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lVille.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirIlot.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectIlot
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirIlotInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lIlot.setEnabled(true);
    if (buildUrbanAlignments) {
      GeneralisationLeftPanelComplement.getInstance().cVoirAlign
          .setEnabled(true);
      GeneralisationLeftPanelComplement.getInstance().cSelectAlign
          .setEnabled(true);
      GeneralisationLeftPanelComplement.getInstance().cVoirAlignInitial
          .setEnabled(true);
      GeneralisationLeftPanelComplement.getInstance().lAlign.setEnabled(true);
    }

    if (UrbanEnrichment.logger.isInfoEnabled()) {
      UrbanEnrichment.logger.info("end of the construction of the cities");
    }

  }

  /**
   * Builds the inner blocks of a town based on a topo map
   * @param town
   * @param carteTopo
   * @param buildUrbanAlignments
   */
  public static void buildBlocksInTown(ITown town, CarteTopo carteTopo,
      boolean buildUrbanAlignments) {
    HashSet<IUrbanBlock> cityBlocks = new HashSet<IUrbanBlock>();

    // parcours des faces de la carte topo
    for (Face face : carteTopo.getPopFaces().select(town.getGeom())) {
      if (face.isInfinite())
        continue;

      IPolygon polygone = face.getGeometrie();

      // verifie si le polygone appartient bien a la ville
      if (!town.getGeom().buffer(5.0).contains(polygone)) {
        continue;
      }

      try {
        polygone = (IPolygon) AdapterFactory.to2DGM_Object(polygone);
      } catch (Exception e) {
        // TownAgent.logger
        // .error("Echec pendant la convertion de la géométrie de la zone élémentaire en 2D : "
        // + e.getMessage());
        // TownAgent.logger.error(polygone.toString());
        continue;
      }

      // recupere les troncons de l'ilot
      IFeatureCollection<INetworkSection> troncons = new FT_FeatureCollection<INetworkSection>();
      Collection<IRoadLine> roads = new HashSet<IRoadLine>();

      // parcours des arcs de la carte topo
      List<Arc> arcs = face.getArcsDirects();
      arcs.addAll(face.getArcsIndirects());
      for (Arc arc : arcs) {
        for (IFeature feature : arc.getCorrespondants()) {

          // arc correspond a un defaultfeature: c'est un contour de ville
          if (feature instanceof DefaultFeature) {
            continue;
          }

          // c'est un troncon
          if (feature instanceof INetworkSection) {
            troncons.add((INetworkSection) feature);
            if (feature instanceof IRoadLine) {
              roads.add((IRoadLine) feature);
            }
          }
        }
      }

      // recupere les batiments de l'ilot

      // recupere les batiments intersectant l'ilot
      Collection<IBuilding> bats = CartAGenDoc.getInstance()
          .getCurrentDataset().getBuildings().select(polygone);

      IFeatureCollection<IUrbanElement> urbanElements = new FT_FeatureCollection<IUrbanElement>();
      for (IBuilding ab : bats) {
        // batiment totallement inclu dans ilot
        if (polygone.contains(ab.getGeom())) {
          urbanElements.add(ab);
          continue;
        }

        // le batiment n'est pas totalement dans l'ilot. calcul de la part du
        // batiment dans l'ilot
        double taux = ab.getGeom().intersection(polygone).area()
            / (ab.getGeom().area());

        // si ce taux est suffisament grand, le batiment est considere comme
        // appartenant a l'ilot
        if (taux > 0.6) {
          urbanElements.add(ab);
          continue;
        }
      }

      IUrbanBlock block = CartagenApplication
          .getInstance()
          .getCreationFactory()
          .createUrbanBlock(polygone, town, urbanElements, troncons, null,
              town.getStreetNetwork());
      // TODO vérifier la paternité de ce code qui crée des ilots avec un
      // même
      // identifiant sur une même zone
      // block.setId(2 ^ town.getId() * 3 ^ compteur);
      cityBlocks.add(block);
      CartAGenDoc.getInstance().getCurrentDataset().getBlocks().add(block);
      if (!(buildUrbanAlignments)) {
        continue;
      }

      // calcul des alignements de batiments dans l'ilot
      UrbanEnrichment.createUrbanAlignmentsBasedOnSections(block);

      GeneralisationLeftPanelComplement.getInstance().cVoirAlign
          .setEnabled(true);
      GeneralisationLeftPanelComplement.getInstance().cSelectAlign
          .setEnabled(true);
      GeneralisationLeftPanelComplement.getInstance().cVoirAlignInitial
          .setEnabled(true);
      GeneralisationLeftPanelComplement.getInstance().lAlign.setEnabled(true);

    }

    // ajout les blocks au reseau de rues
    // MODIF Guillaume (complete build of the street network)
    town.getStreetNetwork().buildNetworkFromCityBlocks(cityBlocks);
    // update the block agents as some city blocks may have been aggregated

  }

  /**
   * Creates the urban alignments of a block from buffers around sections
   */
  public static void createUrbanAlignmentsBasedOnSections(IUrbanBlock block) {

    // Creation of alignment following each road section
    List<IUrbanAlignment> structures = new ArrayList<IUrbanAlignment>();
    for (INetworkSection section : block.getSurroundingNetwork()) {
      List<IUrbanElement> buildsAlongSection = new ArrayList<IUrbanElement>();
      IGeometry buffer = section.getGeom().buffer(20.0, 2,
          BufferParameters.CAP_SQUARE, BufferParameters.CAP_SQUARE);
      for (IUrbanElement build : block.getUrbanElements()) {
        if (build.getGeom().intersects(buffer)) {
          buildsAlongSection.add(build);
        }
      }
      if (buildsAlongSection.size() > 2) {
        IUrbanAlignment align = CartagenApplication.getInstance()
            .getCreationFactory().createUrbanAlignment(buildsAlongSection);
        structures.add(align);
      }
    }

    // Splitting of heterogeneous alignments based on distance and size criteria
    List<IUrbanAlignment> structuresAfterSplit = new ArrayList<IUrbanAlignment>();
    for (IUrbanAlignment structure : structures) {
      structuresAfterSplit.addAll(UrbanEnrichment.split(structure,
          2.0 * Legend.getSYMBOLISATI0N_SCALE() / 1000.0, Math.PI / 3.0));
    }

    // Alignments mustn't intersect sections
    List<IUrbanAlignment> structuresAfterClean = new ArrayList<IUrbanAlignment>();
    for (IUrbanAlignment structure : structuresAfterSplit) {
      for (INetworkSection section : block.getSurroundingNetwork()) {
        if (structure.getShapeLine().intersects(section.getGeom())) {
          structure.getUrbanElements().clear();
          CartAGenDoc.getInstance().getCurrentDataset().getUrbanAlignments()
              .remove(structure);
        } else {
          structuresAfterClean.add(structure);
        }
      }
    }

    // Merging of analog alignments
    HashMap<IUrbanAlignment, Boolean> nonDeletedStructures = new HashMap<IUrbanAlignment, Boolean>();
    for (IUrbanAlignment structure : structuresAfterClean) {
      nonDeletedStructures.put(structure, Boolean.TRUE);
    }
    for (IUrbanAlignment structure : nonDeletedStructures.keySet()) {
      if (!nonDeletedStructures.get(structure)) {
        continue;
      }
      for (IUrbanAlignment structureBis : nonDeletedStructures.keySet()) {
        if (structureBis.equals(structure)) {
          continue;
        }
        if (!nonDeletedStructures.get(structureBis)) {
          continue;
        }
        // Comparison of urban elements of each alignment
        List<IUrbanElement> commonBuilds = new ArrayList<IUrbanElement>();
        for (IUrbanElement build : structure.getUrbanElements()) {
          if (structureBis.getUrbanElements().contains(build)) {
            commonBuilds.add(build);
          }
        }
        // More than 2 buildings in common => merge the alignments
        if (commonBuilds.size() > 2) {
          UrbanEnrichment.merge(structure, structureBis);
          nonDeletedStructures.put(structureBis, Boolean.FALSE);
        }
        // 2 buildings in common => careful of cycles, otherwise merge the
        // alignments
        else if (commonBuilds.size() == 2) {
          if ((commonBuilds.get(0).equals(structure.getInitialElement()) && commonBuilds
              .get(1).equals(structure.getFinalElement()))
              || (commonBuilds.get(0).equals(structure.getFinalElement()) && commonBuilds
                  .get(1).equals(structure.getInitialElement()))) {
            continue;
          }
          UrbanEnrichment.merge(structure, structureBis);
          nonDeletedStructures.put(structureBis, Boolean.FALSE);
        }
        // 1 building in common => merge the alignment only if it is an end
        // building with narrow angle between alignments
        else if (commonBuilds.size() == 1) {
          IDirectPosition pt1 = null;
          IDirectPosition pt2 = commonBuilds.get(0).getGeom().centroid();
          IDirectPosition pt3 = null;
          if (commonBuilds.get(0).equals(structure.getInitialElement())) {
            pt1 = structure.getUrbanElements().get(1).getGeom().centroid();
          } else if (commonBuilds.get(0).equals(structure.getFinalElement())) {
            pt1 = structure.getUrbanElements()
                .get(structure.getUrbanElements().size() - 2).getGeom()
                .centroid();
          }
          if (commonBuilds.get(0).equals(structureBis.getInitialElement())) {
            pt3 = structureBis.getUrbanElements().get(1).getGeom().centroid();
          } else if (commonBuilds.get(0).equals(structureBis.getFinalElement())) {
            pt3 = structureBis.getUrbanElements()
                .get(structureBis.getUrbanElements().size() - 2).getGeom()
                .centroid();
          }
          // Angle test
          if (pt1 != null
              && pt3 != null
              && Angle.angleTroisPoints(pt1, pt2, pt3).getValeur() < Math.PI / 6.0) {
            UrbanEnrichment.merge(structure, structureBis);
            nonDeletedStructures.put(structureBis, Boolean.FALSE);
          }
        }
      }
    }

  }

  /**
   * Merge two alignments, both of them having buildings in common
   * @param align1 first alignment to be merged
   * @param align2 second alignment to be merged
   */
  public static void merge(IUrbanAlignment align1, IUrbanAlignment align2) {

    // liaison avec les micros
    for (IUrbanElement build : align2.getUrbanElements()) {
      if (align1.getUrbanElements().contains(build)) {
        continue;
      }
      align1.getUrbanElements().add(build);
    }

    // Destroy of the second alignment
    align2.getUrbanElements().clear();
    CartAGenDoc.getInstance().getCurrentDataset().getUrbanAlignments()
        .remove(align2);

    // Computation of the characteristics
    align1.computeInitialAndFinalElements();
    align1.computeShapeLine();
    align1.setInitialShapeLine((ILineString) align1.getShapeLine().clone());

  }

  /**
   * split an alignment in several intern alignments according to 1) a condition
   * on size homogeneity 2) a maximum distance criteria between buildings 3) a
   * maximum angle criteria between buildings
   * @param align the alignment to be splitted
   * @param distance the maximum distance to detect splitting points of the
   *          alignment
   * @param angle the maximum angle to detect splitting points of the alignment
   */
  public static List<IUrbanAlignment> split(IUrbanAlignment align,
      double distance, double angle) {

    List<IUrbanAlignment> aligns = new ArrayList<IUrbanAlignment>();

    double[] sizes = new double[align.getUrbanElements().size()];
    for (int k = 0; k < align.getUrbanElements().size(); k++) {
      sizes[k] = align.getUrbanElements().get(k).getGeom().area();
    }
    Arrays.sort(sizes);
    double medianSize;
    if ((sizes.length % 2) == 0) { // even
      medianSize = (sizes[sizes.length / 2] * sizes[sizes.length / 2 - 1]) / 2.0;
    } else {
      medianSize = sizes[sizes.length / 2];
    }

    List<IUrbanElement> currentPart = new ArrayList<IUrbanElement>();

    for (int i = 0; i < align.getUrbanElements().size(); i++) {

      // Current building is too large
      if (align.getUrbanElements().get(i).getGeom().area() / medianSize > 2.0) {
        if (currentPart.size() > 2) {
          aligns.add(CartagenApplication.getInstance().getCreationFactory()
              .createUrbanAlignment(currentPart));
        }
        currentPart.clear();
      }

      // Distance between current buildings is too high
      else if (i > 0
          && align.getUrbanElements().get(i - 1).getGeom()
              .distance(align.getUrbanElements().get(i).getGeom()) > distance) {
        if (currentPart.size() > 2) {
          aligns.add(CartagenApplication.getInstance().getCreationFactory()
              .createUrbanAlignment(currentPart));
        }
        currentPart.clear();
        currentPart.add(align.getUrbanElements().get(i));
      }

      // Angle between current buildings is too high
      else if (i > 1
          && Math
              .abs(Angle.angleTroisPoints(
                  align.getUrbanElements().get(i - 2).getGeom().centroid(),
                  align.getUrbanElements().get(i - 1).getGeom().centroid(),
                  align.getUrbanElements().get(i).getGeom().centroid())
                  .getValeur()) < Math.PI - Math.abs(angle)) {
        if (currentPart.size() > 2) {
          aligns.add(CartagenApplication.getInstance().getCreationFactory()
              .createUrbanAlignment(currentPart));
        }
        currentPart.clear();
        currentPart.add(align.getUrbanElements().get(i - 1));
        currentPart.add(align.getUrbanElements().get(i));
      }

      else {
        currentPart.add(align.getUrbanElements().get(i));
      }

    }

    if (currentPart.size() > 2) {
      aligns.add(CartagenApplication.getInstance().getCreationFactory()
          .createUrbanAlignment(currentPart));
    }

    align.getUrbanElements().clear();
    CartAGenDoc.getInstance().getCurrentDataset().getUrbanAlignments()
        .remove(align);

    return aligns;

  }

}
