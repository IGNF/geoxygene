package fr.ign.cogit.mapping.webentity.spatial;

import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

import fr.ign.cogit.mapping.webentity.spatial.data.BufferedGeometry;
import fr.ign.cogit.mapping.webentity.spatial.data.FloatingCircle;

/***
 * Cette classe permet de convertir des string en des objets
 * geometriques : actuellement tous les objets simples et 
 * plus tard des objets multi...to do (s s'expirer de  coordinateToString 
 * @author  Dr DTsatcha
 *
 */

public class SpatialGeometryFactory {
    private static final PrecisionModel PRECISION_MODEL = new PrecisionModel(
            PrecisionModel.FLOATING);
    public static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(
            PRECISION_MODEL, Constants.WGS84_SRID);
    
   protected static String geoname = null;

    

//    public static Coordinate getCoordinateFromPos(String pos) {
//        if (null == pos) {
//            throw new RuntimeException("Null position for point");
//        }
//        String[] degrees = pos.split(" ");
//        if (degrees.length != 2) {
//            throw new RuntimeException("'" + pos
//                    + "' does not contain 2 values");
//        }
//        double lat = Double.parseDouble(makeDouble(degrees[0]));
//        double lon = Double.parseDouble(makeDouble(degrees[1]));
//        Coordinate c = new Coordinate(lon, lat);
//
//        return c;
//    }

    public String getGeoname() {
    return geoname;
}

public void setGeoname(String geoname) {
    this.geoname = geoname;
}

    /**
     * Get coordinates from the position list.
     *
     * @param posList
     *            the position list
     * @return the coordinate representation of the string.
     */
//    public static Coordinate[] getCoordinatesFromPosList(String posList) {
//        String[] degrees = posList.split(" ");
//        if (degrees.length % 2 != 0) {
//            throw new RuntimeException("'" + posList
//                    + "' does not contain an even number of values");
//        }
//
//        List<Coordinate> coords = new ArrayList<>();
//        for (int i = 0; i < degrees.length; i += 2) {
//            double lat = Double.parseDouble(makeDouble(degrees[i]));
//            double lon = Double.parseDouble(makeDouble(degrees[i + 1]));
//            Coordinate c = new Coordinate(lon, lat);
//            coords.add(c);
//        }
//        return coords.toArray(new Coordinate[] {});
//    }

    
    
    
    private static String makeDouble(String number) {
        String n = number;
        if (!n.contains(".")) {
            n = n + ".0";
        }
        return n;
    }

    /**
     * Converts an array of coordinates into a ring by ensuring that the first
     * and last coordinate are the same.
     *
     * @param coords
     *            the coordinates
     * @return a ring of coordinates.
     */
    public static Coordinate[] makeRing(Coordinate[] coords) {
        Coordinate[] ret = coords;
        if(coords!=null){
        if (!(ret[0].equals(ret[ret.length - 1]))) {
            Coordinate[] tmp = new Coordinate[ret.length + 1];
            for (int i = 0; i < ret.length; i++) {
                tmp[i] = ret[i];
            }
            tmp[ret.length] = tmp[0];
            ret = tmp;
        }
        }
        return ret;
    }

    // permet de creer un point
    /*
     * @param pos designe un string contenant un point example Point(10,10);
     * 
     * @param return geom un objet geometrique
     */
    public static Point createPoint(Map<Integer, Coordinate[]> coordinates) {
        Point geom = null;
       

        if (coordinates == null) {
            throw new RuntimeException("'"
                    + "' ne contient pas des information géometrique");
      } else {

            // le premier element de la liste des entrées de la mapS
            System.out.println("test"+coordinates.get(0)[0]);
            geom = GEOMETRY_FACTORY.createPoint(coordinates.get(0)[0]);
            
            }
        if (null != geom) {
            geom.setUserData("EPSG:4326");
        }
        return geom;
    }
    
    
    /*
     * @param posList designe un string contenant des coordonnées de point
     * LineString(10,25,30,50,10,25);
     * 
     * @param return un objet LineString geometrique
     */
    public static LineString createLineString(
            Map<Integer, Coordinate[]> coordinates) {
        LineString geom = null;

        if (coordinates == null ) {
            throw new RuntimeException("'"
                    + "' ne contient pas des information géometrique");
        } else {

            // Map<Integer, Coordinate[]> coordinates=
            // coordinateToString(posList);
            // on recupère le premier de la map
             geom = GEOMETRY_FACTORY.createLineString(coordinates
                    .get(0));
            geom.setUserData("EPSG:4326");
        }
        return geom;
    }
    /*
     * @param posList
     * designe une liste de point sous forme de String
     * @return polygon
     */

    public static Polygon createPolyGon(Map<Integer, Coordinate[]> coordinates) {
        Polygon geom =null;
        List<LinearRing> allHoles = new ArrayList<>();
         // on recupère le premier de la map
        LinearRing shell = null;
        LinearRing[] holes = null;
        if (coordinates == null) {
            throw new RuntimeException("'"
                    + "' ne contient pas des information géometrique");
        } else {
        // on parcoure la map de facon ordonnée
        for (int i = 0; i < coordinates.size(); i++) {

            if (i == 0) {
                // nous avons le premier polygone
                Coordinate[] firstP = coordinates.get(i);
                shell = GEOMETRY_FACTORY.createLinearRing(makeRing(firstP));
            } else {
                Coordinate[] nextP = coordinates.get(i);
                LinearRing hole = GEOMETRY_FACTORY
                        .createLinearRing(makeRing(nextP));
                allHoles.add(hole);
            }
        }
        // on fabrique le polygon sans trous
        if (allHoles!=null && allHoles.size()==0){
             geom = GEOMETRY_FACTORY.createPolygon(shell,null);
             geom.setUserData("EPSG:4326");
        }else{
            // fabrique le polynome avec des trous..
            geom = GEOMETRY_FACTORY.createPolygon(shell,allHoles.toArray(new LinearRing[] {}));
            geom.setUserData("EPSG:4326");
            
        }
        
        }
        return geom;
    }
    
    
    
    
    public static FloatingCircle createFloatingCircle(double radius) {
        FloatingCircle geom = new FloatingCircle(GEOMETRY_FACTORY, radius);
        geom.setUserData("EPSG:4326");
        return geom;
    }

