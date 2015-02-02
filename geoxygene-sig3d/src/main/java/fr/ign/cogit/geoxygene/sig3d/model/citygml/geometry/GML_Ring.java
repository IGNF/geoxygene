package fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry;

import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;

/**
 * 
 * @author MBrasebin
 * 
 */
public class GML_Ring extends GM_Ring {

  public GML_Ring(GM_CompositeCurve compCurve, double tolerance)
      throws Exception {
    super(compCurve, tolerance);
    // TODO Auto-generated constructor stub
  }

  public GML_Ring(GM_CompositeCurve compCurve) {
    super(compCurve);
    // TODO Auto-generated constructor stub
  }

  public GML_Ring(GM_OrientableCurve oriCurve, double tolerance)
      throws Exception {
    super(oriCurve, tolerance);
    // TODO Auto-generated constructor stub
  }

  public GML_Ring(GM_OrientableCurve oriCurve) {
    super(oriCurve);
    // TODO Auto-generated constructor stub
  }

  public GML_Ring() {
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
