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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObjLin;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObjSurf;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.conversion.I18N;

/**
 * @author gtouya
 * 
 */

public class ExportFrame extends JFrame implements ActionListener {

  private String exportDir;
  private static final long serialVersionUID = 1L;
  static Logger logger = Logger.getLogger(ExportFrame.class.getName());
  private List<String> unexportedLayers = new ArrayList<String>();
  private List<Layer> layers;
  private Map<Layer, JCheckBox> mapLayers = new HashMap<Layer, JCheckBox>();

  public ExportFrame(ProjectFrame projectFrame) {
    CartAGenDoc.getInstance().getCurrentDataset();
    this.layers = projectFrame.getLayers();
    this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.setResizable(false);
    this.setSize(new Dimension(400, 300));
    this.setLocation(100, 100);
    this.setTitle(CartagenApplication.getInstance().getFrame().getTitle()
        + " - export généralisation");
    this.setIconImage(CartagenApplication.getInstance().getFrame().getIcon());
    this.setAlwaysOnTop(true);

    this.getContentPane().setLayout(
        new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

    JPanel dirPanel = new JPanel();
    JButton dirButton = new JButton(new ImageIcon(this.getClass().getResource(
        "/images/icons/magnifier.png")));
    dirButton.addActionListener(this);
    dirButton.setActionCommand("directory");
    txtDir = new JTextField();
    txtDir.setPreferredSize(new Dimension(180, 20));
    txtDir.setMaximumSize(new Dimension(180, 20));
    txtDir.setMinimumSize(new Dimension(180, 20));
    dirPanel.add(this.txtDir);
    dirPanel.add(dirButton);
    dirPanel.setLayout(new BoxLayout(dirPanel, BoxLayout.X_AXIS));
    dirPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    JPanel layerPanel = new JPanel();
    for (Layer layer : layers) {
      JCheckBox box = new JCheckBox(layer.getName());
      mapLayers.put(layer, box);
      layerPanel.add(box);
    }
    layerPanel.setLayout(new BoxLayout(layerPanel, BoxLayout.Y_AXIS));

    JPanel btnPanel = new JPanel();
    bExport = new JButton("Export");
    bExport.addActionListener(this);
    bExport.setActionCommand("export");
    bExportTout = new JButton("Export All");
    bExportTout.addActionListener(this);
    bExportTout.setActionCommand("exportAll");
    btnPanel.add(this.bExport);
    btnPanel.add(this.bExportTout);
    btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
    btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    this.add(dirPanel);
    this.add(new JScrollPane(layerPanel));
    this.add(btnPanel);

    this.pack();
  }

  /**
	 */
  private JButton bExportTout, bExport;
  private JTextField txtDir;
  private JLabel lSuivi;

  private boolean chooseDirectory() {

    // File chooser
    JFileChooser choix = new JFileChooser();
    choix.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int retour = choix.showDialog(null, "Select export directory");
    if (retour == JFileChooser.APPROVE_OPTION)
      this.txtDir.setText(choix.getSelectedFile().getAbsolutePath());

    return true;

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("export")) {
      exportDir = txtDir.getText();
      computeUnexportedLayers();
      exportToShapefiles();
      dispose();
    } else if (e.getActionCommand().equals("exportAll")) {
      exportDir = txtDir.getText();
      exportToShapefiles();
      dispose();
    } else if (e.getActionCommand().equals("directory")) {
      chooseDirectory();
    }
  }

