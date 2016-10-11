package fr.ign.cogit.geoxygene.sig3d.geometry;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.calculation.Calculation3D;
import fr.ign.cogit.geoxygene.sig3d.convert.transform.Extrusion2DObject;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

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
 * @version 0.1 Il s'agit d'une boite englobante 3D avec bords respectivement
 *          parallèles aux plans (z =0,y = 0 et z =0) Cette classe sert
 *          notamment à gèrer l'emprise 3D d'une couche ou d'une carte Minimum
 *          bounding box with axes paralelle to X,Y and Z axis
 */
public class Box3D {

  private IDirectPosition pMin;
  private IDirectPosition pMax;

  /**
   * Constructeur à partir d'une position minimale et d'une position maximale
   * 
   * @param pMin point indiquant xMin,yMin et zMin
   * @param pMax point indiquant xMax, yMax et zMax
   */
  public Box3D(IDirectPosition pMin, IDirectPosition pMax) {
    this.pMin = pMin;
    this.pMax = pMax;

  }

  /**
   * Renvoie le centre de la boite
   * 
   * @return le centre de la boite
   */
  public IDirectPosition getCenter() {
    double x = (this.pMin.getX() + this.pMax.getX()) / 2;
    double y = (this.pMin.getY() + this.pMax.getY()) / 2;
    double z = (this.pMin.getZ() + this.pMax.getZ()) / 2;

    return new DirectPosition(x, y, z);
  }

  /**
   * Calcule la boite correspondant à un objet de géométrie quelconque
   * 
   * @param obj
   */

  public Box3D(IGeometry obj) {
    this(obj.coord());
  }

  /**
   * Calcule d'un boite à partir d'une liste de points
   * 
   * @param dpl la liste de points utilisée pour déterminer la boite
   */
  public Box3D(IDirectPositionList dpl) {

    this.pMin = Calculation3D.pointMin(dpl);
    this.pMax = Calculation3D.pointMax(dpl);

  }

  /**
   * Construit une boîte à partir de coordonnées maximales et minimales
   * 
   * @param xmin
   * @param ymin
   * @param zmin
   * @param xmax
   * @param ymax
   * @param zmax
   */
  public Box3D(double xmin, double ymin, double zmin, double xmax, double ymax,
      double zmax) {
    this.pMin = new DirectPosition(xmin, ymin, zmin);
    this.pMax = new DirectPosition(xmax, ymax, zmax);

  }

  /**
   * Permet de récupérer le point inférieur de la boite
   * 
   * @return point de coordonnées xMin, yMin et zMin
   */
  public IDirectPosition getLLDP() {
    return this.pMin;
  }

  /**
   * Permet de récupérer le point supérieur de la boite
   * 
   * @return point de coordonnées xMax, yMax, zMax
   */
  public IDirectPosition getURDP() {
    return this.pMax;
  }

  /**
   * Permet de calculer l'intersection entre 2 boites
   * 
   * @param b boite paramètre
   * @return indique si les boites s'intersectent
   */
  public boolean intersect(Box3D b) {

    // Correction

    double xmin1 = pMin.getX();
    double xmin2 = b.pMin.getX();
    double xmax1 = pMax.getX();
    double xmax2 = b.pMax.getX();
    double ymin1 = pMin.getY();
    double ymin2 = b.pMin.getY();
    double ymax1 = pMax.getY();
    double ymax2 = b.pMax.getY();
    double zmin1 = pMin.getZ();
    double zmin2 = b.pMin.getZ();
    double zmax1 = pMax.getZ();
    double zmax2 = b.pMin.getZ();

    boolean bx = ((xmin2 <= xmax1) && (xmin2 >= xmin1))
        || ((xmin1 <= xmax2) && (xmin1 >= xmin2));
    boolean by = ((ymin2 <= ymax1) && (ymin2 >= ymin1))
        || ((ymin1 <= ymax2) && (ymin1 >= ymin2));
    boolean bz = ((zmin2 <= zmax1) && (zmin2 >= zmin1))
        || ((zmin1 <= zmax2) && (zmin1 >= zmin2));

    return bx && by && bz;

    /*
     * // -------------------------------------------------------------------
     * IDirectPosition pMin = b.getLLDP(); IDirectPosition pMax = b.getURDP();
     * 
     * if (b.pMin.getX() <= pMax.getX() && b.pMin.getY() <= pMax.getY() &&
     * b.pMin.getZ() <= pMax.getZ()) {
     * 
     * if (pMin.getX() <= b.pMax.getX() && pMin.getY() <= b.pMax.getY() &&
     * pMin.getZ() <= b.pMax.getZ()) {
     * 
     * return true;
     * 
     * }
     * 
     * }
     * 
     * return false; //
     * -------------------------------------------------------------------
     */

  }

