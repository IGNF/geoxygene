/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.dataloading;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.geotools.data.shapefile.shp.ShapefileException;

import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.cartagen.software.dataset.CartAGenEnrichment;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;

public class EnrichFrameOld extends JDialog implements ActionListener {
  private static final long serialVersionUID = -6992190369890036500L;

  private static EnrichFrameOld enrichFrame = null;

  /**
   * Recuperation de l'instance unique (singleton)
   * @return instance unique (singleton) de EnrichFrame
   */
  public static EnrichFrameOld getInstance() {
    if (EnrichFrameOld.enrichFrame == null) {
      synchronized (EnrichFrameOld.class) {
        if (EnrichFrameOld.enrichFrame == null) {
          EnrichFrameOld.enrichFrame = new EnrichFrameOld();
        }
      }
    }
    return EnrichFrameOld.enrichFrame;
  }

  // Components

  private JCheckBox enrichRelief;
  private JCheckBox enrichLandUse;
  private JCheckBox enrichBuildings;
  private JCheckBox enrichBuildingsAlign;
  private JCheckBox enrichRoads;
  private JCheckBox enrichHydro;
  private JLabel espace1;
  private JCheckBox buildNetworkFaces;
  private JLabel espace2;
  private JCheckBox reset;
  private final SourceDLM sourceDlm;
  private final int scale;
  private int version = 1;// version 1 pour l'ancien chargeur, version 2 pour le

  // nouveau chargeur

  public boolean isResetSelected() {
    return this.reset.isSelected();
  }

  private final JButton ok = new JButton("Valider");

  /**
   * Construction of the frame
   */

  public EnrichFrameOld() {
    super((JFrame) null, true);
    // super("Needed enrichments");
    this.sourceDlm = ImportDataFrame.getInstance(false).getSourceDlm();
    this.scale = ImportDataFrame.getInstance(false).getScale();
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    this.initFrame();
    this.pack();
    // this.setLocation(200,100);
    this.setLocationRelativeTo(null);
  }

  /**
   * Initialisation of the frame
   * @return
   */

  public void initFrame() {

    JPanel pnl = new JPanel();
    pnl.setLayout(new BorderLayout());

    // Creation of the checkboxes

    this.enrichRelief = new JCheckBox("Relief enrichment ", false);
    this.enrichRelief.setMnemonic(KeyEvent.VK_R);

    this.enrichLandUse = new JCheckBox("Land use enrichment", false);
    this.enrichLandUse.setMnemonic(KeyEvent.VK_L);

    this.enrichBuildings = new JCheckBox("Building enrichment: towns & blocks",
        false);
    this.enrichBuildings.setMnemonic(KeyEvent.VK_B);
    this.enrichBuildings.setSelected(true);

    this.enrichBuildingsAlign = new JCheckBox(
        "Building enrichment: urban alignments", false);
    this.enrichBuildingsAlign.setMnemonic(KeyEvent.VK_B);
    this.enrichBuildingsAlign.setSelected(true);

    this.enrichRoads = new JCheckBox("Road enrichment", false);
    this.enrichRoads.setMnemonic(KeyEvent.VK_T);
    this.enrichRoads.setSelected(true);

    this.enrichHydro = new JCheckBox("Hydrography enrichment", false);
    this.enrichHydro.setMnemonic(KeyEvent.VK_H);

    this.espace1 = new JLabel("");
    this.buildNetworkFaces = new JCheckBox("Network faces construction", false);
    this.buildNetworkFaces.setMnemonic(KeyEvent.VK_H);

    this.espace2 = new JLabel("");
    this.reset = new JCheckBox("Reset of the whole dataset", true);
    this.reset.setMnemonic(KeyEvent.VK_S);
    this.ok.addActionListener(this);

    JPanel checkPanel = new JPanel(new GridLayout(0, 1));
    checkPanel.setBorder(BorderFactory
        .createTitledBorder("Please select the needed enrichments"));

    checkPanel.add(this.enrichRelief);
    checkPanel.add(this.enrichLandUse);
    checkPanel.add(this.enrichBuildings);
    checkPanel.add(this.enrichBuildingsAlign);
    checkPanel.add(this.enrichRoads);
    checkPanel.add(this.enrichHydro);
    checkPanel.add(this.espace1);
    checkPanel.add(this.buildNetworkFaces);
    checkPanel.add(this.espace2);

    // Validation

    Box bBox = Box.createHorizontalBox();
    bBox.add(this.reset);
    bBox.add(this.ok);
    pnl.add(bBox, "South");
    pnl.add(checkPanel, BorderLayout.LINE_START);
    pnl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    this.setContentPane(pnl);

    this.setLocationRelativeTo(null);
  }

