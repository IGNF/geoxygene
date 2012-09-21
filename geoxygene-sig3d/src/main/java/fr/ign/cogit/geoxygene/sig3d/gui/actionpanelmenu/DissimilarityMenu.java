package fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.jfree.ui.RefineryUtilities;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.calculation.Calculation3D;
import fr.ign.cogit.geoxygene.sig3d.calculation.Indicator;
import fr.ign.cogit.geoxygene.sig3d.calculation.ShapeIndicator;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.gui.window.result.DissimilarityCalculationDialog;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
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
 * Fenetre permettant de calculer la distance (ressemblance entre 2 objets)
 * 
 * This windows enable to evaluate dissimilarity between 2 objects
 */
public class DissimilarityMenu extends JPanel implements ActionListener {
  // Formulaire permettant de choisir les 2 objets et le style de calcul
  JComboBox opChoice;
  JTextField jTFFeat1 = new JTextField();
  JTextField jTFFeat2 = new JTextField();
  JButton sel1 = new JButton();
  JButton sel2 = new JButton();
  JButton ok = new JButton();
  InterfaceMap3D iMap3D;

  GM_Solid s1;
  GM_Solid s2;

  private final static Logger logger = Logger.getLogger(DissimilarityMenu.class
      .getName());

  public static final String[] choix = new String[] {
      Messages.getString("3DGIS.Distance"), Messages.getString("3DGIS.Aire"),
      Messages.getString("3DGIS.Volume") };

  // Les controles

  /**
	 */
  private static final long serialVersionUID = 1L;

  /**
   * Initialisation des composants
   * 
   * @param iMap3D
   */
  public DissimilarityMenu(InterfaceMap3D iMap3D) {
    super();

    this.iMap3D = iMap3D;

    this.setBorder(new TitledBorder(new EtchedBorder(), Messages
        .getString("FenetreDissimilarite.Title")));

    // Titre
    this.setLayout(null);
    this.setSize(386, 206);
    this.setVisible(true);

    // étiquette Opération
    JLabel op = new JLabel();
    op.setBounds(10, 25, 50, 20);
    op.setText(Messages.getString("3DIGS.Calcul"));
    this.add(op);

    // Choix de l'Opération

    this.opChoice = new JComboBox(DissimilarityMenu.choix);
    this.opChoice.setBounds(150, 25, 100, 20);
    this.opChoice.setVisible(true);
    this.add(this.opChoice);

    // àtiquette Objet1
    JLabel label1 = new JLabel();
    label1.setBounds(10, 65, 50, 20);
    label1.setText("Objet 1");
    label1.setVisible(true);
    this.add(label1);

    // CheckBox objet
    this.jTFFeat1.setBounds(150, 65, 100, 20);
    this.jTFFeat1.setVisible(true);
    this.add(this.jTFFeat1);

    // Bouton sel 1
    this.sel1.setBounds(250, 65, 50, 20);
    this.sel1.setText(Messages.getString("3DGIS.Browse"));
    this.sel1.setVisible(true);
    this.sel1.addActionListener(this);
    this.add(this.sel1);

    // Etiquette Objet2
    JLabel label2 = new JLabel();
    label2.setBounds(10, 105, 50, 20);
    label2.setText("Objet 2");
    label2.setVisible(true);
    this.add(label2);

    // CheckBox objet2
    this.jTFFeat2.setBounds(150, 105, 100, 20);
    this.jTFFeat2.setEnabled(false);
    this.jTFFeat2.setVisible(true);
    this.add(this.jTFFeat2);

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
      FT_FeatureCollection<IFeature> lObj = this.iMap3D.getSelection();
      if (lObj.size() == 0) {
        JOptionPane.showMessageDialog(this,
            Messages.getString("3DGIS.NoSlection"), "Selection",
            JOptionPane.INFORMATION_MESSAGE);
        return;

      }
      IFeature objetgeo = lObj.get(0);

      if (objetgeo.getGeom().dimension() != 3) {

        JOptionPane.showMessageDialog(this,
            Messages.getString("FenetreDissimilarite.IsNotSolid"),
            Messages.getString("FenetreDissimilarite.Title"),
            JOptionPane.INFORMATION_MESSAGE);
        return;
      }

      this.s1 = (GM_Solid) objetgeo.getGeom();

      this.jTFFeat1.setText("Objet 1 Ok");
      return;

    }

    // récupère l'objet sélectionné dans la fentre et l'affecte à objet2
    if (source.equals(this.sel2)) {
      FT_FeatureCollection<IFeature> lObj = this.iMap3D.getSelection();
      if (lObj.size() == 0) {
        JOptionPane.showMessageDialog(this,
            Messages.getString("3DGIS.NoSlection"),
            Messages.getString("FenetreDissimilarite.Title"),
            JOptionPane.INFORMATION_MESSAGE);
        return;

      }
      IFeature objetgeo = lObj.get(0);

      if (objetgeo.getGeom().dimension() != 3) {

        JOptionPane.showMessageDialog(this,
            Messages.getString("FenetreDissimilarite.IsNotSolid"),
            Messages.getString("FenetreDissimilarite.Title"),
            JOptionPane.INFORMATION_MESSAGE);
        return;
      }

      this.s2 = (GM_Solid) objetgeo.getGeom();

      this.jTFFeat2.setText("Objet 2 Ok");

      return;

    }

