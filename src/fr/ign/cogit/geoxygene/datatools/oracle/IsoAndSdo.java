/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework compliant with OGC/ISO specifications for 
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

package fr.ign.cogit.geoxygene.datatools.oracle;

import oracle.sdoapi.geom.CoordPoint;
import oracle.sdoapi.geom.CoordPointImpl;
import oracle.sdoapi.geom.CurvePolygon;
import oracle.sdoapi.geom.CurveString;
import oracle.sdoapi.geom.Geometry;
import oracle.sdoapi.geom.GeometryCollection;
import oracle.sdoapi.geom.GeometryFactory;
import oracle.sdoapi.geom.LineString;
import oracle.sdoapi.geom.MultiCurvePolygon;
import oracle.sdoapi.geom.MultiCurveString;
import oracle.sdoapi.geom.MultiLineString;
import oracle.sdoapi.geom.MultiPoint;
import oracle.sdoapi.geom.MultiPolygon;
import oracle.sdoapi.geom.Point;
import oracle.sdoapi.geom.Polygon;
import oracle.sdoapi.geom.Segment;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_CurveSegment;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Curve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Surface;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;


/**
 * Méthodes de conversion du format SDOAPI vers le format ISO, et réciproquement.
 * Le format SDOAPI permet ensuite d'importer et d'exporter facilement dans Oracle.
 *
 * <P> A priori achevé, MAIS  
 *  ne fonctionne pas pour les arcs de cercles et
 * ne fonctionne pas pour les GM_Surface composées de plusieurs patch.
 *  
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1  
 */

 

public class IsoAndSdo {

    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // sdoapi2iso /////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
       
    /** Convertit du format SDOAPI vers notre format ISO. Si on a null en entrée, renvoie null.
      * Cette méthode publique appelle la méthode privée "sdoapi2iso_xxxxx" adéquate, selon le type de géométrie rencontrée.*/
    public static GM_Object sdoapi2iso (Geometry geom) throws Exception {
        GM_Object gm_o = null;
        int n=3;    // dimension par defaut (avant on la passait en parametre)
        if (geom != null) {
            java.lang.Class geomClass = geom.getGeometryType();     
            if (geomClass == java.lang.Class.forName("oracle.sdoapi.geom.Point"))
                gm_o = sdoapi2iso_point((Point)geom,n);
            else if (geomClass == java.lang.Class.forName("oracle.sdoapi.geom.LineString"))
                gm_o = sdoapi2iso_linestring((LineString)geom,n);
            else if (geomClass == java.lang.Class.forName("oracle.sdoapi.geom.Polygon"))
                gm_o = sdoapi2iso_polygon((Polygon)geom,n);
            else if (geomClass == java.lang.Class.forName("oracle.sdoapi.geom.CurveString"))
                gm_o = sdoapi2iso_curvestring((CurveString)geom,n);
            else if (geomClass == java.lang.Class.forName("oracle.sdoapi.geom.CurvePolygon"))
                gm_o = sdoapi2iso_curvepolygon((CurvePolygon)geom,n);
            else if (geomClass == java.lang.Class.forName("oracle.sdoapi.geom.GeometryCollection")) 
                gm_o = sdoapi2iso_geometrycollection((GeometryCollection)geom,n);
            else if (geomClass == java.lang.Class.forName("oracle.sdoapi.geom.MultiPoint"))
                gm_o = sdoapi2iso_multipoint((MultiPoint)geom,n);
            else if (geomClass == java.lang.Class.forName("oracle.sdoapi.geom.MultiLineString")) 
                gm_o = sdoapi2iso_multilinestring((MultiLineString)geom,n);
            else if (geomClass == java.lang.Class.forName("oracle.sdoapi.geom.MultiPolygon"))
                gm_o = sdoapi2iso_multipolygon((MultiPolygon)geom,n);
            else if (geomClass == java.lang.Class.forName("oracle.sdoapi.geom.MultiCurveString"))
                gm_o = sdoapi2iso_multicurvestring((MultiCurveString)geom,n);
            else if (geomClass == java.lang.Class.forName("oracle.sdoapi.geom.MultiCurvePolygon"))
                gm_o = sdoapi2iso_multicurvepolygon((MultiCurvePolygon)geom,n);
            else throw new Exception("Format sdoapi non reconnu : "+geomClass.getName());
        }
        return gm_o;
    }
    
    
    /** Convertit un Point SDOAPI en un GM_Point ISO */
    private static GM_Point sdoapi2iso_point (Point sdoPoint, int n) throws Exception {
        double[] tab = new double[n];
        for (int j=0; j<n; j++) tab[j] = sdoPoint.getOrd(j);
        DirectPosition direct_pt = new DirectPosition(tab);
        GM_Point thePoint = new GM_Point(direct_pt);
        return thePoint;
    }
    
    
    // VERSION AVANT HERITAGE DE GM_LINESTRING SUR GM_CURVE
    /** Convertit une LineString SDOAPI en une GM_Curve ISO, composée d'une et d'une seule GM_LineString. */
   /* private static GM_Curve sdoapi2iso_linestring (LineString sdoLineString) throws Exception {
        GM_LineString isoLineString  = new GM_LineString();
        int n = sdoLineString.getCoordinateDimension();
        int nbpts = sdoLineString.getNumPoints();
        for (int i=0; i<nbpts; i++) {  
            CoordPoint coord_pt = sdoLineString.getPointAt(i);
            double[] tab = new double[n];
            for (int j=0; j<n; j++) tab[j] = coord_pt.getOrd(j);
            DirectPosition direct_pt = new DirectPosition(tab);
            isoLineString.appendControlPoint(direct_pt);
        }        
        GM_Curve isoCurve = new GM_Curve();
        isoCurve.appendSegment(isoLineString);
        return isoCurve;
    }*/
    

