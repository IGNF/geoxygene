package fr.ign.cogit.geoxygene.sig3d.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import fr.ign.cogit.geoxygene.api.feature.Representation;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.table.layerstable.LayersListTableModel;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.BuildingModWindow;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.CartoonModWindow;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.OneColoredLayerWindow;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.SymbolWindow;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.TexturationWindow;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.ToponymWindow;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.choice.RepresentationWindowFactory;
import fr.ign.cogit.geoxygene.sig3d.representation.basic.BasicRep3D;
import fr.ign.cogit.geoxygene.sig3d.representation.modellingfile.RepresentationModel;
import fr.ign.cogit.geoxygene.sig3d.representation.sample.BuildingTexture;
import fr.ign.cogit.geoxygene.sig3d.representation.sample.ObjectCartoon;
import fr.ign.cogit.geoxygene.sig3d.representation.texture.TexturedSurface;
import fr.ign.cogit.geoxygene.sig3d.representation.toponym.BasicToponym3D;
import fr.ign.cogit.geoxygene.sig3d.semantic.Map3D;
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
 * Menu contextuel affiché lors d'un clic droit sur une couche de type
 * CoucheVecteur - Un menu édition permet d'éditer la représentation des entités
 * dont la représentation est : - Toponyme - BasiqueRep3D - RepresentationModel
 * - Un menu de suppression de couches Menu displayed when right click on an
 * instance of CoucheVecteur
 */
public class VectorialLayerMenu extends JPopupMenu {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  /**
   * On a besoin de la coucheVecteur de la carte 3D dans laquelle elle est
   * affichée et du modèle de la table
   * 
   * @param vL la couche sur laquelle on agit
   * @param map3D la carte
   * @param mjtc le tablemodel des couches
   */
  public VectorialLayerMenu(final VectorLayer vL, final Map3D map3D,
      final LayersListTableModel mjtc) {
    super();

    // Menu permettant d'étider le style d'une couche
    JMenuItem item1 = new JMenuItem();
    item1.setText(Messages.getString("MenuCoucheVecteur.Edition"));
    this.add(item1);

    item1.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (vL == null) {
          return;
        }

        if (vL.size() == 0) {

          return;
        }

        Representation rep = vL.get(0).getRepresentation();
        JDialog fen = null;

        // On affiche la fenêtre correspondant au style chargé
        if (rep instanceof BasicRep3D) {

          fen = new OneColoredLayerWindow(vL);

        } else if (rep instanceof BasicToponym3D) {
          fen = new ToponymWindow(vL);

        } else if (rep instanceof RepresentationModel) {
          fen = new SymbolWindow(vL);

        } else if (rep instanceof ObjectCartoon) {
          fen = new CartoonModWindow(vL);

        } else if (rep instanceof TexturedSurface) {
          fen = new TexturationWindow(vL);

        } else if (rep instanceof BuildingTexture) {
          fen = new BuildingModWindow(vL);

        }

        if (fen != null) {
          fen.setVisible(true);
        }

        mjtc.fireTableDataChanged();

      }
    });

    // Menu permettant de proposer une nouveau style à une couche
    JMenuItem item2 = new JMenuItem();
    item2.setText(Messages.getString("MenuCoucheVecteur.ChangeStyle"));
    this.add(item2);
    item2.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (vL == null) {
          return;
        }

        if (vL.size() == 0) {

          return;
        }
        // On modifie le style et on génère le dialogue associé à la
        // couche
        RepresentationWindowFactory.generateDialog(vL).setVisible(true);

        mjtc.fireTableDataChanged();

      }
    });

    // Menu permettant de supprimer une couche le style d'une couche
    JMenuItem item3 = new JMenuItem();
    item3.setText(Messages.getString("MenuCoucheVecteur.Delete"));
    this.add(item3);
    item3.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (vL == null) {
          return;
        }

        if (vL.size() == 0) {

          return;
        }
        map3D.removeLayer(vL.getLayerName());
        mjtc.fireTableDataChanged();

      }
    });

  }

}
