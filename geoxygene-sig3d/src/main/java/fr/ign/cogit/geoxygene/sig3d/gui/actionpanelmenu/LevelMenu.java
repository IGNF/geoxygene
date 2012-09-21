package fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.appli.COGITColorChooserPanel;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.representation.control.Level;
import fr.ign.cogit.geoxygene.sig3d.semantic.Layer;
import fr.ign.cogit.geoxygene.sig3d.semantic.Map3D;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

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
 * 
 * 
 * Fenetre permettant de modifier les informations concernant le plan de
 * référence
 * 
 * Window for level management
 */
public class LevelMenu extends JPanel implements ChangeListener, ActionListener {

  /**
   * Un couche portant ce nom sera crée
   */
  public static final String NAME_LEVEL_LAYER = "NIVEAU";
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  // La carte à laquelle est associée la fenetre
  InterfaceMap3D iMap3D;

  // Menu glissant permettant de modifier la hauteur du niveau
  JSlider jSLevel;

  // informations liées au menu glissant
  JLabel jTFlevMax;
  JLabel jTFlevMin;
  JLabel jTFlevAct;

  // champs permettant de renseigner le nombre de divisions
  JTextField jtfNBDiv;

  // champs renseignant la taille des coordonénes
  JTextField jtTextSize;

  // champs renseignant la distance d'apparition des coordonnées
  JTextField jtDistance;

  // Checkbox gérant l'apparition des coordonnnées
  JCheckBox jcbToponymVisible;

  // Champs permettant d'avoir le mode grille
  JCheckBox jcbSolid;

  // Bouton mettant à jour les apparences
  JButton update;

  // Gestion de la couleur du plan
  JButton jBRectangleColor;

  // Gestion de la couleur des coordonnées
  JButton jBTextColor;

