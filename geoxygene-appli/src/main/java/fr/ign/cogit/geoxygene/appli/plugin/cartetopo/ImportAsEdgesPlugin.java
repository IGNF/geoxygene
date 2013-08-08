package fr.ign.cogit.geoxygene.appli.plugin.cartetopo;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Chargeur;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;

/**
 * 
 * 
 * @author Bertrand Dumenieu
 */
public class ImportAsEdgesPlugin implements GeOxygeneApplicationPlugin, ActionListener {
  
  /** Classic logger. */
  private static Logger LOGGER = Logger.getLogger(ImportAsEdgesPlugin.class.getName());
  
  /** GeOxygeneApplication. */
  private GeOxygeneApplication application;
  
  /**
   * Initialize the plugin.
   * @param application the application
   */
  @Override
  public final void initialize(final GeOxygeneApplication application) {
    
    this.application = application;
    
    // Check if the DataMatching menu exists. If not we create it.
    JMenu menu = null;
    String menuName = I18N.getString("CarteTopoPlugin.CarteTopoPlugin"); //$NON-NLS-1$
    for (Component c : application.getFrame().getJMenuBar().getComponents()) {
      if (c instanceof JMenu) {
        JMenu aMenu = (JMenu) c;
        if (aMenu.getText() != null
            && aMenu.getText().equalsIgnoreCase(menuName)) {
          menu = aMenu;
        }
      }
    }
    if (menu == null) {
      menu = new JMenu(menuName);
    }
    
    // Add network data matching menu item to the menu.
    JMenuItem menuItem = new JMenuItem(I18N.getString("CarteTopoPlugin.ImportAsEdges")); //$NON-NLS-1$
    menuItem.addActionListener(this);
    menu.add(menuItem);
    
    // Refresh menu of the application
    application
        .getFrame()
        .getJMenuBar()
        .add(menu, application.getFrame().getJMenuBar().getComponentCount() - 2);
    
  }
  
  /**
   * 
   */
  @Override
  public void actionPerformed(final ActionEvent e) {
      
    ProjectFrame project = ImportAsEdgesPlugin.this.application.getFrame()
        .getSelectedProjectFrame();
    Set<Layer> selectedLayers = project.getLayerLegendPanel()
        .getSelectedLayers();
    if (selectedLayers.size() != 1) {
      LOGGER.error("You need to select one (and only one) layer."); //$NON-NLS-1$
      return;
    }
    Layer layer = selectedLayers.iterator().next();
    IFeatureCollection<? extends IFeature> routes =  layer.getFeatureCollection();
    String attribute = "SENS";
    Map<Object, Integer> orientationMap = new HashMap<Object, Integer>(2);
    orientationMap.put("Direct", new Integer(1));
    orientationMap.put("Inverse", new Integer(-1));
    orientationMap.put("Double", new Integer(2));
    orientationMap.put("NC", new Integer(2));
    String groundAttribute = "POS_SOL";
    double tolerance = 0.1;
    CarteTopo networkMap = new CarteTopo("Network Map");
    Chargeur.importAsEdges(routes, networkMap, attribute, orientationMap,
        groundAttribute, null, groundAttribute, tolerance);

    SchemaDefaultFeature schema = new SchemaDefaultFeature();

    FeatureType type = new FeatureType();
    //type.addFeatureAttribute(new AttributeType("gid", "id", "integer"));
    type.addFeatureAttribute(new AttributeType("t_id", "String"));
    type.addFeatureAttribute(new AttributeType("oneway", "String"));
    type.addFeatureAttribute(new AttributeType("cost", "double"));
    type.addFeatureAttribute(new AttributeType("r_cost", "double"));
    type.addFeatureAttribute(new AttributeType("source", "integer"));
    type.addFeatureAttribute(new AttributeType("target", "integer"));
    type.setGeometryType(GM_LineString.class);

    Map<Integer, String[]> map = new HashMap<Integer, String[]>(0);
    for (int i = 0; i < type.getFeatureAttributes().size(); i++) {
      String name = type.getFeatureAttributeI(i).getMemberName();
      map.put(i, new String[] { name, name });
      System.out.println("" + i + " " + name);
    }
    schema.setAttLookup(map);
    schema.setFeatureType(type);

    FT_FeatureCollection<DefaultFeature> collection = new FT_FeatureCollection<DefaultFeature>();
    collection.setFeatureType(type);

    

    
    Map<Arc, DefaultFeature> mapArc = new HashMap<Arc, DefaultFeature>(
        networkMap.getPopArcs().size());
    System.out.println(networkMap.getPopArcs().size());

//    Map<String, double[]> speedMap = getSpeedMap();

    int count = 0;
    for (Arc arc : networkMap.getPopArcs()) {
      System.out.println("arc " + count + " / "
          + networkMap.getPopArcs().size());
      count++;
      boolean oneWay = arc.getOrientation() != 2;
      boolean reverse = false;
      ILineString line = arc.getGeometrie();
      if (arc.getOrientation() == -1) {
        line = (GM_LineString) line.reverse();
        reverse = true;
      }
      IFeature feat = arc.getCorrespondant(0);
      DefaultFeature f = new DefaultFeature(line);
      f.setAttributes(new Object[schema.getAttLookup().size()]);
      f.setFeatureType(type);
      f.setSchema(schema);
      //f.setAttribute("gid", (Long)feat.getAttribute("gid"));
      f.setAttribute("oneway", (oneWay) ? "Y" : "N");
      long mspeed = (Long)feat.getAttribute("mspeed");
      mspeed -=15;
      if(mspeed <0) mspeed = 0;
      mspeed *= 1000;
      mspeed /= 3600;

      double speed = mspeed+0.001;
      double time = arc.getGeometrie().length() / speed;
      if(mspeed == 0){
        time = 10000;
      }
      double reverseCost = time;
      if (oneWay) {
        reverseCost = 10000;
      }
      if(time > 10000)
        System.out.println("PROBLEM");
      f.setAttribute("t_id", feat.getAttribute("ID"));
      f.setAttribute("cost", time);
      f.setAttribute("r_cost", reverseCost);
      Noeud source = (reverse) ? arc.getNoeudFin() : arc.getNoeudIni();
      Noeud target = (reverse) ? arc.getNoeudIni() : arc.getNoeudFin();
      f.setAttribute("source", source.getId());
      f.setAttribute("target", target.getId());
      collection.add(f);
      mapArc.put(arc, f);
    }
    
    
    // ShapefileWriter.write(collection, "/home/BDumenieu/Bureau/network.shp");
      
  }

}
