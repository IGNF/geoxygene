package fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry;

import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Curve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Surface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_SurfaceBoundary;

/**
 * 
 * @author MBrasebin
 * 
 */
public class GML_Polygon extends GM_Polygon {

  public GML_Polygon(GM_Curve curve, double tolerance) throws Exception {
    super(curve, tolerance);
    // TODO Auto-generated constructor stub
  }

  public GML_Polygon(GM_Curve curve) {
    super(curve);
    // TODO Auto-generated constructor stub
  }

  public GML_Polygon(GM_Envelope env) {
    super(env);
    // TODO Auto-generated constructor stub
  }

  public GML_Polygon(GM_LineString ls, double tolerance) throws Exception {
    super(ls, tolerance);
    // TODO Auto-generated constructor stub
  }

  public GML_Polygon(GM_LineString ls) {
    super(ls);
    // TODO Auto-generated constructor stub
  }

  public GML_Polygon(GM_Polygon geom) {
    super(geom);
    // TODO Auto-generated constructor stub
  }

  public GML_Polygon(GM_Ring ring) {
    super(ring);
    // TODO Auto-generated constructor stub
  }

  public GML_Polygon(GM_SurfaceBoundary boundary, GM_Surface spanSurf) {
    super(boundary, spanSurf);
    // TODO Auto-generated constructor stub
  }

  public GML_Polygon(GM_SurfaceBoundary bdy) {
    super(bdy);
    // TODO Auto-generated constructor stub
  }

  public GML_Polygon() {
    super();
  }

  private String ID = "";

  public String getID() {
    return this.ID;
  }

  public void setID(String iD) {
    this.ID = iD;
  }

}
