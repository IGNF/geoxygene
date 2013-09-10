/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.enrichment;

import java.awt.Color;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;

/**
 * This pre-process is dedicated to polygonal features and correct slivers that
 * should be a hole in the geometry.
 * @author GTouya
 * 
 */
public class CorrectSliversProcess extends ScaleMasterPreProcess {

  private static CorrectSliversProcess instance = null;

  public CorrectSliversProcess() {
    // Exists only to defeat instantiation.
  }

  public static CorrectSliversProcess getInstance() {
    if (instance == null) {
      instance = new CorrectSliversProcess();
    }
    return instance;
  }

  @Override
  public void execute(CartAGenDB dataset) throws Exception {

    for (Class<? extends IGeneObj> classObj : this.getProcessedClasses()) {
      IPopulation<IGeneObj> pop = dataset.getDataSet().getCartagenPop(
          dataset.getDataSet().getPopNameFromClass(classObj));

      for (IGeneObj obj : pop) {
        if (obj.isEliminated())
          continue;

        // only keep invalid geometries
        if (obj.getGeom().isValid())
          continue;

        IDirectPosition doublePt = null;
        for (IDirectPosition pt : obj.getGeom().coord()) {
          if (pt.equals(obj.getGeom().coord().get(0)))
            continue;
          if (obj.getGeom().coord().getList().indexOf(pt) != obj.getGeom()
              .coord().getList().lastIndexOf(pt)) {
            doublePt = pt;
            break;
          }
        }
        if (doublePt != null) {
          IGeometry oldGeom = obj.getGeom();
          IDirectPositionList newGeomList = new DirectPositionList();
          IDirectPositionList holeList = new DirectPositionList();
          holeList.add(doublePt);
          for (int i = 0; i < oldGeom.coord().size(); i++) {
            if (i <= oldGeom.coord().getList().indexOf(doublePt))
              newGeomList.add(oldGeom.coord().get(i));
            else if (i > oldGeom.coord().getList().lastIndexOf(doublePt))
              newGeomList.add(oldGeom.coord().get(i));
            else
              holeList.add(oldGeom.coord().get(i));
          }

          // build the new polygons
          IPolygon pol = new GM_Polygon(new GM_LineString(newGeomList));
          IRing hole = new GM_Ring(new GM_LineString(holeList));
          pol.addInterior(hole);
          CartagenApplication.getInstance().getFrame().getLayerManager()
              .addToGeometriesPool(pol, Color.RED, 4);
        }
      }
    }
  }

  @Override
  public String getPreProcessName() {
    return "Correct Sliver Polygons";
  }

}
