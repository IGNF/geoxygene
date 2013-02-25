/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/**
 * @author julien Gaffuri 28 janv. 2009
 */
package fr.ign.cogit.cartagen.software.interfacecartagen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.SymbolShape;
import fr.ign.cogit.cartagen.core.genericschema.admin.ISimpleAdminUnit;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IDEMPixel;
import fr.ign.cogit.cartagen.core.genericschema.road.IPathLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.graph.IEdge;
import fr.ign.cogit.cartagen.graph.INode;
import fr.ign.cogit.cartagen.graph.ITriangleFace;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.cartagen.software.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Symbolisation;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.VisuPanel;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolList;
import fr.ign.cogit.cartagen.spatialanalysis.measures.BlockBuildingsMeasures;
import fr.ign.cogit.cartagen.spatialanalysis.measures.TriangleFacesMeasures;
import fr.ign.cogit.cartagen.spatialanalysis.measures.congestion.CongestionComputation;
import fr.ign.cogit.cartagen.spatialanalysis.measures.congestion.DeletionCost;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.style.ExternalGraphic;
import fr.ign.cogit.geoxygene.util.algo.OrientationMeasure;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.lineoffset.LineOffsetBuilder;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.lineoffset.OffsetSegment;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.lineoffset.OffsetSegmentIntersection;

/**
 * une symbolisation de couche. une instance de cette classe fournit une methode
 * de dessin d'un objet sur une fenetre
 * @author julien Gaffuri 28 janv. 2009
 */
public abstract class GeneralisationSymbolisation extends Symbolisation {
  protected final static Logger logger = Logger
      .getLogger(GeneralisationSymbolisation.class.getName());