  /**
   * Export the generalised data of the dataset as shapefiles (one shapefile per
   * scale master line).
   */
  public void exportToShapefiles() {
    // loop on the layers
    for (Layer layer : layers) {
      // one shapefile is exported per layer
      // first get the name of the layer to create the name of the shapefile

      // check if the layer has to be exported
      if (unexportedLayers.contains(layer.getName()))
        continue;

      String shapeFileName = layer.getName();
      if (logger.isLoggable(Level.FINE)) {
        logger.fine(shapeFileName);
        logger.fine(this.exportDir);
      }

      Class<? extends IGeometry> geomType = null;

      // get the features to export
      IFeatureCollection<IGeneObj> features = new FT_FeatureCollection<IGeneObj>();
      for (IFeature obj : layer.getFeatureCollection()) {
        if (!(obj instanceof IGeneObj))
          continue;
        if (geomType == null) {
          if (obj instanceof IGeneObjLin)
            geomType = ILineString.class;
          else if (obj instanceof IGeneObjSurf)
            geomType = IPolygon.class;
          else
            geomType = IPoint.class;
        }

        if (!((IGeneObj) obj).isEliminated()) {
          features.add((IGeneObj) obj);
        }
      }
      if (features.isEmpty()) {
        continue;
      }

      // write the shapefile
      write(features, geomType, this.exportDir + "\\" + shapeFileName);
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public <Feature extends IFeature> void write(
      IFeatureCollection<IGeneObj> featurePop,
      Class<? extends IGeometry> geomType, String shpName) {
    if (featurePop == null) {
      return;
    }
    if (featurePop.isEmpty()) {
      return;
    }
    String shapefileName = shpName;
    try {
      if (!shapefileName.contains(".shp")) { //$NON-NLS-1$
        shapefileName = shapefileName + ".shp"; //$NON-NLS-1$
      }
      ShapefileDataStore store = new ShapefileDataStore(new File(shapefileName)
          .toURI().toURL());

      // specify the geometry type
      String specs = "geom:"; //$NON-NLS-1$
      specs += AdapterFactory.toJTSGeometryType(geomType).getSimpleName();

      // specify the attributes: there is only one the MRDB link
      specs += "," + "a_pour_antecedant" + ":" + Integer.class.getName();
      List<String> getters = new ArrayList<String>();
      if (featurePop.size() != 0) {
        Class<?> classObj = featurePop.get(0).getClass();
        Vector<Object> result = addAttributesToHeader(classObj);
        getters = (List<String>) result.get(0);
        specs += result.get(1);
      }

      String featureTypeName = shapefileName.substring(
          shapefileName.lastIndexOf("/") + 1, //$NON-NLS-1$
          shapefileName.lastIndexOf(".")); //$NON-NLS-1$
      featureTypeName = featureTypeName.replace('.', '_');
      SimpleFeatureType type = DataUtilities.createType(featureTypeName, specs);
      store.createSchema(type);
      FeatureStore featureStore = (FeatureStore) store
          .getFeatureSource(featureTypeName);
      Transaction t = new DefaultTransaction();
      List<SimpleFeature> collection = new ArrayList<SimpleFeature>();
      int i = 1;
      for (IGeneObj feature : featurePop) {
        if (feature.isDeleted()) {
          continue;
        }
        List<Object> liste = new ArrayList<Object>(0);
        // change the CRS if needed
        IGeometry geom = feature.getGeom();
        if ((geom instanceof ILineString) && (geom.coord().size() < 2))
          continue;

        liste.add(AdapterFactory.toGeometry(new GeometryFactory(), geom));
        liste.add(feature.getId());
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
      store.dispose();
    } catch (MalformedURLException e) {
      logger.log(Level.SEVERE, I18N.getString("ShapefileWriter.FileName") //$NON-NLS-1$
          + shapefileName + I18N.getString("ShapefileWriter.Malformed")); //$NON-NLS-1$
      e.printStackTrace();
    } catch (IOException e) {
      logger.log(Level.SEVERE,
          I18N.getString("ShapefileWriter.ErrorWritingFile") //$NON-NLS-1$
              + shapefileName);
      e.printStackTrace();
    } catch (SchemaException e) {
      logger.log(Level.SEVERE,
          I18N.getString("ShapefileWriter.SchemeUsedForWritingFile") //$NON-NLS-1$
              + shapefileName + I18N.getString("ShapefileWriter.Incorrect")); //$NON-NLS-1$
      e.printStackTrace();
    } catch (Exception e) {
      logger.log(Level.SEVERE,
          I18N.getString("ShapefileWriter.ErrorWritingFile") //$NON-NLS-1$
              + shapefileName);
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
