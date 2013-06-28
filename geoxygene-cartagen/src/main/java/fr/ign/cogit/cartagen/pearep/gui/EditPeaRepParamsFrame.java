package fr.ign.cogit.cartagen.pearep.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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

import org.apache.xerces.dom.DocumentImpl;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.pearep.derivation.XMLParser;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.filter.XMLFileFilter;
import fr.ign.cogit.cartagen.util.FileUtil;
import fr.ign.cogit.cartagen.util.XMLUtil;

public class EditPeaRepParamsFrame extends JFrame implements ActionListener {

  /****/
  private static final long serialVersionUID = 1L;

  private JTextField txtExport, txtFolder, txtLayer;
  private JSpinner spinScale;
  private List<DatabaseImport> dbs;
  private JList jlistDbs, jlistLayers;
  private JComboBox cbType;
  private List<String> currentLayers;
  private List<String> shapefiles;

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
    } else if (e.getActionCommand().equals("cancel"))
      this.setVisible(false);
    else if (e.getActionCommand().equals("add")) {
      this.dbs.add(new DatabaseImport((SourceDLM) cbType.getSelectedItem(),
          currentLayers, txtFolder.getText()));
      updateDbsList();
    } else if (e.getActionCommand().equals("remove")) {
      this.dbs.remove(this.jlistDbs.getSelectedIndex());
      updateDbsList();
    } else if (e.getActionCommand().equals("remove_layer")) {
      currentLayers.remove(this.jlistLayers.getSelectedValue());
      this.updateLayersList();
    } else if (e.getActionCommand().equals("add_layer")) {
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
        this.pack();
      }
    }
  }

  public EditPeaRepParamsFrame() {
    super();
    this.setTitle(I18N.getString("EditPeaRepParamsFrame.frameTitle"));
    dbs = new ArrayList<EditPeaRepParamsFrame.DatabaseImport>();
    this.currentLayers = new ArrayList<String>();
    this.shapefiles = new ArrayList<String>();

    // a panel for the buttons
    JPanel pButtons = new JPanel();
    JButton btnSave = new JButton(I18N.getString("MainLabels.lblSave"));
    btnSave.addActionListener(this);
    btnSave.setActionCommand("save");
    JButton btnCancel = new JButton(I18N.getString("MainLabels.lblCancel"));
    btnCancel.addActionListener(this);
    btnCancel.setActionCommand("cancel");
    pButtons.add(btnSave);
    pButtons.add(btnCancel);
    pButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));

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
        .getClassLoader().getResource("images/browse.jpeg")));
    btnOpenEx.addActionListener(this);
    btnOpenEx.setActionCommand("open-export");
    pDefinition.add(Box.createHorizontalGlue());
    pDefinition.add(new JLabel(I18N.getString("EditPeaRepParamsFrame.scale")));
    pDefinition.add(spinScale);
    pDefinition.add(Box.createHorizontalGlue());
    pDefinition.add(new JLabel(I18N
        .getString("EditPeaRepParamsFrame.exportFolder")));
    pDefinition.add(txtExport);
    pDefinition.add(btnOpenEx);
    pDefinition.add(Box.createHorizontalGlue());
    pDefinition.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pDefinition.setLayout(new BoxLayout(pDefinition, BoxLayout.X_AXIS));

    // a panel for databases import
    JPanel pDatabase = new JPanel();
    jlistDbs = new JList();
    jlistDbs.setPreferredSize(new Dimension(120, 120));
    jlistDbs.setMaximumSize(new Dimension(120, 120));
    jlistDbs.setMinimumSize(new Dimension(120, 120));
    jlistDbs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JPanel pAddRemove = new JPanel();
    JButton btnAdd = new JButton(I18N.getString("MainLabels.lblAdd"));
    btnAdd.addActionListener(this);
    btnAdd.setActionCommand("add");
    JButton btnRemove = new JButton(I18N.getString("MainLabels.lblRemove"));
    btnRemove.addActionListener(this);
    btnRemove.setActionCommand("remove");
    pAddRemove.add(btnAdd);
    pAddRemove.add(btnRemove);
    pAddRemove.setLayout(new BoxLayout(pAddRemove, BoxLayout.Y_AXIS));
    JPanel pDb = new JPanel();
    JPanel pFolder = new JPanel();
    txtFolder = new JTextField();
    txtFolder.setPreferredSize(new Dimension(200, 20));
    txtFolder.setMaximumSize(new Dimension(200, 20));
    txtFolder.setMinimumSize(new Dimension(200, 20));
    JButton btnOpen = new JButton(new ImageIcon(EditScaleMasterFrame.class
        .getClassLoader().getResource("images/browse.jpeg")));
    btnOpen.addActionListener(this);
    btnOpen.setActionCommand("open");
    pFolder.add(txtFolder);
    pFolder.add(btnOpen);
    pFolder.setLayout(new BoxLayout(pFolder, BoxLayout.X_AXIS));
    cbType = new JComboBox(new SourceDLM[] { SourceDLM.MGCPPlusPlus,
        SourceDLM.VMAP1PlusPlus });
    cbType.setPreferredSize(new Dimension(120, 20));
    cbType.setMaximumSize(new Dimension(120, 20));
    cbType.setMinimumSize(new Dimension(120, 20));
    pDb.add(cbType);
    pDb.add(pFolder);
    pDb.setLayout(new BoxLayout(pDb, BoxLayout.Y_AXIS));
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
    pLayers.add(txtLayer);
    pLayers.add(btnAddLayer);
    pLayers.add(btnRemoveLayer);
    pLayers.setLayout(new BoxLayout(pLayers, BoxLayout.Y_AXIS));

    pDatabase.add(jlistDbs);
    pDatabase.add(Box.createHorizontalGlue());
    pDatabase.add(pAddRemove);
    pDatabase.add(Box.createHorizontalGlue());
    pDatabase.add(pDb);
    pDatabase.add(Box.createHorizontalGlue());
    pDatabase.add(new JScrollPane(jlistLayers));
    pDatabase.add(Box.createHorizontalGlue());
    pDatabase.add(pLayers);
    pDatabase.setLayout(new BoxLayout(pDatabase, BoxLayout.X_AXIS));

    // frame main setup
    this.getContentPane().add(Box.createVerticalGlue());
    this.getContentPane().add(pDefinition);
    this.getContentPane().add(Box.createVerticalGlue());
    this.getContentPane().add(pDatabase);
    this.getContentPane().add(Box.createVerticalGlue());
    this.getContentPane().add(pButtons);
    this.getContentPane().setLayout(
        new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    this.pack();
  }

  public EditPeaRepParamsFrame(File file) {
    this();

    // update the frame fields with the loaded file
    XMLParser xmlParser = new XMLParser(file);
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
    this.pack();
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
    this.pack();
  }

  class DatabaseImport {
    private SourceDLM dbType;
    private List<String> layers;
    private String folder;

    public DatabaseImport(SourceDLM dbType, List<String> layers, String folder) {
      super();
      this.dbType = dbType;
      this.layers = layers;
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
}
