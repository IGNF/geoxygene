/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import fr.ign.cogit.cartagen.core.genericschema.airport.IAirportArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayArea;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterNode;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayNode;
import fr.ign.cogit.cartagen.core.genericschema.railway.ITriageArea;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadArea;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.ITown;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanAlignment;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.LeftPanel;

/**
 * @author julien Gaffuri 6 mars 2009
 */
public class GeneralisationLeftPanelComplement extends JPanel {
  private static final long serialVersionUID = -3719644987901188303L;

  // le panneau gauche
  LeftPanel pg = null;

  // le bouton initial/final
  public JButton switchInitialFinal = new JButton("Display initial data");
  private boolean isInitialData = false;

  // les icônes
  private JLabel iconSee = new JLabel(new ImageIcon(
      GeneralisationLeftPanelComplement.class
          .getResource("/images/iconSee.png").getPath().replaceAll("%20", " ")));
  private JLabel iconSelect = new JLabel(new ImageIcon(
      GeneralisationLeftPanelComplement.class
          .getResource("/images/iconSelect.png").getPath()
          .replaceAll("%20", " ")));
  private JLabel iconSeeInit = new JLabel(new ImageIcon(
      GeneralisationLeftPanelComplement.class
          .getResource("/images/iconSeeInit.png").getPath()
          .replaceAll("%20", " ")));

  // URBAN
  private JLabel lUrban = new JLabel("URBAN");

  public JLabel lBati = new JLabel(" Building");
  public JCheckBox cVoirBati = new JCheckBox();
  public JCheckBox cSelectBati = new JCheckBox();
  public JCheckBox cVoirBatiInitial = new JCheckBox();

  public JLabel lVille = new JLabel(" Town");
  public JCheckBox cVoirVille = new JCheckBox();
  public JCheckBox cSelectVille = new JCheckBox();
  public JCheckBox cVoirVilleInitial = new JCheckBox();

  public JLabel lIlot = new JLabel(" Block");
  public JCheckBox cVoirIlot = new JCheckBox();
  public JCheckBox cSelectIlot = new JCheckBox();
  public JCheckBox cVoirIlotInitial = new JCheckBox();

  public JLabel lAlign = new JLabel(" Alignment");
  public JCheckBox cVoirAlign = new JCheckBox();
  public JCheckBox cSelectAlign = new JCheckBox();
  public JCheckBox cVoirAlignInitial = new JCheckBox();

  // NETWORKS
  private JLabel lReseaux = new JLabel("NETWORKS");

  public JLabel lRR = new JLabel(" Road");
  public JCheckBox cVoirRR = new JCheckBox();
  public JCheckBox cSelectRR = new JCheckBox();
  public JCheckBox cVoirRRInitial = new JCheckBox();

  public JLabel lRF = new JLabel(" Rail");
  public JCheckBox cVoirRF = new JCheckBox();
  public JCheckBox cSelectRF = new JCheckBox();
  public JCheckBox cVoirRFInitial = new JCheckBox();

  public JLabel lRH = new JLabel(" Hydrography");
  public JCheckBox cVoirRH = new JCheckBox();
  public JCheckBox cSelectRH = new JCheckBox();
  public JCheckBox cVoirRHInitial = new JCheckBox();

  public JLabel lRE = new JLabel(" Electricity");
  public JCheckBox cVoirRE = new JCheckBox();
  public JCheckBox cSelectRE = new JCheckBox();
  public JCheckBox cVoirREInitial = new JCheckBox();

  // RELIEF
  private JLabel lRelief = new JLabel("RELIEF");

  public JLabel lCN = new JLabel(" Contour");
  public JCheckBox cVoirCN = new JCheckBox();
  public JCheckBox cSelectCN = new JCheckBox();
  public JCheckBox cVoirCNInitial = new JCheckBox();

  public JLabel lPointCote = new JLabel(" HeightSpot");
  public JCheckBox cVoirPointCote = new JCheckBox();
  public JCheckBox cSelectPointCote = new JCheckBox();
  public JCheckBox cVoirPointCoteInitial = new JCheckBox();

  public JLabel lOmbrageTransparent = new JLabel(" Shading");
  public JCheckBox cVoirOmbrageTransparent = new JCheckBox();
  public JCheckBox cSelectOmbrageTransparent = new JCheckBox();
  public JCheckBox cVoirOmbrageTransparentInitial = new JCheckBox();

  public JLabel lOmbrageOpaque = new JLabel(" Shading (opaque)");
  public JCheckBox cVoirOmbrageOpaque = new JCheckBox();
  public JCheckBox cSelectOmbrageOpaque = new JCheckBox();
  public JCheckBox cVoirOmbrageOpaqueInitial = new JCheckBox();

  public JLabel lMNTDegrade = new JLabel(" DTM");
  public JCheckBox cVoirMNTDegrade = new JCheckBox();
  public JCheckBox cSelectMNTDegrade = new JCheckBox();
  public JCheckBox cVoirMNTDegradeInitial = new JCheckBox();

  public JLabel lHypsometrie = new JLabel(" Hypsometry");
  public JCheckBox cVoirHypsometrie = new JCheckBox();
  public JCheckBox cSelectHypsometrie = new JCheckBox();
  public JCheckBox cVoirHypsometrieInitial = new JCheckBox();

  public JLabel lReliefElem = new JLabel(" Relief Element");
  public JCheckBox cVoirReliefElem = new JCheckBox();
  public JCheckBox cSelectReliefElem = new JCheckBox();
  public JCheckBox cVoirReliefElemInitial = new JCheckBox();

