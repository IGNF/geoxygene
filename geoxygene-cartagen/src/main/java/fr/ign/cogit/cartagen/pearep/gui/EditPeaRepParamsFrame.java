package fr.ign.cogit.cartagen.pearep.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.batik.ext.swing.GridBagConstants;
import org.apache.xerces.dom.DocumentImpl;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterTheme;
import fr.ign.cogit.cartagen.pearep.PeaRepGeneralisation;
import fr.ign.cogit.cartagen.pearep.derivation.DataCorrection;
import fr.ign.cogit.cartagen.pearep.derivation.XMLParser;
import fr.ign.cogit.cartagen.pearep.enrichment.ScaleMasterPreProcess;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.filter.XMLFileFilter;
import fr.ign.cogit.cartagen.util.FileUtil;
import fr.ign.cogit.cartagen.util.XMLUtil;

public class EditPeaRepParamsFrame extends JFrame implements ActionListener {

  /****/
  private static final long serialVersionUID = 1L;

  private JTextField txtExport, txtFolder, txtLayer, txtTheme;
  private JSpinner spinScale;
  private List<DatabaseImport> dbs;
  private List<DataCorrectionInfo> corrections;
  private JList jlistDbs, jlistLayers, jlistThemes, jlistCorrections;
  private JComboBox cbType, cbTypeCorrection, cbPreProcess;
  private List<String> currentLayers, currentThemes;
  private List<String> shapefiles;
  private List<String> availablePreProcesses;

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("save")) {
      JFileChooser fc = new JFileChooser();
      fc.setFileFilter(new XMLFileFilter());
      int returnVal = fc.showSaveDialog(this);
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File file = fc.getSelectedFile();
      try {
        this.saveToXml(file);
      } catch (TransformerException e1) {
        e1.printStackTrace();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      setVisible(false);
    } else if (e.getActionCommand().equals("cancel")) {
      this.setVisible(false);
      this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    } else if (e.getActionCommand().equals("add")) {
      this.dbs.add(new DatabaseImport((SourceDLM) cbType.getSelectedItem(),
          currentLayers, txtFolder.getText()));
      updateDbsList();
      this.currentLayers.clear();
      this.updateLayersList();
    } else if (e.getActionCommand().equals("remove")) {
      this.dbs.remove(this.jlistDbs.getSelectedIndex());
      updateDbsList();
    } else if (e.getActionCommand().equals("remove_layer")) {
      currentLayers.remove(this.jlistLayers.getSelectedValue());
      this.updateLayersList();
    } else if (e.getActionCommand().equals("add_layer")) {
      if (!this.txtLayer.getText().equals(""))
        currentLayers.add(this.txtLayer.getText());
      this.updateLayersList();
    } else if (e.getActionCommand().equals("open")) {
      // load a file previously stored in xml
      JFileChooser fc = new JFileChooser();
      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      int returnVal = fc.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        this.txtFolder.setText(fc.getSelectedFile().getPath());
        this.updateShapefiles();
      }
    } else if (e.getActionCommand().equals("open-export")) {
      // load a file previously stored in xml
      JFileChooser fc = new JFileChooser();
      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      int returnVal = fc.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        this.txtExport.setText(fc.getSelectedFile().getPath());
        this.validate();
      }
    } else if (e.getActionCommand().equals("add_corr")) {
      DataCorrectionInfo info = new DataCorrectionInfo(
          (SourceDLM) cbTypeCorrection.getSelectedItem(), currentThemes,
          (String) cbPreProcess.getSelectedItem());
      this.corrections.add(info);
      this.updateCorrList();
      this.currentThemes.clear();
      this.updateThemesList();
      this.txtTheme.setText("");
      this.validate();
    } else if (e.getActionCommand().equals("remove_corr")) {
      DataCorrectionInfo info = (DataCorrectionInfo) jlistCorrections
          .getSelectedValue();
      this.corrections.remove(info);
      this.updateCorrList();
      this.validate();
    } else if (e.getActionCommand().equals("add_theme")) {
      if (!txtTheme.getText().equals("")) {
        this.currentThemes.add(txtTheme.getText());
        this.updateThemesList();
        this.validate();
      }
    } else if (e.getActionCommand().equals("remove_theme")) {
      String selected = (String) jlistThemes.getSelectedValue();
      this.currentThemes.remove(selected);
      this.updateThemesList();
      this.validate();
    } else if (e.getActionCommand().equals("load")) {
      JFileChooser fc = new JFileChooser();
      fc.setFileFilter(new XMLFileFilter());
      int returnVal = fc.showOpenDialog(this);
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File xmlFile = fc.getSelectedFile();
      updateFrame(xmlFile);
    }
  }

  public EditPeaRepParamsFrame(boolean jar) {
    super();
    this.setTitle(I18N.getString("EditPeaRepParamsFrame.frameTitle"));
    dbs = new ArrayList<EditPeaRepParamsFrame.DatabaseImport>();
    this.currentLayers = new ArrayList<String>();
    this.shapefiles = new ArrayList<String>();
    this.corrections = new ArrayList<EditPeaRepParamsFrame.DataCorrectionInfo>();
    this.availablePreProcesses = new ArrayList<String>();
    this.currentThemes = new ArrayList<String>();
    this.setSize(1200, 700);
    this.setIconImage(new ImageIcon(EditPeaRepParamsFrame.class
        .getClassLoader().getResource("resources/images/icons/logo.jpg"))
        .getImage());

    this.setMinimumSize(new Dimension(1200, 700));
    if (jar)
      try {
        this.initPreProcessesJar();
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }
    else
      this.initPreProcesses();

    // a panel for the buttons
    JButton btnSave = new JButton(I18N.getString("MainLabels.lblSave"));
    btnSave.addActionListener(this);
    btnSave.setActionCommand("save");
    JButton btnLoad = new JButton(I18N.getString("MainLabels.lblLoad"));
    btnLoad.addActionListener(this);
    btnLoad.setActionCommand("load");
    JButton btnCancel = new JButton(I18N.getString("MainLabels.lblCancel"));
    btnCancel.addActionListener(this);
    btnCancel.setActionCommand("cancel");
    JPanel pButtons = new JPanel();
    pButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pButtons.setLayout(new GridBagLayout());
    pButtons.add(btnSave, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstants.WEST, GridBagConstants.NONE, new Insets(2, 2, 2, 2),
        1, 1));
    pButtons.add(btnLoad, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
        GridBagConstants.WEST, GridBagConstants.NONE, new Insets(2, 2, 2, 2),
        1, 1));
    pButtons.add(btnCancel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
        GridBagConstants.WEST, GridBagConstants.NONE, new Insets(2, 2, 2, 2),
        1, 1));

    // a panel for the output scale and the export folder
    JPanel pDefinition = new JPanel();
    spinScale = new JSpinner(new SpinnerNumberModel(100000, 25000, 5000000,
        5000));
    spinScale.setPreferredSize(new Dimension(120, 20));
    spinScale.setMaximumSize(new Dimension(120, 20));
    spinScale.setMinimumSize(new Dimension(120, 20));
    txtExport = new JTextField();
    txtExport.setPreferredSize(new Dimension(200, 20));
    txtExport.setMaximumSize(new Dimension(200, 20));
    txtExport.setMinimumSize(new Dimension(200, 20));
    JButton btnOpenEx = new JButton(new ImageIcon(EditScaleMasterFrame.class
        .getClassLoader().getResource("resources/images/browse.jpeg")));
    btnOpenEx.addActionListener(this);
    btnOpenEx.setActionCommand("open-export");

    pDefinition.setLayout(new GridBagLayout());
    pDefinition.add(new JLabel(I18N.getString("EditPeaRepParamsFrame.scale")),
        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstants.WEST,
            GridBagConstants.NONE, new Insets(2, 2, 2, 2), 1, 1));
    pDefinition.add(spinScale, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
        GridBagConstants.WEST, GridBagConstants.NONE, new Insets(2, 2, 2, 2),
        1, 1));
    pDefinition.add(
        new JLabel(I18N.getString("EditPeaRepParamsFrame.exportFolder")),
        new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstants.WEST,
            GridBagConstants.NONE, new Insets(2, 2, 2, 2), 1, 1));
    pDefinition.add(txtExport, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0,
        GridBagConstants.WEST, GridBagConstants.HORIZONTAL, new Insets(2, 2, 2,
            2), 1, 1));
    pDefinition.add(btnOpenEx, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
        GridBagConstants.WEST, GridBagConstants.NONE, new Insets(2, 2, 2, 2),
        1, 1));

    // a panel for databases import
    JPanel pDatabase = new JPanel();
    jlistDbs = new JList();
    jlistDbs.setPreferredSize(new Dimension(120, 120));
    jlistDbs.setMaximumSize(new Dimension(120, 120));
    jlistDbs.setMinimumSize(new Dimension(120, 120));
    jlistDbs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JButton btnAdd = new JButton(I18N.getString("MainLabels.lblAdd"));
    btnAdd.addActionListener(this);
    btnAdd.setActionCommand("add");
    JButton btnRemove = new JButton(I18N.getString("MainLabels.lblRemove"));
    btnRemove.addActionListener(this);
    btnRemove.setActionCommand("remove");
    txtFolder = new JTextField();
    txtFolder.setPreferredSize(new Dimension(200, 20));
    txtFolder.setMaximumSize(new Dimension(200, 20));
    txtFolder.setMinimumSize(new Dimension(200, 20));
    JButton btnOpen = new JButton(new ImageIcon(EditScaleMasterFrame.class
        .getClassLoader().getResource("resources/images/browse.jpeg")));
    btnOpen.addActionListener(this);
    btnOpen.setActionCommand("open");

    cbType = new JComboBox(new SourceDLM[] { SourceDLM.MGCPPlusPlus,
        SourceDLM.VMAP1PlusPlus });
    cbType.setPreferredSize(new Dimension(120, 20));
    cbType.setMaximumSize(new Dimension(120, 20));
    cbType.setMinimumSize(new Dimension(120, 20));

    JPanel pDb = new JPanel();
    pDb.setLayout(new GridBagLayout());
    pDb.add(cbType, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstants.WEST, GridBagConstants.NONE, new Insets(2, 2, 2, 2),
        1, 1));
    pDb.add(txtFolder, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
        GridBagConstants.WEST, GridBagConstants.HORIZONTAL, new Insets(2, 2, 2,
            2), 1, 1));
    pDb.add(btnOpen, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
        GridBagConstants.WEST, GridBagConstants.NONE, new Insets(2, 2, 2, 2),
        1, 1));

    jlistLayers = new JList();
    jlistLayers.setPreferredSize(new Dimension(80, 320));
    jlistLayers.setMaximumSize(new Dimension(80, 320));
    jlistLayers.setMinimumSize(new Dimension(80, 320));
    jlistLayers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JPanel pLayers = new JPanel();
    JButton btnAddLayer = new JButton(I18N.getString("MainLabels.lblAdd"));
    btnAddLayer.addActionListener(this);
    btnAddLayer.setActionCommand("add_layer");
    JButton btnRemoveLayer = new JButton(I18N.getString("MainLabels.lblRemove"));
    btnRemoveLayer.addActionListener(this);
    btnRemoveLayer.setActionCommand("remove_layer");
    txtLayer = new JTextField();
    txtLayer.setPreferredSize(new Dimension(100, 20));
    txtLayer.setMaximumSize(new Dimension(100, 20));
    txtLayer.setMinimumSize(new Dimension(100, 20));
    AutoCompleteDecorator.decorate(txtLayer, shapefiles, false);
    pLayers.setLayout(new GridBagLayout());
    pLayers.add(txtLayer, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
        GridBagConstants.WEST, GridBagConstants.HORIZONTAL, new Insets(2, 2, 2,
            2), 1, 1));
    pLayers.add(btnAddLayer, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
        GridBagConstants.WEST, GridBagConstants.NONE, new Insets(2, 2, 2, 2),
        1, 1));
    pLayers.add(btnRemoveLayer, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
        GridBagConstants.WEST, GridBagConstants.NONE, new Insets(2, 2, 2, 2),
        1, 1));

    pDatabase.setLayout(new GridBagLayout());
    pDatabase.add(pDb, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstants.WEST, GridBagConstants.HORIZONTAL, new Insets(2, 2, 2,
            2), 1, 1));
    pDatabase.add(pLayers, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
        GridBagConstants.WEST, GridBagConstants.HORIZONTAL, new Insets(2, 2, 2,
            2), 1, 1));
    pDatabase.add(new JScrollPane(jlistLayers), new GridBagConstraints(0, 2, 1,
        1, 1.0, 1.0, GridBagConstants.WEST, GridBagConstants.BOTH, new Insets(
            2, 2, 2, 2), 1, 1));
    pDatabase.add(btnAdd, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
        GridBagConstants.CENTER, GridBagConstants.NONE, new Insets(2, 2, 2, 2),
        1, 1));
    pDatabase.add(new JScrollPane(jlistDbs), new GridBagConstraints(2, 2, 1, 1,
        1.0, 1.0, GridBagConstants.WEST, GridBagConstants.BOTH, new Insets(2,
            2, 2, 2), 1, 1));
    pDatabase.add(btnRemove, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
        GridBagConstants.WEST, GridBagConstants.NONE, new Insets(2, 2, 2, 2),
        1, 1));

    // a panel for data corrections
    cbTypeCorrection = new JComboBox(new SourceDLM[] { SourceDLM.MGCPPlusPlus,
        SourceDLM.VMAP1PlusPlus });
    cbTypeCorrection.setPreferredSize(new Dimension(120, 20));
    cbTypeCorrection.setMaximumSize(new Dimension(120, 20));
    cbTypeCorrection.setMinimumSize(new Dimension(120, 20));
    cbPreProcess = new JComboBox(this.availablePreProcesses.toArray());
    cbPreProcess.setPreferredSize(new Dimension(160, 20));
    cbPreProcess.setMaximumSize(new Dimension(160, 20));
    cbPreProcess.setMinimumSize(new Dimension(160, 20));
    jlistThemes = new JList();
    jlistThemes.setPreferredSize(new Dimension(80, 320));
    jlistThemes.setMaximumSize(new Dimension(80, 320));
    jlistThemes.setMinimumSize(new Dimension(80, 320));
    jlistThemes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jlistCorrections = new JList();
    jlistCorrections.setPreferredSize(new Dimension(120, 120));
    jlistCorrections.setMaximumSize(new Dimension(120, 120));
    jlistCorrections.setMinimumSize(new Dimension(120, 120));
    jlistCorrections.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    txtTheme = new JTextField();
    txtTheme.setPreferredSize(new Dimension(100, 20));
    txtTheme.setMaximumSize(new Dimension(100, 20));
    txtTheme.setMinimumSize(new Dimension(100, 20));
    JButton btnAddCorr = new JButton(
        I18N.getString("EditPeaRepParamsFrame.lblAddPreProcess"));
    btnAddCorr.addActionListener(this);
    btnAddCorr.setActionCommand("add_corr");
    JButton btnRemoveCorr = new JButton(
        I18N.getString("EditPeaRepParamsFrame.lblRemovePreProcess"));
    btnRemoveCorr.addActionListener(this);
    btnRemoveCorr.setActionCommand("remove_corr");
    JPanel pBtnsCorr = new JPanel();
    pBtnsCorr.setLayout(new GridBagLayout());
    pBtnsCorr.add(cbPreProcess, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
        GridBagConstants.WEST, GridBagConstants.HORIZONTAL, new Insets(2, 2, 2,
            2), 1, 1));
    pBtnsCorr.add(cbTypeCorrection, new GridBagConstraints(0, 1, 1, 1, 1.0,
        0.0, GridBagConstants.WEST, GridBagConstants.HORIZONTAL, new Insets(2,
            2, 2, 2), 1, 1));
    pBtnsCorr.add(btnAddCorr, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,
        GridBagConstants.WEST, GridBagConstants.HORIZONTAL, new Insets(2, 2, 2,
            2), 1, 1));

    JButton btnAddTheme = new JButton(
        I18N.getString("EditPeaRepParamsFrame.lblAddTheme"));
    btnAddTheme.addActionListener(this);
    btnAddTheme.setActionCommand("add_theme");
    JButton btnRemoveTheme = new JButton(
        I18N.getString("EditPeaRepParamsFrame.lblRemoveTheme"));
    btnRemoveTheme.addActionListener(this);
    btnRemoveTheme.setActionCommand("remove_theme");

    JPanel pEditCorr = new JPanel();
    pEditCorr.setLayout(new GridBagLayout());
    pEditCorr.add(txtTheme, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
        GridBagConstants.WEST, GridBagConstants.HORIZONTAL, new Insets(2, 2, 2,
            2), 1, 1));
    pEditCorr.add(btnAddTheme, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
        GridBagConstants.WEST, GridBagConstants.NONE, new Insets(2, 2, 2, 2),
        1, 1));
    pEditCorr.add(btnRemoveTheme, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
        GridBagConstants.WEST, GridBagConstants.NONE, new Insets(2, 2, 2, 2),
        1, 1));

    JPanel pDataCorrections = new JPanel();
    pDataCorrections.setBorder(BorderFactory.createTitledBorder(I18N
        .getString("EditPeaRepParamsFrame.titleCorrections")));
    pDataCorrections.setLayout(new GridBagLayout());
    pDataCorrections.add(pEditCorr, new GridBagConstraints(0, 0, 1, 1, 1.0,
        0.0, GridBagConstants.WEST, GridBagConstants.HORIZONTAL, new Insets(2,
            2, 2, 2), 1, 1));
    pDataCorrections.add(new JScrollPane(jlistThemes), new GridBagConstraints(
        0, 1, 1, 1, 1.0, 1.0, GridBagConstants.WEST, GridBagConstants.BOTH,
        new Insets(2, 2, 2, 2), 1, 1));
    pDataCorrections.add(pBtnsCorr, new GridBagConstraints(1, 1, 1, 1, 1.0,
        1.0, GridBagConstants.WEST, GridBagConstants.BOTH, new Insets(2, 2, 2,
            2), 1, 1));
    pDataCorrections.add(new JScrollPane(jlistCorrections),
        new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, GridBagConstants.WEST,
            GridBagConstants.BOTH, new Insets(2, 2, 2, 2), 1, 1));
    pDataCorrections.add(btnRemoveCorr, new GridBagConstraints(3, 1, 1, 1, 0.0,
        0.0, GridBagConstants.WEST, GridBagConstants.NONE, new Insets(2, 2, 2,
            2), 1, 1));

    // frame main setup
    this.getContentPane().setLayout(new GridBagLayout());
    this.getContentPane().add(
        pDefinition,
        new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstants.CENTER,
            GridBagConstants.HORIZONTAL, new Insets(2, 2, 2, 2), 1, 1));
    this.getContentPane().add(
        pDatabase,
        new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstants.CENTER,
            GridBagConstants.BOTH, new Insets(2, 2, 2, 2), 1, 1));
    this.getContentPane().add(
        pDataCorrections,
        new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstants.CENTER,
            GridBagConstants.BOTH, new Insets(2, 2, 2, 2), 1, 1));
    this.getContentPane().add(
        pButtons,
        new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstants.CENTER,
            GridBagConstants.HORIZONTAL, new Insets(2, 2, 2, 2), 1, 1));

    this.validate();
  }

  public EditPeaRepParamsFrame(File file, boolean jar) {
    this(jar);
    updateFrame(file);
  }

  private void updateFrame(File xmlFile) {

    // update the frame fields with the loaded file
    XMLParser xmlParser = new XMLParser(xmlFile);
    try {
      xmlParser.parseParameters(null);
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // first the scale and the export folder
    this.spinScale.setValue(xmlParser.getScale());
    if (xmlParser.getExportFolder() != null)
      this.txtExport.setText(xmlParser.getExportFolder());
    for (String dbType : xmlParser.getMapPath().keySet()) {
      SourceDLM type = SourceDLM.valueOf(dbType);
      String folder = xmlParser.getMapPath().get(dbType);
      List<String> layers = xmlParser.getMapLayers().get(dbType);
      this.dbs.add(new DatabaseImport(type, layers, folder));
    }
    this.updateDbsList();

    // preprocesses
    for (DataCorrection correction : xmlParser.getCorrections()) {
      List<String> themes = new ArrayList<String>();
      for (ScaleMasterTheme theme : correction.getThemes())
        themes.add(theme.getName());
      this.corrections.add(new DataCorrectionInfo(correction.getDbType(),
          themes, correction.getProcess().getPreProcessName()));
      this.updateCorrList();
    }
  }

  private void initPreProcesses() {
    // get the directory of the package of this class
    Package pack = this.getClass().getPackage();
    String name = pack.getName();
    name = name.replace('.', '/');
    if (!name.startsWith("/")) {
      name = "/" + name;
    }
    URL pathName = this.getClass().getResource(name);
    File directory = new File(pathName.getFile());
    // get the parent directories to get fr.ign.cogit.cartagen package
    while (!directory.getName().equals("cartagen")) {
      directory = directory.getParentFile();
    }
    directory = FileUtil.getNamedFileInDir(directory, "pearep");
    Collection<String> excluded = new HashSet<String>();
    List<File> files = FileUtil.getAllFilesInDir(directory, excluded);
    for (File file : files) {
      if (!file.getName().endsWith(".class")) {
        continue;
      }

      String path = file.getPath().substring(file.getPath().indexOf("fr"));
      String classname = FileUtil.changeFileNameToClassName(path);
      try {
        // Try to create an instance of the object
        Class<?> classObj = Class.forName(classname);
        if (classObj.isInterface()) {
          continue;
        }
        if (classObj.isLocalClass()) {
          continue;
        }
        if (classObj.isMemberClass()) {
          continue;
        }
        if (classObj.isEnum()) {
          continue;
        }
        if (Modifier.isAbstract(classObj.getModifiers())) {
          continue;
        }

        // test if it's a pre-process class
        if (ScaleMasterPreProcess.class.isAssignableFrom(classObj)) {
          ScaleMasterPreProcess instance = (ScaleMasterPreProcess) classObj
              .getMethod("getInstance").invoke(null);
          this.availablePreProcesses.add(instance.getPreProcessName());
          continue;
        }

      } catch (ClassNotFoundException cnfex) {
        cnfex.printStackTrace();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      }
    }

    Collections.sort(this.availablePreProcesses);
  }

  private void initPreProcessesJar() throws URISyntaxException {

    String jarPath = PeaRepGeneralisation.class.getProtectionDomain()
        .getCodeSource().getLocation().toURI().getPath().substring(1);
    String jarName = jarPath.substring(jarPath.lastIndexOf("/") + 1);
    try {
      JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName));
      JarEntry jarEntry;
      while (true) {
        jarEntry = jarFile.getNextJarEntry();
        if (jarEntry == null) {
          break;
        }
        if (!(jarEntry.getName().contains("pearep"))) {
          continue;
        }
        if (!(jarEntry.getName().endsWith(".class"))) {
          continue;
        }

        // Try to create an instance of the object
        Class<?> classObj = Class.forName(FileUtil
            .changeFileNameToClassName(jarEntry.getName()));

        if (classObj.isInterface()) {
          continue;
        }
        if (classObj.isLocalClass()) {
          continue;
        }
        if (classObj.isMemberClass()) {
          continue;
        }
        if (classObj.isEnum()) {
          continue;
        }
        if (Modifier.isAbstract(classObj.getModifiers())) {
          continue;
        }

        // test if it's a pre-process class
        if (ScaleMasterPreProcess.class.isAssignableFrom(classObj)) {
          ScaleMasterPreProcess instance = (ScaleMasterPreProcess) classObj
              .getMethod("getInstance").invoke(null);
          this.availablePreProcesses.add(instance.getPreProcessName());
          continue;
        }

      }
      jarFile.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }

    Collections.sort(this.availablePreProcesses);
  }

  private void saveToXml(File file) throws TransformerException, IOException {
    Node n = null;
    // ********************************************
    // CREATION DU DOCUMENT XML
    // Document (Xerces implementation only).
    DocumentImpl xmlDoc = new DocumentImpl();
    // Root element.
    Element root = xmlDoc
        .createElement("PEA-REP-generalisation-DV1-parametres");

    // GENERAL INFORMATION
    Element outScaleElem = xmlDoc.createElement("echelles-sortie");
    root.appendChild(outScaleElem);
    Element scaleElem = xmlDoc.createElement("echelle");
    n = xmlDoc.createTextNode(String.valueOf(this.spinScale.getValue()));
    scaleElem.appendChild(n);
    outScaleElem.appendChild(scaleElem);
    if (!txtExport.getText().equals("")) {
      Element exportElem = xmlDoc.createElement("dossier-export");
      n = xmlDoc.createTextNode(txtExport.getText());
      exportElem.appendChild(n);
      root.appendChild(exportElem);
    }

    // DATABASES TO IMPORT
    Element dbsElem = xmlDoc.createElement("BDs-entree");
    for (DatabaseImport db : this.dbs) {
      Element dbElem = xmlDoc.createElement("BD");
      // name
      Element nomElem = xmlDoc.createElement("nom");
      n = xmlDoc.createTextNode(db.getDbType().name());
      nomElem.appendChild(n);
      dbElem.appendChild(nomElem);
      // path
      Element pathElem = xmlDoc.createElement("chemin");
      n = xmlDoc.createTextNode(db.getFolder());
      pathElem.appendChild(n);
      dbElem.appendChild(pathElem);
      // layer restrictions
      for (String layer : db.getLayers()) {
        Element layerElem = xmlDoc.createElement("layer");
        n = xmlDoc.createTextNode(layer);
        layerElem.appendChild(n);
        dbElem.appendChild(layerElem);
      }
      dbsElem.appendChild(dbElem);
    }
    root.appendChild(dbsElem);

    // DATA CORRECTIONS
    for (DataCorrectionInfo correction : this.corrections) {
      Element correctionElem = xmlDoc.createElement("pre-traitement");
      root.appendChild(correctionElem);
      // name
      Element nomElem = xmlDoc.createElement("nom-processus");
      n = xmlDoc.createTextNode(correction.getProcessName());
      nomElem.appendChild(n);
      correctionElem.appendChild(nomElem);
      // type
      Element dbElem = xmlDoc.createElement("base-de-donnees");
      n = xmlDoc.createTextNode(correction.getDbType().name());
      dbElem.appendChild(n);
      correctionElem.appendChild(dbElem);
      for (String theme : correction.getThemes()) {
        Element themeElem = xmlDoc.createElement("theme");
        n = xmlDoc.createTextNode(theme);
        themeElem.appendChild(n);
        correctionElem.appendChild(themeElem);
      }
    }

    // ECRITURE DU FICHIER
    xmlDoc.appendChild(root);
    XMLUtil.writeDocumentToXml(xmlDoc, file);
  }

  private void updateShapefiles() {
    File folder = new File(txtFolder.getText());
    if (!folder.exists())
      return;
    Set<String> files = new HashSet<String>(FileUtil.getAllFileNamesInDir(
        folder, false, ".shp"));
    this.shapefiles.clear();
    this.shapefiles.addAll(files);
    AutoCompleteDecorator.decorate(txtLayer, shapefiles, false);
  }

  /**
   * Update the content of the {@link JList} jlistDbs according to the list of
   * dbs stored in {@code this}.
   */
  private void updateDbsList() {
    DefaultListModel model = new DefaultListModel();
    for (DatabaseImport db : this.dbs)
      model.addElement(db);
    this.jlistDbs.setModel(model);
    this.validate();
  }

  /**
   * Update the content of the {@link JList} jlistLayers according to the list
   * of current layers stored in {@code this}.
   */
  private void updateLayersList() {
    DefaultListModel model = new DefaultListModel();
    for (String layer : this.currentLayers)
      model.addElement(layer);
    this.jlistLayers.setModel(model);
    this.validate();
  }

  /**
   * Update the content of the {@link JList} jlistCorrections according to the
   * list of corrections stored in {@code this}.
   */
  private void updateCorrList() {
    DefaultListModel model = new DefaultListModel();
    for (DataCorrectionInfo db : this.corrections)
      model.addElement(db);
    this.jlistCorrections.setModel(model);
    this.validate();
  }

  /**
   * Update the content of the {@link JList} jlistThemes according to the list
   * of current themes stored in {@code this}.
   */
  private void updateThemesList() {
    DefaultListModel model = new DefaultListModel();
    for (String theme : this.currentThemes)
      model.addElement(theme);
    this.jlistThemes.setModel(model);
    this.validate();
  }

  class DatabaseImport {
    private SourceDLM dbType;
    private List<String> layers = new ArrayList<String>();
    private String folder;

    public DatabaseImport(SourceDLM dbType, List<String> layers, String folder) {
      super();
      this.dbType = dbType;
      this.layers.addAll(layers);
      this.setFolder(folder);
    }

    public SourceDLM getDbType() {
      return dbType;
    }

    public void setDbType(SourceDLM dbType) {
      this.dbType = dbType;
    }

    public List<String> getLayers() {
      return layers;
    }

    public void setLayers(List<String> layers) {
      this.layers = layers;
    }

    @Override
    public String toString() {
      StringBuffer buff = new StringBuffer(dbType.toString() + " (");
      for (String layer : layers) {
        buff.append(layer);
        buff.append(", ");
      }
      if (!layers.isEmpty())
        buff.delete(buff.length() - 2, buff.length());
      buff.append(")");
      return buff.toString();
    }

    public String getFolder() {
      return folder;
    }

    public void setFolder(String folder) {
      this.folder = folder;
    }
  }

  class DataCorrectionInfo {
    private SourceDLM dbType;
    private List<String> themes = new ArrayList<String>();
    private String processName;

    public DataCorrectionInfo(SourceDLM dbType, List<String> themes,
        String processName) {
      super();
      this.dbType = dbType;
      this.themes.addAll(themes);
      this.setProcessName(processName);
    }

    public SourceDLM getDbType() {
      return dbType;
    }

    public void setDbType(SourceDLM dbType) {
      this.dbType = dbType;
    }

    public List<String> getThemes() {
      return themes;
    }

    public void setThemes(List<String> themes) {
      this.themes = themes;
    }

    @Override
    public String toString() {
      StringBuffer buff = new StringBuffer(processName + " ("
          + dbType.toString());
      for (String layer : themes) {
        buff.append(layer);
        buff.append(", ");
      }
      buff.append(")");
      return buff.toString();
    }

    public String getProcessName() {
      return processName;
    }

    public void setProcessName(String processName) {
      this.processName = processName;
    }

  }
}
