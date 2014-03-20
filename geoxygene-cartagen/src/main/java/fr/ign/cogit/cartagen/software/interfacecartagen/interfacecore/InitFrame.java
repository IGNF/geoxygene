/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/*
 * Created on 26 juil. 2005
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.software.CartagenApplication;

/**
 * La fenetre de lancement de l'interface
 * @author julien Gaffuri
 * 
 */
public class InitFrame extends JFrame {
  private static final long serialVersionUID = -3566099212243269188L;
  static Logger logger = Logger.getLogger(InitFrame.class.getName());

  private Image icon;

  public Image getIcon() {
    if (this.icon == null) {
      this.icon = new ImageIcon(InitFrame.class
          .getResource("/images/icone.gif").getPath().replaceAll("%20", " "))
          .getImage();
    }
    return this.icon;
  }

  // Modif Cecile construction ImageIcon a partir d'une URL et non d'un file
  // path string
  // private ImageIcon goodInit = new ImageIcon(InitFrame.class.getResource(
  // "/images/goodInit.png").getPath().replaceAll("%20", " "));
  private ImageIcon goodInit = new ImageIcon(
      InitFrame.class.getResource("/images/goodInit.png"));
  private ImageIcon warnInit = new ImageIcon(
      InitFrame.class.getResource("/images/warnInit.png"));
  private ImageIcon wrongInit = new ImageIcon(
      InitFrame.class.getResource("/images/wrongInit.png"));
  // Fin modif Cecile

  public void isGood(JLabel lbl) {
    lbl.setText("");
    lbl.setIcon(this.goodInit);
  }

  public void isWarned(JLabel lbl) {
    lbl.setText("");
    lbl.setIcon(this.warnInit);
  }

  public void isWrong(JLabel lbl) {
    lbl.setText("");
    lbl.setIcon(this.wrongInit);
  }

  /**
   * Components
   */

  public JPanel pnlCentral;
  public JPanel pnlEnd;

  public JLabel lblInterface;
  public JLabel lblLoading;
  public JLabel lblEnrichment;
  public JLabel lblStartGothic;
  public JLabel lblID;
  public JLabel lblIDValue;
  public JLabel lblProjections;
  public JLabel lblProjectionsValue;
  public JLabel lblDataset;
  public JLabel lblDatasetValue;
  public JLabel lblDLL;
  public JLabel lblOpen;
  public JLabel lblSchema;
  public JLabel lblCreation;
  public JLabel lblSpatialIndexGothic;
  public JLabel lblVersion;
  public JLabel lblLayers;
  public JLabel lblGeneConstraints;

  public JLabel lblInterfaceIcon;
  public JLabel lblLoadingIcon;
  public JLabel lblEnrichmentIcon;
  public JLabel lblStartGothicIcon;
  public JLabel lblIDIcon;
  public JLabel lblProjectionsIcon;
  public JLabel lblDatasetIcon;
  public JLabel lblDLLIcon;
  public JLabel lblOpenIcon;
  public JLabel lblSchemaIcon;
  public JLabel lblCreationIcon;
  public JLabel lblSpatialIndexGothicIcon;
  public JLabel lblVersionIcon;
  public JLabel lblLayersIcon;
  public JLabel lblGeneConstraintsIcon;

  public JLabel lblEnd;
  public JLabel lblEndIcon;
  public JButton buttonOK;

  /**
   * Components initialisation
   */

