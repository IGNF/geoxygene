package fr.ign.cogit.geoxygene.sig3d.geometry;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.conversion.Extrusion2DObject;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;


/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 0.1
 * 
 * Class to generate cylinder
 * 
 * 
 *
 */
public class Cylinder {

  /**
   * Permet de générer un cylindre vertical
   * 
   * @param centre son centre
   * @param zMin son altitude minimale
   * @param zMax son altitude maximale
   * @param radius le rayon
   * @return
   */
  public static IGeometry generateCylinder(IDirectPosition centre, double zMin,
      double zMax, double radius) {

    IPoint point = new GM_Point(centre);

    IGeometry obj = point.buffer(radius );

    return Extrusion2DObject.convertFromGeometry(obj, zMin, zMax);

  }
  
  
  public static IGeometry generateCylinder(IDirectPosition centre, double zMin,
      double zMax, double radius, int nbSegments) {

    IPoint point = new GM_Point(centre);

    IGeometry obj = point.buffer(radius, nbSegments );

    return Extrusion2DObject.convertFromGeometry(obj, zMin, zMax);

  }
  
  
  public static IGeometry generateCylinder2(IDirectPosition centre, double zMin,
      double zMax, double radius, int nbSegments) {

    double xcentre = centre.getX();
    double ycentre = centre.getY();
    
    
    IDirectPositionList dpl = new DirectPositionList();
    dpl.add(centre);

    
    
    for(int j=0;j<=nbSegments;j++){
      
      
      double xTemp = xcentre +  radius
      * Math.cos(2 * Math.PI * ( 1.0 * j / nbSegments) );
      
      
      
      double yTemp = ycentre + radius
      * Math.sin(2 * Math.PI * ( 1.0 *   j / nbSegments));

      IDirectPosition dpTemp = new DirectPosition(xTemp, yTemp);
      
      dpl.add(dpTemp);
      
      
      
    }
    
    
    return Extrusion2DObject.convertFromGeometry( new GM_Polygon(new GM_LineString(dpl)), zMin, zMax);

  }



}
