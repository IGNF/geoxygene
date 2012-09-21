package fr.ign.cogit.geoxygene.sig3d.gui.table.layerstable;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTable;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.gui.MainWindow;
import fr.ign.cogit.geoxygene.sig3d.gui.menu.DTMMenu;
import fr.ign.cogit.geoxygene.sig3d.gui.menu.VectorialLayerMenu;
import fr.ign.cogit.geoxygene.sig3d.gui.table.featurestable.FeaturesListTable;
import fr.ign.cogit.geoxygene.sig3d.semantic.DTM;
import fr.ign.cogit.geoxygene.sig3d.semantic.Layer;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 *  
 * @version 0.1
 * 
 * Classe permettant de gèrer les intéractions entre la table et l'utilisateur
 * 
 * - Clic droit : afffichage d'un menu contxtuel en fonction du type de couche
 * (CoucheVecteur ou MNT)
 * 
 * - Simple clic : influe sur la selectabilité et l'affichage en fonction de la
 * colonne
 * 
 * - Double clic : affichage d'une table sur la liste des entités de cette
 * couche (dans le cas d'un FeatureCollection)
 * 
 * 
 * 
 * Class allowing interaction between the table and the user
 * 
 */
public class LayersListTableListener extends MouseAdapter {

  private JTable layerTable;
  private MainWindow mainWindow;

  /**
   * Initialisation du listener
   * @param tableCouche la table sur laquelle on applique les résultats de
   *          l'écoute
   * @param fvg l'application que l'on écoute
   */
  public LayersListTableListener(JTable tableCouche, MainWindow fvg) {
    this.layerTable = tableCouche;
    this.mainWindow = fvg;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void mouseReleased(MouseEvent e) {
    Point p = new Point(e.getX(), e.getY());
    // On récupère la colonne et la ligne sélectionnées dans le modèle
    int rowView = this.layerTable.rowAtPoint(p);
    int colView = this.layerTable.columnAtPoint(p);

    int row = this.layerTable.convertRowIndexToModel(rowView);
    int col = this.layerTable.convertColumnIndexToModel(colView);

    LayersListTableModel mjtc = (LayersListTableModel) this.layerTable
        .getModel();
    Layer c = mjtc.getLayer(row);

    if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
      // On change la visibilité si la colonne de visibilité est
      // selectionnée
      if (col == LayersListTableModel.IND_COLVIS) {
        c.setVisible(!c.isVisible());
        mjtc.fireTableDataChanged();

      }
      // On change la selectabilité pour cette colonne
      if (col == LayersListTableModel.IND_COLSEL) {

        boolean select = c.isSelectable();

        c.setSelectable(!select);
        mjtc.fireTableDataChanged();

      }

    } else if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
      // On affiche la table contenant la liste des entités en cas de
      // double clic
      if (c instanceof FT_FeatureCollection<?>) {
        this.mainWindow.getInterfaceMap3D().setSelection(
            new FT_FeatureCollection<IFeature>());
        FeaturesListTable f = new FeaturesListTable(
            (FT_FeatureCollection<IFeature>) c,
            this.mainWindow.getInterfaceMap3D());

        this.mainWindow.getActionPanel().setActionComponent(f);

        // f.setTitle(c.getNomCouche());
        // f.setVisible(true);
      }

    } else if (e.isPopupTrigger() && this.layerTable.isEnabled()) {
      // On affiche le menu correspondant au type de couche
      JPopupMenu jmenu = null;

      if (c instanceof VectorLayer) {
        jmenu = new VectorialLayerMenu((VectorLayer) c, this.mainWindow
            .getInterfaceMap3D().getCurrent3DMap(), mjtc);

      } else if (c instanceof DTM) {
        jmenu = new DTMMenu((DTM) c, this.mainWindow.getInterfaceMap3D()
            .getCurrent3DMap(), mjtc);
      }

      if (jmenu == null) {
        return;
      }
      jmenu.show(this.layerTable, p.x, p.y);

    }

  }

}