    /** Convertit une LineString SDOAPI en une GM_LineString ISO. */
     private static GM_LineString sdoapi2iso_linestring (LineString sdoLineString, int n) throws Exception {
        GM_LineString isoLineString  = new GM_LineString();
        int nbpts = sdoLineString.getNumPoints();
		double[] tab = new double[n];
        for (int i=0; i<nbpts; i++) {  
            CoordPoint coord_pt = sdoLineString.getPointAt(i);
            for (int j=0; j<n; j++) tab[j] = coord_pt.getOrd(j);
            DirectPosition direct_pt = new DirectPosition(tab);
            isoLineString.getControlPoint().add(direct_pt);
        }        
        return isoLineString;
    }
    
     
// A REPRENDRE quand on aura implementé GM_ArcString    
    /** Convertit une CurveString SDOAPI en une GM_Curve ISO.
      * A TERMINER : pour l'instant je genere une GM_Curve composee uniquement de GM_LineString. 
      * Les segments éventuellement circulaires sont linéarisés (pas de 10, en dur).
      * Il faudra generer une vraie GM_Curve composee de segments éventuellement circulaires. */
    private static GM_Curve sdoapi2iso_curvestring (CurveString sdoCurveString, int n) throws Exception {
        GM_Curve isoCurve = new GM_Curve();
        int nbseg = sdoCurveString.getNumSegments();
        Segment SegmentArray[] = sdoCurveString.getSegmentArray();
        for (int j=0; j<nbseg; j++) {
            Segment segment = SegmentArray[j];
            if (segment.getSegmentType() == java.lang.Class.forName("oracle.sdoapi.geom.CircularArc")) {  // c'est ca qu'il faudra changer
                Segment segment_arc = (Segment)segment.clone();
                segment = segment_arc.linearizeSegment(10);
            } else if (segment.getSegmentType() != java.lang.Class.forName("oracle.sdoapi.geom.LinearSegment"))
                throw new Exception("Cas non traité dans la conversion de SDOAPI en ISO (ni linéaire, ni arc de cercle)");
            GM_LineString isoLineString  = new GM_LineString();
            int nbpts = segment.getNumPoints();
            for (int i=0; i<nbpts; i++) {  
                CoordPoint coord_pt = segment.getPointAt(i);
                double tab[] = new double[n];
                for (int k=0; k<n; k++) tab[k] = coord_pt.getOrd(k);
                DirectPosition direct_pt = new DirectPosition(tab);
                isoLineString.getControlPoint().add(direct_pt);
            }
            isoCurve.addSegment(isoLineString);
        }                
        return isoCurve;
    }

    
    // VERSION AVANT HERITAGE DE GM_POLYGON SUR GM_SURFACE
    /** Convertit un Polygon SDOAPI en une GM_Surface ISO, composée d'un et d'un seul GM_Polygon. */
   /* private static GM_Surface sdoapi2iso_polygon (Polygon sdoPolygon) throws Exception {
        LineString sdoExteriorRing = (LineString)sdoPolygon.getExteriorRing();
        GM_Curve isoCurve = IsoAndSdo.sdoapi2iso_linestring(sdoExteriorRing); 
        GM_Ring isoRing = new GM_Ring (isoCurve);
        GM_SurfaceBoundary isoBoundary = new GM_SurfaceBoundary(isoRing);
        if (sdoPolygon.getNumRings() > 1) {
            LineString[] sdoInteriorRing = (LineString[])sdoPolygon.getInteriorRingArray();
            for (int i=0; i<sdoInteriorRing.length; i++) {
                isoCurve = IsoAndSdo.sdoapi2iso_linestring(sdoInteriorRing[i]);
                isoRing = new GM_Ring (isoCurve);
                isoBoundary.appendInterior(isoRing);
            }
        }
        GM_Polygon isoPoly = new GM_Polygon(isoBoundary);
        GM_Surface isoSurface = new GM_Surface(isoPoly);        
        return isoSurface;
    }

    
    // VERSION AVANT HERITAGE DE GM_POLYGON SUR GM_SURFACE
    /** Convertit un CurvePolygon SDOAPI en une GM_Surface ISO, composée d'un et d'un seul GM_Polygon. */
  /*  private static GM_Surface sdoapi2iso_curvepolygon (CurvePolygon sdoCurvePolygon) throws Exception {
        CurveString sdoExteriorRing = sdoCurvePolygon.getExteriorRing();
        GM_Curve isoCurve = IsoAndSdo.sdoapi2iso_curvestring(sdoExteriorRing); 
        GM_Ring isoRing = new GM_Ring (isoCurve);
        GM_SurfaceBoundary isoBoundary = new GM_SurfaceBoundary(isoRing);
        if (sdoCurvePolygon.getNumRings() > 1) {
            CurveString[] sdoInteriorRing = sdoCurvePolygon.getInteriorRingArray();
            for (int i=0; i<sdoInteriorRing.length; i++) {
                isoCurve = IsoAndSdo.sdoapi2iso_curvestring(sdoInteriorRing[i]);
                isoRing = new GM_Ring (isoCurve);
                isoBoundary.appendInterior(isoRing);
            }
        }
        GM_Polygon isoPoly = new GM_Polygon(isoBoundary);
        GM_Surface isoSurface = new GM_Surface(isoPoly);        
        return isoSurface;
    }

    
    /** Convertit un Polygon SDOAPI en un GM_Polygon ISO. */
    private static GM_Polygon sdoapi2iso_polygon (Polygon sdoPolygon, int n) throws Exception {
        LineString sdoExteriorRing = (LineString)sdoPolygon.getExteriorRing();
        GM_Curve isoCurve = IsoAndSdo.sdoapi2iso_linestring(sdoExteriorRing,n); 
        GM_Ring isoRing = new GM_Ring (isoCurve);
        GM_Polygon isoPoly = new GM_Polygon(isoRing);
        if (sdoPolygon.getNumRings() > 1) {
            LineString[] sdoInteriorRing = (LineString[])sdoPolygon.getInteriorRingArray();
            for (int i=0; i<sdoInteriorRing.length; i++) {
                isoCurve = IsoAndSdo.sdoapi2iso_linestring(sdoInteriorRing[i],n);
                isoRing = new GM_Ring (isoCurve);
                isoPoly.addInterior(isoRing);
            }
        }
        return isoPoly;
    }
    
    
    /** Convertit un CurvePolygon SDOAPI en un GM_Polygon ISO. */
    private static GM_Polygon sdoapi2iso_curvepolygon (CurvePolygon sdoCurvePolygon, int n) throws Exception {
        CurveString sdoExteriorRing = sdoCurvePolygon.getExteriorRing();
        GM_Curve isoCurve = IsoAndSdo.sdoapi2iso_curvestring(sdoExteriorRing,n); 
        GM_Ring isoRing = new GM_Ring (isoCurve);
        GM_Polygon isoPoly = new GM_Polygon(isoRing);
        if (sdoCurvePolygon.getNumRings() > 1) {
            CurveString[] sdoInteriorRing = sdoCurvePolygon.getInteriorRingArray();
            for (int i=0; i<sdoInteriorRing.length; i++) {
                isoCurve = IsoAndSdo.sdoapi2iso_curvestring(sdoInteriorRing[i],n);
                isoRing = new GM_Ring (isoCurve);
                isoPoly.addInterior(isoRing);
            }
        }
        return isoPoly;
    }
    

