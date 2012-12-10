package fr.ign.cogit.geoxygene.semio.legend.mapContent.operation;

import java.awt.BasicStroke;

import fr.ign.cogit.geoxygene.semio.legend.mapContent.SymbolisedFeature;
import fr.ign.cogit.geoxygene.semio.legend.symbol.GraphicSymbol;

import com.vividsolutions.jts.operation.buffer.BufferParameters;

import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see Licence_CeCILL-C_fr.html
 *        see Licence_CeCILL-C_en.html
 * 
 *        see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 * @author Charlotte Hoarau - IGN / Laboratoire COGIT
 * @author Vianney Dugrain - IGN / Laboratoire COGIT
 *
 */
public class AreaOp {
  
private SymbolisedFeature symbolisedFeature;
  
  public SymbolisedFeature getSymbolisedFeature() {
    return symbolisedFeature;
  }
  
  public void setSymbolisedFeature(SymbolisedFeature symbolisedFeature) {
    this.symbolisedFeature = symbolisedFeature;
  }
  
  public AreaOp(){};
  
  public AreaOp(SymbolisedFeature symbolisedFeature) {
    super();
    this.symbolisedFeature = symbolisedFeature;
  }
  
  /**
   * Computes the area covered by the feature on the map.
   * 
   * <strong>French:</strong><br />
   * Calcule la surface couverte par un objet carto. 
   *  
   *  Unité: renvoie la surface en mètres terrain.
   *  Si l'objet est un polygone :
   *      superficie = aire du polygone de buffer de valeur (contour/2)
   *  Si l'objet est un point :
   *      superficie = aire incluse dans le symbole + périmètre * (contour/2)
   *  Si l'objet est une ligne:
   *      superficie = longueur * épaisseur + cercle de diamètre égal au contour
   */
  public void computeArea() {
    GraphicSymbol symbol = this.symbolisedFeature.getSymbolisedFeatureCollection().getLegend().getSymbol();
    
    //Getting the widthOnMap attribute of the GraphicSymbol
    //Epaisseur exprimée en mètres terrain
    double widthOnMap = symbol.getWidthOnMap();
    
    //Computing the area depending on the implantation mode (polygon, line or point)
    if (this.symbolisedFeature.getGeom().getClass().equals(GM_Polygon.class)
        || this.symbolisedFeature.getGeom().getClass().equals(GM_MultiSurface.class)) {
        
        // Prise en compte du contour et de la surface aux angles :
        if(symbol.getJoin() == BasicStroke.JOIN_MITER) {
          this.symbolisedFeature.setArea(this.symbolisedFeature.getGeom().buffer(
              (widthOnMap / 2),
              8,
              BufferParameters.CAP_SQUARE,
              BufferParameters.JOIN_MITRE).area());
          
        } else if (symbol.getJoin() == BasicStroke.JOIN_BEVEL) {
          this.symbolisedFeature.setArea(this.symbolisedFeature.getGeom().buffer(
              (widthOnMap / 2),
              8,
              BufferParameters.CAP_FLAT,
              BufferParameters.JOIN_BEVEL).area());
          
        } else if (symbol.getJoin() == BasicStroke.JOIN_ROUND) {
          this.symbolisedFeature.setArea(this.symbolisedFeature.getGeom().buffer(
              (widthOnMap / 2),
              8,
              BufferParameters.CAP_ROUND,
              BufferParameters.JOIN_ROUND).area());
        }
        
    } else if (this.symbolisedFeature.getGeom().getClass().equals(GM_LineString.class)
        || this.symbolisedFeature.getGeom().getClass().equals(GM_MultiCurve.class)) {
     
      // Prise en compte du contour et de la surface aux angles :
        if (symbol.getJoin() == BasicStroke.JOIN_MITER) {
          if (symbol.getCap() == BasicStroke.CAP_BUTT) {
            this.symbolisedFeature.setArea(this.symbolisedFeature.getGeom().buffer(
                (widthOnMap / 2),
                8,
                BufferParameters.CAP_FLAT,
                BufferParameters.JOIN_MITRE).area());
          } else if (symbol.getCap() == BasicStroke.CAP_ROUND) {
            this.symbolisedFeature.setArea(this.symbolisedFeature.getGeom().buffer(
                (widthOnMap / 2),
                8,
                BufferParameters.CAP_ROUND,
                BufferParameters.JOIN_MITRE).area());
          } else if (symbol.getCap() == BasicStroke.CAP_SQUARE) {
            this.symbolisedFeature.setArea(this.symbolisedFeature.getGeom().buffer(
                (widthOnMap / 2),
                8,
                BufferParameters.CAP_SQUARE,
                BufferParameters.JOIN_MITRE).area());
          }
          
        } else if (symbol.getJoin() == BasicStroke.JOIN_BEVEL) {
          if (symbol.getCap() == BasicStroke.CAP_BUTT) {
            this.symbolisedFeature.setArea(this.symbolisedFeature.getGeom().buffer(
                (widthOnMap / 2),
                8,
                BufferParameters.CAP_FLAT,
                BufferParameters.JOIN_BEVEL).area());
          } else if (symbol.getCap() == BasicStroke.CAP_ROUND) {
            this.symbolisedFeature.setArea(this.symbolisedFeature.getGeom().buffer(
                (widthOnMap / 2),
                8,
                BufferParameters.CAP_ROUND,
                BufferParameters.JOIN_BEVEL).area());
          } else if (symbol.getCap() == BasicStroke.CAP_SQUARE) {
            this.symbolisedFeature.setArea(this.symbolisedFeature.getGeom().buffer(
                (widthOnMap / 2),
                8,
                BufferParameters.CAP_SQUARE,
                BufferParameters.JOIN_BEVEL).area());
          }
          
        } else if (symbol.getJoin() == BasicStroke.JOIN_ROUND) {
          if (symbol.getCap() == BasicStroke.CAP_BUTT) {
            this.symbolisedFeature.setArea(this.symbolisedFeature.getGeom().buffer(
                (widthOnMap / 2),
                8,
                BufferParameters.CAP_FLAT,
                BufferParameters.JOIN_ROUND).area());
          } else if (symbol.getCap() == BasicStroke.CAP_ROUND) {
            this.symbolisedFeature.setArea(this.symbolisedFeature.getGeom().buffer(
                (widthOnMap / 2),
                8,
                BufferParameters.CAP_ROUND,
                BufferParameters.JOIN_ROUND).area());
          } else if (symbol.getCap() == BasicStroke.CAP_SQUARE) {
            this.symbolisedFeature.setArea(this.symbolisedFeature.getGeom().buffer(
                (widthOnMap / 2),
                8,
                BufferParameters.CAP_SQUARE,
                BufferParameters.JOIN_ROUND).area());
          }
        }
        
    } else if (this.symbolisedFeature.getGeom().getClass().equals(GM_Point.class)
                || this.symbolisedFeature.getGeom().getClass().equals(GM_MultiPoint.class)) {
        //Getting the widthOnMap attribute of the GraphicSymbol
        //depending on the unit of mesure
        double sizeOnMap = symbol.getSizeOnMap();
        
        // Computing the area depending on the shape of the GraphicSymbol
        // On calcule l'aire du symbole en fonction de la forme du GraphicSymbol         
        if (symbol.getPointSymbolName().equalsIgnoreCase("circle")) {
          this.symbolisedFeature.setArea(Math.PI * Math.pow(sizeOnMap / 2, 2) 
                    + 2 * Math.PI * (sizeOnMap/2) * widthOnMap/2);
        
        } else if (symbol.getPointSymbolName().equalsIgnoreCase("triangle")) {
          this.symbolisedFeature.setArea(Math.pow(sizeOnMap / 2, 2) * 3 * Math.sqrt(3) / 4 
                    + 3 * (sizeOnMap/2) * Math.sqrt(3) * widthOnMap/2);
        
        } else if (symbol.getPointSymbolName().equalsIgnoreCase("star")) {
            double aire = ((5/2) * Math.sin(2*Math.PI/5)*Math.pow(sizeOnMap/2, 2) 
                    - ((5/4) * Math.pow(sizeOnMap, 2) * Math.pow(Math.sin(Math.PI / 5), 2) 
                    * Math.tan(Math.PI/5))) + 10 * sizeOnMap * Math.tan(Math.PI / 5) * (widthOnMap/2); 
            this.symbolisedFeature.setArea(aire);
        
        } else if (symbol.getPointSymbolName().equalsIgnoreCase("hLine") 
                || symbol.getPointSymbolName().equalsIgnoreCase("vLine")) {
          this.symbolisedFeature.setArea(sizeOnMap * 0.2 * sizeOnMap + (2 * sizeOnMap + 2 * sizeOnMap * 0.1) * widthOnMap/2);
        
        } else if (symbol.getPointSymbolName().equalsIgnoreCase("cross")) {
          this.symbolisedFeature.setArea(2 * (sizeOnMap * 0.2 * sizeOnMap + (2 * sizeOnMap + 2 * sizeOnMap * 0.1) * widthOnMap/2)
                    - 0.04 * Math.pow(sizeOnMap, 2) - 0.8 * sizeOnMap * widthOnMap/2);
        
        } else if (symbol.getPointSymbolName().equalsIgnoreCase("x")) {
            double rayon = sizeOnMap / 2;
            double squareSOM = Math.pow(rayon, 2);
            double aire = 4 * (Math.sqrt(squareSOM - (squareSOM / 32)) - (rayon / (4 * Math.sqrt(2))))
                            * (rayon * Math.sqrt(2) / 4) + squareSOM / 8 
                            + (8 * (Math.sqrt(squareSOM - (squareSOM / 32)) - (rayon / (4 * Math.sqrt(2)))) + rayon * Math.sqrt(2)) * widthOnMap/2;
            this.symbolisedFeature.setArea(aire);
        
        } else {
          this.symbolisedFeature.setArea(sizeOnMap * sizeOnMap + 4 * sizeOnMap * widthOnMap/2);
        }
    } else {
      this.symbolisedFeature.setArea(0);
    }
  }
  
  /**
   * Computes the area covered by the feature on the map.
   * 
   * <strong>French:</strong><br />
   * Calcule la surface couverte par un objet carto. 
   *  
   *  Unité: renvoie la surface en mètres terrain.
   *  Si l'objet est un polygone :
   *      superficie = aire du polygone de buffer de valeur (contour/2)
   *  Si l'objet est un point :
   *      superficie = aire incluse dans le symbole + périmètre * (contour/2)
   *  Si l'objet est une ligne:
   *      superficie = longueur * épaisseur + cercle de diamètre égal au contour
   */
  public static void computeArea(SymbolisedFeature symbolisedFeature) {
    
  };
}
