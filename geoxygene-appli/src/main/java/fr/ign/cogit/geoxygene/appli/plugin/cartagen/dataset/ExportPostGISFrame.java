/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/**
 * 
 */
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.dataset;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.conversion.I18N;

/**
 * @author gtouya
 * 
 */

public class ExportPostGISFrame extends JFrame implements ActionListener {

  private static final long serialVersionUID = 1L;
  static Logger logger = Logger.getLogger(ExportPostGISFrame.class.getName());
  private List<String> unexportedLayers = new ArrayList<String>();
  private List<Layer> layers;
  private Map<Layer, JCheckBox> mapLayers = new HashMap<Layer, JCheckBox>();
  private Map<Layer, JTextField> nameLayers = new HashMap<Layer, JTextField>();
  private JTextField txtHost, txtPort, txtDb, txtUser, txtPwd, txtSchema;

  public ExportPostGISFrame(ProjectFrame projectFrame) {
    CartAGenDoc.getInstance().getCurrentDataset();
    this.layers = projectFrame.getLayers();
    this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.setResizable(false);
    this.setPreferredSize(new Dimension(600, 300));
    this.setLocation(100, 100);
    this.setTitle(CartAGenPlugin.getInstance().getApplication().getMainFrame()
        .getGui().getTitle()
        + " - export généralisation");
    this.setAlwaysOnTop(true);

    this.getContentPane().setLayout(
        new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

    JPanel layerPanel = new JPanel();
    for (Layer layer : layers) {
      JPanel layerLine = new JPanel();
      JCheckBox box = new JCheckBox(layer.getName());
      JTextField txt = new JTextField(layer.getName());
      txt.setPreferredSize(new Dimension(100, 20));
      txt.setMinimumSize(new Dimension(100, 20));
      txt.setMaximumSize(new Dimension(100, 20));
      mapLayers.put(layer, box);
      nameLayers.put(layer, txt);
      layerLine.add(box);
      layerLine.add(txt);
      layerLine.setLayout(new BoxLayout(layerLine, BoxLayout.X_AXIS));
      layerPanel.add(layerLine);
    }
    layerPanel.setLayout(new BoxLayout(layerPanel, BoxLayout.Y_AXIS));

    // define a panel with the connection information
    JPanel connectionPanel = new JPanel();
    // hôte
    JPanel hostPanel = new JPanel();
    txtHost = new JTextField("localhost");
    txtHost.setPreferredSize(new Dimension(100, 20));
    txtHost.setMinimumSize(new Dimension(100, 20));
    txtHost.setMaximumSize(new Dimension(100, 20));
    hostPanel.add(new JLabel("host : "));
    hostPanel.add(txtHost);
    hostPanel.setLayout(new BoxLayout(hostPanel, BoxLayout.X_AXIS));
    hostPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    // port
    JPanel portPanel = new JPanel();
    txtPort = new JTextField("5432");
    txtPort.setPreferredSize(new Dimension(80, 20));
    txtPort.setMinimumSize(new Dimension(80, 20));
    txtPort.setMaximumSize(new Dimension(80, 20));
    portPanel.add(new JLabel("port : "));
    portPanel.add(txtPort);
    portPanel.setLayout(new BoxLayout(portPanel, BoxLayout.X_AXIS));
    portPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    // database
    JPanel dbPanel = new JPanel();
    txtDb = new JTextField();
    txtDb.setPreferredSize(new Dimension(120, 20));
    txtDb.setMinimumSize(new Dimension(120, 20));
    txtDb.setMaximumSize(new Dimension(120, 20));
    dbPanel.add(new JLabel("database name : "));
    dbPanel.add(txtDb);
    dbPanel.setLayout(new BoxLayout(dbPanel, BoxLayout.X_AXIS));
    dbPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    // user
    JPanel userPanel = new JPanel();
    txtUser = new JTextField();
    txtUser.setPreferredSize(new Dimension(100, 20));
    txtUser.setMinimumSize(new Dimension(100, 20));
    txtUser.setMaximumSize(new Dimension(100, 20));
    userPanel.add(new JLabel("user : "));
    userPanel.add(txtUser);
    userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.X_AXIS));
    userPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    // password
    JPanel pwdPanel = new JPanel();
    txtPwd = new JTextField();
    txtPwd.setPreferredSize(new Dimension(100, 20));
    txtPwd.setMinimumSize(new Dimension(100, 20));
    txtPwd.setMaximumSize(new Dimension(100, 20));
    pwdPanel.add(new JLabel("password : "));
    pwdPanel.add(txtPwd);
    pwdPanel.setLayout(new BoxLayout(pwdPanel, BoxLayout.X_AXIS));
    pwdPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    // password
    JPanel schemaPanel = new JPanel();
    txtSchema = new JTextField("public");
    txtSchema.setPreferredSize(new Dimension(100, 20));
    txtSchema.setMinimumSize(new Dimension(100, 20));
    txtSchema.setMaximumSize(new Dimension(100, 20));
    schemaPanel.add(new JLabel("schema : "));
    schemaPanel.add(txtSchema);
    schemaPanel.setLayout(new BoxLayout(schemaPanel, BoxLayout.X_AXIS));
    schemaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    // button
    JButton connBtn = new JButton("Test Connection");
    connBtn.addActionListener(this);
    connBtn.setActionCommand("test_connect");

