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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;

import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.cartagen.software.dataset.DataSetZone;
import fr.ign.cogit.cartagen.software.dataset.DigitalCartographicModel;
import fr.ign.cogit.cartagen.software.dataset.DigitalLandscapeModel;
import fr.ign.cogit.cartagen.software.dataset.ShapeFileDB;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolGroup;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolList;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolsUtil;

public class ImportDataFrame extends JFrame implements ActionListener {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static ImportDataFrame importDataFrame = null;

  /**
   * Recuperation de l'instance unique (singleton)
   * @return instance unique (singleton) de EnrichFrame
   */
  public static ImportDataFrame getInstance(boolean isInitial) {
    if (ImportDataFrame.importDataFrame == null) {
      synchronized (EnrichFrame.class) {
        if (ImportDataFrame.importDataFrame == null) {
          ImportDataFrame.importDataFrame = new ImportDataFrame(isInitial);
        }
      }
    }
    return ImportDataFrame.importDataFrame;
  }

  private SourceDLM sourceDlm;
  private int scale;
  private String datasetName;
  private String filePath;

  private boolean isInitial = false;
  public static String extentClass = null;
  public static boolean extentFile = false;

  private final JComboBox cbSourceDlm, cbType;
  private final JTextField txtZone, txtDataset, txtScale, txtPath, txtExtent;
  private final JButton btnBrowse;
  private final JRadioButton rbComputed, rbFile;