    /** Convertit une GeometryCollection SDOAPI en un GM_Aggregate ISO */
    private static GM_Aggregate sdoapi2iso_geometrycollection (GeometryCollection sdoCollection, int m) throws Exception {
        GM_Aggregate isoAggregate = new GM_Aggregate();
        int n = sdoCollection.getNumGeometries();
        for (int i=0; i<n; i++) {
            Geometry sdoGeom = sdoCollection.getGeometryAt(i);
            GM_Object isoGeom = IsoAndSdo.sdoapi2iso(sdoGeom/*,m*/);
            isoAggregate.add(isoGeom);
        }
        return isoAggregate;    
    }
    
    
    /** Convertit un MultiPoint SDOAPI en un GM_MultiPoint ISO */
    private static GM_MultiPoint sdoapi2iso_multipoint (MultiPoint sdoCollection, int m) throws Exception {
        GM_MultiPoint isoAggregate = new GM_MultiPoint();
        int n = sdoCollection.getNumGeometries();
        for (int i=0; i<n; i++) {
            Geometry sdoGeom = sdoCollection.getGeometryAt(i);
            GM_Object isoGeom = IsoAndSdo.sdoapi2iso(sdoGeom/*,m*/);
            isoAggregate.add(isoGeom);
        }
        return isoAggregate;    
    }
    

