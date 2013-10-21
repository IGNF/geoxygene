/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.VisuPanel;
import fr.ign.cogit.cartagen.software.interfacecartagen.menus.DataThemesGUIComponent;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * @author julien Gaffuri 6 mars 2009
 */
public class GeneralisationVisuPanelComplement {
  static Logger logger = Logger
      .getLogger(GeneralisationVisuPanelComplement.class.getName());

  // le panneau a completer
  VisuPanel pv;

  IBuilding buildingToDisplace = null;

  /**
	 */
  private static GeneralisationVisuPanelComplement content = null;

  public static GeneralisationVisuPanelComplement getInstance() {
    if (GeneralisationVisuPanelComplement.content == null) {
      GeneralisationVisuPanelComplement.content = new GeneralisationVisuPanelComplement();
    }
    return GeneralisationVisuPanelComplement.content;
  }

  private GeneralisationVisuPanelComplement() {
    this.pv = CartagenApplication.getInstance().getFrame().getVisuPanel();
  }

  /**
	 * 
	 */
  public void add() {

    // ajoute ecouteur de mouvement de souris
    this.pv.addMouseMotionListener(new MouseMotionListener() {

      @Override
      public void mouseMoved(MouseEvent e) {

      }

      @Override
      public void mouseDragged(MouseEvent e) {

        // si un batiment peut etre déplacé
        if (DataThemesGUIComponent.getInstance().getBuildingMenu().mBuildingDeplacement
            .isSelected()) {

          // get click coordinates
          double x = GeneralisationVisuPanelComplement.this.pv.pixToCoordX(e
              .getX());
          double y = GeneralisationVisuPanelComplement.this.pv.pixToCoordY(e
              .getY());
          /*
           * GM_Point p = new GM_Point(new DirectPosition(x, y));
           * 
           * // Building displacement IBuilding buildingToDisplace = null; for
           * (IBuilding building : CartagenApplication.getInstance()
           * .getDataSet().getBuildings()) { if (building.getGeom() == null ||
           * building.getGeom().isEmpty()) { continue; } if
           * (building.isDeleted()) { continue; } IPolygon geom =
           * building.getGeom(); if (geom.distance(p) <= 1) { buildingToDisplace
           * = building; break; } }
           */
          if (GeneralisationVisuPanelComplement.this.buildingToDisplace != null) {
            IPolygon geom = GeneralisationVisuPanelComplement.this.buildingToDisplace
                .getGeom();
            GeneralisationVisuPanelComplement.this.buildingToDisplace
                .setGeom(geom.translate(x - geom.centroid().getX(), y
                    - geom.centroid().getY(), 0));
            GeneralisationVisuPanelComplement.this.buildingToDisplace
                .registerDisplacement();
          }

        }

        // Rafraichissement si necessaire
        if (!GeneralisationVisuPanelComplement.this.pv.automaticRefresh) {
          try {
            GeneralisationVisuPanelComplement.this.pv.imageUpdate();
          } catch (InterruptedException e1) {
          }
          GeneralisationVisuPanelComplement.this.pv.repaint();
        }

      }

    });

    // ajoute ecouteur de clic de souris
    this.pv.addMouseListener(new MouseListener() {

      @Override
      public void mousePressed(MouseEvent e) {

        if (e.getButton() == 0) {
          return;
        }

        // Dessin de segment si demande
        if (CartagenApplication.getInstance().getFrame().getMenu().mGeomPoolDrawSegments
            .isSelected()) {
          double x = GeneralisationVisuPanelComplement.this.pv.pixToCoordX(e
              .getX()), y = GeneralisationVisuPanelComplement.this.pv
              .pixToCoordY(e.getY());
          if (CartagenApplication.getInstance().getFrame().getMenu().mGeomPoolDrawSegmentsCoords
              .size() == 0) {
            CartagenApplication.getInstance().getFrame().getMenu().mGeomPoolDrawSegmentsCoords
                .add(new DirectPosition(x, y));
          } else {
            ILineSegment seg = new GM_LineSegment(CartagenApplication
                .getInstance().getFrame().getMenu().mGeomPoolDrawSegmentsCoords
                .get(0), new DirectPosition(x, y));
            CartagenApplication.getInstance().getFrame().getMenu().mGeomPoolDrawSegmentsCoords
                .clear();
            CartagenApplication.getInstance().getFrame().getLayerManager()
                .addToGeometriesPool(seg);
          }

        }

        if (e.getButton() == MouseEvent.BUTTON1) {

          if (DataThemesGUIComponent.getInstance().getBuildingMenu().mBuildingDeplacement
              .isSelected()) {

            // get click coordinates
            double x = GeneralisationVisuPanelComplement.this.pv.pixToCoordX(e
                .getX());
            double y = GeneralisationVisuPanelComplement.this.pv.pixToCoordY(e
                .getY());
            GM_Point p = new GM_Point(new DirectPosition(x, y));

            // Building displacement
            GeneralisationVisuPanelComplement.this.buildingToDisplace = null;
            for (IBuilding building : CartAGenDocOld.getInstance()
                .getCurrentDataset().getBuildings()) {
              if (building.getGeom() == null || building.getGeom().isEmpty()) {
                continue;
              }
              if (building.isDeleted()) {
                continue;
              }
              IPolygon geom = building.getGeom();
              if (geom.distance(p) <= 1) {
                GeneralisationVisuPanelComplement.this.buildingToDisplace = building;
                break;
              }
            }

          }

        } else if (e.getButton() == MouseEvent.BUTTON2) {
        } else if (e.getButton() == MouseEvent.BUTTON3) {
        } else {
          GeneralisationVisuPanelComplement.logger
              .error("clic de souris avec bouton inconnu (mousePressed): "
                  + e.getButton());
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        if (e.getButton() == 0) {
          return;
        }
        if (e.getButton() == MouseEvent.BUTTON1) {
          GeneralisationVisuPanelComplement.this.buildingToDisplace = null;
        } else if (e.getButton() == MouseEvent.BUTTON2) {
        } else if (e.getButton() == MouseEvent.BUTTON3) {
        } else {
          GeneralisationVisuPanelComplement.logger
              .error("clic de souris avec bouton inconnu (mouseReleased): "
                  + e.getButton());
        }
      }

      @Override
      public void mouseClicked(MouseEvent e) {
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent e) {
        this.mouseReleased(e);
      }

    });

  }
}
