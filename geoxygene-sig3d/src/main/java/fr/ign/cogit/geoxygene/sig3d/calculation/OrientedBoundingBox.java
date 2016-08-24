package fr.ign.cogit.geoxygene.sig3d.calculation;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.analysis.roof.RoofDetection;
import fr.ign.cogit.geoxygene.sig3d.convert.transform.Extrusion2DObject;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.util.algo.SmallestSurroundingRectangleComputation;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see LICENSE.TXT
 * 
 * see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 1.7
 * 
 **/
public class OrientedBoundingBox {

  private IPolygon poly = null;
  private double zMin = Double.NaN;
  private double zMax = Double.NaN;
  private IGeometry sol = null;
  private double width = -1;
  private double length = -1;
  private double angle = -1;

  private IDirectPosition centre = null;

  public OrientedBoundingBox(IGeometry geom) {

    if (geom != null && geom.coord().size() != 0) {

      IMultiSurface<IOrientableSurface> lOS = (IMultiSurface<IOrientableSurface>) RoofDetection
          .detectRoof(geom, 0.2, false);

      if (lOS == null || lOS.size() == 0) {
        return;
      }

      poly = SmallestSurroundingRectangleComputation.getSSR(lOS);

      Box3D b = new Box3D(geom);

      zMin = b.getLLDP().getZ();
      zMax = b.getURDP().getZ();
    }

  }

  public double getWidth() {
    if (width == -1) {
      IDirectPositionList dpl = poly.coord();
      double v1 = dpl.get(0).distance(dpl.get(1));
      double v2 = dpl.get(1).distance(dpl.get(2));

      length = Math.max(v1, v2);
      width = Math.min(v1, v2);

    }

    return width;
  }



  public double getHeight() {
    return (this.getzMax() - this.getzMin());
  }
  
  public double getLength() {
    if (length == -1) {
      IDirectPositionList dpl = poly.coord();
      double v1 = dpl.get(0).distance(dpl.get(1));
      double v2 = dpl.get(1).distance(dpl.get(2));

      length = Math.max(v1, v2);
      width = Math.min(v1, v2);

    }

    return length;
  }

  public IGeometry get3DGeom() {

    if (sol == null && poly != null) {

      sol = Extrusion2DObject.convertFromPolygon(poly, zMin, zMax);

    }

    return sol;

  }

  public IDirectPosition getCentre() {

    if (centre == null) {

      IDirectPosition dp1 = poly.coord().get(0);
      IDirectPosition dp2 = poly.coord().get(2);

      double x = (dp1.getX() + dp2.getX()) / 2;
      double y = (dp1.getY() + dp2.getY()) / 2;

      centre = new DirectPosition(x, y);

    }
    return centre;
  }

  public IPolygon getPoly() {
    return poly;
  }

  public double getzMin() {
    return zMin;
  }

  public double getzMax() {
    return zMax;
  }

  public double getAngle() {
    if (angle == -1) {

      IDirectPosition dp1 = poly.coord().get(0);
      IDirectPosition dp2 = poly.coord().get(1);
      IDirectPosition dp3 = poly.coord().get(2);

      if (dp1.distance2D(dp2) < dp1.distance2D(dp3)) {

        dp2 = dp3;
      }

      Vecteur v = new Vecteur(dp1, dp2);
      v.normalise();

      double angleTemp = Math.acos(v.getX());

      angle = angleTemp % (Math.PI / 2);

      if (angle < 0) {
        angle = Math.PI / 2 + angle;
      }

    }
    return angle;
  }

}