  /**
   * Constructeur du niveau associée à un composant de carto
   * 
   * @param iMap3D
   */
  public LevelMenu(InterfaceMap3D iMap3D) {

    super();

    // On initialise la plan à l'altitude minimale
    // Si il n'y a pas de données, le menu est vide
    Map3D carte = iMap3D.getCurrent3DMap();

    Box3D b = carte.getBoundingBox();
    this.setBorder(new TitledBorder(new EtchedBorder(), Messages
        .getString("FenetreNiveau.Title")));

    if (b == null) {
      JTextArea ta = new JTextArea(
          Messages.getString("FenetreNiveau.Unavailable"));
      ta.setBounds(15, 15, 300, 300);
      this.add(ta);
      return;
    }

    this.iMap3D = iMap3D;

    // Titre

    this.setLayout(null);

    // Indique si l'on souhaite afficher les coordonnées
    this.jcbToponymVisible = new JCheckBox(
        Messages.getString("FenetreNiveau.Coordinate"));
    this.jcbToponymVisible.setSelected(true);
    this.jcbToponymVisible.setBounds(10, 25, 170, 30);
    this.jcbToponymVisible.addActionListener(this);
    this.add(this.jcbToponymVisible);

    // Gestion du menu glissant
    JLabel op = new JLabel();
    op.setBounds(10, 70, 140, 20);
    op.setText(Messages.getString("FenetreNiveau.LevelInM"));
    this.add(op);

    double zMin = b.getLLDP().getZ();
    double zMax = b.getURDP().getZ();

    Layer c = iMap3D.getCurrent3DMap().getLayer(LevelMenu.NAME_LEVEL_LAYER);

    this.jSLevel = new JSlider();
    this.jSLevel.setBounds(50, 110, 30, 500);
    this.jSLevel.setOrientation(SwingConstants.VERTICAL);

    this.jTFlevMax = new JLabel("");
    this.jTFlevMax.setBounds(80, 75, 40, 100);
    this.add(this.jTFlevMax);

    this.jTFlevMin = new JLabel("");
    this.jTFlevMin.setBounds(80, 570, 40, 100);
    this.add(this.jTFlevMin);

    this.jTFlevAct = new JLabel("");
    this.jTFlevAct.setBounds(10, 570, 40, 100);
    this.add(this.jTFlevAct);

    if (c == null || !(c instanceof VectorLayer)) {

      this.jSLevel.setMaximum((int) zMax);
      this.jSLevel.setMinimum((int) zMin);
      this.jSLevel.setValue((int) zMin);

      this.jTFlevAct.setText("" + (int) zMin);
      this.jTFlevMax.setText("" + (int) zMax);
      this.jTFlevMin.setText("" + (int) zMin);
    } else {

      VectorLayer cVecte = (VectorLayer) c;

      if (cVecte.size() == 0) {

        this.jSLevel.setMaximum((int) zMax);
        this.jSLevel.setMinimum((int) zMin);
        this.jSLevel.setValue((int) zMin);

        this.jTFlevAct.setText("" + (int) zMin);
        this.jTFlevMax.setText("" + (int) zMax);
        this.jTFlevMin.setText("" + (int) zMin);
      } else {
        // On place un niveau à l'altitude miniamle
        this.moveLevel((int) cVecte.get(0).getGeom().coord().get(0).getZ());

      }
    }

    this.jSLevel.addChangeListener(this);
    this.add(this.jSLevel);
    // Gestion du nombre de divisions
    JLabel nbDivText = new JLabel(
        Messages.getString("FenetreNiveau.NumberDivisions"));
    nbDivText.setBounds(10, 630, 200, 20);
    this.add(nbDivText);

    this.jtfNBDiv = new JTextField("10");
    this.jtfNBDiv.setBounds(200, 630, 40, 20);
    this.add(this.jtfNBDiv);

    // Gestion de la couleur de l'emprise
    JLabel jLCoulEmprise = new JLabel(Messages.getString("3DGIS.Color"));
    jLCoulEmprise.setBounds(10, 660, 150, 20);

    this.add(jLCoulEmprise);

    this.jBRectangleColor = new JButton();
    this.jBRectangleColor.setBounds(200, 660, 40, 20);

    this.jBRectangleColor.addActionListener(this);
    this.jBRectangleColor.setBackground(Color.cyan);
    this.add(this.jBRectangleColor);

    // Gestion de la couleur de la police
    JLabel jLCoulPolice = new JLabel(Messages.getString("3DGIS.Color") + " "
        + Messages.getString("3DGIS.Police"));
    jLCoulPolice.setBounds(10, 690, 150, 20);

    this.add(jLCoulPolice);

    this.jBTextColor = new JButton();
    this.jBTextColor.setBounds(200, 690, 40, 20);

    this.jBTextColor.addActionListener(this);
    this.jBTextColor.setBackground(Color.red);
    this.add(this.jBTextColor);

    // Gestion de la taille du texte
    JLabel TailleText = new JLabel(Messages.getString("FenetreNiveau.TextSize"));
    TailleText.setBounds(10, 720, 200, 20);
    this.add(TailleText);

    this.jtTextSize = new JTextField("10");
    this.jtTextSize.setBounds(200, 720, 40, 20);
    this.add(this.jtTextSize);

    // Gestion du mode grille
    this.jcbSolid = new JCheckBox(Messages.getString("FenetreNiveau.GridMod"));
    this.jcbSolid.setSelected(false);
    this.jcbSolid.setBounds(10, 740, 170, 30);
    this.jcbSolid.addActionListener(this);
    this.add(this.jcbSolid);

    // Gestion de la distance de visibilité des coordonnées
    JLabel jlDistance = new JLabel(
        Messages.getString("FenetreNiveau.DistanceVisibility"));
    jlDistance.setBounds(10, 770, 200, 20);
    this.add(jlDistance);

    this.jtDistance = new JTextField("5000");
    this.jtDistance.setBounds(200, 770, 40, 20);
    this.add(this.jtDistance);

    // Bouton de mise à jour
    this.update = new JButton(Messages.getString("3DGIS.Update"));
    this.update.setBounds(30, 810, 200, 20);
    this.update.addActionListener(this);
    this.add(this.update);

    this.setSize(300, 84);

    this.moveLevel((int) zMin);
  }

