package fr.ign.cogit.geoxygene.sig3d.gui.table.layerstable;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.semantic.Layer;
import fr.ign.cogit.geoxygene.sig3d.semantic.Map3D;

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
 * Modele de la table contenant la liste de couche Table Model for the table
 * containing the layer list
 */
public class LayersListTableModel extends AbstractTableModel {

  /**
   * Indice de la colonne de nom lors de la représentation d'un couche
   */
  public static final int IND_COLNAME = 3;

  /**
   * Indice de la colonne de visibilité lors de la représentation d'un couche
   */
  public static final int IND_COLVIS = 0;

  /**
   * Indice de la colonne de selectabilité lors de la représentation d'un couche
   */
  public static final int IND_COLSEL = 1;

  /**
   * Indice de la colonne de représentation lors de la représentation d'un
   * couche
   */
  public static final int IND_COLREP = 2;
  /**
   * Nombre total de colonnes
   */
  public static final int NB_COL = 4;

  private static final long serialVersionUID = 1L;
  private InterfaceMap3D iMap3D;

  /**
   * Constructeur faisant le lien avec l'interface d'affichage de la carte dans
   * le but de gèrer les différentes interactions (affichage/suppression de
   * cartes)
   * 
   * @param iMap3D
   */
  public LayersListTableModel(InterfaceMap3D iMap3D) {
    this.iMap3D = iMap3D;
  }

  @Override
  /**
   * Le nombre de colonnes est fixe
   */
  public int getColumnCount() {
    return LayersListTableModel.NB_COL;
  }

  @Override
  /**
   * Le nombre de lignes dépend du nombre de couches
   */
  public int getRowCount() {
    Map3D carte = this.iMap3D.getCurrent3DMap();

    if (carte == null) {

      return 0;
    }

    ArrayList<Layer> lCouches = carte.getLayerList();

    if (lCouches == null) {
      return 0;
    }

    return this.iMap3D.getCurrent3DMap().getLayerList().size();
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {

    if (rowIndex >= this.getRowCount()) {
      return null;
    }

    Layer c = this.iMap3D.getCurrent3DMap().getLayerList().get(rowIndex);

    if (columnIndex == LayersListTableModel.IND_COLNAME) {
      return c.getLayerName();
    } else if (columnIndex == LayersListTableModel.IND_COLVIS) {
      return c.isVisible();
    } else if (columnIndex == LayersListTableModel.IND_COLSEL) {
      return c.isSelectable();
    } else if (columnIndex == LayersListTableModel.IND_COLREP) {
      return c;
    }

    return null;
  }

  /**
   * Renvoie la couche associée à une ligne
   * 
   * @param rowIndex
   * @return renvoie la couche se trouvant à la ligne rowIndex du model
   */
  public Layer getLayer(int rowIndex) {
    Layer c = this.iMap3D.getCurrent3DMap().getLayerList().get(rowIndex);
    return c;
  }

  @Override
  public String getColumnName(int column) {

    if (column == LayersListTableModel.IND_COLNAME) {
      return Messages.getString("3DGIS.Name");

    } else if (column == LayersListTableModel.IND_COLVIS) {
      return Messages.getString("3DGIS.Visible");
    } else if (column == LayersListTableModel.IND_COLSEL) {
      return Messages.getString("3DGIS.Select");
    } else if (column == LayersListTableModel.IND_COLREP) {
      return "";
    }
    return "";
  }

  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    if (aValue instanceof Boolean) {

      if (columnIndex == LayersListTableModel.IND_COLVIS) {
        this.getLayer(rowIndex).setVisible((Boolean) aValue);

      } else if (columnIndex == LayersListTableModel.IND_COLSEL) {
        this.getLayer(rowIndex).setSelectable((Boolean) aValue);

      }
    }

  }

  @Override
  /**
   * Pas d'édition possible pour l'instant
   */
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return false;
  }

}
