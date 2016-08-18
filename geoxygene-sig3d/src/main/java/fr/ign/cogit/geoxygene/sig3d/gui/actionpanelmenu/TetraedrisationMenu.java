package fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.choice.RepresentationWindowFactory;
import fr.ign.cogit.geoxygene.sig3d.tetraedrisation.Tetraedrisation;
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
 * Fenetre gérant les différentes décomposition d'objets 3D possibles Windows of
 * this class are used to tetrahedrize or triangulate 3D objects
 */
public class TetraedrisationMenu extends JPanel implements ActionListener {
  // Composants du formulaire
  JComboBox<String>  choixContrainte;
  JComboBox<String>  choixDeomposition;

  JButton ok = new JButton();
  InterfaceMap3D iCarte3D;

  IFeature o1;
  IFeature o2;
  GM_Solid resultat;

  // Options de type de décomposition
  private static String[] choix = {
      Messages.getString("Triangulation.Constraint"),
      Messages.getString("Triangulation.UnConstraint") };

  // Type de résultat souhaité
  private static String[] choix2 = {
      Messages.getString("Triangulation.Triangle"),
      Messages.getString("Triangulation.Tetraedron") };

  // Les controles

  /**
	 */
  private static final long serialVersionUID = 1L;

  /**
   * Affiche la fenêtre
   * 
   * @param iCarte3D l'interface de carte 3D utilisé dans le menu. On appliquera
   *          la transformation sur le premier objet de la sélection
   */
  public TetraedrisationMenu(InterfaceMap3D iCarte3D) {
    super();

    this.iCarte3D = iCarte3D;

    // Titre
    this.setBorder(new TitledBorder(new EtchedBorder(), Messages
        .getString("Triangulation.Title")));

    this.setLayout(null);

    // àtiquette Opération
    JLabel op = new JLabel();
    op.setBounds(10, 25, 140, 20);
    op.setText(Messages.getString("Triangulation.Constraint"));
    this.add(op);

    // Etiquette type décompositino
    JLabel o = new JLabel();
    o.setBounds(10, 65, 140, 20);
    o.setText(Messages.getString("Triangulation.Type"));
    o.setVisible(true);
    this.add(o);

    this.choixContrainte = new JComboBox<String> (TetraedrisationMenu.choix);
    this.choixContrainte.setBounds(150, 25, 150, 20);
    this.choixContrainte.setVisible(true);
    this.choixContrainte.setSelectedIndex(0);
    this.choixContrainte.addActionListener(this);

    this.add(this.choixContrainte);

    // Etiquette su choix de la décomposition
    this.choixDeomposition = new JComboBox<String> (TetraedrisationMenu.choix2);
    this.choixDeomposition.setBounds(150, 65, 150, 20);
    this.choixDeomposition.setVisible(true);
    this.choixDeomposition.setSelectedIndex(0);
    this.choixDeomposition.addActionListener(this);
    this.add(this.choixDeomposition);

    this.ok.setBounds(50, 145, 150, 20);
    this.ok.setText(Messages.getString("3DGIS.Ok"));
    this.ok.addActionListener(this);
    this.add(this.ok);

    this.setSize(386, 206);
    this.setVisible(true);
  }

  // Execution
  @Override
  public void actionPerformed(ActionEvent actionevent) {
    Object source = actionevent.getSource();

    if (source.equals(this.ok)) {

      if (this.iCarte3D.getSelection().size() == 0) {
        JOptionPane.showMessageDialog(this,
            Messages.getString("3DGIS.NoSlection"),
            Messages.getString("Triangulation.Title"),
            JOptionPane.INFORMATION_MESSAGE);
        return;

      }

      // Décomposition du premier élément de la sélection
      IFeature obj = this.iCarte3D.getSelection().get(0);

      if (obj.getGeom().dimension() != 3) {
        JOptionPane.showMessageDialog(this,
            Messages.getString("3DGIS.IsNotSolid"),
            Messages.getString("Triangulation.Title"),
            JOptionPane.INFORMATION_MESSAGE);
        return;

      }
      // On récupère les paramètres
      boolean mesh = (this.choixDeomposition.getSelectedIndex() == 0);
      boolean contrainte = (this.choixContrainte.getSelectedIndex() == 0);

      try {
        // On applique la tétraèdrisation
        Tetraedrisation tet = new Tetraedrisation(obj);

        tet.tetraedrise(contrainte, mesh);

        FT_FeatureCollection<IFeature> lObj = new FT_FeatureCollection<IFeature>();

        if (mesh) {
          IFeature objFin = new DefaultFeature(new GM_Solid(tet.getTriangles()));
          lObj.add(objFin);

        } else {

          List<GM_Solid> lSolid = tet.getTetraedres();
          int nbTetra = lSolid.size();

          for (int i = 0; i < nbTetra; i++) {
            IFeature objFin = new DefaultFeature(lSolid.get(i));
            lObj.add(objFin);
          }
        }
        // On ajoute la couche à la carte utilisée dans l'interface
        ((JDialog) RepresentationWindowFactory.generateDialog(this.iCarte3D,
            lObj)).setVisible(true);

        JOptionPane.showMessageDialog(this.iCarte3D,
            Messages.getString("Triangulation.Succes"),
            Messages.getString("Triangulation.Title"),
            JOptionPane.INFORMATION_MESSAGE);

      } catch (Exception e) {
        e.printStackTrace();

      }

    }

  }

}