    /** Convertit un MultiLineString SDOAPI en un GM_MultiCurve ISO */
    private static GM_MultiCurve sdoapi2iso_multilinestring (MultiLineString sdoCollection, int m) throws Exception {
        GM_MultiCurve isoAggregate = new GM_MultiCurve();
        int n = sdoCollection.getNumGeometries();
        for (int i=0; i<n; i++) {
            Geometry sdoGeom = sdoCollection.getGeometryAt(i);
            GM_Object isoGeom = IsoAndSdo.sdoapi2iso(sdoGeom/*,m*/);
            isoAggregate.add(isoGeom);
        }
        return isoAggregate;    
    }
  
    
    /** Convertit un MultiCurveString SDOAPI en un GM_MultiCurve ISO */
    private static GM_MultiCurve sdoapi2iso_multicurvestring (MultiCurveString sdoCollection, int m) throws Exception {
        GM_MultiCurve isoAggregate = new GM_MultiCurve();
        int n = sdoCollection.getNumGeometries();
        for (int i=0; i<n; i++) {
            Geometry sdoGeom = sdoCollection.getGeometryAt(i);
            GM_Object isoGeom = IsoAndSdo.sdoapi2iso(sdoGeom/*,m*/);
            isoAggregate.add(isoGeom);
        }
        return isoAggregate;    
    }  
        
        
    /** Convertit un MultiPolygon SDOAPI en un GM_MultiPolygon ISO */    
    private static GM_MultiSurface sdoapi2iso_multipolygon (MultiPolygon sdoCollection, int m) throws Exception {
        GM_MultiSurface isoAggregate = new GM_MultiSurface();
        int n = sdoCollection.getNumGeometries();
        for (int i=0; i<n; i++) {
            Geometry sdoGeom = sdoCollection.getGeometryAt(i);
            GM_Object isoGeom = IsoAndSdo.sdoapi2iso(sdoGeom/*,m*/);
            isoAggregate.add(isoGeom);
        }
        return isoAggregate;    
    }  
    
    
    /** Convertit un MultiCurvePolygon SDOAPI en un GM_MultiPolygon ISO */  
    private static GM_MultiSurface sdoapi2iso_multicurvepolygon (MultiCurvePolygon sdoCollection, int m) throws Exception {
        GM_MultiSurface isoAggregate = new GM_MultiSurface();
        int n = sdoCollection.getNumGeometries();
        for (int i=0; i<n; i++) {
            Geometry sdoGeom = sdoCollection.getGeometryAt(i);
            GM_Object isoGeom = IsoAndSdo.sdoapi2iso(sdoGeom/*,m*/);
            isoAggregate.add(isoGeom);
        }
        return isoAggregate;    
    }  
      

    
    

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // iso2sdoapi /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////// 
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Convertit de notre format ISO vers le format SDOAPI. Si on a null en entrée, renvoie null.
      * Cette méthode publique appelle la méthode privée "iso2sdoapi_xxxxx" adéquate, selon le type de géométrie rencontrée.*/
    public static Geometry iso2sdoapi (GeometryFactory gf, GM_Object gm_o) throws Exception {
        Geometry geom = null;
        if (gm_o != null) {
            java.lang.Class gmoClass = gm_o.getClass();      
            if (gmoClass == java.lang.Class.forName("fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point"))
                geom = iso2sdoapi_point(gf,(GM_Point)gm_o);  // a faire
            else if (gmoClass == java.lang.Class.forName("fr.ign.cogit.geoxygene.spatial.geomprim.GM_Curve")) 
                geom = iso2sdoapi_curve(gf,(GM_Curve)gm_o);
            else if (gmoClass == java.lang.Class.forName("fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString")) 
                geom = iso2sdoapi_linestring(gf,(GM_LineString)gm_o);
            else if (gmoClass == java.lang.Class.forName("fr.ign.cogit.geoxygene.spatial.geomprim.GM_Surface"))
                geom = iso2sdoapi_surface(gf,(GM_Surface)gm_o);
            else if (gmoClass == java.lang.Class.forName("fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon"))
                geom = iso2sdoapi_polygon(gf,(GM_Polygon)gm_o);            
            else if (gmoClass == java.lang.Class.forName("fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate"))
                geom = iso2sdoapi_aggregate(gf,(GM_Aggregate)gm_o);
            else if (gmoClass == java.lang.Class.forName("fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint"))
                geom = iso2sdoapi_multipoint(gf,(GM_MultiPoint)gm_o);
            else if (gmoClass == java.lang.Class.forName("fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve"))
                geom = iso2sdoapi_multicurve(gf,(GM_MultiCurve)gm_o);
            else if (gmoClass == java.lang.Class.forName("fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface"))
                geom = iso2sdoapi_multisurface(gf,(GM_MultiSurface)gm_o);
            else throw new Exception("Impossible de rendre ce type de géométrie ISO persistante : "+gmoClass.getName());
        }
        return geom;
    }
        

