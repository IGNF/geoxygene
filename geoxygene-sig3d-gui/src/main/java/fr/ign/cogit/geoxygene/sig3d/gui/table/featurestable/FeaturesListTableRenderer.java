package fr.ign.cogit.geoxygene.sig3d.gui.table.featurestable;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.Representation;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.representation.I3DRepresentation;

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
 * Le rendu graphique de la table contenant la liste des entités
 * 
 * The graphic render of the feature table
 * 
 */
public class FeaturesListTableRenderer extends DefaultTableCellRenderer {
  // La carte en relation avec la table
  private InterfaceMap3D iMap3D;

  public FeaturesListTableRenderer(InterfaceMap3D iMap3D) {
    super();
    this.iMap3D = iMap3D;
  }

  private static final long serialVersionUID = 1L;

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    // L'affichage utilisé est celui de base
    Component comp = super.getTableCellRendererComponent(table, value,
        isSelected, hasFocus, row, column);

    TableModel tModel = table.getModel();

    if (tModel instanceof FeaturesListTableModel) {
      FeaturesListTableModel tLEModel = (FeaturesListTableModel) tModel;
      // On récupère l'entité attachée à la ligne
      IFeature feat = tLEModel.getFeature(row);
      Representation rep = feat.getRepresentation();

      if (rep != null) {
        if (rep instanceof I3DRepresentation) {
          // Si représentation il y a, on applique la sélection
          I3DRepresentation iR3D = (I3DRepresentation) rep;
          boolean fSelected = iR3D.isSelected();
          // Changement d'état de sélection
          // On modifie l'état de sélection
          if (fSelected != isSelected) {
            if (isSelected) {
              if (this.iMap3D != null) {

                this.iMap3D.addToSelection(feat);

              }

            } else {
              if (this.iMap3D != null) {

                this.iMap3D.getSelection().remove(feat);
                iR3D.setSelected(false);
              }

            }

          }

        }
      }
    }

    return comp;
  }
}