  /**
   * Fonction permettant de récupérer un niveau si il existe et sinon de le
   * recrée
   * 
   * @return
   */
  private Level getLevel() {

    Map3D carte = this.iMap3D.getCurrent3DMap();

    Layer c = carte.getLayer(LevelMenu.NAME_LEVEL_LAYER);
    VectorLayer cv;

    if (c != null) {
      // Si la couche exite on le récupère
      cv = (VectorLayer) c;

      if (cv.size() != 0) {

        Level n = (Level) cv.get(0).getRepresentation();
        return n;

      } else {
        // Sinon on en recrée un en fonction des informations définies
        // dans les champs
        int nbDiv = 10;
        try {
          nbDiv = Integer.parseInt(this.jtfNBDiv.getText());

        } catch (NumberFormatException nfe) {

          nfe.printStackTrace();

        }

        if (nbDiv < 1) {
          nbDiv = 10;

        }

        int tailleText = 10;

        try {
          tailleText = Integer.parseInt(this.jtTextSize.getText());

        } catch (NumberFormatException nfe) {
          nfe.printStackTrace();

        }

        if (tailleText < 1) {
          tailleText = 10;

        }

        double distance = 5000;

        try {
          distance = Double.parseDouble(this.jtDistance.getText());

        } catch (NumberFormatException nfe) {
          nfe.printStackTrace();

        }

        if (distance < 0.5) {
          distance = 5000;

        }

        int z = this.jSLevel.getValue();
        IFeature feat = new DefaultFeature(LevelMenu.generateEnvelope(carte, z));
        Level n = new Level(feat, true, z, this.jBTextColor.getBackground(),
            this.jBRectangleColor.getBackground(), nbDiv, tailleText,
            this.jcbSolid.isSelected(), distance);
        feat.setRepresentation(n);
        cv.add(feat);
        cv.refresh();

        return n;
      }

    } else {
      // Cas ou la couche n'existe pas
      int nbDiv = 10;
      try {
        nbDiv = Integer.parseInt(this.jtfNBDiv.getText());

      } catch (NumberFormatException nfe) {

        nfe.printStackTrace();

      }

      if (nbDiv < 1) {
        nbDiv = 10;

      }

      int tailleText = 10;

      try {
        tailleText = Integer.parseInt(this.jtTextSize.getText());

      } catch (NumberFormatException nfe) {
        nfe.printStackTrace();

      }

      if (tailleText < 1) {
        tailleText = 10;

      }

      double distance = 5000;

      try {
        distance = Double.parseDouble(this.jtDistance.getText());

      } catch (NumberFormatException nfe) {
        nfe.printStackTrace();

      }

      if (distance < 0.5) {
        distance = 5000;

      }

      int z = this.jSLevel.getValue();
      IFeature feat = new DefaultFeature(LevelMenu.generateEnvelope(carte, z));
      Level n = new Level(feat, true, z, this.jBTextColor.getBackground(),
          this.jBRectangleColor.getBackground(), nbDiv, tailleText,
          this.jcbSolid.isSelected(), distance);
      feat.setRepresentation(n);

      FT_FeatureCollection<IFeature> featColl = new FT_FeatureCollection<IFeature>();

      featColl.add(feat);

      cv = new VectorLayer(featColl, LevelMenu.NAME_LEVEL_LAYER);
      carte.addLayer(cv);

      return n;
    }

  }

  /**
   * Permet de créer un polygone de type emprise correspondant à l'emprise d'une
   * carte
   * 
   * @param carte la carte dont on utilisera l'emprise
   * @param z l'altitude à laquelle l'emprise sera fixée
   * @return
   */
  private static GM_Polygon generateEnvelope(Map3D carte, double z) {

    Box3D b = carte.getBoundingBox();

    if (b == null) {

      return null;
    }

    IDirectPosition pMin = b.getLLDP();
    IDirectPosition pMax = b.getURDP();

    int zMin = (int) pMin.getZ();
    int zMax = (int) pMax.getZ();

    z = Math.min(zMax, z);
    z = Math.max(zMin, z);

    double xMin = pMin.getX();
    double yMin = pMin.getY();

    double xMax = pMax.getX();
    double yMax = pMax.getY();

    DirectPosition dp1 = new DirectPosition(xMin, yMin, z);
    DirectPosition dp2 = new DirectPosition(xMax, yMin, z);
    DirectPosition dp3 = new DirectPosition(xMax, yMax, z);
    DirectPosition dp4 = new DirectPosition(xMin, yMax, z);

    DirectPositionList dpl = new DirectPositionList();
    dpl.add(dp1);
    dpl.add(dp2);
    dpl.add(dp3);
    dpl.add(dp4);
    dpl.add(dp1);

    GM_LineString gls = new GM_LineString(dpl);

    GM_Polygon poly = new GM_Polygon(gls);

    return poly;
  }

