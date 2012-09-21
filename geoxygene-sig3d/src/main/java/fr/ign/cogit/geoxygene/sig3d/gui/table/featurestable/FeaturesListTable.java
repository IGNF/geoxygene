package fr.ign.cogit.geoxygene.sig3d.gui.table.featurestable;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
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
 * Constructeur de la table contenant les entités. Le rendu et le model sont
 * affectés ici Class enable the construction of a JTable containing rendering
 * and model
 */
public class FeaturesListTable extends JTable {

  /**
	 * 
	 */
  private static final long serialVersionUID = -1114696091139126341L;

  /**
   * Créer une table affichant des entités liées à un environnement 3D (pour
   * emttre à jour les sélection)
   * 
   * @param featColl Les entités que l'on souhaite voir apparaître dans la table
   * @param iMap3D l'environnement 3D
   */
  public FeaturesListTable(FT_FeatureCollection<IFeature> featColl,
      InterfaceMap3D iMap3D) {

    super();
    this.setModel(new FeaturesListTableModel(featColl));
    this.setDefaultRenderer(Object.class, new FeaturesListTableRenderer(iMap3D));
    this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    // On met une largeur à la colonne géométrie
    TableColumnModel columnModel = this.getColumnModel();
    TableColumn col = columnModel.getColumn(columnModel.getColumnCount() - 1);
    col.setPreferredWidth(250);
  }

  /**
   * Met à jour le contenu de la table
   */
  public void refresh() {

    ((FeaturesListTableModel) this.getModel()).fireTableDataChanged();
  }

}
