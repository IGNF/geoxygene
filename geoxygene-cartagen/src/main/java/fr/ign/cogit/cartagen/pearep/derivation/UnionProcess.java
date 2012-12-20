/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.derivation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

/**
 * Beastly union of nearby polygons (using JTS) to be used inside a
 * ScaleMaster2.0
 * @author JFGirres
 * 
 */
public class UnionProcess extends ScaleMasterGeneProcess {

  private static UnionProcess instance = null;

  public UnionProcess() {
    // Exists only to defeat instantiation.
  }

  public static UnionProcess getInstance() {
    if (instance == null) {
      instance = new UnionProcess();
    }
    return instance;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void execute(IFeatureCollection<? extends IGeneObj> features) {
    List<Geometry> list = new ArrayList<Geometry>();
    try {
      // Detection of invalid polygons
      for (IGeneObj obj : features) {
        IGeometry geom = obj.getGeom();
        Geometry jtsGeom;
        jtsGeom = JtsGeOxygene.makeJtsGeom(geom);
        if (jtsGeom instanceof MultiPolygon) {
          MultiPolygon mp = (MultiPolygon) jtsGeom;
          for (int i = 0; i < mp.getNumGeometries(); i++) {
            Polygon poly = (Polygon) mp.getGeometryN(i);
            if (poly.isValid() && poly.getArea() != 0) {
              list.add(poly);
            }
          }
        } else {
          if (jtsGeom instanceof Polygon) {
            Polygon poly = (Polygon) jtsGeom;
            if (poly.isValid() && poly.getArea() != 0) {
              list.add(poly);
            }
          } else {
            list.add(jtsGeom);
          }
        }
      }

      // Beastly union of the polygons
      Geometry jtsUnion = JtsAlgorithms.union(list);
      IGeometry union = JtsGeOxygene.makeGeOxygeneGeom(jtsUnion);

      // Get the object population
      IPopulation<IGeneObj> pop = CartAGenDoc.getInstance().getCurrentDataset()
          .getCartagenPop(
              CartAGenDoc.getInstance().getCurrentDataset()
                  .getPopNameFromClass(features.get(0).getClass()));

      // Get the constructor of the class, in order to create new objects
      Constructor<? extends IGeneObj> constructor = features.get(0).getClass()
          .getConstructor(IPolygon.class);

      // Eliminate original objects
      for (IGeneObj ft : features) {
        ft.eliminateBatch();
      }

      // Split MultiPolygons and fill Polygons in the population
      if (union.isMultiSurface()) {
        IMultiSurface<IPolygon> multiPoly = (IMultiSurface<IPolygon>) union;
        for (IPolygon polygon : multiPoly.getList()) {
          IGeometry unionGeom = polygon;
          pop.add(constructor.newInstance(unionGeom));
        }
      } else if (union.isPolygon()) {
        IGeometry unionGeom = union;
        pop.add(constructor.newInstance(unionGeom));
      }
    }

    catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getProcessName() {
    return "Union";
  }

  @Override
  public void parameterise() {
  }

}