  /**
   * style qui affiche un segment
   * @param couleur
   * @return
   */
  public static GeneralisationSymbolisation segment(final Color couleur) {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        if (!(obj instanceof IEdge)) {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Mauvais type!");
          return;
        }
        IEdge s = (IEdge) obj;

        pv.drawSegment(couleur, s.getInitialNode().getPosition().getX(), s
            .getInitialNode().getPosition().getY(), s.getFinalNode()
            .getPosition().getX(), s.getFinalNode().getPosition().getY(), 1);
      }
    };
  }

  public static GeneralisationSymbolisation segment() {
    return GeneralisationSymbolisation.segment(Color.RED);
  }

  /**
   * affiche les valeurs des couts de suppression des batiments de son ilot
   * @return
   */
  public static GeneralisationSymbolisation ilotCoutsSuppressionBatiments() {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        if (!(obj instanceof IUrbanBlock)) {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Mauvais type!");
          return;
        }
        IUrbanBlock ilot = (IUrbanBlock) obj;

        double surfMaxBati = BlockBuildingsMeasures
            .getBlockBiggestBuildingArea(ilot);
        for (IUrbanElement ago : ilot.getUrbanElements()) {
          double cout = DeletionCost.getCoutSuppression(ago, surfMaxBati,
              GeneralisationSpecifications.DISTANCE_MAX_PROXIMITE);
          pv.drawText(Color.RED, ago.getGeom(), (int) (100 * cout) + "");

        }
      }
    };
  }

  /**
	 */
  private static GeneralisationSymbolisation initial = null;
  static Color initialColor = new Color(0, 255, 255, 40);

  /**
   * symbole initial
   * @return
   */
  public static GeneralisationSymbolisation initial() {
    if (GeneralisationSymbolisation.initial == null) {
      GeneralisationSymbolisation.initial = new GeneralisationSymbolisation() {
        @Override
        public void draw(VisuPanel pv, IFeature obj) {
          if (!(obj instanceof IGeneObj)) {
            GeneralisationSymbolisation.logger
                .warn("probleme dans le dessin de " + obj + ". Mauvais type!");
            return;
          }
          if (((IGeneObj) obj).getInitialGeom() instanceof IPolygon) {
            pv.draw(new Color(0, 0, 255, 50), ((IPolygon) ((IGeneObj) obj)
                .getInitialGeom()).exteriorLineString(),
                0.07 * 25000.0 / 1000.0, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND);
          } else {
            pv.draw(Symbolisation.couleurDefaut,
                ((IGeneObj) obj).getInitialGeom());
          }
        }
      };
    }
    return GeneralisationSymbolisation.initial;
  }

  /**
	 */
  private static GeneralisationSymbolisation initialLimit = null;

  /**
   * symbole initial
   * @return
   */
  public static GeneralisationSymbolisation initialLimite() {
    if (GeneralisationSymbolisation.initialLimit == null) {
      GeneralisationSymbolisation.initialLimit = new GeneralisationSymbolisation() {
        @Override
        public void draw(VisuPanel pv, IFeature obj) {
          if (!(obj instanceof IGeneObj)) {
            GeneralisationSymbolisation.logger
                .warn("probleme dans le dessin de " + obj + ". Mauvais type!");
            return;
          }
          IGeometry geom = ((IGeneObj) obj).getInitialGeom();
          if (geom instanceof IPolygon) {
            pv.drawLimit(Symbolisation.couleurDefaut, (IPolygon) geom);
          } else if (geom instanceof IMultiSurface<?>) {
            pv.drawLimit(Symbolisation.couleurDefaut, (IMultiSurface<?>) geom);
          } else {
            GeneralisationSymbolisation.logger
                .warn("probleme dans le dessin de " + obj
                    + ". Mauvais type de geometrie: "
                    + geom.getClass().getSimpleName());
            return;
          }
        }
      };
    }
    return GeneralisationSymbolisation.initial;
  }

  /**
   * dessine la rosaces des orientations des murs d'un batiment
   * @return
   */
  public static GeneralisationSymbolisation rosaceOrientation() {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        if (!(obj instanceof IBuilding)) {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Mauvais type!");
          return;
        }
        IBuilding bat = (IBuilding) obj;

        int taille = 2;
        double facteur = 1.0;

        // nombre d'orientations testees dans l'intervalle [0, Pi/2[
        int nbOrientationTestees = OrientationMeasure
            .getNB_ORIENTATIONS_TESTEES();

        // calcule et recupere la table des contributions
        double[] contributionsCotesOrientation = new OrientationMeasure(
            bat.getGeom()).getContributionsCotesOrientation();

        //
        IDirectPosition c = bat.getGeom().centroid();
        double pasOrientation = 0.5 * Math.PI / nbOrientationTestees;

        for (int i = 0; i < nbOrientationTestees; i++) {

          // valeur de l'angle
          double angle = i * pasOrientation;
          //
          double lg = facteur * contributionsCotesOrientation[i];

          // dessin
          pv.drawSegment(Color.BLUE, c.getX(), c.getY(),
              c.getX() + lg * Math.cos(angle), c.getY() + lg * Math.sin(angle),
              taille);
        }
      }
    };
  }

  /**
   * dessine la rosaces des encombrements autour d'un batiment necessite d'avoir
   * calcule la triangulation de l'ilot dans lequel se trouve le batiment
   * @return
   */
  public static GeneralisationSymbolisation batimentRosaceEncombrement() {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        if (!(obj instanceof IBuilding)) {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Mauvais type!");
          return;
        }
        IBuilding bat = (IBuilding) obj;

        int taille = 2;
        double facteur = 20.0;

        // calcul des encombrements
        CongestionComputation enc = new CongestionComputation();
        enc.calculEncombrement(bat,
            GeneralisationSpecifications.DISTANCE_MAX_PROXIMITE);
        int nb = enc.getEncombrements().length;

        //
        IDirectPosition c = bat.getGeom().centroid();
        double pasOrientation = 2 * Math.PI / nb;

        for (int i = 0; i < nb; i++) {

          // valeur de l'angle
          double angle = i * pasOrientation;
          //
          double lg = facteur * enc.getEncombrements()[i];

          // dessin
          pv.drawSegment(Color.BLUE, c.getX(), c.getY(),
              c.getX() + lg * Math.cos(angle), c.getY() + lg * Math.sin(angle),
              taille);
        }

      }
    };
  }

  /**
   * courbe de niveau, avec epaisseur differente pour courbes maitresses et
   * normales
   * @return
   */
  public static GeneralisationSymbolisation courbeDeNiveau(
      final AbstractLayerGroup layerGroup) {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }
        if (!(obj instanceof IContourLine)) {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Mauvais type!");
          return;
        }
        IContourLine cn = (IContourLine) obj;
        if (layerGroup.symbolisationDisplay) {
          pv.draw(GeneralisationLegend.CN_COULEUR, cn.getGeom(), cn.getWidth()
              * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
              BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        } else {
          pv.draw(GeneralisationLegend.CN_COULEUR, cn.getGeom(),
              0.07 * 25000.0 / 1000.0, BasicStroke.CAP_ROUND,
              BasicStroke.JOIN_ROUND);
        }
      }
    };
  }

  public static GeneralisationSymbolisation courbeDeNiveau() {
    return GeneralisationSymbolisation.courbeDeNiveau(CartagenApplication
        .getInstance().getLayerGroup());
  }

  /**
   * affiche pixel avec couleur degradee en fonction de sa valeur (du noir au
   * blanc)
   * @return
   */
  public static GeneralisationSymbolisation pixelsDegade() {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        IDEMPixel pixel = (IDEMPixel) obj;
        Color col;
        double Zmin = CartAGenDoc.getInstance().getCurrentDataset()
            .getReliefField().getZMin();
        double Zmax = CartAGenDoc.getInstance().getCurrentDataset()
            .getReliefField().getZMax();
        if (Zmin == Zmax) {
          col = Color.LIGHT_GRAY;
        } else {
          double t = (pixel.getZ() - Zmin) / (Zmax - Zmin);
          col = new Color((int) (230 - 120 * t), (int) (240 - 90 * t),
              (int) (220 - 130 * t));
        }
        pv.drawRectangle(col, pixel.getX(), pixel.getY(), CartagenApplication
            .getInstance().getDEMResolution());
      }
    };
  }

  /**
   * zones arborees BDTopo
   * @return
   */
  public static GeneralisationSymbolisation zonesArboreesBDTopo(
      final AbstractLayerGroup layerGroup) {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        if (!(obj instanceof ISimpleLandUseArea)) {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Mauvais type!");
          return;
        }
        ISimpleLandUseArea parcelle = (ISimpleLandUseArea) obj;

        if (layerGroup.symbolisationDisplay) {
          if (parcelle.getType() == 0) {
            pv.draw(GeneralisationLegend.ZA_COULEUR_VIDE, obj.getGeom());
          } else if (parcelle.getType() == 1) {
            pv.draw(GeneralisationLegend.ZA_COULEUR_FORET, obj.getGeom());
            pv.drawLimit(
                GeneralisationLegend.ZA_COULEUR_CONTOUR_FORET,
                parcelle.getGeom(),
                GeneralisationLegend.ZA_LARGEUR_CONTOUR_FORET
                    * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
          } else if (parcelle.getType() == 2) {
            pv.draw(GeneralisationLegend.ZA_COULEUR_ACTIVITE, obj.getGeom());
            pv.drawLimit(
                GeneralisationLegend.ZA_COULEUR_CONTOUR_ACTIVITE,
                parcelle.getGeom(),
                GeneralisationLegend.ZA_LARGEUR_CONTOUR_ACTIVITE
                    * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
          } else {
            GeneralisationSymbolisation.logger
                .warn("probleme dans le dessin de " + obj
                    + ". Type de zone non traite: " + parcelle.getType());
          }
        } else if (parcelle.getType() == 0) {
          pv.draw(GeneralisationLegend.ZA_COULEUR_VIDE, obj.getGeom());
        } else if (parcelle.getType() == 1) {
          pv.draw(GeneralisationLegend.ZA_COULEUR_FORET, obj.getGeom());
        } else if (parcelle.getType() == 2) {
          pv.draw(GeneralisationLegend.ZA_COULEUR_ACTIVITE, obj.getGeom());
        } else {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Type de zone non traite: " + parcelle.getType());
        }

      }
    };
  }

  public static GeneralisationSymbolisation zonesArboreesBDTopo() {
    return GeneralisationSymbolisation.zonesArboreesBDTopo(CartagenApplication
        .getInstance().getLayerGroup());
  }

  /**
   * zones arborees BDTopo
   * @return
   */
  public static GeneralisationSymbolisation defaultLandUse(
      final AbstractLayerGroup layerGroup, final List<Color> fillColors,
      final List<Color> limitColors) {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        if (!(obj instanceof ISimpleLandUseArea)) {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Mauvais type!");
          return;
        }
        ISimpleLandUseArea parcelle = (ISimpleLandUseArea) obj;

        if (fillColors.get(parcelle.getType()) == null)
          return;

        if (layerGroup.symbolisationDisplay) {
          if (parcelle.getType() >= fillColors.size()) {
            pv.draw(GeneralisationLegend.ZA_COULEUR_VIDE, obj.getGeom());
          } else {
            pv.draw(fillColors.get(parcelle.getType()), obj.getGeom());
            pv.drawLimit(
                limitColors.get(parcelle.getType()),
                parcelle.getGeom(),
                GeneralisationLegend.ZA_LARGEUR_CONTOUR_FORET
                    * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
          }
        } else if (parcelle.getType() == 0) {
          pv.draw(GeneralisationLegend.ZA_COULEUR_VIDE, obj.getGeom());
        } else if (parcelle.getType() == 1) {
          pv.draw(GeneralisationLegend.ZA_COULEUR_FORET, obj.getGeom());
        } else {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Type de zone non traite: " + parcelle.getType());
        }

      }
    };
  }

  /**
   * style pour les zones administratives
   * @return
   */
  public static GeneralisationSymbolisation zonesAdministratives(
      final AbstractLayerGroup layerGroup) {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        if (!(obj instanceof ISimpleAdminUnit)) {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Mauvais type!");
          return;
        }
        ISimpleAdminUnit parcelle = (ISimpleAdminUnit) obj;

        if (layerGroup.symbolisationDisplay) {
          // pv.dessinerLimite(Legende.ADMIN_COULEUR, parcelle.getGeom(),
          // Legende.ADMIN_LARGEUR, BasicStroke.CAP_ROUND,
          // BasicStroke.JOIN_ROUND, Legende.ADMIN_POINTILLES);
          pv.drawLimit(GeneralisationLegend.ADMIN_COULEUR, parcelle.getGeom(),
              GeneralisationLegend.ADMIN_LARGEUR, BasicStroke.CAP_ROUND,
              BasicStroke.JOIN_ROUND);
        } else {
          pv.drawLimit(GeneralisationLegend.ADMIN_COULEUR, parcelle.getGeom());
        }
      }
    };
  }

  public static GeneralisationSymbolisation zonesAdministratives() {
    return GeneralisationSymbolisation.zonesAdministratives(CartagenApplication
        .getInstance().getLayerGroup());
  }

  /**
   * dessin du dessous d'un troncon de route: couleur dessous, avec largeur
   * dessous
   * @return
   */
  public static GeneralisationSymbolisation troncon(final Color col,
      final AbstractLayerGroup layerGroup) {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        if (!(obj instanceof IRoadLine)) {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Mauvais type!");
          return;
        }
        IRoadLine tr = (IRoadLine) obj;

        if (layerGroup.symbolisationDisplay) {

          pv.draw(col, tr.getGeom(),
              tr.getWidth() * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
              BasicStroke.CAP_BUTT, BasicStroke.CAP_ROUND);

        } else {
          if (tr.getImportance() == 4) {
            pv.draw(Color.BLUE, tr.getGeom(), 0.15 * 25000.0 / 1000.0,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
          } else if (tr.getImportance() == 3 || tr.getImportance() == 2) {
            pv.draw(Color.ORANGE, tr.getGeom(), 0.15 * 25000.0 / 1000.0,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
          } else {
            pv.draw(Color.DARK_GRAY, tr.getGeom(), 0.15 * 25000.0 / 1000.0,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
          }
        }
      }
    };
  }

  public static GeneralisationSymbolisation troncon(final Color col) {
    return GeneralisationSymbolisation.troncon(col, CartagenApplication
        .getInstance().getLayerGroup());

  }

  /**
   * draw a path
   * @return
   */
  @SuppressWarnings("unused")
  public static GeneralisationSymbolisation path(final Color col,
      final AbstractLayerGroup layerGroup) {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        if (!(obj instanceof IPathLine)) {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Mauvais type!");
          return;
        }
        IPathLine tr = (IPathLine) obj;

        pv.draw(col, tr.getGeom(),
            tr.getWidth() * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
            BasicStroke.CAP_BUTT, BasicStroke.CAP_ROUND);
      }
    };
  }

  public static GeneralisationSymbolisation noeud(final Color col,
      final AbstractLayerGroup layerGroup) {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }
        if (!layerGroup.symbolisationDisplay) {
          return;
        }

        if (!(obj instanceof INetworkNode)) {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Mauvais type!");
          return;
        }
        INetworkNode n = (INetworkNode) obj;

        if (n.getDegree() <= 1) {
          return;
        }
        pv.drawCircle(col, n.getPosition().getX(), n.getPosition().getY(),
            n.getWidth() * Legend.getSYMBOLISATI0N_SCALE() / 1000.0);
      }
    };
  }

  public static GeneralisationSymbolisation noeud(final Color col) {
    return GeneralisationSymbolisation.noeud(col, CartagenApplication
        .getInstance().getLayerGroup());
  }

  /**
   * dessin du dessus d'un troncon routier: dessin en fonction de sa valeur
   * d'importance, d'une certaine couleur et largeur
   * @param importance
   * @param couleurDessus
   * @param largeurDessusmm
   * @return
   */
  public static GeneralisationSymbolisation tronconRouteDessus(
      final int importance, final Color couleurDessus,
      final double largeurDessusmm, final AbstractLayerGroup layerGroup) {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }
        if (!layerGroup.symbolisationDisplay) {
          return;
        }

        if (!(obj instanceof IRoadLine)) {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Mauvais type!");
          return;
        }
        IRoadLine tr = (IRoadLine) obj;

        if (tr.getImportance() != importance) {
          return;
        }
        pv.draw(couleurDessus, tr.getGeom(),
            largeurDessusmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
            BasicStroke.CAP_BUTT, BasicStroke.CAP_ROUND);
      }
    };
  }

  public static GeneralisationSymbolisation tronconRouteDessus(
      final int importance, final Color couleurDessus,
      final double largeurDessusmm) {
    return GeneralisationSymbolisation.tronconRouteDessus(importance,
        couleurDessus, largeurDessusmm, CartagenApplication.getInstance()
            .getLayerGroup());
  }

  public static GeneralisationSymbolisation noeudRouteDessus(
      final int importance, final Color couleurDessus,
      final double largeurDessus, final AbstractLayerGroup layerGroup) {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }
        if (!layerGroup.symbolisationDisplay) {
          return;
        }

        if (!(obj instanceof INetworkNode)) {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Mauvais type!");
          return;
        }

        INetworkNode n = (INetworkNode) obj;

        if (n.getDegree() <= 1 || n.getSectionsMaxImportance() != importance) {
          return;
        }
        pv.drawCircle(couleurDessus, n.getPosition().getX(), n.getPosition()
            .getY(), largeurDessus * Legend.getSYMBOLISATI0N_SCALE() / 1000.0);
      }
    };
  }

  public static GeneralisationSymbolisation noeudRouteDessus(
      final int importance, final Color couleurDessus,
      final double largeurDessus) {
    return GeneralisationSymbolisation.noeudRouteDessus(importance,
        couleurDessus, largeurDessus, CartagenApplication.getInstance()
            .getLayerGroup());
  }

  public static GeneralisationSymbolisation tronconRouteDecale() {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        if (!(obj instanceof IRoadLine)) {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Mauvais type!");
          return;
        }
        IRoadLine tr = (IRoadLine) obj;

        ILineString ls = tr.getGeom();
        double dist = 10.0;
        LineOffsetBuilder off = new LineOffsetBuilder(ls, dist);

        off.compute();

        ArrayList<OffsetSegment> sG = off.getLeftOffsetSegments();
        ArrayList<OffsetSegment> sD = off.getRightOffsetSegments();
        ArrayList<OffsetSegmentIntersection> dpinter = off.getIntersections();

        for (OffsetSegment element : sG) {
          if (element.arc0 != null) {
            pv.draw(Color.BLUE, element.arc0);
          }
          if (element.segment != null) {
            pv.draw(Color.BLUE, element.segment);
          }
          if (element.arc1 != null) {
            pv.draw(Color.BLUE, element.arc1);
          }
        }
        for (OffsetSegment element : sD) {
          if (element.arc0 != null) {
            pv.draw(Color.RED, element.arc0);
          }
          if (element.segment != null) {
            pv.draw(Color.RED, element.segment);
          }
          if (element.arc1 != null) {
            pv.draw(Color.RED, element.arc1);
          }
        }
        for (OffsetSegmentIntersection dp : dpinter) {
          pv.draw(Color.MAGENTA, dp.pos, 5);
        }

      }
    };
  }

  // agents point

  public static GeneralisationSymbolisation agentPoint(final Color couleur,
      final double largeur) {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        INode ap = (INode) obj;
        pv.drawCircle(couleur, ap.getGeom(), largeur);
      }
    };
  }

  public static GeneralisationSymbolisation agentPoint(final Color couleur) {
    return GeneralisationSymbolisation.agentPoint(couleur, 4);
  }

  public static GeneralisationSymbolisation agentPoint() {
    return GeneralisationSymbolisation.agentPoint(Color.RED);
  }

  // triangle

  public static GeneralisationSymbolisation triangle(final Color couleur) {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        ITriangleFace t = (ITriangleFace) obj;
        pv.drawLimit(couleur, t.getGeom());
      }
    };
  }

  public static GeneralisationSymbolisation triangleOmbrageTransparent(
      final Color col_retourne, final int transparence,
      final double dirOmbrageNoirX, final double dirOmbrageNoirY,
      final double dirOmbrageNoirZ, final double dirOmbrageJauneX,
      final double dirOmbrageJauneY, final double dirOmbrageJauneZ) {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        ITriangleFace t = (ITriangleFace) obj;

        Color col;
        if (TriangleFacesMeasures.istReverted(t)) {
          col = col_retourne;
        } else {
          double[] or = TriangleFacesMeasures.getSlopeVector(t);

          double ombrageNoir = -(or[0] * dirOmbrageNoirX + or[1]
              * dirOmbrageNoirY + or[2] * dirOmbrageNoirZ)
              / Math.sqrt(dirOmbrageNoirX * dirOmbrageNoirX + dirOmbrageNoirY
                  * dirOmbrageNoirY + dirOmbrageNoirZ * dirOmbrageNoirZ);
          double ombrageJaune = -(or[0] * dirOmbrageJauneX + or[1]
              * dirOmbrageJauneY + or[2] * dirOmbrageJauneZ)
              / Math.sqrt(dirOmbrageJauneX * dirOmbrageJauneX
                  + dirOmbrageJauneY * dirOmbrageJauneY + dirOmbrageJauneZ
                  * dirOmbrageJauneZ);

          double luminositeNoir = 0.5, luminositeJaune = 0.8;
          ombrageNoir = luminositeNoir + ombrageNoir * (1 - luminositeNoir);
          ombrageJaune = luminositeJaune + ombrageJaune * (1 - luminositeJaune);

          if (ombrageNoir <= 0) {
            col = Color.BLACK;
          } else if (ombrageJaune <= 0) {
            col = new Color((int) (255.0 * ombrageNoir),
                (int) (255.0 * ombrageNoir), 0);
          } else {
            col = new Color((int) (255.0 * ombrageNoir),
                (int) (255.0 * ombrageNoir),
                (int) (255.0 * ombrageJaune * ombrageNoir), transparence);
          }
        }

        pv.draw(col, t.getGeom());
      }
    };
  }

  public static GeneralisationSymbolisation triangleOmbrageOpaque(
      final Color col_retourne, final double dirOmbrageNoirX,
      final double dirOmbrageNoirY, final double dirOmbrageNoirZ,
      final double dirOmbrageJauneX, final double dirOmbrageJauneY,
      final double dirOmbrageJauneZ) {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        ITriangleFace t = (ITriangleFace) obj;

        Color col;
        if (TriangleFacesMeasures.istReverted(t)) {
          col = col_retourne;
        } else {
          double[] or = TriangleFacesMeasures.getSlopeVector(t);

          double ombrageNoir = -(or[0] * dirOmbrageNoirX + or[1]
              * dirOmbrageNoirY + or[2] * dirOmbrageNoirZ)
              / Math.sqrt(dirOmbrageNoirX * dirOmbrageNoirX + dirOmbrageNoirY
                  * dirOmbrageNoirY + dirOmbrageNoirZ * dirOmbrageNoirZ);
          double ombrageJaune = -(or[0] * dirOmbrageJauneX + or[1]
              * dirOmbrageJauneY + or[2] * dirOmbrageJauneZ)
              / Math.sqrt(dirOmbrageJauneX * dirOmbrageJauneX
                  + dirOmbrageJauneY * dirOmbrageJauneY + dirOmbrageJauneZ
                  * dirOmbrageJauneZ);

          double luminositeNoir = 0.5, luminositeJaune = 0.8;
          ombrageNoir = luminositeNoir + ombrageNoir * (1 - luminositeNoir);
          ombrageJaune = luminositeJaune + ombrageJaune * (1 - luminositeJaune);

          if (ombrageNoir <= 0) {
            col = Color.BLACK;
          } else if (ombrageJaune <= 0) {
            col = new Color((int) (255.0 * ombrageNoir),
                (int) (255.0 * ombrageNoir), 0);
          } else {
            col = new Color((int) (255.0 * ombrageNoir),
                (int) (255.0 * ombrageNoir),
                (int) (255.0 * ombrageJaune * ombrageNoir));
          }
        }
        pv.draw(col, t.getGeom());
      }
    };
  }

  public static GeneralisationSymbolisation triangleHypsometrie() {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        ITriangleFace t = (ITriangleFace) obj;
        Color couleur = GeneralisationLegend.getTeinteHypsometrique((t
            .getNode1().getPosition().getZ()
            + t.getNode2().getPosition().getZ() + t.getNode3().getPosition()
            .getZ()) / 3);
        pv.draw(couleur, t.getGeom());
      }
    };
  }

  public static GeneralisationSymbolisation triangleContientBatiments() {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        ITriangleFace t = (ITriangleFace) obj;

        if (!TriangleFacesMeasures.contientBatiment(t)) {
          return;
        }

        pv.draw(Color.YELLOW, t.getGeom());
      }
    };
  }

  public static GeneralisationSymbolisation triangleVecteurPente(
      final Color couleur, final Color col_retourne) {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        ITriangleFace t = (ITriangleFace) obj;

        double facteur = 10.0;

        Color col;
        if (TriangleFacesMeasures.istReverted(t)) {
          col = col_retourne;
        } else {
          col = couleur;
        }
        double[] or = TriangleFacesMeasures.getSlopeVector(t);
        double x = TriangleFacesMeasures.getX(t), y = TriangleFacesMeasures
            .getY(t);
        pv.drawSegment(col, x, y, x + or[0] * facteur, y + or[1] * facteur, 2);
      }
    };
  }

  /**
   * mesos colores
   * @return
   */
  public static Symbolisation ilotColore() {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        // verification
        if (!(obj instanceof IUrbanBlock)) {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Mauvais type d'objet: "
              + obj.getClass().getSimpleName());
          return;
        }
        IUrbanBlock block = (IUrbanBlock) obj;

        if (!block.isColored()) {
          return;
        }

        // dessin de la surface
        pv.draw(GeneralisationLegend.ILOTS_GRISES_COULEUR, block.getGeom());
      }
    };
  }

  public static GeneralisationSymbolisation symboliseDessous(
      final int priority, final AbstractLayerGroup layerGroup,
      final SymbolList symbols) {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {

        if (obj.isDeleted()) {
          return;
        }
        if (!layerGroup.symbolisationDisplay) {

          IRoadLine tr = (IRoadLine) obj;
          if (tr.getImportance() == 4) {
            pv.draw(Color.BLUE, tr.getGeom(), 2, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND);
          } else if (tr.getImportance() == 3 || tr.getImportance() == 2) {
            pv.draw(Color.ORANGE, tr.getGeom(), 2, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND);
          } else {
            pv.draw(Color.DARK_GRAY, tr.getGeom(), 2, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND);
          }

          return;
        }

        if (!(obj instanceof IRoadLine)) {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Mauvais type!");
          return;
        }
        IRoadLine tr = (IRoadLine) obj;

        SymbolShape shape = symbols.getSymbolShapeBySymbolID(tr.getSymbolId());
        if (tr.getSymbolId() == -1) {

          pv.draw(Color.black, tr.getGeom(),
              tr.getWidth() * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
              BasicStroke.CAP_BUTT, BasicStroke.CAP_ROUND);

        } else if (shape.ext_width != 0 && shape.ext_priority == priority) {
          pv.draw(shape.ext_colour, tr.getGeom(),
              shape.ext_width * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
              BasicStroke.CAP_BUTT, BasicStroke.CAP_ROUND);
        }

      }
    };

  }

  public static GeneralisationSymbolisation symboliseDessous(
      final int priority, final SymbolList symbols) {
    return GeneralisationSymbolisation.symboliseDessous(priority,
        CartagenApplication.getInstance().getLayerGroup(), symbols);
  }

  public static GeneralisationSymbolisation symboliseDessus(final int priority,
      final AbstractLayerGroup layerGroup, final SymbolList symbols) {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {

        if (obj.isDeleted()) {
          return;
        }
        if (!layerGroup.symbolisationDisplay) {
          return;
        }

        if (!(obj instanceof IRoadLine)) {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Mauvais type!");
          return;
        }
        IRoadLine tr = (IRoadLine) obj;

        SymbolShape shape = symbols.getSymbolShapeBySymbolID(tr.getSymbolId());
        if (tr.getSymbolId() == -1) {

          pv.draw(tr.getFrontColor(), tr.getGeom(), tr.getInternWidth()

          * Legend.getSYMBOLISATI0N_SCALE() / 1000.0, BasicStroke.CAP_BUTT,
              BasicStroke.CAP_ROUND);
        } else if (shape.int_priority == priority) {
          pv.draw(shape.int_colour, tr.getGeom(),
              shape.int_width * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
              BasicStroke.CAP_BUTT, BasicStroke.CAP_ROUND);
        }

      }
    };

  }

  public static GeneralisationSymbolisation symboliseDessus(final int priority,
      final SymbolList symbols) {
    return GeneralisationSymbolisation.symboliseDessus(priority,
        CartagenApplication.getInstance().getLayerGroup(), symbols);
  }

  public static GeneralisationSymbolisation symboliseSeparator(
      final AbstractLayerGroup layerGroup, final SymbolList symbols) {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {

        if (obj.isDeleted()) {
          return;
        }
        if (!layerGroup.symbolisationDisplay) {
          return;
        }

        if (!(obj instanceof IRoadLine)) {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Mauvais type!");
          return;
        }
        IRoadLine tr = (IRoadLine) obj;

        SymbolShape shape = symbols.getSymbolShapeBySymbolID(tr.getSymbolId());
        if (tr.getSymbolId() == -1) {

          pv.draw(tr.getSeparatorColor(), tr.getGeom(), tr.getWidth()

          * Legend.getSYMBOLISATI0N_SCALE() / 1000.0, BasicStroke.CAP_BUTT,
              BasicStroke.CAP_ROUND);
        } else if (shape.sep_width != 0) {
          pv.draw(shape.sep_colour, tr.getGeom(),
              shape.sep_width * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
              BasicStroke.CAP_BUTT, BasicStroke.CAP_ROUND);
        }

      }
    };

  }

  public static GeneralisationSymbolisation symboliseSeparator(
      final SymbolList symbols) {
    return GeneralisationSymbolisation.symboliseSeparator(CartagenApplication
        .getInstance().getLayerGroup(), symbols);
  }

  public static GeneralisationSymbolisation noeudRouteDessusNew(
      final int priority, final AbstractLayerGroup layerGroup) {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }
        if (!layerGroup.symbolisationDisplay) {
          return;
        }

        if (!(obj instanceof INetworkNode)) {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Mauvais type!");
          return;
        }

        INetworkNode n = (INetworkNode) obj;
        SymbolShape shape = n.getMaxWidthSymbol();
        if (shape == null || n.getDegree() <= 1
            || priority != shape.int_priority) {
          return;// )return;
        }
        pv.drawCircle(shape.int_colour, n.getPosition().getX(), n.getPosition()
            .getY(), shape.int_width * Legend.getSYMBOLISATI0N_SCALE() / 1000.0);

      }
    };
  }

  public static GeneralisationSymbolisation noeudRouteDessusNew(
      final int priority) {
    return GeneralisationSymbolisation.noeudRouteDessusNew(priority,
        CartagenApplication.getInstance().getLayerGroup());
  }

  public static GeneralisationSymbolisation noeudRouteDessousNew(
      final int priority, final AbstractLayerGroup layerGroup) {
    return new GeneralisationSymbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }
        if (!layerGroup.symbolisationDisplay) {
          return;
        }

        if (!(obj instanceof INetworkNode)) {
          GeneralisationSymbolisation.logger.warn("probleme dans le dessin de "
              + obj + ". Mauvais type!");
          return;
        }

        INetworkNode n = (INetworkNode) obj;
        SymbolShape shape = n.getMaxWidthSymbol();
        if (shape == null || n.getDegree() <= 1
            || priority != shape.int_priority) {
          return;// )return;
        }
        pv.drawCircle(shape.ext_colour, n.getPosition().getX(), n.getPosition()
            .getY(), (shape.ext_width) * Legend.getSYMBOLISATI0N_SCALE()
            / 1000.0);

      }
    };
  }

  public static GeneralisationSymbolisation noeudRouteDessousNew(
      final int priority) {
    return GeneralisationSymbolisation.noeudRouteDessousNew(priority,
        CartagenApplication.getInstance().getLayerGroup());
  }

  /**
   * Draws an image symbol on a feature (whose geometry isn't drawn).
   * @param layerGroup
   * @param pv
   * @param obj
   * @param filename
   * @param width
   * @param height
   */
  public static void drawPtSymbolRaster(AbstractLayerGroup layerGroup,
      VisuPanel pv, IFeature obj, String filename, int width, int height) {
    ExternalGraphic tree = new ExternalGraphic();
    URL url = Symbolisation.class.getResource(filename);
    tree.setHref(url.toString());
    Image image = tree.getOnlineResource();
    drawPtSymbolRaster(layerGroup, pv, obj, image, width, height);
  }

  /**
   * Draws an image symbol on a feature (whose geometry isn't drawn).
   * @param layerGroup
   * @param pv
   * @param obj
   * @param image
   * @param width
   * @param height
   */
  public static void drawPtSymbolRaster(AbstractLayerGroup layerGroup,
      VisuPanel pv, IFeature obj, Image image, int width, int height) {
    int x = pv.coordToPixX(obj.getGeom().centroid().getX()) - width / 2;
    int y = pv.coordToPixY(obj.getGeom().centroid().getY()) - height / 2;
    pv.getG2D().drawImage(image, x, y, width, height, null, pv);
  }

  /**
   * Draws an image symbol on a feature (whose geometry isn't drawn).
   * @param layerGroup
   * @param pv
   * @param obj
   * @param image
   * @param width
   * @param height
   */
  public static void drawPtSymbolRaster(AbstractLayerGroup layerGroup,
      VisuPanel pv, IFeature obj, Image image) {
    int x = pv.coordToPixX(obj.getGeom().centroid().getX())
        - image.getWidth(pv) / 2;
    int y = pv.coordToPixY(obj.getGeom().centroid().getY())
        - image.getHeight(pv) / 2;
    pv.getG2D().drawImage(image, x, y, null, pv);
  }

  /**
   * Draws an image symbol on a feature (whose geometry isn't drawn).
   * @param layerGroup
   * @param pv
   * @param obj
   * @param image
   * @param sizeRatio
   */
  public static void drawPtSymbolRaster(AbstractLayerGroup layerGroup,
      VisuPanel pv, IFeature obj, Image image, float sizeRatio) {
    int x = pv.coordToPixX(obj.getGeom().centroid().getX())
        - Math.round(image.getWidth(pv) * sizeRatio / 2);
    int y = pv.coordToPixY(obj.getGeom().centroid().getY())
        - Math.round(image.getHeight(pv) * sizeRatio / 2);
    pv.getG2D().drawImage(image, x, y, null, pv);
  }

}
