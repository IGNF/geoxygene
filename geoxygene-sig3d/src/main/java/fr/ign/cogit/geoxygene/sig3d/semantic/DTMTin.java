package fr.ign.cogit.geoxygene.sig3d.semantic;

import java.util.Iterator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangulatedSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.sig3d.semantic.AbstractDTMLayer;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;
import fr.ign.cogit.geoxygene.util.index.Tiling;

public class DTMTin extends AbstractDTMLayer {

  double DT_OFFSETTING = 1;

  IFeatureCollection<IFeature> featColl = new FT_FeatureCollection<IFeature>();

  Box3D b = null;

  public DTMTin(ITriangulatedSurface tin) {
    super();

    b = new Box3D(tin);
    int nbTin = tin.getPatch().size();

    for (int i = 0; i < nbTin; i++) {

      featColl.add(new DefaultFeature((IPolygon) tin.getPatch().get(i)));

    }

    featColl.initSpatialIndex(Tiling.class, true);

  }

  @Override
  public void refresh() {
    // TODO Auto-generated method stub

  }

  @Override
  public MultiPolygon processSurfacicGrid(double xmin, double xmax,
      double ymin, double ymax) {
    // TODO Auto-generated method stub

    GM_Envelope e = new GM_Envelope(xmin, xmax, ymin, ymax);

    Iterator<IFeature> iFeat = this.featColl.select(e).iterator();

    if (!iFeat.hasNext()) {
      return null;
    }

    IMultiSurface<IOrientableSurface> iMS = new GM_MultiSurface<IOrientableSurface>();

    while (iFeat.hasNext()) {

      iMS.add((IPolygon) iFeat.next());

    }

    try {
      return (MultiPolygon) JtsGeOxygene.makeJtsGeom(iMS);
    } catch (Exception e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    return null;
  }

  @Override
  public IGeometry getGeometryAt(double x, double y) {

    Iterator<IFeature> iFeat = this.featColl.select(new DirectPosition(x, y),
        0.1).iterator();

    if (iFeat.hasNext()) {
      return iFeat.next().getGeom();
    }

    return null;
  }

  @Override
  public Box3D get3DEnvelope() {
    // TODO Auto-generated method stub
    return b;
  }

  @Override
  public Coordinate castCoordinate(double x, double y) {

    IGeometry geom = this.getGeometryAt(x, y);

    if (geom == null) {
      return null;
    }

    if (geom instanceof ITriangle) {

      ITriangle tri = (ITriangle) geom;

      IDirectPosition dp1 = tri.getCorners()[0].getDirect();
      IDirectPosition dp2 = tri.getCorners()[1].getDirect();
      IDirectPosition dp3 = tri.getCorners()[2].getDirect();

      double xn = (dp2.getY() - dp1.getY()) * (dp3.getZ() - dp1.getZ())
          - (dp3.getY() - dp1.getY()) * (dp2.getZ() - dp1.getZ());
      double yn = (dp3.getX() - dp1.getX()) * (dp2.getZ() - dp1.getZ())
          - (dp2.getX() - dp1.getX()) * (dp3.getZ() - dp1.getZ());
      double zn = (dp2.getX() - dp1.getX()) * (dp3.getY() - dp1.getY())
          - (dp3.getX() - dp1.getX()) * (dp2.getY() - dp1.getY());

      double zMoy = (dp1.getX() * xn + dp1.getY() * yn + dp1.getZ() * zn - x
          * xn - y * yn)
          / zn;

      return new Coordinate(x, y, zMoy);

    } else {
      System.out.println("Bad class");
    }

    return null;
  }

}
