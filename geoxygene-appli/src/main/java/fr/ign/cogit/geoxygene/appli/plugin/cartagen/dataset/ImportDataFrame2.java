/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.dataset;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.io.File;
import java.io.IOException;

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
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.dataset.DataSetZone;
import fr.ign.cogit.cartagen.core.dataset.DigitalCartographicModel;
import fr.ign.cogit.cartagen.core.dataset.DigitalLandscapeModel;
import fr.ign.cogit.cartagen.core.dataset.SourceDLM;
import fr.ign.cogit.cartagen.core.dataset.postgis.MappingXMLParser;
import fr.ign.cogit.cartagen.core.dataset.shapefile.ShapeFileDB;
import fr.ign.cogit.cartagen.core.dataset.shapefile.ShapeToLayerMapping;
import fr.ign.cogit.geoxygene.appli.panel.XMLFileFilter;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;

public class ImportDataFrame2 extends JFrame implements ActionListener {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static ImportDataFrame2 importDataFrame = null;

  /**
   * Recuperation de l'instance unique (singleton)
   * @return instance unique (singleton) de EnrichFrame
   */
  public static ImportDataFrame2 getInstance(boolean isInitial,
      CartAGenPlugin plugIn) {
    if (ImportDataFrame2.importDataFrame == null) {
      synchronized (EnrichFrame.class) {
        if (ImportDataFrame2.importDataFrame == null) {
          ImportDataFrame2.importDataFrame = new ImportDataFrame2(plugIn);
        }
      }
    }
    return ImportDataFrame2.importDataFrame;
  }

  private CartAGenPlugin plugIn;

  private SourceDLM sourceDlm;
  private int scale;
  private String datasetName;
  private String filePath;

  private File xmlFile;
  public static String extentClass = null;
  public static boolean extentFile = false;

  private JPanel pMapping;
  private final JComboBox<SourceDLM> cbSourceDlm;
  private final JComboBox<String> cbType;
  private final JTextField txtZone, txtDataset, txtScale, txtPath, txtExtent,
      txtXML;
  private final JButton btnBrowse, btnXML;
  private final JRadioButton rbComputed, rbFile;

  public ImportDataFrame2(CartAGenPlugin plugIn) {
    super("Import Shapefile data into a new dataset");
    System.out.println("Import Shapefile data into a new dataset");
    this.plugIn = plugIn;
    this.setSize(600, 300);
    this.setAlwaysOnTop(true);

    // *********************************
    // THE ZONE PARAMETERS PANEL
    // *********************************
    JPanel zonePanel = new JPanel();
    this.txtZone = new JTextField("Zone test");
    this.txtZone.setPreferredSize(new Dimension(80, 20));
    this.txtZone.setMaximumSize(new Dimension(80, 20));
    this.txtZone.setMinimumSize(new Dimension(80, 20));
    this.cbSourceDlm = new JComboBox<>(SourceDLM.values());
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

    // a panel for the mapping XML file
    pMapping = new JPanel();
    txtXML = new JTextField("xml/mapping_test_data.xml");
    txtXML.setMaximumSize(new Dimension(150, 20));
    txtXML.setMinimumSize(new Dimension(150, 20));
    txtXML.setPreferredSize(new Dimension(150, 20));
    btnXML = new JButton(new ImageIcon(
        this.getClass().getClassLoader().getResource("images/browse.jpeg")));
    btnXML.addActionListener(this);
    btnXML.setActionCommand("browseXML");
    pMapping.add(new JLabel("mapping xml file: "));
    pMapping.add(txtXML);
    pMapping.add(btnXML);
    pMapping.setLayout(new BoxLayout(pMapping, BoxLayout.X_AXIS));

    JPanel zonePanel1 = new JPanel();
    zonePanel1.add(new JLabel("Zone name : "));
    zonePanel1.add(this.txtZone);
    zonePanel1.add(Box.createHorizontalGlue());
    zonePanel1.add(pMapping);
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
    zonePanel.setBorder(
        BorderFactory.createTitledBorder(blackLine, "Description of the zone"));
    zonePanel.setLayout(new BoxLayout(zonePanel, BoxLayout.Y_AXIS));

    // *********************************
    // THE PATH PARAMETERS PANEL
    // *********************************
    JPanel pathPanel = new JPanel();
    this.txtDataset = new JTextField("CartAGen_dataset");
    this.txtDataset.setPreferredSize(new Dimension(90, 20));
    this.txtDataset.setMaximumSize(new Dimension(90, 20));
    this.txtDataset.setMinimumSize(new Dimension(90, 20));
    this.cbType = new JComboBox<>(new String[] { "DLM", "DCM" });
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
    ImageIcon icon = new ImageIcon(ImportDataFrame2.class
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
    this.getContentPane()
        .setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    this.pack();
    System.out.println("frame created");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("Cancel")) {
      this.setVisible(false);
    } else if (e.getActionCommand().equals("Browse")) {

      JFileChooser fc;
      try {
        fc = new JFileChooser(plugIn.getCheminDonnees());
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

    } else if (e.getActionCommand().equals("browseXML")) {
      JFileChooser fc = new JFileChooser();
      fc.setFileFilter(new XMLFileFilter());
      int returnVal = fc.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        xmlFile = fc.getSelectedFile();
        txtXML.setText(xmlFile.getPath());
      }
    } else if (e.getActionCommand().equals("OK")) {

      // check if xmlFile is not nul
      if (txtXML.getText().equals("")) {
        txtXML.setText("xml/mapping_test_data.xml");
      }
      if (xmlFile == null) {
        txtXML.setText("xml/mapping_test_data.xml");
        xmlFile = new File(this.getClass().getClassLoader()
            .getResource(txtXML.getText()).getPath());
      }

      // Creates the mapping by reading the xml file
      MappingXMLParser mappingXMLParser = new MappingXMLParser(xmlFile);
      ShapeToLayerMapping mapping = null;
      try {
        mapping = mappingXMLParser.parseShapeMapping();
      } catch (ParserConfigurationException e1) {
        e1.printStackTrace();
      } catch (SAXException e1) {
        e1.printStackTrace();
      } catch (IOException e1) {
        e1.printStackTrace();
      }

      this.importDataSet((SourceDLM) this.cbSourceDlm.getSelectedItem(),
          Integer.parseInt(this.txtScale.getText()), this.txtZone.getText(),
          this.txtDataset.getText(), this.txtPath.getText(),
          this.txtExtent.getText(), this.rbFile.isSelected(),
          this.cbType.getSelectedItem().equals("DLM"), true, mapping);

      try {
        plugIn.getProjectFrameFromDbName(this.txtDataset.getText())
            .getLayerViewPanel().getViewport().zoomToFullExtent();
      } catch (NoninvertibleTransformException e1) {
        e1.printStackTrace();
      }

    }

  }