    public static BufferedGeometry createBufferedGeometry(Geometry extent) {
        BufferedGeometry geom = new BufferedGeometry(GEOMETRY_FACTORY, extent);
        geom.setUserData(extent.getUserData());
        return geom;
    }

    public static BufferedGeometry createBufferedGeometry(Geometry extent,
            double distance) {
        BufferedGeometry geom = new BufferedGeometry(GEOMETRY_FACTORY, extent,
                distance);
        geom.setUserData(extent.getUserData());
        return geom;
    }

    
    
    /*****************
     * 
     * Ces algorithmmes permettent de trouver  les composantes de géometries
     * à partir des données des chaines de caractères. il peut etre un point
     * linestring et polygone dans le cas de base de données postgis.. L'extension
     * de cette methode au multi-polygone reviendrait à trouver une strategie 
     * de regression afin de segmenter le multi et d'envoyer ses composants.
     * 
     */
    
    public static Map<Integer, Coordinate[]> coordinateToString(String textDabase) {
        
        // le nom de la geometrie
        int featureOrder = 0;
        double lon, lat;
        int i = 0;
        // liste des coordonnées
        List<Coordinate> coords = new ArrayList<>();
        Map<Integer, Coordinate[]> content = new HashMap<Integer, Coordinate[]>();
        // permet de separer le nom et les composantes géometriques
        // de l'objet dans postgis c'est (... par exemple POINT(10,10)...
        // dans virtuoso c'est l'espace " ".
        
      
     
        String[] twopart = textDabase.split("\\(");
        int begin = twopart[0].length();
        String use = textDabase.substring(begin, textDabase.length());
        // on récupère la prémière composante de la segmentation
        geoname = twopart[0];
        
       // System.out.println(geoname);
        
        // correction des multi-points de geoxygene en realité sont des polygones 
        // sans trous...
        
        if(geoname.contains("MULTIPOLYGON")){
            // on enlève la première et la dernière parenthèse
            use=use.substring(1, use.length()-1);
        }
        
        while (i < use.length()) {

            /*
             * Permet de gerer la sequence des parenthèses ouvrantes des
             * polygones
             */
            int j = i;
            while (use.charAt(i) == '(' || use.charAt(i) == ','
                    && i < use.length()) {
                i++;

            }
            // on recherche des points à recupérer
            int ptl = 0;
            String s = "";
            String[] ptc = { "", "" };
            while (use.charAt(i) != ')' && use.charAt(i) != ','
                    && i < use.length()) {

                // while (use.charAt(i) != ',') {
                // System.out.println(use.charAt(i));

                while (use.charAt(i) != ' ' && ptl == 0 && i < use.length()) {
                    s = s + use.substring(i, i + 1);
                    i++;

                }
                if ((use.charAt(i) == ' ')) {
                    ptc[0] = s;
                 s = "";
                    ptl++;
                    i++;
                }
                // System.out.println(s);
                s = s + use.substring(i, i + 1);
                i++;
            }
            if (use.charAt(i) == ',') {
                ptc[1] = s;
                s = "";
                lon = Double.parseDouble(ptc[0]);
                lat = Double.parseDouble(ptc[1]);
                MathContext mc = new MathContext(0);
                 Coordinate c = new Coordinate(lon, lat);
                coords.add(c);
                ptl = 0;
             
                i++;
            }
          
            if (use.charAt(i) == ')') {
               ptc[1] = s;
                s = "";
                lon = Double.parseDouble(ptc[0]);
                lat = Double.parseDouble(ptc[1]);
               ptl = 0;
                // coordfeature.add(pt);
                Coordinate c = new Coordinate(lon, lat);
                coords.add(c);
                if (coords.size() > 0) {
                    content.put(featureOrder,
                            coords.toArray(new Coordinate[] {}));
                    featureOrder++;

                    coords = new ArrayList<Coordinate>();
                    if (i < use.length()) {
                        i++;
                    } else {
                        break;
                    }

                    i++;
                }
              
            }
        }
        
      
                
        return content;
    }
       

    /**
     * Get the UTM Zone SRID for a given geometry
     *
     * @param geometry
     *            the geometry to lookup.
     * @return the geometry's UTM SRID
     */
    public static int UTMZoneSRID(Geometry geometry) {
        Point p = geometry.getCentroid();
        int srid = 0;
        double lat = p.getY();
        double lon = p.getX();

        if (lat > 0) {
            srid = 32600;
        } else {
            srid = 32700;
        }

        double zone = Math.floor(((lon + 186d)) / 6);

        // make sure longitude 180.00 is in zone 60
        if (((Double) lon).equals(180D)) {
            zone = 60;
        }

        // special zone for Norway
        if (lat >= 56D && lat < 64D && lon >= 3D && lon <= 12D) {
            zone = 32;
        }

        // special zones for Svalbard
        if (lat >= 72D && lat < 84D) {
            if (lon >= 0D && lon < 9D) {
                zone = 31;
            } else if (lon >= 9D && lon < 21D) {
                zone = 33;
            } else if (lon >= 21D && lon < 33D) {
                zone = 35;
            } else if (lon >= 33D && lon < 42D) {
                zone = 37;
            }
        }
        srid += zone;

        return srid;
    }
}