  public ImportDataFrame(boolean isInitial) {
    super("Import Shapefile data into a new dataset");
    this.isInitial = isInitial;
    this.setSize(600, 300);

    // *********************************
    // THE ZONE PARAMETERS PANEL
    // *********************************
    JPanel zonePanel = new JPanel();
    this.txtZone = new JTextField("Zone test");
    this.txtZone.setPreferredSize(new Dimension(80, 20));
    this.txtZone.setMaximumSize(new Dimension(80, 20));
    this.txtZone.setMinimumSize(new Dimension(80, 20));
    this.cbSourceDlm = new JComboBox(SourceDLM.values());
    this.cbSourceDlm.setPreferredSize(new Dimension(130, 20));
    this.cbSourceDlm.setMaximumSize(new Dimension(130, 20));
    this.cbSourceDlm.setMinimumSize(new Dimension(130, 20));
    this.rbComputed = new JRadioButton("Computed");
    this.rbComputed.setSelected(true);
    this.rbFile = new JRadioButton("From file");
    ButtonGroup bg = new ButtonGroup();
    bg.add(this.rbComputed);
    bg.add(this.rbFile);
    this.txtExtent = new JTextField();
    this.txtExtent.setPreferredSize(new Dimension(70, 20));
    this.txtExtent.setMaximumSize(new Dimension(70, 20));
    this.txtExtent.setMinimumSize(new Dimension(70, 20));
    Border blackLine = BorderFactory.createLineBorder(Color.BLACK);
    JPanel zonePanel1 = new JPanel();
    zonePanel1.add(new JLabel("Zone name : "));
    zonePanel1.add(this.txtZone);
    zonePanel1.add(Box.createHorizontalGlue());
    zonePanel1.add(new JLabel("Source DLM : "));
    zonePanel1.add(this.cbSourceDlm);
    zonePanel1.setLayout(new BoxLayout(zonePanel1, BoxLayout.X_AXIS));
    JPanel zonePanel2 = new JPanel();
    zonePanel2.add(new JLabel("Zone extent : "));
    zonePanel2.add(this.rbComputed);
    zonePanel2.add(Box.createHorizontalGlue());
    zonePanel2.add(this.rbFile);
    zonePanel2.add(this.txtExtent);
    zonePanel2.setLayout(new BoxLayout(zonePanel2, BoxLayout.X_AXIS));
    zonePanel.add(zonePanel1);
    zonePanel.add(zonePanel2);
    zonePanel.setBorder(BorderFactory.createTitledBorder(blackLine,
        "Description of the zone"));
    zonePanel.setLayout(new BoxLayout(zonePanel, BoxLayout.Y_AXIS));

    // *********************************
    // THE PATH PARAMETERS PANEL
    // *********************************
    JPanel pathPanel = new JPanel();
    this.txtDataset = new JTextField("CartAGen_dataset");
    this.txtDataset.setPreferredSize(new Dimension(90, 20));
    this.txtDataset.setMaximumSize(new Dimension(90, 20));
    this.txtDataset.setMinimumSize(new Dimension(90, 20));
    this.cbType = new JComboBox(new String[] { "DLM", "DCM" });
    this.cbType.setPreferredSize(new Dimension(90, 20));
    this.cbType.setMaximumSize(new Dimension(90, 20));
    this.cbType.setMinimumSize(new Dimension(90, 20));
    this.cbType.setSelectedItem("DLM");
    this.txtScale = new JTextField();
    this.txtScale.setPreferredSize(new Dimension(50, 20));
    this.txtScale.setMaximumSize(new Dimension(50, 20));
    this.txtScale.setMinimumSize(new Dimension(50, 20));
    // only digits authorized in this text field
    this.txtScale.setDocument(new PlainDocument() {
      private static final long serialVersionUID = 1L;

      @Override
      public void insertString(int offset, String str, AttributeSet a)
          throws BadLocationException {
        for (int i = 0; i < str.length(); i++) {
          if (!Character.isDigit(str.charAt(i))) {
            return;
          }
        }
        super.insertString(offset, str, a);
      }
    });
    try {
      this.txtScale.getDocument().insertString(0, "25000",
          new SimpleAttributeSet());
    } catch (BadLocationException e) {
    }
    this.txtPath = new JTextField();
    this.txtPath.setPreferredSize(new Dimension(170, 20));
    this.txtPath.setMaximumSize(new Dimension(170, 20));
    this.txtPath.setMinimumSize(new Dimension(170, 20));
    ImageIcon icon = new ImageIcon(ImportDataFrame.class
        .getResource("/images/browse.jpeg").getPath().replaceAll("%20", " "));
    this.btnBrowse = new JButton(icon);
    this.btnBrowse.addActionListener(this);
    this.btnBrowse.setActionCommand("Browse");
    JPanel pathPanel1 = new JPanel();
    pathPanel1.add(new JLabel("Dataset name : "));
    pathPanel1.add(this.txtDataset);
    pathPanel1.add(Box.createHorizontalGlue());
    pathPanel1.add(new JLabel("Symbol scale 1 : "));
    pathPanel1.add(this.txtScale);
    pathPanel1.setLayout(new BoxLayout(pathPanel1, BoxLayout.X_AXIS));
    JPanel pathPanel2 = new JPanel();
    pathPanel2.add(new JLabel("Shapefiles path : "));
    pathPanel2.add(this.txtPath);
    pathPanel2.add(this.btnBrowse);
    pathPanel2.setLayout(new BoxLayout(pathPanel2, BoxLayout.X_AXIS));
    pathPanel.add(pathPanel1);
    pathPanel.add(pathPanel2);
    pathPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pathPanel.setLayout(new BoxLayout(pathPanel, BoxLayout.Y_AXIS));

    // *********************************
    // THE OK/CANCEL PANEL
    // *********************************
    JPanel btnPanel = new JPanel();
    JButton okBtn = new JButton("OK");
    okBtn.addActionListener(this);
    okBtn.setActionCommand("OK");
    okBtn.setPreferredSize(new Dimension(100, 50));
    JButton cancelBtn = new JButton("Cancel");
    cancelBtn.addActionListener(this);
    cancelBtn.setActionCommand("Cancel");
    cancelBtn.setPreferredSize(new Dimension(100, 50));
    btnPanel.add(okBtn);
    btnPanel.add(cancelBtn);
    btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

    // *********************************
    // THE FRAME
    // *********************************
    this.getContentPane().add(zonePanel);
    this.getContentPane().add(pathPanel);
    this.getContentPane().add(btnPanel);
    this.getContentPane().setLayout(
        new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    this.pack();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("Cancel")) {
      this.setVisible(false);
    } else if (e.getActionCommand().equals("Browse")) {

      JFileChooser fc;
      try {
        CartagenApplication.getInstance().lectureFichierConfigurationDonnees();
        fc = new JFileChooser(CartagenApplication.getInstance()
            .getCheminDonnees());
      } catch (Exception exception) {
        fc = new JFileChooser();
      }

      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      // fc.setCurrentDirectory(new File("donnees/"));
      int returnVal = fc.showOpenDialog(this);
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File file = fc.getSelectedFile();
      this.txtPath.setText(file.getPath());

    } else if (e.getActionCommand().equals("OK")) {

      if (!this.isInitial) {
        this.importCurrentDataSet((SourceDLM) this.cbSourceDlm
            .getSelectedItem(), Integer.parseInt(this.txtScale.getText()),
            this.txtZone.getText(), this.txtDataset.getText(), this.txtPath
                .getText(), this.txtExtent.getText(), this.rbFile.isSelected(),
            this.cbType.getSelectedItem().equals("DLM"), true);
      }// end of if(!isInitial
      else {
        this.importInitialDataSet((SourceDLM) this.cbSourceDlm
            .getSelectedItem(), Integer.parseInt(this.txtScale.getText()),
            this.txtZone.getText(), this.txtDataset.getText(), this.txtPath
                .getText(), this.txtExtent.getText(), this.rbFile.isSelected(),
            this.cbType.getSelectedItem().equals("DLM"));

      }

      CartagenApplication.getInstance().getFrame().getVisuPanel()
          .zoomToFullExtent();

    }

  }

