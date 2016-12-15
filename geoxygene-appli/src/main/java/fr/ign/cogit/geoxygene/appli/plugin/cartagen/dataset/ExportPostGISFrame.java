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
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
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
import org.geotools.data.Transaction;
import org.geotools.data.store.ContentFeatureStore;
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
        this.setTitle(
                CartAGenPlugin.getInstance().getApplication().getMainFrame()
                        .getGui().getTitle() + " - export généralisation");
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
        connectionPanel
                .setLayout(new BoxLayout(connectionPanel, BoxLayout.Y_AXIS));

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
        bExportSelection = new JButton("Export Selection");
        bExportSelection.addActionListener(this);
        bExportSelection.setActionCommand("exportSel");
        bExportTout = new JButton("Export All");
        bExportTout.addActionListener(this);
        bExportTout.setActionCommand("exportAll");
        btnPanel.add(this.bExport);
        btnPanel.add(this.bExportSelection);
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
                DriverManager.getConnection(url, txtUser.getText(),
                        txtPwd.getText());
                JOptionPane.showMessageDialog(null,
                        "Connection to " + url + " established");
            } catch (SQLException e1) {
                JOptionPane.showMessageDialog(null, "Connection failed");
                e1.printStackTrace();
            }
        }
    }

    /**
     * Export the generalised data of the dataset as shapefiles (one shapefile
     * per scale master line).
     */
    public void exportToPostGIS(boolean selection) {

        // loop on the layers
        for (Layer layer : layers) {
            // one table is exported per layer
            // first get the name of the layer to create the name of the
            // shapefile

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
            Collection<? extends IFeature> iterable = layer
                    .getFeatureCollection();
            if (selection)
                iterable = SelectionUtil.getSelectedObjects(
                        CartAGenPlugin.getInstance().getApplication(),
                        layer.getName());
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
            }
            if (features.isEmpty()) {
                continue;
            }

            // write the table
            write(layer.getName(), features, geomType, tableName);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <Feature extends IFeature> void write(String layerName,
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
            String specs = "geom:"; //$NON-NLS-1$
            specs += AdapterFactory.toJTSGeometryType(geomType).getSimpleName();

            // get the xml attribute mapping
            File xmlFile = new File(this.getClass().getClassLoader()
                    .getResource("xml/mapping_export_postgis.xml").getPath());
            ExportPostGISMappingXMLParser mappingXMLParser = new ExportPostGISMappingXMLParser(
                    xmlFile);
            Hashtable<String, ArrayList<ArrayList<String>>> mappingPops = mappingXMLParser
                    .parsePostGISMapping();
            // get the current pop mapping
            ArrayList<ArrayList<String>> mappingCurrentPop = mappingPops
                    .get(layerName);
            if (mappingCurrentPop != null) {
                for (int i = 0; i < mappingCurrentPop.size(); i++) {
                    // get each attribute
                    ArrayList<String> mapping = mappingCurrentPop.get(i);
                    // get postgis attr
                    String postgisAttr = mapping.get(0);
                    // get type
                    String attrType = mapping.get(2);
                    specs += "," + postgisAttr + ":" + attrType;
                }
            }

            // create the data schema
            String featureTypeName = tableName;
            SimpleFeatureType type = DataUtilities.createType(featureTypeName,
                    specs);
            dataStore.createSchema(type);
            ContentFeatureStore featureStore = (ContentFeatureStore) dataStore
                    .getFeatureSource(featureTypeName);
            Transaction t = new DefaultTransaction();
            Collection features = new HashSet<>();
            int i = 1;
            for (IFeature feature : featurePop) {
                List<Object> liste = new ArrayList<Object>(0);
                // change the CRS if needed
                IGeometry geom = feature.getGeom();
                if ((geom instanceof ILineString) && (geom.coord().size() < 2))
                    continue;
                // affect the geometry
                liste.add(
                        AdapterFactory.toGeometry(new GeometryFactory(), geom));
                // affect the other attributes
                for (int j = 0; j < mappingCurrentPop.size(); j++) {
                    // get each attribute
                    ArrayList<String> mapping = mappingCurrentPop.get(j);
                    // get java attr
                    String javaAttr = mapping.get(1);
                    // get type
                    String getter = "get"
                            + javaAttr.substring(0, 1).toUpperCase()
                            + javaAttr.substring(1);
                    try {
                        Method m = feature.getClass().getMethod(getter);
                        String attrType = mapping.get(2);
                        if (m.invoke(feature) == null)
                            liste.add(null);
                        else {
                            if (!m.invoke(feature).getClass().getName()
                                    .equals(attrType))
                                liste.add(m.invoke(feature).toString());
                            else
                                liste.add(m.invoke(feature));
                        }
                    } catch (NoSuchMethodException e) {
                        String getterbis = "is"
                                + javaAttr.substring(0, 1).toUpperCase()
                                + javaAttr.substring(1);
                        try {
                            Method mbis = feature.getClass()
                                    .getMethod(getterbis);
                            String attrType = mapping.get(2);
                            if (mbis.invoke(feature) == null)
                                liste.add(null);
                            else {
                                if (!mbis.invoke(feature).getClass().getName()
                                        .equals(attrType))
                                    liste.add(mbis.invoke(feature).toString());
                                else
                                    liste.add(mbis.invoke(feature));
                            }
                        } catch (NoSuchMethodException ebis) {
                            try {
                                Method mter = feature.getClass()
                                        .getMethod(javaAttr);
                                liste.add(mter.invoke(feature));
                            } catch (NoSuchMethodException eter) {
                                logger.log(Level.SEVERE,
                                        "this method does not exist : "
                                                + mapping.get(1)
                                                + "on the layer : "
                                                + layerName);
                                // skip this attribute
                                liste.add(null);
                            }
                        }
                    }
                }
                SimpleFeature simpleFeature = SimpleFeatureBuilder.build(type,
                        liste.toArray(), String.valueOf(i));
                features.add(simpleFeature);
                i++;
            }
            featureStore.addFeatures(features);
            t.commit();
            t.close();
            dataStore.dispose();
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE,
                    I18N.getString("ExportPostGISFrame.FileName") //$NON-NLS-1$
                            + tableName
                            + I18N.getString("ExportPostGISFrame.Malformed")); //$NON-NLS-1$
            e.printStackTrace();
        } catch (IOException e) {
            logger.log(Level.SEVERE,
                    I18N.getString("ExportPostGISFrame.ErrorWritingFile") //$NON-NLS-1$
                            + tableName);
            e.printStackTrace();
        } catch (SchemaException e) {
            logger.log(Level.SEVERE,
                    I18N.getString(
                            "ExportPostGISFrame.SchemeUsedForWritingFile") //$NON-NLS-1$
                            + tableName
                            + I18N.getString("ExportPostGISFrame.Incorrect")); //$NON-NLS-1$
            e.printStackTrace();
        } catch (Exception e) {
            logger.log(Level.SEVERE,
                    I18N.getString("ExportPostGISFrame.ErrorWritingFile") //$NON-NLS-1$
                            + tableName);
            e.printStackTrace();
        }
    }

    private void computeUnexportedLayers() {
        for (Layer layer : layers) {
            JCheckBox box = mapLayers.get(layer);
            if (!box.isSelected())
                unexportedLayers.add(layer.getName());
        }
    }
}