  /**
   * Fonction permettant de déplacer en altitude un plan sans dépasser les
   * altitudes min et max de la scène
   * 
   * @param z la nouvelle altitude
   */
  private void moveLevel(int z) {

    Box3D b = this.iMap3D.getCurrent3DMap().getBoundingBox();

    if (b == null) {

      return;
    }

    IDirectPosition pMin = b.getLLDP();
    IDirectPosition pMax = b.getURDP();

    int zMin = (int) pMin.getZ();
    int zMax = (int) pMax.getZ();

    z = Math.min(zMax, z);
    z = Math.max(zMin, z);

    Level n = this.getLevel();
    n.setZ(z);

    this.jSLevel.setMaximum(zMin);
    this.jSLevel.setMaximum(zMax);
    this.jSLevel.setValue(z);

    this.jTFlevMax.setText("" + zMax);
    this.jTFlevMin.setText("" + zMin);

    this.jTFlevAct.setText("" + z);

    if (zMax == zMin) {
      return;
    }
    // On modifie en conséquence la position de l'information de Z
    this.jTFlevAct.setBounds(10, this.jSLevel.getY() + this.jSLevel.getHeight()
        * (zMax - z) / (zMax - zMin) - 40, this.jTFlevAct.getWidth(),
        this.jTFlevAct.getHeight());

  }

  boolean finish = true;

  @Override
  public void stateChanged(ChangeEvent e) {
    // Gestion de la glissière d'altitude
    if (this.finish) {

      this.finish = false;
      this.moveLevel(this.jSLevel.getValue());
      this.finish = true;
    }

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();

    // Affichage des informations de coordonnées
    if (source == this.jcbToponymVisible) {
      Level n = this.getLevel();
      n.visualizeCoordinates(this.jcbToponymVisible.isSelected());

    }
    // Mise à jour et rafraichissement du niveau lorsque le bouton update
    // est pressé (modifications dans le style de la représentation=
    if (source == this.update) {

      int nbDiv = 10;
      try {
        nbDiv = Integer.parseInt(this.jtfNBDiv.getText());

      } catch (NumberFormatException nfe) {
        JOptionPane
            .showMessageDialog(
                this,
                Messages.getString("FenetreShapeFile.ValueIsNotNumber" + " >0"), //$NON-NLS-1$
                Messages.getString("FenetreShapeFile.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
        return;
      }

      int tailleText = 0;

      try {
        tailleText = Integer.parseInt(this.jtTextSize.getText());

      } catch (NumberFormatException nfe) {
        JOptionPane
            .showMessageDialog(
                this,
                Messages.getString("FenetreShapeFile.ValueIsNotNumber" + " >0"), //$NON-NLS-1$
                Messages.getString("FenetreShapeFile.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
        return;

      }

      if (tailleText < 1) {
        JOptionPane
            .showMessageDialog(
                this,
                Messages.getString("FenetreShapeFile.ValueIsNotNumber" + " >0"), //$NON-NLS-1$
                Messages.getString("FenetreShapeFile.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
        return;

      }

      double distance = 5000;

      try {
        distance = Double.parseDouble(this.jtDistance.getText());

      } catch (NumberFormatException nfe) {
        nfe.printStackTrace();

      }

      if (distance < 0.5) {
        distance = 5000;

      }

      Level n = this.getLevel();
      n.update(this.jcbToponymVisible.isSelected(), this.jSLevel.getValue(),
          this.jBTextColor.getBackground(),
          this.jBRectangleColor.getBackground(), nbDiv, tailleText,
          this.jcbSolid.isSelected(), distance);

    }

    if (source == this.jBRectangleColor) {

      Color couleur = COGITColorChooserPanel.showDialog(this,
          Messages.getString("FenetreAjoutCouche.ColorLayer"), null); //$NON-NLS-1$

      if (couleur == null) {
        return;
      }

      this.jBRectangleColor.setBackground(couleur);

      return;
    }

    if (source == this.jBTextColor) {

      Color couleur = COGITColorChooserPanel.showDialog(this,
          Messages.getString("FenetreAjoutCouche.ColorLayer"), null); //$NON-NLS-1$

      if (couleur == null) {
        return;
      }

      this.jBTextColor.setBackground(couleur);

      return;
    }

  }

}