    connectionPanel.add(hostPanel);
    connectionPanel.add(Box.createVerticalGlue());
    connectionPanel.add(portPanel);
    connectionPanel.add(Box.createVerticalGlue());
    connectionPanel.add(dbPanel);
    connectionPanel.add(Box.createVerticalGlue());
    connectionPanel.add(userPanel);
    connectionPanel.add(Box.createVerticalGlue());
    connectionPanel.add(pwdPanel);
    connectionPanel.add(Box.createVerticalGlue());
    connectionPanel.add(schemaPanel);
    connectionPanel.add(Box.createVerticalGlue());
    connectionPanel.add(connBtn);
    connectionPanel.add(Box.createVerticalGlue());
    connectionPanel.setLayout(new BoxLayout(connectionPanel, BoxLayout.Y_AXIS));

    // build a horizontal panel for layerPanel et connectionPanel
    JPanel middlePanel = new JPanel();
    middlePanel.add(new JScrollPane(layerPanel));
    middlePanel.add(Box.createHorizontalGlue());
    middlePanel.add(connectionPanel);
    middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.X_AXIS));

    JPanel btnPanel = new JPanel();
    bExport = new JButton("Export");
    bExport.addActionListener(this);
    bExport.setActionCommand("export");
    bExportSelection = new JButton("Export Selected");
    bExportSelection.addActionListener(this);
    bExportSelection.setActionCommand("exportSel");
    bExportTout = new JButton("Export All");
    bExportTout.addActionListener(this);
    bExportTout.setActionCommand("exportAll");
    btnPanel.add(this.bExport);
    btnPanel.add(this.bExportTout);
    btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
    btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    // Visualiser/Editer les tables attributaires
    this.add(middlePanel);
    this.add(btnPanel);

    this.pack();
  }

  /**
	 */
  private JButton bExportTout, bExport, bExportSelection;

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("export")) {
      computeUnexportedLayers();
      exportToPostGIS(false);
      dispose();
    } else if (e.getActionCommand().equals("exportSel")) {
      computeUnexportedLayers();
      exportToPostGIS(true);
      dispose();
    } else if (e.getActionCommand().equals("exportAll")) {
      exportToPostGIS(false);
      dispose();
    } else if (e.getActionCommand().equals("test_connect")) {
      String url = "jdbc:postgresql://" + txtHost.getText() + ":"
          + txtPort.getText() + "/" + txtDb.getText();
      try {
        DriverManager.getConnection(url, txtUser.getText(), txtPwd.getText());
        JOptionPane.showMessageDialog(null, "Connection to " + url
            + " established");
      } catch (SQLException e1) {
        JOptionPane.showMessageDialog(null, "Connection failed");
        e1.printStackTrace();
      }
    }
  }

  /**
   * Export the generalised data of the dataset as shapefiles (one shapefile per
   * scale master line).
   */
  public void exportToPostGIS(boolean selection) {

    // loop on the layers
    for (Layer layer : layers) {
      // one shapefile is exported per layer
      // first get the name of the layer to create the name of the shapefile

      // check if the layer has to be exported
      if (unexportedLayers.contains(layer.getName()))
        continue;

      String tableName = nameLayers.get(layer).getText();
      if (tableName == null || tableName == "") {
        tableName = layer.getName();
      }
      if (logger.isLoggable(Level.FINE)) {
        logger.fine(tableName);
      }

      Class<? extends IGeometry> geomType = null;

      // get the features to export
      Collection<? extends IFeature> iterable = layer.getFeatureCollection();
      System.out.println(iterable.size()); // bon nombre = avec les éliminés
      if (selection)
        iterable = SelectionUtil.getSelectedObjects(CartAGenPlugin
            .getInstance().getApplication(), layer.getName());
      IFeatureCollection<IFeature> features = new FT_FeatureCollection<IFeature>();
      for (IFeature obj : iterable) {
        // set the geometry type
        if (geomType == null) {
          if (obj.getGeom() instanceof ILineString)
            geomType = ILineString.class;
          else if (obj.getGeom() instanceof IPolygon)
            geomType = IPolygon.class;
          else
            geomType = IPoint.class;
        }

        if ((obj instanceof IGeneObj)) {
          features.add(obj);
          continue;
        }

        // @author mdumont
        // on veut exporter même les éléments supprimés, en les taguant
        // eliminated, pour des besoins de généralisation
        // if (!((IGeneObj) obj).isEliminated()) {
        // features.add((IGeneObj) obj);
        // }
      }
      if (features.isEmpty()) {
        continue;
      }

      // write the table
      write(features, geomType, tableName);
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public <Feature extends IFeature> void write(
      IFeatureCollection<IFeature> featurePop,
      Class<? extends IGeometry> geomType, String tableName) {
    if (featurePop == null) {
      return;
    }
    if (featurePop.isEmpty()) {
      return;
    }

    try {

      Map postGISparams = new HashMap();

      postGISparams.put("dbtype", "postgis");
      postGISparams.put("host", txtHost.getText());
      postGISparams.put("port", new Integer(txtPort.getText()));
      postGISparams.put("database", txtDb.getText());
      postGISparams.put("user", txtUser.getText());
      postGISparams.put("passwd", txtPwd.getText());
      if (txtSchema.getText() != null && !txtSchema.getText().equals(""))
        postGISparams.put("schema", txtSchema.getText());

      // storage in PostGIS
      DataStore dataStore = DataStoreFinder.getDataStore(postGISparams);

      if (dataStore == null) {
        return;
      }

      // specify the geometry type
      String specs = "the_geom:"; //$NON-NLS-1$
      specs += AdapterFactory.toJTSGeometryType(geomType).getSimpleName();

      // specify the attributes: there is only one the MRDB link
      specs += "," + "antecedents_liste" + ":" + String.class.getName();
      // add the initial geometry
      specs += "," + "initial_geom" + ":" + String.class.getName();
      // add the eliminated status
      specs += "," + "is_eliminated" + ":" + String.class.getName();
      // add the unique key
      specs += "," + "uKey" + ":" + String.class.getName();
      // the others attributes
      List<String> getters = new ArrayList<String>();
      if (featurePop.size() != 0) {
        Class<?> classObj = featurePop.get(0).getClass();
        Vector<Object> result = addAttributesToHeader(classObj);
        getters = (List<String>) result.get(0);
        specs += result.get(1);
      }

      // create the data schema
      String featureTypeName = tableName;
      SimpleFeatureType type = DataUtilities.createType(featureTypeName, specs);
      dataStore.createSchema(type);
      FeatureStore<SimpleFeatureType, SimpleFeature> featureStore = (FeatureStore<SimpleFeatureType, SimpleFeature>) dataStore
          .getFeatureSource(featureTypeName);
      Transaction t = new DefaultTransaction();
      List<SimpleFeature> collection = new ArrayList<SimpleFeature>();
      int i = 1;
      for (IFeature feature : featurePop) {
        // @author mdumont on exporte aussi les éléments éliminés par la
        // généralisation, avec un attribut is_eliminated
        // if (feature.isDeleted()) {
        // continue;
        // }
        List<Object> liste = new ArrayList<Object>(0);
        // change the CRS if needed
        IGeometry geom = feature.getGeom();
        if ((geom instanceof ILineString) && (geom.coord().size() < 2))
          continue;
        // affect the geometry
        liste.add(AdapterFactory.toGeometry(new GeometryFactory(), geom));
        // affect the antecedent (MRDB link)
        Set<IGeneObj> listAnt = ((IGeneObj) feature).getAntecedents();
        List<String> listAntId = new ArrayList<String>();
        for (IGeneObj ant : listAnt) {
          listAntId.add(ant.getAttribute("uKey").toString());
        }
        liste.add(listAntId.toString());
        // affect the initial geometry
        if (feature instanceof IGeneObj) {
          liste.add(((IGeneObj) feature).getInitialGeom().toString());
        } else {
          liste.add("null");
        }
        // affect the eliminated status :
        liste.add(((IGeneObj) feature).isEliminated());
        // affect the unique key
        liste.add(feature.getAttribute("uKey"));
        // put the attributes in the list, after the geometry
        for (String getter : getters) {
          Method m = feature.getClass().getDeclaredMethod(getter);
          liste.add(m.invoke(feature));
        }
        SimpleFeature simpleFeature = SimpleFeatureBuilder.build(type,
            liste.toArray(), String.valueOf(i++));
        collection.add(simpleFeature);
      }
      featureStore.addFeatures(DataUtilities.collection(collection));
      t.commit();
      t.close();
      dataStore.dispose();
    } catch (MalformedURLException e) {
      logger.log(Level.SEVERE, I18N.getString("ExportPostGISFrame.FileName") //$NON-NLS-1$
          + tableName + I18N.getString("ExportPostGISFrame.Malformed")); //$NON-NLS-1$
      e.printStackTrace();
    } catch (IOException e) {
      logger.log(Level.SEVERE,
          I18N.getString("ExportPostGISFrame.ErrorWritingFile") //$NON-NLS-1$
              + tableName);
      e.printStackTrace();
    } catch (SchemaException e) {
      logger.log(Level.SEVERE,
          I18N.getString("ExportPostGISFrame.SchemeUsedForWritingFile") //$NON-NLS-1$
              + tableName + I18N.getString("ExportPostGISFrame.Incorrect")); //$NON-NLS-1$
      e.printStackTrace();
    } catch (Exception e) {
      logger.log(Level.SEVERE,
          I18N.getString("ExportPostGISFrame.ErrorWritingFile") //$NON-NLS-1$
              + tableName);
      e.printStackTrace();
    }
  }

  private Vector<Object> addAttributesToHeader(Class<?> featureClass) {
    List<String> attrNames = new ArrayList<String>();
    Set<String> acceptedTypes = new HashSet<String>();
    acceptedTypes.add("java.lang.Integer");
    acceptedTypes.add("java.lang.String");
    acceptedTypes.add("java.lang.Double");
    acceptedTypes.add("java.lang.Boolean");
    acceptedTypes.add("int");
    acceptedTypes.add("double");
    acceptedTypes.add("long");
    acceptedTypes.add("boolean");
    String specs = "";
    for (Method m : featureClass.getDeclaredMethods()) {
      if (!m.getName().startsWith("get")) {
        continue;
      }
      if (m.getName().equals("getGeom")) {
        continue;
      }
      if (m.getName().equals("getGeoxObj")) {
        continue;
      }
      if (m.getName().equals("getSymbolId")) {
        continue;
      }
      if (m.getName().equals("getId")) {
        continue;
      }
      if (m.getName().equals("getAttributeMap")) {
        continue;
      }
      if (m.getName().equals("getSymbolExtent")) {
        continue;
      }
      String returnType = m.getReturnType().getName();
      if (!acceptedTypes.contains(returnType)) {
        continue;
      }
      attrNames.add(m.getName());
      String attributeName = m.getName().substring(3, 4).toLowerCase()
          + m.getName().substring(4);
      if (returnType.equals(long.class.getName())) {
        returnType = Integer.class.getName();
      }
      specs += "," + attributeName + ":" + returnType;
    }
    Vector<Object> vect = new Vector<Object>(2);
    vect.add(attrNames);
    vect.add(specs);
    return vect;
  }

  private void computeUnexportedLayers() {
    for (Layer layer : layers) {
      JCheckBox box = mapLayers.get(layer);
      if (!box.isSelected())
        unexportedLayers.add(layer.getName());
    }
  }
}