  public void importInitialDataSet(SourceDLM source, int scale, String txtZone,
      String txtDataset, String filePath, String txtExtent,
      boolean rbFileSelected, boolean dlmSelected) {
    CartagenApplication.getInstance().setCheminDonneesInitial(this.filePath);
    this.setVisible(false);

    this.sourceDlm = source;
    this.setScale(scale);
    CartAGenDocOld.getInstance().setZone(new DataSetZone(txtZone, null));
    this.datasetName = txtDataset;
    this.filePath = filePath;
    if (rbFileSelected) {
      extentFile = true;
      extentClass = txtExtent;
    }
    // create the new CartAGen dataset
    ShapeFileDB database = new ShapeFileDB(this.datasetName);
    database.setSourceDLM(this.sourceDlm);
    database.setSymboScale(this.scale);
    database.setSystemPath(this.filePath);
    database.setDocument(CartAGenDocOld.getInstance());
    CartAGenDataSet dataset = new CartAGenDataSet();
    CartagenApplication.getInstance().getDocument()
        .addDatabase(this.datasetName, database);
    LoadingFrame.cheminAbsolu = this.filePath;
    database.setDataSet(dataset);
    if (dlmSelected) {
      database.setType(new DigitalLandscapeModel());
    } else {
      database.setType(new DigitalCartographicModel());
    }

    SymbolGroup symbGroup = SymbolsUtil.getSymbolGroup(SourceDLM.BD_TOPO_V2,
        scale);
    dataset.setSymbols(SymbolList.getSymbolList(symbGroup));

    CartagenApplication.getInstance().loadData(this.filePath, this.sourceDlm,
        scale, dataset);
    // CartagenApplication.getInstance().loadDat(sourceDlm, scale);

    CartAGenDocOld.getInstance().setInitialDataset(dataset);

    CartagenApplication.getInstance().getInitialLayerGroup()
        .loadLayers(dataset, true);
    CartagenApplication
        .getInstance()
        .getInitialLayerGroup()
        .loadInterfaceWithLayers(
            CartagenApplication.getInstance().getFrame().getLayerManager(),
            CartagenApplication.getInstance().getDocument().getInitialDataset()
                .getSymbols());
  }