  // LAND USE
  private JLabel lLandUse = new JLabel("LAND USE");

  public JLabel lOccSol = new JLabel(" Land use");
  public JCheckBox cVoirOccSol = new JCheckBox();
  public JCheckBox cSelectOccSol = new JCheckBox();
  public JCheckBox cVoirOccSolInitial = new JCheckBox();

  public JLabel lAdmin = new JLabel(" Administrative");
  public JCheckBox cVoirAdmin = new JCheckBox();
  public JCheckBox cSelectAdmin = new JCheckBox();
  public JCheckBox cVoirAdminInitial = new JCheckBox();

  // TOPOLOGY
  private JLabel lTopology = new JLabel("TOPOLOGY");

  public JLabel lNetworkFaces = new JLabel(" Network Faces");
  public JCheckBox cVoirNetworkFaces = new JCheckBox();
  public JCheckBox cSelectNetworkFaces = new JCheckBox();
  public JCheckBox cVoirNetworkFacesInitial = new JCheckBox();

  // MISC
  private JLabel lMisc = new JLabel("MISC");

  public JLabel lAirports = new JLabel(" Airports");
  public JCheckBox cVoirAirport = new JCheckBox();
  public JCheckBox cSelectAirports = new JCheckBox();
  public JCheckBox cVoirAirportsInitial = new JCheckBox();

  public JLabel lMask = new JLabel(" Dataset limits");
  public JCheckBox cVoirMask = new JCheckBox();
  public JCheckBox cSelectMask = new JCheckBox();
  public JCheckBox cVoirMaskInitial = new JCheckBox();

  public JLabel lPOI = new JLabel(" POIs");
  public JCheckBox cVoirPOI = new JCheckBox();
  public JCheckBox cSelectPOI = new JCheckBox();
  public JCheckBox cVoirPOIInitial = new JCheckBox();
  /**
	 */
  private static GeneralisationLeftPanelComplement content = null;

  public static GeneralisationLeftPanelComplement getInstance() {
    if (GeneralisationLeftPanelComplement.content == null) {
      GeneralisationLeftPanelComplement.content = new GeneralisationLeftPanelComplement();
    }
    return GeneralisationLeftPanelComplement.content;
  }

  private GeneralisationLeftPanelComplement() {
    this.pg = CartagenApplication.getInstance().getFrame().getLeftPanel();
  }

