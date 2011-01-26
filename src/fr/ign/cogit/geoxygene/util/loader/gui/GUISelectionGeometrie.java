package fr.ign.cogit.geoxygene.util.loader.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Choix des paramètres géométriques lors de la création de tables postgis et du
 * fichier de mapping à partir d'une classe java (le problème ne se pose pas
 * avec Oracle)
 * 
 * @author Eric Grosso - IGN / Laboratoire COGIT
 * @version 1.0
 */

public class GUISelectionGeometrie extends JDialog {

  /**
	 * 
	 */
  private static final long serialVersionUID = 7651040118952366197L;
  private static Dialog frame = new JDialog();
  final GUISelectionGeometrie selectionGeometrie;
  private Color bluegray = new Color(197, 197, 232);
  private JPanel panneauType, panneauDimension, jPanelBoutton;
  // private JScrollPane scrollChargement, scrollStockage;
  private Box boxType, boxDimension;
  // private int selectionType = 0, selectionDimension = 0;
  private JButton jButtonOK;
  private Box boxe = Box.createVerticalBox();
  static String[] stringsChoixType = new String[4];
  static String[] stringsChoixDimension = new String[2];
  private String titreType = ""; //$NON-NLS-1$
  private String titreDimension = ""; //$NON-NLS-1$

  private int typeGeometrie;

  public int getTypeGeometrie() {
    return this.typeGeometrie;
  }

  public void setTypeGeometrie(int typeGeometrie) {
    this.typeGeometrie = typeGeometrie;
  }

  private int dimensionGeometrie;

  public int getDimensionGeometrie() {
    return this.dimensionGeometrie;
  }

  public void setDimensionGeometrie(int dimensionGeometrie) {
    this.dimensionGeometrie = dimensionGeometrie;
  }

  /** Constructeur de l'interface **/
  public GUISelectionGeometrie() {
    super(GUISelectionGeometrie.frame, "Choix de la géométrie", true);
    this.selectionGeometrie = this;
    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    this.setPreferredSize(new Dimension(430, 270));
    this.setSize(430, 270);
    this.setResizable(false);
    this.setBackground(Color.white);
    this.setAlwaysOnTop(true);

    Dimension tailleEcran = Toolkit.getDefaultToolkit().getScreenSize();
    this.setLocation((tailleEcran.width - this.getWidth()) / 2,
        (tailleEcran.height - this.getHeight()) / 2);

    Container contentPane = this.getContentPane();
    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

    // initialisation des différents composants et variables
    GUISelectionGeometrie.stringsChoixType[0] = "Point"; //$NON-NLS-1$
    GUISelectionGeometrie.stringsChoixType[1] = "LineString"; //$NON-NLS-1$
    GUISelectionGeometrie.stringsChoixType[2] = "Polygon"; //$NON-NLS-1$
    GUISelectionGeometrie.stringsChoixType[3] = "Geometry collection"; //$NON-NLS-1$

    GUISelectionGeometrie.stringsChoixDimension[0] = "2D"; //$NON-NLS-1$
    GUISelectionGeometrie.stringsChoixDimension[1] = "3D"; //$NON-NLS-1$

    this.titreType = "Quel est le type de votre géométrie ?";
    this.titreDimension = "Quel est la dimension de votre géométrie ?";
    this.initBoxTypeGeom();
    this.initBoxDimensionGeom();
    this.initJPanelType();
    this.initJPanelDimension();
    this.initJPanelBoutton();

    this.boxe.setBackground(Color.white);
    this.boxe.add(this.panneauType);
    this.boxe.add(this.panneauDimension);

    this.boxe.add(this.jPanelBoutton);

    // Association des panneaux au conteneur
    this.setContentPane(this.boxe);

    // Visualisation
    this.pack();
    this.setVisible(true);
  }

  /** initialise le panel principal **/
  private void initJPanelType() {
    this.panneauType = new JPanel(new java.awt.BorderLayout());
    this.panneauType.setPreferredSize(new Dimension(820, 300));
    this.panneauType.setMaximumSize(new Dimension(820, 300));
    this.panneauType.setBackground(Color.white);
    this.panneauType.setBorder(BorderFactory.createTitledBorder(BorderFactory
        .createEtchedBorder(this.bluegray, this.bluegray), this.titreType));
    this.panneauType.add(this.boxType);
  }

  /** initialise le panel principal **/
  private void initJPanelDimension() {
    this.panneauDimension = new JPanel(new java.awt.BorderLayout());
    this.panneauDimension.setPreferredSize(new Dimension(820, 300));
    this.panneauDimension.setMaximumSize(new Dimension(820, 300));
    this.panneauDimension.setBackground(Color.white);
    this.panneauDimension.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(this.bluegray, this.bluegray),
        this.titreDimension));
    this.panneauDimension.add(this.boxDimension);
  }

  private void initBoxTypeGeom() {
    this.boxType = Box.createVerticalBox();
    ButtonGroup choix = new ButtonGroup();
    JCheckBox checkBox;
    final int t = GUISelectionGeometrie.stringsChoixType.length;
    for (int i = 0; i < t; i++) {
      checkBox = new JCheckBox(GUISelectionGeometrie.stringsChoixType[i]);
      checkBox.setBackground(Color.white);
      if (i == 0) {
        checkBox.setSelected(true);
        this.setTypeGeometrie(0);
      }
      checkBox.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (e.getStateChange() == 1) {
            Object source = e.getItemSelectable();
            for (int index = 0; index < t; index++) {
              if (((JCheckBox) source).getText().equals(
                  GUISelectionGeometrie.stringsChoixType[index])) {
                GUISelectionGeometrie.this.setTypeGeometrie(index);
              }
            }
          }
        }
      });
      choix.add(checkBox);
      this.boxType.add(checkBox);
    }
  }

  private void initBoxDimensionGeom() {
    this.boxDimension = Box.createVerticalBox();
    ButtonGroup choix = new ButtonGroup();
    JCheckBox checkBox;
    final int t = GUISelectionGeometrie.stringsChoixDimension.length;
    for (int i = 0; i < t; i++) {
      checkBox = new JCheckBox(GUISelectionGeometrie.stringsChoixDimension[i]);
      checkBox.setBackground(Color.white);
      if (i == 0) {
        checkBox.setSelected(true);
        this.setDimensionGeometrie(2);
      }
      checkBox.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (e.getStateChange() == 1) {
            Object source = e.getItemSelectable();
            for (int index = 0; index < t; index++) {
              if (((JCheckBox) source).getText().equals(
                  GUISelectionGeometrie.stringsChoixDimension[index])) {
                GUISelectionGeometrie.this.setDimensionGeometrie(index + 2);
              }
            }
          }
        }
      });
      choix.add(checkBox);
      this.boxDimension.add(checkBox);
    }
  }

  /** initialisation de jPanelBoutton */
  private void initJPanelBoutton() {
    this.initJButtonOK();

    this.jPanelBoutton = new JPanel();
    this.jPanelBoutton.setLayout(new FlowLayout());
    this.jPanelBoutton.add(this.jButtonOK);
    this.jPanelBoutton.setBackground(Color.white);
  }

  /** This method initializes jButtonChargement */
  private void initJButtonOK() {
    this.jButtonOK = new JButton();
    this.jButtonOK.setPreferredSize(new java.awt.Dimension(80, 30));
    this.jButtonOK.setSize(new java.awt.Dimension(80, 30));
    this.jButtonOK.setText("Ok");
    this.jButtonOK.setVisible(true);

    this.jButtonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUISelectionGeometrie.this.selectionGeometrie.dispose();
      }
    });
  }
}
