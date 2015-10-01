package fr.ign.cogit.geoxygene.sig3d.equation;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.calculation.raycasting.RayCasting;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

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
 * @version 0.7
 * 
 * 3D Grid class
 * 
 */
public class Grid3D {

  private IDirectPosition origine;

  private double pasX;
  private double pasY;
  private double pasZ;

  private double epsilon = 0;

  public Grid3D(IDirectPosition origine, double pasX, double pasY, double pasZ) {
    super();
    this.origine = origine;
    this.pasX = pasX;
    this.pasY = pasY;
    this.pasZ = pasZ;
  }

  public IDirectPositionList intersection(IGeometry geom) {

    List<IPolygon> lP = new ArrayList<IPolygon>();

    if (geom instanceof IPolygon) {
      return intersection((IPolygon) geom);
    } else if (geom instanceof ISolid) {

      ISolid sol = (ISolid) geom;

      for (IOrientableSurface os : sol.getFacesList()) {

        if (os instanceof IPolygon) {
          lP.add((IPolygon) os);
        }

      }

    } else if (geom instanceof IMultiSurface<?>) {

      IMultiSurface<IOrientableSurface> multiS = (IMultiSurface<IOrientableSurface>) geom;

      for (IOrientableSurface os : multiS.getList()) {

        if (os instanceof IPolygon) {
          lP.add((IPolygon) os);
        }

      }
    }

    if (lP.size() == 0) {
      // System.out.println("Grid3D : Cas non géré");
    } else {

      IDirectPositionList dplOut = new DirectPositionList();

      for (IPolygon p : lP) {
        dplOut.addAll(this.intersection(p));
      }
      return dplOut;
    }

    return null;
  }

  private IDirectPositionList intersection(IPolygon poly) {

    IDirectPositionList dpl = new DirectPositionList();

    Box3D b = new Box3D(poly);
    IDirectPosition dpMin = b.getLLDP();
    double xmin = dpMin.getX();
    double ymin = dpMin.getY();
    double zmin = dpMin.getZ();

    IDirectPosition dpMax = b.getURDP();
    double xmax = dpMax.getX();
    double ymax = dpMax.getY();
    double zmax = dpMax.getZ();

    // On prépare l'initialisation des boucles des éléments à parcourir

    double nbX = (xmin - this.getOrigine().getX()) / this.getPasX();

    double xAct;

    if ((int) nbX == nbX) {
      xAct = xmin;
    } else {
      xAct = (this.getPasX()) * (((int) nbX) + 1) + this.getOrigine().getX();

    }

    double nbY = (ymin - this.getOrigine().getY()) / this.getPasY();

    double yAct;

    if ((int) nbY == nbY) {
      yAct = ymin;
    } else {
      yAct = (this.getPasY()) * (((int) nbY) + 1) + this.getOrigine().getY();

    }

    double nbZ = (zmin - this.getOrigine().getZ()) / this.getPasZ();

    double zAct;

    if ((int) nbZ == nbZ) {
      zAct = zmin;
    } else {
      zAct = (this.getPasZ()) * (((int) nbZ) + 1) + this.getOrigine().getZ();

    }

    // Liste des équations de ligne qui serviront à faire les calculs
    // d'intersection
    List<LineEquation> lEq = new ArrayList<LineEquation>();

    // génération des equation de ligne intersectant le plan O,x,z
    // Pour rappel : x = a1 * t + a0 y = b1 * t + b0 z = c1 * t + c0
    for (double x = xAct; x <= xmax; x = x + this.getPasX()) {

      for (double z = zAct; z <= zmax; z = z + this.getPasZ()) {

        lEq.add(new LineEquation(x, 0, 0, 1, z, 0));

      }

    }

    // génération des equation de ligne intersectant le plan O,y,z
    // Pour rappel : x = a1 * t + a0 y = b1 * t + b0 z = c1 * t + c0
    for (double y = yAct; y <= ymax; y = y + this.getPasY()) {

      for (double z = zAct; z <= zmax; z = z + this.getPasZ()) {

        lEq.add(new LineEquation(0, 1, y, 0, z, 0));

      }

    }

    // génération des equation de ligne intersectant le plan O,x,y
    // Pour rappel : x = a1 * t + a0 y = b1 * t + b0 z = c1 * t + c0
    for (double y = yAct; y <= ymax; y = y + this.getPasY()) {

      for (double x = xAct; x <= xmax; x = x + this.getPasX()) {

        lEq.add(new LineEquation(x, 0, y, 0, 0, 1));

      }

    }

    for (LineEquation lE : lEq) {

      IDirectPosition dp = intersectionLP(lE, poly);

      if (dp != null) {
        dpl.add(dp);

      }

    }

    return dpl;
  }

  public static IDirectPosition intersectionLP(LineEquation lE, IPolygon poly) {

    PlanEquation pE;
    if(poly instanceof ITriangle){
      pE = new PlanEquation(poly);
    }else{
      pE =  new ApproximatedPlanEquation(poly);
    }
    
   

    IDirectPosition dp = lE.intersectionLinePlan(pE);

    if (dp == null) {
      return null;
    }

    if (dp != null) {

      if (Double.isNaN(dp.getX())) {
        return null;
      }

    }

    if (RayCasting.lieInsidePolygon(dp, poly)) {
      return dp;
    }

    return null;
  }

  public IDirectPosition getOrigine() {
    return origine;
  }

  public void setOrigine(IDirectPosition origine) {
    this.origine = origine;
  }

  public double getPasX() {
    return pasX;
  }

  public void setPasX(double pasX) {
    this.pasX = pasX;
  }

  public double getPasY() {
    return pasY;
  }

  public void setPasY(double pasY) {
    this.pasY = pasY;
  }

  public double getPasZ() {
    return pasZ;
  }

  public void setPasZ(double pasZ) {
    this.pasZ = pasZ;
  }

  public double getEpsilon() {
    return this.epsilon;
  }

  public void setEpsilon(double epsilon) {
    this.epsilon = epsilon;
  }

}
