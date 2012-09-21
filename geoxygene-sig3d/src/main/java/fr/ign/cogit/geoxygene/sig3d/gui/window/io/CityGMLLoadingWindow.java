package fr.ign.cogit.geoxygene.sig3d.gui.window.io;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.io.XML.citygml.ParserCityGMLV2;
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
 * Fenêtre permettant de gérer le chargement de CityGML Window to loadCityGML
 * data
 */
public class CityGMLLoadingWindow extends JDialog implements ActionListener {

  /**
     * 
     */
  private static final long serialVersionUID = -7310593855314831276L;
  InterfaceMap3D iMap3D;
  String file;

  // Bouton validation/annulation
  JButton ok = new JButton();
  JButton cancel = new JButton();

  JCheckBox jCBKeepCoordinate;
  JComboBox jCBLOD;

  /**
   * Constructeur de la fenêtre
   * 
   * @param iMap3D l'interface de carte dans laquelle on ajoutera les données
   * @param file le fichier que l'on souhaite charger
   */
  public CityGMLLoadingWindow(InterfaceMap3D iMap3D, String file) {

    super();

    // Elle est rendue modale
    this.setFocusable(true);
    this.setModal(true);

    this.setTitle(Messages.getString("CityGMLLoadingWindow.Title")); //$NON-NLS-1$
    this.setLayout(null);

    this.iMap3D = iMap3D;
    this.file = file;

    JLabel label = new JLabel(Messages.getString("CityGMLLoadingWindow.LOD"));
    label.setBounds(10, 10, 200, 30);
    this.add(label);

    Object[] l = { 0, 1, 2, 3, 4 };
    this.jCBLOD = new JComboBox(l);
    this.jCBLOD.setBounds(220, 10, 50, 30);
    this.jCBLOD.setSelectedIndex(1);
    this.add(this.jCBLOD);

    this.jCBKeepCoordinate = new JCheckBox(
        Messages.getString("CityGMLLoadingWindow.Translate"), false);
    this.jCBKeepCoordinate.setBounds(10, 50, 250, 30);
    this.add(this.jCBKeepCoordinate);

    // Boutons de validations
    this.ok.setBounds(80, 90, 100, 20);
    this.ok.setText(Messages.getString("3DGIS.OK")); //$NON-NLS-1$
    this.ok.addActionListener(this);
    this.add(this.ok);

    this.cancel.setBounds(180, 90, 100, 20);
    this.cancel.setText(Messages.getString("3DGIS.NO")); //$NON-NLS-1$
    this.cancel.addActionListener(this);
    this.add(this.cancel);

    this.setSize(330, 180);

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object o = e.getSource();

    if (o.equals(this.ok)) {
      try {

        // On récupère le LOD
        int lod = Integer.parseInt(this.jCBLOD.getSelectedItem().toString());

        // On charge les données
        List<FT_FeatureCollection<IFeature>> lFeatureCollections = ParserCityGMLV2
            .readCityGMLFile(new File(this.file), lod,
                this.jCBKeepCoordinate.isSelected());

        // On récupère la carte courrante
        Map3D carte = this.iMap3D.getCurrent3DMap();

        // On récupère le nombre de couches à charger
        int nbColl = lFeatureCollections.size();

        // Pour chaque élément on créé une couche vectorielle
        // On affecte son nom
        // On l'ajoute dans la carte
        for (int i = 0; i < nbColl; i++) {

          VectorLayer vL = new VectorLayer(lFeatureCollections.get(i),
              ParserCityGMLV2.NOM_COUCHE[i] + ParserCityGMLV2.NB_LOADING);
          carte.addLayer(vL);

        }

      } catch (Exception e1) {
        // TODO Auto-generated catch block
        JOptionPane.showMessageDialog(this.iMap3D,
            Messages.getString("CityGMLLoadingWindow.Error"),
            Messages.getString("CityGMLLoadingWindow.Title"),
            JOptionPane.ERROR_MESSAGE);
        e1.printStackTrace();
        return;

      }

      this.dispose();

    }

    if (o.equals(this.cancel)) {
      this.dispose();

    }
  }

  public static void main(String[] args) {
    (new CityGMLLoadingWindow(null, "")).setVisible(true);
  }

}
