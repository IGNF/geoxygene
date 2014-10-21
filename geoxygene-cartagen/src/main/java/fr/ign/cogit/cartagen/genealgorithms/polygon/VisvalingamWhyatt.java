/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.genealgorithms.polygon;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Triangle;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.GeometryFactory;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Implementation of the Visvilingam-Whyatt (1993) algorithm for line
 * simplification by vertex filtering. It is more adapted to natural line than
 * the Douglas&Peucker algorithm.
 * @author GTouya
 * 
 */
public class VisvalingamWhyatt {

  private double areaTolerance;

  public VisvalingamWhyatt(double areaTolerance) {
    this.areaTolerance = areaTolerance;
  }

  /**
   * Visvalingam-Whyatt simplification of a line string.
   * @param line
   * @return
   */
  public ILineString simplify(ILineString line) {
    ILineString copy = new GM_LineString(line.coord());
    boolean loop = true;
    while (loop) {
      IDirectPosition ptAreaMin = null;
      double areaMin = Double.MAX_VALUE;
      for (IDirectPosition pt : copy.coord()) {
        if (pt.equals(line.startPoint()))
          continue;
        if (pt.equals(line.endPoint()))
          continue;
        double area = computeEffectiveArea(copy, pt);
        if (area < areaMin && !containsAnotherPoint(line, pt)) {
          areaMin = area;
          if (area < areaTolerance)
            ptAreaMin = pt;
        }
      }
      if (ptAreaMin == null)
        break;
      // remove the point
      //System.out.println("point removed");
      IDirectPositionList newCoord = copy.coord();
      newCoord.remove(ptAreaMin);
      copy = new GM_LineString(newCoord);
    }
    return copy;
  }

  /**
   * Visvalingam-Whyatt simplification of a collection of line string.
   * @param IFeatureCollection<IFeature> lignes
   * @return IFeatureCollection<IFeature> lignes
   */
  public IFeatureCollection<IFeature> simplifyOld(IFeatureCollection<IFeature> lignes) {
    IFeatureCollection<IFeature> copy = new FT_FeatureCollection<>(lignes); 
    int i=0;
    long startTime , endTime;//= System.nanoTime();
    long sumtimeArea = 0, sumtimeSelect = 0;
    System.out.println("methode sans enveloppe convexe préalable");
    startTime = System.nanoTime();
    copy.initSpatialIndex(Tiling.class, false, 160);
    endTime = System.nanoTime();
    System.out.println("index spatial initialisé en "+ (endTime - startTime)/1000000 + " ms");
    //SpatialIndex<IFeature> index = copy.getSpatialIndex();
    if (copy.hasSpatialIndex())
      System.out.println("index spatial ok");
    
    for (IFeature f: copy){
      ILineString l = (ILineString)f.getGeom();
      boolean loop = true;
      while (loop) {
        IDirectPosition ptAreaMin = null;
        double areaMin = Double.MAX_VALUE;
        for (IDirectPosition pt : l.coord()) {
          //System.out.println("parcours des pts d'une ligne");
          if (pt.equals(l.startPoint()))
            continue;
          if (pt.equals(l.endPoint()))
            continue;
          IGeometry triangle = getTriangle(l, pt);
          //double ar = computeEffectiveArea(l, pt);
//          Triangle t = new Triangle(new Coordinate(triangle.coord().get(0).getX(), triangle.coord().get(0).getY()),
//                                    new Coordinate(triangle.coord().get(1).getX(), triangle.coord().get(1).getY()),
//                                    new Coordinate(triangle.coord().get(2).getX(), triangle.coord().get(2).getY())
//                                    );
          startTime = System.nanoTime();
          double area = triangle.area();
          endTime = System.nanoTime();
          sumtimeArea += (endTime - startTime); 
          //double area = fastArea(triangle);
          //il sufit de commenter le && pour avoir l'algo initial
          if (area < areaMin && !containsAnotherPoint(l, pt)) {
            areaMin = area;
            //System.out.println("area < areaMin");
            startTime = System.nanoTime();
            if (area < areaTolerance && lignes.select(triangle).size() < 2){
              ptAreaMin = pt;
              //System.out.println("pt to remove found");
            }
            endTime = System.nanoTime();
            sumtimeSelect += (endTime - startTime);
          }
        }
        if (ptAreaMin == null)
          break;
        // remove the point
        //System.out.println("point removed");
        IDirectPositionList newCoord = l.coord();
        newCoord.remove(ptAreaMin);
        l = new GM_LineString(newCoord);
        ++i;
      }   
      f.setGeom(l);
    }
    System.out.println(i + " points supprimés");
    System.out.println(" Temps passé pour calcul aire : "+ sumtimeArea/1000000);
    System.out.println(" Temps passé pour select : "+ sumtimeSelect/1000000);
    return copy;
  }