    /** Convertit un GM_Point ISO en un Point SDOAPI. */
    private static Geometry iso2sdoapi_point (GeometryFactory gf, GM_Point isoPoint) throws Exception {
        CoordPoint coordPoint = new CoordPointImpl();
        for (int k=0; k<isoPoint.getPosition().getDimension(); k++) {
                try {
                    double x = isoPoint.getPosition().getCoordinate(k);
                    if (!Double.isNaN(x)) coordPoint.setOrd(x,k);         
                } catch (Exception e) {throw new Exception(e.getMessage());}
        }
        Geometry geom = gf.createPoint(coordPoint);
        return geom;
    }
                        
    
    /** Convertit une GM_Curve ISO en un type de courbe SDOAPI.
     *  Si la courbe est compose d'un seul segment qui est une polyligne, on appelle iso2sdoapi_linestring, qui génère une LineString.
     *  Sinon on appelle iso2sdoapi_curvestring, qui génère une CurveString. */
    private static Geometry iso2sdoapi_curve (GeometryFactory gf, GM_Curve isoCurve) throws Exception {
       Geometry geom = null;
       if ((isoCurve.sizeSegment() == 1) && (isoCurve.getSegment(0).getInterpolation()=="linear")) 
           geom = iso2sdoapi_linestring(gf,(GM_LineString)(isoCurve.getSegment(0)));
        else geom = iso2sdoapi_curvestring(gf,isoCurve);
      return geom;
    }
        
        
    /** Génère une LineString SDOAPI à partir d'une GM_Curve ISO constituée d'une et d'une seule GM_LineString. */
    private static Geometry iso2sdoapi_linestring (GeometryFactory gf, GM_LineString isoCurve) throws Exception {
        Geometry geom = null;
        DirectPositionList listOfPoints = isoCurve.coord();
        CoordPoint[] sdoPointArray = new CoordPoint[listOfPoints.size()];
        listOfPoints.initIterator();
        int j=0;
        while (listOfPoints.hasNext()) {
            DirectPosition isoPoint = listOfPoints.next();
            CoordPoint sdoPoint = new CoordPointImpl();
            for (int k=0; k<isoPoint.getDimension(); k++) {      
                try {
                    double x = isoPoint.getCoordinate(k);
                    //   sdoPoint.setOrd(x,k);   
                    if (!Double.isNaN(x)) sdoPoint.setOrd(x,k);                    
                } catch (Exception e) {throw new Exception(e.getMessage());}
            }
            sdoPointArray[j] = sdoPoint;
            j++;
        }
        geom = gf.createLineString(sdoPointArray);
        return geom;
    }
    

// A REPRENDRE quand on aura implementé GM_ArcString    
    /** Génère une CurveString SDOAPI à partir d'une GM_Curve ISO. Ne fonctionne que pour des segments linéaires.
     *  A TERMINER : pour l'instant je genere une CurveString composee uniquement de segments linéaires.
     *  Il faudra generer une vraie CurveString composée de segments éventuellement circulaires. */
    private static Geometry iso2sdoapi_curvestring (GeometryFactory gf, GM_Curve isoCurve) throws Exception {      
        Geometry geom = null;
        String sdoClassName = "";      
        Segment[] sdoSegmentArray = new Segment[isoCurve.sizeSegment()];
        for (int i=0; i<isoCurve.sizeSegment(); i++) {
            GM_CurveSegment isoSegment = isoCurve.getSegment(i);
            // prévoir les cas : Bezier, Circular
            if (isoSegment.getInterpolation() == "linear") sdoClassName = "oracle.sdoapi.geom.LinearSegment";
                else throw new Exception("Cas non traité dans la conversion de ISO en SDOAPI (géométrie non linéaire)");
            DirectPositionList listOfPoints = isoSegment.coord();
            CoordPoint[] sdoPointArray = new CoordPoint[listOfPoints.size()];
            listOfPoints.initIterator();
            int j = 0;
            while (listOfPoints.hasNext()) {
                DirectPosition isoPoint = listOfPoints.next();
                CoordPoint sdoPoint = new CoordPointImpl();
                for (int k=0; k<isoPoint.getDimension(); k++) {
                    try {
                        double x = isoPoint.getCoordinate(k);
                        if (!Double.isNaN(x)) sdoPoint.setOrd(x,k);                             
                    } catch (Exception e) {throw new Exception(e.getMessage());}
                    
                }
                sdoPointArray[j] = sdoPoint;
                j++;
            }
            Segment sdoSegment = gf.createSegment(java.lang.Class.forName(sdoClassName),sdoPointArray);
            sdoSegmentArray[i] = sdoSegment;
        }
        geom = gf.createCurveString(sdoSegmentArray);
        return geom;
    }
    