  /**
   * Validation of the frame
   */

  @Override
  public void actionPerformed(ActionEvent e) {

    Object source = e.getSource();

    // update the CartAGenDB information on enrichments
    CartAGenDB db = CartAGenDocOld.getInstance().getCurrentDataset()
        .getCartAGenDB();

    if (source == this.ok) {

      if (this.enrichRelief.isSelected()) {
        CartagenApplication.getInstance().setEnrichissementRelief(true);
        db.getEnrichments().add(CartAGenEnrichment.RELIEF);
      } else {
        CartagenApplication.getInstance().setEnrichissementRelief(false);
      }

      if (this.enrichLandUse.isSelected()) {
        CartagenApplication.getInstance().setEnrichissementOccSol(true);
        db.getEnrichments().add(CartAGenEnrichment.LAND_USE);
      } else {
        CartagenApplication.getInstance().setEnrichissementOccSol(false);
      }

      if (this.enrichBuildingsAlign.isSelected()) {
        CartagenApplication.getInstance().setEnrichissementBatiAlign(true);
        db.getEnrichments().add(CartAGenEnrichment.BUILDINGS_ALIGNMENT);
      } else {
        CartagenApplication.getInstance().setEnrichissementBatiAlign(false);
      }

      if (this.enrichBuildings.isSelected()) {
        CartagenApplication.getInstance().setEnrichissementBati(true);
        db.getEnrichments().add(CartAGenEnrichment.BUILDINGS);
      } else {
        CartagenApplication.getInstance().setEnrichissementBati(false);
        CartagenApplication.getInstance().setEnrichissementBatiAlign(false);
      }

      if (this.enrichRoads.isSelected()) {
        CartagenApplication.getInstance().setEnrichissementRoutier(true);
        db.getEnrichments().add(CartAGenEnrichment.ROAD_NETWORK);
      } else {
        CartagenApplication.getInstance().setEnrichissementRoutier(false);
      }

      if (this.enrichHydro.isSelected()) {
        CartagenApplication.getInstance().setEnrichissementHydro(true);
        db.getEnrichments().add(CartAGenEnrichment.HYDRO_NETWORK);
      } else {
        CartagenApplication.getInstance().setEnrichissementHydro(false);
      }

      if (this.buildNetworkFaces.isSelected()) {
        CartagenApplication.getInstance().setConstructNetworkFaces(true);
        db.getEnrichments().add(CartAGenEnrichment.NETWORK_FACES);
      } else {
        CartagenApplication.getInstance().setConstructNetworkFaces(false);
      }

      this.setVisible(false);

      try {// computeDataLoading2=new charger computeDataLoading=old charger
        // if(version==2)
        LoaderUtil.computeDataLoading(this.sourceDlm, this.scale);
        // else LoaderUtil.computeDataLoading(sourceDlm, scale);
      } catch (ShapefileException e1) {
        e1.printStackTrace();
      } catch (IOException e1) {
        e1.printStackTrace();
      }

    }

  }

  public void setVersion(int version) {
    this.version = version;
  }

  public int getVersion() {
    return this.version;
  }

}
