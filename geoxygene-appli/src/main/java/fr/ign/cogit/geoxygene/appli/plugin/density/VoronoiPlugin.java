/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 */
package fr.ign.cogit.geoxygene.appli.plugin.density;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.triangulate.VoronoiDiagramBuilder;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.plugin.density.tools.DensityPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.density.tools.VectPolygon;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;

/**
 * Calcul et affiche le diagramme de Voronoi d'un semi de point
 * 
 * @author SFabry
 */
public class VoronoiPlugin extends DensityPlugin {

  @Override
  public void actionPerformed(ActionEvent e) {
    this.projectFrame = application.getMainFrame().getSelectedProjectFrame();

    IPopulation<? extends IFeature> pop = getPopulation();
    
    
    ArrayList<Coordinate> listCoord = new ArrayList<Coordinate>();
    
    for (IFeature iFeature : pop.getElements()) {
      Coordinate c = new Coordinate(iFeature.getGeom().centroid().getX(), iFeature.getGeom().centroid().getY());
      listCoord.add(c);
    }
    GeometryFactory g = new GeometryFactory();
    Population<DefaultFeature> newPop = new Population<DefaultFeature>("Voronoi");
    
    VoronoiDiagramBuilder vdb = new VoronoiDiagramBuilder();
    vdb.setSites(listCoord);
    GeometryCollection gc = (GeometryCollection) vdb.getDiagram(g);

    VectPolygon v = new VectPolygon();
    
    for (int i = 0; i < gc.getNumGeometries(); i++) {
      
      Polygon p = (Polygon) gc.getGeometryN(i);
      Coordinate[] tab = p.getCoordinates();
      
      List<IDirectPosition> list1 = new ArrayList<IDirectPosition>();
      for (Coordinate coordinate : tab) {
        list1.add(new DirectPosition(coordinate.x, coordinate.y));
      }
      
      GM_LineString lineString = new GM_LineString(list1);
      GM_Polygon vor = new GM_Polygon(lineString);
      
      v.add(vor);
    }
    
    Vector<IGeometry> vp = new Vector<IGeometry>();
    
    GM_MultiPoint agg = new GM_MultiPoint();
    for (IFeature iFeature : pop.getElements()) {
      DirectPosition dp = new DirectPosition(iFeature.getGeom().centroid().getX(), iFeature.getGeom().centroid().getY());
      GM_Point p = new GM_Point(dp);
      agg.add(p);
    }
    IGeometry convexHull = agg.convexHull();
    
    for (GM_Polygon gm_Polygon : v) {
      IGeometry toto = gm_Polygon.intersection(convexHull);
      vp.add(toto);
      newPop.add(new DefaultFeature(toto)); 
    }
    
    
    try {
      v.write("./data/density/Voronoi.xls");
    } catch (IOException e1) {
      JOptionPane.showMessageDialog(null, "Probleme d'ecriture du fichier", "erreur", JOptionPane.ERROR_MESSAGE);
    }
    
    projectFrame.getDataSet().addPopulation(newPop);
    Layer layerC = projectFrame.getSld().createLayer("Voronoi",GM_Polygon.class, Color.BLACK, new Color(232, 151, 95), 1f, 1);
    layerC.getSymbolizer().setUnitOfMeasurePixel();
    
    projectFrame.getSld().add(layerC);
    
    projectFrame.getSld().moveLayer(0,projectFrame.getSld().layersCount()-1);
    
    ShapefileWriter.chooseAndWriteShapefile(newPop);
    
  }

}
