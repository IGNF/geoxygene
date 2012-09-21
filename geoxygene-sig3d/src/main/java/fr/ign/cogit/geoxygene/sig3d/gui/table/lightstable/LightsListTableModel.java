package fr.ign.cogit.geoxygene.sig3d.gui.table.lightstable;

import java.util.List;

import javax.media.j3d.PointLight;
import javax.swing.table.AbstractTableModel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;

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
 * Modèle de la table permettant de représenter les lumières ModelTable for
 * ligths
 */
public class LightsListTableModel extends AbstractTableModel {

  /**
     * 
     */
  private static final long serialVersionUID = 6634202042845879109L;

  /**
   * Nombre total de colonnes
   */
  public static final int NB_COL = 4;

  @Override
  public void fireTableDataChanged() {
    // TODO Auto-generated method stub
    super.fireTableDataChanged();
  }

  /**
   * Indice de la colonne X
   */
  public static final int IND_X = 0;

  /**
   * Indice de la colonne Y
   */
  public static final int IND_Y = 1;

  /**
   * Indice de la colonne Z
   */
  public static final int IND_Z = 2;

  /**
   * Indice de la colonne Couleur
   */
  public static final int IND_COL = 3;

  private List<PointLight> lLights;
  private InterfaceMap3D iMap3D;

  /**
   * Permet de créer le model
   */
  public LightsListTableModel(InterfaceMap3D iMap3D) {
    this.iMap3D = iMap3D;
    this.lLights = iMap3D.getLights();
  }

  @Override
  /**
   * Le nombre de colonnes est fixe
   */
  public int getColumnCount() {
    return LightsListTableModel.NB_COL;
  }

  @Override
  public String getColumnName(int column) {

    if (column == LightsListTableModel.IND_X) {
      return "X";
    }

    if (column == LightsListTableModel.IND_Y) {
      return "Y";
    }

    if (column == LightsListTableModel.IND_Z) {
      return "Z";
    }

    if (column == LightsListTableModel.IND_COL) {
      return Messages.getString("3DGIS.Color");
    }
    return super.getColumnName(column);
  }

  @Override
  /**
   * Le nombre de lignes dépend du nombre de couches
   */
  public int getRowCount() {
    return this.lLights.size();
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {

    if (rowIndex >= this.getRowCount()) {
      return null;
    }

    PointLight pl = this.lLights.get(rowIndex);

    Point3f point = new Point3f();
    pl.getPosition(point);

    if (columnIndex == LightsListTableModel.IND_X) {
      return point.x - this.iMap3D.getTranslate().x;
    } else if (columnIndex == LightsListTableModel.IND_Y) {
      return point.y - this.iMap3D.getTranslate().y;
    } else if (columnIndex == LightsListTableModel.IND_Z) {
      return point.z - this.iMap3D.getTranslate().z;
    } else if (columnIndex == LightsListTableModel.IND_COL) {

      Color3f color = new Color3f();

      pl.getColor(color);
      return color.get();
    }

    return null;
  }
}
