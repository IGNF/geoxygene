/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for 
 * the development and deployment of geographic (GIS) applications. It is a open source 
 * contribution of the COGIT laboratory at the Institut Géographique National (the French 
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net 
 *  
 * Copyright (C) 2005 Institut Géographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation; 
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with 
 * this library (see file LICENSE if present); if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *  
 */
 
package fr.ign.cogit.geoxygene.generalisation;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Curve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;


/**
 * Methodes statiques de generalisation par filtrage (Douglas-Peucker).
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */


public class Filtering {


    public Filtering () {
        
    }

    ////////////////////////////////////////////////////////////////////////////////
    ///// DouglasPeucker ///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////    
    /** Filtrage de Douglas-Peucker sur une polyligne. */
    // On constitue une liste de points, et appelle la méthode "DouglasPeuckerListe" sur cette liste 
    public static GM_LineString DouglasPeuckerLineString (GM_Curve G0, double seuil) {

        // linéarise la courbe
        GM_LineString theLineString = null;
        try {theLineString =  G0.asLineString(0.0,0.0,0.0);}
            catch (Exception e) {e.printStackTrace();}

        // constitue une liste de points avec la polyligne
        DirectPositionList initList = theLineString.coord();

        // appelle la méthode qui agit sur la liste - on récupère une liste de points
        DirectPositionList resultList = DouglasPeuckerList(initList,seuil);

        // crée une polyligne avec cette liste de points
        GM_LineString theResult = new GM_LineString(resultList);
        return theResult;
      }      


      /** Filtrage de Douglas-Peucker sur un polygone */
      public static GM_Polygon DouglasPeuckerPoly (GM_Polygon P0, double seuil) {
          
          // filtre la frontiere exterieure
          GM_Curve ext = DouglasPeuckerLineString(P0.getExterior().getPrimitive(),seuil);
          GM_Polygon poly = new GM_Polygon (ext);
          
          // filtre les anneaux
          if (P0.sizeInterior() != 0) {
              for (int i=0; i<P0.sizeInterior(); i++) {
                GM_Curve inte = DouglasPeuckerLineString(P0.getInterior(i).getPrimitive(),seuil);
                poly.addInterior( new GM_Ring(inte) );
              }
          }
          
          return poly;
      }


      /** Filtrage de DouglasPeucker sur un GM_Object. 
       Supportés : Aggrégat, Courbe, Polyligne, Polygon.  */
      public static GM_Object DouglasPeucker (GM_Object geom, double seuil) {
                    
        if ((geom instanceof GM_Curve) || (geom instanceof GM_LineString)) return  DouglasPeuckerLineString((GM_Curve)geom,seuil);
        
        else if (geom instanceof GM_Polygon) return DouglasPeuckerPoly((GM_Polygon)geom,seuil);
        
        else if (geom instanceof GM_MultiCurve) {
            GM_MultiCurve aggr = (GM_MultiCurve)geom;
            GM_MultiCurve result = new GM_MultiCurve();
            for (int i=0; i<aggr.size(); i++) {
                GM_Curve elt = (GM_Curve)aggr.get(i);
                result.add(DouglasPeuckerLineString(elt,seuil));
            }
            return result;
        }
        
        else if (geom instanceof GM_MultiSurface) {
            GM_MultiSurface aggr = (GM_MultiSurface)geom;
            GM_MultiSurface result = new GM_MultiSurface();
            for (int i=0; i<aggr.size(); i++) {
                GM_Polygon elt = (GM_Polygon)aggr.get(i);
                result.add(DouglasPeuckerPoly(elt,seuil));
            }
            return result;
        }
        
        else { //GM_Aggregat
            GM_Aggregate aggr = (GM_Aggregate)geom;
            GM_Aggregate result = new GM_Aggregate();
            for (int i=0; i<aggr.size(); i++) {
                GM_Object elt = (GM_Object)aggr.get(i);
                result.add(DouglasPeucker(elt,seuil));
            }
            return result;
        }
      }

      
      /** Douglas-Peucker sur une liste de points */
       // On applique l'algo en utilisant la récursivité
      public static DirectPositionList DouglasPeuckerList (DirectPositionList PtList, double seuil) {

        DirectPositionList douglasVector = new DirectPositionList();
        DirectPositionList filtreVector = new DirectPositionList();

        int i = 0;
        int k = 0;
        double dist = 0.0;
        double max = 0.0;
        DirectPosition Pt;

        int nbpts = PtList.size();   
        DirectPosition PtIni = PtList.get(0);
        DirectPosition PtFin = PtList.get(nbpts-1);
        if (nbpts > 2) {
          for (k=1; k<nbpts-1; k++) {
              Pt = PtList.get(k);
              dist = distLigne(Pt,PtIni,PtFin);        
              if ((dist>=seuil) && (dist>max)) {
                  i = k;
                  max = dist;
              }
          }
        }

        if (i != 0) {
          douglasVector.addAll(DouglasPeuckerList(new DirectPositionList(PtList.getList().subList(0,i+1)),seuil));
          douglasVector.addAll(DouglasPeuckerList(new DirectPositionList(PtList.getList().subList(i,nbpts)),seuil));    
        } else {
            douglasVector.add(PtIni);
            douglasVector.add(PtFin);
        }

        /* elimination des doublons */
        DirectPosition ptmul = douglasVector.get(0);
        filtreVector.add(ptmul);
        for (k=0; k<douglasVector.size(); k++) {
            Pt = douglasVector.get(k);
            if (!Pt.equals2D(ptmul, 0.)) {
                filtreVector.add(Pt);
                ptmul = Pt;
            }
        }

        return filtreVector;
      }



    /** distance du point P a la droite [AB] */
    private static double distLigne (DirectPosition P, DirectPosition A, DirectPosition B) {
        // equation de (AB): y = m1x + p1
        // on appelle H le projete de P sur [AB]
        // equation de (PH) : y = m2x + p2
        // on a (PH) perpendicualire a (AB) ce qui permet de trouver les coordonnees de H
        // on en deduit la longueur [PH]
        double Ax = A.getX(); double Ay = A.getY();
        double Bx = B.getX(); double By = B.getY();
        double Px = P.getX(); double Py = P.getY();
        double Hx = 0.0; double Hy = 0.0;
        double m1 = 0.0; double m2 = 0.0;
        double p1 = 0.0; double p2 = 0.0;

        double deltaX = (Bx-Ax);
        double deltaY = (By-Ay);
        if ((deltaX != 0) && (deltaY != 0)) {
            m1 = deltaY / deltaX;
            p1 = Ay - m1*Ax;
            m2 = - deltaX / deltaY;
            p2 = Py - m2*Px;
            Hy = (p1*m2-p2*m1) / (m2-m1);
            Hx = (p1-p2) / (m2-m1);
        } else if (deltaY != 0) {
            Hx = Ax;
            Hy = Py;       
        } else { 
            Hx = Ax;
            Hy = Ay;}

        return Math.sqrt((Py-Hy)*(Py-Hy) + (Px-Hx)*(Px-Hx));
    }

}