    // Effectue le calcul
    if (source.equals(this.ok)) {

      ShapeIndicator ind = new ShapeIndicator(this.s1);
      ShapeIndicator ind2 = new ShapeIndicator(this.s2);

      int option = this.opChoice.getSelectedIndex();

      try {

        switch (option) {

          case 0:
            ind.computeLengthIndicator();
            ind2.computeLengthIndicator();

            break;

          case 1:
            ind.computeAreaIndicator();
            ind2.computeAreaIndicator();
            break;

          case 2:
            ind.computeVolumeIndicator();
            ind2.computeVolumeIndicator();
            break;

        }

        DissimilarityCalculationDialog rep = new DissimilarityCalculationDialog(
            Messages.getString("FenetreDissimilarite.Title"),
            ind.getFinalPoints(), ind2.getFinalPoints());

        rep.pack();
        RefineryUtilities.centerFrameOnScreen(rep);
        rep.setVisible(true);

        // this.exportDifference(ind, ind2);

      } catch (Exception e) {

        e.printStackTrace();

      }

    }

  }

  /**
   * Permet de créer une fenetre permettant d'exporter 2 indicateurs au format
   * CSV
   * 
   * @param ind
   * @param ind2
   */
  public void exportDissimilarity(ShapeIndicator ind, ShapeIndicator ind2) {

    try {
      String date = new Date(System.currentTimeMillis()).toString();
      date = date.replaceAll(":", "");

      // JFileChooser homeChooser = new JFileChooser("Mes documents");
      JFileChooser homeChooser = new JFileChooser(
          Messages.getString("3DGIS.HomeDir"));

      homeChooser.setAcceptAllFileFilterUsed(false);
      homeChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      File dossier = homeChooser.getSelectedFile();

      if (dossier == null) {

        JOptionPane.showMessageDialog(this,
            Messages.getString("FenetreSauvegarde.Fail"),
            Messages.getString("FenetreDissimilarite.Title"),
            JOptionPane.INFORMATION_MESSAGE);

        return;
      }

      String nomfichier = (String) JOptionPane.showInputDialog(this,
          Messages.getString("FileName"),
          Messages.getString("FenetreDissimilarite.Title"),
          JOptionPane.QUESTION_MESSAGE, null, null, // c'est
          // ouvert
          // !!!
          ""); // valeur initiale
      if (nomfichier.equals("")) {

        nomfichier = "Sauvegarde";
      }

      FileWriter data = new FileWriter(dossier.getPath() + "\\" + nomfichier
          + ".csv");

      // Nous indiquons les caractéristiques de pas et les différentes
      // valeurs remarquables
      data.write("*****************************************************\n");
      data.write("*****************************************************\n");
      data.write("****Résultat de la dissimilarité entre 2 objets ******\n");
      data.write("*****************************************************\n");
      data.write("*****************************************************\n");
      data.write("Valeur minimale objet1:;" + ind.getValMin() + ";"
          + "Valeur minimale objet2;" + ind2.getValMin() + "\n");
      data.write("Valeur maximale objet1:;" + ind.getValMax() + ";"
          + "Valeur maximale objet2;" + ind2.getValMax() + "\n");
      data.write("Valeur moyenne objet1:;" + ind.getValMoy() + ";"
          + "Valeur moyenne objet2;" + ind2.getValMoy() + "\n");
      data.write("Pas entre 2 classes objet 1:;" + ind.getStepSize() + ";"
          + "Pas entre 2 classes objet 2:;" + ind2.getStepSize() + "\n");

      // On affiche le nombre de valeurs par classes

      data.write("*****************************************************\n");
      data.write("*******************Informations**********************\n");
      data.write("*****************************************************\n");

      data.write("Valeur surface objet1:;" + Calculation3D.area(this.s1) + ";"
          + "Valeur surface objet2;" + Calculation3D.area(this.s2) + "\n");
      data.write("Valeur volume objet1:;" + Calculation3D.volume(this.s1) + ";"
          + "Valeur surface objet2;" + Calculation3D.volume(this.s2) + "\n");

      IDirectPosition dp1 = Calculation3D.centerOfGravity(this.s1);
      IDirectPosition dp2 = Calculation3D.centerOfGravity(this.s2);

      data.write("Distance entre centres de gravité;" + dp1.distance(dp2)
          + "\n");

      DirectPositionList dpl = ind.getFinalPoints();
      DirectPositionList dpl2 = ind2.getFinalPoints();

      int nbValeurs = dpl.size();

      // On affiche les points formant la "signature" de l'objet

      data.write("*****************************************************\n");
      data.write("*******************Points approx*********************\n");
      data.write("*****************************************************\n");

      double valeur = 0;

      for (int i = 0; i < nbValeurs; i++) {

        valeur = valeur + Math.pow(dpl.get(i).getY() - dpl2.get(i).getY(), 2);

        data.write(dpl.get(i).getX() + ";" + (int) dpl.get(i).getY() + ";"
            + ";" + dpl2.get(i).getX() + ";" + (int) dpl2.get(i).getY() + "\n");
      }

      valeur = Math.sqrt(valeur) / (ind.nbSamples);

      data.write("*****************************************************\n");
      data.write("*******************Résultats*********************\n");
      data.write("*****************************************************\n");

      data.write("Calcul de différence;" + valeur + "\n");

      data.write("Calcul de convexité objet1;"
          + Indicator.convexityIndicator(this.s1)
          + ";Calcul de convexité objet2;"
          + Indicator.convexityIndicator(this.s2) + "\n");

      data.write("Calcul de convexité objet1;"
          + Indicator.surfacicConvexIndicator(this.s1)
          + ";Calcul de convexité objet2;"
          + Indicator.surfacicConvexIndicator(this.s2) + "\n");

      data.close();

      DissimilarityMenu.logger.info(Messages
          .getString("FenetreSauvegarde.Success"));
    } catch (Exception e) {
      e.printStackTrace();

    }

  }

}