  /**
   * Visvalingam-Whyatt simplification of a collection of line string.
   * @param IFeatureCollection<IFeature> lignes
   * @return IFeatureCollection<IFeature> lignes
   */
  public IFeatureCollection<IFeature> simplifyDebugVersion(IFeatureCollection<IFeature> lignes) {
    IFeatureCollection<IFeature> copy = new FT_FeatureCollection<>(lignes);
    int i=0;
    long startTime , endTime;
    long sumtimeArea = 0, sumtimeSelect = 0, sumSimp = 0;
    System.out.println("methode avec enveloppe convexe préalable");
    for (IFeature f: copy){
      IFeatureCollection<IFeature> lignesEnvConv = null;
      ILineString l = (ILineString)f.getGeom();
      boolean loop = true;
      while (loop) {
        IDirectPosition ptAreaMin = null;
        double areaMin = Double.MAX_VALUE;
        for (IDirectPosition pt : l.coord()) {
          //System.out.println("parcours des pts d'une ligne");
          if (pt.equals(l.startPoint()))
            continue;
          if (pt.equals(l.endPoint()))
            continue;        
          IGeometry triangle = getTriangle(l, pt);
          startTime = System.nanoTime();
          double area = triangle.area();
          endTime = System.nanoTime();
          sumtimeArea += (endTime - startTime); 
          if (area < areaMin && !containsAnotherPoint(l, pt)) {            
            if (area < areaTolerance ){
              if (lignesEnvConv == null){
                startTime = System.nanoTime();
                //lignesEnvConv = new FT_FeatureCollection<>(lignes.select(l.convexHull().getEnvelope()));
                lignesEnvConv = new FT_FeatureCollection<>(lignes.select(l.convexHull()));
                endTime = System.nanoTime();
                sumSimp += (endTime - startTime);
              }
              startTime = System.nanoTime();
              if (lignesEnvConv.select(triangle).size() < 2){
                ptAreaMin = pt;
                areaMin = area;
                //System.out.println("pt to remove found");
                }
              endTime = System.nanoTime();
              sumtimeSelect += (endTime - startTime);
              }
            }
          }
        if (ptAreaMin == null)
          break;
        // remove the point
        //System.out.println("point removed");       
        IDirectPositionList newCoord = l.coord();
        newCoord.remove(ptAreaMin);
        l = new GM_LineString(newCoord);       
        ++i;
      }      
      f.setGeom(l);     
    }
    System.out.println(i + " points supprimés");
    System.out.println(" Temps passé pour calcul aire : "+ sumtimeArea/1000000 +" ms");
    System.out.println(" Temps passé pour select : "+ sumtimeSelect/1000000+ " ms");
    System.out.println(" Temps passé pour intersection lignes et enveloppe convexe : "+ sumSimp/1000000+ " ms");
    return copy;
  }

  /**
   * Visvalingam-Whyatt simplification of a collection of line string.
   * @param IFeatureCollection<IFeature> lignes
   * @return IFeatureCollection<IFeature> lignes
   */
  public IFeatureCollection<IFeature> simplify(IFeatureCollection<IFeature> lignes) {
    IFeatureCollection<IFeature> copy = new FT_FeatureCollection<>(lignes);
    int i = 0;
    System.out.println("methode avec enveloppe convexe préalable");
    for (IFeature f: copy){
      boolean pointPoidsMinExists = true;
      ILineString line = (ILineString) f.getGeom();
      IFeatureCollection<IFeature> lignesInterConvexHull = null;
      while(pointPoidsMinExists){
        double areaMin = Double.MAX_VALUE;
        IDirectPosition pointMin = null;
        for (IDirectPosition point: line.coord()){
          if ( point.equals(line.startPoint()) || point.equals(line.endPoint()) )
            continue;
          IGeometry triangle = getTriangle(line, point);
          double areaTriangle = triangle.area();
          if (areaTriangle < areaMin && !containsAnotherPoint(line, point))
            if (areaTriangle < areaTolerance) {
              if (lignesInterConvexHull == null)
                lignesInterConvexHull = new FT_FeatureCollection<>(lignes.select(line.convexHull()));
              if (lignesInterConvexHull.select(triangle).size() < 2){
                areaMin = areaTriangle;
                pointMin = point;
              }
            }
        } //fin d'un parcours d'une ligne
        if (pointMin != null) {
          IDirectPositionList newCoord = line.coord();
          newCoord.remove(pointMin);
          line = new GM_LineString(newCoord);       
          ++i;
        } else
            pointPoidsMinExists = false;   
      }// c'est fini pour la ligne, on passe à la suivante
      f.setGeom(line);
    }
    System.out.println(i + " points supprimés");
    return copy;
  }
  
  
  /**
   * Visvalingam-Whyatt simplification of a polygon.
   * @param line
   * @return
   */
  public IPolygon simplify(IPolygon polygon) {
    IPolygon newPol = new GM_Polygon(simplify(polygon.exteriorLineString()));
    for (int i = 0; i < polygon.getInterior().size(); i++) {
      ILineString hole = simplify(polygon.interiorLineString(i));
      newPol.addInterior(new GM_Ring(hole));
    }

    return newPol;
  }

