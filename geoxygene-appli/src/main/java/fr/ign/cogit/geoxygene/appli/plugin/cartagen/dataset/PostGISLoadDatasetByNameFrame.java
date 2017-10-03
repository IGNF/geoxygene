package fr.ign.cogit.geoxygene.appli.plugin.cartagen.dataset;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.dataset.GeneObjImplementation;
import fr.ign.cogit.cartagen.core.dataset.postgis.PostGISCartAGenDB;
import fr.ign.cogit.cartagen.core.dataset.postgis.PostGISClass;
import fr.ign.cogit.cartagen.core.dataset.postgis.PostGISLoader;
import fr.ign.cogit.cartagen.core.dataset.postgis.MappingXMLParser;
import fr.ign.cogit.cartagen.core.dataset.postgis.PostGISToLayerMapping;
import fr.ign.cogit.cartagen.core.dataset.postgis.PostgisDB;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.layer.LayerFactory;
import fr.ign.cogit.geoxygene.appli.panel.XMLFileFilter;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.NamedLayer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.UserStyle;

public class PostGISLoadDatasetByNameFrame extends JFrame
    implements ActionListener, ListSelectionListener {

  // *****************************************
  // PROPERTIES
  // *****************************************
  private static final long serialVersionUID = 1L;
  // private Connection conn;
  private JTextField txtDbName, txtHost, txtPort, txtPwd, txtUser, txtSLD,
      txtXML, txtCartagenDbName, txtSchema;
  private JList<String> dList, aList;
  private JButton addDataset, removeDataset, connection, btnOk, btnCancel,
      btnSLD, btnXML;
  private JPanel pConnection, pConnectButton, pDatasets, pButtons, pPanels,
      pScale, pMapping, pNewWin;
  private JScrollPane scrollA, scrollD;
  private List<String> proposList = new ArrayList<String>();
  private PostGISLoader loader;
  private JSpinner spinScale;
  private File sldFile, xmlFile;
  private JComboBox<String> newWindow;
  private JComboBox<Object> existingFrames;

  // *****************************************
  // CONSTRUCTOR
  // *****************************************
  public PostGISLoadDatasetByNameFrame()
      throws HeadlessException, NoSuchMethodException, SecurityException {
    super("Load PostGIS data");
    this.setSize(600, 600);
    this.setAlwaysOnTop(true);
    this.setLocationRelativeTo(null);

    // *****************************************
    // DATABASE CONNECTION
    // *****************************************
    // Panel for the connection information
    pConnection = new JPanel();
    txtHost = new JTextField("localhost");
    txtHost.setMaximumSize(new Dimension(70, 20));
    txtHost.setMinimumSize(new Dimension(70, 20));
    txtHost.setPreferredSize(new Dimension(70, 20));
    txtPort = new JTextField("5432");
    txtPort.setMaximumSize(new Dimension(50, 20));
    txtPort.setMinimumSize(new Dimension(50, 20));
    txtPort.setPreferredSize(new Dimension(50, 20));
    txtUser = new JTextField("postgres");
    txtUser.setMaximumSize(new Dimension(70, 20));
    txtUser.setMinimumSize(new Dimension(70, 20));
    txtUser.setPreferredSize(new Dimension(70, 20));
    txtPwd = new JTextField("postgres");
    txtPwd.setMaximumSize(new Dimension(70, 20));
    txtPwd.setMinimumSize(new Dimension(70, 20));
    txtPwd.setPreferredSize(new Dimension(70, 20));
    txtDbName = new JTextField("database name");
    txtDbName.setMaximumSize(new Dimension(150, 20));
    txtDbName.setMinimumSize(new Dimension(150, 20));
    txtDbName.setPreferredSize(new Dimension(150, 20));
    txtSchema = new JTextField("public");
    txtSchema.setMaximumSize(new Dimension(70, 20));
    txtSchema.setMinimumSize(new Dimension(70, 20));
    txtSchema.setPreferredSize(new Dimension(70, 20));
    pConnection.add(Box.createHorizontalGlue());
    pConnection.add(new JLabel("Host: "));
    pConnection.add(txtHost);
    pConnection.add(Box.createHorizontalGlue());
    pConnection.add(new JLabel("Port: "));
    pConnection.add(txtPort);
    pConnection.add(Box.createHorizontalGlue());
    pConnection.add(new JLabel("Id: "));
    pConnection.add(txtUser);
    pConnection.add(Box.createHorizontalGlue());
    pConnection.add(new JLabel("Pwd: "));
    pConnection.add(txtPwd);
    pConnection.add(Box.createHorizontalGlue());
    pConnection.add(new JLabel("Db: "));
    pConnection.add(txtDbName);
    pConnection.add(Box.createHorizontalGlue());
    pConnection.add(new JLabel("schema: "));
    pConnection.add(txtSchema);
    pConnection.add(Box.createHorizontalGlue());
    pConnection.setLayout(new BoxLayout(pConnection, BoxLayout.X_AXIS));

    // Panel for the Connection Button
    pConnectButton = new JPanel();
    connection = new JButton("Connection");
    connection.addActionListener(this);
    connection.setActionCommand("connection");
    pConnectButton.add(connection);
    pConnectButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pConnectButton.setLayout(new BoxLayout(pConnectButton, BoxLayout.Y_AXIS));

    // a panel for the dataset names
    pDatasets = new JPanel();
    aList = new JList<String>();
    aList.setMaximumSize(new Dimension(160, 190));
    aList.setMinimumSize(new Dimension(160, 190));
    aList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    aList.addListSelectionListener(this);
    scrollA = new JScrollPane(aList);
    dList = new JList<String>();
    dList.setMaximumSize(new Dimension(160, 190));
    dList.setMinimumSize(new Dimension(160, 190));
    dList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    dList.addListSelectionListener(this);
    scrollD = new JScrollPane(dList);
    addDataset = new JButton("    Add    ");
    addDataset.addActionListener(this);
    addDataset.setActionCommand("addDataset");
    removeDataset = new JButton("Remove");
    removeDataset.addActionListener(this);
    removeDataset.setActionCommand("removeDataset");
    removeDataset.setEnabled(false);
    pDatasets.add(Box.createHorizontalGlue());
    pDatasets.add(scrollA);
    pDatasets.add(Box.createHorizontalGlue());
    JPanel pbuttonsD = new JPanel();
    pbuttonsD.add(new JLabel("Choose datasets"));
    pbuttonsD.add(new JLabel("to load:"));
    pbuttonsD.add(Box.createVerticalStrut(60));
    pbuttonsD.add(addDataset);
    pbuttonsD.add(removeDataset);
    pbuttonsD.setLayout(new BoxLayout(pbuttonsD, BoxLayout.Y_AXIS));
    pDatasets.add(pbuttonsD);
    pDatasets.add(Box.createHorizontalGlue());
    pDatasets.add(scrollD);
    pDatasets.add(Box.createHorizontalGlue());
    pDatasets.setLayout(new BoxLayout(pDatasets, BoxLayout.X_AXIS));

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

    // a panel for using a new or existing dataset
    pNewWin = new JPanel();
    String[] items = { "Open in a new window", "Use an existing window:" };
    newWindow = new JComboBox<String>(items);
    newWindow.addActionListener(this);
    newWindow.setActionCommand("changeWin");
    pNewWin.add(newWindow);
    pNewWin.add(Box.createHorizontalGlue());
    Map<String, CartAGenDB> db = CartAGenDoc.getInstance().getDatabases();
    Object[] frames = db.keySet().toArray();
    existingFrames = new JComboBox<Object>(frames);
    existingFrames.setEnabled(false);
    pNewWin.add(existingFrames);
    pNewWin.setLayout(new BoxLayout(pNewWin, BoxLayout.X_AXIS));

    // a panel for the scale spinner and the SLD file
    pScale = new JPanel();
    SpinnerModel model = new SpinnerNumberModel(25000, 1, 10000000, 500);
    spinScale = new JSpinner(model);
    spinScale.setMaximumSize(new Dimension(70, 20));
    spinScale.setMinimumSize(new Dimension(70, 20));
    spinScale.setPreferredSize(new Dimension(70, 20));
    txtSLD = new JTextField("");
    txtSLD.setMaximumSize(new Dimension(150, 20));
    txtSLD.setMinimumSize(new Dimension(150, 20));
    txtSLD.setPreferredSize(new Dimension(150, 20));
    txtCartagenDbName = new JTextField("database name");
    txtCartagenDbName.setMaximumSize(new Dimension(150, 20));
    txtCartagenDbName.setMinimumSize(new Dimension(150, 20));
    txtCartagenDbName.setPreferredSize(new Dimension(150, 20));
    btnSLD = new JButton(new ImageIcon(
        this.getClass().getClassLoader().getResource("images/browse.jpeg")));
    btnSLD.addActionListener(this);
    btnSLD.setActionCommand("browseSLD");
    pScale.add(new JLabel("db name in CartAGen: "));
    pScale.add(txtCartagenDbName);
    pScale.add(Box.createHorizontalGlue());
    pScale.add(new JLabel("symbolisation scale: "));
    pScale.add(spinScale);
    pScale.add(Box.createHorizontalGlue());
    pScale.add(new JLabel("dataset name: "));
    pScale.add(txtCartagenDbName);
    pScale.add(Box.createHorizontalGlue());
    pScale.add(new JLabel("SLD: "));
    pScale.add(txtSLD);
    pScale.add(btnSLD);
    pScale.setLayout(new BoxLayout(pScale, BoxLayout.X_AXIS));

    // ***********************************
    // LOADING LAUNCHMENT
    // ***********************************
    // Panel for the buttons
    pButtons = new JPanel();
    btnOk = new JButton("OK");
    btnOk.addActionListener(this);
    btnOk.setActionCommand("ok");
    btnCancel = new JButton("Cancel");
    btnCancel.addActionListener(this);
    btnCancel.setActionCommand("cancel");
    pButtons.add(btnOk);
    pButtons.add(btnCancel);
    pButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));

    // ***********************************
    // GRAPHIC ORGANISATION
    // ***********************************
    // Global panel
    pPanels = new JPanel();
    pPanels.add(Box.createVerticalStrut(20));
    pPanels.add(pConnection);
    pPanels.add(Box.createVerticalStrut(20));
    pPanels.add(pConnectButton);
    pPanels.add(Box.createVerticalStrut(20));
    pPanels.add(pDatasets);
    pPanels.add(Box.createVerticalStrut(20));
    pPanels.add(pMapping);
    pPanels.add(Box.createVerticalStrut(20));
    pPanels.add(pNewWin);
    pPanels.add(Box.createVerticalStrut(20));
    pPanels.add(pScale);
    pPanels.add(Box.createVerticalStrut(20));
    pPanels.add(pButtons);
    pPanels.setLayout(new BoxLayout(pPanels, BoxLayout.Y_AXIS));

    // Layout of the frame
    this.getContentPane().add(pPanels);
    this.getContentPane()
        .setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    this.pack();
  }

  // **************************************
  // BUTTONS ACTIONS
  // **************************************

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("cancel")) {
      // ******** Close the loading frame
      this.dispose();
    } else if (e.getActionCommand().equals("connection")) {
      // ******** Load the database tables and list them in aList
      proposList.clear();
      loader = new PostGISLoader(txtHost.getText(), txtPort.getText(),
          txtDbName.getText(), txtUser.getText(), txtPwd.getText(),
          txtSchema.getText());
      proposList = loader.showTables();
      // alphabetic order
      java.util.Collections.sort(proposList);
      // Add the available datasets to the aList panel
      DefaultListModel<String> model = new DefaultListModel<String>();
      for (String propos : proposList)
        model.addElement(propos);
      aList.setModel(model);
    } else if (e.getActionCommand().equals("addDataset")) {
      // ******** Add the selected datasets from aList to dList
      // Get the selected items
      List<String> selList = aList.getSelectedValuesList();
      // Get the current dList items
      if (!selList.isEmpty()) {
        ListModel<String> oldModel = dList.getModel();
        Integer indexD = oldModel.getSize();
        // Add the current dList items to the selList
        for (Integer i = 0; i < indexD; i++) {
          if (!selList.contains(oldModel.getElementAt(i))) {
            selList.add(oldModel.getElementAt(i));
          }
        }
        // Create the new model
        java.util.Collections.sort(selList);
        DefaultListModel<String> newModel = new DefaultListModel<String>();
        for (String selValue : selList) {
          newModel.addElement(selValue);
        }
        // Update the dList model
        dList.setModel(newModel);
      } else
        System.out.print("Selection is empty");
    } else if (e.getActionCommand().equals("removeDataset")) {
      // ********* Remove the selected items from dList
      if (!dList.isSelectionEmpty()) {
        // Get the dList model and the selected items
        ListModel<String> oldModel = dList.getModel();
        List<String> selList = dList.getSelectedValuesList();
        // Create the new model, without the selected items
        DefaultListModel<String> newModel = new DefaultListModel<String>();
        for (Integer i = 0; i < oldModel.getSize(); i++) {
          if (!selList.contains(oldModel.getElementAt(i))) {
            newModel.addElement(oldModel.getElementAt(i));
          }
        }
        // Update the dList model
        dList.setModel(newModel);
      }
    } else if (e.getActionCommand().equals("browseSLD")) {
      JFileChooser fc = new JFileChooser();
      fc.setFileFilter(new XMLFileFilter());
      int returnVal = fc.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        sldFile = fc.getSelectedFile();
        txtSLD.setText(sldFile.getPath());
      }
    } else if (e.getActionCommand().equals("browseXML")) {
      JFileChooser fc = new JFileChooser();
      fc.setFileFilter(new XMLFileFilter());
      int returnVal = fc.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        xmlFile = fc.getSelectedFile();
        txtXML.setText(xmlFile.getPath());
      }
    } else if (e.getActionCommand().equals("changeWin")) {
      if (newWindow.getSelectedIndex() == 1) {
        existingFrames.setEnabled(true);
        spinScale.setEnabled(false);
        txtCartagenDbName.setEnabled(false);
      } else {
        existingFrames.setEnabled(false);
        spinScale.setEnabled(true);
        txtCartagenDbName.setEnabled(true);
      }
    } else if (e.getActionCommand().equals("ok")) {
      // ******** Load the datasets chosen in dList
      System.out.println("Loading data...");
      CartAGenDoc doc = CartAGenDoc.getInstance();
      // If needed, creates a CartAGen document
      if (doc.getName() == null) {
        doc.setName(txtCartagenDbName.getText());
        doc.setPostGisDb(PostgisDB.get(txtCartagenDbName.getText(), true));
      }
      // Get selected layers
      ListModel<String> selection = dList.getModel();
      Integer indexS = selection.getSize();
      List<String> layersList = new ArrayList<String>();
      // Add the current dList items to the layersList
      for (Integer i = 0; i < indexS; i++) {
        layersList.add(selection.getElementAt(i));
      }
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
      MappingXMLParser calacMappingXMLParser = new MappingXMLParser(
          xmlFile);
      PostGISToLayerMapping mapping = null;
      try {
        mapping = calacMappingXMLParser.parsePostGISMapping();
      } catch (ParserConfigurationException e1) {
        e1.printStackTrace();
      } catch (SAXException e1) {
        e1.printStackTrace();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      loader.setMapping(mapping);
      // check the SLDFile
      if (sldFile == null) {
        sldFile = new File(txtSLD.getText());
      }
      Legend.setSYMBOLISATI0N_SCALE(
          ((Integer) spinScale.getValue()).doubleValue());
      CartAGenDataSet dataset;
      // ******* Open in a new window? *******//
      // in an existing window
      if (newWindow.getSelectedIndex() == 1) {
        // get the selected database name
        String dbName = existingFrames.getSelectedItem().toString();
        // get the corresponding dataset
        PostGISCartAGenDB database = (PostGISCartAGenDB) doc.getDataset(dbName)
            .getCartAGenDB();
        doc.setCurrentDataset(database.getDataSet());
        database.setLoader(loader);
        dataset = database.getDataSet();
        GeneObjImplementation implementation = mapping
            .getGeneObjImplementation();
        database.setGeneObjImpl(implementation);
        try {
          for (String layerName : layersList) {
            PostGISClass geoClass = new PostGISClass(layerName,
                dataset.getFeatureTypeFromPopName(layerName),
                dataset.getGeometryTypeFromName(layerName));
            database.overwrite(geoClass);
          }
          // apply sld
          applySldToLoadedData(database, sldFile);
        } catch (FileNotFoundException | JAXBException e1) {
          e1.printStackTrace();
        }
      }
      // in a new window
      else {
        // check the SLDFile
        if (sldFile == null && !txtSLD.getText().equals("")) {
          sldFile = new File(txtSLD.getText());
        }
        Legend.setSYMBOLISATI0N_SCALE(
            ((Integer) spinScale.getValue()).doubleValue());
        // Creates a new database/dataset
        PostGISCartAGenDB database = new PostGISCartAGenDB(
            txtCartagenDbName.getText());
        database.setDocument(CartAGenDoc.getInstance());
        database.setSymboScale((Integer) spinScale.getValue());
        database.setGeneObjImpl(mapping.getGeneObjImplementation());
        dataset = new CartAGenDataSet();
        doc.addDatabase(txtCartagenDbName.getText(), database);
        database.setDataSet(dataset);
        doc.setCurrentDataset(dataset);
        database.setLoader(loader);
        for (String layer : layersList) {
          System.out.println("\nJe charge : " + layer);
          loader.loadData(dataset, layer, true);
        }
        if (sldFile == null || txtSLD.getText().equals(""))
          try {
            CartAGenPlugin.getInstance().addDatabaseToFrame(database);
          } catch (JAXBException e1) {
            e1.printStackTrace();
          }
        else {
          StyledLayerDescriptor newSld;
          try {
            newSld = StyledLayerDescriptor.unmarshall(sldFile.getPath());
            CartAGenPlugin.getInstance().addDatabaseToFrame(database, newSld);
          } catch (FileNotFoundException | JAXBException e1) {
            e1.printStackTrace();
          }
        }
      }
      System.out.println(CartAGenDoc.getInstance().getCurrentDataset());
      System.out.println(CartAGenDoc.getInstance().getDatabases());
      this.dispose();
    }
  }

  public void valueChanged(ListSelectionEvent e) {
    if (dList.isSelectionEmpty())
      removeDataset.setEnabled(false);
    else
      removeDataset.setEnabled(true);
  }

  private void applySldToLoadedData(PostGISCartAGenDB database, File sldFile)
      throws FileNotFoundException, JAXBException {
    // -----> get the undisplayed pops
    List<IPopulation<? extends IFeature>> pops = database.getDataSet()
        .getPopulations();
    for (int i = 0; i < pops.size(); i++) {
      IPopulation<? extends IFeature> pop = pops.get(i);
      // if pop is Geometry Pool
      if (pop.getNom().equals("Geometry Pool"))
        continue;
      // add the new layer to the ProjectFrame
      NamedLayer layer;
      ProjectFrame pFrame = CartAGenPlugin.getInstance()
          .getProjectFrameFromDbName(database.getName());
      if (pFrame.getLayer(pop.getNom()) != null) {
        // layer is already displayed
        continue;
      } else {
        // if SLD does not contain a layer for this pop
        if (database.getDataSet().getSld().getLayer(pop.getNom()) == null) {
          // create a new style
          float opacity = 0.8f;
          float strokeWidth = 1.0f;
          Color fillColor = new Color((float) Math.random(),
              (float) Math.random(), (float) Math.random());
          layer = new NamedLayer(database.getDataSet().getSld(), pop.getNom());
          UserStyle style = new UserStyle();
          style.setName("Style créé pour le layer " + pop.getNom());//$NON-NLS-1$
          FeatureTypeStyle fts = new FeatureTypeStyle();
          fts.getRules().add(LayerFactory.createRule(
              database.getDataSet().getGeometryTypeFromName(pop.getNom()),
              fillColor.darker(), fillColor, opacity, opacity, strokeWidth));
          style.getFeatureTypeStyles().add(fts);
          layer.getStyles().add(style);
          // add the new layer to the sld
          database.getDataSet().getSld().add(layer);
        } else {
          layer = (NamedLayer) database.getDataSet().getSld()
              .getLayer(pop.getNom());
          layer.setSld(database.getDataSet().getSld());
        }
        // add the new layer to the projectframe
        pFrame.addLayer(layer);
      }
    }
    // ----> if a SLD file is given
    if (sldFile != null) {
      // create a sld from the given sldfile
      StyledLayerDescriptor newSld = StyledLayerDescriptor
          .unmarshall(sldFile.getPath());
      // affect this sld to the dataset
      database.getDataSet().setSld(newSld);
      database.getDataSet().getSld().setDataSet(database.getDataSet());
    }
  }
}