  public void importCurrentDataSet(SourceDLM selectedItem, String filePath) {
    this.importCurrentDataSet(selectedItem,
        Integer.parseInt(this.txtScale.getText()), this.txtZone.getText(),
        this.txtDataset.getText(), filePath, this.txtExtent.getText(),
        this.rbFile.isSelected(), this.cbType.getSelectedItem().equals("DLM"),
        false);
  }

  public void importCurrentDataSet(SourceDLM selectedItem, int scale,
      String txtZone, String txtDataset, String filePath, String txtExtent,
      boolean rbFileSelected, boolean dlmSelected, boolean withEnrichement) {
    this.sourceDlm = selectedItem;
    this.setScale(scale);
    CartAGenDocOld.getInstance().setZone(new DataSetZone(txtZone, null));
    this.datasetName = txtDataset;
    this.filePath = filePath;
    if (rbFileSelected) {
      ImportDataFrame.extentFile = true;
      ImportDataFrame.extentClass = txtExtent;
    }
    // create the new CartAGen dataset
    ShapeFileDB database = new ShapeFileDB(this.datasetName);
    database.setSourceDLM(this.sourceDlm);
    database.setSymboScale(this.scale);
    database.setSystemPath(this.filePath);
    database.setDocument(CartAGenDocOld.getInstance());
    CartAGenDataSet dataset = new CartAGenDataSet();
    CartAGenDocOld.getInstance().addDatabase(this.datasetName, database);
    CartAGenDocOld.getInstance().setCurrentDataset(dataset);
    database.setDataSet(dataset);
    if (dlmSelected) {
      database.setType(new DigitalLandscapeModel());
    } else {
      database.setType(new DigitalCartographicModel());
    }

    // launch the import plug-in

    if (this.sourceDlm == SourceDLM.SPECIAL_CARTAGEN) {
      SymbolGroup symbGroup = SymbolsUtil.getSymbolGroup(
          SourceDLM.SPECIAL_CARTAGEN, scale);
      CartAGenDocOld.getInstance().getCurrentDataset()
          .setSymbols(SymbolList.getSymbolList(symbGroup));

      LoadingFrame.cheminAbsolu = this.filePath;
      DataLoadingConfig ccd = new DataLoadingConfig();
      this.filePath = this.filePath.replace("\\", "/");
      ccd.configuration(this.filePath);
      CartagenApplication.getInstance().setCheminDonnees(this.filePath);
      this.setVisible(false);
      LoadingFrame loadingFrame = new LoadingFrame(new File(this.filePath));
      if (loadingFrame.test()) {
        EnrichFrame.getInstance().setVisible(true);
        return;
      }
      loadingFrame.setVisible(true);
    } else {
      LoadingFrame.cheminAbsolu = this.filePath;
      DataLoadingConfig ccd = new DataLoadingConfig();
      this.filePath = this.filePath.replace("\\", "/");
      ccd.configuration(this.filePath);
      CartagenApplication.getInstance().setCheminDonnees(this.filePath);

      this.setVisible(false);
      // loadingFrameMultiBD.setVisible(true);
      if (withEnrichement) {
        EnrichFrame enrichFrame = EnrichFrame.getInstance();
        enrichFrame.setVersion(2);
        enrichFrame.setVisible(true);

        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .loadLayers(
                dataset,
                CartagenApplication.getInstance().getLayerGroup().symbolisationDisplay);
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .loadInterfaceWithLayers(
                CartagenApplication.getInstance().getFrame().getLayerManager(),
                dataset.getSymbols());

      }

    }

    /*
     * 
     * CartagenApplication.getInstance().loadAndEnrichData2(sourceDlm,scale);
     * CartagenApplication.getInstance().initGeneralisation();
     */

  }

  public void setScale(int scale) {
    this.scale = scale;
  }

  public int getScale() {
    return this.scale;
  }

  public SourceDLM getSourceDlm() {
    return this.sourceDlm;
  }

  public void setSourceDlm(SourceDLM sourceDlm) {
    this.sourceDlm = sourceDlm;
  }

}
