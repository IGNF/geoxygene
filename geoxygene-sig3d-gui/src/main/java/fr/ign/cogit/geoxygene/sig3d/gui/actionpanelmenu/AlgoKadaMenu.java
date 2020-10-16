package fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.choice.RepresentationWindowFactory;
import fr.ign.cogit.geoxygene.sig3d.simplification.aglokada.Algo_kada;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;

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
 * Fenetre de gestion de l'algorithme de Kada Window of the simplification
 * algorithm of Kada
 */
public class AlgoKadaMenu extends JPanel implements ActionListener {

  private final static Logger logger = LogManager.getLogger(AlgoKadaMenu.class
      .getName());
  // paramètres de l'algorithme
  JTextField jTFInitialValue;
  JTextField jTFFinalValue;

  JTextField jTFCutZ;

  JButton ok = new JButton();
  InterfaceMap3D iMap3D;

  // Les controles

  /**
	 */
  private static final long serialVersionUID = 1L;

  /**
   * Affichage de la fenêtre permettant de paramètrer l'algorithme
   * 
   * @param iCarte3D
   */
  public AlgoKadaMenu(InterfaceMap3D iCarte3D) {
    super();

    this.iMap3D = iCarte3D;

    this.setBorder(new TitledBorder(new EtchedBorder(), Messages
        .getString("FenetreSimplification.Title")));

    // Titre

    this.setLayout(null);

    // àtiquette Opération
    JLabel labelBorneIni = new JLabel();
    labelBorneIni.setBounds(10, 25, 100, 20);
    labelBorneIni.setText(Messages.getString("FenetreSimplification.Initial"));
    this.add(labelBorneIni);

    this.jTFInitialValue = new JTextField("0.3");
    this.jTFInitialValue.setBounds(200, 25, 30, 20);
    this.jTFInitialValue.setVisible(true);
    this.jTFInitialValue.addActionListener(this);

    this.add(this.jTFInitialValue);

    // Etiquette Objet2
    JLabel labelBorneFinale = new JLabel();
    labelBorneFinale.setBounds(10, 65, 200, 20);
    labelBorneFinale.setText(Messages
        .getString("FenetreSimplification.Longueur"));
    labelBorneFinale.setVisible(true);
    this.add(labelBorneFinale);

    this.jTFFinalValue = new JTextField("1.3");
    this.jTFFinalValue.setBounds(200, 65, 30, 20);
    this.jTFFinalValue.setVisible(true);
    this.jTFFinalValue.addActionListener(this);
    this.add(this.jTFFinalValue);

    // Etiquette Objet2
    JLabel labelCoupeZ = new JLabel();
    labelCoupeZ.setBounds(10, 105, 100, 20);
    labelCoupeZ.setText(Messages.getString("FenetreSimplification.ZCut"));
    labelCoupeZ.setVisible(true);
    this.add(labelCoupeZ);

    this.jTFCutZ = new JTextField("3");
    this.jTFCutZ.setBounds(200, 105, 30, 20);
    this.jTFCutZ.setVisible(true);
    this.jTFCutZ.addActionListener(this);
    this.add(this.jTFCutZ);

    this.ok.setBounds(50, 145, 150, 20);
    this.ok.setText(Messages.getString("3DGIS.Ok"));
    this.ok.addActionListener(this);
    this.add(this.ok);

    this.setSize(386, 206);
  }

  /**
   * Liste des actions
   */
  @Override
  public void actionPerformed(ActionEvent actionevent) {
    Object source = actionevent.getSource();

    if (source == this.ok) {

      String borneInitiale = this.jTFInitialValue.getText();
      String borneFinale = this.jTFFinalValue.getText();
      String coupeZ = this.jTFCutZ.getText();

      if (borneInitiale.isEmpty()) {
        return;
      }

      if (coupeZ.isEmpty()) {
        return;
      }

      if (coupeZ.isEmpty()) {
        return;
      }

      int nobj = this.iMap3D.getSelection().size();

      // calcul des paramètres
      Algo_kada kada = new Algo_kada();

      kada.setMinimalFinalLength(Double.parseDouble(borneFinale));
      kada.setElminationThreshold(Double.parseDouble(borneInitiale));
      kada.setCutThreshold(Double.parseDouble(coupeZ));

      FT_FeatureCollection<IFeature> lObj = new FT_FeatureCollection<IFeature>();

      // Exécution de l'algorithme pour tous les objets

      long t = System.currentTimeMillis();
      for (int i = 0; i < nobj; i++) {
        IFeature objG = this.iMap3D.getSelection().get(i);

        if (objG.getGeom().dimension() != 3) {

          continue;
        }

        GM_Solid s = (GM_Solid) objG.getGeom();
        kada.process(s);

        List<GM_Solid> lCorps = kada.getLSolid2();

        int nbCorps = lCorps.size();

        for (int j = 0; j < nbCorps; j++) {

          IFeature obj = new DefaultFeature(lCorps.get(j));
          lObj.add(obj);
        }

      }

      int nObj = lObj.size();

      if (nObj == 0) {

        JOptionPane.showMessageDialog(this,
            Messages.getString("3DGIS.CalculFail"),
            Messages.getString("FenetreSimplification.Title"),
            JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      AlgoKadaMenu.logger.info(Messages.getString("3DGIS.CalculSuccess")
          + (System.currentTimeMillis() - t) + " ms");
      // On ajoute la couche

      ((JDialog) RepresentationWindowFactory.generateDialog(this.iMap3D, lObj))
          .setVisible(true);
      JOptionPane.showMessageDialog(this,
          Messages.getString("3DGIS.CalculSuccess"),
          Messages.getString("FenetreSimplification.Title"),
          JOptionPane.INFORMATION_MESSAGE);
      return;
    }

  }

}