  public void importDataSet(SourceDLM source, int scale, String txtZone,
      String txtDataset, String filePath, String txtExtent,
      boolean rbFileSelected, boolean dlmSelected, boolean withEnrichment,
      ShapeToLayerMapping shapefileMapping) {
    plugIn.setCheminDonneesInitial(this.filePath);
    this.setVisible(false);
    this.sourceDlm = source;
    this.setScale(scale);
    CartAGenDoc.getInstance().setZone(new DataSetZone(txtZone, null));
    this.datasetName = txtDataset;
    this.filePath = filePath;
    if (rbFileSelected) {
      extentFile = true;
      extentClass = txtExtent;
    }
    System.out.println(txtExtent + " " + rbFileSelected + " " + dlmSelected);
    // create the new CartAGen dataset
    ShapeFileDB database = new ShapeFileDB(this.datasetName);
    database.setDocument(CartAGenDoc.getInstance());
    database.setSourceDLM(this.sourceDlm);
    database.setSymboScale(this.scale);
    database.setSystemPath(this.filePath);
    database.setGeneObjImpl(CartAGenPlugin.getInstance().getGeneObjImpl());
    CartAGenDataSet dataset = new CartAGenDataSet();
    CartAGenDoc.getInstance().addDatabase(this.datasetName, database);
    LoadingFrame.cheminAbsolu = this.filePath;
    database.setDataSet(dataset);
    if (dlmSelected) {
      database.setType(new DigitalLandscapeModel());
    } else {
      database.setType(new DigitalCartographicModel());
    }

    CartAGenDoc.getInstance().setInitialDataset(dataset);
    CartAGenDoc.getInstance().setCurrentDataset(dataset);
    new CartAGenLoader().loadData(this.filePath, this.sourceDlm, scale, dataset,
        shapefileMapping);

    // CartagenApplication.getInstance().loadDat(sourceDlm, scale);

    // on crée la projectFrame associée à la BD
    try {
      plugIn.addDatabaseToFrame(database);
    } catch (JAXBException e) {
      e.printStackTrace();
    }

    // CartAGenDoc.getInstance().setInitialDataset(dataset);

    if (withEnrichment) {
      // EnrichFrame enrichFrame = EnrichFrame.getInstance();
      // enrichFrame.setDataSet(dataset);
      // enrichFrame.setFactory(new DefaultCreationFactory());
      // enrichFrame.setVisible(true);
    }
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
