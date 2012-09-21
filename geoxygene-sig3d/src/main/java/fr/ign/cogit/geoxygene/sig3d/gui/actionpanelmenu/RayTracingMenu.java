package fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.calculation.RayTracing;
import fr.ign.cogit.geoxygene.sig3d.geometry.Prism;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.gui.navigation3D.Picking;
import fr.ign.cogit.geoxygene.sig3d.representation.basic.Object3d;
import fr.ign.cogit.geoxygene.sig3d.semantic.Layer;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;
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
 * Fenetre gérant l'ajout de couches ainsi que les différentes options
 * d'affichage
 * 
 * Window which manages the addition of layers
 * 
 */
public class RayTracingMenu extends JPanel implements ActionListener,
    ChangeListener {
  // Le prisme sera génèré dans cette couche
  public final static String PRISM_LAYER_NAME = "Prisme_Visu";
  JSlider jAlpha;
  JSlider jBeta;
  JSlider jAngle;
  JTextField jStep;

  JSlider jHeight;
  JSlider jRadius;

  JLabel jHeightVal;
  JLabel jRadiusVal;

  double radius = 50;
  JLabel jAlphaVal;
  JLabel jBetaVal;
  JLabel jAngleVal;

  JComboBox jModeChoice;

  JButton ok = new JButton();

  IDirectPosition center;

  InterfaceMap3D iMap3D;

  double zIni;

  private static final long serialVersionUID = 1L;

  /**
   * Fenetre permettant d'effectuer le calcul de rayonnement
   * 
   * 
   * @param iMap3D la carte dans laquelle le calcul sera effectué
   * 
   */
  public RayTracingMenu(InterfaceMap3D iMap3D) {
    super();
    Layer cTemp = iMap3D.getCurrent3DMap().getLayer(Picking.NOM_COUCHE_POINTS);

    if (cTemp == null) {

      return;
    }

    if (!(cTemp instanceof VectorLayer)) {

      return;
    }
    VectorLayer c = (VectorLayer) cTemp;

    if (c.size() == 0) {

      return;
    }

    this.center = c.get(0).getGeom().coord().get(0);
    this.zIni = this.center.getZ();
    // Elle est rendue modale
    this.setFocusable(true);
    // this.setModal(true);
    this.setBorder(new TitledBorder(new EtchedBorder(), Messages
        .getString("FenetreRayonnement.Title")));

    // Titre

    this.setLayout(null);

    // Formulaire du nom
    JLabel labelNom = new JLabel();
    labelNom.setBounds(10, 25, 150, 20);
    labelNom.setText(Messages.getString("3DGIS.Angle") + " alpha");
    this.add(labelNom);

    this.jAlpha = new JSlider(0, 360, 0);
    this.jAlpha.setBounds(160, 25, 200, 20);
    this.jAlpha.setVisible(true);
    this.jAlpha.addChangeListener(this);

    this.add(this.jAlpha);

    this.jAlphaVal = new JLabel("0");
    this.jAlphaVal.setBounds(380, 25, 200, 20);
    this.jAlphaVal.setVisible(true);

    this.add(this.jAlphaVal);

    // Formulaire du nom
    JLabel labelNom2 = new JLabel();
    labelNom2.setBounds(10, 55, 150, 20);
    labelNom2.setText(Messages.getString("3DGIS.Angle") + " beta");
    this.add(labelNom2);

    this.jBeta = new JSlider(-90, 90, 0);
    this.jBeta.setBounds(160, 55, 200, 20);
    this.jBeta.setVisible(true);
    this.jBeta.addChangeListener(this);

    this.add(this.jBeta);

    this.jBetaVal = new JLabel("0");
    this.jBetaVal.setBounds(380, 55, 200, 20);
    this.jBetaVal.setVisible(true);

    this.add(this.jBetaVal);

    // Formulaire du nom
    JLabel labelNom3 = new JLabel();
    labelNom3.setBounds(10, 85, 150, 20);
    labelNom3.setText(Messages.getString("3DGIS.Angle") + " "
        + Messages.getString("FenetreRayonnement.Open"));
    this.add(labelNom3);

    this.jAngle = new JSlider(0, 90, 20);
    this.jAngle.setBounds(160, 85, 200, 20);
    this.jAngle.setVisible(true);
    this.jAngle.addChangeListener(this);
    this.add(this.jAngle);

    this.jAngleVal = new JLabel("20");
    this.jAngleVal.setBounds(380, 85, 200, 20);
    this.jAngleVal.setVisible(true);

    this.add(this.jAngleVal);

    // Formulaire du nom
    JLabel labelNom4 = new JLabel();
    labelNom4.setBounds(10, 115, 150, 20);
    labelNom4.setText(Messages.getString("FenetreRayonnement.Step") + "°");
    this.add(labelNom4);

    this.jStep = new JTextField("10");
    this.jStep.setBounds(160, 115, 200, 20);
    this.jStep.setVisible(true);

    this.add(this.jStep);

    // Formulaire du nom
    JLabel labelNom6 = new JLabel();
    labelNom6.setBounds(10, 155, 150, 20);
    labelNom6.setText(Messages.getString("FenetreRayonnement.Height"));
    this.add(labelNom6);

    this.jHeight = new JSlider(0, 100, 0);
    this.jHeight.setBounds(160, 155, 200, 20);
    this.jHeight.setVisible(true);
    this.jHeight.addChangeListener(this);
    this.add(this.jHeight);

    this.jHeightVal = new JLabel("0 m");
    this.jHeightVal.setBounds(380, 140155, 200, 20);
    this.jHeightVal.setVisible(true);

    this.add(this.jHeightVal);

    // Formulaire du nom
    JLabel labelNom7 = new JLabel();
    labelNom7.setBounds(10, 195, 150, 20);
    labelNom7.setText(Messages.getString("FenetreRayonnement.Rayon"));
    this.add(labelNom7);

    this.jRadius = new JSlider(50, 1000, 50);
    this.jRadius.setBounds(160, 195, 200, 20);
    this.jRadius.setVisible(true);
    this.jRadius.addChangeListener(this);
    this.add(this.jRadius);

    this.jRadiusVal = new JLabel("50 m");
    this.jRadiusVal.setBounds(380, 195, 200, 20);
    this.jRadiusVal.setVisible(true);

    this.add(this.jRadiusVal);

    String[] lObject = { Messages.getString("FenetreRayonnement.PointCloud"),
        Messages.getString("FenetreRayonnement.Selection") };
    this.jModeChoice = new JComboBox(lObject);
    this.jModeChoice.setBounds(10, 235, 350, 20);
    this.add(this.jModeChoice);

    // Boutons de validations
    this.ok.setBounds(100, 285, 100, 20);
    this.ok.setText(Messages.getString("3DGIS.OK"));
    this.ok.addActionListener(this);
    this.add(this.ok);

    this.setSize(450, 340);

    this.iMap3D = iMap3D;

    this.setVisible(true);
  }

  /**
   * Gestion des actions
   */
  @Override
  public void actionPerformed(ActionEvent actionevent) {
    Object source = actionevent.getSource();

    // bouton de validation
    if (source == this.ok) {

      boolean mode = Messages.getString("FenetreRayonnement.PointCloud")
          .equals(this.jModeChoice.getSelectedItem().toString());

      double pas = Double.parseDouble(this.jStep.getText());
      int rayon = this.jRadius.getValue();

      Layer c = this.iMap3D.getCurrent3DMap().getLayer(
          RayTracingMenu.PRISM_LAYER_NAME);
      Layer c2 = this.iMap3D.getCurrent3DMap().getLayer(
          Picking.NOM_COUCHE_POINTS);
      if (c == null) {
        return;
      }
      c.setVisible(false);
      c2.setVisible(false);
      RayTracing.processRayonnement(this.iMap3D, this.jAlpha.getValue(),
          this.jBeta.getValue(), this.jAngle.getValue(), rayon, pas,
          this.center, mode);
      c.setVisible(true);
      c2.setVisible(true);

    }

  }

  /**
   * Classe permettant de gèrer la modification du prisme
   */
  @Override
  public void stateChanged(ChangeEvent e) {

    Layer cTemp = this.iMap3D.getCurrent3DMap().getLayer(
        Picking.NOM_COUCHE_POINTS);

    if (cTemp == null) {

      return;
    }

    if (!(cTemp instanceof VectorLayer)) {

      return;
    }
    VectorLayer cVectTemp = (VectorLayer) cTemp;

    if (cVectTemp.size() == 0) {

      return;
    }

    this.center = cVectTemp.get(0).getGeom().coord().get(0);

    JSlider source = (JSlider) e.getSource();
    int val = source.getValue();

    if (source == this.jHeight) {

      this.jHeightVal.setText("" + val + " m");
      this.center.setZ(val + this.zIni);

    }
    if (source == this.jAlpha) {

      this.jAlphaVal.setText("" + val);

    }

    if (source == this.jBeta) {

      this.jBetaVal.setText("" + val);
    }

    if (source == this.jAngle) {
      this.jAngleVal.setText("" + val);

    }

    if (source == this.jRadius) {
      this.jRadiusVal.setText("" + val + " m");
      this.radius = val;

    }

    VectorLayer c = (VectorLayer) this.iMap3D.getCurrent3DMap().getLayer(
        RayTracingMenu.PRISM_LAYER_NAME);

    GM_Solid sol = RayTracingMenu
        .prismComputation(this.jAlpha.getValue(), this.jBeta.getValue(),
            this.jAngle.getValue(), this.center, this.radius);

    if (c == null) {

      FT_FeatureCollection<IFeature> ftColl = new FT_FeatureCollection<IFeature>();

      DefaultFeature feat = new DefaultFeature(sol);
      feat.setRepresentation(new Object3d(feat, true, Color.BLUE, 0.5, true));

      ftColl.add(feat);

      c = new VectorLayer(ftColl, RayTracingMenu.PRISM_LAYER_NAME);

      this.iMap3D.getCurrent3DMap().addLayer(c);

    } else {
      c.get(0).setGeom(sol);
      c.get(0).setRepresentation(
          new Object3d(c.get(0), true, Color.blue, 0.5, true));
      c.refresh();
    }

  }

  /**
   * Calcul un prisme
   * 
   * @param alpha direction horizontale
   * @param beta direction verticale
   * @param angle angle d'ouverture
   * @param center centre du prisme
   * @param radius rayon du prisme
   * @return
   */
  private static GM_Solid prismComputation(int alpha, int beta, int angle,
      IDirectPosition center, double radius) {

    double angleAlphaMin = (alpha - angle) * Math.PI / 180;
    double angleLambdaMin = Math.max(-90, beta - angle) * Math.PI / 180;

    double angleAlphaMax = (alpha + angle) * Math.PI / 180;
    double angleLambdaMax = Math.min(90, beta + angle) * Math.PI / 180;

    return new Prism(center, radius, 0.1, angleAlphaMin, angleAlphaMax,
        angleLambdaMin, angleLambdaMax);

  }

}
