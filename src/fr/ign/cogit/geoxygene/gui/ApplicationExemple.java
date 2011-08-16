package fr.ign.cogit.geoxygene.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map.Entry;

import javax.swing.JColorChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IDataSet;
import fr.ign.cogit.geoxygene.util.loader.Chargement;

/**
 * Exemple de d'utilisation de l'interface de géoxygene
 * @author julien Gaffuri 6 mars 2009
 * 
 */
public class ApplicationExemple {
  static Logger logger = Logger.getLogger(ApplicationExemple.class.getName());

  /**
   * @param args
   */
  public static void main(String[] args) {

    // construction de fenetre
    final InterfaceGeoxygene fr = new InterfaceGeoxygene();

    /**
     * Ajout d'un menu et d'un menuitem associé pour le chargement de shapefiles
     */
    JMenu menuChargement = new JMenu("Chargement");
    menuChargement.setFont(fr.getMenu().getFont());
    JMenuItem menuItemDernierChargement = new JMenuItem(
        "Recharger les derniers fichiers chargés");
    final Chargement chargement = Chargement.charge("chargement.xml");
    if (chargement == null) {
      menuItemDernierChargement.setEnabled(false);
    }
    menuItemDernierChargement.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (chargement != null) {
          IDataSet dataSet = fr.getPanelVisu().getDataset();
          dataSet.setNom(chargement.getDataSet().getNom());
          dataSet.setTypeBD(chargement.getDataSet().getTypeBD());
          dataSet.setModele(chargement.getDataSet().getModele());
          dataSet.setZone(chargement.getDataSet().getZone());
          dataSet.setCommentaire(chargement.getDataSet().getCommentaire());
          dataSet.setDate(chargement.getDataSet().getDate());
          ApplicationExemple.logger.info("Chargement de "
              + chargement.getFichiers().size() + " fichiers");
          for (Entry<String, String> entry : chargement.getFichiers()
              .entrySet()) {
            ApplicationExemple.logger.info("Chargement du fichier "
                + entry.getValue() + " pour la population " + entry.getKey());
            fr.chargeShapefile(entry.getValue(), entry.getKey());
          }
        }
      }
    });
    menuItemDernierChargement.setFont(fr.getMenu().getFont());
    menuChargement.add(menuItemDernierChargement);

    JMenuItem menuItemChargement = new JMenuItem("Charger des données");
    menuItemChargement.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        fr.chargeShapefiles();
      }
    });
    menuItemChargement.setFont(fr.getMenu().getFont());
    menuChargement.add(menuItemChargement);
    fr.getMenu().add(menuChargement, 0);

    /**
     * Ajout d'un menu pour éditer la légende de l'affichage
     */
    JMenu menu = new JMenu("Légende");
    JMenuItem menuItem = new JMenuItem("Editeur de SLD");
    menuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        new FrameEditeurSLD(fr).setVisible(true);
      }
    });
    menu.add(menuItem);
    JMenuItem menuItemFond = new JMenuItem(
        "Editeur la couleur du fond de carte");
    menuItemFond.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Color newColor = JColorChooser.showDialog(fr,
            "Choisir une couleur pour le trait", fr.getPanelVisu()
                .getBackground());
        fr.getPanelVisu().setBackground(newColor);
      }
    });
    menu.setFont(fr.getMenu().getFont());
    menuItem.setFont(fr.getMenu().getFont());
    menuItemFond.setFont(fr.getMenu().getFont());
    menu.add(menuItemFond);
    fr.getMenu().add(menu, fr.getMenu().getMenuCount() - 1);

    // affichage de la fenetre
    fr.setVisible(true);
  }
}
