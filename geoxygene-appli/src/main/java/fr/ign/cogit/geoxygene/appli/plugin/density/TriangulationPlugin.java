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
import java.util.ArrayList;
import java.util.Vector;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.triangulate.DelaunayTriangulationBuilder;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.appli.plugin.density.tools.DensityPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.density.tools.VectTriangle;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;

/**
 * Calcul et affiche la triangulation de Delauney
 * 
 * @author SFabry
 */
public class TriangulationPlugin extends DensityPlugin {

  @Override
  public void actionPerformed(ActionEvent e) {
    this.projectFrame = application.getMainFrame().getSelectedProjectFrame();
    IPopulation<? extends IFeature> pop = getPopulation();

    Vector<Population<DefaultFeature>> vectPop = new Vector<Population<DefaultFeature>>();

    Population<DefaultFeature> popForShape = new Population<DefaultFeature>();

    ArrayList<Coordinate> listCoord = new ArrayList<Coordinate>();

    for (IFeature iFeature : pop.getElements()) {
      Coordinate c = new Coordinate(iFeature.getGeom().centroid().getX(), iFeature.getGeom().centroid().getY());
      listCoord.add(c);
    }
    GeometryFactory g = new GeometryFactory();
    DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
    dtb.setSites(listCoord);
    GeometryCollection gc = (GeometryCollection) dtb.getTriangles(g);

    VectTriangle vt = new VectTriangle();

    for (int i = 0; i < gc.getNumGeometries(); i++) {

      Polygon p = (Polygon) gc.getGeometryN(i);
      Coordinate[] tab = p.getCoordinates();

      DirectPositionList idpl = new DirectPositionList();
      for (Coordinate coordinate : tab) {
        idpl.add(new DirectPosition(coordinate.x, coordinate.y));
      }

      GM_Triangle tri = new GM_Triangle(idpl);

      vt.add(tri); 
      popForShape.add(new DefaultFeature(tri));
    }

    int n = 7;

    for(int i=0; i<n; i++)
      vectPop.add(new Population<DefaultFeature>("Triangulation - "+(i*100/n)));


    for (int i = 0; i < vt.size(); i++) {
      vectPop.get((int)(((double)i)/vt.size()*n)).add(new DefaultFeature(vt.get(i)));
    }

    for(int i=0; i<n; i++){
      projectFrame.getDataSet().addPopulation(vectPop.get(i));
      Layer layerC = projectFrame.getSld().createLayer("Triangulation - "+(i*100/n),GM_Polygon.class, Color.BLACK, new Color(232, i*255/(n-1), 12), 1f, 1);
      layerC.getSymbolizer().setUnitOfMeasurePixel();

      projectFrame.getSld().add(layerC);
    }


    ShapefileWriter.chooseAndWriteShapefile(popForShape);

  }

}