  private double computeEffectiveArea(ILineString copy, IDirectPosition pt) {
    IDirectPosition first = null, last = null;
    for (int i = 1; i < copy.coord().size() - 1; i++) {
      if (copy.coord().get(i).equals(pt)) {
        first = copy.coord().get(i - 1);
        last = copy.coord().get(i + 1);
      }
    }
    IPolygon triangle = GeometryFactory.buildTriangle(first, pt, last);
    return triangle.area();
  }
  
  // returns the triangle surrounding pt in the linestring line
  private IPolygon getTriangle(ILineString copy, IDirectPosition pt) {
    IDirectPosition first = null, last = null;
    for (int i = 1; i < copy.coord().size() - 1; i++) {
      if (copy.coord().get(i).equals(pt)) {
        first = copy.coord().get(i - 1);
        last = copy.coord().get(i + 1);
        break;
      }
    }
    IPolygon triangle = GeometryFactory.buildTriangle(first, pt, last);
    return triangle;
  }
  
  // fast computation of triangle area
  // variante du Heron : 16A² = (a²+b²+c²)²-2(a⁴+b⁴+c⁴)
  // valeur differente de la methode area() car imprecision sur double trop importante
  @Deprecated
  public double fastArea(IGeometry triangle){
    IDirectPositionList coords = triangle.coord();
    //System.out.println(coords);
    double a2 = (coords.get(0).getX() - coords.get(1).getX())*(coords.get(0).getX() - coords.get(1).getX())
               +(coords.get(0).getY() - coords.get(1).getY())*(coords.get(0).getY() - coords.get(1).getY());
    double b2 = (coords.get(1).getX() - coords.get(2).getX())*(coords.get(1).getX() - coords.get(2).getX())
               +(coords.get(1).getY() - coords.get(2).getY())*(coords.get(1).getY() - coords.get(2).getY());
    double c2 = (coords.get(0).getX() - coords.get(2).getX())*(coords.get(0).getX() - coords.get(2).getX())
               +(coords.get(0).getY() - coords.get(2).getY())*(coords.get(1).getY() - coords.get(2).getY());
    double s = triangle.length()/2;
    
    double a = Math.sqrt(a2);
    double b = Math.sqrt(b2);
    double c = Math.sqrt(c2);
    
    return Math.sqrt(s*(s-a)*(s-b)*(s-c));
    //return Math.sqrt(((a2+b2+c2)*(a2+b2+c2)-2*(a2*a2+b2*b2+c2*c2)))/4;
    //return Math.sqrt((2*a2*b2+2*b2*c2+2*a2*c2 - a2-b2-c2)/16);
   // return 0.25*Math.sqrt( (a+b+c)*(a+b-c)*(b+c-a)*(a-b+c));
  }
  
  // returns true if the triangle surrounding b in the line string line
  // contains another point of the line string, false otherwise
  private boolean containsAnotherPoint(ILineString line, IDirectPosition b){
    IDirectPosition a = null, c = null;
    int ind_a = 0, ind_b = 0, ind_c = 0;
    for (int i = 1; i < line.coord().size() - 1; i++) {
      if (line.coord().get(i).equals(b)) {
        a = line.coord().get(i - 1); ind_a = i - 1;
        c = line.coord().get(i + 1); ind_c = i + 1;
        ind_b = i;
        break;
      }
    }
    IPolygon triangle = GeometryFactory.buildTriangle(a, b, c);
    for (int i = 0; i < line.coord().size(); i++){
      if (i == ind_a || i == ind_b || i == ind_c)
        continue;
      if (triangle.contains(line.coord().get(i).toGM_Point()))
        return true;
    }
    return false;
  }
  
  public static void main(String[] args){
    IDirectPosition a = new DirectPosition(1000, 0); 
    IDirectPosition b = new DirectPosition(1201, 3250);
    IDirectPosition c = new DirectPosition(32114, 2514);
    List<IDirectPosition> points = new ArrayList<>();
    points.add(a);
    points.add(b);
    points.add(c);
    ILineString ligne = new GM_LineString(points); 
    IPolygon triangle = new GM_Polygon(ligne);
    //VisvalingamWhyatt vis = new VisvalingamWhyatt(25);
    Triangle t = new Triangle(new Coordinate(1000,0), new Coordinate(1201, 3250), new Coordinate(32114, 2514));
    
    long startTime = System.nanoTime();
    for (int i = 0 ; i< 100000; ++i)
      triangle.area();
    long endTime = System.nanoTime();
    long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.
    System.out.println("Aire avec polygon.area() : " + triangle.area());
    System.out.println("computed in " + duration + "ms");
    
    startTime = System.nanoTime();
    for (int i = 0 ; i< 100000; ++i)
      t.area();//vis.fastArea(triangle);
    endTime = System.nanoTime();
    duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.
    System.out.println("Aire avec fastArea() : " + t.area());
    System.out.println("computed in " + duration + "ms");
  }
}
