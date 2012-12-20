/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.urban;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.MorphologyTransform;

public class EmptySpacesDetection {

  private static Logger logger = Logger.getLogger(EmptySpacesDetection.class
      .getName());

  /**
   * Detection of the empty spaces by making the difference between the block
   * geometry and the buffered buildings of the block. This is the
   * implementation of the method described in (Gaffuri & Trévisan, 2004). The
   * set of empty spaces of the block is incremented.
   * @param block the block in which the empty spaces are detected
   * @param bufferThreshold the size of the buffer around the buildings (20.0 m
   *          advised)
   * @param sizeThreshold the minimum size (in m²) for keeping a polygon as
   *          empty space (2000.0 m²)
   */
  @SuppressWarnings("unchecked")
  public static void detectEmptySpaces(IUrbanBlock block,
      double bufferThreshold, double sizeThreshold) {
    MorphologyTransform morph = new MorphologyTransform(bufferThreshold / 3, 10);
    IGeometry blockGeom = block.getGeom();
    for (IUrbanElement element : block.getUrbanElements()) {
      blockGeom = blockGeom.difference(element.getGeom()
          .buffer(bufferThreshold));
    }
    if (blockGeom instanceof IPolygon) {
      if (blockGeom.area() > sizeThreshold) {
        // simplify the empty space geometry
        IPolygon closed = morph.closing((IPolygon) blockGeom);
        IGeometry opened = morph.opening(closed);
        if (opened == null) {
          return;
        }
        if (opened instanceof IPolygon) {
          block.getEmptySpaces().add(
              CartagenApplication.getInstance().getCreationFactory()
                  .createEmptySpace((IPolygon) opened));
        } else if (opened instanceof IMultiSurface<?>) {
          IMultiSurface<IOrientableSurface> multi = (IMultiSurface<IOrientableSurface>) opened;
          for (IOrientableSurface simple : multi.getList()) {
            if (simple.area() > sizeThreshold) {
              block.getEmptySpaces().add(
                  CartagenApplication.getInstance().getCreationFactory()
                      .createEmptySpace((IPolygon) simple));
            }
          }
        }
        return;
      }
    }
    // in this case, the geometry is a multisurface
    if (!(blockGeom instanceof IMultiSurface<?>)) {
      EmptySpacesDetection.logger
          .warn("Problem, the geometry is neither a Polygon nor a Multisurface!");
      return;
    }

    IMultiSurface<IOrientableSurface> multiGeom = (IMultiSurface<IOrientableSurface>) blockGeom;
    for (IOrientableSurface surf : multiGeom.getList()) {
      if (surf.area() > sizeThreshold) {
        // simplify the empty space geometry
        IPolygon closed = morph.closing((IPolygon) surf);
        IGeometry opened = morph.opening(closed);
        if (opened == null) {
          continue;
        }
        if (opened instanceof IPolygon) {
          block.getEmptySpaces().add(
              CartagenApplication.getInstance().getCreationFactory()
                  .createEmptySpace((IPolygon) opened));
        } else if (opened instanceof IMultiSurface<?>) {
          IMultiSurface<IOrientableSurface> multi = (IMultiSurface<IOrientableSurface>) opened;
          for (IOrientableSurface simple : multi.getList()) {
            if (simple.area() > sizeThreshold) {
              block.getEmptySpaces().add(
                  CartagenApplication.getInstance().getCreationFactory()
                      .createEmptySpace((IPolygon) simple));
            }
          }
        }
      }
    }
  }
}