     /** Convertit une GM_Surface ISO en un type de surface SDOAPI.
     *  Si la surface est compose d'un seul morceau qui est un polygone, on appelle iso2sdoapi_polygon, qui génère un Polygon ou un CurvePolygon.
     *  Sinon : A FAIRE. */
    private static Geometry iso2sdoapi_surface (GeometryFactory gf, GM_Surface isoSurface) throws Exception {
       Geometry geom = null;
       if ((isoSurface.sizePatch() == 1) && (isoSurface.getPatch(0).getInterpolation()=="planar")) geom = iso2sdoapi_polygon(gf,(GM_Polygon)isoSurface);
        else throw new Exception("On ne peut rendre persistante qu'une surface composée d'un et d'un seul patch qui est un GM_Polygon");
        return geom;
    }
    
    
    /** Génère un Polygon ou un CurvePolygon SDOAPI à partir d'un GM_Polygon ISO. */
    private static Geometry iso2sdoapi_polygon (GeometryFactory gf, GM_Polygon isoSurface) throws Exception {       
        Geometry sdoGeom = null;
        if (isoSurface.sizeExterior() == 1) {
            GM_Ring isoExtRing = isoSurface.getExterior();
            int nInt = isoSurface.sizeInterior();
            if (nInt == 0) {  
                    GM_Curve isoExtCurve = isoExtRing.getPrimitive();              
                    Geometry sdoExtCurve  = IsoAndSdo.iso2sdoapi_curve(gf,isoExtCurve);
                    if (sdoExtCurve.getGeometryType().getName() == "oracle.sdoapi.geom.LineString") {
                        LineString[] sdoInterior = null;                
                        sdoGeom = gf.createPolygon((LineString)sdoExtCurve,sdoInterior);
                    }
                    if (sdoExtCurve.getGeometryType().getName() == "oracle.sdoapi.geom.CurveString")  {
                        CurveString[] sdoInterior = null;
                        sdoGeom = gf.createCurvePolygon((CurveString)sdoExtCurve,sdoInterior);
                    }
                
            } else {        // il y a des anneaux interieurs
                    GM_Curve isoExtCurve = isoExtRing.getPrimitive();              
                    Geometry sdoExtCurve = IsoAndSdo.iso2sdoapi_curve(gf,isoExtCurve);
                    Geometry[] sdoIntCurve = new Geometry[nInt];
                    for (int i=0; i<nInt; i++) {
                        GM_Ring isoIntRing = isoSurface.getInterior(i);
                        GM_Curve theIsoIntCurve = isoIntRing.getPrimitive();              
                        sdoIntCurve[i] = IsoAndSdo.iso2sdoapi_curve(gf,theIsoIntCurve);
                    }
                                    
                    boolean flagLineString = true;
                    if (sdoExtCurve.getGeometryType().getName() == "oracle.sdoapi.geom.CurveString") flagLineString = false;
                    else for (int i=0; i<nInt; i++)
                        if (sdoIntCurve[i].getGeometryType().getName() == "oracle.sdoapi.geom.CurveString") {
                            flagLineString = false;
                            break;
                        }
                    
                    if (flagLineString == true) {
                        LineString[] sdoIntLineString = new LineString[nInt];                      
                        for (int i=0; i<nInt; i++) sdoIntLineString[i] = (LineString)sdoIntCurve[i];
                        sdoGeom = gf.createPolygon((LineString)sdoExtCurve,sdoIntLineString);
                    } else {
                        CurveString[] sdoIntCurveString = new CurveString[nInt];                      
                        for (int i=0; i<nInt; i++) sdoIntCurveString[i] = (CurveString)sdoIntCurve[i];
                        sdoGeom = gf.createCurvePolygon((CurveString)sdoExtCurve,sdoIntCurveString);
                    }
            }
       } else throw new Exception("Cas non géré : cardExterior != 1 pour un polygone");       
            
        return sdoGeom;             
    }
    
    
    /** Génère une GeometryCollection SDOAPI à partir d'un GM_Aggregate ISO */
    private static Geometry iso2sdoapi_aggregate (GeometryFactory gf, GM_Aggregate isoCollection) throws Exception {
        Geometry sdoCollection = null;
        int n = isoCollection.size();
        Geometry[] sdoArray = new Geometry [n];
        for (int i=0; i<n; i++) {
            GM_Object isoGeom = isoCollection.get(i);
            Geometry sdoGeom = IsoAndSdo.iso2sdoapi(gf,isoGeom);
            sdoArray[i] = sdoGeom;
        }
        sdoCollection = gf.createGeometryCollection(sdoArray);
        return sdoCollection;
    }
    
    
    /** Génère un MultiPoint SDOAPI à partir d'un GM_MultiPoint ISO */
    private static Geometry iso2sdoapi_multipoint (GeometryFactory gf, GM_MultiPoint isoCollection) throws Exception {
        Geometry sdoMultiPoint = null;
        int n = isoCollection.size();
        Point[] sdoArray = new Point[n];
        for (int i=0; i<n; i++) {
            GM_Point isoGeom = (GM_Point)isoCollection.get(i);
            Point sdoGeom = (Point)IsoAndSdo.iso2sdoapi(gf,isoGeom);
            sdoArray[i] = sdoGeom;
        }
        sdoMultiPoint = gf.createGeometryCollection(sdoArray);
        return sdoMultiPoint;
    }
    
    
    /** Génère un MultiCurveString SDOAPI à partir d'un GM_MultiCurve ISO */
    private static Geometry iso2sdoapi_multicurve (GeometryFactory gf, GM_MultiCurve isoCollection) throws Exception {
        Geometry sdoMultiCurveString = null;
        int n = isoCollection.size();
        CurveString[] sdoArray = new CurveString[n];
        for (int i=0; i<n; i++) {
            GM_Curve isoGeom = (GM_Curve)isoCollection.get(i);
            CurveString sdoGeom = (CurveString)IsoAndSdo.iso2sdoapi(gf,isoGeom);
            sdoArray[i] = sdoGeom;
        }
        sdoMultiCurveString = gf.createGeometryCollection(sdoArray);
        return sdoMultiCurveString;
    }
    
    
    /** Génère un MultiCurvePolygon SDOAPI à partir d'un GM_MultiSurface ISO */
    private static Geometry iso2sdoapi_multisurface (GeometryFactory gf, GM_MultiSurface isoCollection) throws Exception {
        Geometry sdoMultiCurvePolygon = null;
        int n = isoCollection.size();
        CurvePolygon[] sdoArray = new CurvePolygon[n];
        for (int i=0; i<n; i++) {
            GM_Surface isoGeom = (GM_Surface)isoCollection.get(i);
            CurvePolygon sdoGeom = (CurvePolygon)IsoAndSdo.iso2sdoapi(gf,isoGeom);
            sdoArray[i] = sdoGeom;
        }
        sdoMultiCurvePolygon = gf.createGeometryCollection(sdoArray);
        return sdoMultiCurvePolygon;
    }
    
}