  public void add() {

    JPanel panneau = this.pg.getPannel();
    Font font = this.pg.getFont();
    Font lFont = new Font("Arial", Font.BOLD, 10);

    GridBagConstraints c;
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 1;

    // SWITCH BUTTON

    c.insets = new Insets(40, 5, 0, 0);
    c.anchor = GridBagConstraints.CENTER;
    c.gridwidth = GridBagConstraints.REMAINDER;
    Font buttonFont = new Font("Arial", Font.BOLD, 9);
    this.switchInitialFinal.setMinimumSize(new Dimension(110, 30));
    this.switchInitialFinal.setMaximumSize(new Dimension(110, 30));
    this.switchInitialFinal.setFont(buttonFont);
    panneau.add(this.switchInitialFinal, c);
    this.switchInitialFinal.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        if (GeneralisationLeftPanelComplement.getInstance().isInitialData == true) {
          GeneralisationLeftPanelComplement.getInstance().displayAllFinal();
          GeneralisationLeftPanelComplement.getInstance().isInitialData = false;
          GeneralisationLeftPanelComplement.getInstance().switchInitialFinal
              .setText("Display Initial data");
        } else {
          GeneralisationLeftPanelComplement.getInstance().displayAllInitial();
          GeneralisationLeftPanelComplement.getInstance().isInitialData = true;
          GeneralisationLeftPanelComplement.getInstance().switchInitialFinal
              .setText("Display Final data");
        }
      }
    });

    // ICONS

    c.gridy++;
    c.gridx = 0;
    c.gridwidth = 1;
    c.insets = new Insets(30, 5, 0, 0);
    panneau.add(this.iconSee, c);
    c.gridx++;
    panneau.add(this.iconSelect, c);
    c.gridx++;
    panneau.add(this.iconSeeInit, c);

    c.anchor = GridBagConstraints.WEST;

    // URBAN

    c.gridy++;
    c.gridx = 0;
    c.insets = new Insets(8, 13, 5, 0);
    c.gridwidth = GridBagConstraints.REMAINDER;
    this.lUrban.setFont(lFont);
    panneau.add(this.lUrban, c);
    c.gridwidth = 1;

    // Building

    c.gridy++;
    c.gridx = 0;
    c.insets = new Insets(0, 5, 0, 0);
    this.cVoirBati.setFont(font);
    panneau.add(this.cVoirBati, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 0);
    this.cSelectBati.setFont(font);
    panneau.add(this.cSelectBati, c);
    c.gridx++;
    this.cVoirBatiInitial.setFont(font);
    panneau.add(this.cVoirBatiInitial, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 5);
    this.lBati.setFont(font);
    panneau.add(this.lBati, c);

    this.cVoirBati.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getLayerGroup().cVoirBati
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirBati
                .isSelected());
      }
    });

    this.cSelectBati.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .getLayer(LayerGroup.LAYER_BUILDING)
            .setSelectable(
                GeneralisationLeftPanelComplement.this.cSelectBati.isSelected());
      }
    });

    this.cVoirBatiInitial.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getInitialLayerGroup().cVoirBati
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirBatiInitial
                .isSelected());
      }
    });

    // Town

    c.gridy++;
    c.gridx = 0;
    c.insets = new Insets(0, 5, 0, 0);
    this.cVoirVille.setFont(font);
    panneau.add(this.cVoirVille, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 0);
    this.cSelectVille.setFont(font);
    panneau.add(this.cSelectVille, c);
    c.gridx++;
    this.cVoirVilleInitial.setFont(font);
    panneau.add(this.cVoirVilleInitial, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 5);
    this.lVille.setFont(font);
    panneau.add(this.lVille, c);

    this.cVoirVille.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getLayerGroup().cVoirVille
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirVille
                .isSelected());
      }
    });

    this.cSelectVille.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .getLayer(LayerGroup.LAYER_TOWN)
            .setSelectable(
                GeneralisationLeftPanelComplement.this.cSelectVille
                    .isSelected());
      }
    });

    this.cVoirVilleInitial.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getInitialLayerGroup().cVoirBati
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirBatiInitial
                .isSelected());
      }
    });

    // Block

    c.gridy++;
    c.gridx = 0;
    c.insets = new Insets(0, 5, 0, 0);
    this.cVoirIlot.setFont(font);
    panneau.add(this.cVoirIlot, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 0);
    this.cSelectIlot.setFont(font);
    panneau.add(this.cSelectIlot, c);
    c.gridx++;
    this.cVoirIlotInitial.setFont(font);
    panneau.add(this.cVoirIlotInitial, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 5);
    this.lIlot.setFont(font);
    panneau.add(this.lIlot, c);

    this.cVoirIlot.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getLayerGroup().cVoirIlot
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirIlot
                .isSelected());
      }
    });

    this.cSelectIlot.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .getLayer(LayerGroup.LAYER_BLOCK)
            .setSelectable(
                GeneralisationLeftPanelComplement.this.cSelectIlot.isSelected());
      }
    });

    this.cVoirIlotInitial.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getInitialLayerGroup().cVoirIlot
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirIlotInitial
                .isSelected());
      }
    });

    // Alignment

    c.gridy++;
    c.gridx = 0;
    c.insets = new Insets(0, 5, 0, 0);
    this.cVoirAlign.setFont(font);
    panneau.add(this.cVoirAlign, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 0);
    this.cSelectAlign.setFont(font);
    panneau.add(this.cSelectAlign, c);
    c.gridx++;
    this.cVoirIlotInitial.setFont(font);
    panneau.add(this.cVoirAlignInitial, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 5);
    this.lAlign.setFont(font);
    panneau.add(this.lAlign, c);

    this.cVoirAlign.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getLayerGroup().cVoirAlign
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirAlign
                .isSelected());
      }
    });

    this.cSelectAlign.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .getLayer(LayerGroup.LAYER_URBAN_ALIGNMENT)
            .setSelectable(
                GeneralisationLeftPanelComplement.this.cSelectAlign
                    .isSelected());
      }
    });

    this.cVoirAlignInitial.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getInitialLayerGroup().cVoirAlign
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirBatiInitial
                .isSelected());
      }
    });

    // NETWORKS

    c.gridy++;
    c.gridx = 0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = new Insets(15, 13, 5, 0);
    this.lReseaux.setFont(lFont);
    panneau.add(this.lReseaux, c);
    c.gridwidth = 1;

    // Road

    c.gridy++;
    c.gridx = 0;
    c.insets = new Insets(0, 5, 0, 0);
    this.cVoirRR.setFont(font);
    panneau.add(this.cVoirRR, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 0);
    this.cSelectRR.setFont(font);
    panneau.add(this.cSelectRR, c);
    c.gridx++;
    this.cVoirRRInitial.setFont(font);
    panneau.add(this.cVoirRRInitial, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 5);
    this.lRR.setFont(font);
    panneau.add(this.lRR, c);

    this.cVoirRR.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getLayerGroup().cVoirRR
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirRR
                .isSelected());
      }
    });

    this.cSelectRR.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .getLayer(LayerGroup.LAYER_ROAD_LINE)
            .setSelectable(
                GeneralisationLeftPanelComplement.this.cSelectRR.isSelected());
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .getLayer(LayerGroup.LAYER_ROAD_NODE)
            .setSelectable(
                GeneralisationLeftPanelComplement.this.cSelectRR.isSelected());
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .getLayer(LayerGroup.LAYER_PATH)
            .setSelectable(
                GeneralisationLeftPanelComplement.this.cSelectRR.isSelected());
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .getLayer(LayerGroup.LAYER_BRIDGE_PT)
            .setSelectable(
                GeneralisationLeftPanelComplement.this.cSelectRR.isSelected());
      }
    });

    this.cVoirRRInitial.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getInitialLayerGroup().cVoirRR
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirRRInitial
                .isSelected());
      }
    });

    // Rail

    c.gridy++;
    c.gridx = 0;
    c.insets = new Insets(0, 5, 0, 0);
    this.cVoirRF.setFont(font);
    panneau.add(this.cVoirRF, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 0);
    this.cSelectRF.setFont(font);
    panneau.add(this.cSelectRF, c);
    c.gridx++;
    this.cVoirRFInitial.setFont(font);
    panneau.add(this.cVoirRFInitial, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 5);
    this.lRF.setFont(font);
    panneau.add(this.lRF, c);

    this.cVoirRF.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getLayerGroup().cVoirRF
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirRF
                .isSelected());
      }
    });

    this.cSelectRF.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .getLayer(LayerGroup.LAYER_RAILWAY_LINE)
            .setSelectable(
                GeneralisationLeftPanelComplement.this.cSelectRF.isSelected());
      }
    });

    this.cVoirRFInitial.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getInitialLayerGroup().cVoirRF
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirRFInitial
                .isSelected());
      }
    });

    // Hydro

    c.gridy++;
    c.gridx = 0;
    c.insets = new Insets(0, 5, 0, 0);
    this.cVoirRH.setFont(font);
    panneau.add(this.cVoirRH, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 0);
    this.cSelectRH.setFont(font);
    panneau.add(this.cSelectRH, c);
    c.gridx++;
    this.cVoirRHInitial.setFont(font);
    panneau.add(this.cVoirRHInitial, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 5);
    this.lRH.setFont(font);
    panneau.add(this.lRH, c);

    this.cVoirRH.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getLayerGroup().cVoirRH
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirRH
                .isSelected());
      }
    });

    this.cSelectRH.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .getLayer(LayerGroup.LAYER_WATER_LINE)
            .setSelectable(
                GeneralisationLeftPanelComplement.this.cSelectRH.isSelected());
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .getLayer(LayerGroup.LAYER_WATER_AREA)
            .setSelectable(
                GeneralisationLeftPanelComplement.this.cSelectRH.isSelected());
      }
    });

    this.cVoirRHInitial.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getInitialLayerGroup().cVoirRH
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirRHInitial
                .isSelected());
      }
    });

    // Electricity

    c.gridy++;
    c.gridx = 0;
    c.insets = new Insets(0, 5, 0, 0);
    this.cVoirRE.setFont(font);
    panneau.add(this.cVoirRE, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 0);
    this.cSelectRE.setFont(font);
    panneau.add(this.cSelectRE, c);
    c.gridx++;
    this.cVoirREInitial.setFont(font);
    panneau.add(this.cVoirREInitial, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 5);
    this.lRE.setFont(font);
    panneau.add(this.lRE, c);

    this.cVoirRE.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getLayerGroup().cVoirRE
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirRE
                .isSelected());
      }
    });

    this.cSelectRE.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .getLayer(LayerGroup.LAYER_ELECRICITY_LINE)
            .setSelectable(
                GeneralisationLeftPanelComplement.this.cSelectRE.isSelected());
      }
    });

    this.cVoirREInitial.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getInitialLayerGroup().cVoirRE
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirREInitial
                .isSelected());
      }
    });

    // RELIEF

    c.gridy++;
    c.gridx = 0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = new Insets(15, 13, 5, 0);
    this.lRelief.setFont(lFont);
    panneau.add(this.lRelief, c);
    c.gridwidth = 1;

    // Contour

    c.gridy++;
    c.gridx = 0;
    c.insets = new Insets(0, 5, 0, 0);
    this.cVoirCN.setFont(font);
    panneau.add(this.cVoirCN, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 0);
    this.cSelectCN.setFont(font);
    panneau.add(this.cSelectCN, c);
    c.gridx++;
    this.cVoirCNInitial.setFont(font);
    panneau.add(this.cVoirCNInitial, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 5);
    this.lCN.setFont(font);
    panneau.add(this.lCN, c);

    this.cVoirCN.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getLayerGroup().cVoirCN
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirCN
                .isSelected());
      }
    });

    this.cSelectCN.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .getLayer(LayerGroup.LAYER_CONTOUR_LINE)
            .setSelectable(
                GeneralisationLeftPanelComplement.this.cSelectCN.isSelected());
      }
    });

    this.cVoirCNInitial.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getInitialLayerGroup().cVoirCN
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirCNInitial
                .isSelected());
      }
    });

    // Height Spot

    c.gridy++;
    c.gridx = 0;
    c.insets = new Insets(0, 5, 0, 0);
    this.cVoirPointCote.setFont(font);
    panneau.add(this.cVoirPointCote, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 0);
    this.cSelectPointCote.setFont(font);
    panneau.add(this.cSelectPointCote, c);
    c.gridx++;
    this.cVoirPointCoteInitial.setFont(font);
    panneau.add(this.cVoirPointCoteInitial, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 5);
    this.lPointCote.setFont(font);
    panneau.add(this.lPointCote, c);

    this.cVoirPointCote.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getLayerGroup().cVoirPointCote
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirPointCote
                .isSelected());
      }
    });

    this.cSelectPointCote.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .getLayer(LayerGroup.LAYER_SPOT_HEIGHT)
            .setSelectable(
                GeneralisationLeftPanelComplement.this.cSelectPointCote
                    .isSelected());
      }
    });

    this.cVoirPointCoteInitial.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getInitialLayerGroup().cVoirPointCote
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirPointCoteInitial
                .isSelected());
      }
    });

    // Shading

    c.gridy++;
    c.gridx = 0;
    c.insets = new Insets(0, 5, 0, 0);
    this.cVoirOmbrageTransparent.setFont(font);
    panneau.add(this.cVoirOmbrageTransparent, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 0);
    this.cSelectOmbrageTransparent.setFont(font);
    panneau.add(this.cSelectOmbrageTransparent, c);
    c.gridx++;
    this.cVoirOmbrageTransparentInitial.setFont(font);
    panneau.add(this.cVoirOmbrageTransparentInitial, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 5);
    this.lOmbrageTransparent.setFont(font);
    panneau.add(this.lOmbrageTransparent, c);

    this.cVoirOmbrageTransparent.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getLayerGroup().cVoirOmbrageTransparent
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirOmbrageTransparent
                .isSelected());
      }
    });

    this.cVoirOmbrageTransparentInitial.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getInitialLayerGroup().cVoirOmbrageTransparent
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirOmbrageTransparentInitial
                .isSelected());
      }
    });

    // DEM

    c.gridy++;
    c.gridx = 0;
    c.insets = new Insets(0, 5, 0, 0);
    this.cVoirMNTDegrade.setFont(font);
    panneau.add(this.cVoirMNTDegrade, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 0);
    this.cSelectMNTDegrade.setFont(font);
    panneau.add(this.cSelectMNTDegrade, c);
    c.gridx++;
    this.cVoirMNTDegradeInitial.setFont(font);
    panneau.add(this.cVoirMNTDegradeInitial, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 5);
    this.lMNTDegrade.setFont(font);
    panneau.add(this.lMNTDegrade, c);

    this.cVoirMNTDegrade.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getLayerGroup().cVoirMNTDegrade
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirMNTDegrade
                .isSelected());
      }
    });

    this.cVoirMNTDegradeInitial.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getInitialLayerGroup().cVoirMNTDegrade
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirMNTDegradeInitial
                .isSelected());
      }
    });

    // Hypsometry

    c.gridy++;
    c.gridx = 0;
    c.insets = new Insets(0, 5, 0, 0);
    this.cVoirHypsometrie.setFont(font);
    panneau.add(this.cVoirHypsometrie, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 0);
    this.cSelectHypsometrie.setFont(font);
    panneau.add(this.cSelectHypsometrie, c);
    c.gridx++;
    this.cVoirHypsometrieInitial.setFont(font);
    panneau.add(this.cVoirHypsometrieInitial, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 5);
    this.lHypsometrie.setFont(font);
    panneau.add(this.lHypsometrie, c);

    this.cVoirHypsometrie.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getLayerGroup().cVoirHypsometrie
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirHypsometrie
                .isSelected());
      }
    });

    this.cVoirHypsometrieInitial.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getInitialLayerGroup().cVoirHypsometrie
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirHypsometrieInitial
                .isSelected());
      }
    });

    // Relief Element

    c.gridy++;
    c.gridx = 0;
    c.insets = new Insets(0, 5, 0, 0);
    this.cVoirReliefElem.setFont(font);
    panneau.add(this.cVoirReliefElem, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 0);
    this.cSelectReliefElem.setFont(font);
    panneau.add(this.cSelectReliefElem, c);
    c.gridx++;
    this.cVoirReliefElemInitial.setFont(font);
    panneau.add(this.cVoirReliefElemInitial, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 5);
    this.lReliefElem.setFont(font);
    panneau.add(this.lReliefElem, c);

    this.cVoirReliefElem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getLayerGroup().cVoirReliefElem
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirReliefElem
                .isSelected());
      }
    });

    this.cSelectReliefElem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .getLayer(LayerGroup.LAYER_RELIEF_ELEM_LINE)
            .setSelectable(
                GeneralisationLeftPanelComplement.this.cSelectReliefElem
                    .isSelected());
      }
    });

    this.cVoirReliefElemInitial.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getInitialLayerGroup().cVoirReliefElem
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirReliefElemInitial
                .isSelected());
      }
    });

    // LAND USE

    c.gridy++;
    c.gridx = 0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = new Insets(15, 13, 5, 0);
    this.lLandUse.setFont(lFont);
    panneau.add(this.lLandUse, c);
    c.gridwidth = 1;

    // Land use

    c.gridy++;
    c.gridx = 0;
    c.insets = new Insets(0, 5, 0, 0);
    this.cVoirOccSol.setFont(font);
    panneau.add(this.cVoirOccSol, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 0);
    this.cSelectOccSol.setFont(font);
    panneau.add(this.cSelectOccSol, c);
    c.gridx++;
    this.cVoirOccSolInitial.setFont(font);
    panneau.add(this.cVoirOccSolInitial, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 5);
    this.lOccSol.setFont(font);
    panneau.add(this.lOccSol, c);

    this.cVoirOccSol.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getLayerGroup().cVoirOccSol
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirOccSol
                .isSelected());
      }
    });

    this.cSelectOccSol.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .getLayer(LayerGroup.LAYER_LAND_USE_AREA)
            .setSelectable(
                GeneralisationLeftPanelComplement.this.cSelectOccSol
                    .isSelected());
      }
    });

    this.cVoirOccSolInitial.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getInitialLayerGroup().cVoirOccSol
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirOccSolInitial
                .isSelected());
      }
    });

    // Admin

    c.gridy++;
    c.gridx = 0;
    c.insets = new Insets(0, 5, 5, 0);
    this.cVoirAdmin.setFont(font);
    panneau.add(this.cVoirAdmin, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 5, 0);
    this.cSelectAdmin.setFont(font);
    panneau.add(this.cSelectAdmin, c);
    c.gridx++;
    this.cVoirAdminInitial.setFont(font);
    panneau.add(this.cVoirAdminInitial, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 5, 5);
    this.lAdmin.setFont(font);
    panneau.add(this.lAdmin, c);

    this.cVoirAdmin.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getLayerGroup().cVoirAdmin
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirAdmin
                .isSelected());
      }
    });

    this.cSelectAdmin.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .getLayer(LayerGroup.LAYER_ADMIN_UNIT)
            .setSelectable(
                GeneralisationLeftPanelComplement.this.cSelectAdmin
                    .isSelected());
      }
    });

    this.cVoirAdminInitial.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getInitialLayerGroup().cVoirAdmin
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirAdminInitial
                .isSelected());
      }
    });

    // TOPOLOGY

    c.gridy++;
    c.gridx = 0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = new Insets(15, 13, 5, 0);
    this.lTopology.setFont(lFont);
    panneau.add(this.lTopology, c);
    c.gridwidth = 1;

    // Network faces

    c.gridy++;
    c.gridx = 0;
    c.insets = new Insets(0, 5, 0, 0);
    this.cVoirNetworkFaces.setFont(font);
    this.cVoirNetworkFaces.setEnabled(false);
    panneau.add(this.cVoirNetworkFaces, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 0);
    this.cSelectNetworkFaces.setFont(font);
    panneau.add(this.cSelectNetworkFaces, c);
    c.gridx++;
    this.cVoirNetworkFacesInitial.setFont(font);
    this.cVoirNetworkFacesInitial.setEnabled(false);
    panneau.add(this.cVoirNetworkFacesInitial, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 5);
    this.lNetworkFaces.setFont(font);
    panneau.add(this.lNetworkFaces, c);

    this.cSelectNetworkFaces.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .getLayer(LayerGroup.LAYER_NETWORK_FACE)
            .setSelectable(
                GeneralisationLeftPanelComplement.this.cSelectNetworkFaces
                    .isSelected());
      }
    });

    // MISC

    c.gridy++;
    c.gridx = 0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = new Insets(15, 13, 5, 0);
    this.lMisc.setFont(lFont);
    panneau.add(this.lMisc, c);
    c.gridwidth = 1;
    // Airports
    c.gridy++;
    c.gridx = 0;
    c.insets = new Insets(0, 5, 0, 0);
    this.cVoirAirport.setFont(font);
    this.cVoirAirport.setEnabled(true);
    panneau.add(this.cVoirAirport, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 0);
    this.cSelectAirports.setFont(font);
    panneau.add(this.cSelectAirports, c);
    c.gridx++;
    this.cVoirAirportsInitial.setFont(font);
    this.cVoirAirportsInitial.setEnabled(false);
    panneau.add(this.cVoirAirportsInitial, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 5);
    this.lAirports.setFont(font);
    panneau.add(this.lAirports, c);

    this.cVoirAirport.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getLayerGroup().cVoirAirport
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirAirport
                .isSelected());
      }
    });

    this.cSelectAirports.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .getLayer(LayerGroup.LAYER_AIRPORT)
            .setSelectable(
                GeneralisationLeftPanelComplement.this.cSelectAirports
                    .isSelected());
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .getLayer(LayerGroup.LAYER_RUNWAY)
            .setSelectable(
                GeneralisationLeftPanelComplement.this.cSelectAirports
                    .isSelected());
      }
    });

    // Mask

    c.gridy++;
    c.gridx = 0;
    c.insets = new Insets(0, 5, 15, 0);
    this.cVoirMask.setFont(font);
    panneau.add(this.cVoirMask, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 15, 0);
    this.cSelectMask.setFont(font);
    this.cSelectMask.setEnabled(false);
    panneau.add(this.cSelectMask, c);
    c.gridx++;
    this.cVoirMaskInitial.setFont(font);
    this.cVoirMaskInitial.setEnabled(false);
    panneau.add(this.cVoirMaskInitial, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 15, 5);
    this.lMask.setFont(font);
    panneau.add(this.lMask, c);

    this.cVoirMask.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getLayerGroup().cVoirMasque
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirMask
                .isSelected());
      }
    });

    // POIs

    c.gridy++;
    c.gridx = 0;
    c.insets = new Insets(0, 5, 15, 0);
    this.cVoirPOI.setFont(font);
    panneau.add(this.cVoirPOI, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 15, 0);
    this.cSelectPOI.setFont(font);
    this.cSelectPOI.setEnabled(true);
    panneau.add(this.cSelectPOI, c);
    c.gridx++;
    this.cVoirPOIInitial.setFont(font);
    this.cVoirPOIInitial.setEnabled(false);
    panneau.add(this.cVoirPOIInitial, c);
    c.gridx++;
    c.insets = new Insets(0, 0, 15, 5);
    this.lPOI.setFont(font);
    panneau.add(this.lPOI, c);

    this.cVoirPOI.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication.getInstance().getLayerGroup().cVoirPOI
            .setSelected(GeneralisationLeftPanelComplement.getInstance().cVoirPOI
                .isSelected());
      }
    });

    this.cSelectPOI.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .getLayer(LayerGroup.LAYER_POI)
            .setSelectable(
                GeneralisationLeftPanelComplement.this.cSelectPOI.isSelected());
      }
    });

    // scroll

    JScrollPane scroll = new JScrollPane(panneau,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    // scroll.setPreferredSize(new Dimension(200,1000));

    // ajoute le contenu au panneau gauche

    this.pg.add(scroll, CartagenApplication.getInstance().getFrame()
        .getLeftPanel().getGBC());

  }

  /**
   * displays all the final data and updates the panel in consequence
   */

  public void displayAllFinal() {

    CartagenApplication.getInstance().getFrame().getLeftPanel().cSymbol
        .setSelected(true);
    // CartagenApplication.getInstance().getFrame().getVisuPanel().symbolisationDisplay
    // = true;
    CartagenApplication.getInstance().getLayerGroup().symbolisationDisplay = true;

    CartagenApplication.getInstance().getFrame().getLeftPanel().cSymbolInitial
        .setSelected(false);
    // CartagenApplication.getInstance().getFrame().getVisuPanel().initialSymbolisationDisplay
    // = false;
    CartagenApplication.getInstance().getInitialLayerGroup().symbolisationDisplay = false;

    if (this.cVoirBati.isEnabled()) {
      this.cVoirBati.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirBati
          .setSelected(true);
    }
    this.cVoirBatiInitial.setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirBati
        .setSelected(false);

    if (this.cVoirVille.isEnabled()) {
      this.cVoirVille.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirVille
          .setSelected(true);
    }
    this.cVoirVilleInitial.setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirVille
        .setSelected(false);

    if (this.cVoirIlot.isEnabled()) {
      this.cVoirIlot.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirIlot
          .setSelected(true);
    }
    this.cVoirIlotInitial.setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirIlot
        .setSelected(false);

    if (this.cVoirAlign.isEnabled()) {
      this.cVoirAlign.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirAlign
          .setSelected(true);
    }
    this.cVoirAlignInitial.setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirAlign
        .setSelected(false);

    if (this.cVoirRR.isEnabled()) {
      this.cVoirRR.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirRR
          .setSelected(true);
    }
    this.cVoirRRInitial.setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirRR
        .setSelected(false);

    if (this.cVoirRH.isEnabled()) {
      this.cVoirRH.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirRH
          .setSelected(true);
    }
    this.cVoirRHInitial.setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirRH
        .setSelected(false);

    if (this.cVoirRF.isEnabled()) {
      this.cVoirRF.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirRF
          .setSelected(true);
    }
    this.cVoirRFInitial.setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirRF
        .setSelected(false);

    if (this.cVoirRE.isEnabled()) {
      this.cVoirRE.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirRE
          .setSelected(true);
    }
    this.cVoirREInitial.setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirRE
        .setSelected(false);

    if (this.cVoirCN.isEnabled()) {
      this.cVoirCN.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirCN
          .setSelected(true);
    }
    this.cVoirCNInitial.setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirCN
        .setSelected(false);

    if (this.cVoirPointCote.isEnabled()) {
      this.cVoirPointCote.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirPointCote
          .setSelected(true);
    }
    this.cVoirPointCoteInitial.setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirPointCote
        .setSelected(false);

    // if (cVoirOmbrageTransparent.isEnabled()) {
    // cVoirOmbrageTransparent.setSelected(true);
    // CartagenApplication.getInstance().getLayerGroup().cVoirOmbrageTransparent.setSelected(true);
    // }
    this.cVoirOmbrageTransparentInitial.setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirOmbrageTransparent
        .setSelected(false);

    // if (cVoirMNTDegrade.isEnabled()) {
    // cVoirMNTDegrade.setSelected(true);
    // CartagenApplication.getInstance().getLayerGroup().cVoirMNTDegrade.setSelected(true);
    // }
    this.cVoirMNTDegradeInitial.setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirMNTDegrade
        .setSelected(false);

    // if (cVoirHypsometrie.isEnabled()) {
    // cVoirHypsometrie.setSelected(true);
    // CartagenApplication.getInstance().getLayerGroup().cVoirHypsometrie.setSelected(true);
    // }
    this.cVoirHypsometrieInitial.setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirHypsometrie
        .setSelected(false);

    if (this.cVoirOccSol.isEnabled()) {
      this.cVoirOccSol.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirOccSol
          .setSelected(true);
    }
    this.cVoirOccSolInitial.setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirOccSol
        .setSelected(false);

    if (this.cVoirAdmin.isEnabled()) {
      this.cVoirAdmin.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirAdmin
          .setSelected(true);
    }
    this.cVoirAdminInitial.setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirAdmin
        .setSelected(false);

    if (this.cVoirAirport.isEnabled()) {
      this.cVoirAirport.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirAirport
          .setSelected(true);
    }
    this.cVoirAirportsInitial.setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirAirport
        .setSelected(false);

  }

  /**
   * displays all the initial data and updates the panel in consequence
   */

  public void displayAllInitial() {

    CartagenApplication.getInstance().getFrame().getLeftPanel().cSymbol
        .setSelected(false);
    // CartagenApplication.getInstance().getFrame().getVisuPanel().symbolisationDisplay
    // = false;
    CartagenApplication.getInstance().getLayerGroup().symbolisationDisplay = false;

    CartagenApplication.getInstance().getFrame().getLeftPanel().cSymbolInitial
        .setSelected(true);
    // CartagenApplication.getInstance().getFrame().getVisuPanel().initialSymbolisationDisplay
    // = true;
    CartagenApplication.getInstance().getInitialLayerGroup().symbolisationDisplay = true;

    if (this.cVoirBatiInitial.isEnabled()) {
      this.cVoirBatiInitial.setSelected(true);
      CartagenApplication.getInstance().getInitialLayerGroup().cVoirBati
          .setSelected(true);
    }
    this.cVoirBati.setSelected(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirBati
        .setSelected(false);

    if (this.cVoirVilleInitial.isEnabled()) {
      this.cVoirVilleInitial.setSelected(true);
      CartagenApplication.getInstance().getInitialLayerGroup().cVoirVille
          .setSelected(true);
    }
    this.cVoirVille.setSelected(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirVille
        .setSelected(false);

    if (this.cVoirIlotInitial.isEnabled()) {
      this.cVoirIlotInitial.setSelected(true);
      CartagenApplication.getInstance().getInitialLayerGroup().cVoirIlot
          .setSelected(true);
    }
    this.cVoirIlot.setSelected(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirIlot
        .setSelected(false);

    if (this.cVoirAlignInitial.isEnabled()) {
      this.cVoirAlignInitial.setSelected(true);
      CartagenApplication.getInstance().getInitialLayerGroup().cVoirAlign
          .setSelected(true);
    }
    this.cVoirAlign.setSelected(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirAlign
        .setSelected(false);

    if (this.cVoirRRInitial.isEnabled()) {
      this.cVoirRRInitial.setSelected(true);
      CartagenApplication.getInstance().getInitialLayerGroup().cVoirRR
          .setSelected(true);
    }
    this.cVoirRR.setSelected(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirRR
        .setSelected(false);

    if (this.cVoirRHInitial.isEnabled()) {
      this.cVoirRHInitial.setSelected(true);
      CartagenApplication.getInstance().getInitialLayerGroup().cVoirRH
          .setSelected(true);
    }
    this.cVoirRH.setSelected(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirRH
        .setSelected(false);

    if (this.cVoirRFInitial.isEnabled()) {
      this.cVoirRFInitial.setSelected(true);
      CartagenApplication.getInstance().getInitialLayerGroup().cVoirRF
          .setSelected(true);
    }
    this.cVoirRF.setSelected(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirRF
        .setSelected(false);

    if (this.cVoirREInitial.isEnabled()) {
      this.cVoirREInitial.setSelected(true);
      CartagenApplication.getInstance().getInitialLayerGroup().cVoirRE
          .setSelected(true);
    }
    this.cVoirRE.setSelected(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirRE
        .setSelected(false);

    if (this.cVoirCNInitial.isEnabled()) {
      this.cVoirCNInitial.setSelected(true);
      CartagenApplication.getInstance().getInitialLayerGroup().cVoirCN
          .setSelected(true);
    }
    this.cVoirCN.setSelected(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirCN
        .setSelected(false);

    if (this.cVoirPointCoteInitial.isEnabled()) {
      this.cVoirPointCoteInitial.setSelected(true);
      CartagenApplication.getInstance().getInitialLayerGroup().cVoirPointCote
          .setSelected(true);
    }
    this.cVoirPointCote.setSelected(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirPointCote
        .setSelected(false);

    // if (cVoirOmbrageTransparentInitial.isEnabled()) {
    // cVoirOmbrageTransparentInitial.setSelected(true);
    // CartagenApplication.getInstance().getInitialLayerGroup().cVoirOmbrageTransparent.setSelected(true);
    // }
    this.cVoirOmbrageTransparent.setSelected(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirOmbrageTransparent
        .setSelected(false);

    // if (cVoirMNTDegradeInitial.isEnabled()) {
    // cVoirMNTDegradeInitial.setSelected(true);
    // CartagenApplication.getInstance().getInitialLayerGroup().cVoirMNTDegrade.setSelected(true);
    // }
    this.cVoirMNTDegrade.setSelected(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirMNTDegrade
        .setSelected(false);

    // if (cVoirHypsometrieInitial.isEnabled()) {
    // cVoirHypsometrieInitial.setSelected(true);
    // CartagenApplication.getInstance().getInitialLayerGroup().cVoirHypsometrie.setSelected(true);
    // }
    this.cVoirHypsometrie.setSelected(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirHypsometrie
        .setSelected(false);

    if (this.cVoirOccSolInitial.isEnabled()) {
      this.cVoirOccSolInitial.setSelected(true);
      CartagenApplication.getInstance().getInitialLayerGroup().cVoirOccSol
          .setSelected(true);
    }
    this.cVoirOccSol.setSelected(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirOccSol
        .setSelected(false);

    if (this.cVoirAdminInitial.isEnabled()) {
      this.cVoirAdminInitial.setSelected(true);
      CartagenApplication.getInstance().getInitialLayerGroup().cVoirAdmin
          .setSelected(true);
    }
    this.cVoirAdmin.setSelected(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirAdmin
        .setSelected(false);

    if (this.cVoirAirportsInitial.isEnabled()) {
      this.cVoirAirportsInitial.setSelected(true);
      CartagenApplication.getInstance().getInitialLayerGroup().cVoirAirport
          .setSelected(true);
    }
    this.cVoirAirport.setSelected(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirAirport
        .setSelected(false);

  }

  /**
   * Sets to {@code true} the display of the layer related to a given geo object
   * class.
   * @param classObj
   */
  public void setLayerDisplay(Class<?> classObj) {
    if (IBuilding.class.isAssignableFrom(classObj)) {
      this.cVoirBati.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirBati
          .setSelected(false);
    } else if (IWaterLine.class.isAssignableFrom(classObj)
        || IWaterArea.class.isAssignableFrom(classObj)
        || IWaterNode.class.isAssignableFrom(classObj)) {
      this.cVoirRH.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirRH
          .setSelected(false);
    } else if (IRoadLine.class.isAssignableFrom(classObj)
        || IRoadArea.class.isAssignableFrom(classObj)
        || IRoadNode.class.isAssignableFrom(classObj)) {
      this.cVoirRR.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirRR
          .setSelected(false);
    } else if (IRailwayLine.class.isAssignableFrom(classObj)
        || ITriageArea.class.isAssignableFrom(classObj)
        || IRailwayNode.class.isAssignableFrom(classObj)) {
      this.cVoirRF.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirRF
          .setSelected(false);
    } else if (IUrbanBlock.class.isAssignableFrom(classObj)) {
      this.cVoirIlot.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirIlot
          .setSelected(false);
    } else if (ITown.class.isAssignableFrom(classObj)) {
      this.cVoirVille.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirVille
          .setSelected(false);
    } else if (IUrbanAlignment.class.isAssignableFrom(classObj)) {
      this.cVoirAlign.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirAlign
          .setSelected(false);
    } else if (IAirportArea.class.isAssignableFrom(classObj)
        || IRunwayArea.class.isAssignableFrom(classObj)) {
      this.cVoirAirport.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirAirport
          .setSelected(false);
    } else if (IContourLine.class.isAssignableFrom(classObj)) {
      this.cVoirCN.setSelected(true);
      CartagenApplication.getInstance().getLayerGroup().cVoirCN
          .setSelected(false);
      // TODO il reste des cas à traiter !
    }
  }
}
