/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.contrib.algorithms;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * <p>
 * An Algorithm to Construct Continuous Area Cartograms:
 *      <a href='http://lambert.nico.free.fr/tp/biblio/Dougeniketal1985.pdf'>Dougeniketal1985.pdf</a>
 * </p>
 */
public class CartogramDougenik {
  
  /** LOGGER. */
  private final static Logger LOGGER = LogManager.getLogger(CartogramDougenik.class.getName());
  
  /**
   * 
   * @param population
   * @param attributeName : needs to be double type
   * @param nbIteration
   * @return
   */
  public static Population<DefaultFeature> doCartogram(IPopulation<IFeature> population, String attributeName, int nbIteration) {
    
    // Define a new population 
    Population<DefaultFeature> newPopulation = new Population<DefaultFeature>(false, "CartogramDougenik", DefaultFeature.class, true);
    newPopulation.setFeatureType((FeatureType) population.getFeatureType());
    ((FeatureType)newPopulation.getFeatureType()).setSchema((SchemaDefaultFeature) ((FeatureType)population.getFeatureType()).getSchema());
    
    for (IFeature feature : population) {
      DefaultFeature n = newPopulation.nouvelElement(feature.getGeom());
      n.setSchema((SchemaDefaultFeature) ((FeatureType)newPopulation.getFeatureType()).getSchema());
      n.setAttributes(((DefaultFeature)feature).getAttributes());
    }
    
    int totalValue = 0;
    for (IFeature feature : newPopulation) {
      int value = new Integer(feature.getAttribute(attributeName).toString());
      if (value > totalValue) {
        totalValue = value;
      }
    }
    LOGGER.trace("POP MAX = " + totalValue);
    
    // ----------------------------------------
    // For each iteration (user controls when done)
    int targetSizeError = 1;
    for (int i = 0; i < nbIteration; i++) {

      // ----------------------------------------
      //    Calculate area et centroid
      double totalArea = 0.0;
      
      for (IFeature feature : newPopulation) {
        double area = feature.getGeom().area();
        totalArea = totalArea + area;
        
      }
      
      // ----------------------------------------
      //    radius, mass, error size
      Map<IFeature, IDirectPosition> centroidList = new HashMap<IFeature, IDirectPosition>();
      Map<IFeature, Double> radiusList = new HashMap<IFeature, Double>();
      Map<IFeature, Double> desiredList = new HashMap<IFeature, Double>();
      Map<IFeature, Double> massList = new HashMap<IFeature, Double>();
      Map<IFeature, Double> sizeErrorList = new HashMap<IFeature, Double>();
      for (IFeature feature : newPopulation) {
        int value = new Integer(feature.getAttribute(attributeName).toString());
        double area = feature.getGeom().area();

        centroidList.put(feature, feature.getGeom().centroid());
        double desired = totalArea * value / totalValue;
        desiredList.put(feature, desired);
        radiusList.put(feature, Math.sqrt(area / Math.PI));
        massList.put(feature, Math.sqrt(desired/Math.PI) - Math.sqrt(area/Math.PI));
        sizeErrorList.put(feature,  Math.max(area, desired) / Math.min(area, desired));
      }
      
      // ----------------------------------------
      //    ForceReductionFactor
      double sizeError = 0;
      double forceReductionFactor = 0;
      for (double d : sizeErrorList.values()) {
        sizeError += d;
      }
      sizeError = sizeError / sizeErrorList.size();
      forceReductionFactor = 1 / (1 + sizeError);
      
      // ----------------------------------------
      // For each boundary line (i.e. for each polygon)
      for (IFeature feature : newPopulation) {
        
        // ----------------------------------------
        // CAS POLYGON
        if (feature.getGeom() instanceof GM_Polygon) {
          
          // Remplace polygon par cette nouvelle liste de points
          DirectPositionList posList = new DirectPositionList();
          
          // For each coordinate pair (i.e. for each edge)
          int cpt = 0;
          IDirectPosition coordOld = null;
          for (IDirectPosition coord : feature.getGeom().coord()) {
            
            if (cpt > 0) {
              
              GM_LineString line = new GM_LineString (coordOld, coord);
              
              // pour chaque point
              for (int k = 0; k < line.getControlPoint().size(); k++) { 
                GM_Point point = new GM_Point(line.getControlPoint().get(k));
                
                double x = point.getPosition().getX();
                double x0 = x;
                double y = point.getPosition().getY();
                double y0 = y;
                
                // For each polygon centroid, create an  array of vectors: [x, y]
                for (IFeature currentFeature : centroidList.keySet()) {
                  
                  IDirectPosition centre = centroidList.get(currentFeature);
                  double cx = centre.getX();
                  double cy = centre.getY();
                  double radius = radiusList.get(currentFeature);
                  double mass = massList.get(currentFeature);
                  double distance = Math.sqrt(Math.pow(x0 - cx, 2) + Math.pow(y0 - cy, 2));
                  double fij = 0;
                  if (distance > radius) {
                    fij = mass * radius / distance;
                  } else {
                    double xF = distance / radius;
                    fij = mass * (Math.pow(xF, 2) * (4 - 3 * xF));
                  }
                  fij = fij * forceReductionFactor / distance;
                  
                  x = (x0 - cx) * fij + x;
                  y = (y0 - cy) * fij + y;
                  
                }
                
                if (k > 0 || (k == 0 && cpt == 1)) {
                  posList.add(new DirectPosition(x, y));
                }
                
              }
                
            }
            
            cpt++;
            coordOld = coord;
          }
          
          feature.setGeom(new GM_Polygon(new GM_LineString(posList)));
          // System.out.println("geom transformée.");
        
          // end instance of polygon
        } else if (feature.getGeom() instanceof GM_MultiSurface) {
          
          GM_MultiSurface<GM_Polygon> multiSurface = (GM_MultiSurface<GM_Polygon>) feature.getGeom();
          IMultiSurface<IPolygon> unionGeom = new GM_MultiSurface<IPolygon>();
          
          for (int j = 0; j < multiSurface.size(); j++) {
            
            // GM_Polygon polygon = multiSurface.get(j);
            
            // Remplace polygon par cette nouvelle liste de points
            DirectPositionList posList = new DirectPositionList();
            
            // For each coordinate pair (i.e. for each edge)
            int cpt = 0;
            IDirectPosition coordOld = null;
            for (IDirectPosition coord : multiSurface.get(j).coord()) {
              if (cpt > 0) {
                GM_LineString line = new GM_LineString (coordOld, coord);
              
                // pour chaque point
                for (int k = 0; k < line.getControlPoint().size(); k++) { 
                  GM_Point point = new GM_Point(line.getControlPoint().get(k));
                  
                  double x = point.getPosition().getX();
                  double x0 = x;
                  double y = point.getPosition().getY();
                  double y0 = y;
                  
                  // For each polygon centroid, create an  array of vectors: [x, y]
                  for (IFeature currentFeature : centroidList.keySet()) {
                    
                    IDirectPosition centre = centroidList.get(currentFeature);
                    double cx = centre.getX();
                    double cy = centre.getY();
                    double radius = radiusList.get(currentFeature);
                    double mass = massList.get(currentFeature);
                    double distance = Math.sqrt(Math.pow(x0 - cx, 2) + Math.pow(y0 - cy, 2));
                    double fij = 0;
                    if (distance > radius) {
                      fij = mass * radius / distance;
                    } else {
                      double xF = distance / radius;
                      fij = mass * (Math.pow(xF, 2) * (4 - 3 * xF));
                    }
                    fij = fij * forceReductionFactor / distance;
                    
                    x = (x0 - cx) * fij + x;
                    y = (y0 - cy) * fij + y;
                    
                  }
                  
                  if (k > 0 || (k == 0 && cpt == 1)) {
                    posList.add(new DirectPosition(x, y));
                  }
                  
                }
                
              }
            
              cpt++;
              coordOld = coord;
            }
          
            unionGeom.add(new GM_Polygon(new GM_LineString(posList)));
          }
          feature.setGeom(unionGeom);
          
        }
        
      } // end each boundary line 
      
      // Break if we hit the target size error
      if (sizeError <= targetSizeError) break;
      
    } // end each iteration
    
    return newPopulation;
    
  }
  
  
}