  private void initComponents() {

    this.pnlCentral = new JPanel();
    this.pnlEnd = new JPanel();

    this.lblInterface = new JLabel("Interface construction");
    this.lblInterface.setForeground(Color.LIGHT_GRAY);
    this.lblLoading = new JLabel("Data loading");
    this.lblLoading.setForeground(Color.LIGHT_GRAY);
    this.lblEnrichment = new JLabel("Data enrichment");
    this.lblEnrichment.setForeground(Color.LIGHT_GRAY);
    this.lblStartGothic = new JLabel("START: GOTHIC CONFIGURATION");
    this.lblStartGothic.setForeground(Color.LIGHT_GRAY);
    this.lblID = new JLabel("    | Database ID :");
    this.lblID.setForeground(Color.LIGHT_GRAY);
    this.lblIDValue = new JLabel("x");
    this.lblIDValue.setForeground(Color.LIGHT_GRAY);
    this.lblProjections = new JLabel("    | Projections dataset :");
    this.lblProjections.setForeground(Color.LIGHT_GRAY);
    this.lblProjectionsValue = new JLabel("x");
    this.lblProjectionsValue.setForeground(Color.LIGHT_GRAY);
    this.lblDataset = new JLabel("    | Working dataset :");
    this.lblDataset.setForeground(Color.LIGHT_GRAY);
    this.lblDatasetValue = new JLabel("x");
    this.lblDatasetValue.setForeground(Color.LIGHT_GRAY);
    this.lblDLL = new JLabel("DLL Loading");
    this.lblDLL.setForeground(Color.LIGHT_GRAY);
    this.lblOpen = new JLabel("Database & pojections opening");
    this.lblOpen.setForeground(Color.LIGHT_GRAY);
    this.lblSchema = new JLabel("Representation schema installation");
    this.lblSchema.setForeground(Color.LIGHT_GRAY);
    this.lblCreation = new JLabel("Dataset opening/creation");
    this.lblCreation.setForeground(Color.LIGHT_GRAY);
    this.lblSpatialIndexGothic = new JLabel("Version and classes definition");
    this.lblSpatialIndexGothic.setForeground(Color.LIGHT_GRAY);
    this.lblVersion = new JLabel("Spatial index initialisation");
    this.lblVersion.setForeground(Color.LIGHT_GRAY);
    this.lblLayers = new JLabel("Interface layers loading");
    this.lblLayers.setForeground(Color.LIGHT_GRAY);
    this.lblGeneConstraints = new JLabel(
        "Instanciation of generalisation constraints");
    this.lblGeneConstraints.setForeground(Color.LIGHT_GRAY);

    this.lblInterfaceIcon = new JLabel("...");
    this.lblInterfaceIcon.setForeground(Color.LIGHT_GRAY);
    this.lblLoadingIcon = new JLabel("...");
    this.lblLoadingIcon.setForeground(Color.LIGHT_GRAY);
    this.lblEnrichmentIcon = new JLabel("...");
    this.lblEnrichmentIcon.setForeground(Color.LIGHT_GRAY);
    this.lblStartGothicIcon = new JLabel("...");
    this.lblStartGothicIcon.setForeground(Color.LIGHT_GRAY);
    this.lblIDIcon = new JLabel("...");
    this.lblIDIcon.setForeground(Color.LIGHT_GRAY);
    this.lblProjectionsIcon = new JLabel("...");
    this.lblProjectionsIcon.setForeground(Color.LIGHT_GRAY);
    this.lblDatasetIcon = new JLabel("...");
    this.lblDatasetIcon.setForeground(Color.LIGHT_GRAY);
    this.lblDLLIcon = new JLabel("...");
    this.lblDLLIcon.setForeground(Color.LIGHT_GRAY);
    this.lblOpenIcon = new JLabel("...");
    this.lblOpenIcon.setForeground(Color.LIGHT_GRAY);
    this.lblSchemaIcon = new JLabel("...");
    this.lblSchemaIcon.setForeground(Color.LIGHT_GRAY);
    this.lblCreationIcon = new JLabel("...");
    this.lblCreationIcon.setForeground(Color.LIGHT_GRAY);
    this.lblSpatialIndexGothicIcon = new JLabel("...");
    this.lblSpatialIndexGothicIcon.setForeground(Color.LIGHT_GRAY);
    this.lblVersionIcon = new JLabel("...");
    this.lblVersionIcon.setForeground(Color.LIGHT_GRAY);
    this.lblLayersIcon = new JLabel("...");
    this.lblLayersIcon.setForeground(Color.LIGHT_GRAY);
    this.lblGeneConstraintsIcon = new JLabel("...");
    this.lblGeneConstraintsIcon.setForeground(Color.LIGHT_GRAY);

    this.lblEnd = new JLabel("Waiting for initialisation");
    this.lblEndIcon = new JLabel("");
    this.buttonOK = new JButton("OK");
    this.buttonOK.setEnabled(false);
    this.buttonOK.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getFrameInit().dispose();
      }
    });

  }

  /**
   * Components placement
   */

  private void placeComponents() {

    // Central panel

    this.pnlCentral.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();

    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridheight = 1;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(15, 15, 5, 15);
    this.pnlCentral.add(this.lblInterface, gbc);
    gbc.gridx += 2;
    gbc.gridwidth = 1;
    this.pnlCentral.add(this.lblInterfaceIcon, gbc);

    gbc.gridx -= 2;
    gbc.gridy++;
    gbc.gridwidth = 2;
    gbc.insets = new Insets(5, 15, 5, 15);
    this.pnlCentral.add(this.lblLoading, gbc);
    gbc.gridx += 2;
    gbc.gridwidth = 1;
    this.pnlCentral.add(this.lblLoadingIcon, gbc);

    gbc.gridx -= 2;
    gbc.gridy++;
    gbc.gridwidth = 2;
    this.pnlCentral.add(this.lblEnrichment, gbc);
    gbc.gridx += 2;
    gbc.gridwidth = 1;
    this.pnlCentral.add(this.lblEnrichmentIcon, gbc);

    gbc.gridx -= 2;
    gbc.gridy++;
    gbc.gridwidth = 2;
    gbc.insets = new Insets(25, 15, 5, 15);
    this.pnlCentral.add(this.lblStartGothic, gbc);
    gbc.gridx += 2;
    gbc.gridwidth = 1;
    this.pnlCentral.add(this.lblStartGothicIcon, gbc);

    gbc.gridx -= 2;
    gbc.gridy++;
    gbc.insets = new Insets(5, 15, 5, 5);
    this.pnlCentral.add(this.lblID, gbc);
    gbc.gridx++;
    this.pnlCentral.add(this.lblIDValue, gbc);
    gbc.gridx++;
    this.pnlCentral.add(this.lblIDIcon, gbc);

    gbc.gridx -= 2;
    gbc.gridy++;
    this.pnlCentral.add(this.lblProjections, gbc);
    gbc.gridx++;
    this.pnlCentral.add(this.lblProjectionsValue, gbc);
    gbc.gridx++;
    this.pnlCentral.add(this.lblProjectionsIcon, gbc);

    gbc.gridx -= 2;
    gbc.gridy++;
    this.pnlCentral.add(this.lblDataset, gbc);
    gbc.gridx++;
    this.pnlCentral.add(this.lblDatasetValue, gbc);
    gbc.gridx++;
    this.pnlCentral.add(this.lblDatasetIcon, gbc);

    gbc.gridx -= 2;
    gbc.gridy++;
    gbc.gridwidth = 2;
    gbc.insets = new Insets(5, 15, 5, 15);
    this.pnlCentral.add(this.lblDLL, gbc);
    gbc.gridx += 2;
    gbc.gridwidth = 1;
    this.pnlCentral.add(this.lblDLLIcon, gbc);

    gbc.gridx -= 2;
    gbc.gridy++;
    gbc.gridwidth = 2;
    this.pnlCentral.add(this.lblOpen, gbc);
    gbc.gridx += 2;
    gbc.gridwidth = 1;
    this.pnlCentral.add(this.lblOpenIcon, gbc);

    gbc.gridx -= 2;
    gbc.gridy++;
    gbc.gridwidth = 2;
    this.pnlCentral.add(this.lblSchema, gbc);
    gbc.gridx += 2;
    gbc.gridwidth = 1;
    this.pnlCentral.add(this.lblSchemaIcon, gbc);

    gbc.gridx -= 2;
    gbc.gridy++;
    gbc.gridwidth = 2;
    this.pnlCentral.add(this.lblCreation, gbc);
    gbc.gridx += 2;
    gbc.gridwidth = 1;
    this.pnlCentral.add(this.lblCreationIcon, gbc);

    gbc.gridx -= 2;
    gbc.gridy++;
    gbc.gridwidth = 2;
    this.pnlCentral.add(this.lblSpatialIndexGothic, gbc);
    gbc.gridx += 2;
    gbc.gridwidth = 1;
    this.pnlCentral.add(this.lblSpatialIndexGothicIcon, gbc);

    gbc.gridx -= 2;
    gbc.gridy++;
    gbc.gridwidth = 2;
    this.pnlCentral.add(this.lblVersion, gbc);
    gbc.gridx += 2;
    gbc.gridwidth = 1;
    this.pnlCentral.add(this.lblVersionIcon, gbc);

    gbc.gridx -= 2;
    gbc.gridy++;
    gbc.gridwidth = 2;
    gbc.insets = new Insets(25, 15, 5, 15);
    this.pnlCentral.add(this.lblLayers, gbc);
    gbc.gridx += 2;
    gbc.gridwidth = 1;
    this.pnlCentral.add(this.lblLayersIcon, gbc);

    gbc.gridx -= 2;
    gbc.gridy++;
    gbc.gridwidth = 2;
    gbc.insets = new Insets(5, 15, 15, 15);
    this.pnlCentral.add(this.lblGeneConstraints, gbc);
    gbc.gridx += 2;
    gbc.gridwidth = 1;
    this.pnlCentral.add(this.lblGeneConstraintsIcon, gbc);

    // End panel

    this.pnlEnd.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();

    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridheight = 1;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.insets = new Insets(25, 15, 5, 15);
    this.pnlEnd.add(this.lblEndIcon, gbc);

    gbc.gridx++;
    gbc.anchor = GridBagConstraints.WEST;
    this.pnlEnd.add(this.lblEnd, gbc);

    gbc.gridx = 1;
    gbc.gridy++;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.insets = new Insets(5, 15, 15, 15);
    this.pnlEnd.add(this.buttonOK, gbc);

    // Global layout

    this.setLayout(new BorderLayout());
    this.add(this.pnlCentral, BorderLayout.CENTER);
    this.add(this.pnlEnd, BorderLayout.SOUTH);

  }

  /**
   * Frame construction
   */

  public InitFrame() {

    this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);

    this.setMinimumSize(new Dimension(400, 500));
    this.setTitle("Initialising CartAGen");
    this.setIconImage(this.getIcon());
    this.setResizable(false);
    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

    this.initComponents();
    this.placeComponents();

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension size = this.getSize();
    screenSize.height = screenSize.height / 2;
    screenSize.width = screenSize.width / 2;
    size.height = size.height / 2;
    size.width = size.width / 2;
    int y = screenSize.height - size.height;
    int x = screenSize.width - size.width;
    this.setLocation(x, y);

    this.pack();
  }

}
