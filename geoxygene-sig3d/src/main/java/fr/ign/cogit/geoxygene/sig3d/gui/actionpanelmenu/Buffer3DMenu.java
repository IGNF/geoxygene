package fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.calculation.buffer3d.VContribution.ContributionAlgorithmBuffer3D;
import fr.ign.cogit.geoxygene.sig3d.calculation.buffer3d.VUnion.UnionAlgorithmBuffer3D;
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
 * Fenetre gêrant le déclenchement de l'algorithme de buffer3D avec les 2
 * algorithmes disponibles Windows for calculating 3D offsetting
 * 
 */
public class Buffer3DMenu extends JPanel implements ActionListener {

  // Champs concernant les paramètres de l'algo
  JTextField jTFBufferSize;
  JTextField jTFDetailSphere;

  JButton ok = new JButton();

  // Lien avec l'affichage pour visualiser le résultat
  InterfaceMap3D iMap3D;

  // Nom de la couche
  JComboBox<String> jCBBAlgorithmChoice;
  private static String[] formatsDisponibles = { "Convolution",
      "Divide & conquer" };

  private static final long serialVersionUID = 1L;

  /**
   * Initialisation d'un menu Attention un objet doit être sélectionné pour
   * valider, erreur sinon
   * 
   * @param iMap3D la carte dans laquelle sera affichée le résultat
   */
  public Buffer3DMenu(InterfaceMap3D iMap3D) {
    super();

    this.iMap3D = iMap3D;

    // Titre
    this.setBorder(new TitledBorder(new EtchedBorder(), Messages
        .getString("CalculBuffer3D.Title")));
    this.setLayout(null);

    // Formulaire
    JLabel tailleBuffer = new JLabel();
    tailleBuffer.setBounds(10, 25, 100, 20);
    tailleBuffer.setText(Messages.getString("CalculBuffer3D.Size"));
    this.add(tailleBuffer);

    this.jTFBufferSize = new JTextField("1");
    this.jTFBufferSize.setBounds(200, 25, 30, 20);
    this.jTFBufferSize.setVisible(true);
    this.jTFBufferSize.addActionListener(this);

    this.add(this.jTFBufferSize);

    // Etiquette Détail de la sphère
    JLabel seuilFusion = new JLabel();
    seuilFusion.setBounds(10, 65, 180, 20);
    seuilFusion.setText(Messages.getString("CalculBuffer3D.Detail"));
    seuilFusion.setVisible(true);
    this.add(seuilFusion);

    this.jTFDetailSphere = new JTextField("10");
    this.jTFDetailSphere.setBounds(200, 65, 30, 20);
    this.jTFDetailSphere.setVisible(true);
    this.jTFDetailSphere.addActionListener(this);
    this.add(this.jTFDetailSphere);

    // Etiquette Choix de l'algorithme
    JLabel labelCoupeZ = new JLabel();
    labelCoupeZ.setBounds(10, 105, 200, 20);
    labelCoupeZ.setText("Choix de l'algorithme");
    labelCoupeZ.setVisible(true);
    this.add(labelCoupeZ);

    this.jCBBAlgorithmChoice = new JComboBox<String>(Buffer3DMenu.formatsDisponibles);
    this.jCBBAlgorithmChoice.setBounds(200, 105, 150, 20);
    this.jCBBAlgorithmChoice.setVisible(true);
    this.jCBBAlgorithmChoice.addActionListener(this);
    this.add(this.jCBBAlgorithmChoice);

    this.ok.setBounds(100, 145, 150, 20);
    this.ok.setText(Messages.getString("3DGIS.Ok"));
    this.ok.addActionListener(this);
    this.add(this.ok);

    this.setSize(386, 206);
    this.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent actionevent) {
    Object source = actionevent.getSource();

    if (source == this.ok) {

      if (this.iMap3D.getSelection().size() == 0) {

        return;
      }
      IFeature feat1 = this.iMap3D.getSelection().get(0);

      String tailleBuffer = this.jTFBufferSize.getText();

      if (tailleBuffer == null) {
        return;
      }

      String detailSphere = this.jTFDetailSphere.getText();

      if (detailSphere == null) {
        return;
      }

      int intDetailSphere = Integer.parseInt(detailSphere);
      int intTailleBuffer = Integer.parseInt(tailleBuffer);

      int ind = this.jCBBAlgorithmChoice.getSelectedIndex();

      // Lance l'algorithme sélectionné
      if (ind == 0) {

        ContributionAlgorithmBuffer3D.offsetting(feat1, intTailleBuffer,
            intDetailSphere, this.iMap3D.getCurrent3DMap());

      } else {

        UnionAlgorithmBuffer3D.offsetting(feat1, intTailleBuffer,
            intDetailSphere, this.iMap3D.getCurrent3DMap());

      }

      return;
    }

  }

}
