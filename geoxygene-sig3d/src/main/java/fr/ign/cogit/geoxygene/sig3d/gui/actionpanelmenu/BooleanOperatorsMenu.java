package fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.calculation.BooleanOperators;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.choice.RepresentationWindowFactory;
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
 * Fenetre permettant de controer les Opérateurs boolean Windows of this class
 * manage boolean operations
 */
public class BooleanOperatorsMenu extends JPanel implements ActionListener {
  // Formulaire permettant de choisir les 2 objets et l'Opérateur
  JComboBox<String> choixop;
  JTextField JTFfeat1 = new JTextField();
  JTextField JTFfeat2 = new JTextField();
  JButton sel1 = new JButton();
  JButton sel2 = new JButton();
  JButton ok = new JButton();
  InterfaceMap3D iMap3D;

  IFeature feat1;
  IFeature feat2;

  public static final String[] choix = new String[] { "Interesection", "Union",
      "Difference" };

  // Les controles

  /**
	 */
  private static final long serialVersionUID = 1L;

  /**
   * Initialisation des composants
   * 
   * @param iMap3D
   */
  public BooleanOperatorsMenu(InterfaceMap3D iMap3D) {
    super();

    this.iMap3D = iMap3D;
    this.setBorder(new TitledBorder(new EtchedBorder(), Messages
        .getString("ColculOpBoolean.Title")));

    // Titre

    this.setLayout(null);
    this.setSize(386, 206);
    this.setVisible(true);

    // àtiquette Opération
    JLabel op = new JLabel();
    op.setBounds(10, 25, 100, 20);
    op.setText("Operation");
    this.add(op);

    this.choixop = new JComboBox<String>(BooleanOperatorsMenu.choix);
    this.choixop.setBounds(150, 25, 100, 20);
    this.choixop.setVisible(true);
    this.add(this.choixop);

    // étiquette Objet1
    JLabel label1 = new JLabel();
    label1.setBounds(10, 65, 50, 20);
    label1.setText(Messages.getString("3DGIS.Objet") + " 1");
    label1.setVisible(true);
    this.add(label1);

    // CheckBox objet
    this.JTFfeat1.setBounds(150, 65, 100, 20);
    this.JTFfeat1.setEnabled(false);
    this.JTFfeat1.setVisible(true);
    this.add(this.JTFfeat1);

    // Bouton sel 1
    this.sel1.setBounds(250, 65, 50, 20);
    this.sel1.setText(Messages.getString("3DGIS.Browse"));
    this.sel1.setVisible(true);
    this.sel1.addActionListener(this);
    this.add(this.sel1);

    // Etiquette Objet2
    JLabel label2 = new JLabel();
    label2.setBounds(10, 105, 50, 20);
    label2.setText(Messages.getString("3DGIS.Objet") + " 2");
    label2.setVisible(true);
    this.add(label2);

    // CheckBox objet2
    this.JTFfeat2.setBounds(150, 105, 100, 20);
    this.JTFfeat2.setEnabled(false);
    this.JTFfeat2.setVisible(true);
    this.add(this.JTFfeat2);

    // Bouton sel 2
    this.sel2.setBounds(250, 105, 50, 20);
    this.sel2.setText(Messages.getString("3DGIS.Browse"));
    this.sel2.setVisible(true);
    this.sel2.addActionListener(this);
    this.add(this.sel2);

    this.ok.setBounds(150, 145, 100, 20);
    this.ok.setText(Messages.getString("3DGIS.Ok"));
    this.ok.addActionListener(this);
    this.add(this.ok);

  }

  @Override
  public void actionPerformed(ActionEvent actionevent) {
    Object source = actionevent.getSource();

    // récupère l'objet sélecionné dans la fenetre et l'affecte à objet1
    if (source.equals(this.sel1)) {
      IFeatureCollection<IFeature> lObj = this.iMap3D.getSelection();
      if (lObj.size() == 0) {
        JOptionPane.showMessageDialog(this, "Pas d'objet sélectionné",
            "Selection", JOptionPane.INFORMATION_MESSAGE);
        return;

      }
      IFeature objetgeo = lObj.get(0);

      if (objetgeo.getGeom().dimension() != 3) {

        JOptionPane.showMessageDialog(this, "Cet objet n'est pas un corps",
            "Selection", JOptionPane.INFORMATION_MESSAGE);
        return;
      }

      this.feat1 = objetgeo;

      this.JTFfeat1.setText(Messages.getString("3DGIS.Objet") + " 1 "
          + Messages.getString("3DGIS.Ok"));
      return;

    }

    // récupère l'objet sélectionné dans la fentre et l'affecte à objet2
    if (source.equals(this.sel2)) {
      IFeatureCollection<IFeature> lObj = this.iMap3D.getSelection();
      if (lObj.size() == 0) {
        JOptionPane.showMessageDialog(this,
            Messages.getString("3DGIS.NoSlection"),
            Messages.getString("ColculOpBoolean.Title"),
            JOptionPane.INFORMATION_MESSAGE);
        return;

      }
      IFeature objetgeo = lObj.get(0);

      if (objetgeo.getGeom().dimension() != 3) {

        JOptionPane.showMessageDialog(this,
            Messages.getString("3DGIS.IsNotSolid"),
            Messages.getString("ColculOpBoolean.Title"),
            JOptionPane.INFORMATION_MESSAGE);
        return;
      }

      this.feat2 = objetgeo;

      this.JTFfeat2.setText(Messages.getString("3DGIS.Objet") + " 2 "
          + Messages.getString("3DGIS.Ok"));

      return;

    }

    // Effectue le calcul
    if (source.equals(this.ok)) {

      // Calcul effectué
      GM_Solid resultat = BooleanOperators.compute(this.feat1, this.feat2,
          (this.choixop.getSelectedIndex()));

      IFeature obj = new DefaultFeature(resultat);

      FT_FeatureCollection<IFeature> lObj = new FT_FeatureCollection<IFeature>();

      lObj.add(obj);

      if (resultat == null) {
        JOptionPane.showMessageDialog(this,
            Messages.getString("3DGIS.CalculFail"),
            Messages.getString("ColculOpBoolean.Title"),
            JOptionPane.INFORMATION_MESSAGE);
        return;
      }

      ((JDialog) RepresentationWindowFactory.generateDialog(this.iMap3D, lObj))
          .setVisible(true);

      JOptionPane.showMessageDialog(this.iMap3D,
          Messages.getString("3DGIS.CalculSuccess"),
          Messages.getString("ColculOpBoolean.Title"),
          JOptionPane.INFORMATION_MESSAGE);

    }

  }

}