  /**
   * Calcule l'intersection entre 2 boites Renvoie null si l'intersection
   * n'existe pas
   * @param b
   * @return
   */
  public Box3D intersection(Box3D b) {

    if (!intersect(b)) {
      return null;
    }

    double xmin = Math.max(pMin.getX(), b.pMin.getX());
    double ymin = Math.max(pMin.getY(), b.pMin.getY());
    double zmin = Math.max(pMin.getZ(), b.pMin.getZ());
    double xmax = Math.min(pMax.getX(), b.pMax.getX());
    double ymax = Math.min(pMax.getY(), b.pMax.getY());
    double zmax = Math.min(pMax.getZ(), b.pMax.getZ());

    DirectPosition pmin = new DirectPosition(xmin, ymin, zmin);
    DirectPosition pmax = new DirectPosition(xmax, ymax, zmax);

    return new Box3D(pmin, pmax);

    /*
     * // -------------------------------------------------------------------
     * IDirectPosition pMin = b.getLLDP(); IDirectPosition pMax = b.getURDP();
     * 
     * double xMin = Math.max(pMin.getX(), this.pMin.getX()); double xMax =
     * Math.min(pMax.getX(), this.pMax.getX()); if (xMin < xMax) { return null;
     * }
     * 
     * double yMin = Math.max(pMin.getY(), this.pMin.getY()); double yMax =
     * Math.min(pMax.getY(), this.pMax.getY()); if (yMin < yMax) { return null;
     * }
     * 
     * double zMin = Math.max(pMin.getZ(), this.pMin.getZ()); double zMax =
     * Math.min(pMax.getZ(), this.pMax.getZ()); if (zMin < zMax) { return null;
     * }
     * 
     * return new Box3D(xMin, yMin, zMin, xMax, yMax, zMax); //
     * -------------------------------------------------------------------
     */

  }

  /**
   * Permet de calculer l'union entre 2 boites
   * 
   * @param b boite paramètre
   * @return renvoie une autre boite résultante de l'emprise union des 2
   *         premières
   */
  public Box3D union(Box3D b) {

    IDirectPosition pMin = new DirectPosition(Math.min(this.getLLDP().getX(), b
        .getLLDP().getX()),
        Math.min(this.getLLDP().getY(), b.getLLDP().getY()), Math.min(this
            .getLLDP().getZ(), b.getLLDP().getZ()));

    IDirectPosition pMax = new DirectPosition(Math.max(this.getURDP().getX(), b
        .getURDP().getX()),
        Math.max(this.getURDP().getY(), b.getURDP().getY()), Math.max(this
            .getURDP().getZ(), b.getURDP().getZ()));

    return new Box3D(pMin, pMax);

  }

  /**
   * @return l'empreinte 2D de la boite
   */
  public IPolygon to_2D() {

    IDirectPositionList dpl = new DirectPositionList();

    dpl.add(new DirectPosition(this.getLLDP().getX(), this.getLLDP().getY(), 0));
    dpl.add(new DirectPosition(this.getURDP().getX(), this.getLLDP().getY(), 0));
    dpl.add(new DirectPosition(this.getURDP().getX(), this.getURDP().getY(), 0));
    dpl.add(new DirectPosition(this.getLLDP().getX(), this.getURDP().getY(), 0));

    dpl.add(new DirectPosition(this.getLLDP().getX(), this.getLLDP().getY(), 0));

    return new GM_Polygon(new GM_LineString(dpl));

  }
  
  
  /**
   * 
   * @return return box as a Geometrie
   */
  public IGeometry createGeometry() {

    IPolygon p = this.to_2D();

    return Extrusion2DObject.convertFromGeometry(p, this.getLLDP().getZ(), this
        .getURDP().getZ());

  }
  
  public double getXDiff(){
	  return (this
		        .getURDP().getX() - this.getLLDP().getX());
  }
  
  public double getYdiff(){
	  return (this
		        .getURDP().getY() - this.getLLDP().getY());
  }
  
  public double getHeight(){
	  return (this
		        .getURDP().getZ() - this.getLLDP().getZ());
  }

}
